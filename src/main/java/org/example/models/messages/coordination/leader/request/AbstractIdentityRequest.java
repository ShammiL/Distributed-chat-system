package org.example.models.messages.coordination.leader.request;

import org.example.models.messages.coordination.leader.AbstractIdentityMessage;

public abstract class AbstractIdentityRequest extends AbstractIdentityMessage {

    public AbstractIdentityRequest(String type, String identity, String identityType, String serverName) {
        super(type, identity, identityType, serverName);
    }

}
