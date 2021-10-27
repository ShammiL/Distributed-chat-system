package org.example.models.messages.coordination.leader.reply;

import com.google.gson.annotations.SerializedName;
import org.example.models.messages.coordination.AbstractCoordinationMessage;

public class RoomInfoResponse extends AbstractCoordinationMessage {

    @SerializedName("roomOwnedServerName")
    String roomOwnedServerName;

    public RoomInfoResponse(String serverName, String roomOwnedServerName) {
        super("room_info_response", serverName);
        this.roomOwnedServerName = roomOwnedServerName;
    }


    public String getRoomOwnedServerName() {
        return roomOwnedServerName;
    }

    public void setRoomOwnedServerName(String roomOwnedServerName) {
        this.roomOwnedServerName = roomOwnedServerName;
    }
}

