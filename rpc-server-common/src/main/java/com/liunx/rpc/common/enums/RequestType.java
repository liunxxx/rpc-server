package com.liunx.rpc.common.enums;

/**
 * 传输类型枚举
 */
public enum RequestType {

    REQUEST_TYPE((byte) 0),
    RESPONSE_TYPE((byte) 1);

    private byte type;

    private RequestType(byte type) {
        this.type = type;
    }

    public static RequestType fetchType(byte type) {
        RequestType[] values = values();
        for (RequestType temp : values) {
            if (temp.type == type) {
                return temp;
            }
        }
        return null;
    }

    public byte getType() {
        return type;
    }
}
