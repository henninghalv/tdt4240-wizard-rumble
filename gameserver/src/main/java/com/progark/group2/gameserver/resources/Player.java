package com.progark.group2.gameserver.resources;

public class Player {

    private String name;
    private int connectionId;
    private int kills;
    private int rank;
    private int timeAliveInMilliseconds;
    private boolean isAlive;

    public Player(String name,  int connectionId, int kills, int rank, int timeAliveInMilliseconds, boolean isAlive) {
        this.name = name;
        this.connectionId = connectionId;
        this.kills = kills;
        this.rank = rank;
        this.timeAliveInMilliseconds = timeAliveInMilliseconds;
        this.isAlive = isAlive;

    }

    public String getName() {
        return name;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public int getKills() {
        return kills;
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

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setTimeAliveInMilliseconds(int timeAliveInMilliseconds) {
        this.timeAliveInMilliseconds = timeAliveInMilliseconds;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

}
