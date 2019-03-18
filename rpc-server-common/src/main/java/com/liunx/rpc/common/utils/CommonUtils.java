package com.liunx.rpc.common.utils;

import com.liunx.rpc.common.annotations.ServiceImplement;
import com.liunx.rpc.common.annotations.ServiceInterface;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommonUtils {

    public static boolean isBlank(String source) {
        if (source != null) {
            for (int i = 0; i < source.length(); i++) {
                if (!Character.isWhitespace(source.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public static Object bytes2Object(ByteBuf buf) {
        if (buf == null || buf.readableBytes() == 0) {
            return null;
        }
        int length = buf.readInt();
        byte magic = buf.readByte();
        Object result = null;
        switch (magic) {
            case 'Z':
                result = buf.readBoolean();
                break;
            case 'C':
                result = buf.readChar();
                break;
            case 'B':
                result = buf.readByte();
                break;
            case 'S':
                result = buf.readShort();
                break;
            case 'I':
                result = buf.readInt();
                break;
            case 'L':
                result = buf.readLong();
                break;
            case 'F':
                result = buf.readFloat();
                break;
            case 'D':
                result = buf.readDouble();
                break;
            case 'G':
                byte[] resultBytes = new byte[length - 1];
                buf.readBytes(resultBytes);
                result = new String(resultBytes);
                break;
            default:
                break;
        }
        return result;
    }

    public static Object[] bytes2Array(ByteBuf buf) {
        if (buf.readableBytes() == 0) {
            return null;
        }
        List<Object> result = new ArrayList<Object>();
        while (buf.readableBytes() > 0) {
            Object object = bytes2Object(buf);
            if (object != null) {
                result.add(object);
            }
        }
        return result.toArray();
    }

    public static int objectSerialLength(Object obj) {
        int length = 0;
        if (obj != null) {
            String className = obj.getClass().getName();
            switch (className) {
                case "java.lang.Boolean":
                case "boolean":
                    length = 2;
                    break;
                case "java.lang.Byte":
                case "byte":
                    length = 2;
                    break;
                case "java.lang.Character":
                case "char":
                    length = 3;
                    break;
                case "java.lang.Short":
                case "short":
                    length = 3;
                    break;
                case "java.lang.Integer":
                case "int":
                    length = 5;
                    break;
                case "java.lang.Long":
                case "long":
                    length = 9;
                    break;
                case "java.lang.Float":
                case "float":
                    length = 5;
                    break;
                case "java.lang.Double":
                case "double":
                    length = 9;
                    break;
                case "java.lang.String":
                    String value = (String) obj;
                    length = 1 + value.length();
                    break;
                default:
                    break;
            }
        }
        return length;
    }

    public static void object2Bytes(Object obj, ByteBuf buf) {
        if (obj == null || buf == null) {
            return;
        }
        String className = obj.getClass().getName();
        switch (className) {
            case "java.lang.Boolean":
            case "boolean":
                buf.writeInt(2);
                buf.writeByte('Z');
                buf.writeBoolean((boolean) obj);
                break;
            case "java.lang.Byte":
            case "byte":
                buf.writeInt(2);
                buf.writeByte('B');
                buf.writeByte((byte) obj);
                break;
            case "java.lang.Character":
            case "char":
                buf.writeInt(3);
                buf.writeByte('C');
                buf.writeChar((char) obj);
                break;
            case "java.lang.Short":
            case "short":
                buf.writeInt(3);
                buf.writeByte('S');
                buf.writeShort((short) obj);
                break;
            case "java.lang.Integer":
            case "int":
                buf.writeInt(5);
                buf.writeByte('I');
                buf.writeInt((int) obj);
                break;
            case "java.lang.Long":
            case "long":
                buf.writeInt(9);
                buf.writeByte('L');
                buf.writeLong((long) obj);
                break;
            case "java.lang.Float":
            case "float":
                buf.writeInt(5);
                buf.writeByte('F');
                buf.writeFloat((float) obj);
                break;
            case "java.lang.Double":
            case "double":
                buf.writeInt(9);
                buf.writeByte('D');
                buf.writeDouble((double) obj);
                break;
            case "java.lang.String":
                String value = (String) obj;
                buf.writeInt(1 + value.length());
                buf.writeByte('G');
                buf.writeBytes(value.getBytes());
                break;
            default:
                break;
        }
    }

    public static void array2Bytes(Object[] objects, ByteBuf buf) {
        if (objects != null && objects.length > 0) {
            for (Object obj : objects) {
                object2Bytes(obj, buf);
            }
        }
    }

    public static Map<String, Object> findClassLocal(final String packageName) {
        URI url = null;
        final ClassLoader classLoader = getClassLoader();
        if (classLoader == null) {
            System.out.println("can not find classLoader");
            return null;
        }
        try {
            url = classLoader.getResource(packageName.replace(".", "/")).toURI();
        } catch (URISyntaxException e) {
            System.out.println("can not find the resources by packageName:" + packageName);
            return null;
        }
        final Map<String, Object> map = new ConcurrentHashMap<String, Object>();
        File file = new File(url);
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    findClassLocal(packageName + "." + pathname.getName());
                }
                if (pathname.getName().endsWith(".class")) {
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(packageName + "." + pathname.getName().replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (clazz.getAnnotation(ServiceImplement.class) != null) {
                        Class<?>[] interfaces = clazz.getInterfaces();
                        for (Class<?> temp : interfaces) {
                            if (temp.getAnnotation(ServiceInterface.class) != null) {
                                try {
                                    map.put(temp.getName(), clazz.newInstance());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        return map;
    }

    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = null;
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        } else {
            classLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return this.getClass().getClassLoader();
                }
            });
        }
        return classLoader;
    }

}
