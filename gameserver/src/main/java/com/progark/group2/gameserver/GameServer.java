package com.progark.group2.gameserver;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import com.esotericsoftware.minlog.Log;
import com.progark.group2.gameserver.resources.GameStatus;
import com.progark.group2.wizardrumble.network.packets.GameStartPacket;
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
        spawnPoints.put(2, new Vector2(500, 1000));
        spawnPoints.put(3, new Vector2(500, 1500));
        spawnPoints.put(4, new Vector2(1500, 1500));
        spawnPoints.put(5, new Vector2(1500, 1000));
        spawnPoints.put(6, new Vector2(1500, 500));
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
            server.sendToAllTCP(packet);
            try {
                startGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (object instanceof PlayerMovementRequest){
            PlayerMovementRequest request = (PlayerMovementRequest) object;
            handlePlayerMovementRequest(connection, request);
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
//    /**
//     * Subtracts the player's health equal to the damage taken
//     * and updates the game data correspondingly.
//     * @param playerID  (int) the id of the player taken damage
//     * @param damage    (int) damage taken
//     */
//    public void takeDamage(int playerID, int damage) {
//        // The player's previous health
//        int previousHealth = players.get(playerID).getHealth();
//
//        // Update player's health
//        players.get(playerID).setHealth(previousHealth - damage);
//
//        if (previousHealth - damage <= 0) {
//            // TODO: send player is dead request to all clients
//            PlayerDeadRequest request = new PlayerDeadRequest();
//            request.setPlayerID(playerID);
//        }
//    }

    /**
     * Sends all players health status to all clients.
     * This should be called between a set time interval
     * to update all clients about the health to all players.
     */
    // TODO: Call this function between a set time interval.
//    public void sendPlayersHealthStatusRequest() {
//        PlayersHealthStatusRequest request = new PlayersHealthStatusRequest();
//        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
//
//        // Add each players health status to the map
//        for (int playerID : players.keySet()) {
//            map.put(playerID, players.get(playerID).getHealth());
//        }
//
//        // Add the health status in the request
//        request.setMap(map);
//
//        // Send health status update to all clients
//        for (Server server : servers) {
//            // TODO: Go through all clients joined and send this request through connection.sendTCP(request).
//        }
//    }
//
    /**
     * Tell the master server that this server no longer is on standby,
     * but in progress. Start game after 30 seconds from when two players joined or
     * when the game server is full.
     */
    private void startGame() throws IOException {
        Log.info("Starting game...");
        MasterServer.getInstance().updateGameServerStatus(this, GameStatus.IN_PROGRESS);
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
        // TODO: Create a timeout for when the server should shutdown anyway

        // TODO: send request with metadata (scoreboard data) to all clients


        // TODO: FIll response with players metadata

        // Send the response back to client
        //connection.sendTCP(response);
        Log.info("ALL PLAYERS ARE DEAD. STOPPING GAMESERVER: GOODBYE WORLD");
        // Stop the server connection for all servers

        try {
            // Try removing this from the master server
            // This should open the used ports in master server
            MasterServer.getInstance().removeGameServer(TCP_PORT, UDP_PORT, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * Return whether all players are dead and the game has ended.
//     * @return  Boolean     True if all players have died
//     */
//    private boolean hasGameEnded() {
//        int playersAlive = 0;
//        for (int playerID : players.keySet()) {
//            // Count every player alive
//            if (players.get(playerID).getHealth() > 0) {
//                playersAlive++;
//            }
//        }
//        return playersAlive <= 1;
//    }

    // =====

}
