package com.panda.framework.netty2.common;

public enum PackType {

    CommRequest(0, "通用滚服请求包"),
    GlobalRequest(1, "大区请求包"),
    RpcRequest(2, "rpc请求包"),
    ;

    private int packType;
    private String desc;

    PackType(int packType, String desc) {
        this.packType = packType;
        this.desc = desc;
    }

    public int getPackType() {
        return packType;
    }

    public void setPackType(int packType) {
        this.packType = packType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
