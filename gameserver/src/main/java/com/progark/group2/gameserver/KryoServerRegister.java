package com.progark.group2.gameserver;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.progark.group2.wizardrumble.network.packets.GameStartPacket;
import com.progark.group2.wizardrumble.network.packets.SpellFiredPacket;
import com.progark.group2.wizardrumble.network.requests.CreateGameRequest;
import com.progark.group2.wizardrumble.network.requests.CreatePlayerRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerDeadRequest;

import com.progark.group2.wizardrumble.network.requests.PlayerTookDamageRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerJoinRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerLeaveRequest;
import com.progark.group2.wizardrumble.network.responses.CreateGameResponse;
import com.progark.group2.wizardrumble.network.responses.CreatePlayerResponse;
import com.progark.group2.wizardrumble.network.responses.GameJoinedResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerJoinResponse;
import com.progark.group2.wizardrumble.network.requests.PlayerMovementRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerNameRequest;
import com.progark.group2.wizardrumble.network.responses.PlayerLeaveResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerMovementResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerNameResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerStatisticsResponse;
import com.progark.group2.wizardrumble.network.requests.PlayersHealthStatusRequest;
import com.progark.group2.wizardrumble.network.responses.ServerErrorResponse;
import com.progark.group2.wizardrumble.network.responses.ServerSuccessResponse;


import java.util.ArrayList;
import java.util.HashMap;

public class KryoServerRegister {

    /**
     * Registers all classes for kryo serializer
     * @param server    kryo server object
     */
    public static void registerKryoClasses(Server server) {
        Kryo kryo = server.getKryo();
        kryo.register(PlayerDeadRequest.class);
        kryo.register(PlayerJoinRequest.class);
        kryo.register(PlayerJoinResponse.class);
        kryo.register(PlayerLeaveRequest.class);
        kryo.register(PlayerLeaveResponse.class);
        kryo.register(PlayerMovementRequest.class);
        kryo.register(PlayerMovementResponse.class);
        kryo.register(PlayerNameRequest.class);
        kryo.register(PlayerNameResponse.class);
        kryo.register(PlayersHealthStatusRequest.class);
        kryo.register(PlayerTookDamageRequest.class);
        kryo.register(PlayerStatisticsResponse.class);
        kryo.register(ServerErrorResponse.class);
        kryo.register(ServerSuccessResponse.class);
        kryo.register(CreatePlayerRequest.class);
        kryo.register(CreatePlayerResponse.class);
        kryo.register(CreateGameRequest.class);
        kryo.register(CreateGameResponse.class);
        kryo.register(GameJoinedResponse.class);
        kryo.register(GameStartPacket.class);
        kryo.register(SpellFiredPacket.class);
        kryo.register(HashMap.class);
        kryo.register(Vector2.class);
        kryo.register(ArrayList.class);
    }
}
