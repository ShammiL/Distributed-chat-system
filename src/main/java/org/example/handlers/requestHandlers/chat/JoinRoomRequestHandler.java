package org.example.handlers.requestHandlers.chat;

import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.JoinRoomRequest;
import org.example.models.room.LocalRoom;
import org.example.models.room.Room;
import org.example.services.client.ChatClientServer;
import org.json.simple.JSONObject;

public class JoinRoomRequestHandler extends AbstractRequestHandler{
    private final JoinRoomRequest request;
    private String identity;
    private String roomId;
    private String formerRoomId;

    public JoinRoomRequestHandler(AbstractChatRequest request, IClient client) {
        super((Client) client);
        this.request = (JoinRoomRequest) request;
    }

    @Override
    public JSONObject processRequest() {
        identity = getClient().getIdentity();
        roomId = request.getRoomId();
        formerRoomId = getClient().getRoom().getRoomId();
        System.out.println("room to join : " + roomId);
        if (isLocalRoomExist(roomId) && isCurrentOwner()) {
            return ReplyObjects.roomChange(identity, roomId, formerRoomId);
        }
        else {
            return ReplyObjects.roomChange(identity, formerRoomId, formerRoomId);
        }
        // TODO: do:: check global list
    }

    @Override
    public void handleRequest() {
        synchronized (this) {
            JSONObject reply = processRequest();
            sendResponse(reply);
            if (isLocalRoomExist( request.getRoomId()) && isCurrentOwner()) {
                Room newRoom = ChatClientServer.localRoomIdLocalRoom.get( request.getRoomId());
                Room formerRoom = getClient().getRoom();
                getClient().setRoom(newRoom);
                broadcast(reply, formerRoom);
                broadcast(reply, newRoom);
            }
        }
    }

    public boolean isLocalRoomExist(String roomId) {
        return ChatClientServer.localRoomIdLocalRoom.containsKey(roomId);
    }

    public Boolean isCurrentOwner() {
        if (!ChatClientServer.localRoomIdLocalRoom.get(formerRoomId).equals(ChatClientServer.getMainHal())) {
            LocalRoom curRoom = (LocalRoom) ChatClientServer.localRoomIdLocalRoom.get(formerRoomId);
            return !curRoom.getOwner().equals(getClient().getIdentity());
        }
        return true;
    }
}