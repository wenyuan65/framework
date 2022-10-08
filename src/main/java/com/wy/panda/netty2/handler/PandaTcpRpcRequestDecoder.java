package com.wy.panda.netty2.handler;

import com.wy.panda.mvc.common.ProtocolType;
import com.wy.panda.mvc.domain.Request;
import com.wy.panda.netty2.common.PackMessage;
import com.wy.panda.netty2.common.PackType;
import com.wy.panda.rpc.RpcRequest;
import com.wy.panda.rpc.RpcRequestParams;
import com.wy.panda.rpc.serilizable.Serializable;
import com.wy.panda.rpc.serilizable.SerializerManager;
import com.wy.panda.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 解析rpc包
 * length 		4字节
 * packageType 	1字节
 * requestId 	4字节
 * command		4字节
 * data			n字节
 * @author wenyuan
 */
public class PandaTcpRpcRequestDecoder extends PandaPackMessageDecoder {

	private boolean rpcCompress;

	public PandaTcpRpcRequestDecoder(boolean rpcCompress) {
		this.rpcCompress = rpcCompress;
	}

	@Override
	protected void doDecode(ChannelHandlerContext ctx, PackMessage msg, List<Object> out) throws Exception {
		ByteBuf packBuf = msg.getPackBuf();

		// data
		ByteBuf dataBuf = Unpooled.buffer(msg.getLength() - 9);
		packBuf.readBytes(dataBuf);

		byte[] contents = dataBuf.array();
		dataBuf.release();
		packBuf.release();

		Serializable serializer = SerializerManager.getInstance().getSerializer();
		if (rpcCompress) {
			contents = ByteUtils.decompress(contents);
		}
		Object result = serializer.readObject(contents);

		RpcRequest request = new RpcRequest();
		request.setRequestId(msg.getRequestId());
		request.setCommand(msg.getCommand());
		request.setParam((RpcRequestParams) result);

		out.add(request);
	}

	@Override
	protected boolean validatePackType(int packType) {
		return packType == PackType.RpcRequest.getPackType();
	}
}
