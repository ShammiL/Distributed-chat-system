package org.example.handlers.requestHandlers.coordination;

import org.example.models.messages.coordination.leader.request.IdentityReleaseRequest;
import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class IdentityReleaseRequestHandler extends AbstractCoordinationRequestHandler{

    private final IdentityReleaseRequest request;
    public IdentityReleaseRequestHandler(ServerInfo server, IdentityReleaseRequest request) {
        super(server);
        this.request = request;
    }


    @Override
    public JSONObject handleRequest() {
        System.out.println("Received Identity Release Request from: " + server.getServerId());
        return null;
    }
}
