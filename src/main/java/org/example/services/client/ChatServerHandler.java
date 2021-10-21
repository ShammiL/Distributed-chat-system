package org.example.services.client;

import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.example.handlers.requestHandlers.chat.AbstractRequestHandler;
import org.example.handlers.requestHandlers.chat.DeleteRoomRequestHandler;
import org.example.handlers.requestHandlers.chat.QuitRequestHandler;
import org.example.handlers.requestHandlers.chat.RequestHandlerFactory;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.models.messages.chat.requests.chat.DeleteRoomRequest;
import org.example.models.messages.chat.requests.chat.QuitRequest;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;
    IClient client;

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
            if (((QuitRequestHandler)requestHandler).isCurrentOwner()) {
                DeleteRoomRequestHandler deleteRoomRequestHandler = new DeleteRoomRequestHandler(new DeleteRoomRequest(((Client) client).getRoom().getRoomId()), client);
                deleteRoomRequestHandler.handleRequest();
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
