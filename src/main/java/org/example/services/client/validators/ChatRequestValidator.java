package org.example.services.client.validators;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.models.messages.chat.requests.RequestConstants;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.json.simple.JSONObject;

import java.util.Arrays;

public class ChatRequestValidator extends ChannelInboundHandlerAdapter {

    private static final String[] validMessageTypes = {
            RequestConstants.CREATE_ROOM,
            RequestConstants.DELETE_ROOM,
            RequestConstants.JOIN_ROOM,
            RequestConstants.LIST,
            RequestConstants.MESSAGE,
            RequestConstants.MOVE_JOIN,
            RequestConstants.NEW_IDENTITY,
            RequestConstants.QUIT,
            RequestConstants.WHO
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
