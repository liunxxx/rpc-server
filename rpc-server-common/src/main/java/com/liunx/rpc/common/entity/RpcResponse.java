package com.liunx.rpc.common.entity;

import java.util.Arrays;

/**
 * 响应对象
 */
public class RpcResponse extends RpcRequest {

    //调用的返回结果
    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "isResponse=" + isResponse +
                ", type=" + type +
                ", serialType=" + serialType +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", result=" + result + "}";
    }

}
