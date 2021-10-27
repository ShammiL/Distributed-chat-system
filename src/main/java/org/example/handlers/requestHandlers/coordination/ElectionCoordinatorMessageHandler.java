package org.example.handlers.requestHandlers.coordination;

import org.apache.log4j.Logger;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.election.BullyElection;
import org.json.simple.JSONObject;

import java.net.ConnectException;

public class ElectionCoordinatorMessageHandler extends AbstractCoordinationRequestHandler {
    private final Logger logger = Logger.getLogger(ElectionCoordinatorMessageHandler.class);

    public ElectionCoordinatorMessageHandler(ServerInfo server) {
        super(server);
    }


    @Override
    public JSONObject handleRequest() {
        logger.info("Received Election Coordinator Message from: " + server.getServerId());
        if (ServerState.getInstance().getHigherServerInfo().contains(server)) {
            BullyElection.getInstance().endCoordinatorMessageTimeout();
            BullyElection.getInstance().endElectionAnswerMsgTimeout();
            BullyElection.getInstance().endElectionStart();
            BullyElection.getInstance().setNewCoordinator(server);
            BullyElection.getInstance().sendCoordinatorInformationMessage(server);
            logger.info("message sent to the coordinator");
        }
        return null;
    }
}
