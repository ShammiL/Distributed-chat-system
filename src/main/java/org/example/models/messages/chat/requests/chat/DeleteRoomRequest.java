package org.example.models.messages.chat.requests.chat;

import org.example.models.messages.chat.requests.RequestConstants;

public class DeleteRoomRequest extends AbstractRoomRequest {

    public DeleteRoomRequest(String roomId) {
        super(RequestConstants.DELETE_ROOM, roomId);
    }
}
