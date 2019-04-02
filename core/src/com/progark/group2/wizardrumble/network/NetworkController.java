package com.progark.group2.wizardrumble.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.progark.group2.wizardrumble.network.requests.CreateGameRequest;
import com.progark.group2.wizardrumble.network.requests.CreatePlayerRequest;
import com.progark.group2.wizardrumble.network.packets.GameStartPacket;
import com.progark.group2.wizardrumble.network.requests.PlayerJoinRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerMovementRequest;
import com.progark.group2.wizardrumble.network.resources.Player;
import com.progark.group2.wizardrumble.network.responses.CreateGameResponse;
import com.progark.group2.wizardrumble.network.responses.CreatePlayerResponse;
import com.progark.group2.wizardrumble.network.responses.GameJoinedResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerJoinResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerLeaveResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerMovementResponse;
import com.progark.group2.wizardrumble.network.responses.ServerErrorResponse;
import com.progark.group2.wizardrumble.network.responses.ServerSuccessResponse;
import com.progark.group2.wizardrumble.states.GameStateManager;
import com.progark.group2.wizardrumble.states.InGameState;
import com.progark.group2.wizardrumble.states.LobbyState;

import java.io.IOException;
import java.util.HashMap;

import static com.progark.group2.wizardrumble.Application.HEIGHT;
import static com.progark.group2.wizardrumble.Application.WIDTH;

public class NetworkController extends Listener{

    private static NetworkController instance;
    private static Client masterServerClient;
    private static Client gameServerClient;
    private static Preferences userPreferences = Gdx.app.getPreferences("user");

    private static int playerId = 0;
    private static Player player = null;
    private static HashMap<Integer, Player> players = new HashMap<Integer, Player>();

    // Master server configuration constants
    private final static int TIMEOUT = 5000;
    private final static String MASTER_SERVER_HOST = "localhost";  // Set this to the local IP address of your computer when running the server
    private final static int MASTER_SERVER_TCP_PORT = 54555;
    private final static int MASTER_SERVER_UDP_PORT = 54777;

