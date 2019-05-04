package com.progark.group2.wizardrumble.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.progark.group2.wizardrumble.entities.spells.FireBall;
import com.progark.group2.wizardrumble.entities.spells.Ice;
import com.progark.group2.wizardrumble.entities.spells.Spell;
import com.progark.group2.wizardrumble.entities.spells.SpellType;
import com.progark.group2.wizardrumble.network.packets.PlayerDeadPacket;
import com.progark.group2.wizardrumble.network.packets.GameEndPacket;
import com.progark.group2.wizardrumble.network.packets.PlayerMovementPacket;
import com.progark.group2.wizardrumble.network.packets.PlayersOnlinePacket;
import com.progark.group2.wizardrumble.network.packets.SpellFiredPacket;
import com.progark.group2.wizardrumble.network.requests.CreateGameRequest;
import com.progark.group2.wizardrumble.network.requests.CreatePlayerRequest;

import com.progark.group2.wizardrumble.network.packets.GameStartPacket;
import com.progark.group2.wizardrumble.network.requests.PlayerJoinRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerLeaveRequest;
import com.progark.group2.wizardrumble.network.resources.Player;
import com.progark.group2.wizardrumble.network.responses.CreateGameResponse;
import com.progark.group2.wizardrumble.network.responses.CreatePlayerResponse;
import com.progark.group2.wizardrumble.network.responses.GameJoinedResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerJoinResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerLeaveResponse;
import com.progark.group2.wizardrumble.network.responses.ServerErrorResponse;
import com.progark.group2.wizardrumble.states.GameStateManager;

import com.progark.group2.wizardrumble.states.PostGameState;
import com.progark.group2.wizardrumble.states.ingamestate.InGameState;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

public class NetworkController extends Listener{

    private static NetworkController instance;
    private static Client client;
    private static Preferences userPreferences = Gdx.app.getPreferences("user");
    private static InGameState gameState;

    private static int playerId = 0;
    private static int gameId = 0;
    private static Player player = null;
    private static HashMap<Integer, Player> players = new HashMap<Integer, Player>();
    private static int playersOnlineCount;

    private long gameStartTime;

    // Master server configuration constants
    private final static int TIMEOUT = 5000;
    private final static String SERVER_HOST = "178.128.243.226";  // Set this to the local IP address of your computer when running the server
    private final static int SERVER_TCP_PORT = 54555;
    private final static int SERVER_UDP_PORT = 54777;

    private NetworkController() throws IOException {
        // Client for handling communication with master server
        Log.info("Creating connection to MasterServer...");
        client = createAndConnectClient(
                TIMEOUT,
                SERVER_HOST,
                SERVER_TCP_PORT,
                SERVER_UDP_PORT
        );
        Log.info("Creating connection to MasterServer: Done!\n");
        Log.info("Getting local data...");
        getLocalData();
        Log.info("Getting local data: Done!\n");
        client.sendTCP(new PlayersOnlinePacket());
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
        client.sendTCP(request);
    }

    /**
     * Sends a request to create a new game.
     */
    public void requestGameCreation() {
        Log.info("Sending CreateGameRequest to MasterServer...\n");
        CreateGameRequest request = new CreateGameRequest();
        request.setPlayerId(playerId);
        client.sendTCP(request);
    }

    public void requestGameStart(){
        Log.info("Sending GameStartPacket to GameServer...\n");
        GameStartPacket request = new GameStartPacket();
        request.setGameId(gameId);
        client.sendTCP(request);
    }

    // =====

    // LISTENERS

    @Override
    public void disconnected(Connection connection){
        connection.close();
        System.exit(0);
    }

