package org.example.models.messages.chat;

import org.example.models.messages.AbstractMessage;

public abstract class AbstractChatRequest extends AbstractMessage {

    public AbstractChatRequest(String type) {
        super(type);
    }
}
