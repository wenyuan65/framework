package com.wy.panda.redis;

import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RedisUtil {

    public static <V> V get(String key) {
        RedissonClient client = RedisManager.getInstance().getClient();
        return (V)client.getBucket(key).get();
    }

    public static <V> void set(String key, V obj) {
        RedissonClient client = RedisManager.getInstance().getClient();
        client.getBucket(key).set(obj);
    }

    public static <V> void set(String key, V obj, long ttl, TimeUnit timeUnit) {
        RedissonClient client = RedisManager.getInstance().getClient();
        client.getBucket(key).set(obj, ttl, timeUnit);
    }

    public static <V> boolean setnx(String key, V obj, long ttl, TimeUnit timeUnit) {
        RedissonClient client = RedisManager.getInstance().getClient();
        return client.getBucket(key).trySet(obj, ttl, timeUnit);
    }

    public static <V> boolean setex(String key, V obj, long ttl, TimeUnit timeUnit) {
        RedissonClient client = RedisManager.getInstance().getClient();
        return client.getBucket(key).setIfExists(obj, ttl, timeUnit);
    }

    public static <V> void mset(Map<String, V> bucketsMap) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RBuckets buckets = client.getBuckets();
        buckets.set(bucketsMap);
    }

    public static <V> Map<String, V> mget(String... keys) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RBuckets buckets = client.getBuckets();
        return buckets.get(keys);
    }

    public static boolean del(String key) {
        RedissonClient client = RedisManager.getInstance().getClient();
        return client.getBucket(key).delete();
    }

    public static long delByPattern(String pattern) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RKeys keys = client.getKeys();
        return keys.deleteByPattern(pattern);
    }

    public static <V> V hget(String key, String field) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RMap<Object, Object> map = client.getMap(key);
        return (V) map.get(field);
    }

    public static <V> V hset(String key, String field, V obj) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RMap<Object, Object> map = client.getMap(key);
        return (V) map.put(field, obj);
    }

    public static <V> Map<String, V> hmget(String key, Set<String> fields) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RMap<String, V> map = client.getMap(key);
        return map.getAll(fields);
    }

    public static <V> void hmset(String key, Map<String, V> fieldMap) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RMap<String, V> map = client.getMap(key);
        map.putAll(fieldMap);
    }

    public static <V> Map<String, V> hgetall(String key) {
        return hgetall(key, null, 10);
    }

    public static <V> Map<String, V> hgetall(String key, String pattern) {
        return hgetall(key, pattern, 10);
    }

    public static <V> Map<String, V> hgetall(String key, String pattern, int count) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RMap<String, V> map = client.getMap(key);

        // readAllMap()方法使用hgetall命令，而entrySet使用hscan命令
        Map<String, V> resultMap = new HashMap<>();
        for (Map.Entry<String, V> entry : map.entrySet(pattern, count)) {
            resultMap.put(entry.getKey(), entry.getValue());
        }

        return resultMap;
    }

    /**
     * 获取所有key
     * @param pattern 获取所有所有key时，pattern传null
     * @param count 获取所有key时，count传 -1
     * @return
     */
    public static Iterable<String> keys(String pattern, int count) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RKeys keys = client.getKeys();

        // 内部实现为scan命令
        if (count == -1) {
            return keys.getKeysByPattern(pattern);
        } else {
            return keys.getKeysByPattern(pattern, count);
        }
    }

    public static boolean exists(String key) {
        RedissonClient client = RedisManager.getInstance().getClient();
        return client.getBucket(key).isExists();
    }

    public static boolean expire(String key, long ttl, TimeUnit timeUnit) {
        RedissonClient client = RedisManager.getInstance().getClient();
        return client.getBucket(key).expire(ttl, timeUnit);
    }

    public static boolean expireAt(String key, long dateTime) {
        RedissonClient client = RedisManager.getInstance().getClient();
        return client.getBucket(key).expire(Instant.ofEpochMilli(dateTime));
    }

    public static long nextId(String key) {
        RedissonClient client = RedisManager.getInstance().getClient();
        return client.getIdGenerator(key).nextId();
    }

    public static long increBy(String key, int delta) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RAtomicLong atomicLong = client.getAtomicLong(key);
        return atomicLong.addAndGet(delta);
    }

    public static RBitSet getBitSet(String name) {
        RedissonClient client = RedisManager.getInstance().getClient();
        return client.getBitSet(name);
    }

    /**
     * 获取限流器,使用方法如下：<br/>
     * RRateLimiter rateLimiter = getRateLimiter("player::22332501");<br/>
     * // 设置限流器：OVERALL(所有实例)/PER_CLIENT(针对单个实例)，最大流速设置：每1秒钟产生20个令牌<br/>
     * rateLimiter.trySetRateAsync(RateType.OVERALL, 20, 1, RateIntervalUnit.SECONDS);<br/>
     * // 获取令牌<br/>
     * rateLimiter.acquire(1);<br/>
     *
     * @param rateLimiter
     * @return
     */
    public static RRateLimiter getRateLimiter(String rateLimiter) {
        RedissonClient client = RedisManager.getInstance().getClient();
        return client.getRateLimiter(rateLimiter);
    }

    /**
     * 发布topic消息
     * @param topic
     * @param message
     * @return
     * @param <M>
     */
    public static <M> long publishTopic(String topic, M message) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RTopic topic1 = client.getTopic(topic);
        return topic1.publish(message);
    }

    /**
     * 订阅topic
     * @param topic
     * @param messageClass
     * @param listener
     * @return
     * @param <M>
     */
    public static <M> int subscribeTopic(String topic, Class<M> messageClass, MessageListener<? extends M> listener) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RTopic topic1 = client.getTopic(topic);

        return topic1.addListener(messageClass, listener);
    }

    /**
     * 创建分布式布隆过滤器
     * @param filterName 过滤器名称
     * @param expectedInsertions 预期插入的数据量
     * @param falseProbability 期望的误差率
     * @return
     * @param <V>
     */
    public static <V> RBloomFilter<V> createBloomFilter(String filterName, long expectedInsertions, double falseProbability) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RBloomFilter<V> bloomFilter = client.getBloomFilter(filterName);
        bloomFilter.tryInit(expectedInsertions, falseProbability);

        return bloomFilter;
    }

    public static <V> RBloomFilter<V> getBloomFilter(String filterName) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RBloomFilter<V> bloomFilter = client.getBloomFilter(filterName);

        return bloomFilter;
    }

    /**
     * pipline批量执行任务
     * @param consumer
     * @return
     */
    public static List<?> batchExecute(Consumer<RBatch> consumer) {
        RedissonClient client = RedisManager.getInstance().getClient();
        RBatch batch = client.createBatch();
        consumer.accept(batch);

        BatchResult<?> batchResult = batch.execute();
        return batchResult.getResponses();
    }

    public static <V> RHyperLogLog<V> getHyperLogLog(String name) {
        RedissonClient client = RedisManager.getInstance().getClient();
        return client.getHyperLogLog(name);
    }

    /**
     * 获取分布式锁
     * @param lockName
     * @return
     */
    public static RLock getLock(String lockName) {
        return RedisManager.getInstance().getClient().getLock(lockName);
    }

    public static RReadWriteLock getReadWriteLock(String lockName) {
        return RedisManager.getInstance().getClient().getReadWriteLock(lockName);
    }

    public static RSemaphore getSemaphore(String semaphoreName) {
        return RedisManager.getInstance().getClient().getSemaphore(semaphoreName);
    }

    public static RCountDownLatch getCountDownLatch(String countDownLatchName) {
        return RedisManager.getInstance().getClient().getCountDownLatch(countDownLatchName);
    }

    public static void main(String[] args) {
        RLock lock = getLock("my_lock");
        lock.lock();
        System.out.println("locking...");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("unlocking...");
            lock.unlock();
        }

    }
	
}
