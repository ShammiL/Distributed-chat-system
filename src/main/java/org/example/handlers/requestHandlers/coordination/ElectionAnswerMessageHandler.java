package org.example.handlers.requestHandlers.coordination;

import org.apache.log4j.Logger;
import org.example.models.server.ServerInfo;
import org.example.services.coordination.election.BullyElection;
import org.json.simple.JSONObject;

public class ElectionAnswerMessageHandler extends AbstractCoordinationRequestHandler {

    private final Logger logger = Logger.getLogger(ElectionAnswerMessageHandler.class);

    public ElectionAnswerMessageHandler(ServerInfo server) {
        super(server);
    }

    @Override
    public JSONObject handleRequest() {
        logger.info("Received ElectionAnswer from: " + server.getServerId());
        if (BullyElection.getInstance().getWaitingForElectionAnswerMsg()) {
            BullyElection.getInstance().endElectionStart();
            BullyElection.getInstance().endElectionAnswerMsgTimeout();
            BullyElection.getInstance().startCoordinatorWaitTimeout();
        }
        return null;
    }
}
