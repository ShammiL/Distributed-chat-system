package org.example.models.messages.coordination.leader.request;

import org.example.models.messages.coordination.AbstractCoordinationMessage;

public class GlobalRoomListRequest extends AbstractCoordinationMessage {

    public GlobalRoomListRequest(String servername) {
        super("room_list", servername);
    }
}
