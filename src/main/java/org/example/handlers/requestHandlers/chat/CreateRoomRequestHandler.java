package org.example.handlers.requestHandlers.chat;


import org.apache.log4j.Logger;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.CreateRoomRequest;
import org.example.models.room.GlobalRoom;
import org.example.models.room.LocalRoom;
import org.example.models.room.Room;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerState;
import org.example.services.client.ChatClientServer;
import org.example.services.UtilService;
import org.example.services.coordination.MessageSender;
import org.json.simple.JSONObject;

import java.net.ConnectException;


public class CreateRoomRequestHandler extends AbstractRequestHandler {
    private final CreateRoomRequest request;
    private boolean approved;
    private String roomId;
    private boolean retried = false;
    private final Logger logger = Logger.getLogger(CreateRoomRequestHandler.class);

    public CreateRoomRequestHandler(AbstractChatRequest request, IClient client) {
        super((Client) client);
        this.request = (CreateRoomRequest) request;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getRoomId() {
        return roomId;
    }

    @Override
    public JSONObject processRequest() {
        roomId = request.getRoomId();
        logger.info("create room request for roomId : " + roomId);
        approved = approveIdentity(roomId);
        return ReplyObjects.createNewRoom(roomId, approved);
    }

    @Override
    public void handleRequest() {
        synchronized (this) {
            JSONObject reply = processRequest();
            if  (!retried) {
                sendResponse(reply);
            }
            if (approved) {
                Room room = new LocalRoom(request.getRoomId(), getClient().getIdentity()); // create a new local room
                Room formerRoom = getClient().getRoom(); // get previous room

                getClient().setRoom(room); // set new room to client
                ChatClientServer.localRoomIdLocalRoom.put(request.getRoomId(),
                        room);// add new room to local list

                // if leader add to global list
                if (ServerState.getInstance().isCoordinator()) {
                    GlobalRoom gRoom = new GlobalRoom(request.getRoomId(), ServerState.getInstance().getServerInfo().getServerId());
                    LeaderState.getInstance().checkAndAddRoom(gRoom);
                }


                JSONObject roomChange = ReplyObjects.roomChange(getClient().getIdentity(),
                        request.getRoomId(), formerRoom.getRoomId());
                sendResponse(roomChange);
                broadcast(roomChange, formerRoom);
            }
        }
    }

    private boolean approveIdentity(String identity) {
        if (validateIdentityValue(identity) && checkIsARoomCreator()) {
            try {
                return checkUniqueIdentity(identity);
            } catch (InterruptedException | ConnectException e) {
                logger.error("error when sending new identity to leader " + e.getMessage());
                // todo: start election and check again
                request.incrementTries();
                retried = ServerState.getInstance().addRetryRequest(request);
                return false;
            }
        } else return false;
    }

    private boolean validateIdentityValue(String identity) {
        if (identity.length() < 3 || identity.length() > 16 || UtilService.isLikeMainHall(identity)) return false;
        else return UtilService.isAlphaNumeric(identity);
    }

    private boolean checkUniqueIdentity(String identity) throws InterruptedException, ConnectException {

        if (validateIdentityValue(identity)) {
            if (ServerState.getInstance().isCoordinator()) {
                return !LeaderState.getInstance().getGlobalRoomList().containsKey(identity);
            } else {
                JSONObject response = MessageSender.reserveIdentity(
                        ServerState.getInstance().getCoordinator(),
                        identity,
                        "room"
                );
                return response.get("reserved").equals("true");
            }

        } else return false;
    }

    private boolean checkIsARoomCreator() {
        if (getClient().getRoom().equals(ChatClientServer.getMainHal())) {
            return true;
        }
        return !((LocalRoom) getClient().getRoom()).getOwner().equals(getClient().getIdentity());
    }

}
