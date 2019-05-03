package com.progark.group2.gameserver;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import com.progark.group2.gameserver.resources.GameStatus;
import com.progark.group2.gameserver.resources.PlayerSlotStatus;
import com.progark.group2.wizardrumble.network.packets.GameEndPacket;
import com.progark.group2.wizardrumble.network.packets.GameStartPacket;
import com.progark.group2.wizardrumble.network.packets.PlayerDeadPacket;
import com.progark.group2.wizardrumble.network.packets.PlayerMovementPacket;
import com.progark.group2.wizardrumble.network.packets.SpellFiredPacket;
import com.progark.group2.wizardrumble.network.resources.Player;
import com.progark.group2.wizardrumble.network.responses.GameJoinedResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerJoinResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerLeaveResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    private int gameId;
    private GameStatus gameStatus;
    private ArrayList<Connection> playerConnections = new ArrayList<Connection>();
    private HashMap<Integer, Player> players = new HashMap<Integer, Player>();
    private HashMap<Integer, Vector2> spawnPoints = new HashMap<Integer, Vector2>();
    private HashMap<Integer, PlayerSlotStatus> playerSlots = new HashMap<Integer, PlayerSlotStatus>();
    private long gameStartTime = 0;

    public Game(int gameId) {
        this.gameId = gameId;
        this.gameStatus = GameStatus.STAND_BY;
        createPlayerSlots();
        createSpawnPoints();
    }

    private void createSpawnPoints(){
        spawnPoints.put(1, new Vector2(500, 500));
        spawnPoints.put(2, new Vector2(500, 750));
        spawnPoints.put(3, new Vector2(500, 1000));
        spawnPoints.put(4, new Vector2(1000, 1000));
        spawnPoints.put(5, new Vector2(1000, 750));
        spawnPoints.put(6, new Vector2(1000, 500));
    }

    private void createPlayerSlots(){
        playerSlots.put(1, PlayerSlotStatus.OPEN);
        playerSlots.put(2, PlayerSlotStatus.OPEN);
        playerSlots.put(3, PlayerSlotStatus.OPEN);
        playerSlots.put(4, PlayerSlotStatus.OPEN);
        playerSlots.put(5, PlayerSlotStatus.OPEN);
        playerSlots.put(6, PlayerSlotStatus.OPEN);
    }

    public void addPlayer(String playerName, int playerId, Connection connection){
        Log.info("Adding player to game: id:" + playerId + " , name:" + playerName);
        int playerSlot = getAvailablePlayerSlot();
        Player player = new Player(
                playerName, playerSlot, 0, 0, 0, spawnPoints.get(playerSlot), 0
        );

        PlayerJoinResponse response = new PlayerJoinResponse();
        response.setPlayerId(playerId);
        response.setPlayerName(playerName);
        response.setPlayerSlotId(player.getPlayerSlotId());
        response.setSpawnPoint(player.getPosition());

        for(Connection c: playerConnections){
            if(c.getID() != connection.getID()){
                c.sendTCP(response);
            }
        }

        for(Integer pid : players.keySet()){
            response = new PlayerJoinResponse();
            response.setPlayerId(pid);
            response.setPlayerName(players.get(pid).getName());
            response.setPlayerSlotId(players.get(pid).getPlayerSlotId());
            response.setSpawnPoint(players.get(pid).getPosition());
            connection.sendTCP(response);
        }

        playerConnections.add(connection);
        players.put(playerId, player);

        GameJoinedResponse response1 = new GameJoinedResponse();
        response1.setPlayerSlotId(player.getPlayerSlotId());
        response1.setSpawnPoint(player.getPosition());
        connection.sendTCP(response1);

        if(playerConnections.size() >= GameServer.getMaximumPlayers()){
            Log.info("Game is full!");
            gameStatus = GameStatus.FULL;
        }
    }

    public void removePlayer(int playerId, Connection connection){
        PlayerLeaveResponse response = new PlayerLeaveResponse();
        response.setPlayerSlotId(players.get(playerId).getPlayerSlotId());
        response.setPlayerId(playerId);
        for(Connection c : playerConnections){
            if(c.getID() != connection.getID()){
                c.sendTCP(response);
            }
        }

        playerSlots.put(players.get(playerId).getPlayerSlotId(), PlayerSlotStatus.OPEN);
        playerConnections.remove(connection);
        players.remove(playerId);

        if(gameStatus.equals(GameStatus.FULL)){
            gameStatus = GameStatus.STAND_BY;
        }
    }

    public void updatePlayerPosition(Connection connection, PlayerMovementPacket packet){
        players.get(packet.getPlayerId()).setPosition(packet.getPosition());
        players.get(packet.getPlayerId()).setRotation(packet.getRotation());
        for(Connection c : playerConnections){
            if(c.getID() != connection.getID()){
                c.sendUDP(packet);
            }
        }
    }

    public void spellFired(Connection connection, SpellFiredPacket packet){
        for(Connection c : playerConnections){
            if(c.getID() != connection.getID()){
                c.sendTCP(packet);
            }
        }
    }

    public void playerDied(Connection connection, PlayerDeadPacket packet){
        if(packet.getKillerId() != 0){
            players.get(packet.getKillerId()).incrementKills(); // Increase kills for killer
        }
        players.get(packet.getVictimId()).setAlive(false); // Set victim player as dead
        players.get(packet.getVictimId()).setTimeAliveInMilliseconds(packet.getPlayerDeathTime() - gameStartTime); // Set time of death

        for(Connection c: playerConnections){
            if(c.getID() != connection.getID()){
                c.sendTCP(packet);
            }
        }

        // Check if game has ended
        if(isGameOver()){
            for(Player player : players.values()){
                if(player.isAlive()){
                    player.setTimeAliveInMilliseconds(packet.getPlayerDeathTime() - gameStartTime); // Same time as last death
                }
            }
            end();
        }
    }

    public void start(){
        gameStartTime = System.currentTimeMillis();
        GameStartPacket packet = new GameStartPacket();
        packet.setGameStartTime(gameStartTime);
        for(Connection c : playerConnections){
            c.sendTCP(packet);
        }
        gameStatus = GameStatus.IN_PROGRESS;
    }

    private boolean isGameOver(){
        int playersAlive = 0;
        for (Player p: players.values()) {
            if (p.isAlive()) playersAlive++;
        }

        if (playersAlive <= 1) {
            return true;
        }

        return false;
    }

    private void end(){
        GameEndPacket packet = new GameEndPacket();
        packet.setPlayers(players);
        for(Connection c : playerConnections){
            c.sendTCP(packet);
        }
        try {
            GameServer.getInstance().removeGame(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getAvailablePlayerSlot() {
        for(int slot : playerSlots.keySet()){
            if(playerSlots.get(slot).equals(PlayerSlotStatus.OPEN)){
                playerSlots.put(slot, PlayerSlotStatus.TAKEN);
                return slot;
            }
        }
        return 0;
    }

    public int getGameId() {
        return gameId;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

}
