package com.progark.group2.wizardrumble.network.requests;

import java.util.List;

public class PlayerNamesRequest extends Request{
    private List<String> playersInLobby;

    public List<String> getPlayersInLobby() {
        return playersInLobby;
    }

    public void setPlayersInLobby(List<String> playersInLobby) {
        this.playersInLobby = playersInLobby;
    }
}
