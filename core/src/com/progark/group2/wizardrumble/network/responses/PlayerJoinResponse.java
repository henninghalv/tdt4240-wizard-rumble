package com.progark.group2.wizardrumble.network.responses;

public class PlayerJoinResponse extends Response {
    private int playerId;
    private String playerName;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
