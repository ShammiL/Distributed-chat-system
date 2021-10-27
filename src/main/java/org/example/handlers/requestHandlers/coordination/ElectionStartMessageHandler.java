package org.example.handlers.requestHandlers.coordination;

import org.apache.log4j.Logger;
import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.election.BullyElection;
import org.json.simple.JSONObject;


public class ElectionStartMessageHandler extends AbstractCoordinationRequestHandler {
    private final Logger logger = Logger.getLogger(ElectionStartMessageHandler.class);

    public ElectionStartMessageHandler(ServerInfo server) {
        super(server);
    }


    @Override
    public JSONObject handleRequest() {
        logger.info("Received Election Start Message from: " + server.getServerId());

//        send answer message
        BullyElection.getInstance().replyAnswerMessage(server);


        if (ServerState.getInstance().getHigherServerInfo().isEmpty()) {
//                if there are no higher priority servers
            logger.info("ElectionStartMessageHandler : there are no higher priority servers");
            BullyElection.getInstance().informAndSetNewCoordinator(
                    ServerState.getInstance().getLowerServerInfo());
        } else {
            BullyElection.getInstance().startElection(ServerState.getInstance().getHigherServerInfo());
        }

        return null;
    }
}
