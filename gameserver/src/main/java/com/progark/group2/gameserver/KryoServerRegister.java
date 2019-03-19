package com.progark.group2.gameserver;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.progark.group2.wizardrumble.network.PlayerJoinRequest;
import com.progark.group2.wizardrumble.network.CreateGameRequest;
import com.progark.group2.wizardrumble.network.PlayerDeadRequest;
import com.progark.group2.wizardrumble.network.PlayerJoinedRequest;
import com.progark.group2.wizardrumble.network.PlayerMovementRequest;
import com.progark.group2.wizardrumble.network.PlayerNameRequest;
import com.progark.group2.wizardrumble.network.PlayerNameResponse;
import com.progark.group2.wizardrumble.network.PlayerStatisticsResponse;
import com.progark.group2.wizardrumble.network.PlayersHealthStatusRequest;
import com.progark.group2.wizardrumble.network.ServerErrorResponse;


import java.util.HashMap;

public class KryoServerRegister {

    /**
     * Registers all classes for kryo serializer
     * @param server    kryo server object
     */
    static void registerKryoClasses(Server server) {
        Kryo kryo = server.getKryo();
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
        kryo.register(HashMap.class);
        kryo.register(Vector2.class);
    }
}
