package org.example.models.messages.chat.requests.chat;

import org.example.models.messages.chat.requests.RequestConstants;

public class CreateRoomRequest extends AbstractRoomRequest{

    public CreateRoomRequest(String roomId) {
        super(RequestConstants.CREATE_ROOM, roomId);
    }
}
