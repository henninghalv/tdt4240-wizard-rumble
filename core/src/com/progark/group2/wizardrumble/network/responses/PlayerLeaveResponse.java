package com.progark.group2.wizardrumble.network.responses;

public class PlayerLeaveResponse extends Response {
    private int playerId;
    private int playerSlotId;

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
}
