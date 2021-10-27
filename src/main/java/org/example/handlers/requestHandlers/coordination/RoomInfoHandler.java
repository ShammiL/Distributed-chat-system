package org.example.handlers.requestHandlers.coordination;

import org.example.models.messages.coordination.leader.reply.ReplyObjects;
import org.example.models.messages.coordination.leader.request.IdentityReserveRequest;
import org.example.models.messages.coordination.leader.request.RoomInfoRequest;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
import org.json.simple.JSONObject;

public class RoomInfoHandler extends AbstractCoordinationRequestHandler{

    private final RoomInfoRequest request;
    public RoomInfoHandler(ServerInfo server, RoomInfoRequest request) {
        super(server);
        this.request = request;
    }

    @Override
    public JSONObject handleRequest() {
        System.out.println("Received RoomInfoRequest Request from: " +
                server.getServerId() + " " +
                request.getRoomId() + " " +
                request.getType() + " "
                );
//        return null;
        String roomOwnedServerName;
        if(LeaderState.getInstance().getServerFromRoomId(request.getRoomId()) != null){
            roomOwnedServerName = LeaderState.getInstance().getServerFromRoomId(request.getRoomId());
        }
        else {
            roomOwnedServerName = null;
        }
        return ReplyObjects.roomIdInfoReply(server.getServerId(), roomOwnedServerName);
    }
}
