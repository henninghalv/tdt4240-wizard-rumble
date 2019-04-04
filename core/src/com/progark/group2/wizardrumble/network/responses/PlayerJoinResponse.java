package com.progark.group2.wizardrumble.network.responses;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class PlayerJoinResponse extends Response {
    private int playerId;
    private int connectionId;
    private Vector2 spawnPoint;
    private String playerName;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public Vector2 getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Vector2 spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
