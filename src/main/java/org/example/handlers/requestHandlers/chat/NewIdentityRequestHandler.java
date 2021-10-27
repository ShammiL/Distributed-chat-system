package org.example.handlers.requestHandlers.chat;

import io.netty.channel.ChannelId;
import org.example.models.client.Client;
import org.example.models.client.GlobalClient;
import org.example.models.client.IClient;
import org.example.models.messages.chat.reply.ReplyObjects;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.NewIdentityRequest;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerState;
import org.example.services.client.ChatClientServer;
import org.example.services.coordination.MessageSender;
import org.json.simple.JSONObject;

import java.net.ConnectException;
import java.util.Map;

public class NewIdentityRequestHandler extends AbstractRequestHandler{
    private final NewIdentityRequest request;
    private boolean approved;
    private String identity;

    // todo: add client to global list

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
                // if is leader add to global client list
                if (ServerState.getInstance().isCoordinator()){
                    GlobalClient gClient = new GlobalClient(request.getIdentity(),
                            ServerState.getInstance().getServerInfo().getServerId());
                    LeaderState.getInstance().checkAndAddClient(gClient);
                }
                JSONObject roomChange = ReplyObjects.newIdRoomChange(getClient().getIdentity(),
                        ChatClientServer.getMainHal().getRoomId());
                sendResponse(roomChange);
                broadcast(roomChange, ChatClientServer.getMainHal());
            }
        }
    }

    public boolean approveIdentity(String identity) throws InterruptedException{

        if (validateIdentityValue(identity)){
            if (ServerState.getInstance().isCoordinator()){
                return !checkUniqueIdentity(identity);
                // todo: add client to global list
            }
            else{
                JSONObject response =  MessageSender.reserveIdentity(
                        ServerState.getInstance().getServerInfoById(
                                ServerState.getInstance().getCoordinator().getServerId()
                        ),
                        identity,
                        "client"
                );
                System.out.println("reserveIdentity status : " + response.get("reserved"));
//
//                JSONObject response2 =  MessageSender.releaseIdentity(
//                        ServerState.getInstance().getServerInfoById(
//                                ServerState.getInstance().getCoordinator().getServerId()
//                        ),
//                        identity,
//                        "client"
//                );
//                System.out.println("releaseIdentity status : " + response2.get("released"));
                return  response.get("reserved").equals("true");
            }

        }
        else return false;
    }


    public boolean validateIdentityValue(String identity){
        if(identity.length() < 3 || identity.length() > 16) return false;
        else return Character.isAlphabetic(identity.charAt(0));
    }

    public boolean checkUniqueIdentity(String identity){
        return LeaderState.getInstance().getGlobalClientList().containsKey(identity);
    }

}
