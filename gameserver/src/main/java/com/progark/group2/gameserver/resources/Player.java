package com.progark.group2.gameserver;

public class Player {

    private String name;
    private int kills;
    private int health;
    private int rank;
    private int timeAliveInMilliseconds;

    public Player(String name, int kills, int health, int rank, int timeAliveInMilliseconds) {
        this.name = name;
        this.kills = kills;
        this.health = health;
        this.rank = rank;
        this.timeAliveInMilliseconds = timeAliveInMilliseconds;
    }

    public String getName() {
        return name;
    }

    public int getKills() {
        return kills;
    }

    public int getHealth() {
        return health;
    }

    public int getRank() {
        return rank;
    }

    public int getTimeAliveInMilliseconds() {
        return timeAliveInMilliseconds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setTimeAliveInMilliseconds(int timeAliveInMilliseconds) {
        this.timeAliveInMilliseconds = timeAliveInMilliseconds;
    }
}
