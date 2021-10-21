package org.example.models.messages.chat.requests.chat;

import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.RequestConstants;

public class MessageRequest extends AbstractChatRequest {

    private final String content;

    public MessageRequest(String content) {
        super(RequestConstants.MESSAGE);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
