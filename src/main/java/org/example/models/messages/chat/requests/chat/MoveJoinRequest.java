package org.example.models.messages.chat.requests.chat;

import org.example.models.messages.chat.requests.RequestConstants;

public class MoveJoinRequest extends AbstractRoomRequest {

    private final String identity;
    private final String former;

    public MoveJoinRequest(String former, String identity, String roomId) {
        super(RequestConstants.MOVE_JOIN, roomId);
        this.former = former;
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public String getFormer() {
        return former;
    }
}
