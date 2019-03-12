package com.progark.group2.gameserver;


import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.progark.group2.wizardrumble.network.CreateGameRequest;
import com.progark.group2.wizardrumble.network.CreateGameResponse;
import com.progark.group2.wizardrumble.network.PlayerJoinedRequest;
import com.progark.group2.wizardrumble.network.ServerErrorResponse;
import com.progark.group2.wizardrumble.network.ServerIsFullResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MasterServer {

    private static MasterServer instance = null;

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
            response.isFull = true;
            connection.sendTCP(response);
        } else {
            // Create a response with the new Gameserver ports
            CreateGameResponse response = new CreateGameResponse();
            response.map = new HashMap<String, Integer>();
            response.map.put("tcpPort", tcpPort);
            response.map.put("udpPort", udpPort);

            // Init a new server
            try {
                addGameServer(createNewServer(tcpPort, udpPort));
                connection.sendTCP(response);
            } catch (IOException e) {
                ServerErrorResponse errorResponse = new ServerErrorResponse();
                errorResponse.errorMsg =
                        "Something is wrong with the server. Please try again later.";
                connection.sendTCP(errorResponse);
                e.printStackTrace();
            }
        }
    }

    static String getPlayerName(int playerID) {
        String playerName = "Guest";
        // TODO: Retrieve playername from database from playerID
        return playerName;
    }

    public static void main(String[] args) throws IOException {
        // Init master server
        MasterServer.getInstance();
    }
}
