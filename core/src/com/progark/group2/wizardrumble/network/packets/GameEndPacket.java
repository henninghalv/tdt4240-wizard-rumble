package com.progark.group2.wizardrumble.network.packets;

import com.progark.group2.wizardrumble.network.resources.Player;

import java.util.HashMap;

public class GameEndPacket {

    private HashMap<Integer, Player> players;
    private int gameId;

    public GameEndPacket() {
    }

    public HashMap<Integer, Player> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<Integer, Player> players) {
        this.players = players;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
