package org.example.services.client;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;
import org.example.handlers.requestHandlers.chat.AbstractRequestHandler;
import org.example.handlers.requestHandlers.chat.DeleteRoomRequestHandler;
import org.example.handlers.requestHandlers.chat.QuitRequestHandler;
import org.example.handlers.requestHandlers.chat.RequestHandlerFactory;
import org.example.models.client.Client;
import org.example.models.client.GlobalClient;
import org.example.models.client.IClient;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.DeleteRoomRequest;
import org.example.models.messages.chat.requests.chat.QuitRequest;
import org.example.models.room.GlobalRoom;
import org.example.models.server.LeaderState;
import org.example.models.server.ServerState;
import org.example.services.coordination.MessageSender;
import org.json.simple.JSONObject;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;
    IClient client;
    private final Logger logger = Logger.getLogger(ChatServerHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
        Client newClient = new Client();
        newClient.setCtx(ctx);
        ChatClientServer.channelIdClient.put(ctx.channel().id(), newClient);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("channel inactive");
        ChatClientServer.channelIdClient.remove(ctx.channel().id());
//        for (Map.Entry<ChannelId, IClient> entry : ChatClientServer.channelIdClient.entrySet()) {
//            Client clientC = (Client) entry.getValue();
//            System.out.println(clientC.getIdentity());
//        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        AbstractChatRequest request = (AbstractChatRequest) msg;
        try {
            client = ChatClientServer.channelIdClient.get(ctx.channel().id());
            serveRequest(client, request);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void serveRequest(IClient client, AbstractChatRequest request)  {
        AbstractRequestHandler requestHandler = RequestHandlerFactory.requestHandler(request, client);
        requestHandler.handleRequest();
        if (requestHandler instanceof QuitRequestHandler) {
            ctx.close();
            ChatClientServer.channelIdClient.remove(ctx.channel().id());
            deleteFromGlobalClient(((Client) client).getIdentity());
            if (((QuitRequestHandler)requestHandler).isCurrentOwner()) {
                DeleteRoomRequestHandler deleteRoomRequestHandler = new DeleteRoomRequestHandler(new DeleteRoomRequest(((Client) client).getRoom().getRoomId()), client);
                deleteRoomRequestHandler.handleRequest();
            }
        }

    }

    private void deleteFromGlobalClient(String clientId){
        // if leader add to global list
        if(ServerState.getInstance().isCoordinator()){
            LeaderState.getInstance().deleteAClient(clientId);
        }
        else {
            //  send server request
            JSONObject response = null;
            try {
                response = MessageSender.releaseIdentity(
                        ServerState.getInstance().getCoordinator(),
                        clientId,
                        "client"
                );
                System.out.println("releaseIdentity status : " + response.get("released"));
            } catch (InterruptedException e) {
                logger.error("Interrupted exception :" + e.getMessage());
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
        AbstractChatRequest request = new QuitRequest();
//        IClient client = ChatClientServer.channelIdClient.remove(ctx.channel().id())
        serveRequest(client, request);
        System.out.println("Disconnected");
        ctx.close();

    }
}
