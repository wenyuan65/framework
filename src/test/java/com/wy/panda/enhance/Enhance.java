package com.wy.panda.enhance;

import com.wy.panda.common.SystemProperty;
import com.wy.panda.jdbc.entity.TableEntity;
import com.wy.panda.jdbc.entity.memory.enhance.CGLIBDomainEnhancer;
import com.wy.panda.jdbc.entity.memory.enhance.EnhanceUtil;
import com.wy.panda.jdbc.name.DefaultNameStrategy;

public class Enhance {

    public static void main(String[] args) {
        // asm, cglib, jdk
        System.setProperty(SystemProperty.ENHANCE_CLASS_OUTPUT, "true");
        System.setProperty(SystemProperty.ENHANCE_CLASS_NAME, "asm");
    }
}
