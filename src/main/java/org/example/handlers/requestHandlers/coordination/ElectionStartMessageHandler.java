package org.example.handlers.requestHandlers.coordination;

import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class ElectionStartMessageHandler extends AbstractCoordinationRequestHandler {

    public ElectionStartMessageHandler(ServerInfo server) {
        super(server);
    }


    @Override
    public JSONObject handleRequest() {
        System.out.println("Received Election Start Message from: " + server.getServerId());
        return null;
    }
}
