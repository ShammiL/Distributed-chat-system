package org.example.models.requests;

public abstract class AbstractRoomRequest extends AbstractRequest{

    private String roomId;

    public AbstractRoomRequest(String type, String roomId) {
        super(type);
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
