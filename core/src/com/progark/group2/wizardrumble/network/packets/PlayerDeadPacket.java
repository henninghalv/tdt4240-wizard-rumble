package com.progark.group2.wizardrumble.network.packets;

public class PlayerDeadPacket {
    private int victimId;
    private int killerId;
    private long playerDeathTime;

    public PlayerDeadPacket() {
    }

    public int getVictimId() {
        return victimId;
    }

    public int getKillerId() {
        return killerId;
    }

    public void setVictimId(int victimId) {
        this.victimId = victimId;
    }

    public void setKillerId(int killerId) {
        this.killerId = killerId;
    }

    public long getPlayerDeathTime() { return playerDeathTime; }

    public void setPlayerDeathTime(long playerDeathTime) { this.playerDeathTime = playerDeathTime; }

}
