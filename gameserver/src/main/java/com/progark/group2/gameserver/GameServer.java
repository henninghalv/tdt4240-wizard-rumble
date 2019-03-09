package com.progark.group2.gameserver;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.progark.group2.wizardrumble.network.ExampleRequest;
import com.progark.group2.wizardrumble.network.ExampleResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public class GameServer {

    // List of all server instances. 1 server = 1 game
    private List<Server> servers = new ArrayList<Server>();
    private int initServerCount = 1;

    // This is the master server
    public GameServer() throws IOException {

        // TODO: Consider initializing some server beforehand. = KEEP
        // TODO: Or only when requested. == REMOVE (and initServerCount)
        // Its used under debbugging
        for (int i = 0; i < initServerCount; i++) {
            addServer(createNewServer(-1, -1));
        }

    }

    /**
     * Add server to the list of all servers
     * @param server    Kryo Server object
     */
    public void addServer(Server server) {
        this.servers.add(server);
    }

    /**
     * Creates a new Kryo Server instance and registers request and
     * response classes.
     * @return      Kryo Server object
     */
    public Server createNewServer(int tcpPort, int udpPort) throws IOException {
        Server server = new Server();
        server.start();

        // TODO: Give a random port or fix duplicate/wrong input
        // TODO: Handle param exeptions
        if (tcpPort >= 65535 || tcpPort <= 0) tcpPort = 54555;
        if (udpPort >= 65535 || udpPort <= 0) udpPort = 54777;

        server.bind(tcpPort, udpPort);

        // Register response and request classes
        Kryo kryo = server.getKryo();
        kryo.register(ExampleRequest.class);
        kryo.register(ExampleResponse.class);

        // Add a receiver listener to server
        server.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof ExampleRequest) {
                    ExampleRequest request = (ExampleRequest)object;
                    System.out.println(request.text);

                    ExampleResponse response = new ExampleResponse();
                    response.text = "Thanks";
                    connection.sendTCP(response);
                }
            }
        });

        return server;
    }

    // TODO this is the method for responding to clientRequest when creating lobby
    public ExampleResponse clientResponse(ExampleRequest request) {

        // Conditional for the request. Create a response accordingly.


        // If the server shall give the client a new server:
        HashMap<String, String> map = new HashMap<String, String>();

        // Instanciate a server here that
        // runs on 127.0.0.1 and ports: 54555 and 54777

        // Example if the response is containing a hashmap
        map.put("host", "127.0.0.1");
        map.put("tcpPort", "54555");
        map.put("udpPort", "54777");

        // GOAL: Give the client host and ports for communicating with the
        // Server instance instead of the masterserver when playing a game

        // Instansiate a response and add the hashmap to it.

        // Return the response/Send the response back to the client.
        return new ExampleResponse();

    }

    public static void main(String[] args) throws IOException {

        // Example server startup
        // Gets ExampleRequest from NetworkController in core
        // Responds with ExampleResponse

        // Examples of multiple servers
        GameServer masterServer = new GameServer();
        // After request from client - exampleRequest
        masterServer.clientResponse(new ExampleRequest());

    }
}
