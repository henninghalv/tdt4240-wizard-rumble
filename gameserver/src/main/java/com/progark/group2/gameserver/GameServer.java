package com.progark.group2.gameserver;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.progark.group2.wizardrumble.entities.Wizard;
import com.progark.group2.wizardrumble.network.PlayerDeadRequest;
import com.progark.group2.wizardrumble.network.PlayerJoinedRequest;
import com.progark.group2.wizardrumble.network.PlayersHealthStatusRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameServer {

    private List<Server> servers = new ArrayList<Server>();
    private HashMap<Integer, Integer> TCP_PORTS = new HashMap<Integer, Integer>();
    private Integer UDP_PORT;

    private final static String NAME = "name";
    private final static String KILLS = "kills";
    private final static String HEALTH = "health";
    private final static String RANK = "rank";
    private final static String TIME_ALIVE = "timeAlive";
    private final static String PLAYER_TCP_PORT = "tcpPort";
    private final static String PLAYER_UDP_PORT = "udpPort";


    // List of all players that has joined the game with their stats for this game
    private HashMap<Integer, HashMap<String, Object>> players =
            new HashMap<Integer, HashMap<String, Object>>();

    // This is the master server
    GameServer(List<Integer> tcpPorts, int udpPort) throws IOException {
        // Add ports for reference
        for (int tcpPort : tcpPorts) {
            // Adds port available for clients to connect to.
            TCP_PORTS.put(tcpPort, null);
        }

        UDP_PORT = udpPort;

        // Create one kryo server object per tcp port and add to list of servers
        for (int tcpPort : tcpPorts) {
            servers.add(createNewServer(tcpPort, udpPort));
        }
    }

    /**
     * Used by master server to determine which tcp port this server used
     * @return  tcp port used by server
     */
    HashMap<Integer, Integer> getTCPPorts() {
        return TCP_PORTS;
    }

    /**
     * Used by master server to determine which udp port this server used
     * @return  udp port used by server
     */
    Integer getUDPPort() {
        return UDP_PORT;
    }

    /**
     * Add a new player to list when joining or creating a new game.
     * @param playerID  (int) player id
     */
    private void addJoinedPlayer(Connection connection, int playerID) {
        // Default name if not registered in DB
        String playerName = "Guest";

        // TODO: Get player name from MasterServer => DB
        //playerName = MasterServer.getPlayerName(playerID);

        Integer givenTCPPort = null;

        // Get an available tcpport at the gameserver that is not used by any other player
        for (int tcpPort : TCP_PORTS.keySet()) {
            // If the port is available for the client to use
            if (TCP_PORTS.get(tcpPort) == null) {
                givenTCPPort = tcpPort;
                // Set the port busy by a playerid
                TCP_PORTS.put(tcpPort, playerID);
                break;
            }
        }

        // If there aren't any tcpPorts available for the player on the gameserver, throw expection
        if (givenTCPPort == null) {
            throw new IllegalArgumentException("Player cannot join. Gameserver cannot give a tcp port");
        }

        // If no UDP ports available
        if (UDP_PORT == null) {
            throw new IllegalArgumentException("Player cannot join. Gameserver cannot give a udp port");
        }

        HashMap<String, Object> playerStats = new HashMap<String, Object>();
        playerStats.put(NAME, playerName); // Player name registered
        playerStats.put(KILLS, 0); // Amount of kills in one game
        playerStats.put(HEALTH, Wizard.DEFAULT_HEALTH); // Start health of player
        playerStats.put(TIME_ALIVE, 0); // Time alive in a game
        playerStats.put(RANK, -1); // Rank based on when the player died
        playerStats.put(PLAYER_TCP_PORT, givenTCPPort); // Given tcp port
        playerStats.put(PLAYER_UDP_PORT, UDP_PORT); // Give a udp port

        // Add playerstats to the list of joined players
        players.put(playerID, playerStats);
    }

    /**
     * Return whether all players are dead and the game has ended.
     * @return  Boolean     True if all players have died
     */
    private boolean hasGameEnded() {
        int playersAlive = 0;
        for (int playerID : players.keySet()) {
            // Count every player alive
            if ((Integer) players.get(playerID).get(HEALTH) > 0) {
                playersAlive++;
            }
        }
        return playersAlive <= 1;
    }

    /**
     * Creates a new Kryo Server instance and registers request and
     * response classes.
     * @return      Kryo Server object
     */
    private Server createNewServer(int tcpPort, int udpPort) throws IOException {
        Server server = new Server();
        server.start();
        System.out.println("creating me by using ports" + tcpPort + " AND " + udpPort);
        server.bind(tcpPort, udpPort);

        // Register response and request classes for kryo serializer
        KryoServerRegister.registerKryoClasses(server);

        // Add a receiver listener to server
        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof PlayerJoinedRequest) {
                    // If a player has joined
                    PlayerJoinedRequest request = (PlayerJoinedRequest)object;

                    // Add player to list of joined players
                    addJoinedPlayer(connection, request.getPlayerID());
                    System.out.println("Player ID: " + request.getPlayerID() + " has been moved to joined player list");
                }
            }
        });

        return server;
    }

    /**
     * Subtracts the player's health equal to the damage taken
     * and updates the game data correspondingly.
     * @param playerID  (int) the id of the player taken damage
     * @param damage    (int) damage taken
     */
    public void takeDamage(int playerID, int damage) {
        // The player's previous health
        int previousHealth = (Integer) players.get(playerID).get(HEALTH);

        // Update player's health
        players.get(playerID).put(HEALTH, previousHealth - damage);

        if (previousHealth - damage <= 0) {
            // TODO: send player is dead request to all clients
            PlayerDeadRequest request = new PlayerDeadRequest();
            request.setPlayerID(playerID);
        }
    }

    /**
     * Sends all players health status to all clients.
     * This should be called between a set time interval
     * to update all clients about the health to all players.
     */
    // TODO: Call this function between a set time interval.
    public void sendPlayersHealthStatusRequest() {
        PlayersHealthStatusRequest request = new PlayersHealthStatusRequest();
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

        // Add each players health status to the map
        for (int playerID : players.keySet()) {
            map.put(playerID, (Integer) players.get(playerID).get(HEALTH));
        }

        // Add the health status in the request
        request.setMap(map);

        // Send health status update to all clients
        for (Server server : servers) {
            // TODO: Go through all clients joined and send this request through connection.sendTCP(request).
        }
    }

    /**
     * When the game has ended and all joinedPlayerIDs has left the game,
     * the server stops and removes itself from the MasterServer.
     */
    private void endGame(Connection connection) {
        if (!hasGameEnded()) return;

        // TODO: Create a timeout for when the server should shutdown anyway

        // TODO: send request with metadata (scoreboard data) to all clients
        //PlayerStatisticsResponse response = new PlayerStatisticsResponse();

        // TODO: FIll response with players metadata

        // Send the response back to client
        //connection.sendTCP(response);

        System.out.println("ALL PLAYERS ARE DEAD. STOPPING GAMESERVER: GOODBYE WORLD");
        // Stop the server connection for all servers
        for (Server server : servers) {
            server.stop();
        }

        try {
            // Try removing this from the master server
            // This should open the used ports in master server
            MasterServer.getInstance().removeGameServer(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public static void main(String[] args) throws IOException {
        GameServer gs = new GameServer(54556, 544557);
        gs.hasGameEnded();
    }*/
}
