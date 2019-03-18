package com.liunx.rpc.common.entity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 同步等待RpcResponse的执行结果
 */
public class RpcResponseFuture {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private Object result;

    private Throwable cause;

    private boolean sendRequestOK;

    //request和response的唯一标识,同一个request的opaque与response是一致的
    private int opaque;

    public int getOpaque() {
        return opaque;
    }

    public void setOpaque(int opaque) {
        this.opaque = opaque;
    }

    public void setResult(Object result) {
        this.result = result;
        countDownLatch.countDown();
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public void setSendRequestOK(boolean sendRequestOK) {
        this.sendRequestOK = sendRequestOK;
    }

    public Object awaitResult() {
        return awaitResult(200, TimeUnit.MILLISECONDS);
    }

    public Object awaitResult(long timeout, TimeUnit unit) {
        try {
            countDownLatch.await(timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
