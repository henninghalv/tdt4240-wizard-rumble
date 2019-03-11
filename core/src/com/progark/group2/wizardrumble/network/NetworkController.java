package com.progark.group2.wizardrumble.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NetworkController {


    public void init() throws IOException {

        // Example connection from client side
        // Sending simple CreateGameRequest to server via TCP
        // The server is in the gameserver module

        Client client = new Client();
        client.start();
        client.connect(5000, "localhost", 54555, 54777);

        Kryo kryo = client.getKryo();
        kryo.register(CreateGameRequest.class);
        kryo.register(CreateGameResponse.class);

        CreateGameRequest request = new CreateGameRequest();
        request.playerID = 0; // TODO: ID is generated through name registering
        client.sendTCP(request);

        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof CreateGameResponse) {
                    CreateGameResponse response = (CreateGameResponse)object;
                    for (String s : response.map.keySet()) {
                        System.out.println(s + ": " + response.map.get(s));
                    }
                }
            }
        });


    }

    public Map getStats(){
        //TODO
        return new HashMap();
    }

    public Map updateStats(Map map){
        //TODO
        return new HashMap();
    }

    public Map updateGameState(Map map){
        //TODO
        return new HashMap();
    }

    public void handleUpdate(){

    }


}
