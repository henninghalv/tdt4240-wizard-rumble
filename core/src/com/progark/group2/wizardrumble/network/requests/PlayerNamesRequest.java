package com.progark.group2.wizardrumble.network;

import java.util.List;

public class PlayerNamesRequest {
    private List<String> playersInLobby;

    public List<String> getPlayersInLobby() {
        return playersInLobby;
    }

    public void setPlayersInLobby(List<String> playersInLobby) {
        this.playersInLobby = playersInLobby;
    }
}
