package com.progark.group2.gameserver;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import com.esotericsoftware.minlog.Log;
import com.progark.group2.gameserver.resources.GameStatus;
import com.progark.group2.wizardrumble.network.packets.GameStartPacket;
import com.progark.group2.wizardrumble.network.packets.PlayerDeadPacket;
import com.progark.group2.wizardrumble.network.packets.PlayerStatsPacket;
import com.progark.group2.wizardrumble.network.packets.SpellFiredPacket;
import com.progark.group2.wizardrumble.network.requests.PlayerJoinRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerLeaveRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerMovementRequest;
import com.progark.group2.wizardrumble.network.resources.Player;
import com.progark.group2.wizardrumble.network.responses.GameJoinedResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerJoinResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerLeaveResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerMovementResponse;
import com.progark.group2.wizardrumble.network.responses.ServerErrorResponse;
import com.progark.group2.wizardrumble.network.responses.ServerSuccessResponse;

import java.io.IOException;
import java.util.HashMap;

public class GameServer extends Listener{

    private static Server server;
    private static int TCP_PORT;
    private static int UDP_PORT;
    private long gameStartTime;
    // List of the six available starting positions on the map.
    private static HashMap<Integer, Vector2> spawnPoints = new HashMap<Integer, Vector2>();
    // List of all players that has joined the game with their stats for this game
    private HashMap<Integer, Player> players = new HashMap<Integer, Player>();


    GameServer(int tcpPort, int udpPort) throws IOException {
        server = createNewServer(tcpPort, udpPort);
        TCP_PORT = tcpPort;
        UDP_PORT = udpPort;
        createSpawnPoints();
    }

    // CREATION

    /**
     * Creates a new Kryo Server instance and registers request and
     * response classes.
     * @return      Kryo Server object
     */
    private Server createNewServer(int tcpPort, int udpPort) throws IOException {
        Server server = new Server();
        server.start();
        server.bind(tcpPort, udpPort);
        KryoServerRegister.registerKryoClasses(server);
        server.addListener(this);
        return server;
    }

    private void createSpawnPoints(){
        spawnPoints.put(1, new Vector2(500, 500));
        spawnPoints.put(2, new Vector2(500, 750));
        spawnPoints.put(3, new Vector2(500, 1000));
        spawnPoints.put(4, new Vector2(1000, 1000));
        spawnPoints.put(5, new Vector2(1000, 750));
        spawnPoints.put(6, new Vector2(1000, 500));
    }

    // =====

    // LISTENERS

