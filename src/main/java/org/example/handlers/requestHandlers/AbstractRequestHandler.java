package org.example.handlers.requestHandlers;

import org.example.models.client.Client;
import org.json.simple.JSONObject;

public abstract class AbstractRequestHandler {
    private Client client;

    public AbstractRequestHandler(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    //    public abstract void processRequest();
    public abstract JSONObject processRequest();

}
