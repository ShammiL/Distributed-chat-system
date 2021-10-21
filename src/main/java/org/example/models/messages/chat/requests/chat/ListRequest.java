package org.example.models.messages.chat.requests.chat;

import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.RequestConstants;

public class ListRequest extends AbstractChatRequest {

    public ListRequest() {
        super(RequestConstants.LIST);
    }
}
