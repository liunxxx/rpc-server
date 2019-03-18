package com.liunx.rpc.common.annotations;

import java.lang.annotation.*;

/**
 * 表示需要暴露服务的接口
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceInterface {
}
