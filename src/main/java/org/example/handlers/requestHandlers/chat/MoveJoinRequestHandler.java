package org.example.handlers.requestHandlers.chat;

import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.requests.chat.MoveJoinRequest;
import org.example.models.room.Room;
import org.example.services.client.ChatClientServer;
import org.json.simple.JSONObject;

public class MoveJoinRequestHandler  extends AbstractRequestHandler{
    private final MoveJoinRequest request;
    public MoveJoinRequestHandler(AbstractChatRequest request, IClient client) {
        super((Client) client);
        this.request = (MoveJoinRequest) request;
    }

    @Override
    public JSONObject processRequest() {
        System.out.println(request.getIdentity() + " "
        + request.getRoomId() + ""
                + request.getFormer() + ""
        );
        System.out.println(ReplyObjects.moveJoinServerMessage(true));
        return ReplyObjects.moveJoinServerMessage(true);
    }

    @Override
    public void handleRequest() {
        System.out.println("Move join request handler");
        Room room = null;
        if (ChatClientServer.localRoomIdLocalRoom.containsKey(request.getRoomId())){
            room = ChatClientServer.localRoomIdLocalRoom.get(request.getRoomId());
        }
        else {
            room = ChatClientServer.getMainHal();
        }
        getClient().setRoom(room);
        getClient().setIdentity(request.getIdentity());

        JSONObject roomChange = ReplyObjects.roomChange(request.getIdentity(),
                room.getRoomId(), request.getFormer());

        broadcast(roomChange, room);


        JSONObject reply = processRequest();
        sendResponse(reply);
        sendResponse(roomChange);

    }


}
