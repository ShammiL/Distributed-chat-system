package org.example.handlers.requestHandlers.chat;

import io.netty.channel.ChannelId;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.NewIdentityRequest;
import org.example.models.server.ServerState;
import org.example.services.client.ChatClientServer;
import org.example.services.coordination.MessageSender;
import org.json.simple.JSONObject;

import java.util.Map;

public class NewIdentityRequestHandler extends AbstractRequestHandler{
    private final NewIdentityRequest request;
    private boolean approved;
    private String identity;


    public NewIdentityRequestHandler(AbstractChatRequest request, IClient client) {
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
        try {
            approved = approveIdentity(identity);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

    public boolean approveIdentity(String identity) throws InterruptedException {
//        MessageSender.reserveIdentity(
//                ServerState.getInstance().getServerInfoById("s2"),
//                identity,
//                "client"
//        );

//        MessageSender.releaseIdentity(
//                ServerState.getInstance().getServerInfoById("s2"),
//                identity,
//                "client"
//        );
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
