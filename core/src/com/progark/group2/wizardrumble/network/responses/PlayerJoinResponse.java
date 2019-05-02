package com.progark.group2.wizardrumble.network.responses;

import com.badlogic.gdx.math.Vector2;

public class PlayerJoinResponse extends Response {
    private int playerId;
    private int playerSlotId;
    private Vector2 spawnPoint;
    private String playerName;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerSlotId() {
        return playerSlotId;
    }

    public void setPlayerSlotId(int playerSlotId) {
        this.playerSlotId = playerSlotId;
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
