package com.wy.panda.enhance;

import com.wy.panda.common.SystemProperty;

public class Enhance {

    public static void main(String[] args) {
        // asm, cglib, jdk
        System.setProperty(SystemProperty.ENHANCE_CLASS_OUTPUT, "true");
        System.setProperty(SystemProperty.ENHANCE_CLASS_NAME, "asm");
    }
}
