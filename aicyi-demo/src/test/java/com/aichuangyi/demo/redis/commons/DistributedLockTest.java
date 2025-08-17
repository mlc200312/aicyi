package com.aichuangyi.demo.redis.commons;

import com.aichuangyi.commons.core.lock.DistributedLock;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aicyiframework.redis.lock.RedissonDistributedLock;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class DistributedLockTest extends BaseLoggerTest {

    @SneakyThrows
    @Test
    public void test() {
        System.out.println("Testing Redisson Distributed Lock...");
        DistributedLock lock = new RedissonDistributedLock("myLock", "redis://127.0.0.1:6379");

        try {
            // 尝试获取锁
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                System.out.println("Redisson lock acquired");
                // 模拟业务处理
                Thread.sleep(500);
            } else {
                System.out.println("Failed to acquire Redisson lock");
            }
        } finally {
            lock.unlock();
            System.out.println("Redisson lock released");
            ((RedissonDistributedLock) lock).shutdown();
        }
    }

//    private static void testZkLock() throws InterruptedException {
//        System.out.println("\nTesting ZooKeeper Distributed Lock...");
//        DistributedLock lock = new ZkDistributedLock("127.0.0.1:2181", "/myLock");
//
//        try {
//            // 尝试获取锁
//            if (lock.tryLock(1, TimeUnit.SECONDS)) {
//                System.out.println("ZooKeeper lock acquired");
//                // 模拟业务处理
//                Thread.sleep(500);
//            } else {
//                System.out.println("Failed to acquire ZooKeeper lock");
//            }
//        } finally {
//            lock.unlock();
//            System.out.println("ZooKeeper lock released");
//            ((ZkDistributedLock) lock).close();
//        }
//    }
}
