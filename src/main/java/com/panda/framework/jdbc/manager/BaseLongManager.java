package com.panda.framework.jdbc.manager;

public abstract class BaseLongManager<K, V> extends BaseManager<K, V> {

    @Override
    public void loadPlayerData(int playerId) {}
    @Override
    public void clearPlayerData(int playerId) {}

    public abstract void loadPlayerData(long playerId);

    public abstract void clearPlayerData(long playerId);

}
