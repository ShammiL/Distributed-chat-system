package org.example.models.messages.coordination.leader.reply;


import com.google.gson.annotations.SerializedName;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.room.GlobalRoom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalRoomResponse extends AbstractCoordinationMessage {

    @SerializedName("globalRoomList")
    private Map<String, GlobalRoom> globalRoomList = new ConcurrentHashMap<>();


    public GlobalRoomResponse(String serverName) {
        super("room_list_response", serverName);
    }

    public Map<String, GlobalRoom> getGlobalRoomList() {
        return globalRoomList;
    }

    public void setGlobalRoomList(Map<String, GlobalRoom> globalRoomList) {
        this.globalRoomList = globalRoomList;
    }
}
