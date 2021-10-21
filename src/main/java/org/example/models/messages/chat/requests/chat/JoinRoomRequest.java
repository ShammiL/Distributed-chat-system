package org.example.models.messages.chat.requests.chat;

import org.example.models.messages.chat.requests.RequestConstants;

public class JoinRoomRequest extends AbstractRoomRequest {

    public JoinRoomRequest(String roomId) {
        super(RequestConstants.JOIN_ROOM, roomId);
    }
}
