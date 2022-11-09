package com.panda.framework.netty2.handler;

import com.panda.framework.netty2.common.PackMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public abstract class PandaPackMessageDecoder extends MessageToMessageDecoder<PackMessage> {

    @Override
    protected final void decode(ChannelHandlerContext ctx, PackMessage msg, List<Object> out) throws Exception {
        // decoder解析匹配的协议包
        if (!validatePackType(msg.getPackageType())) {
            return;
        }

        doDecode(ctx, msg, out);
    }

    protected abstract void doDecode(ChannelHandlerContext ctx, PackMessage msg, List<Object> out) throws Exception;

    protected abstract boolean validatePackType(int packType);

}
