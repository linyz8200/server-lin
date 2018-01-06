package com.server.lin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * Created by LinLive on 2018/1/4.
 */
public class ServerHandle  extends ChannelInboundHandlerAdapter {

    protected void handleMercury(ChannelHandlerContext channelHandlerContext, JSONObject j) {
        System.out.println("server 接收数据成功:" + j.toJSONString() );
        channelHandlerContext.writeAndFlush(j.toJSONString());
        System.out.println("server返回发送方数据:"+j.toJSONString());
    }


    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        System.err.println("---client " + ctx.channel().remoteAddress().toString() + " reader timeout, close it---");
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(" exception"+cause.toString());
    }




    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String s = (String)msg;
        JSONObject j = JSON.parseObject(s);
        Integer type = j.getInteger("type");
        if(type == 0){
            //心跳检测
            sendPongMsg(ctx);
            return;
        }else{
            handleMercury(ctx, j);
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("---" + ctx.channel().remoteAddress() + " is inactive---");
    }

    private void sendPongMsg(ChannelHandlerContext context) {
        JSONObject j = new JSONObject();
        j.put("type", 999);
        context.channel().writeAndFlush(j.toJSONString());
        System.out.println("心跳检测返回： " + context.channel().remoteAddress());
    }


    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        System.err.println("---WRITER_IDLE---");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        System.err.println("---ALL_IDLE---");
    }

}
