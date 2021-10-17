package org.example.handlers.requestHandlers;

import io.netty.channel.ChannelId;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.reply.ReplyObjects;
import org.example.models.requests.AbstractRequest;
import org.example.models.requests.DeleteRoomRequest;
import org.example.models.room.LocalRoom;
import org.example.services.client.ChatClientServer;
import org.json.simple.JSONObject;

import java.util.Map;

public class DeleteRoomRequestHandler extends AbstractRequestHandler{
    public DeleteRoomRequest request;

    public DeleteRoomRequestHandler(AbstractRequest request, IClient client) {
        super((Client) client);
        this.request = (DeleteRoomRequest) request;
    }

    @Override
    public JSONObject processRequest() {
        return ReplyObjects.deleteRoomClientMessage(request.getRoomId(), isCapable());
    }

    @Override
    public void handleRequest() {
//        synchronized (this) {
//            JSONObject reply = processRequest();
//            sendResponse(reply);
//
//            if (reply.get("approved").toString().equals("true")) {
//                for (Map.Entry<ChannelId, IClient> entry : ChatClientServer.channelIdClient.entrySet()) {
//                    Client client1 = (Client) entry.getValue();
////                        if (client1.getRoom().getRoomId().equals(reply.get("roomid").toString())){
////                            JoinRoomRequestHandler joinRoomRequestHandler = new JoinRoomRequestHandler(request, client1);
////                            JSONObject reply1 = joinRoomRequestHandler.processRequest();
////                            sendResponse(reply1);
////                            if (joinRoomRequestHandler.isLocalRoomExist(((JoinRoomRequest) request).getRoomId()) && joinRoomRequestHandler.isCurrentOwner()) {
////                                Room newRoom = ChatClientServer.localRoomIdLocalRoom.get(((JoinRoomRequest) request).getRoomId());
////                                Room formerRoom = ((Client) client).getRoom();
////                                ((Client) client).setRoom(newRoom);
////                                broadcast(reply, formerRoom);
////                                broadcast(reply, newRoom);
////                            }
////                        }
//                }
//                // todo: send server request
//            }
//        }
    }

    private boolean isCapable(){
        return (request.getRoomId().equals(getClient().getRoom().getRoomId())
                && ((LocalRoom) getClient().getRoom()).getOwner().equals(getClient().getIdentity()));
    }
}
