package com.liunx.rpc.common.annotations;

import java.lang.annotation.*;

/**
 * 表示需要暴露接口的实现
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceImplement {
}
