package org.example.models.messages.coordination.election;

import org.example.models.client.GlobalClient;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.room.GlobalRoom;
import org.example.services.client.ChatClientServer;
import org.example.services.coordination.ListServices;

import java.util.Map;

public class CoordinatorInformationMessage extends AbstractCoordinationMessage {
    private Map<String, GlobalClient> globalClientList;
    private Map<String, GlobalRoom> globalRoomList;


    public CoordinatorInformationMessage(String serverName) {
        super("coordinatorinformation", serverName);
        this.globalRoomList = ListServices.convertGlobalRoomList(ChatClientServer.localRoomIdLocalRoom);
        this.globalClientList = ListServices.convertGlobalClientList(ChatClientServer.channelIdClient);
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

}
