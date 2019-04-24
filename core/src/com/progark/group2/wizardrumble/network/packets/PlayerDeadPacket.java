package com.progark.group2.wizardrumble.network.packets;

public class PlayerDeadPacket {
    private int playerId;
    private long playerDeathTime;

    public PlayerDeadPacket() {
    }

    public int getPlayerId() { return playerId; }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public long getPlayerDeathTime() { return playerDeathTime; }

    public void setPlayerDeathTime(long playerDeathTime) { this.playerDeathTime = playerDeathTime; }

}
