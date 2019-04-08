package com.progark.group2.wizardrumble.network.requests;

public class DamagePlayerPacket {
    private int playerId;
    private int damage;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}
