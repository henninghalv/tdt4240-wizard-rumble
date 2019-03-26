package com.progark.group2.wizardrumble.network.responses;

import java.util.HashMap;

public class PlayerStatisticsResponse extends Response {

    private int playerID;
    private HashMap<String, Object> map;

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public HashMap<String, Object> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Object> map) {
        this.map = map;
    }

    // TODO: Add metadata such ass score and placement for scoreboard
    // A list of players sorted after when they died
    // Amount of kills
}
