package org.example.handlers.requestHandlers.coordination;

import org.example.models.messages.coordination.election.CoordinatorInformationMessage;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class CoordinatorInformationMessageHandler extends AbstractCoordinationRequestHandler{
    private CoordinatorInformationMessage message;

    public CoordinatorInformationMessageHandler(ServerInfo server, CoordinatorInformationMessage message) {
        super(server);
        this.message = message;
    }

    @Override
    public JSONObject handleRequest() {
        System.out.println("in handler coordination information");
        LeaderState.getInstance().addListsOfAServer(message.getGlobalClientList(), message.getGlobalRoomList());
        return null;
    }
}
