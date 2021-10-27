package org.example.handlers.requestHandlers.coordination;

import org.example.models.server.ServerInfo;
import org.example.services.coordination.election.BullyElection;
import org.json.simple.JSONObject;

public class ElectionAnswerMessageHandler extends AbstractCoordinationRequestHandler {

    public ElectionAnswerMessageHandler(ServerInfo server) {
        super(server);
    }

    @Override
    public JSONObject handleRequest() {
        System.out.println("Received ElectionAnswer from: " + server.getServerId());
        if (BullyElection.getInstance().getWaitingForElectionAnswerMsg()) {
            BullyElection.getInstance().endElectionStart();
            BullyElection.getInstance().endElectionAnswerMsgTimeout();
            BullyElection.getInstance().startCoordinatorWaitTimeout();
        }
        return null;
    }
}
