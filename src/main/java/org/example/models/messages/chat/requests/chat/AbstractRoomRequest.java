package org.example.models.messages.chat.requests.chat;

import org.example.models.messages.chat.AbstractChatRequest;

public abstract class AbstractRoomRequest extends AbstractChatRequest {

    private final String roomId;

    public AbstractRoomRequest(String type, String roomId) {
        super(type);
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
