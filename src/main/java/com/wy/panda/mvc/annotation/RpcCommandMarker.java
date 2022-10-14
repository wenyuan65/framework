package com.wy.panda.mvc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Documented
public @interface RpcCommandMarker {

    /**
     * 标记command的序号
     * @return
     */
    String code();

    /**
     * 标记command名称
     * @return
     */
    String action();

}
