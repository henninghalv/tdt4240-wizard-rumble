package com.progark.group2.gameserver;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import com.progark.group2.wizardrumble.network.PlayerDeadRequest;
import com.progark.group2.wizardrumble.network.PlayerStatisticsResponse;
import com.progark.group2.wizardrumble.network.ServerErrorResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameServer {

    private Server server;

    // List of all joinedPlayerIDs joined the game
    private List<Integer> joinedPlayerIDs = new ArrayList<Integer>();

    // List of all joinedPlayerIDs that are dead in the game
    private List<Integer> deadPlayerIDs = new ArrayList<Integer>();

    // This is the master server
    protected GameServer(int tcpPort, int udpPort) throws IOException {
        server = createNewServer(tcpPort, udpPort);
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
    private void addDeadPlayer(int playerID) {
        deadPlayerIDs.add(playerID);
    }

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
        kryo.register(ServerErrorResponse.class);
        kryo.register(PlayerDeadRequest.class);
        kryo.register(PlayerStatisticsResponse.class);

        // Add a receiver listener to server
        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof PlayerDeadRequest) {
                    // If a player is dead.
                    PlayerDeadRequest request = (PlayerDeadRequest)object;

                    // Add player to list of dead player
                    addDeadPlayer(request.playerID);
                    System.out.println("Player ID: " + request.playerID + " Has been moved to deadPlayerIDs list");

                    /*CreateGameResponse response = new CreateGameResponse();
                    response.text = "Thanks";
                    connection.sendTCP(response);*/
                }
            }
        });

        return server;
    }

    /**
     * When the game has ended and all joinedPlayerIDs has left the game,
     * the server stops and removes itself from the MasterServer.
     * @throws IOException      Failed to initialize MasterServer
     */
    public void endGame() throws IOException {
        if (!hasGameEnded()) return;

        // TODO: send request with metadata (scoreboard data) to all clients

        server.stop();
        MasterServer.getInstance().removeGameServer(this);
    }
}
