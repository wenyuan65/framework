package com.panda.framework.enhance;

import com.panda.framework.common.SystemProperty;

public class Enhance {

    public static void main(String[] args) {
        // asm, cglib, jdk
        System.setProperty(SystemProperty.ENHANCE_CLASS_OUTPUT, "true");
        System.setProperty(SystemProperty.ENHANCE_CLASS_NAME, "asm");
    }
}
