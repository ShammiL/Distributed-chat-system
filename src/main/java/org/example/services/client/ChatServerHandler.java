package org.example.services.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.example.handlers.requestHandlers.MessageRequestHandler;
import org.example.handlers.requestHandlers.NewIdentityRequestHandler;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.reply.ReplyObjects;
import org.example.models.requests.*;
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
        ChatClientServer.channelIdClient.put(ctx.channel().id(), new Client());
//        System.out.println("ctx id : "+ctx.channel().id().);


    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
//        channelIdClient.remove(ctx.channel().id());
        ChatClientServer.channelIdClient.remove(ctx.channel().id());

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

    private void serveRequest(IClient client, AbstractRequest request) throws ClassNotFoundException {
        if (request instanceof NewIdentityRequest) {
            //new identity handler
            System.out.println("NewIdentityRequest");
            synchronized (this) {
                NewIdentityRequestHandler newIdentityRequestHandler = new NewIdentityRequestHandler(request, client);
                JSONObject reply = newIdentityRequestHandler.processRequest();
                sendResponse(reply);
                if (newIdentityRequestHandler.isApproved()) {
                    ((Client) client).setIdentity(newIdentityRequestHandler.getIdentity());
                    ((Client) client).setCtx(ctx);
                    JSONObject roomChange = ReplyObjects.newIdRoomChange(((Client) client).getIdentity());
                    sendResponse(roomChange);
                    broadcast(roomChange);
                }
            }
        }
        else if (request instanceof CreateRoomRequest) {
            System.out.println("CreateRoomRequest");

        }
        else if (request instanceof DeleteRoomRequest) {
            System.out.println("DeleteRoomRequest");


        }
        else if (request instanceof JoinRoomRequest) {
            System.out.println("JoinRoomRequest");


        }
        else if (request instanceof MoveJoinRequest) {
            System.out.println("MoveJoinRequest");


        }
        else if (request instanceof ListRequest) {
            System.out.println("ListRequest");


        }
        else if (request instanceof MessageRequest) {
            System.out.println("MessageRequest");
            MessageRequestHandler messageRequestHandler = new MessageRequestHandler(request, client);
            JSONObject msg = messageRequestHandler.processRequest();
            broadcast(msg);
        }
        else if (request instanceof QuitRequest) {
            System.out.println("QuitRequest");

        }
        else if (request instanceof WhoRequest) {
            System.out.println("WhoRequest");
        }
        else {
            throw new ClassNotFoundException("Request object invalid");
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();
    }

    //----------------------------------------------------------------
    public void sendResponse(JSONObject reply) {
        ByteBuf buffer;
        buffer = Unpooled.copiedBuffer(reply.toJSONString() + "\n", CharsetUtil.UTF_8);

        final ChannelFuture f = ctx.writeAndFlush(buffer);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                System.out.println("listner");
            }
        });
    }

    public void broadcast(JSONObject reply){
        for (Map.Entry<ChannelId, IClient> entry : ChatClientServer.channelIdClient.entrySet()) {
            Client clientFromMap = (Client) entry.getValue();
            System.out.println("clientFromMap: " + clientFromMap.getIdentity() + " | client: " + ((Client) client).getIdentity());
            if (!clientFromMap.getIdentity().equals(((Client) client).getIdentity())) {
                ChannelHandlerContext ctxOfClientFromMap = clientFromMap.getCtx();

                ByteBuf buffer;
                buffer = Unpooled.copiedBuffer(reply.toJSONString() + "\n", CharsetUtil.UTF_8);

                final ChannelFuture f = ctxOfClientFromMap.writeAndFlush(buffer);
                f.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        assert f == future;
                        System.out.println("broadcast listner");
                    }
                });

            }
        }

    }
}
