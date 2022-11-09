package com.panda.framework.mvc.annotation;

public @interface Bind {

    String[] value() default { "playerId" };

}
