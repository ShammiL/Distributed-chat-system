package org.example.handlers.requestHandlers.coordination;

import org.apache.log4j.Logger;
import org.example.models.messages.coordination.election.CoordinatorInformationMessage;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class CoordinatorInformationMessageHandler extends AbstractCoordinationRequestHandler {
    private CoordinatorInformationMessage message;
    private final Logger logger = Logger.getLogger(CoordinatorInformationMessageHandler.class);

    public CoordinatorInformationMessageHandler(ServerInfo server, CoordinatorInformationMessage message) {
        super(server);
        this.message = message;
    }

    @Override
    public JSONObject handleRequest() {
        logger.info("Coordination information from: " + server.getServerId());
        LeaderState.getInstance().addListsOfAServer(message.getGlobalClientList(), message.getGlobalRoomList());
        return null;
    }
}