    public void received(Connection connection, Object object){
        if (object instanceof PlayerJoinRequest){
            // If a player has joined
            PlayerJoinRequest request = (PlayerJoinRequest) object;
            // Add player to list of joined players
            try {
                handlePlayerJoinRequest(connection, request);
            } catch (IOException e) {
                sendServerErrorResponse(connection, "Something is wrong with the server. Please try again later!");
                e.printStackTrace();
            }
        }
        else if (object instanceof PlayerLeaveRequest){
            PlayerLeaveRequest request = (PlayerLeaveRequest) object;
            sendServerSuccessResponse(connection, "Goodbye!");
            try {
                handlePlayerLeaveRequest(connection, request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (object instanceof GameStartPacket){
            GameStartPacket packet = (GameStartPacket) object;
            try {
                startGame(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (object instanceof SpellFiredPacket){
            SpellFiredPacket packet = (SpellFiredPacket) object;
            server.sendToAllExceptUDP(connection.getID(), packet);
        }
        else if (object instanceof PlayerMovementRequest){
            PlayerMovementRequest request = (PlayerMovementRequest) object;
            handlePlayerMovementRequest(connection, request);
        }
        else if (object instanceof PlayerDeadPacket){
            PlayerDeadPacket packet = (PlayerDeadPacket) object;
            System.out.println("Killer id: " + packet.getKillerId());
            players.get(packet.getKillerId()).incrementKills(); // Increase kills for killer
            players.get(packet.getVictimId()).setAlive(false); // Set victim player as dead
            Log.info("Player died: " + packet.getVictimId() + ", Killed by " + packet.getKillerId());
            server.sendToAllExceptTCP(connection.getID(), packet);

            // Check if game has ended
            // TODO: Consider finding a nice lambda for this
            int playersAlive = 0;
            for (Player p: players.values()) {
                if (p.isAlive()) playersAlive++;
            }

            // If game has ended
            if (playersAlive <= 1) {
                // Send player stats to all players for scoreboard: kills and ranking
                sendPlayerStatsResponse();
                endGame();
            }
        }
    }

    // =====

    // REQUEST HANDLING

    /**
     * Handles the PlayerJoinRequest
     * @param connection
     * @param request
     * @throws IOException
     */
    private void handlePlayerJoinRequest(Connection connection, PlayerJoinRequest request) throws IOException {
        String playerName = MasterServer.getPlayerName(request.getPlayerId());
        // This does NOT use the "sendPlayerJoinResponse" method to prevent sending it to the player being added.
        PlayerJoinResponse response = new PlayerJoinResponse();
        response.setPlayerId(request.getPlayerId());
        response.setPlayerName(playerName);
        response.setConnectionId(connection.getID());
        response.setSpawnPoint(spawnPoints.get(connection.getID()));
        server.sendToAllExceptTCP(connection.getID(), response);

        for(Integer playerId : players.keySet()){
            sendPlayerJoinResponse(connection, playerId, players.get(playerId));
        }

        addPlayer(request.getPlayerId(), playerName, connection.getID());
        GameJoinedResponse response1 = new GameJoinedResponse();
        response1.setSpawnPoint(spawnPoints.get(connection.getID()));
        connection.sendTCP(response1);
    }

    private void handlePlayerLeaveRequest(Connection connection, PlayerLeaveRequest request) throws IOException {
        removePlayer(request.getPlayerId());
        sendPlayerLeaveResponse(connection, request);
    }

    private void handlePlayerMovementRequest(Connection connection, PlayerMovementRequest request){
        sendPlayerMovementResponse(connection, request);
    }

    // =====

    // RESPONSES

    /**
     * Sends a generic Server Success Response with a simple message
     * @param connection
     * @param message
     */
    private void sendServerSuccessResponse(Connection connection, String message){
        ServerSuccessResponse response = new ServerSuccessResponse();
        response.setSuccessMessage(message);
        connection.sendTCP(response);
    }

    /**
     * Sends a server error message to the client. Fire this where something can fail on the server side.
     * @param connection
     * @param message
     */
    private void sendServerErrorResponse(Connection connection, String message){
        ServerErrorResponse errorResponse = new ServerErrorResponse();
        errorResponse.setErrorMsg(message);
        connection.sendTCP(errorResponse);
    }

    private void sendPlayerMovementResponse(Connection connection, PlayerMovementRequest request){
        PlayerMovementResponse response = new PlayerMovementResponse();
        response.setPlayerId(request.getPlayerId());
        response.setPosition(request.getPosition());
        response.setRotation(request.getRotation());
        players.get(request.getPlayerId()).setPosition(request.getPosition());
        players.get(request.getPlayerId()).setRotation(request.getRotation());
        server.sendToAllExceptUDP(connection.getID(), response);
    }

    private void sendPlayerJoinResponse(Connection connection, int playerId, Player player){
        PlayerJoinResponse response = new PlayerJoinResponse();
        response.setPlayerId(playerId);
        response.setPlayerName(player.getName());
        response.setConnectionId(player.getConnectionId());
        response.setSpawnPoint(player.getPosition());
        connection.sendTCP(response);
    }

    private void sendPlayerLeaveResponse(Connection connection, PlayerLeaveRequest request){
        PlayerLeaveResponse response = new PlayerLeaveResponse();
        response.setPlayerId(request.getPlayerId());
        server.sendToAllExceptTCP(connection.getID(), response);
    }

    private void sendPlayerStatsResponse() {
        PlayerStatsPacket packet = new PlayerStatsPacket();
        packet.setPlayers(players);
        server.sendToAllTCP(packet);
    }

    // =====

    // ACTIONS

    /**
     * Add a new player to list when joining or creating a new game.
     * @return Player name
     * @param playerId  (int) player id
     */
    private void addPlayer(int playerId, String playerName, int connectionId) throws IOException {
        Player player = new Player(
                playerName, // name
                connectionId,
                0, // Kills
                0, // Position or rank according to time of death
                0,// Time alive, milliseconds,
                spawnPoints.get(connectionId),
                0
        );

        players.put(playerId, player);
        System.out.println("Server has: " + players.keySet().size() + "/6 players!");
        if(players.keySet().size() == MasterServer.getMaximumPlayers()){
            Log.info("Server full! Updating status...");
            MasterServer.getInstance().updateGameServerStatus(this, GameStatus.IN_PROGRESS);
            Log.info("Done!\n");
        }
    }

    private void removePlayer(int playerId) throws IOException {
        players.remove(playerId);
        MasterServer.getInstance().updateGameServerStatus(this, GameStatus.STAND_BY);
    }


    // =====

    // HELPER METHODS

    Integer getTCPPort(){
        return TCP_PORT;
    }

    Integer getUDPPort(){
        return UDP_PORT;
    }

    // =====

    // GAME LOGIC
    /**
     * Tell the master server that this server no longer is on standby,
     * but in progress. Start game after 30 seconds from when two players joined or
     * when the game server is full.
     */
    private void startGame(GameStartPacket packet) throws IOException {
        Log.info("Starting game...");
        gameStartTime = System.currentTimeMillis();
        MasterServer.getInstance().updateGameServerStatus(this, GameStatus.IN_PROGRESS);
        packet.setGameStartTime(gameStartTime);
        server.sendToAllTCP(packet);
        Log.info("Done!\n");

//        if (players.keySet().size() == MasterServer.getMaximumPlayers()) {
//            // set gameserver status to inprogress
//        }
//
//        if (players.keySet().size() >= 2) {
//            // Start countdown from 30 sec.
//            // After this countdown, set gameserver status to inprogress
//
//        }
    }
//
    /**
     * When the game has ended and all joinedPlayerIDs has left the game,
     * the server stops and removes itself from the MasterServer.
     */
    private void endGame() {
        Log.info("ONE WINNING PLAYER ALIVE. STOPPING GAMESERVER: GOODBYE WORLD");
        // Stop the server connection for all players
        for(Connection connection : server.getConnections()){
            connection.close();
        }
        try {
            // Try removing this from the master server
            // This should open the used ports in master server
            MasterServer.getInstance().removeGameServer(TCP_PORT, UDP_PORT, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.close();
    }
    // =====

}
