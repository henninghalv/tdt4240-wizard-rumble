package com.progark.group2.wizardrumble.network.responses;

import com.badlogic.gdx.math.Vector2;

public class GameJoinedResponse extends Response {

    private Vector2 spawnPoint;
    private int playerSlotId;

    public Vector2 getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Vector2 spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public int getPlayerSlotId() {
        return playerSlotId;
    }

    public void setPlayerSlotId(int playerSlotId) {
        this.playerSlotId = playerSlotId;
    }
}
