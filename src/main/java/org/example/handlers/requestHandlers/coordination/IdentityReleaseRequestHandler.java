package org.example.handlers.requestHandlers.coordination;

import org.apache.log4j.Logger;
import org.example.models.messages.coordination.leader.reply.ReplyObjects;
import org.example.models.messages.coordination.leader.request.IdentityReleaseRequest;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class IdentityReleaseRequestHandler extends AbstractCoordinationRequestHandler {

    private final Logger logger = Logger.getLogger(IdentityReleaseRequestHandler.class);
    private final IdentityReleaseRequest request;

    public IdentityReleaseRequestHandler(ServerInfo server, IdentityReleaseRequest request) {
        super(server);
        this.request = request;
    }


    @Override
    public JSONObject handleRequest() {
        logger.info("Received Identity Release Request from: " +
                server.getServerId() + " " +
                request.getIdentity() + " " +
                request.getIdentityType() + " " +
                request.getServerName()
        );

        if (request.getIdentityType().equals("room")) {
            LeaderState.getInstance().deleteARoom(request.getIdentity());
        } else if (request.getIdentityType().equals("client")) {
            LeaderState.getInstance().deleteAClient(request.getIdentity());
        }
        return ReplyObjects.identityReleaseReply(
                true,
                request.getIdentity(),
                request.getIdentityType(),
                server.getServerId()
        );
    }
}
