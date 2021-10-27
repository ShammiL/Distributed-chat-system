package org.example.services.coordination.validators;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.json.simple.JSONObject;

import java.util.Arrays;

public class CoordinationMessageValidator extends ChannelInboundHandlerAdapter {

    private static final String[] validMessageTypes = {
            "coordinatorinformation",
            "electionanswer",
            "electioncoordinator",
            "electionstart",
            "heartbeat",
            "identity_release_request",
            "identity_reserve_request"
    };

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);

        AbstractCoordinationMessage message = (AbstractCoordinationMessage) msg;
        if (Arrays.stream(validMessageTypes).anyMatch(type -> type.equals(message.getType()))) {
            ctx.fireChannelRead(message);
        } else {
            JSONObject errorMsg = new JSONObject();
            errorMsg.put("status", "error");
            errorMsg.put("message", "Invalid message type");
            ctx.writeAndFlush(errorMsg.toJSONString()).sync();
            ctx.close();
        }
    }
}
