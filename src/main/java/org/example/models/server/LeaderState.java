package org.example.models.server;

import io.netty.channel.ChannelId;
import org.example.models.client.Client;
import org.example.models.client.GlobalClient;
import org.example.models.client.IClient;
import org.example.models.room.GlobalRoom;
import org.example.models.room.Room;
import org.example.services.client.ChatClientServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeaderState {
    private static LeaderState instance;
    private static Map<String, GlobalClient> globalClientList;
    private static Map<String, GlobalRoom> globalRoomList;

    public static synchronized LeaderState getInstance() {
        if (instance == null && ServerState.getInstance().getCoordinator().equals(ServerState.getInstance().getServerInfo())) {
            instance = new LeaderState();
            globalClientList = convertGlobalClientList(ChatClientServer.channelIdClient);
            globalRoomList = convertGlobalRoomList(ChatClientServer.localRoomIdLocalRoom);
        }
        return instance;
    }

    public static Map<String, GlobalClient> getGlobalClientList() {
        return globalClientList;
    }

    public static Map<String, GlobalRoom> getGlobalRoomList() {
        return globalRoomList;
    }

    public static Map<String, GlobalClient> convertGlobalClientList(Map<ChannelId, IClient> localClientList) {
        Map<String, GlobalClient> globalClientList = new ConcurrentHashMap<>();
        for (Map.Entry<ChannelId, IClient> entry : localClientList.entrySet()) {
            Client client = (Client) entry.getValue();
            GlobalClient gClient = new GlobalClient(client.getIdentity(), ServerState.getInstance().getServerInfo().getServerId());
            globalClientList.put(gClient.getIdentity(), gClient);

        }

        return globalClientList;
    }

    public static Map<String, GlobalRoom> convertGlobalRoomList(Map<String, Room> localRoomList) {
        Map<String, GlobalRoom> globalRoomList = new ConcurrentHashMap<>();
        for (Map.Entry<String, Room> entry : localRoomList.entrySet()) {
            GlobalRoom gRoom = new GlobalRoom(entry.getValue().getRoomId(), ServerState.getInstance().getServerInfo().getServerId());
            globalRoomList.put(gRoom.getRoomId(), gRoom);

        }

        return globalRoomList;
    }
}
