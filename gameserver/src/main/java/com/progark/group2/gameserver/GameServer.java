package com.progark.group2.gameserver;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import com.progark.group2.wizardrumble.network.PlayerDeadRequest;
import com.progark.group2.wizardrumble.network.PlayerJoinedRequest;

import java.io.IOException;
import java.util.HashMap;

public class GameServer {

    private Server server;
    private int TCP_PORT;
    private int UDP_PORT;

    private final static String NAME = "name";
    private final static String KILLS = "kills";
    private final static String IS_DEAD = "isDead";
    private final static String POSITION = "position";
    private final static String TIME_ALIVE = "timeAlive";

    // List of all players that has joined the game with their stats for this game
    private HashMap<Integer, HashMap<String, Object>> players =
            new HashMap<Integer, HashMap<String, Object>>();

    // This is the master server
    GameServer(int tcpPort, int udpPort) throws IOException {
        // Add ports for reference
        TCP_PORT = tcpPort;
        UDP_PORT = udpPort;

        // Set a created a kryo server object
        server = createNewServer(TCP_PORT, UDP_PORT);
    }

    /**
     * Used by master server to determine which tcp port this server used
     * @return  tcp port used by server
     */
    int getTCPPort() {
        return TCP_PORT;
    }

    /**
     * Used by master server to determine which udp port this server used
     * @return  dup port used by server
     */
    int getUDPPort() {
        return UDP_PORT;
    }

    /**
     * Add a new player to list when joining or creating a new game.
     * @param playerID  (int) player id
     */
    private void addJoinedPlayer(int playerID) {
        // Default name if not registered in DB
        String playerName = "Guest";

        // TODO: Get player name from MasterServer => DB
        //playerName = MasterServer.getPlayerName(playerID);

        HashMap<String, Object> playerStats = new HashMap<String, Object>();
        playerStats.put(IS_DEAD, false); // If the player is dead
        playerStats.put(NAME, playerName); // Player name registered
        playerStats.put(KILLS, 0); // Amount of kills in one game
        playerStats.put(TIME_ALIVE, 0); // Time alive in a game
        playerStats.put(POSITION, -1); // Placement based on when the player died
        // TODO: Consider adding more info to register more metadata

        // Add playerstats to the list of joined players
        players.put(playerID, playerStats);
    }

    /**
     * Removes a player when the player is leaving the game or disconnects.
     * Player stats will not be saved.
     * @param playerID  (int) player id
     */
    protected void removeJoinedPlayer(int playerID) {
        players.remove(playerID);
    }

    /**
     * Sets a player status as dead.
     * @param playerID  (int) player id
     */
    private void addDeadPlayer(int playerID) {
        players.get(playerID).put(IS_DEAD, true);
    }

    /**
     * Return whether all players are dead and the game has ended.
     * @return  Boolean     True if all players have died
     */
    private boolean hasGameEnded() {
        for (int playerID : players.keySet()) {
            // If one player is alive
            if (!(Boolean) players.get(playerID).get(IS_DEAD)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new Kryo Server instance and registers request and
     * response classes.
     * @return      Kryo Server object
     */
    private Server createNewServer(int tcpPort, int udpPort) throws IOException {
        Server server = new Server();
        server.start();
        server.bind(tcpPort, udpPort);

        // Register response and request classes for kryo serializer
        KryoServerRegister.registerKryoClasses(server);

        // Add a receiver listener to server
        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof PlayerDeadRequest) {
                    // If a player is dead.
                    PlayerDeadRequest request = (PlayerDeadRequest)object;

                    // Add player to list of dead player
                    addDeadPlayer(request.playerID);
                    System.out.println("Player ID: " + request.playerID + " has been moved to deadPlayerIDs list");

                    // End game if all players are dead
                    endGame(connection);
                } else if (object instanceof PlayerJoinedRequest) {
                    // If a player has joined
                    PlayerJoinedRequest request = (PlayerJoinedRequest)object;

                    // Add player to list of joined players
                    addJoinedPlayer(request.playerID);
                    System.out.println("Player ID: " + request.playerID + " has been moved to joined player list");

                }
            }
        });

        return server;
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
        // Stop the server connection
        server.stop();
        try {
            // Try removing this from the master server
            // This should open the used ports in master server
            MasterServer.getInstance().removeGameServer(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        GameServer gs = new GameServer(54556, 544557);
        gs.hasGameEnded();
    }
}
