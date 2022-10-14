package com.wy.panda.mvc.annotation;

public @interface Bind {

    String[] value() default { "playerId" };

}
