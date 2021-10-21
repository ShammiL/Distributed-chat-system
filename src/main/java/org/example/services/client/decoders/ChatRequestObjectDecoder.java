package org.example.services.client.decoders;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.example.models.messages.chat.AbstractChatRequest;
import org.example.services.client.deserializers.ChatRequestDeserializer;

import java.util.List;

public class ChatRequestObjectDecoder extends ByteToMessageDecoder {

    private final Gson gson;

    public ChatRequestObjectDecoder() {
        super();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(AbstractChatRequest.class, new ChatRequestDeserializer());
        gson = gsonBuilder.create();
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readableBytes();
        String json = byteBuf.readCharSequence(length, CharsetUtil.UTF_8).toString();
        list.add(gson.fromJson(json, AbstractChatRequest.class));
    }
}
