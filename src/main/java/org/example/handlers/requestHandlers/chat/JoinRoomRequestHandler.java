package org.example.handlers.requestHandlers.chat;

import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.JoinRoomRequest;
import org.example.models.room.LocalRoom;
import org.example.models.room.Room;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerInfo;
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
    private String moveJoinHost;
    private String moveJoinPort;
    boolean isGlobalRoomExist = false;

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

        if(isNotCurrentOwner()){
            if(isLocalRoomExist(roomId)){
                return ReplyObjects.roomChange(identity, roomId, formerRoomId);
            }
            else {
//                if room does not exist in local room list check in global list
                try {
                    isGlobalRoomExist = checkGlobalList(roomId);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ConnectException e) {
//                    e.printStackTrace();
                    System.out.println("leader not alive");
//                  TODO ...
                }
                if(isGlobalRoomExist){
//                    if room exist in global room list
                    System.out.println("routeMessage : " + moveJoinHost + " " + moveJoinPort);
                    return ReplyObjects.routeMessage(roomId, moveJoinHost, moveJoinPort);
                } else {
//                    if room does not exist in global room list
                    return ReplyObjects.roomChange(identity, formerRoomId, formerRoomId);
                }
            }

        } else {
//            if client is an owner of a room
            return ReplyObjects.roomChange(identity, formerRoomId, formerRoomId);
        }
    }

    public boolean checkGlobalList(String roomId) throws InterruptedException, ConnectException {

        if(ServerState.getInstance().isCoordinator()){
//            if server is the coordinator
            String roomOwnedServerName;
            if(LeaderState.getInstance().getServerFromRoomId(roomId) != null){
                roomOwnedServerName = LeaderState.getInstance().getServerFromRoomId(roomId);
            }
            else {
                roomOwnedServerName = null;
            }
            if(roomOwnedServerName != null){
                ServerInfo roomOwnedServer = ServerState.getInstance().getServersList().get(roomOwnedServerName);
                moveJoinPort = Integer.toString(roomOwnedServer.getClientPort());
                moveJoinHost = roomOwnedServer.getServerAddress();
                return true;
            }
        }
        else {
//            if server is not the coordinator send requestRoomInfo message to coordinator
            JSONObject response = MessageSender.requestRoomInfo(
                    ServerState.getInstance().getCoordinator(),
                    roomId);

            if(response != null) {
//            if there are no connection errors
                if(!response.isEmpty()){
//                if room contains in global room list
                    moveJoinHost = response.get("host").toString();
                    moveJoinPort = response.get("port").toString();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void handleRequest() {
        synchronized (this) {
            JSONObject reply = processRequest();
            sendResponse(reply);
            System.out.println("Handle request method in join room" + reply.toString());
            if (isLocalRoomExist( request.getRoomId()) && isNotCurrentOwner()) {
                Room newRoom = ChatClientServer.localRoomIdLocalRoom.get( request.getRoomId());
                Room formerRoom = getClient().getRoom();
                getClient().setRoom(newRoom);
                broadcast(reply, formerRoom);
                broadcast(reply, newRoom);
            }
            else if(isNotCurrentOwner() & isGlobalRoomExist){
                JSONObject roomChange = ReplyObjects.roomChange(identity, roomId, formerRoomId);
                broadcast(roomChange, getClient().getRoom());
                ChatClientServer.channelIdClient.remove(getClient().getCtx().channel().id());
            }
        }
    }

    public boolean isLocalRoomExist(String roomId) {
        return ChatClientServer.localRoomIdLocalRoom.containsKey(roomId);
    }

    public Boolean isNotCurrentOwner() {
        if (!ChatClientServer.localRoomIdLocalRoom.get(formerRoomId).equals(ChatClientServer.getMainHal())) {
            LocalRoom curRoom = (LocalRoom) ChatClientServer.localRoomIdLocalRoom.get(formerRoomId);
            return !curRoom.getOwner().equals(getClient().getIdentity());
        }
        return true;
    }
}
