package org.example.handlers.requestHandlers.coordination;

import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.election.BullyElection;
import org.json.simple.JSONObject;

public class HeartbeatMessageHandler extends AbstractCoordinationRequestHandler {

    public HeartbeatMessageHandler(ServerInfo server) {
        super(server);
    }


    @Override
    public JSONObject handleRequest() {
        System.out.println("Received Heartbeat Message from: " + server.getServerId());
        if (server.equals(ServerState.getInstance().getCoordinator())
                && !server.equals(ServerState.getInstance().getServerInfo())) {
            BullyElection.getInstance().restartElectionTimeout();
        }
        return null;
    }
}
