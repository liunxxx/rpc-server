package com.liunx.rpc.common.handlers;

import com.alibaba.fastjson.JSON;
import com.liunx.rpc.common.entity.RpcRequest;
import com.liunx.rpc.common.entity.RpcResponse;
import com.liunx.rpc.common.enums.RequestType;
import com.liunx.rpc.common.enums.SerializeType;
import com.liunx.rpc.common.utils.CommonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 自定义解码器
 */
public class RpcRouteDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 10) {
            return;
        }
        byte requestType = in.readByte();
        byte serialType = in.readByte();
        int opaque = in.readInt();
        if (serialType == SerializeType.JSON_TYPE.getType()) {
            int length = in.readInt();
            byte[] jsonBytes = new byte[length];
            in.readBytes(jsonBytes);
            if (requestType == RequestType.REQUEST_TYPE.getType()) {
                RpcRequest request = JSON.parseObject(jsonBytes, RpcRequest.class);
                out.add(request);
            } else if (requestType == RequestType.RESPONSE_TYPE.getType()) {
                RpcResponse response = JSON.parseObject(jsonBytes, RpcResponse.class);
                out.add(response);
            }
        } else if (serialType == SerializeType.BYTE_TYPE.getType()) {
            int length = in.readInt();
            int classNameLength = in.readInt();
            byte[] classNameBytes = new byte[classNameLength];
            in.readBytes(classNameBytes);
            String className = new String(classNameBytes);
            int methodNameLength = in.readInt();
            byte[] methodNameBytes = new byte[methodNameLength];
            in.readBytes(methodNameBytes);
            String methodName = new String(methodNameBytes);
            boolean isResponse = false;
            Object result = null;
            Object[] args = null;
            if (requestType == RequestType.RESPONSE_TYPE.getType()) {
                isResponse = true;
                int resultLength = in.readInt();
                if (resultLength > 0) {
                    result = CommonUtils.bytes2Object(in);
                }
            }
            if (in.readableBytes() > 0) {
                args = CommonUtils.bytes2Array(in);
            }
            if (isResponse) {
                RpcResponse response = new RpcResponse();
                response.setClassName(className);
                response.setMethodName(methodName);
                response.setArgs(args);
                response.setResult(result);
                response.setOpaque(opaque);
                out.add(response);
            } else {
                RpcRequest request = new RpcRequest();
                request.setClassName(className);
                request.setMethodName(methodName);
                request.setArgs(args);
                request.setOpaque(opaque);
                out.add(request);
            }
        }
    }
}
