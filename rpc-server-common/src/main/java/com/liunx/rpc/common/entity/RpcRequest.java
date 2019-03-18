package com.liunx.rpc.common.entity;


import com.liunx.rpc.common.enums.RequestType;
import com.liunx.rpc.common.enums.SerializeType;
import com.liunx.rpc.common.utils.CommonUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求对象
 */
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 981996692914202862L;

    //默认的序列化方式,默认为JSON
    protected transient final byte DEFAULT_SERIAL_TYPE = SerializeType.JSON_TYPE.getType();

    //默认的传输类型,默认为equest
    protected transient final byte DEFAULT_REQUEST_TYPE = RequestType.REQUEST_TYPE.getType();

    protected transient final AtomicInteger requestId = new AtomicInteger(1);

    //是否是响应类型
    protected transient boolean isResponse = false;

    //传输类型
    protected transient byte type = DEFAULT_REQUEST_TYPE;

    //序列化方式
    protected transient byte serialType = DEFAULT_SERIAL_TYPE;

    //调用的接口名称
    protected String className;

    //调用的接口方法
    protected String methodName;

    //方法需要的参数
    protected Object[] args;

    private transient int opaque = requestId.getAndIncrement();

    public RpcRequest() {

    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public boolean isResponse() {
        return isResponse;
    }

    public void setResponse(boolean response) {
        isResponse = response;
    }

    public byte getSerialType() {
        return serialType;
    }

    public void setSerialType(byte serialType) {
        this.serialType = serialType;
    }

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    //检查对象是否合法,必须要有序列化类型,传输类型,请求的接口名,请求的方法
    public boolean isValid() {
        return  !(type == 0 || type == 1) || !(serialType == 0 || serialType ==1)
                || CommonUtils.isBlank(className) || CommonUtils.isBlank(methodName);
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "isResponse=" + isResponse +
                ", type=" + type +
                ", serialType=" + serialType +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
