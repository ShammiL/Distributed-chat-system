package org.example.services.coordination.decoders;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.example.models.messages.coordination.AbstractCoordinationMessage;
import org.example.services.coordination.deserializers.CoordinationRequestDeserializer;

import java.util.List;

public class CoordinationRequestDecoder extends ByteToMessageDecoder {

    private final Gson gson;

    public CoordinationRequestDecoder() {
        super();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(AbstractCoordinationMessage.class, new CoordinationRequestDeserializer());
        gson = gsonBuilder.create();
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readableBytes();
        String json = byteBuf.readCharSequence(length, CharsetUtil.UTF_8).toString();
        list.add(gson.fromJson(json, AbstractCoordinationMessage.class));
    }
}
