package org.example.handlers.requestHandlers;


import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.reply.ReplyObjects;
import org.example.models.requests.AbstractRequest;
import org.example.models.requests.CreateRoomRequest;
import org.example.models.room.LocalRoom;
import org.example.services.client.ChatClientServer;
import org.example.services.client.UtilService;
import org.json.simple.JSONObject;


public class CreateRoomRequestHandler extends AbstractRequestHandler {
    private CreateRoomRequest request;
    private boolean approved;
    private String roomId;

    public CreateRoomRequestHandler(AbstractRequest request, IClient client) {
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
