package org.example.handlers.requestHandlers.coordination;

import org.example.models.messages.coordination.election.CoordinatorInformationMessage;
import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class CoordinatorInformationMessageHandler extends AbstractCoordinationRequestHandler{

    public CoordinatorInformationMessageHandler(ServerInfo server, CoordinatorInformationMessage message) {
        super(server);
    }

    @Override
    public JSONObject handleRequest() {
        System.out.println("in handler");
        //TODO:: append local client and room lists to leaderstate global lists
        return null;
    }
}
