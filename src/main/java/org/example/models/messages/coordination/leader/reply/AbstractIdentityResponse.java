package org.example.models.messages.coordination.leader.reply;

import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.leader.AbstractIdentityMessage;

public class AbstractIdentityResponse extends AbstractIdentityMessage {

    protected String status;

    public AbstractIdentityResponse(String type, String identity, String identityType, String status, String serverName) {
        super(type, identity, identityType, serverName);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
