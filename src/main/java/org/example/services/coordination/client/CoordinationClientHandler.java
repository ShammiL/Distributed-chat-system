package org.example.services.coordination.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.models.messages.coordination.leader.request.AbstractIdentityRequest;
import org.json.simple.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

public class CoordinationClientHandler extends ChannelInboundHandlerAdapter {

    private final AbstractCoordinationMessage message;
    private final AtomicBoolean requestSent = new AtomicBoolean(false);
    private final AtomicBoolean status;
    public CoordinationClientHandler(AbstractCoordinationMessage message, AtomicBoolean status){
        this.message = message;
        this.status = status;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.writeAndFlush(message);
        if (!(message instanceof AbstractIdentityRequest)){
            ctx.close();
        }
        requestSent.set(true);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if (requestSent.get()) {
            JSONObject object = (JSONObject)msg;
            switch (message.getType()){
                case "identity_release_request":
                    handleIdentityReleaseResponse(
                            (String) object.get("identity"),
                            (String) object.get("identityType"),
                            (String) object.get("status")
                    );
                    break;
                case "identity_reserve_request":
                    handleIdentityReserveResponse(
                            (String) object.get("identity"),
                            (String) object.get("identityType"),
                            (String) object.get("status")
                    );
                    break;
                default:
            }
            ctx.close();
        }
    }

    private void handleIdentityReserveResponse(String identity, String identityType, String status) {
        AbstractIdentityRequest request = (AbstractIdentityRequest) message;
        if (request.getIdentity().equals(identity)
                && request.getIdentityType().equals(identityType) && "success".equals(status)) {
            // Reserve identity successful
            this.status.set(true);
        }
        //TODO: else?
    }

    private void handleIdentityReleaseResponse(String identity, String identityType, String status) {
        AbstractIdentityRequest request = (AbstractIdentityRequest) message;
        if (request.getIdentity().equals(identity)
        && request.getIdentityType().equals(identityType) && "success".equals(status)) {
            // Release identity successful
            this.status.set(true);
        }
        //TODO: else?
    }
}
