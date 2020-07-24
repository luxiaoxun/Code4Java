package com.luxx.map.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpVersion;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * Map服务，Netty的Http请求处理
 *
 * @author luxiaoxun
 * @version 1.0
 * @since 2015.06.1
 */
public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {
    private static Logger log = LoggerFactory.getLogger(HttpServerInboundHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            final String uri = request.getUri();
            byte[] responseContent = null;
            try {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
                boolean isKeepAlive = HttpHeaders.isKeepAlive(request);
                MapRequestParam mapRequestParam = getMapRequestParam(uri);
                if (!mapRequestParam.isOk()) {
                    if (!isKeepAlive) {
                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                    } else {
                        response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                        ctx.writeAndFlush(response);
                    }
                } else {
                    responseContent = MapCache.getInstance().getMapCacheTile(mapRequestParam);
                    if (responseContent != null) {
                        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                                Unpooled.wrappedBuffer(responseContent));
                        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "image/jpeg");
                        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
                    }

                    if (!isKeepAlive) {
                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                    } else {
                        response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                        ctx.writeAndFlush(response);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                request.release();
            }
        }
    }

    //检测请求Url是否合法，以下两种情况合法：
    //1: http://192.1.114.11:8899/788865972/{z}/{x}/{y} (http://192.1.114.11:8899/788865972/6/50/25)
    //2: http://192.1.114.11:8899/FileService/image?map=quanguo&type=web&x=5&y=3&z=3
    private MapRequestParam getMapRequestParam(String url) {
        MapRequestParam mapRequestParam = new MapRequestParam();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(url);
        Map<String, List<String>> paramsMap = queryStringDecoder.parameters();
        if (paramsMap.isEmpty()) {
            String[] params = url.split("/");
            if (params != null && params.length == 5) {
                String dbId = params[1];
                mapRequestParam.setDbType(dbId);
                try {
                    int zoom = Integer.parseInt(params[2]);
                    mapRequestParam.setZoom(zoom);
                    int x = Integer.parseInt(params[3]);
                    mapRequestParam.setX(x);
                    int y = Integer.parseInt(params[4]);
                    mapRequestParam.setY(y);
                    mapRequestParam.setOk(true);
                } catch (NumberFormatException e) {
                    log.warn("请求Url:" + url + "不合法，异常：" + e.toString());
                    mapRequestParam.setOk(false);
                }
            }
        } else {
            if (paramsMap.containsKey("z") && paramsMap.containsKey("x")
                    && paramsMap.containsKey("y")) {
                try {
                    List<String> zoomList = paramsMap.get("z");
                    if (zoomList.size() > 0) {
                        mapRequestParam.setZoom(Integer.parseInt(zoomList.get(0)));
                    }
                    List<String> xList = paramsMap.get("x");
                    if (xList.size() > 0) {
                        mapRequestParam.setX(Integer.parseInt(xList.get(0)));
                    }
                    List<String> yList = paramsMap.get("y");
                    if (yList.size() > 0) {
                        mapRequestParam.setY(Integer.parseInt(yList.get(0)));
                    }
                    mapRequestParam.setDbType("788865972");
                    mapRequestParam.setOk(true);
                } catch (NumberFormatException e) {
                    log.warn("请求Url:" + url + "不合法.异常:" + e.toString());
                    mapRequestParam.setOk(false);
                }
            }
        }

        return mapRequestParam;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.debug(cause.getMessage());
        ctx.close();
    }

}
