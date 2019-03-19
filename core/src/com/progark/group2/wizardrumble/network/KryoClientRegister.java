package com.progark.group2.wizardrumble.network;

import com.badlogic.gdx.math.Vector2;
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
        kryo.register(PlayerStatisticsResponse.class);
        kryo.register(ServerErrorResponse.class);
        kryo.register(CreateGameRequest.class);
        kryo.register(CreateGameResponse.class);
        kryo.register(PlayerNameRequest.class);
        kryo.register(PlayerNameResponse.class);
        kryo.register(PlayerMovementRequest.class);
        kryo.register(PlayersHealthStatusRequest.class);
        kryo.register(HashMap.class);
        kryo.register(Vector2.class);
    }
}
