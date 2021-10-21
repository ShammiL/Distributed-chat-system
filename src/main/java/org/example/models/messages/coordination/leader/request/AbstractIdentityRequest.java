package org.example.models.messages.coordination.leader.request;

import org.example.models.messages.coordination.AbstractCoordinationMessage;

public abstract class AbstractIdentityRequest extends AbstractCoordinationMessage {

    private final String identity;
    private final String identityType;

    public AbstractIdentityRequest(String type, String identity, String identityType, String serverName) {
        super(type, serverName);
        this.identity = identity;
        this.identityType = identityType;
    }

    public String getIdentity() {
        return identity;
    }

    public String getIdentityType() {
        return identityType;
    }
}
