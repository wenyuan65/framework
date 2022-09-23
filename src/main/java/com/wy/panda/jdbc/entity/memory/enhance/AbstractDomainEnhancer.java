package com.wy.panda.jdbc.entity.memory.enhance;

import com.wy.panda.bootstrap.PandaClassLoader;
import com.wy.panda.jdbc.entity.TableEntity;
import com.wy.panda.jdbc.entity.memory.dynamic.DynamicUpdate;

public abstract class AbstractDomainEnhancer implements DomainEnhancer {

    public <V> Class<V> enhance(Class<V> clazz, TableEntity tableEntity) {
        String name = clazz.getName();
        String enhancedClazzName = name + ENHANCED_CLASS_NAME_SUFFIX;
        String interfaceName = DynamicUpdate.class.getName();

        String enhancedClassPathName = enhancedClazzName.replace('.', '/');
        String superClassPathName = name.replace('.', '/');
        String interfacePathName = interfaceName.replace('.', '/');

        EnhanceContext ctx = new EnhanceContext();
        ctx.setClazz(clazz);
        ctx.setTableEntity(tableEntity);
        ctx.setEnhancedClazzName(enhancedClazzName);
        ctx.setEnhancedClassPathName(enhancedClassPathName);
        ctx.setSuperClassPathName(superClassPathName);
        ctx.setInterfacePathName(interfacePathName);
        ctx.setClassLoader((PandaClassLoader) Thread.currentThread().getContextClassLoader());

        return (Class<V>)doEnhance(ctx);
    }

    public abstract Class<?> doEnhance(EnhanceContext ctx);
}
