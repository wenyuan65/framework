package com.wy.panda.annotation;

public @interface Bind {

    String[] value() default { "playerId" };

}
