package org.example.handlers.requestHandlers.chat;


import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.CreateRoomRequest;
import org.example.models.room.LocalRoom;
import org.example.models.room.Room;
import org.example.services.client.ChatClientServer;
import org.example.services.client.UtilService;
import org.json.simple.JSONObject;


public class CreateRoomRequestHandler extends AbstractRequestHandler {
    private final CreateRoomRequest request;
    private boolean approved;
    private String roomId;

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
        System.out.println("roomId : " + roomId);
        approved = approveIdentity(roomId);
        return ReplyObjects.createNewRoom(roomId, approved);
    }

    @Override
    public void handleRequest() {
        synchronized (this) {
            JSONObject reply = processRequest();
            sendResponse(reply);
            if (approved) {
                Room room = new LocalRoom(request.getRoomId(), getClient().getIdentity()); // create a new local room
                Room formerRoom = getClient().getRoom(); // get previous room

                getClient().setRoom(room); // set new room to client
                ChatClientServer.localRoomIdLocalRoom.put(request.getRoomId(),
                        room); // add new room to local list


                JSONObject roomChange = ReplyObjects.roomChange(getClient().getIdentity(),
                        request.getRoomId(), formerRoom.getRoomId());
                sendResponse(roomChange);
                broadcast(roomChange, formerRoom);
            }
        }
    }

    private boolean approveIdentity(String identity) {
        if (validateIdentityValue(identity) && checkUniqueIdentity(identity) && checkIsARoomCreator()) {
            return checkUniqueIdentity(identity);
        } else return false;
    }

    private boolean validateIdentityValue(String identity) {
        if (identity.length() < 3 || identity.length() > 16) return false;
        else return UtilService.isAlphaNumeric(identity) ;
    }

    private boolean checkUniqueIdentity(String identity) {
        // todo:: Change to global room list
        return !ChatClientServer.localRoomIdLocalRoom.containsKey(identity);
    }

    private boolean checkIsARoomCreator(){
        if (getClient().getRoom().equals(ChatClientServer.getMainHal())){
            return true;
        }
        return !((LocalRoom) getClient().getRoom()).getOwner().equals(getClient().getIdentity());
    }

}
