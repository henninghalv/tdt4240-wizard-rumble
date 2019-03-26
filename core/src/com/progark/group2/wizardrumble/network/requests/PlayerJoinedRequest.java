package com.progark.group2.wizardrumble.network.requests;

public class PlayerJoinedRequest extends Request {
    private int playerID;

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

}
