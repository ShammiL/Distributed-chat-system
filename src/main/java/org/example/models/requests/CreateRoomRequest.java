package org.example.models.requests;

public class CreateRoomRequest extends AbstractRoomRequest{

    public CreateRoomRequest(String roomId) {
        super(RequestConstants.CREATE_ROOM, roomId);
    }
}
