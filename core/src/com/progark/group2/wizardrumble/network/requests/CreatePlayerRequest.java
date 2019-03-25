package com.progark.group2.wizardrumble.network.requests;

public class CreatePlayerRequest extends Request{
    private String playerName;


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
