package com.liunx.rpc.common.enums;

/**
 * 序列化方式枚举
 */
public enum SerializeType {

    JSON_TYPE((byte) 0),
    BYTE_TYPE((byte) 1);

    private byte type;


    private SerializeType(byte type) {
        this.type = type;
    }

    public static SerializeType fetchType(byte type) {
        SerializeType[] values = values();
        for (SerializeType temp : values) {
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
