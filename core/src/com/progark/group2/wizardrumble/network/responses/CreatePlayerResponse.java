package com.progark.group2.wizardrumble.network.responses;

public class CreatePlayerResponse extends Response {
    private int playerId;
    private String username;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
