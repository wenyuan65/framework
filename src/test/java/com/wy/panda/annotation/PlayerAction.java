package com.wy.panda.annotation;

import com.wy.panda.mvc.annotation.Action;
import com.wy.panda.mvc.annotation.Bind;
import com.wy.panda.mvc.annotation.CommandMarker;
import com.wy.panda.mvc.annotation.RequestParam;
import com.wy.panda.rpc.RpcManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Action
public class PlayerAction {

    @Command(Cmd.Player_getPlayerList)
    public void getPlayerList() {
    }

    @Command(Cmd.Player_getPlayerInfo)
    public void getPlayerInfo(@RequestParam("playerId") long playerId) {
    }

    @Bind()
    @Command(Cmd.Player_updatePlayerName)
    public void updatePlayerName(@RequestParam("playerId") long playerId, @RequestParam("name") String name) {
    }

    @Bind({ "clubId" })
    @Command(value = Cmd.Club_apply)
    public void apply(@RequestParam("playerId") long playerId, @RequestParam("clubId") int clubId) {
    }

    @RpcCommand(RpcCmd.getPlayerFriends)
    public Integer getPlayerFriends(int playerId) {
        return 0;
    }

    public <T> T sendRpc(Cmd cmd, int playerId) {
        return (T)RpcManager.getInstance().send(cmd.getCode(), "127.0.0.1", 8080, playerId);
    }

    public static void main(String[] args) {

        try {
            Method method = PlayerAction.class.getDeclaredMethod("apply", long.class, int.class);

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

        int coreThreadPoolSize = 129;
        if ((coreThreadPoolSize & (coreThreadPoolSize - 1)) != 0) {
            int shift = 32 - Integer.numberOfLeadingZeros(coreThreadPoolSize - 1);
            coreThreadPoolSize = 1 << shift;
        }

        System.out.println(coreThreadPoolSize);
    }



}
