package org.example.services.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.example.handlers.requestHandlers.NewIdentityRequestHandler;
import org.example.models.client.Client;
import org.example.models.client.IClient;
import org.example.models.requests.*;
import org.json.simple.JSONObject;



import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    private final Map<ChannelId, IClient> channelIdClient = new ConcurrentHashMap<>();
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.ctx = ctx;
        channelIdClient.put(ctx.channel().id(), new Client());

    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        channelIdClient.remove(ctx.channel().id());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        AbstractRequest request = (AbstractRequest) msg;
        try {
            IClient client = channelIdClient.get(ctx.channel().id());
            serveRequest(client, request);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void serveRequest(IClient client, AbstractRequest request) throws ClassNotFoundException {
        if (request instanceof NewIdentityRequest) {
            //new identity handler
            System.out.println("NewIdentityRequest");
            NewIdentityRequestHandler newIdentityRequestHandler = new NewIdentityRequestHandler(request);
            newIdentityRequestHandler.processRequest();
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
    public void sendResponse() {
        //        JSONObject newIdentity = new JSONObject();
//        newIdentity.put("type", "newidentity");
//        newIdentity.put("approved", "true");
//        final ChannelFuture f = ctx.writeAndFlush(newIdentity);
////        (newIdentity.toJSONString() + "\n").getBytes("UTF-8"))
//        f.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) {
//                assert f == future;
//                System.out.println("listner");
////                ctx.close();
//            }
//
//        });
        JSONObject newIdentity = new JSONObject();
        newIdentity.put("type", "newidentity");
        newIdentity.put("approved", "true");

        ByteBuf buffer;
        buffer = Unpooled.copiedBuffer(newIdentity.toJSONString(), CharsetUtil.UTF_8);


        final ChannelFuture f = ctx.writeAndFlush(buffer);

        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                System.out.println("listner");
                ctx.close();
            }
        });
    }
}
