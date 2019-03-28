package com.progark.group2.wizardrumble.network.requests;

import java.util.HashMap;

public class PlayerNamesRequest extends Request {
    private HashMap<Integer, String> playersInLobby;

    public HashMap<Integer, String> getPlayersInLobby() {
        return playersInLobby;
    }

    public void setPlayersInLobby(HashMap<Integer, String>playersInLobby) {
        this.playersInLobby = playersInLobby;
    }
}
