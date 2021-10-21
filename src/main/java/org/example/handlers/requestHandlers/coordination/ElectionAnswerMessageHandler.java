package org.example.handlers.requestHandlers.coordination;

import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class ElectionAnswerMessageHandler extends AbstractCoordinationRequestHandler {

    public ElectionAnswerMessageHandler(ServerInfo server) {
        super(server);
    }



    @Override
    public JSONObject handleRequest() {
        System.out.println("Received ElectionAnswer from: " + server.getServerId());
        return null;
    }
}
