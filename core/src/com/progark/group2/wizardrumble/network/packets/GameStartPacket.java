package com.progark.group2.wizardrumble.network.packets;

public class GameStartPacket extends Packet {
    private long gameStartTime;
    public GameStartPacket() {
    }

    public long getGameStartTime() { return gameStartTime; }

    public void setGameStartTime(long gameStartTime) { this.gameStartTime = gameStartTime; }
}
