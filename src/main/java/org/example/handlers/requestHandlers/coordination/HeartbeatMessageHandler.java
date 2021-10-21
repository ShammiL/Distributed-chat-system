package org.example.handlers.requestHandlers.coordination;

import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class HeartbeatMessageHandler extends AbstractCoordinationRequestHandler {

    public HeartbeatMessageHandler(ServerInfo server) {
        super(server);
    }


    @Override
    public JSONObject handleRequest() {
        System.out.println("Received Heartbeat Message from: " + server.getServerId() );
        return null;
    }
}
