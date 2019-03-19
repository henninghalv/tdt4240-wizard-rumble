package com.progark.group2.gameserver;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.progark.group2.wizardrumble.network.CreateGameRequest;
import com.progark.group2.wizardrumble.network.CreateGameResponse;
import com.progark.group2.wizardrumble.network.PlayerDeadRequest;
import com.progark.group2.wizardrumble.network.PlayerJoinedRequest;
import com.progark.group2.wizardrumble.network.PlayerNameRequest;
import com.progark.group2.wizardrumble.network.PlayerNameResponse;
import com.progark.group2.wizardrumble.network.PlayerStatisticsResponse;
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
