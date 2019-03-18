package com.liunx.rpc.common.handlers;

import com.alibaba.fastjson.JSON;
import com.liunx.rpc.common.entity.RpcRequest;
import com.liunx.rpc.common.entity.RpcResponse;
import com.liunx.rpc.common.enums.SerializeType;
import com.liunx.rpc.common.utils.CommonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcRouteEncoder extends MessageToByteEncoder<RpcRequest> {

    private static final SerializeType DEFAULT_SERIAL_TYPE = SerializeType.JSON_TYPE;

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out) throws Exception {
        if (msg == null || msg.isValid()) {
            return;
        }
        byte requestType = msg.getType();
        byte serialType = msg.getSerialType();
        int opaque = msg.getOpaque();
        boolean isResponse = msg.isResponse();
        out.writeByte(requestType);
        out.writeByte(serialType);
        out.writeInt(opaque);
        if (serialType == SerializeType.JSON_TYPE.getType()) {
            String msgJson = JSON.toJSONString(msg);
            int length = msgJson.length();
            out.writeInt(length);
            byte[] msgBytes = msgJson.getBytes();
            out.writeBytes(msgBytes);
        } else if (serialType == SerializeType.BYTE_TYPE.getType()) {
            int length = 0;
            String className = msg.getClassName();
            length += 4 + className.length();
            String methodName = msg.getMethodName();
            length += 4 + methodName.length();
            if (msg.isResponse()) {
                Object resObject = ((RpcResponse)msg).getResult();
                if (resObject == null) {
                    length += 4;
                } else {
                    length += 4 + CommonUtils.objectSerialLength(resObject);
                }
            }
            Object[] args = msg.getArgs();
            if (args != null && args.length > 0) {
                for (Object obj : args) {
                    int argLength = CommonUtils.objectSerialLength(obj);
                    if (argLength > 0) {
                        length += 4 + argLength;
                    }
                }
            }
            out.writeInt(length);
            out.writeInt(className.length());
            out.writeBytes(className.getBytes());
            out.writeInt(methodName.length());
            out.writeBytes(methodName.getBytes());
            if (msg.isResponse()) {
                out.writeInt(CommonUtils.objectSerialLength(((RpcResponse)msg).getResult()));
                CommonUtils.object2Bytes(((RpcResponse)msg).getResult(), out);
            }
            if (args != null && args.length > 0) {
                CommonUtils.array2Bytes(args, out);
            }
        }
    }
}
