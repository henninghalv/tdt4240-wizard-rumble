package com.progark.group2.wizardrumble.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.progark.group2.wizardrumble.network.requests.GameStartRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerJoinedRequest;
import com.progark.group2.wizardrumble.network.responses.PlayerJoinedResponse;
import com.progark.group2.wizardrumble.network.responses.ServerErrorResponse;
import com.progark.group2.wizardrumble.network.responses.ServerSuccessResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NetworkController {

    private static NetworkController instance = null;

    // TODO: Set this based on generated ID from server
    private final static int playerID = 6;

    // IP address to MasterServer
    private final static int TIMEOUT = 5000;
    private final static String MASTER_SERVER_HOST = "localhost";
    private final static int MASTER_SERVER_TCP_PORT = 54555;
    private final static int MASTER_SERVER_UDP_PORT = 54777;

    private NetworkController() throws IOException {

        // Client for handling communication with master server
        final Client masterServerClient = createAndConnectClient(
                TIMEOUT,
                MASTER_SERVER_HOST,
                MASTER_SERVER_TCP_PORT,
                MASTER_SERVER_UDP_PORT
        );

        // TODO Register player before joining game
        // Send request to join
        PlayerJoinedRequest request = new PlayerJoinedRequest();
        request.setPlayerID(playerID); // TODO: ID is generated through name registering
        masterServerClient.sendTCP(request);

        masterServerClient.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof PlayerJoinedResponse) {
                    PlayerJoinedResponse response = (PlayerJoinedResponse) object;
                    requestGameCreation(response);
                    // Creating game server, the closing the master server connection
                    closeClientConnection(masterServerClient);
                } else if (object instanceof ServerErrorResponse) {
                    // If there is a server error
                    ServerErrorResponse response = (ServerErrorResponse) object;
                    System.out.println("Client got this error message: " + response.getErrorMsg());
                    // TODO: Handle server error on client side. Give error message to interface
                }
            }
        });
    }

    public void requestGameCreation(PlayerJoinedResponse response){
        System.out.println("tcp " + response.getTcpPort());
        System.out.println("udp " + response.getUdpPort());
        try {
            // Client tries to connect to the given GameServer
            Client client = createAndConnectClient(
                    TIMEOUT,
                    MASTER_SERVER_HOST,
                    response.getTcpPort(),
                    response.getUdpPort()
            );
            // Player asks to join gameserver
            PlayerJoinedRequest request = new PlayerJoinedRequest();
            request.setPlayerID(playerID);
            client.sendTCP(request);
            // Add listeners to the connection
            addGameServerListener(client);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addGameServerListener(Client client){
        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof ServerSuccessResponse) {
                    // TODO: Wait for game to start.
                }
                else if (object instanceof GameStartRequest){

                }
                // TODO: Add listeners for game status.
            }
        });
    }

    public Client createAndConnectClient(Integer timeout, String host, Integer tcp, Integer udp) throws IOException {
        Client client = new Client();
        client.start();
        client.connect(timeout, host, tcp, udp);
        KryoClientRegister.registerKryoClasses(client);  // Register classes for kryo serializer
        return client;
    }

    public void closeClientConnection(Client client){
        client.close();
        client.stop();
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
