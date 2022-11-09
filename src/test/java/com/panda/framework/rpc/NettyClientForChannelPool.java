package com.panda.framework.rpc;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class NettyClientForChannelPool {
    final EventLoopGroup group = new NioEventLoopGroup();
    final Bootstrap strap = new Bootstrap();
    InetSocketAddress addr1 = new InetSocketAddress("127.0.0.1", 8080);
    InetSocketAddress addr2 = new InetSocketAddress("127.0.0.1", 8888);

    ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;
    
    public void build() throws Exception {
        strap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(strap.remoteAddress(key), new NettyChannelPoolHandler(), 2);
            }
        };
    }

    public static void main(String[] args) throws Exception {
    	NettyClientForChannelPool client = new NettyClientForChannelPool();
        client.build();
        final String ECHO_REQ = "Hello Netty.$_";
        for (int i = 0; i < 10; i++) {
            // depending on when you use addr1 or addr2 you will get different pools.
            final SimpleChannelPool pool = client.poolMap.get(client.addr1);
            Future<Channel> f = pool.acquire();
            f.addListener((FutureListener<Channel>) f1 -> {
                if (f1.isSuccess()) {
                    Channel ch = f1.getNow();
                    ch.writeAndFlush(ECHO_REQ);
                    // Release back to pool
                    pool.release(ch);
                }
            });
        }
    }
}

class NettyClientHander extends ChannelInboundHandlerAdapter {
    static AtomicInteger count = new AtomicInteger(1);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(count.getAndIncrement() + ":" + msg);
    }
}

class NettyChannelPoolHandler implements ChannelPoolHandler {
    @Override
    public void channelReleased(Channel ch) throws Exception {
        System.out.println("channelReleased. Channel ID: " + ch.id());
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        System.out.println("channelAcquired. Channel ID: " + ch.id());
    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
        System.out.println("channelCreated. Channel ID: " + ch.id());
        SocketChannel channel = (SocketChannel) ch;
        channel.config().setKeepAlive(true);
        channel.config().setTcpNoDelay(true);
        channel.pipeline()
                .addLast(new DelimiterBasedFrameDecoder(1024, delimiter))
                .addLast(new StringDecoder()).addLast(new StringEncoder()).addLast(new NettyClientHander());

    }
}
