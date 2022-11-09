package com.panda.framework.jdbc.memory.enhance;

import com.panda.framework.compile.JdkCompiler;
import com.panda.framework.jdbc.entity.FieldEntity;
import com.panda.framework.jdbc.entity.TableEntity;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JdkDomainEnhancer extends AbstractDomainEnhancer {

    @Override
    public Class<?> doEnhance(EnhanceContext ctx) {
        Class<?> clazz = ctx.getClazz();
        TableEntity tableEntity = ctx.getTableEntity();
        String clazzSimpleName = clazz.getSimpleName();

        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(ctx.getSuperClassPathName()).append(";\r\n");
        sb.append("import com.wy.panda.jdbc.entity.memory.dynamic.DynamicUpdate;\r\n");
        sb.append("import java.util.Date;\r\n");

        sb.append("class ").append(clazzSimpleName).append(ENHANCED_CLASS_NAME_SUFFIX).append(" extends ").append(clazzSimpleName).append(" implements DynamicUpdate<").append(clazzSimpleName).append("> {\r\n");
        // getDynamicUpdateSQL
        sb.append(buildDynamicUpdateMethod(clazz, tableEntity));
        // clone
        sb.append(buildCloneMethod(clazz, tableEntity));

        sb.append("\t}\r\n}");

        outputEnhancedClass(ctx, sb.toString().getBytes(StandardCharsets.UTF_8), "java");

        try {
            JdkCompiler compiler = new JdkCompiler();
            return compiler.compile(ctx.getEnhancedClazzName(), sb.toString());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private String buildDynamicUpdateMethod(Class<?> clazz, TableEntity tableEntity) {
        String clazzSimpleName = clazz.getSimpleName();
        Field keyField = tableEntity.getKeyField();

        StringBuilder sb = new StringBuilder();
        sb.append("\t@Override\r\n\tpublic String getDynamicUpdateSQL(").append(clazzSimpleName).append(" oldObj, ").append(clazzSimpleName).append(" newObj, , TableEntity tableEntity) {\r\n");

        sb.append("boolean updated = false;");
        sb.append("StringBuilder sb = new StringBuilder();");
        sb.append("sb.append(\"update \").append(tableEntity.getTableName()).append(\" set \");");

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(keyField.getName())) {
                continue;
            }

            FieldEntity fieldEntity = tableEntity.getFieldEntity(field.getName());
            String getterName = fieldEntity.getGetterName();
            String columnName = fieldEntity.getColumnName();
            if (field.getClass().isPrimitive()) {
                sb.append("if (newObj.").append(getterName).append("() != oldObj.").append(getterName).append("()) {\r\n");
                sb.append("sb.append(\"").append(columnName).append("=\").append(newObj.getPlayerId());\r\n");
                sb.append("updated = true;}\r\n");
            } else if (String.class.equals(field.getClass()) || Date.class.equals(field.getClass())) {
                StringBuilder builder = new StringBuilder();
                builder.append("if (newObj.${getterName}() == null && newObj.${getterName}() != oldObj.${getterName}()) {");
                builder.append("sb.append(',').append(\"${columnName}=NULL\");");
                builder.append("updated = true;");
                builder.append("} else if (newObj.${getterName}() != null && !newObj.${getterName}().equals(oldObj.${getterName}())) {");

                if (Date.class.equals(field.getClass())) {
                    builder.append("String value = DateUtil.format(DateUtil.FORMAT_PATTERN_COMMON, newObj.${getterName}());");
                } else {
                    builder.append("String value = newObj.${getterName}();");
                }

                builder.append("sb.append(',').append(\"${columnName}='\").append(value).append(\"'\");");
                builder.append("updated = true;}");

                String methodCode = builder.toString();
                methodCode = methodCode.replaceAll("\\$\\{getterName\\}", getterName);
                methodCode = methodCode.replaceAll("\\$\\{columnName\\}", columnName);

                sb.append(methodCode);
            } else {
                throw new RuntimeException("not support for field type " + field.getName() + " in class " + clazz.getName());
            }
        }

        FieldEntity fieldEntity = tableEntity.getFieldEntity(keyField.getName());
        sb.append("sb.append(\" where id=\").append(newObj.").append(fieldEntity.getGetterName()).append("());");
        sb.append("if (!updated) { return \"\"; } return sb.toString();");

        return sb.toString();
    }

    private String buildCloneMethod(Class<?> clazz, TableEntity tableEntity) {
        String clazzSimpleName = clazz.getSimpleName();
        StringBuilder sb = new StringBuilder();

        sb.append("@Override\npublic ").append(clazzSimpleName).append(" clones() {");
        sb.append(clazzSimpleName).append(" clone = new ").append(clazzSimpleName).append("();");

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            FieldEntity fieldEntity = tableEntity.getFieldEntity(field.getName());
            String getterName = fieldEntity.getGetterName();
            String setterName = fieldEntity.getSetterName();

            sb.append("clone.").append(setterName).append("(this.").append(getterName).append("());");
        }

        sb.append("return clone;}");

        return sb.toString();
    }
}