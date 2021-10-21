package org.example.models.messages.coordination.election;

import org.example.models.messages.coordination.AbstractCoordinationMessage;

public class ElectionStartMessage extends AbstractCoordinationMessage {

    public ElectionStartMessage(String serverName) {
        super("electionstart", serverName);
    }
}
