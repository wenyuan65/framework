package com.wy.panda.annotation;

public enum Cmd {

    Player_getPlayerList(10001, "player@getPlayerList"),
    Player_getPlayerInfo(10002, "player@getPlayerInfo"),

    Rpc_getPlayerFriends(90101, "rpc_friends@getPlayerFriends"),

    ;

    private int code;
    private String command;

    Cmd(int code, String command) {
        this.code = code;
        this.command = command;
    }

    public int getCode() {
        return code;
    }

    public String getCommand() {
        return command;
    }

}
