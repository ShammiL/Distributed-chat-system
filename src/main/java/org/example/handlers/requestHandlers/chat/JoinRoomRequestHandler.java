package org.example.handlers.requestHandlers.chat;

import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.JoinRoomRequest;
import org.example.models.room.LocalRoom;
import org.example.models.room.Room;
import org.example.models.server.ServerState;
import org.example.services.client.ChatClientServer;
import org.example.services.coordination.MessageSender;
import org.json.simple.JSONObject;

import java.net.ConnectException;

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

        try {
            checkGlobalList(roomId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
//            e.printStackTrace();
            System.out.println("leader not alive");
//            TODO ...
        }

        if (isLocalRoomExist(roomId) && isCurrentOwner()) {
            return ReplyObjects.roomChange(identity, roomId, formerRoomId);
        }
        else {
            return ReplyObjects.roomChange(identity, formerRoomId, formerRoomId);
        }
        // TODO: do:: check global list

    }

    public void checkGlobalList(String roomId) throws InterruptedException, ConnectException {
        JSONObject response = MessageSender.requestRoomInfo(
                ServerState.getInstance().getCoordinator(),
                roomId);

        if(response != null) {
//            if there are no connection errors
            if(!response.isEmpty()){
//                if room contains in global room list
                String host = response.get("host").toString();
                String port = response.get("port").toString();
                System.out.println("JoinRoomRequestHandler : " + host + " " + port);
            }
        }
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
