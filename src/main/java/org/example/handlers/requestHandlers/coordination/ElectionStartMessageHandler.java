package org.example.handlers.requestHandlers.coordination;

import org.example.models.server.ServerInfo;
import org.example.models.server.ServerState;
import org.example.services.coordination.election.BullyElection;
import org.json.simple.JSONObject;

import java.net.ConnectException;

public class ElectionStartMessageHandler extends AbstractCoordinationRequestHandler {

    public ElectionStartMessageHandler(ServerInfo server) {
        super(server);
    }


    @Override
    public JSONObject handleRequest() {
        System.out.println("Received Election Start Message from: " + server.getServerId());

//        send answer message
        new BullyElection().replyAnswerMessage(server);


        if (ServerState.getInstance().getHigherServerInfo().isEmpty()) {
//                if there are no higher priority servers
            System.out.println("ElectionStartMessageHandler : there are no higher priority servers");
            new BullyElection().informAndSetNewCoordinator(
                    ServerState.getInstance().getLowerServerInfo());
        } else {
            new BullyElection().startElection(ServerState.getInstance().getHigherServerInfo());
        }

        return null;
    }
}
