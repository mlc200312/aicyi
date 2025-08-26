package com.aichuangyi.demo.redis;

import com.aicyiframework.core.cache.CacheConfig;
import com.aicyiframework.core.cache.StringCacheManager;
import com.aichuangyi.demo.AicyiDemoApplication;
import com.aichuangyi.test.domain.BaseLoggerTest;
import com.aicyiframework.data.redis.cache.RedisCacheFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 业务描述
 * @date 11:13
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AicyiDemoApplication.class)
public class RedisCacheManagerTest extends BaseLoggerTest {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    private StringCacheManager<Object> redisCacheManager;

    @Before
    public void before() {
        redisCacheManager = new RedisCacheFactory(redisConnectionFactory).createCache("cache", CacheConfig.defaultConfig());
    }

    @Test
    public void test() {
        redisCacheManager.put("test_put", "tes put");
        Object testPut = redisCacheManager.get("test_put");

        assert testPut.equals("tes put");

        boolean containsTestPut = redisCacheManager.containsKey("test_put");

        assert containsTestPut == true;

        redisCacheManager.expire("test_put", 1 * 60L, TimeUnit.MINUTES);
        long expire = redisCacheManager.getExpire("test_put", TimeUnit.SECONDS);

        assert expire <= 60 * 60 && expire >= 60 * 60 - 10;

        redisCacheManager.remove("test_put");
        Object removeTestPut = redisCacheManager.get("test_put");

        assert removeTestPut == null;

        boolean containsRemoveTestPut = redisCacheManager.containsKey("test_put");

        assert containsRemoveTestPut == false;

        Object testPut01 = redisCacheManager.get("test_put_01", key -> {
            System.out.println("load key: " + key);
            return "haha haha";
        });

        assert testPut01 == "haha haha";

        redisCacheManager.put("test_put_02", "tes put 02");
        Object testPut02 = redisCacheManager.getAndRemove("test_put_02");

        assert testPut02.equals("tes put 02");

        boolean containsTestPut02 = redisCacheManager.containsKey("test_put_02");

        assert containsTestPut02 == false;

        redisCacheManager.put("test_put_03", "tes put 03");
        Object testPut03 = redisCacheManager.getAndReplace("test_put_03", "tes put 04");

        assert testPut03.equals("tes put 03");

        Object newTestPut03 = redisCacheManager.get("test_put_03");

        assert newTestPut03.equals("tes put 04");

        log("test", testPut, containsTestPut, expire, removeTestPut, containsRemoveTestPut, testPut01, testPut02, containsTestPut02, testPut03, newTestPut03);
    }

    @Test
    public void testPutAll() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("test_put_all_01", "test put all 01");
        map.put("test_put_all_02", "test put all 02");
        map.put("test_put_all_03", "test put all 03");

        redisCacheManager.putAll(map);
        Map<String, Object> testPutAll = redisCacheManager.getAll(map.keySet());

        assert testPutAll.size() == 3;

        Set<String> keys = redisCacheManager.keys();

        long size = redisCacheManager.size();

        redisCacheManager.removeAll(map.keySet());
        Map<String, Object> removeTestPutAll = redisCacheManager.getAll(map.keySet());

        assert removeTestPutAll == null || removeTestPutAll.size() == 0;

        Set<String> removeKeys = redisCacheManager.keys();

        long removeSize = redisCacheManager.size();

        log("testPutAll", testPutAll, keys, size, removeTestPutAll, removeKeys, removeSize);
    }
}
