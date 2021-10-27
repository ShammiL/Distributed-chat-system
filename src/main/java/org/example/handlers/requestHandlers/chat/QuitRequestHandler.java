package org.example.handlers.requestHandlers.chat;

import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.QuitRequest;
import org.example.models.room.LocalRoom;
import org.example.services.client.ChatClientServer;
import org.json.simple.JSONObject;

public class QuitRequestHandler extends AbstractRequestHandler {
    private final QuitRequest request;
    private String identity;
    private String roomId;
    private String formerRoomId;

    public QuitRequestHandler(AbstractChatRequest request, IClient client) {
        super((Client) client);
        this.request = (QuitRequest) request;
    }

    @Override
    public JSONObject processRequest() {
        identity = getClient().getIdentity();
        roomId = "";
        formerRoomId = getClient().getRoom().getRoomId();
        return ReplyObjects.roomChange(identity, roomId, formerRoomId);

    }

    @Override
    public void handleRequest() {
        synchronized (this) {
            JSONObject reply = processRequest();
            sendResponse(reply);
            broadcast(reply, getClient().getRoom());


        }

    }

    public Boolean isCurrentOwner() {
        if (!ChatClientServer.localRoomIdLocalRoom.get(formerRoomId).equals(ChatClientServer.getMainHal())) {
            LocalRoom curRoom = (LocalRoom) ChatClientServer.localRoomIdLocalRoom.get(formerRoomId);
            return curRoom.getOwner().equals(getClient().getIdentity());
        }
        return false;
    }
}
