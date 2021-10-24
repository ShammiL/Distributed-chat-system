package org.example.models.messages.coordination.election;

import io.netty.channel.ChannelId;
import org.example.models.client.Client;
import org.example.models.client.GlobalClient;
import org.example.models.client.IClient;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.room.GlobalRoom;
import org.example.models.room.LocalRoom;
import org.example.models.room.Room;
import org.example.models.server.ServerState;
import org.example.services.client.ChatClientServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CoordinatorInformationMessage extends AbstractCoordinationMessage {
    private Map<String, GlobalClient> globalClientList;
    private Map<String, GlobalRoom> globalRoomList;

    public CoordinatorInformationMessage(String type, String serverName) {
        super("coordinatorinformation", serverName);
    }

    public CoordinatorInformationMessage(String serverName) {
        super("coordinatorinformation", serverName);
        this.globalRoomList = convertGlobalRoomList(ChatClientServer.localRoomIdLocalRoom);
        this.globalClientList = convertGlobalClientList(ChatClientServer.channelIdClient);
    }

    public Map<String, GlobalClient> getGlobalClientList() {
        return globalClientList;
    }

    public Map<String, GlobalRoom> getGlobalRoomList() {
        return globalRoomList;
    }

    public void setGlobalRoomList(Map<String, GlobalRoom> globalRoomList) {
        this.globalRoomList = globalRoomList;
    }

    public void setGlobalClientList(Map<String, GlobalClient> globalClientList) {
        this.globalClientList = globalClientList;
    }

    public Map<String, GlobalClient> convertGlobalClientList(Map<ChannelId, IClient> localClientList) {
        Map<String, GlobalClient> globalClientList = new ConcurrentHashMap<>();
        for (Map.Entry<ChannelId, IClient> entry : localClientList.entrySet()) {
            Client client = (Client) entry.getValue();
            GlobalClient gClient = new GlobalClient(client.getIdentity(), ServerState.getInstance().getServerInfo().getServerId());
            globalClientList.put(gClient.getIdentity(), gClient);

        }

        return globalClientList;
    }

    public Map<String, GlobalRoom> convertGlobalRoomList(Map<String, Room> localRoomList) {
        Map<String, GlobalRoom> globalRoomList = new ConcurrentHashMap<>();
        for (Map.Entry<String, Room> entry : localRoomList.entrySet()) {
            GlobalRoom gRoom = new GlobalRoom(entry.getValue().getRoomId(), ServerState.getInstance().getServerInfo().getServerId());
            globalRoomList.put(gRoom.getRoomId(), gRoom);

        }

        return globalRoomList;
    }
}
