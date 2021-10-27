package org.example.models.messages.coordination.heartbeat;

import org.example.models.messages.coordination.AbstractCoordinationMessage;

public class HeartbeatMessage extends AbstractCoordinationMessage {
    public HeartbeatMessage(String serverName) {
        super("heartbeat", serverName);
    }
}
