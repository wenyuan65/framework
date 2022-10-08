package com.wy.panda.annotation;


import com.wy.panda.mvc.annotation.RpcCommandMarker;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@RpcCommandMarker(code = "code", action = "command")
public @interface RpcCommand {

    RpcCmd value();

}
