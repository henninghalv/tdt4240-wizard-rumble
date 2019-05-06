package com.progark.group2.gameserver;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.progark.group2.gameserver.misc.AsciiArtCreator;
import com.progark.group2.gameserver.resources.GameStatus;
import com.progark.group2.wizardrumble.network.packets.GameEndPacket;
import com.progark.group2.wizardrumble.network.packets.GameStartPacket;
import com.progark.group2.wizardrumble.network.packets.PlayerDeadPacket;
import com.progark.group2.wizardrumble.network.packets.PlayerMovementPacket;
import com.progark.group2.wizardrumble.network.packets.PlayersOnlinePacket;
import com.progark.group2.wizardrumble.network.packets.SpellFiredPacket;
import com.progark.group2.wizardrumble.network.requests.CreateGameRequest;
import com.progark.group2.wizardrumble.network.requests.CreatePlayerRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerJoinRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerLeaveRequest;
import com.progark.group2.wizardrumble.network.responses.CreateGameResponse;
import com.progark.group2.wizardrumble.network.responses.CreatePlayerResponse;
import com.progark.group2.gameserver.database.SQLiteDBConnector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class GameServer extends Listener{

    private static GameServer instance = null;
    private static SQLiteDBConnector connector;
    private static int gameIdCounter;

    private HashMap<Integer, Game> games = new HashMap<Integer, Game>();
    private Server server;

    private final static int MAXIMUM_PLAYERS_PER_GAME = 6;

    private final static int DEFAULT_TCP_PORT = 54555;
    private final static int DEFAULT_UDP_PORT = 54777;

    private GameServer() throws IOException {
        Log.info("Initializing master server...");
        server = createAndConnectMasterServer();
        Log.info("Done!\n");
        // Add a receiver listener to server
        Log.info("Adding listeners...");
        server.addListener(this);
        Log.info("Done!\n");
        gameIdCounter = 1;
    }

    @Override
    public void disconnected(Connection connection){
        PlayersOnlinePacket packet = new PlayersOnlinePacket();
        packet.setPlayersOnlineCount(server.getConnections().length);
        for(Connection c : server.getConnections()){
            c.sendTCP(packet);
        }
        for(Game game : games.values()){
            if(game.getPlayerConnections().values().contains(connection)){
                if(game.isStarted()){
                    game.removePlayerFromGame(game.getPlayerIdFromConnection(connection), connection);
                } else {
                    game.removePlayerFromLobby(game.getPlayerIdFromConnection(connection), connection);
                }
            }
        }
    }

    @Override
    public void received(Connection connection, Object object){
        if (object instanceof CreateGameRequest) {
            Log.info("Received CreateGameRequest...");
            handleCreateGameRequest(connection);
        } else if (object instanceof CreatePlayerRequest){
            Log.info("Received CreateNewPlayerRequest...");
            CreatePlayerRequest request = (CreatePlayerRequest) object;
            try {
                handleCreatePlayerRequest(connection, request.getPlayerName());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (object instanceof PlayerJoinRequest){
            PlayerJoinRequest request = (PlayerJoinRequest) object;
            handlePlayerJoinRequest(connection, request);
        } else if (object instanceof GameStartPacket){
            GameStartPacket packet = (GameStartPacket) object;
            handleGameStartPacket(packet);
        } else if (object instanceof PlayerMovementPacket){
            PlayerMovementPacket packet = (PlayerMovementPacket) object;
            handlePlayerMovementPacket(connection, packet);
        } else if (object instanceof SpellFiredPacket){
            SpellFiredPacket packet = (SpellFiredPacket) object;
            handleSpellFiredPacket(connection, packet);
        } else if (object instanceof PlayerDeadPacket){
            PlayerDeadPacket packet = (PlayerDeadPacket) object;
            handlePlayerDeadPacket(connection, packet);
        } else if (object instanceof PlayerLeaveRequest){
            PlayerLeaveRequest request = (PlayerLeaveRequest) object;
            handlePlayerLeaveRequest(connection, request);
        } else if (object instanceof PlayersOnlinePacket){
            PlayersOnlinePacket packet = (PlayersOnlinePacket) object;
            packet.setPlayersOnlineCount(server.getConnections().length);
            for(Connection c : server.getConnections()){
                c.sendTCP(packet);
            }
        } else if (object instanceof GameEndPacket){
            GameEndPacket packet = (GameEndPacket) object;
            if(games.containsKey(packet.getGameId())){
                removeGame(games.get(packet.getGameId()));
            }
        }
    }

    /**
     * Singleton class - This will get GameServer instance unless it's defined
     * @return  Returns the GameServer instance
     * @throws IOException      Exception upon creation of master server
     */
    static GameServer getInstance() throws IOException {
        if (instance == null) {
            instance = new GameServer();
        }
        return instance;
    }

    // SETUP METHODS

    /**
     * Creates a new KryoNet Server and connects it
     * @return A KryoNet server object
     * @throws IOException
     */
    private Server createAndConnectMasterServer() throws IOException {
        Server server = new Server();
        server.start();
        server.bind(DEFAULT_TCP_PORT, DEFAULT_UDP_PORT);
        KryoServerRegister.registerKryoClasses(server);  // Register classes for kryo serializer
        return server;
    }

    // =====

    // REQUEST HANDLING

    private void handleCreateGameRequest(Connection connection){
        Game game = findAvailableGame();
        if(game != null){
            sendCreateGameResponse(connection, game);
        } else {
            Log.info("Creating new game...");
            game = createNewGame();
            addGame(game);
            sendCreateGameResponse(connection, game);
        }
    }

    private void handleCreatePlayerRequest(Connection connection, String username) throws SQLException {
        Log.info("Getting highest id...");
        int id = connector.getHighestId() + 1;
        Log.info("Done!\n");
        Log.info("Creating new player...");
        connector.createNewPlayer(id, username);
        Log.info("Done!\n");
        sendCreatePlayerResponse(connection, id, username);
    }

    private void handlePlayerJoinRequest(Connection connection, PlayerJoinRequest request){
        Game game = games.get(request.getGameId());
        game.addPlayer(getPlayerName(request.getPlayerId()), request.getPlayerId(), connection);
    }

    private void handleGameStartPacket(GameStartPacket packet){
        Game game = games.get(packet.getGameId());
        game.start();
    }

    private void handlePlayerMovementPacket(Connection connection, PlayerMovementPacket packet){
        if(games.keySet().contains(packet.getGameId())){
            Game game = games.get(packet.getGameId());
            game.updatePlayerPosition(connection, packet);
        }
    }

    private void handleSpellFiredPacket(Connection connection, SpellFiredPacket packet){
        Game game = games.get(packet.getGameId());
        game.spellFired(connection, packet);
    }

    private void handlePlayerDeadPacket(Connection connection, PlayerDeadPacket packet){
        Game game = games.get(packet.getGameId());
        game.playerDied(connection, packet);
    }

    private void handlePlayerLeaveRequest(Connection connection, PlayerLeaveRequest request){
        Game game = games.get(request.getGameId());
        if(game.isStarted()){
            game.removePlayerFromGame(request.getPlayerId(), connection);
        } else{
            game.removePlayerFromLobby(request.getPlayerId(), connection);
        }
    }

    // =====

    // RESPONSES

    /**
     * Sends a reponse to the client when it wants to create a game
     * @param connection
     * @param game
     */
    private void sendCreateGameResponse(Connection connection, Game game){
        CreateGameResponse response = new CreateGameResponse();
        response.setGameId(game.getGameId());
        connection.sendTCP(response);
    }

    /**
     * Sends a response to the client when a player has been created in the DB
     * @param connection
     * @param id
     * @param username
     */
    private void sendCreatePlayerResponse(Connection connection, int id, String username){
        CreatePlayerResponse response = new CreatePlayerResponse();
        response.setPlayerId(id);
        response.setUsername(username);
        connection.sendTCP(response);
    }

    // =====

    // CREATION

    /**
     * Creates a new Game instance
     * @return Game
     */

    private Game createNewGame(){
        return new Game(gameIdCounter);
    }

    // =====

    // ACTIONS

    /**
     * Finds an available Game object and returns it
     * @return Game
     */

    private Game findAvailableGame(){
        Log.info("Looking for available game...");
        for(Game game : games.values()){
            if(game.getGameStatus().equals(GameStatus.STAND_BY)){
                Log.info("Found one!");
                return game;
            }
        }
        Log.info("Did not find available game...");
        return null;
    }

    /**
     * Add game to the list of all games on standby
     * @param game
     */
    private void addGame(Game game){
        games.put(gameIdCounter, game);
        gameIdCounter++;
    }

    /**
     * Remove game from the list of all games
     * @param game
     */
    public void removeGame(Game game){
        Log.info("Removing game...");
        games.remove(game.getGameId());
    }

    // =====

    // HELPER METHODS

    /**
     * Gets the constant Maximum players per game.
     * @return
     */
    static int getMaximumPlayers() {
        return MAXIMUM_PLAYERS_PER_GAME;
    }

    /**
     * Gameserver will request for playername from master server.
     * The master server must then send a sql query to DB.
     * @param playerID  The id of the player that name is requested
     * @return  The name of the player with corresponding playerID
     */
     static String getPlayerName(int playerID)  {
        // Default name if not registered in DB
        String playerName = "Guest";
        try {
            // Connects to the database and tries to get the player name based on player id
            playerName = connector.getPlayer(playerID).get(playerID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerName;
    }

    // =====

    /**
     * Main method that runs the server
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // Print ASCIIArt
        AsciiArtCreator art = new AsciiArtCreator();
        art.createArt("WIZARD RUMBLE SERVER", 300, 20);
        // Init master server
        GameServer.getInstance();
        try {
            // Init DB connection
            connector = SQLiteDBConnector.getInstance();
            connector.connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
