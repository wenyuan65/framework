package com.wy.panda.annotation;

import com.wy.panda.mvc.annotation.CommandMarker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PlayerAction {

    @Command(Cmd.Player_getPlayerList)
    public void getPlayerList() {

    }

    @Command(Cmd.Player_getPlayerInfo)
    public void getPlayerInfo() {

    }

    public static void main(String[] args) {

        try {
            Method method = PlayerAction.class.getDeclaredMethod("getPlayerList");

            Annotation[] annotations = method.getDeclaredAnnotations();
            Class<? extends Annotation> annotationType = annotations[0].annotationType();
            CommandMarker annotation = annotationType.getAnnotation(CommandMarker.class);

            Method value = annotationType.getDeclaredMethod("value");
            Object result = value.invoke(annotations[0]);
            System.out.println(result);

            Field codeField = result.getClass().getDeclaredField(annotation.code());
            Field actionField = result.getClass().getDeclaredField(annotation.action());

            codeField.setAccessible(true);
            Object code = codeField.get(result);

            actionField.setAccessible(true);
            Object action = actionField.get(result);

            System.out.println(code);
            System.out.println(action);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }



}
