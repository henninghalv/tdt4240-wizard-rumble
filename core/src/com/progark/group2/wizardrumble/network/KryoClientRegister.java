package com.progark.group2.wizardrumble.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import java.util.HashMap;

public class KryoClientRegister {

    /**
     * Registers all classes for kryo serializer
     * @param client    kryo client object
     */
    static void registerKryoClasses(Client client) {
        Kryo kryo = client.getKryo();
        kryo.register(PlayerJoinedRequest.class);
        kryo.register(PlayerDeadRequest.class);
        kryo.register(PlayerStatisticsResponse.class);
        kryo.register(ServerErrorResponse.class);
        kryo.register(CreateGameRequest.class);
        kryo.register(CreateGameResponse.class);
        kryo.register(PlayerNameRequest.class);
        kryo.register(PlayerNameResponse.class);
        kryo.register(HashMap.class);
    }
}
