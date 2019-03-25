package com.progark.group2.gameserver;


import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.progark.group2.gameserver.database.SQLiteDBConnector;
import com.progark.group2.wizardrumble.network.CreateGameRequest;
import com.progark.group2.wizardrumble.network.CreateGameResponse;
import com.progark.group2.wizardrumble.network.PlayerJoinedRequest;
import com.progark.group2.wizardrumble.network.ServerErrorResponse;
import com.progark.group2.wizardrumble.network.ServerIsFullResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MasterServer {

    private static MasterServer instance = null;
    private static SQLiteDBConnector connector;

    // List of all server instances. 1 server = 1 game
    private List<GameServer> servers = new ArrayList<GameServer>();
    private Server server;

    private final static int GAMESERVER_COUNT= 1000;

    private final static int DEFAULT_MASTERSERVER_TCP_PORT = 54555;
    private final static int DEFAULT_MASTERSERVER_UDP_PORT = 54777;
    private final static int DEFAULT_GAMESERVER_TCP_PORT = 55000;
    private final static int DEFAULT_GAMESERVER_UDP_PORT = DEFAULT_GAMESERVER_TCP_PORT + GAMESERVER_COUNT + 1;

    private final static HashMap<Integer, String> TCP_PORTS =
            new HashMap<Integer, String>();
    private final static HashMap<Integer, String> UDP_PORTS =
            new HashMap<Integer, String>();

    private MasterServer(int tcpPort, int udpPort) throws IOException {
        // Create a list of TCP and UDP ports that are available
        populateTCPAndUDPPorts();

        // Init server
        server = new Server();
        server.start();
        server.bind(tcpPort, udpPort);

        // Register response and request classes for kryonet serialize
        KryoServerRegister.registerKryoClasses(server);

        // Add a receiver listener to server
        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                // If the client wants to create a new game lobby
                if (object instanceof CreateGameRequest) {
                    sendGameCreatedResponse(connection);
                } else if (object instanceof PlayerJoinedRequest) {
                    System.out.println("THIS IS MASTER SERVER DONT GIVE ME THIS CRAP");
                }
            }
        });
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

    /**
     * Populates a hashmap for TCP and UDP ports so the master server knows
     * which ports that are open.
     */
    private void populateTCPAndUDPPorts() {
        // Populate tcp
        for (int i = DEFAULT_GAMESERVER_TCP_PORT; i <  DEFAULT_GAMESERVER_TCP_PORT + GAMESERVER_COUNT; i++) {
            TCP_PORTS.put(i, "open");
        }

        // Populate udp
        for (int i = DEFAULT_GAMESERVER_UDP_PORT; i <  DEFAULT_GAMESERVER_UDP_PORT + GAMESERVER_COUNT; i++) {
            UDP_PORTS.put(i, "open");
        }
    }

    /**
     * Finds an available TCP port from hashmap
     * @return  (int)  The first open port number for TCP
     */
    private int findTCPPort() {
        for (int port : TCP_PORTS.keySet()) {
            if ("open".equals(TCP_PORTS.get(port))) {
                TCP_PORTS.put(port, "closed");
                return port;
            }
        }
        return -1;
    }

    /**
     * Finds an available UDP port from hashmap
     * @return  (int)   The first open port number for UDP
     */
    private int findUDPPort() {
        for (int port : UDP_PORTS.keySet()) {
            if ("open".equals(UDP_PORTS.get(port))) {
                UDP_PORTS.put(port, "closed");
                return port;
            }
        }
        return -1;
    }

    /**
     * Add server to the list of all servers
     * @param server    GameServer object
     */
    private void addGameServer(GameServer server) {
        this.servers.add(server);
    }

    /**
     * Remove server from the list of all servers and
     * updates which ports that are now open
     */
    void removeGameServer(GameServer server) {
        TCP_PORTS.put(server.getTCPPort(), "open");
        UDP_PORTS.put(server.getUDPPort(), "open");
        this.servers.remove(server);
    }

    /**
     * Creates a new GameServer instance and registers request and
     * response classes.
     * @return      GameServer object
     */
    private GameServer createNewServer(int tcpPort, int udpPort) throws IOException {
        return new GameServer(tcpPort, udpPort);
    }

    private void sendGameCreatedResponse(Connection connection) {

        // Find a available ports
        int tcpPort = findTCPPort();
        int udpPort = findUDPPort();

        // If gameservers are full
        if (tcpPort == -1 || udpPort == -1) {
            ServerIsFullResponse response = new ServerIsFullResponse();
            response.setIsFull(true);
            connection.sendTCP(response);
        } else {
            // Create a response with the new Gameserver ports
            CreateGameResponse response = new CreateGameResponse();
            response.setMap(new HashMap<String, Integer>());
            response.getMap().put("tcpPort", tcpPort);
            response.getMap().put("udpPort", udpPort);

            // Init a new server
            try {
                addGameServer(createNewServer(tcpPort, udpPort));
                connection.sendTCP(response);
            } catch (IOException e) {
                ServerErrorResponse errorResponse = new ServerErrorResponse();
                errorResponse.setErrorMsg(
                        "Something is wrong with the server. Please try again later.");
                connection.sendTCP(errorResponse);
                e.printStackTrace();
            }
        }
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
            System.out.println("Player: " + playerName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerName;
    }

    public static void main(String[] args) throws IOException {
        // Init master server
        MasterServer.getInstance();
        try {
            connector = SQLiteDBConnector.getInstance();
            connector.connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
