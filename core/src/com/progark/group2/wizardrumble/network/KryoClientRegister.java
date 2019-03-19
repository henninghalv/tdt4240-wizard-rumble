package com.progark.group2.wizardrumble.network;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import java.util.HashMap;
import java.util.List;

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
        kryo.register(PlayerJoinRequest.class);
        kryo.register(CreateGameRequest.class);
        kryo.register(PlayerNameRequest.class);
        kryo.register(PlayerNameResponse.class);
        kryo.register(PlayerMovementRequest.class);
        kryo.register(PlayersHealthStatusRequest.class);
        kryo.register(PlayerDeadRequest.class);
        kryo.register(HashMap.class);
        kryo.register(List.class);
        kryo.register(Vector2.class);
    }
}
