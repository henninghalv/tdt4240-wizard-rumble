package com.progark.group2.wizardrumble.network.packets;

public class GameStartPacket{
    private long gameStartTime;
    private int gameId;

    public GameStartPacket() {
    }

    public long getGameStartTime() { return gameStartTime; }

    public void setGameStartTime(long gameStartTime) { this.gameStartTime = gameStartTime; }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