    @Override
    public void received(Connection connection, Object object){
        if (object instanceof CreatePlayerResponse){
            Log.info("Received CreatePlayerResponse");
            CreatePlayerResponse response = (CreatePlayerResponse) object;
            handleCreatePlayerResponse(response);
        } else if (object instanceof CreateGameResponse) {
            Log.info("Received CreateGameResponse");
            CreateGameResponse response = (CreateGameResponse) object;
            handleCreateGameResponse(response);
        } else if (object instanceof PlayerJoinResponse){
            Log.info("Received PlayerJoinResponse");
            PlayerJoinResponse response = (PlayerJoinResponse) object;
            handlePlayerJoinResponse(response);
        } else if (object instanceof GameStartPacket){
            GameStartPacket packet = (GameStartPacket) object;
            gameStartTime = packet.getGameStartTime();
            handleGameStart();
        } else if (object instanceof GameJoinedResponse){
            GameJoinedResponse response = (GameJoinedResponse) object;
            NetworkController.player = createPlayer(userPreferences.getString("username"), response.getPlayerSlotId(), response.getSpawnPoint());
        } else if (object instanceof PlayerMovementPacket){
            PlayerMovementPacket packet = (PlayerMovementPacket) object;
            handlePlayerMovementResponse(packet);
        } else if (object instanceof SpellFiredPacket){
            SpellFiredPacket packet = (SpellFiredPacket) object;
            handleSpellCastPacket(packet);
        } else if (object instanceof PlayerLeaveResponse){
            PlayerLeaveResponse response = (PlayerLeaveResponse) object;
            handlePlayerLeaveResponse(response);
        } else if (object instanceof PlayerDeadPacket){
            PlayerDeadPacket packet = (PlayerDeadPacket) object;
            handlePlayerDeath(packet);
        } else if (object instanceof GameEndPacket) {
            GameEndPacket packet = (GameEndPacket) object;
            if(!players.isEmpty()){
                handleGameEnd(packet);
            }
            if(client.isConnected()){
                packet.setGameId(gameId);
                client.sendTCP(packet);
            }
        } else if (object instanceof ServerErrorResponse) {
            ServerErrorResponse response = (ServerErrorResponse) object;
            Log.error("Client got this error message: " + response.getErrorMsg());
        } else if (object instanceof PlayersOnlinePacket){
            PlayersOnlinePacket packet = (PlayersOnlinePacket) object;
            playersOnlineCount = packet.getPlayersOnlineCount();
            Log.info("Players online: " + playersOnlineCount);
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
        gameId = response.getGameId();
        connectToGame();
        Log.info("Game created and connected! Waiting for players...\n");
    }

    private void handlePlayerJoinResponse(PlayerJoinResponse response) {
        Log.info("Player joined: " + response.getPlayerName());
        Player player = createPlayer(response.getPlayerName(), response.getPlayerSlotId(), response.getSpawnPoint());
        players.put(response.getPlayerId(), player);
    }

    private void handlePlayerLeaveResponse(PlayerLeaveResponse response){
        Log.info("Player left: " + players.get(response.getPlayerId()).getName());
        if(gameStartTime > 0){
            players.get(response.getPlayerId()).setAlive(false);
            players.get(response.getPlayerId()).setTimeAliveInMilliseconds((System.currentTimeMillis() - gameStartTime)/1000);
        } else {
            players.remove(response.getPlayerId());
        }
    }

    private void handleGameStart() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    gameState = new InGameState(GameStateManager.getInstance());
                    GameStateManager.getInstance().set(gameState);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            });
    }

    private void handlePlayerMovementResponse(PlayerMovementPacket response){
        updateEnemyPosition(response.getPlayerId(), response.getPosition(), response.getRotation());
    }

    private void handleSpellCastPacket(final SpellFiredPacket packet){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(packet.getSpellType().equals(SpellType.FIREBALL)){
                    updateEnemyCastSpells(
                            new FireBall(packet.getSpellOwnerId(), packet.getSpawnPoint(), packet.getRotation(), packet.getVelocity())
                    );
                } else if (packet.getSpellType().equals(SpellType.ICE)){
                    updateEnemyCastSpells(
                            new Ice(packet.getSpellOwnerId(), packet.getSpawnPoint(), packet.getRotation(), packet.getVelocity())
                    );
                } else {
                    updateEnemyCastSpells(
                            new FireBall(packet.getSpellOwnerId(), packet.getSpawnPoint(), packet.getRotation(), packet.getVelocity())
                    );
                }
            }
        });

    }

    private void handlePlayerDeath(final PlayerDeadPacket packet){
        players.get(packet.getVictimId()).setTimeAliveInMilliseconds(packet.getPlayerDeathTime() - gameStartTime);
        players.get(packet.getVictimId()).setAlive(false);
        if(packet.getKillerId() == playerId){
            player.incrementKills();
        }
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                gameState.handleEnemyDead(players.get(packet.getVictimId()).getPlayerSlotId());
            }
        });
    }

    private void handleGameEnd(final GameEndPacket packet){
        for(Integer id: packet.getPlayers().keySet()){
            if(id != playerId){
                players.put(id, packet.getPlayers().get(id));
            } else{
                player.setTimeAliveInMilliseconds(packet.getPlayers().get(id).getTimeAliveInMilliseconds());
            }
        }

        gameStartTime = 0;

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run(){
                try {
                    GameStateManager.getInstance().set(new PostGameState(GameStateManager.getInstance()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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

    private Player createPlayer(String name, int playerSlotId, Vector2 spawnpoint){
        Player player = new Player(name, playerSlotId, 0, 0, 0, spawnpoint, 0);
        return player;
    }

    private void connectToGame(){
        PlayerJoinRequest request = new PlayerJoinRequest();
        request.setPlayerId(playerId);
        request.setGameId(gameId);
        client.sendTCP(request);
    }

    private Client createAndConnectClient(Integer timeout, String host, Integer tcp, Integer udp) throws IOException {
        Client client = new Client();
        client.start();
        client.connect(timeout, InetAddress.getByName(host), tcp, udp);
        KryoClientRegister.registerKryoClasses(client);  // Register classes for kryo serializer
        client.addListener(this);
        return client;
    }

    // GAME LOGIC

    public void updatePlayerPosition(Vector2 position, float rotation){
        PlayerMovementPacket packet = new PlayerMovementPacket();
        packet.setPlayerId(playerId);
        packet.setGameId(gameId);
        packet.setPosition(position);
        packet.setRotation(rotation);
        client.sendUDP(packet);
    }

    private void updateEnemyPosition(int playerId, Vector2 position, float rotation) {
        players.get(playerId).setPosition(position);
        players.get(playerId).setRotation(rotation);
    }

    public void castSpell(Spell spell){
        SpellFiredPacket packet = new SpellFiredPacket();
        packet.setGameId(gameId);
        packet.setSpellType(spell.getSpellType());
        packet.setSpawnPoint(spell.getPosition());
        packet.setRotation(spell.getRotation());
        packet.setVelocity(spell.getVelocity());
        packet.setSpellOwnerId(spell.getSpellOwnerID());
        client.sendTCP(packet);
    }

    private void updateEnemyCastSpells(Spell spell){
        gameState.addSpell(spell);
    }

    public void playerKilledBy(int killerId){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                gameState.handlePlayerDead();
            }
        });
        PlayerDeadPacket packet = new PlayerDeadPacket();
        packet.setGameId(gameId);
        packet.setVictimId(playerId);
        packet.setKillerId(killerId);
        packet.setPlayerDeathTime(System.currentTimeMillis());
        client.sendTCP(packet);
        player.setTimeAliveInMilliseconds(System.currentTimeMillis() - gameStartTime);
        player.setAlive(false);
    }

    public void playerLeftGame(){
        players.clear();
        PlayerLeaveRequest request = new PlayerLeaveRequest();
        request.setGameId(gameId);
        request.setPlayerId(playerId);
        request.setPlayerSlotId(player.getPlayerSlotId());
        client.sendTCP(request);
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

    public InGameState getGameState(){
        return gameState;
    }

    public int getPlayersOnlineCount() {
        return playersOnlineCount;
    }

    // =====
}
