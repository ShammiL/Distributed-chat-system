package org.example.services.coordination.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;

public class CoordinationResponseDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readableBytes();
        String json = byteBuf.readCharSequence(length, CharsetUtil.UTF_8).toString();
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(json);
        list.add(object);
    }
}
