package org.example.models.messages.coordination.heartbeat;

import org.example.models.messages.coordination.AbstractCoordinationMessage;

public class HeartbeatMessage extends AbstractCoordinationMessage {
    //TODO: Possible to a separate UDP server for heartbeats
    public HeartbeatMessage(String serverName) {
        super("heartbeat", serverName);
    }
}
