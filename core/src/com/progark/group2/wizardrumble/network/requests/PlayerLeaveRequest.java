package com.progark.group2.wizardrumble.network.requests;

public class PlayerLeaveRequest {
    private int gameId;
    private int playerId;
    private int playerSlotId;

    public PlayerLeaveRequest() {
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

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
