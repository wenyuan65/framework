package com.wy.panda.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisManager {

    private static final RedisManager instance = new RedisManager();

    private boolean connected = false;

    private RedissonClient client;

    private RedisManager() {
    }

    public static RedisManager getInstance() {
        return instance;
    }

    public void init(Config config) {
        client = Redisson.create(config);

        connected = true;
    }

    public RedissonClient getClient() {
        if (!connected) {
            throw new RuntimeException("redis un-connected");
        }

        return client;
    }

    public static void main(String[] args) {
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE);
        config.useSingleServer()
                .setAddress("127.0.0.1:6799")
                .setPassword("123456")
                .setConnectionMinimumIdleSize(4)
                .setDatabase(0);
        RedisManager.getInstance().init(config);
    }

}
