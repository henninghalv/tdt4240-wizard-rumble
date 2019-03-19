package com.progark.group2.gameserver;


import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.progark.group2.wizardrumble.network.PlayerJoinRequest;
import com.progark.group2.wizardrumble.network.CreateGameRequest;
import com.progark.group2.wizardrumble.network.ServerErrorResponse;
import com.progark.group2.wizardrumble.network.ServerIsFullResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MasterServer {

    private static MasterServer instance = null;

    // List of all server instances. 1 server = 1 game
    private List<GameServer> servers = new ArrayList<GameServer>();
    private Server server;

    private final static int GAMESERVER_COUNT= 100;
    private final static int MAXIMUM_PLAYERS_PER_GAME = 6;
    private final static int MAXIMUM_UDP_PORT_PER_GAMESERVER = 1;

    private final static int DEFAULT_MASTERSERVER_TCP_PORT = 54555;
    private final static int DEFAULT_MASTERSERVER_UDP_PORT = 54777;
    private final static int DEFAULT_GAMESERVER_TCP_PORT = 55000;
    private final static int DEFAULT_GAMESERVER_UDP_PORT =
            DEFAULT_GAMESERVER_TCP_PORT + (GAMESERVER_COUNT * MAXIMUM_PLAYERS_PER_GAME) + 1;

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
                if (object instanceof PlayerJoinRequest) {


                    // choose or create a gameserver:

                    // If none found, create a new gameserver and add player

                    sendGameCreatedResponse(connection);
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
            // Need one port per player for tcp and this for every gameserver.
            for (int j = 0; j < MAXIMUM_PLAYERS_PER_GAME; j++) {
                TCP_PORTS.put(j, "open");
            }
        }

        // Populate udp
        for (int i = DEFAULT_GAMESERVER_UDP_PORT; i <  DEFAULT_GAMESERVER_UDP_PORT + GAMESERVER_COUNT; i++) {
            UDP_PORTS.put(i, "open");
        }
    }

    /**
     * Finds an available TCP ports from hashmap
     * @return  (int)  The first open port number for TCP
     */
    private List<Integer> findTCPPorts() {
        List<Integer> tcpPorts = new ArrayList<Integer>();
        for (int tcpPort : TCP_PORTS.keySet()) {
            if ("open".equals(TCP_PORTS.get(tcpPort))) {
                tcpPorts.add(tcpPort);
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
    private List<Integer> findUDPPorts() {
        List<Integer> udpPorts = new ArrayList<Integer>();
        for (int port : UDP_PORTS.keySet()) {
            if ("open".equals(UDP_PORTS.get(port))) {
                UDP_PORTS.put(port, "closed");
                udpPorts.add(port);
            }

            if (udpPorts.size() == MAXIMUM_UDP_PORT_PER_GAMESERVER) {
                break;
            }
        }
        return udpPorts;
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

        // Open all tcpports
        for (int tcpPort : server.getTCPPorts().keySet()) {
            TCP_PORTS.put(tcpPort, "open");
        }

        // Open udpport
        UDP_PORTS.put(server.getUDPPort(), "open");

        // Remove gameserver from MasterServer
        this.servers.remove(server);
    }

    /**
     * Creates a new GameServer instance and registers request and
     * response classes.
     * @return      GameServer object
     */
    private GameServer createNewServer(
            List<Integer> tcpPorts, int udpPort
    ) throws IOException {
        return new GameServer(tcpPorts, udpPort);
    }

    private void sendGameCreatedResponse(Connection connection) {

        // Find a available ports
        List<Integer> tcpPorts = findTCPPorts();
        List<Integer> udpPorts = findUDPPorts();

        // If gameservers are full
        if (tcpPorts.size() <= 0 || udpPorts.size() <= 0) {
            ServerIsFullResponse response = new ServerIsFullResponse();
            response.setIsFull(true);
            connection.sendTCP(response);
        } else {
            // Create a response with the new Gameserver ports
            CreateGameRequest request = new CreateGameRequest();
            request.setMap(new HashMap<String, List<Integer>>());

            // Add available ports for a game server.
            request.getMap().put("tcpPort", tcpPorts);
            request.getMap().put("udpPort", udpPorts);

            // Init a new server
            try {
                addGameServer(createNewServer(tcpPorts, udpPorts.get(0)));
                connection.sendTCP(request);
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
    String getPlayerName(int playerID) {
        String playerName = "Guest"; // Placeholder?
        // TODO: Retrieve playername from database from playerID
        return playerName;
    }

    public static void main(String[] args) throws IOException {
        // Init master server
        MasterServer.getInstance();
    }
}
