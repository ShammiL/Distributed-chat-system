package org.example.handlers.requestHandlers.coordination;

import org.example.models.client.GlobalClient;
import org.example.models.messages.coordination.leader.reply.ReplyObjects;
import org.example.models.messages.coordination.leader.request.IdentityReserveRequest;
import org.example.models.room.GlobalRoom;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class IdentityReserveRequestHandler extends AbstractCoordinationRequestHandler{

    private final IdentityReserveRequest request;
    public IdentityReserveRequestHandler(ServerInfo server, IdentityReserveRequest request) {
        super(server);
        this.request = request;
    }


    @Override
    public JSONObject handleRequest() {
        System.out.println("Received Identity Reserve Request from: " +
                server.getServerId() + " " +
                request.getIdentity() + " " +
                request.getIdentityType() + " " +
                request.getServerName()
        );
//        return null;
        boolean approved = false;

        if (request.getIdentityType().equals("room")){
            GlobalRoom room = new GlobalRoom(request.getIdentity() , server.getServerId());
            approved = LeaderState.getInstance().checkAndAddRoom(room);
        }
        else if (request.getIdentityType().equals("client")){
            GlobalClient client = new GlobalClient(request.getIdentity() , server.getServerId());
            approved = LeaderState.getInstance().checkAndAddClient(client);
        }
        return ReplyObjects.identityReserveReply(
                approved,
                request.getIdentity(),
                request.getIdentityType(),
                server.getServerId()
        );
    }
}
