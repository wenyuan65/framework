package com.panda.framework.rpc.initializer;

import com.panda.framework.rpc.handler.RpcClientHandler;
import com.panda.framework.rpc.handler.RpcRequestCodec;
import com.panda.framework.rpc.handler.RpcResponseCodec;
import com.panda.framework.rpc.serilizable.Serializable;
import com.panda.framework.netty2.initializer.NettyClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class RpcClientInitializer extends NettyClientInitializer {

    private boolean compress;
    private Serializable serializer;

    public RpcClientInitializer(boolean compress, Serializable serializer) {
        this.compress = compress;
        this.serializer = serializer;
    }

    @Override
    public void initBootstrap(Bootstrap bootstrap) {
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
//        bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);
//        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);

        bootstrap.handler(this);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("responseDecoder", new RpcResponseCodec(compress, serializer));
        pipeline.addLast("requestEncoder", new RpcRequestCodec(compress, serializer));
        pipeline.addLast("rpcClientHandler", new RpcClientHandler());
    }
}
