package com.nettymq.server;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nettymq.message.Header;
import com.nettymq.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ToMessageDecoder extends ByteToMessageDecoder {

	private static final Logger log = LoggerFactory
			.getLogger(ToMessageDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext arg0, ByteBuf arg1,
			List<Object> arg2) throws Exception {

		// At least 5 bytes to decode
		if (arg1.readableBytes() >= 5) {
			int msgLength = arg1.readInt();
			byte msgType = arg1.readByte();
			if (msgLength >= 5) {
				ByteBuf bf = arg1.readBytes(msgLength - 5);
				byte[] data = bf.array();
				Header header = new Header();
				header.setMsgLength(msgLength);
				header.setMsgType(msgType);

				Message message = new Message();
				message.setHeader(header);
				message.setData(data);

				arg2.add(message); // Decode one message successfully
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
		log.error(cause.getMessage());
	}
}
