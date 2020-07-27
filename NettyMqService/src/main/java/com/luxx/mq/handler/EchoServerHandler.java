package com.luxx.mq.handler;

import com.luxx.mq.server.MqSender;
import com.luxx.mq.message.Message;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler implementation for the echo server.
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(EchoServerHandler.class);

    public static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private MqSender mqSender;

    public EchoServerHandler(MqSender mqSender) {
        this.mqSender = mqSender;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            // ByteBuf in = (ByteBuf) msg;
            // String data = in.toString(io.netty.util.CharsetUtil.UTF_8);
            Message message = (Message) (msg);

            // Receive message from client
            // Send message to rabbit MQ who wants to subscribe
            String dataString = new String(message.getData(), CharsetUtil.UTF_8);
            mqSender.send(dataString);

            // Echo server: send back the msg to client (just for test)
            log.debug(String.format("Receive message: %s", dataString));
            ctx.writeAndFlush(Unpooled.copiedBuffer(message.getData()));
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // A closed channel will be removed from ChannelGroup automatically
        channels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // System.out.println("Disconnected client "+ctx.channel().remoteAddress());
        log.debug("Disconnected client " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.warn(cause.getMessage());
    }

}
