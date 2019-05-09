package com.progark.group2.wizardrumble.network.packets;

public class PlayersOnlinePacket {
    private int playersOnlineCount;

    public PlayersOnlinePacket() {
    }

    public int getPlayersOnlineCount() {
        return playersOnlineCount;
    }

    public void setPlayersOnlineCount(int playersOnlineCount) {
        this.playersOnlineCount = playersOnlineCount;
    }
}
