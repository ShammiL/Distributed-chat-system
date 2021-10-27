package org.example.models.messages.coordination.leader.request;

import com.google.gson.annotations.SerializedName;
import org.example.models.messages.coordination.AbstractCoordinationMessage;

public class RoomInfoRequest extends AbstractCoordinationMessage {

    @SerializedName("roomId")
    private String roomId;

    public RoomInfoRequest(String serverName, String roomId) {
        super("room_info_request", serverName);
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
