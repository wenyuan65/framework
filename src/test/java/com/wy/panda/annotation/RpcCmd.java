package com.wy.panda.annotation;

public enum RpcCmd {

    getPlayerFriends(90101, "rpc_friends@getPlayerFriends"),
    ;

    private int code;
    private String command;

    RpcCmd(int code, String command) {
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
