package org.example.services.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.example.handlers.requestHandlers.*;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.reply.ReplyObjects;
import org.example.models.requests.*;
import org.example.models.room.LocalRoom;
import org.example.models.room.Room;
import org.json.simple.JSONObject;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    //    private final Map<ChannelId, IClient> channelIdClient = new ConcurrentHashMap<>();
    private ChannelHandlerContext ctx;
    IClient client;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
//        channelIdClient.put(ctx.channel().id(), new Client());
        Client newClient = new Client();
        newClient.setCtx(ctx);
        ChatClientServer.channelIdClient.put(ctx.channel().id(), newClient);
//        System.out.println("ctx id : "+ctx.channel().id().);


    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
//        channelIdClient.remove(ctx.channel().id());
        System.out.println("channel inactive");
        ChatClientServer.channelIdClient.remove(ctx.channel().id());
//        for (Map.Entry<ChannelId, IClient> entry : ChatClientServer.channelIdClient.entrySet()) {
//            Client clientC = (Client) entry.getValue();
//            System.out.println(clientC.getIdentity());
//        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        AbstractRequest request = (AbstractRequest) msg;
        try {
//            IClient client = channelIdClient.get(ctx.channel().id());
            client = ChatClientServer.channelIdClient.get(ctx.channel().id());
            serveRequest(client, request);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void serveRequest(IClient client, AbstractRequest request)  {
        AbstractRequestHandler requestHandler = RequestHandlerFactory.requestHandler(request, client);
        requestHandler.handleRequest();
        if (requestHandler instanceof QuitRequestHandler) {
            ctx.close();
            ChatClientServer.channelIdClient.remove(ctx.channel().id());
            if (((QuitRequestHandler)requestHandler).isCurrentOwner()) {
                DeleteRoomRequestHandler deleteRoomRequestHandler = new DeleteRoomRequestHandler(new DeleteRoomRequest(((Client) client).getRoom().getRoomId()), (Client) client);
                deleteRoomRequestHandler.handleRequest();
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
        AbstractRequest request = new QuitRequest();
//        IClient client = ChatClientServer.channelIdClient.remove(ctx.channel().id())
        serveRequest(client, request);
        System.out.println("Disconnected");
        ctx.close();

    }
}
