package org.example.models.messages.coordination.election;

import org.example.models.messages.coordination.AbstractCoordinationMessage;

public class ElectionAnswerMessage extends AbstractCoordinationMessage {

    public ElectionAnswerMessage(String serverName) {
        super("electionanswer", serverName);
    }
}
