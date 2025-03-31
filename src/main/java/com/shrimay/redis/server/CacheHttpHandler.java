package com.shrimay.redis.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shrimay.redis.service.CacheService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import java.util.Map;

class CacheHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    public CacheHttpHandler(CacheService cacheService, ObjectMapper objectMapper) {
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        
        if (uri.startsWith("/get") && request.method() == HttpMethod.GET) {
            QueryStringDecoder decoder = new QueryStringDecoder(uri);
            Map<String, String> response = cacheService.get(decoder.parameters().get("key").get(0));
            sendResponse(ctx, request, response);
        } else if (uri.equals("/put") && request.method() == HttpMethod.POST) {
            Map<String, String> body = objectMapper.readValue(request.content().toString(io.netty.util.CharsetUtil.UTF_8), Map.class);
            Map<String, String> response = cacheService.put(body.get("key"), body.get("value"));
            sendResponse(ctx, request, response);
        } else {
            sendResponse(ctx, request, Map.of("status", "ERROR", "message", "Invalid endpoint"));
        }
    }

    private void sendResponse(ChannelHandlerContext ctx, FullHttpRequest req, Map<String, String> responseMap) throws Exception {
        String jsonResponse = objectMapper.writeValueAsString(responseMap);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(jsonResponse.getBytes()));
        
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
