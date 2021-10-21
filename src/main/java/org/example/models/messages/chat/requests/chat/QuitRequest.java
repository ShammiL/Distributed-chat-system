package org.example.models.messages.chat.requests.chat;

import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.RequestConstants;

public class QuitRequest extends AbstractChatRequest {

    public QuitRequest() {
        super(RequestConstants.QUIT);
    }
}
