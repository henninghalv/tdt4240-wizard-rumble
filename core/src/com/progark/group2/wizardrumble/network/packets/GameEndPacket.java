package com.progark.group2.wizardrumble.network.packets;

import com.progark.group2.wizardrumble.network.resources.Player;

import java.util.HashMap;

public class GameEndPacket {

    private HashMap<Integer, Player> players;

    public GameEndPacket() {
    }

    public HashMap<Integer, Player> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<Integer, Player> players) {
        this.players = players;
    }
}
