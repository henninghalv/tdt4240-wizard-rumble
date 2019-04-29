package com.progark.group2.wizardrumble.network;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.progark.group2.wizardrumble.network.packets.GameStartPacket;
import com.progark.group2.wizardrumble.network.packets.PlayerDeadPacket;
import com.progark.group2.wizardrumble.network.packets.GameEndPacket;
import com.progark.group2.wizardrumble.network.packets.PlayerMovementPacket;
import com.progark.group2.wizardrumble.network.packets.SpellFiredPacket;
import com.progark.group2.wizardrumble.network.requests.CreateGameRequest;
import com.progark.group2.wizardrumble.network.requests.CreatePlayerRequest;

import com.progark.group2.wizardrumble.network.requests.PlayerJoinRequest;
import com.progark.group2.wizardrumble.network.requests.PlayerLeaveRequest;
import com.progark.group2.wizardrumble.network.resources.Player;
import com.progark.group2.wizardrumble.network.responses.CreateGameResponse;
import com.progark.group2.wizardrumble.network.responses.CreatePlayerResponse;
import com.progark.group2.wizardrumble.network.responses.GameJoinedResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerJoinResponse;
import com.progark.group2.wizardrumble.network.responses.PlayerLeaveResponse;
import com.progark.group2.wizardrumble.network.responses.ServerErrorResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class KryoClientRegister {

    /**
     * Registers all classes for kryo serializer
     * @param client    kryo client object
     */
    static void registerKryoClasses(Client client) {
        Kryo kryo = client.getKryo();
        kryo.register(Integer.class);
        kryo.register(HashMap.class);
        kryo.register(Vector2.class);
        kryo.register(ArrayList.class);
        kryo.register(Player.class);

        kryo.register(PlayerJoinRequest.class);
        kryo.register(PlayerJoinResponse.class);
        kryo.register(PlayerLeaveRequest.class);
        kryo.register(PlayerLeaveResponse.class);
        kryo.register(CreatePlayerRequest.class);
        kryo.register(CreatePlayerResponse.class);
        kryo.register(CreateGameRequest.class);
        kryo.register(CreateGameResponse.class);
        kryo.register(GameJoinedResponse.class);
        kryo.register(GameStartPacket.class);
        kryo.register(GameEndPacket.class);
        kryo.register(PlayerMovementPacket.class);
        kryo.register(SpellFiredPacket.class);
        kryo.register(PlayerDeadPacket.class);
        kryo.register(ServerErrorResponse.class);
    }
}
