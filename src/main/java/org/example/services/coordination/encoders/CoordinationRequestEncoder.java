package org.example.services.coordination.encoders;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.services.coordination.serializers.CoordinationRequestSerializer;

import java.nio.charset.StandardCharsets;

public class CoordinationRequestEncoder extends MessageToByteEncoder<AbstractCoordinationMessage> {

    private final Gson gson;

    public CoordinationRequestEncoder() {
        super();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(AbstractCoordinationMessage.class, new CoordinationRequestSerializer());
        gson = gsonBuilder.create();
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, AbstractCoordinationMessage coordinationMessage,
                          ByteBuf byteBuf) throws Exception {
        byte[] json = gson.toJson(coordinationMessage, AbstractCoordinationMessage.class).getBytes(StandardCharsets.UTF_8);
        byteBuf.writeBytes(json);
    }
}
