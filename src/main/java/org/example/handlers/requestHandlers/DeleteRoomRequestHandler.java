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

public class DeleteRoomRequestHandler extends AbstractRequestHandler {
    private DeleteRoomRequest request;
    private boolean approved;

    public DeleteRoomRequestHandler(AbstractRequest request, IClient client) {
        super((Client) client);
        this.request = (DeleteRoomRequest) request;
    }

    @Override
    public JSONObject processRequest() {
        this.approved = isCapable();
        return ReplyObjects.deleteRoomClientMessage(request.getRoomId(), approved);
    }

    @Override
    public void handleRequest() {
        synchronized (this) {
            JSONObject reply = processRequest();
            sendResponse(reply);

            if (approved) {
                for (Map.Entry<ChannelId, IClient> entry : ChatClientServer.channelIdClient.entrySet()) {
                    Client clientC = (Client) entry.getValue();
                    if (clientC.getRoom().getRoomId().equals(request.getRoomId())) {
                        JSONObject replyC = clientToMainRoom(clientC);
                        sendResponse(replyC);
                        broadcast(replyC, clientC.getRoom());
                        broadcast(replyC, ChatClientServer.getMainHal());
                        clientC.setRoom(ChatClientServer.getMainHal());

                    }
                }
                ChatClientServer.localRoomIdLocalRoom.remove(request.getRoomId());
            }
            // todo: send server request
        }
    }



    public JSONObject clientToMainRoom(Client clientC) {
        return ReplyObjects.roomChange(clientC.getIdentity(), ChatClientServer.getMainHal().getRoomId(),
                clientC.getRoom().getRoomId());

    }


    private boolean isCapable() {
        return (request.getRoomId().equals(getClient().getRoom().getRoomId())
                && ((LocalRoom) getClient().getRoom()).getOwner().equals(getClient().getIdentity()));
    }
}
