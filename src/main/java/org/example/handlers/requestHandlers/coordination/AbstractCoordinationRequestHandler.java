package org.example.handlers.requestHandlers.coordination;

import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public abstract class AbstractCoordinationRequestHandler {

    protected ServerInfo server;

    public AbstractCoordinationRequestHandler(ServerInfo server) {
        this.server = server;
    }

    public abstract JSONObject handleRequest();
}
