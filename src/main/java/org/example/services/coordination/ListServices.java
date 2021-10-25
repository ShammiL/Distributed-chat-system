package org.example.services.coordination;

import io.netty.channel.ChannelId;
import org.example.models.client.Client;
import org.example.models.client.GlobalClient;
import org.example.models.client.IClient;
import org.example.models.room.GlobalRoom;
import org.example.models.room.Room;
import org.example.models.server.ServerState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ListServices {
    private ListServices(){}

    public static Map<String, GlobalClient> convertGlobalClientList(Map<ChannelId, IClient> localClientList) {
        Map<String, GlobalClient> globalClients = new ConcurrentHashMap<>();
        for (Map.Entry<ChannelId, IClient> entry : localClientList.entrySet()) {
            Client client = (Client) entry.getValue();
            GlobalClient gClient = new GlobalClient(client.getIdentity(), ServerState.getInstance().getServerInfo().getServerId());
            globalClients.put(gClient.getIdentity(), gClient);

        }

        return globalClients;
    }

    public static Map<String, GlobalRoom> convertGlobalRoomList(Map<String, Room> localRoomList) {
        Map<String, GlobalRoom> globalRooms = new ConcurrentHashMap<>();
        for (Map.Entry<String, Room> entry : localRoomList.entrySet()) {
            GlobalRoom gRoom = new GlobalRoom(entry.getValue().getRoomId(), ServerState.getInstance().getServerInfo().getServerId());
            globalRooms.put(gRoom.getRoomId(), gRoom);

        }

        return globalRooms;
    }
}
