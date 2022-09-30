package com.wy.panda.jdbc.memory.enhance;

import com.wy.panda.bootstrap.PandaClassLoader;
import com.wy.panda.jdbc.entity.TableEntity;
import com.wy.panda.jdbc.memory.dynamic.DynamicUpdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public void outputEnhancedClass(EnhanceContext ctx, byte[] data, String suffix) {
        if (!EnhanceUtil.outputEnhancedClass) {
            return;
        }

        File file = new File(ctx.getEnhancedClassPathName() + "." + suffix);
        try (FileOutputStream fos = new FileOutputStream(file);) {
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
