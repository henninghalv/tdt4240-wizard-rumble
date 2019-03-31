package com.progark.group2.wizardrumble.network.responses;

public class PlayerLeaveResponse extends Response {
    private int playerId;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

}
