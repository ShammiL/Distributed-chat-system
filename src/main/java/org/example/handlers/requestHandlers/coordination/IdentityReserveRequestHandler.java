package org.example.handlers.requestHandlers.coordination;

import org.example.models.messages.coordination.leader.request.IdentityReserveRequest;
import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class IdentityReserveRequestHandler extends AbstractCoordinationRequestHandler{

    private final IdentityReserveRequest request;
    public IdentityReserveRequestHandler(ServerInfo server, IdentityReserveRequest request) {
        super(server);
        this.request = request;
    }


    @Override
    public JSONObject handleRequest() {
        System.out.println("Received Identity Reserve Request from: " + server.getServerId());
        return null;
    }
}
