package com.luxx.mq.handler;

import java.util.List;

import com.luxx.mq.message.Header;
import com.luxx.mq.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // At least 5 bytes to decode
        if (in.readableBytes() < 5) {
            return;
        }

        in.markReaderIndex();
        int msgLength = in.readInt();
        if (in.readableBytes() < msgLength) {
            in.resetReaderIndex();
            return;
        }

        byte msgType = in.readByte();
        if (msgLength >= 5) {
            ByteBuf bf = in.readBytes(msgLength - 5);
            byte[] data = bf.array();
            Header header = new Header();
            header.setMsgLength(msgLength);
            header.setMsgType(msgType);

            Message message = new Message();
            message.setHeader(header);
            message.setData(data);

            out.add(message); // Decode one message successfully
        }
    }
}