    private NetworkController() throws IOException{
        // Client for handling communication with master server
        Log.info("Creating connection to MasterServer...");
        masterServerClient = createAndConnectClient(
                TIMEOUT,
                MASTER_SERVER_HOST,
                MASTER_SERVER_TCP_PORT,
                MASTER_SERVER_UDP_PORT
        );
        Log.info("Creating connection to MasterServer: Done!\n");
        Log.info("Getting local data...");
        getLocalData();
        Log.info("Getting local data: Done!\n");
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
    public void requestGameCreation() {
        Log.info("Sending CreateGameRequest to MasterServer...\n");
        CreateGameRequest request = new CreateGameRequest();
        request.setPlayerId(playerId);
        masterServerClient.sendTCP(request);
    }

    private void requestJoinGame(){
        Log.info("Sending PlayerJoinRequest to GameServer...\n");
        PlayerJoinRequest request = new PlayerJoinRequest();
        request.setPlayerId(playerId);
        gameServerClient.sendTCP(request);
    }

    public void requestGameStart(){
        Log.info("Sending GameStartPacket to GameServer...\n");
        GameStartPacket request = new GameStartPacket();
        gameServerClient.sendTCP(request);
    }

    // =====

    // LISTENERS

    public void received(Connection connection, Object object){
        if (object instanceof CreatePlayerResponse){
            Log.info("Received CreatePlayerResponse");
            CreatePlayerResponse response = (CreatePlayerResponse) object;
            handleCreatePlayerResponse(response);
        }
        else if (object instanceof CreateGameResponse) {
            Log.info("Received CreateGameResponse");
            CreateGameResponse response = (CreateGameResponse) object;
            handleCreateGameResponse(response);
        }
        else if (object instanceof PlayerJoinResponse){
            Log.info("Received PlayerJoinResponse");
            PlayerJoinResponse response = (PlayerJoinResponse) object;
            handlePlayerJoinResponse(connection, response);

        }
        else if (object instanceof GameStartPacket){
            handleGameStart();
        }
        else if (object instanceof GameJoinedResponse){
            Player player = new Player(
                    userPreferences.getString("username"),
                    connection.getID(),
                    0,
                    0,
                    0,
                    new Vector2(WIDTH / 2f, HEIGHT / 2f + (32 * 4)),
                    0
            );  //TODO: Change the Vector2 to be starting position
            NetworkController.player = player;
        }
        else if (object instanceof PlayerMovementResponse){
            // TODO: Update enemy wizard position
            PlayerMovementResponse response = (PlayerMovementResponse) object;
            handlePlayerMovementResponse(response);
        }
        else if (object instanceof PlayerLeaveResponse){
            // TODO: Remove the player from players
            PlayerLeaveResponse response = (PlayerLeaveResponse) object;
            handlePlayerLeaveRequest(response);
        }
        else if (object instanceof ServerSuccessResponse) {
            // TODO: Delete this if not used. Smells bad.
        }
        else if (object instanceof ServerErrorResponse) {
            // If there is a server error
            ServerErrorResponse response = (ServerErrorResponse) object;
            Log.error("Client got this error message: " + response.getErrorMsg());
        }
    }

    // =====

    // RESPONSE HANDLING

    private void handleCreatePlayerResponse(CreatePlayerResponse response){
        Log.info("Saving player id...");
        playerId = response.getPlayerId();
        userPreferences.putInteger("playerId", playerId);
        userPreferences.flush();
        Log.info("Done!\n");
        Log.info("Welcome, " + response.getUsername());
        Log.info("You are now ready to play!\n");
    }

    private void handleCreateGameResponse(CreateGameResponse response){
        Log.info("Requesting Game Creation...");
        connectToGame(response);
        Log.info("Closing connection to MasterServer...");
        closeClientConnection(masterServerClient); // TODO: Do we need to do this??
        Log.info("Connection to MasterServer closed!\n");
        Log.info("Game created and connected! Waiting for players...\n");
    }

    private void handlePlayerJoinResponse(Connection connection, PlayerJoinResponse response) {
        Log.info("Player joined: " + response.getPlayerName());
        Player player = new Player(
                response.getPlayerName(),
                connection.getID(),
                0,
                0,
                0,
                new Vector2(WIDTH / 2f, HEIGHT / 2f + (32 * 4)),
                0
        );  //TODO: Change the Vector2 to be starting position
        players.put(response.getPlayerId(), player);
    }

    private void handlePlayerLeaveRequest(PlayerLeaveResponse response){
        Log.info("Player left: " + players.get(playerId).getName());
        players.remove(response.getPlayerId());
    }

    private void handleGameStart() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                InGameState state = null;
                try {
                    state = InGameState.getInstance();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                GameStateManager.getInstance().set(state);
            }
            });
    }

    private void handlePlayerMovementResponse(PlayerMovementResponse response){
        updateEnemyPosition(response.getPlayerId(), response.getPosition(), response.getRotation());
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
            gameServerClient = createAndConnectClient(
                    TIMEOUT,
                    MASTER_SERVER_HOST,
                    response.getTcpPort(),
                    response.getUdpPort()
            );
            Log.info("Connection to GameServer established!\n");
            // Add listeners to the connection
            Log.info("Requesting to join game...");
            requestJoinGame();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Client createAndConnectClient(Integer timeout, String host, Integer tcp, Integer udp) throws IOException {
        Client client = new Client();
        client.start();
        client.connect(timeout, host, tcp, udp);
        KryoClientRegister.registerKryoClasses(client);  // Register classes for kryo serializer
        client.addListener(this);
        return client;
    }

    private void closeClientConnection(Client client){
        client.close();
        client.stop();
    }

    // GAME LOGIC

    public void updatePlayerPosition(Vector2 position, float rotation){
        PlayerMovementRequest request = new PlayerMovementRequest();
        request.setPlayerId(playerId);
        request.setPosition(position);
        request.setRotation(rotation);
        gameServerClient.sendUDP(request);
    }

    private void updateEnemyPosition(int playerId, Vector2 position, float rotation) {
        players.get(playerId).setPosition(position);
        players.get(playerId).setRotation(rotation);
    }


    // =====

    // HELPER METHODS

    public HashMap<Integer, Player> getPlayers(){
        return players;
    }

    public int getPlayerId(){ return playerId; }

    public Player getPlayer() {
        return player;
    }

    // =====
}
