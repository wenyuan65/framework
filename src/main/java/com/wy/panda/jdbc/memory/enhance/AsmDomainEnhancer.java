package com.wy.panda.jdbc.memory.enhance;

import com.wy.panda.common.ReflactUtil;
import com.wy.panda.jdbc.entity.FieldEntity;
import com.wy.panda.jdbc.entity.TableEntity;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;

public class AsmDomainEnhancer extends AbstractDomainEnhancer {

    @Override
    public Class<?> doEnhance(EnhanceContext ctx) {
        ClassWriter cw = new ClassWriter(0);
        // 定义类名及结构
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, ctx.getEnhancedClassPathName(),  null,
                ctx.getSuperClassPathName(), new String[]{ctx.getInterfacePathName()});
        // 构造函数
        generateConstructor(cw, ctx.getSuperClassPathName());
        // getDynamicUpdateSQL方法及其桥接方法
        generateDynamicUpdateMethod(cw, ctx.getClazz(), ctx.getSuperClassPathName(), ctx.getEnhancedClassPathName(), ctx.getTableEntity());
        // clones方法及其桥接方法
        generateCloneMethod(cw, ctx.getClazz(), ctx.getSuperClassPathName(), ctx.getEnhancedClassPathName(), ctx.getTableEntity());
        cw.visitEnd();

        byte[] data = cw.toByteArray();

        outputEnhancedClass(ctx, data, "class");

        return ctx.getClassLoader().defineClass(ctx.getEnhancedClazzName(), data);
    }

    private static <V> void generateDynamicUpdateMethod(ClassWriter cw, Class<V> clazz, String superClassPathName, String enhancedClassPathName, TableEntity tableEntity) {
        String tableEntityPathName = TableEntity.class.getName().replace('.', '/');
        String methodSigned = String.format("(L%s;L%s;L%s;)%s", superClassPathName, superClassPathName,
                tableEntityPathName, "Ljava/lang/String;");
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getDynamicUpdateSQL", methodSigned, null, null);

        mv.visitCode();
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        mv.visitInsn(Opcodes.DUP);
        mv.visitIntInsn(Opcodes.BIPUSH, 64);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(I)V", false);
        mv.visitVarInsn(Opcodes.ASTORE, 4);  // sb
        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitLdcInsn("UPDATE ");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, tableEntityPathName, "getTableName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn(" SET ");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitInsn(Opcodes.POP);
        // 初始化本地变量
        mv.visitInsn(Opcodes.ICONST_1);
        mv.visitVarInsn(Opcodes.ISTORE, 5);  // isFirst
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitVarInsn(Opcodes.ASTORE, 6);  // oldValue
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitVarInsn(Opcodes.ASTORE, 7);  // newValue
        mv.visitLdcInsn("");
        mv.visitVarInsn(Opcodes.ASTORE, 8);  // columnName

        String keyFieldName = tableEntity.getKeyName();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(keyFieldName)) {
                continue;
            }

            Class<?> type = field.getType();
            if (type.isPrimitive()) {
                buildPrimriyFieldUpdateSqlSegement(mv, superClassPathName, tableEntityPathName, type, field, tableEntity);
            } else {
                buildObjectFieldUpdateSqlSegement(mv, superClassPathName, tableEntityPathName, type, field, tableEntity);
            }
        }

        Label label = new Label();
        mv.visitVarInsn(Opcodes.ILOAD, 5);
        mv.visitJumpInsn(Opcodes.IFEQ, label);
        mv.visitLdcInsn("");
        mv.visitInsn(Opcodes.ARETURN);

        String getterName = tableEntity.getFieldEntity(keyFieldName).getGetterName();
        Class<?> keyFieldType = tableEntity.getKeyField().getType();
        String keyFieldDesc = ReflactUtil.getDesc(keyFieldType);
        String getterMethodDesc = "()" + keyFieldDesc;

        mv.visitLabel(label);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, superClassPathName, getterName, getterMethodDesc, false);
        mv.visitVarInsn(Opcodes.ISTORE, 9);

        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, tableEntityPathName, "getKeyColoumnName", "()Ljava/lang/String;", false);
        mv.visitVarInsn(Opcodes.ASTORE, 10);

        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitLdcInsn(" WHERE ");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(Opcodes.ALOAD, 10);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn("=");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(Opcodes.ILOAD, 9);
        String appendKeyDesc = "(" + keyFieldDesc + ")Ljava/lang/StringBuilder;";
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", appendKeyDesc, false);
        mv.visitInsn(Opcodes.POP);
        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(3, 11);
        mv.visitEnd();

        // 桥接方法
        String bridgeMethodSign = String.format("(Ljava/lang/Object;Ljava/lang/Object;L%s;)Ljava/lang/String;", tableEntityPathName);
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC,
                "getDynamicUpdateSQL", bridgeMethodSign, null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitTypeInsn(Opcodes.CHECKCAST, superClassPathName);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitTypeInsn(Opcodes.CHECKCAST, superClassPathName);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, enhancedClassPathName, "getDynamicUpdateSQL", methodSigned, false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(4, 4);
        mv.visitEnd();
    }

    private static void buildObjectFieldUpdateSqlSegement(MethodVisitor mv, String superClassPathName,
                                                          String tableEntityPathName, Class<?> type, Field field, TableEntity tableEntity) {
        String fieldName = field.getName();
        String getterName = tableEntity.getFieldEntity(fieldName).getGetterName();
        String fieldDesc = ReflactUtil.getDesc(type);
        String getterMethodDesc = "()" + fieldDesc;
//		String typePathName = type.getName().replace('.', '/');

        Label tmpLabel1 = new Label(); // 246
        Label tmpLabel2 = new Label(); // 261
        Label tmpLabel3 = new Label(); // 274
        Label tmpLabel4 = new Label(); // 309
        Label tmpLabel5 = new Label(); // 327
        mv.visitVarInsn(Opcodes.ALOAD, 1);  // oldObj
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, superClassPathName, getterName, getterMethodDesc, false);
        mv.visitVarInsn(Opcodes.ASTORE, 6); // oldValue
        mv.visitVarInsn(Opcodes.ALOAD, 2);  // newObj
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, superClassPathName, getterName, getterMethodDesc, false);
        mv.visitVarInsn(Opcodes.ASTORE, 7);  // newValue

        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitJumpInsn(Opcodes.IFNULL, tmpLabel2);

        mv.visitVarInsn(Opcodes.ALOAD, 6);
        mv.visitJumpInsn(Opcodes.IFNULL, tmpLabel1);
        mv.visitVarInsn(Opcodes.ALOAD, 6);
        mv.visitVarInsn(Opcodes.ALOAD, 7);
