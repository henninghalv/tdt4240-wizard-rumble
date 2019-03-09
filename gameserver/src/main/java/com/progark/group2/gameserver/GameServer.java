package com.progark.group2.gameserver;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.progark.group2.wizardrumble.network.ExampleRequest;
import com.progark.group2.wizardrumble.network.ExampleResponse;

import java.io.IOException;

public class GameServer {

    public static void main(String[] args) throws IOException {

        // Example server startup
        // Gets ExampleRequest from NetworkController in core
        // Responds with ExampleResponse

        Server server = new Server();
        server.start();
        server.bind(54555, 54777);

        Kryo kryo = server.getKryo();
        kryo.register(ExampleRequest.class);
        kryo.register(ExampleResponse.class);

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

    }

}
