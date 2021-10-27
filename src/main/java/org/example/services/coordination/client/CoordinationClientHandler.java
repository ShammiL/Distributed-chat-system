package org.example.services.coordination.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.leader.reply.GlobalRoomResponse;
import org.example.models.messages.coordination.leader.reply.IdentityReleaseResponse;
import org.example.models.messages.coordination.leader.reply.IdentityReserveResponse;
import org.example.models.messages.coordination.leader.request.AbstractIdentityRequest;
import org.example.models.server.LeaderState;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class CoordinationClientHandler extends ChannelInboundHandlerAdapter {

    private final AbstractCoordinationMessage message;
    private final AtomicBoolean requestSent = new AtomicBoolean(false);
    //    private final AtomicBoolean status;
    private final JSONObject responseObj;
    //    private JSONObject responseObj;
    private final boolean isFireAndForget;

    private static String[] nonFireAndForgetTypes = {
            "identity_reserve_request",
            "identity_release_request",
            "election_start",
            "room_list",
    };

    public CoordinationClientHandler(AbstractCoordinationMessage message, JSONObject responseObj) {
        this.message = message;
        this.responseObj = responseObj;
        isFireAndForget = Arrays.stream(nonFireAndForgetTypes).noneMatch(type -> type.equals(message.getType()));
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.writeAndFlush(message);
        if (isFireAndForget) {
            ctx.close();
        }
        requestSent.set(true);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        System.out.println(msg);
        if (requestSent.get()) {
            AbstractCoordinationMessage response = (AbstractCoordinationMessage) msg;

            switch (response.getType()) {
                case "identity_release_response":
                    handleIdentityReleaseResponse((IdentityReleaseResponse) response);
                    break;
                case "identity_reserve_response":
                    handleIdentityReserveResponse((IdentityReserveResponse) response);
                    break;
                case "room_list_response":
                    handleRoomListResponse((GlobalRoomResponse) msg);
                    break;
                default:
            }
            ctx.close();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleIdentityReserveResponse(IdentityReserveResponse response) {
        System.out.println("handleIdentityReserveResponse");
        AbstractIdentityRequest request = (AbstractIdentityRequest) message;
        this.responseObj.clear();

        if (request.getIdentity().equals(response.getIdentity())
                && request.getIdentityType().equals(response.getIdentityType())
                && "success".equals(response.getStatus())) {
            // Reserve identity successful
//            this.status.set(true);
            this.responseObj.put("reserved", "true");

        } else {
            this.responseObj.put("reserved", "false");

        }
    }

    @SuppressWarnings("unchecked")
    private void handleIdentityReleaseResponse(IdentityReleaseResponse response) {
        AbstractIdentityRequest request = (AbstractIdentityRequest) message;
        this.responseObj.clear();

        System.out.println("handleIdentityReleaseResponse");
        if (request.getIdentity().equals(response.getIdentity())
                && request.getIdentityType().equals(response.getIdentityType())
                && "success".equals(response.getStatus())) {
            // Release identity successful
            this.responseObj.put("released", "true");
        } else {
            this.responseObj.put("released", "false");

        }
    }

    @SuppressWarnings("unchecked")
    private void handleRoomListResponse(GlobalRoomResponse response) {
        System.out.println("handle room list response ");
        System.out.println(response.getGlobalRoomList());
        String jsonString = new Gson().toJson(response);
        this.responseObj.clear();
        this.responseObj.put("room_list", jsonString);
        System.out.println(responseObj);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }
}
