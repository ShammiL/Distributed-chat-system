package org.example.models.messages.coordination.election;

import org.example.models.messages.coordination.AbstractCoordinationMessage;

public class ElectionCoordinatorMessage extends AbstractCoordinationMessage {

    public ElectionCoordinatorMessage(String serverName) {
        super("electioncoordinator", serverName);
    }
}
