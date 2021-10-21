package org.example.models.messages.chat.requests.chat;

import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.RequestConstants;

public class WhoRequest extends AbstractChatRequest {

    public WhoRequest() {
        super(RequestConstants.WHO);
    }
}
