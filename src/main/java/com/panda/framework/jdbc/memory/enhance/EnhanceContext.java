package com.panda.framework.jdbc.memory.enhance;

import com.panda.framework.bootstrap.PandaClassLoader;
import com.panda.framework.jdbc.entity.TableEntity;

public class EnhanceContext {

    private PandaClassLoader classLoader;
    private Class<?> clazz;
    private String enhancedClazzName;
    private String enhancedClassPathName;
    private String superClassPathName;
    private String interfacePathName;

    private TableEntity tableEntity;

    public PandaClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(PandaClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getEnhancedClazzName() {
        return enhancedClazzName;
    }

    public void setEnhancedClazzName(String enhancedClazzName) {
        this.enhancedClazzName = enhancedClazzName;
    }

    public String getEnhancedClassPathName() {
        return enhancedClassPathName;
    }

    public void setEnhancedClassPathName(String enhancedClassPathName) {
        this.enhancedClassPathName = enhancedClassPathName;
    }

    public String getSuperClassPathName() {
        return superClassPathName;
    }

    public void setSuperClassPathName(String superClassPathName) {
        this.superClassPathName = superClassPathName;
    }

    public String getInterfacePathName() {
        return interfacePathName;
    }

    public void setInterfacePathName(String interfacePathName) {
        this.interfacePathName = interfacePathName;
    }

    public TableEntity getTableEntity() {
        return tableEntity;
    }

    public void setTableEntity(TableEntity tableEntity) {
        this.tableEntity = tableEntity;
    }
}
