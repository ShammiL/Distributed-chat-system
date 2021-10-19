package org.example.handlers.requestHandlers;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelId;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.reply.ReplyObjects;
import org.example.models.requests.AbstractRequest;
import org.example.models.requests.NewIdentityRequest;
import org.example.services.client.ChatClientServer;
import org.json.simple.JSONObject;

import java.util.Map;

public class NewIdentityRequestHandler extends AbstractRequestHandler{
    private NewIdentityRequest request;
    private boolean approved;
    private String identity;


    public NewIdentityRequestHandler(AbstractRequest request, IClient client) {
        super((Client) client);
        this.request = (NewIdentityRequest)  request;
    }


    public boolean isApproved() {
        return approved;
    }

    public String getIdentity() {
        return identity;
    }

    @Override
    public JSONObject processRequest() {
        identity = request.getIdentity();
        System.out.println("identity : "+identity);
        approved = approveIdentity(identity);
        String approvalString;
        if (approved) approvalString = "true";
        else approvalString = "false";
        System.out.println(approvalString);
        return ReplyObjects.newIdentityReply(approvalString);
    }

    @Override
    public void handleRequest() {
        synchronized (this) {
            JSONObject reply = processRequest();
            sendResponse(reply);
            if (approved) {
                getClient().setIdentity(request.getIdentity());
                getClient().setRoom(ChatClientServer.getMainHal());
                JSONObject roomChange = ReplyObjects.newIdRoomChange(getClient().getIdentity());
                sendResponse(roomChange);
                broadcast(roomChange, ChatClientServer.getMainHal());
            }
        }
    }

    public boolean approveIdentity(String identity){
        if (validateIdentityValue(identity)){
            return checkUniqueIdentity(identity);
        }
        else return false;
    }


    public boolean validateIdentityValue(String identity){
        if(identity.length() < 3 || identity.length() > 16) return false;
        else return Character.isAlphabetic(identity.charAt(0));
    }

    public boolean checkUniqueIdentity(String identity){
        for (Map.Entry<ChannelId, IClient> entry : ChatClientServer.channelIdClient.entrySet()) {
            Client client = (Client) entry.getValue();
            if (client.getIdentity().equals(identity)) {
                return false;
            }
        }
        return true;
    }

}
