package org.example.models.messages.coordination.leader;

import com.google.gson.annotations.SerializedName;
import org.example.models.messages.coordination.AbstractCoordinationMessage;

public class AbstractIdentityMessage extends AbstractCoordinationMessage {
    protected final String identity;

    @SerializedName("identityType")
    protected final String identityType;

    public AbstractIdentityMessage(String type, String identity, String identityType, String serverName) {
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
