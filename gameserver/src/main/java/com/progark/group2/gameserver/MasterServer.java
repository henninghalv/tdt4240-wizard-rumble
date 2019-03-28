package com.progark.group2.gameserver;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.progark.group2.gameserver.misc.AsciiArtCreator;
import com.progark.group2.gameserver.resources.GameStatus;
import com.progark.group2.gameserver.resources.PortStatus;
import com.progark.group2.wizardrumble.network.requests.CreateGameRequest;
import com.progark.group2.wizardrumble.network.requests.CreatePlayerRequest;
import com.progark.group2.wizardrumble.network.responses.CreateGameResponse;
import com.progark.group2.wizardrumble.network.responses.CreatePlayerResponse;
import com.progark.group2.wizardrumble.network.responses.ServerErrorResponse;
import com.progark.group2.gameserver.database.SQLiteDBConnector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MasterServer {

    private static MasterServer instance = null;
    private static SQLiteDBConnector connector;

    // List of all server instances. 1 server = 1 game
    private HashMap<GameServer, GameStatus> gameServers = new HashMap<GameServer, GameStatus>();
    private Server server;

    private final static int GAMESERVER_COUNT = 100;
    private final static int MAXIMUM_PLAYERS_PER_GAME = 6;

    private final static int DEFAULT_MASTERSERVER_TCP_PORT = 54555;
    private final static int DEFAULT_MASTERSERVER_UDP_PORT = 54777;
    private final static int DEFAULT_GAMESERVER_TCP_PORT = 55000;
    private final static int DEFAULT_GAMESERVER_UDP_PORT =
            DEFAULT_GAMESERVER_TCP_PORT + (GAMESERVER_COUNT * MAXIMUM_PLAYERS_PER_GAME) + 1;

    private final static HashMap<Integer, PortStatus> TCP_PORTS =
            new HashMap<Integer, PortStatus>();
    private final static HashMap<Integer, PortStatus> UDP_PORTS =
            new HashMap<Integer, PortStatus>();

    private MasterServer(final int tcpPort, final int udpPort) throws IOException {
        // Create a list of TCP and UDP ports that are available
        Log.info("Creating ports...");
        populateTCPAndUDPPorts();
        Log.info("Done!\n");
        // Init server
        Log.info("Initializing master server...");
        server = createAndConnectServer(tcpPort, udpPort);
        Log.info("Done!\n");
        // Add a receiver listener to server
        Log.info("Adding listener for PlayerJoinedRequest...");
        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                // If the client wants to create a new game lobby
                if (object instanceof CreateGameRequest) {
                    Log.info("Received PlayerJoinedRequest...");
                    handleCreateGameRequest(connection);
                }
                else if (object instanceof CreatePlayerRequest){
                    Log.info("Received CreateNewPlayerRequest...");
                    CreatePlayerRequest request = (CreatePlayerRequest) object;
                    try {
                        handleCreatePlayerRequest(connection, request.getPlayerName());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        Log.info("Done!\n");
    }

    /**
     * Singleton class - This will get MasterServer instance unless it's defined
     * @return  Returns the MasterServer instance
     * @throws IOException      Exception upon creation of master server
     */
    static MasterServer getInstance() throws IOException {
        if (instance == null) {
            instance = new MasterServer(DEFAULT_MASTERSERVER_TCP_PORT, DEFAULT_MASTERSERVER_UDP_PORT);
        }
        return instance;
    }

    // SETUP METHODS

    /**
     * Populates a hashmap for TCP and UDP ports so the master server knows
     * which ports that are open.
     */
    private void populateTCPAndUDPPorts() {
        // Populate tcp
        for (int i = DEFAULT_GAMESERVER_TCP_PORT; i <  DEFAULT_GAMESERVER_TCP_PORT + GAMESERVER_COUNT; i++) {
            // Need one port per player for tcp and this for every gameserver.
            TCP_PORTS.put(i, PortStatus.OPEN);
        }

        // Populate udp
        for (int i = DEFAULT_GAMESERVER_UDP_PORT; i <  DEFAULT_GAMESERVER_UDP_PORT + GAMESERVER_COUNT; i++) {
            UDP_PORTS.put(i, PortStatus.OPEN);
        }
    }

    /**
     * Creates a new KryoNet Server and connects it
     * @param tcp TCP port
     * @param udp UDP port
     * @return A KryoNet server object
     * @throws IOException
     */
    private Server createAndConnectServer(Integer tcp, Integer udp) throws IOException {
        Server server = new Server();
        server.start();
        server.bind(tcp, udp);
        KryoServerRegister.registerKryoClasses(server);  // Register classes for kryo serializer
        return server;
    }

    // =====


    // REQUEST HANDLING

    /**
     * Method for handling a CreateGameRequest
     * @param connection
     */
    private void handleCreateGameRequest(Connection connection){
        GameServer gameServer = findAvailableGameServer();
        if(gameServer != null){
            sendCreateGameResponse(connection, gameServer);
        }
        else{
            Log.info("No GameServer found!\n");
            Log.info("Trying to create new GameServer...\n");
            // If no server with status STAND_BY is found, try to create a new server
            try {
                gameServer = createNewGameServer();
                addGameServer(gameServer);
                // Send the ports to player with a response
                sendCreateGameResponse(connection, gameServer);
            } catch (IOException e) {
                //TODO: Add server is full if there are maximum game servers
                sendServerErrorResponse(connection, "Something is wrong with the server. Please try again later.");
                e.printStackTrace();
            }
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

    // =====


    // RESPONSES

    /**
     * Sends a reponse to the client when it wants to create a game
     * @param connection
     * @param gameServer
     */
    private void sendCreateGameResponse(Connection connection, GameServer gameServer){
        CreateGameResponse response = new CreateGameResponse();
        response.setTcpPort(gameServer.getAvailableTCPPort());
        response.setUdpPort(gameServer.getAvailableUDPPort());
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

    // =====

    // CREATION

    /**
     * Creates a new GameServer instance and registers request and
     * response classes.
     * @return      GameServer object
     */
    private GameServer createNewGameServer() throws IOException {
        Log.info("Creating a new GameServer...");
        Log.info("Finding available TCP ports...");
        List<Integer> tcpPorts = findAvailableTCPPorts();
        Log.info("Done!");
        Log.info("Finding available UDP ports...");
        List<Integer> udpPorts = findAvailableUDPPorts();
        Log.info("Done!");

        if (tcpPorts.size() <= 0 || udpPorts.size() <= 0) {
            throw new IllegalStateException(
                    "There are not enough ports to create a new GameServer...");
        }

        if (tcpPorts.size() < MAXIMUM_PLAYERS_PER_GAME) {
            throw new IllegalStateException(
                    "There aren't enough tcp ports to handle the maximum amount of players");
        }

        // Return a list of all tcpPorts, one for each player. They all have same udp port.
        Log.info("GameServer created!\n");
        return new GameServer(tcpPorts, udpPorts);
    }

    // =====

    // ACTIONS

    private GameServer findAvailableGameServer(){
        Log.info("Looking for available GameServer...");
        // For all gameServers that are not in progress, but on standby
        for (GameServer gs : gameServers.keySet()) {
            if (GameStatus.STAND_BY.equals(gameServers.get(gs))) {
                Log.info("Found one!\n");
                return gs;
            }
        }
        return null;
    }

    /**
     * Finds an available TCP ports from hashmap
     * @return  (int)  The first open port number for TCP
     */
    private List<Integer> findAvailableTCPPorts() {
        List<Integer> tcpPorts = new ArrayList<Integer>();
        for (int tcpPort : TCP_PORTS.keySet()) {
            if (PortStatus.OPEN.equals(TCP_PORTS.get(tcpPort))) {
                tcpPorts.add(tcpPort);
                TCP_PORTS.put(tcpPort, PortStatus.CLOSED);
            }

            if (tcpPorts.size() == MAXIMUM_PLAYERS_PER_GAME) {
                break;
            }
        }
        return tcpPorts;
    }

    /**
     * Finds an available UDP port from hashmap
     * @return  (int)   The first open port number for UDP
     */
    private List<Integer> findAvailableUDPPorts() {
        List<Integer> udpPorts = new ArrayList<Integer>();
        for (int udpPort : UDP_PORTS.keySet()) {
            if (PortStatus.OPEN.equals(UDP_PORTS.get(udpPort))) {
                udpPorts.add(udpPort);
                UDP_PORTS.put(udpPort, PortStatus.CLOSED);
            }

            if (udpPorts.size() == MAXIMUM_PLAYERS_PER_GAME) {
                break;
            }
        }
        return udpPorts;
    }
    /**
     * Add server to the list of all servers on standby
     * @param server    GameServer object
     */
    private void addGameServer(GameServer server) {
        this.gameServers.put(server, GameStatus.STAND_BY);
    }

    /**
     * Updates the game server status to open or closed
     * @param server
     * @param status
     */
    void updateGameServerStatus(GameServer server, GameStatus status){
        gameServers.put(server, status);
    }

    /**
     * Remove server from the list of all servers and
     * updates which ports that are now open
     */
    void removeGameServer(GameServer server) {
        // Open all tcpports
        for (int tcpPort : server.getTCPPorts().keySet()) {
            TCP_PORTS.put(tcpPort, PortStatus.OPEN);
        }

        // Open all udpPorts
        for (int udpPort : server.getUDPPorts().keySet()) {
            TCP_PORTS.put(udpPort, PortStatus.OPEN);
        }

        // Remove gameserver from MasterServer
        this.gameServers.remove(server);
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
            Log.info("Player joined: " + playerName + "\n");
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
        MasterServer.getInstance();
        try {
            // Init DB connection
            connector = SQLiteDBConnector.getInstance();
            connector.connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