//		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, typePathName, "equals", "(Ljava/lang/Object;)Z", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
        mv.visitJumpInsn(Opcodes.IFEQ, tmpLabel2);

        mv.visitLabel(tmpLabel1);
        mv.visitVarInsn(Opcodes.ALOAD, 7);
        mv.visitJumpInsn(Opcodes.IFNULL, tmpLabel5);
        mv.visitVarInsn(Opcodes.ALOAD, 7);
        mv.visitVarInsn(Opcodes.ALOAD, 6);
//		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, typePathName, "equals", "(Ljava/lang/Object;)Z", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
        mv.visitJumpInsn(Opcodes.IFNE, tmpLabel5);

        mv.visitLabel(tmpLabel2);
        mv.visitVarInsn(Opcodes.ILOAD, 5);
        mv.visitJumpInsn(Opcodes.IFNE, tmpLabel3);
        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitIntInsn(Opcodes.BIPUSH, 44);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
        mv.visitInsn(Opcodes.POP);

        mv.visitLabel(tmpLabel3);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitVarInsn(Opcodes.ISTORE, 5);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitLdcInsn(fieldName);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, tableEntityPathName, "getColumnName", "(Ljava/lang/String;)Ljava/lang/String;", false);
        mv.visitVarInsn(Opcodes.ASTORE, 8);
        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitVarInsn(Opcodes.ALOAD, 8);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitInsn(Opcodes.POP);

        mv.visitVarInsn(Opcodes.ALOAD, 7);
        mv.visitJumpInsn(Opcodes.IFNONNULL, tmpLabel4);
        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitLdcInsn("=''");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitInsn(Opcodes.POP);
        mv.visitJumpInsn(Opcodes.GOTO, tmpLabel5);

        mv.visitLabel(tmpLabel4);
        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitLdcInsn("='");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

        String appendParamDesc = String.class == type ? "Ljava/lang/Object;" : fieldDesc;
        mv.visitVarInsn(Opcodes.ALOAD, 7);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(" + appendParamDesc + ")Ljava/lang/StringBuilder;", false);
        mv.visitIntInsn(Opcodes.BIPUSH, 39);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
        mv.visitInsn(Opcodes.POP);
        mv.visitLabel(tmpLabel5);
    }

    private static void buildPrimriyFieldUpdateSqlSegement(MethodVisitor mv, String superClassPathName,
                                                           String tableEntityPathName, Class<?> type, Field field, TableEntity tableEntity) {
        String fieldName = field.getName();
        // getter方法的名称
        String getterName = tableEntity.getFieldEntity(fieldName).getGetterName();
        // 字段类型描述符
        String fieldDesc = ReflactUtil.getDesc(type);
        // getter方法的描述符
        String getterMethodDesc = "()" + fieldDesc;
        // 作为方法唯一入参的参数部分描述
        String methodInputDesc = new StringBuilder(8).append('(').append(fieldDesc).append(')').toString();

        Label tmpLabel1 = new Label(); // 59
        Label tmpLabel2 = new Label(); // 72
        Label tmpLabel3 = new Label(); // 103

        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitJumpInsn(Opcodes.IFNULL, tmpLabel1);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, superClassPathName, getterName, getterMethodDesc, false);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, superClassPathName, getterName, getterMethodDesc, false);

        if (long.class == type) {
            mv.visitInsn(Opcodes.LCMP);
            mv.visitJumpInsn(Opcodes.IFEQ, tmpLabel3);
        } else if (float.class == type) {
            mv.visitInsn(Opcodes.FCMPL);
            mv.visitJumpInsn(Opcodes.IFEQ, tmpLabel3);
        } else if (double.class == type) {
            mv.visitInsn(Opcodes.DCMPL);
            mv.visitJumpInsn(Opcodes.IFEQ, tmpLabel3);
        } else {
            mv.visitJumpInsn(Opcodes.IF_ICMPEQ, tmpLabel3);
        }
        mv.visitLabel(tmpLabel1);

        mv.visitVarInsn(Opcodes.ILOAD, 5);
        mv.visitJumpInsn(Opcodes.IFNE, tmpLabel2);
        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitIntInsn(Opcodes.BIPUSH, 44);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
        mv.visitInsn(Opcodes.POP);
        mv.visitLabel(tmpLabel2);

        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitVarInsn(Opcodes.ISTORE, 5);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitLdcInsn(fieldName);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, tableEntityPathName, "getColumnName", "(Ljava/lang/String;)Ljava/lang/String;", false);
        mv.visitVarInsn(Opcodes.ASTORE, 8);
        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitVarInsn(Opcodes.ALOAD, 8);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

        mv.visitLdcInsn("=");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, superClassPathName, getterName, getterMethodDesc, false);

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", methodInputDesc + "Ljava/lang/StringBuilder;", false);
        mv.visitInsn(Opcodes.POP);
        mv.visitLabel(tmpLabel3);
    }

    private static <V> void generateCloneMethod(ClassWriter cw, Class<V> clazz, String superClassPathName, String enhancedClassPathName, TableEntity tableEntity) {
        // 定义方法
        String methodParamSign = String.format("()L%s;", superClassPathName);
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "clones", methodParamSign, null, null);
        mv.visitCode();
        mv.visitTypeInsn(Opcodes.NEW, superClassPathName);
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassPathName, "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ASTORE, 1);

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            FieldEntity fieldEntity = tableEntity.getFieldEntity(fieldName);
            String getterName = fieldEntity.getGetterName();
            String setterName = fieldEntity.getSetterName();
            String fieldDesc = ReflactUtil.getDesc(field.getType());

            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, superClassPathName, getterName, "()" + fieldDesc, false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, superClassPathName, setterName, "(" + fieldDesc + ")V", false);
        }

        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(3, 2);
        mv.visitEnd();

        // 桥接方法
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC,
                "clones", "()Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, enhancedClassPathName, "clones", methodParamSign, false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private static void generateConstructor(ClassWriter cw, String superClassPathName) {
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superClassPathName, "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

//    private static String generateGetterName(String fieldName) {
//        StringBuilder sb = new StringBuilder();
//        return sb.append("get").append(Character.toUpperCase(fieldName.charAt(0)))
//                .append(fieldName.substring(1)).toString();
//    }
//
//    private static String generateSetterName(String fieldName) {
//        StringBuilder sb = new StringBuilder();
//        return sb.append("set").append(Character.toUpperCase(fieldName.charAt(0)))
//                .append(fieldName.substring(1)).toString();
//    }

    @SuppressWarnings("unused")
    private static String generateGetterMethodDesc(Field field) {
        Class<?> type = field.getType();
        String fieldDesc = ReflactUtil.getDesc(type);
        return "()" + fieldDesc;
    }

}
