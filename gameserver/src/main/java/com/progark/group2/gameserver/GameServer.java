package com.progark.group2.gameserver;

import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class GameServer {

    public static void main(String[] args) throws IOException {
        // Example server startup
        Server server = new Server();
        server.start();
        server.bind(54555, 54777);
    }

}
