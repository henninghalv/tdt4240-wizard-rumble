package com.progark.group2.wizardrumble.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.progark.group2.wizardrumble.network.requests.CreateGameRequest;
import com.progark.group2.wizardrumble.network.requests.CreatePlayerRequest;
import com.progark.group2.wizardrumble.network.requests.GameStartRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerJoinedRequest;
import com.progark.group2.wizardrumble.network.responses.CreateGameResponse;
import com.progark.group2.wizardrumble.network.responses.CreatePlayerResponse;
import com.progark.group2.wizardrumble.network.responses.ServerErrorResponse;
import com.progark.group2.wizardrumble.network.responses.ServerSuccessResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NetworkController {

    private static NetworkController instance;
    private static Client masterServerClient;
    private static Preferences userPreferences = Gdx.app.getPreferences("user");
    private static int playerId;
    // Master server configuration constants
    private final static int TIMEOUT = 5000;
    private final static String MASTER_SERVER_HOST = "localhost";  // Set this to the local IP address of your computer when running the server
    private final static int MASTER_SERVER_TCP_PORT = 54555;
    private final static int MASTER_SERVER_UDP_PORT = 54777;

    private NetworkController() throws IOException {
        // Client for handling communication with master server
        Log.info("Creating connection to MasterServer...");
        masterServerClient = createAndConnectClient(
                TIMEOUT,
                MASTER_SERVER_HOST,
                MASTER_SERVER_TCP_PORT,
                MASTER_SERVER_UDP_PORT
        );
        Log.info("Done!\n");
        Log.info("Adding listeners...");
        addMasterServerListener();
        Log.info("Getting local data...");
        getLocalData();
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

    // REQUESTS

    /**
     * Sends a request to create a new player in the database.
     * @param username
     */
    void requestPlayerCreation(String username){
        CreatePlayerRequest request = new CreatePlayerRequest();
        request.setPlayerName(username);
        masterServerClient.sendTCP(request);
    }

    /**
     * Sends a request to create a new game.
     */
    public void requestGameCreation(){
        Log.info("Sending PlayerJoinedRequest to MasterServer...");
        CreateGameRequest request = new CreateGameRequest();
        request.setPlayerId(playerId); // TODO: ID is generated through name registering
        masterServerClient.sendTCP(request);
        Log.info("Done!\n");
    }

    // =====

    // ACTIONS

    /**
     * Gets the locally stored player data. If no data, prompt for new data.
     */
    private void getLocalData(){
        Preferences preferences = Gdx.app.getPreferences("user");
        playerId = preferences.getInteger("playerId", 0);
        if(playerId == 0){
            Gdx.input.getTextInput(
                    new UsernamePrompt(),
                    "Please tell us your name, Wizard!",
                    "",
                    "Username"
            );
        }
    }

    private void connectToGame(CreateGameResponse response){
        try {
            // Client tries to connect to the given GameServer
            Log.info("Creating connection to assigned GameServer...");
            Client client = createAndConnectClient(
                    TIMEOUT,
                    MASTER_SERVER_HOST,
                    response.getTcpPort(),
                    response.getUdpPort()
            );
            Log.info("Done!\n");
            // Add listeners to the connection
            Log.info("Adding listeners...");
            addGameServerListener(client);
            Log.info("Done!\n");
            // Player asks to join gameserver
            Log.info("Sending PlayerJoinedRequest to GameServer...");
            PlayerJoinedRequest request = new PlayerJoinedRequest();
            request.setPlayerID(playerId);
            client.sendTCP(request);
            Log.info("Done!\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Client createAndConnectClient(Integer timeout, String host, Integer tcp, Integer udp) throws IOException {
        Client client = new Client();
        client.start();
        client.connect(timeout, host, tcp, udp);
        KryoClientRegister.registerKryoClasses(client);  // Register classes for kryo serializer
        return client;
    }

    private void closeClientConnection(Client client){
        client.close();
        client.stop();
    }


    // LISTENERS

    private void addMasterServerListener(){
        masterServerClient.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof CreateGameResponse) {
                    Log.info("Received PlayerJoinedResponse");
                    CreateGameResponse response = (CreateGameResponse) object;
                    Log.info("Requesting Game Creation...");
                    connectToGame(response);
                    Log.info("Done!\n");
                    // Creating game server, the closing the master server connection
                    Log.info("Closing connection to MasterServer...");
                    closeClientConnection(masterServerClient);
                    Log.info("Done!\n");
                }
                else if (object instanceof CreatePlayerResponse){
                    Log.info("Received CreatePlayerResponse");
                    CreatePlayerResponse response = (CreatePlayerResponse) object;
                    playerId = response.getPlayerId();
                    userPreferences.putInteger("playerId", playerId);
                    userPreferences.flush();
                }
                else if (object instanceof ServerErrorResponse) {
                    // If there is a server error
                    ServerErrorResponse response = (ServerErrorResponse) object;
                    Log.info("Client got this error message: " + response.getErrorMsg());
                    // TODO: Handle server error on client side. Give error message to interface
                }
            }
        });
        Log.info("Done!\n");
    }

    private void addGameServerListener(Client client){
        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof ServerSuccessResponse) {
                    // TODO: Wait for game to start.
                    Log.info("Game created and connected! Waiting for players...");
                }
                else if (object instanceof GameStartRequest){
                    // TODO: Start game
                }
                // TODO: Add listeners for game status.
            }
        });
    }

    // =====

    // GAME LOGIC

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

    // =====
}
