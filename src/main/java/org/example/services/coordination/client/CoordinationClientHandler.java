package org.example.services.coordination.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.leader.reply.IdentityReleaseResponse;
import org.example.models.messages.coordination.leader.reply.IdentityReserveResponse;
import org.example.models.messages.coordination.leader.request.AbstractIdentityRequest;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class CoordinationClientHandler extends ChannelInboundHandlerAdapter {

    private final AbstractCoordinationMessage message;
    private final AtomicBoolean requestSent = new AtomicBoolean(false);
    private final AtomicBoolean status;
    private final boolean isFireAndForget;

    private static String[] nonFireAndForgetTypes = {
            "identity_reserve_request",
            "identity_release_request",
            "election_start",
    };

    public CoordinationClientHandler(AbstractCoordinationMessage message, AtomicBoolean status){
        this.message = message;
        this.status = status;
        isFireAndForget = Arrays.stream(nonFireAndForgetTypes).noneMatch(type -> type.equals(message.getType()));
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.writeAndFlush(message);
        if (isFireAndForget){
            ctx.close();
        }
        requestSent.set(true);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if (requestSent.get()) {
            AbstractCoordinationMessage response = (AbstractCoordinationMessage) msg;
            switch (response.getType()){
                case "identity_release_response":
                    handleIdentityReleaseResponse((IdentityReleaseResponse) response);
                    break;
                case "identity_reserve_response":
                    handleIdentityReserveResponse((IdentityReserveResponse)response);
                    break;
                default:
            }
            ctx.close();
        }
    }

    private void handleIdentityReserveResponse(IdentityReserveResponse response) {
        AbstractIdentityRequest request = (AbstractIdentityRequest) message;
        if (request.getIdentity().equals(response.getIdentity())
                && request.getIdentityType().equals(response.getIdentityType()) && "success".equals(response.getStatus())) {
            // Reserve identity successful
            this.status.set(true);
        }
        //TODO: else?
    }

    private void handleIdentityReleaseResponse(IdentityReleaseResponse response) {
        AbstractIdentityRequest request = (AbstractIdentityRequest) message;
        if (request.getIdentity().equals(response.getIdentity())
        && request.getIdentityType().equals(response.getIdentityType())
                && "success".equals(response.getStatus())) {
            // Release identity successful
            this.status.set(true);
        }
        //TODO: else?
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }
}
