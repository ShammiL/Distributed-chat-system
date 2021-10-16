package org.example.services.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.example.handlers.requestHandlers.CreateRoomRequestHandler;
import org.example.handlers.requestHandlers.JoinRoomRequestHandler;
import org.example.handlers.requestHandlers.MessageRequestHandler;
import org.example.handlers.requestHandlers.NewIdentityRequestHandler;
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
                    ((Client) client).setRoom(ChatClientServer.getMainHal());
                    JSONObject roomChange = ReplyObjects.newIdRoomChange(((Client) client).getIdentity());
                    sendResponse(roomChange);
                    broadcast(roomChange, ChatClientServer.getMainHal());
                }
            }
        } else if (request instanceof CreateRoomRequest) {
            System.out.println("CreateRoomRequest");
            synchronized (this) {
                CreateRoomRequestHandler createRoomRequestHandler = new CreateRoomRequestHandler(request, client);
                JSONObject reply = createRoomRequestHandler.processRequest();
                sendResponse(reply);
                if (createRoomRequestHandler.isApproved()) {
                    Room room = new LocalRoom(((CreateRoomRequest) request).getRoomId(),
                            ((Client) client).getIdentity()); // create a new local room
                    Room formerRoom = ((Client) client).getRoom(); // get previous room

                    ((Client) client).setRoom(room); // set new room to client
                    ChatClientServer.localRoomIdLocalRoom.put(((CreateRoomRequest) request).getRoomId(),
                            room); // add new room to local list


                    JSONObject roomChange = ReplyObjects.roomChange(((Client) client).getIdentity(),
                            ((CreateRoomRequest) request).getRoomId(), formerRoom.getRoomId());
                    sendResponse(roomChange);
                    broadcast(roomChange, formerRoom);
                }
            }
        } else if (request instanceof DeleteRoomRequest) {
            System.out.println("DeleteRoomRequest");


        } else if (request instanceof JoinRoomRequest) {
            System.out.println("JoinRoomRequest");
            synchronized (this) {
                JoinRoomRequestHandler joinRoomRequestHandler = new JoinRoomRequestHandler(request, client);
                JSONObject reply = joinRoomRequestHandler.processRequest();
                sendResponse(reply);
                if (joinRoomRequestHandler.isLocalRoomExist(((JoinRoomRequest) request).getRoomId()) && joinRoomRequestHandler.isCurrentOwner()) {
                    Room newRoom = ChatClientServer.localRoomIdLocalRoom.get(((JoinRoomRequest) request).getRoomId());
                    Room formerRoom = ((Client) client).getRoom();
                    ((Client) client).setRoom(newRoom);
                    broadcast(reply, formerRoom);
                    broadcast(reply, newRoom);
                }
            }


        } else if (request instanceof MoveJoinRequest) {
            System.out.println("MoveJoinRequest");


        } else if (request instanceof ListRequest) {
            System.out.println("ListRequest");


        } else if (request instanceof MessageRequest) {
            System.out.println("MessageRequest");
            MessageRequestHandler messageRequestHandler = new MessageRequestHandler(request, client);
            JSONObject msg = messageRequestHandler.processRequest();
            broadcast(msg, ((Client) client).getRoom());
        } else if (request instanceof QuitRequest) {
            System.out.println("QuitRequest");

        } else if (request instanceof WhoRequest) {
            System.out.println("WhoRequest");
        } else {
            throw new ClassNotFoundException("Request object invalid");
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();
    }

    public void sendResponse(JSONObject reply) {
//        ByteBuf buffer;
//        buffer = Unpooled.copiedBuffer(reply.toJSONString() + "\n", CharsetUtil.UTF_8);

        final ChannelFuture f = ctx.writeAndFlush(reply.toJSONString() + "\n");
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                System.out.println("listner");
            }
        });
    }

    public void broadcast(JSONObject reply, Room room) {
        for (Map.Entry<ChannelId, IClient> entry : ChatClientServer.channelIdClient.entrySet()) {
            Client clientFromMap = (Client) entry.getValue();
            System.out.println("clientFromMap: " + clientFromMap.getIdentity() + " | client: " + ((Client) client).getIdentity());
            if (!clientFromMap.getIdentity().equals(((Client) client).getIdentity()) && clientFromMap.getRoom().equals(room)) {
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
