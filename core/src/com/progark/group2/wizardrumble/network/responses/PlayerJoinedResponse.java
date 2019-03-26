package com.progark.group2.wizardrumble.network.responses;

public class PlayerJoinedResponse {
    private int tcpPort;
    private int udpPort;

    public int getTcpPort() {
        return tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }
}
