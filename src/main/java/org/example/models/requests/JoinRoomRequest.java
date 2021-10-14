package org.example.models.requests;

public class JoinRoomRequest extends AbstractRoomRequest {

    public JoinRoomRequest(String roomId) {
        super(RequestConstants.JOIN_ROOM, roomId);
    }
}
