package org.example.models.server;

import io.netty.channel.ChannelId;
import org.example.models.client.IClient;
import org.example.models.room.Room;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeaderState {
    private static LeaderState instance;
    private static Map<String, IClient> globalClientList = new ConcurrentHashMap<>();
    private static Map<String, Room> globalRoomList = new ConcurrentHashMap<>();

    public static synchronized LeaderState getInstance() {
        if (instance == null && ServerState.getInstance().getCoordinator().equals(ServerState.getInstance().getServerInfo())) {
            instance = new LeaderState();
        }
        return instance;
    }

    public static Map<String, IClient> getGlobalClientList() {
        return globalClientList;
    }

    public static Map<String, Room> getGlobalRoomList() {
        return globalRoomList;
    }

}
