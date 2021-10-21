package org.example.handlers.requestHandlers.coordination;

import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class ElectionCoordinatorMessageHandler extends AbstractCoordinationRequestHandler {

    public ElectionCoordinatorMessageHandler(ServerInfo server) {
        super(server);
    }


    @Override
    public JSONObject handleRequest() {
        System.out.println("Received Election Coordinator Message from: " + server.getServerId());
        return null;
    }
}
