package org.example.models.messages.chat.requests.chat;

import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.RequestConstants;

public class NewIdentityRequest extends AbstractChatRequest {

    private final String identity;

    public NewIdentityRequest(String identity) {
        super(RequestConstants.NEW_IDENTITY);
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }
}
