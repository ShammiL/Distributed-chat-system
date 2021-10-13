package org.example.models.requests;

public class DeleteRoomRequest extends AbstractRoomRequest {

    public DeleteRoomRequest(String roomId) {
        super(RequestConstants.DELETE_ROOM, roomId);
    }
}
