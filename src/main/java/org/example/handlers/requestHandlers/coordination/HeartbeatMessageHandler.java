package org.example.handlers.requestHandlers.coordination;

import org.apache.log4j.Logger;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.election.BullyElection;
import org.json.simple.JSONObject;

public class HeartbeatMessageHandler extends AbstractCoordinationRequestHandler {

    private final Logger logger = Logger.getLogger(HeartbeatMessageHandler.class);

    public HeartbeatMessageHandler(ServerInfo server) {
        super(server);
    }


    @Override
    public JSONObject handleRequest() {
        logger.info("Received Heartbeat Message from: " + server.getServerId());
        if (server.equals(ServerState.getInstance().getCoordinator())
                && !server.equals(ServerState.getInstance().getServerInfo())) {
            BullyElection.getInstance().restartElectionTimeout();
        }
        return null;
    }
}
