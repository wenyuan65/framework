package com.wy.panda.annotation;


import com.wy.panda.mvc.annotation.CommandMarker;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@CommandMarker(code = "code", action = "command")
public @interface Command {

    Cmd value();

}
