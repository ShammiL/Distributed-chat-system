package org.example.handlers.requestHandlers.coordination;

import org.example.models.messages.coordination.leader.reply.ReplyObjects;
import org.example.models.messages.coordination.leader.request.IdentityReleaseRequest;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class IdentityReleaseRequestHandler extends AbstractCoordinationRequestHandler{

    private final IdentityReleaseRequest request;
    public IdentityReleaseRequestHandler(ServerInfo server, IdentityReleaseRequest request) {
        super(server);
        this.request = request;
    }


    @Override
    public JSONObject handleRequest() {
        System.out.println("Received Identity Release Request from: " +
                server.getServerId() + " " +
                request.getIdentity() + " " +
                request.getIdentityType() + " " +
                request.getServerName()
        );
//        return null;
        boolean approved = false;
        // todo: delete from global lists
//        if (request.getIdentity().equals("room")){
//            approved = !LeaderState.getInstance().getGlobalRoomList().containsKey(request.getIdentity());
//        }
//        else if (request.getIdentity().equals("client")){
//            approved = !LeaderState.getInstance().getGlobalClientList().containsKey(request.getIdentity());
//        }
        return ReplyObjects.identityReleaseReply(
                true,
                request.getIdentity(),
                request.getIdentityType(),
                server.getServerId()
        );
    }
}
