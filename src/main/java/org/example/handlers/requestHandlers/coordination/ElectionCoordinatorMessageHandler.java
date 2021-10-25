package org.example.handlers.requestHandlers.coordination;

import org.example.models.server.ServerInfo;
import org.example.services.coordination.election.BullyElection;
import org.json.simple.JSONObject;

import java.net.ConnectException;

public class ElectionCoordinatorMessageHandler extends AbstractCoordinationRequestHandler {

    public ElectionCoordinatorMessageHandler(ServerInfo server) {
        super(server);
    }


    @Override
    public JSONObject handleRequest() {
        System.out.println("Received Election Coordinator Message from: " + server.getServerId());
//      set msg sender as new coordinator
        new BullyElection().setNewCoordinator(server);
        new BullyElection().sendCoordinatorInformationMessage(server);
        System.out.println("mesg sent");
        return null;
    }
}
