package com.progark.group2.wizardrumble.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.progark.group2.wizardrumble.entities.WizardPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NetworkController {

    private static NetworkController instance = null;

    // TODO: Set this based on generated ID from server
    private final static int playerID = 0;

    // IP address to MasterServer
    private final static int TIMEOUT = 5000;
    private final static String MASTER_SERVER_HOST = "localhost";
    private final static int MASTER_SERVER_TCP_PORT = 54555;
    private final static int MASTER_SERVER_UDP_PORT = 54777;

    // Client for handling communication with given game server
    private static Client client = new Client();

    private NetworkController() throws IOException {

        // Client for handling communication with master server
        Client masterServerClient = new Client();
        masterServerClient.start();
        masterServerClient.connect(
                TIMEOUT,
                MASTER_SERVER_HOST,
                MASTER_SERVER_TCP_PORT,
                MASTER_SERVER_UDP_PORT
        );

        // Register classes for kryo serializer
        KryoClientRegister.registerKryoClasses(masterServerClient);

        final CreateGameRequest request = new CreateGameRequest();
        request.setPlayerID(playerID); // TODO: ID is generated through name registering
        masterServerClient.sendTCP(request);

        masterServerClient.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof CreateGameResponse) {
                    CreateGameResponse response = (CreateGameResponse) object;
                    try {
                        // Client tries to connect to the given GameServer
                        client.close();
                        client.start();
                        client.connect(
                                TIMEOUT,
                                MASTER_SERVER_HOST,
                                response.getMap().get("tcpPort"),
                                response.getMap().get("udpPort")
                        );

                        // Register classes for kryo serializer
                        KryoClientRegister.registerKryoClasses(client);

                        // Let the client join the game server (lobby)
                        PlayerJoinedRequest requestToJoin = new PlayerJoinedRequest();
                        client.sendTCP(requestToJoin);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (object instanceof PlayersHealthStatusRequest) {
                    PlayersHealthStatusRequest request = (PlayersHealthStatusRequest) object;
                    // Find player instance
                    WizardPlayer player = WizardPlayer.getInstance();

                    // Player lose health
                    WizardPlayer.getInstance().loseHealth(
                            request.getMap().get(player.getPlayerID()));

                } else if (object instanceof ServerIsFullResponse) {
                    // If all servers are full
                    ServerIsFullResponse response = (ServerIsFullResponse) object;
                    System.out.println("Client got that the server is full: " + response.getIsFull());
                    // TODO: Handle server is full - display message on interface
                } else if (object instanceof ServerErrorResponse) {
                    // If there occures a server error
                    ServerErrorResponse response = (ServerErrorResponse) object;
                    System.out.println("Client got this error message: " + response.getErrorMsg());
                    // TODO: Handle server error on client side. Give error message to interface
                }
            }
        });
    }

    /**
     * Get networkcontroller instance if not existing
     * @return  (NetworkController) The instance made or retrieved
     * @throws IOException  If instansiating a server went wrong
     */
    public static NetworkController getInstance() throws IOException {
        if (instance == null) {
            instance = new NetworkController();
        }
        return instance;
    }

    /**
     * Send a request to the gameserver that this player has died.
     */
    public void sendPlayerDeadRequest() {
        PlayerDeadRequest requestPlayerDied = new PlayerDeadRequest();
        requestPlayerDied.setPlayerID(playerID);
        client.sendTCP(requestPlayerDied);
    }

    public Map getStats(){
        //TODO
        return new HashMap();
    }

    public Map updateStats(Map map){
        //TODO
        return new HashMap();
    }


    public Map updateGameState(Map map){
        //TODO
        return new HashMap();
    }

    public void handleUpdate(){

    }
}
