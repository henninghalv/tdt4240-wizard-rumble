package com.progark.group2.wizardrumble.network.packets;

public class PlayerDeadPacket {
    private int playerId;

    public PlayerDeadPacket() {
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
