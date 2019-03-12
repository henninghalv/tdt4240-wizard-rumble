package com.progark.group2.gameserver;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import com.progark.group2.wizardrumble.network.CreateGameRequest;
import com.progark.group2.wizardrumble.network.CreateGameResponse;
import com.progark.group2.wizardrumble.network.PlayerDeadRequest;
import com.progark.group2.wizardrumble.network.PlayerJoinedRequest;
import com.progark.group2.wizardrumble.network.PlayerStatisticsResponse;
import com.progark.group2.wizardrumble.network.ServerErrorResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameServer {

    private Server server;
    private int TCP_PORT;
    private int UDP_PORT;

    // List of all joinedPlayerIDs joined the game
    private List<Integer> joinedPlayerIDs = new ArrayList<Integer>();

    // List of all joinedPlayerIDs that are dead in the game
    private List<Integer> deadPlayerIDs = new ArrayList<Integer>();

    // This is the master server
    protected GameServer(int tcpPort, int udpPort) throws IOException {
        // Add ports for reference
        TCP_PORT = tcpPort;
        UDP_PORT = udpPort;

        // Set a created a kryo server object
        server = createNewServer(TCP_PORT, UDP_PORT);
    }

    public int getTCPPort() {
        return TCP_PORT;
    }

    public int getUDPPort() {
        return UDP_PORT;
    }

    /**
     * Add a new player to list when joining or creating a new game.
     * @param playerID  (int) player id
     */
    protected void addJoinedPlayer(int playerID) {
        this.joinedPlayerIDs.add(playerID);
    }

    /**
     * Removes a player when the player is leaving the game or disconnects.
     * @param playerID  (int) player id
     */
    protected void removeJoinedPlayer(int playerID) {
        for (int i = 0; i < joinedPlayerIDs.size(); i++) {
            if (joinedPlayerIDs.get(i) == playerID) {
                joinedPlayerIDs.remove(i);
                break;
            }
        }
    }

    /**
     * Adds a player to the list of dead players.
     * @param playerID  (int) player id
     */
    private void addDeadPlayer(int playerID) { deadPlayerIDs.add(playerID); }

    /**
     * Return whether all players are dead and the game has ended.
     * @return  Boolean     True if all players have died
     */
    private boolean hasGameEnded() {
        return deadPlayerIDs.size() == joinedPlayerIDs.size();
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

        // Register response and request classes
        Kryo kryo = server.getKryo();
        kryo.register(PlayerJoinedRequest.class);
        kryo.register(PlayerDeadRequest.class);
        kryo.register(PlayerStatisticsResponse.class);
        kryo.register(ServerErrorResponse.class);
        kryo.register(CreateGameRequest.class);
        kryo.register(CreateGameResponse.class);
        kryo.register(HashMap.class);

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
    public void endGame(Connection connection) {
        if (!hasGameEnded()) return;

        // TODO: Create a timeout for when the server shutdown anyway

        // TODO: send request with metadata (scoreboard data) to all clients
        //PlayerStatisticsResponse response = new PlayerStatisticsResponse();

        // TODO: FIll response with players metadata

        // Send the response back to client
        //connection.sendTCP(response);

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
}
