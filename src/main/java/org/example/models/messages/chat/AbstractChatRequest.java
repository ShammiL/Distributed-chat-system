package org.example.models.messages.chat;

import org.example.models.client.Client;
import org.example.models.messages.AbstractMessage;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractChatRequest extends AbstractMessage {

    private int tries = 0;
    private Client client;

    public AbstractChatRequest(String type) {
        super(type);
    }

    public synchronized int getTries() {
        return tries;
    }

    public synchronized void incrementTries() {
        tries += 1;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
