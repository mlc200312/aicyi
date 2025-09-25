package io.github.aicyi.test.util;

import io.github.aicyi.commons.logging.Logger;
import io.github.aicyi.commons.logging.LoggerFactory;
import io.github.aicyi.commons.util.DateTimeUtils;
import io.github.aicyi.commons.core.lock.DistributedLock;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 功能描述
 * @date 2019-06-13
 **/
public class AicyiFactory extends ThreadFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(AicyiFactory.class);
    private static final ThreadLocal<Long> TIME_THREAD_LOCAL = new ThreadLocal<>();
    public static int PRODUCT_COUNT = 0;

    private String name;
    private Robot robot;

    public AicyiFactory(int count, String name, Robot robot) {
        super(count);
        this.name = name;
        this.robot = robot;
    }

    public AicyiFactory(int count, Robot robot) {
        this(count, "Aicyi", robot);
    }

    public String getName() {
        return name;
    }

    /**
     * z
     * 运作
     */
    public void startRun() {
        long startTime = System.currentTimeMillis();

        TIME_THREAD_LOCAL.set(startTime);

        LOGGER.info("开始工作时间:{}", LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        int workerCount = getMaxThreads();

        LOGGER.info("【{}】工厂共{}人", this.getName(), workerCount);

        for (int i = 0; i < workerCount; i++) {

            submitTask(new Worker(String.format("%06d", i), RandomGenerator.generateFullName(), robot));
        }
    }

    /**
     * 停止运作
     */
    public void stopRun() {
        try {
            //判断是否还有线程工作
            while (getActiveThreadCount() > 0) {
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    LOGGER.error(e, "fail to sleep");
                    Thread.currentThread().interrupt();
                }
            }
            LOGGER.info("【{}】工厂停工，共生产了{}件商品！", this.getName(), AicyiFactory.PRODUCT_COUNT);
            LOGGER.info("结束工作时间:{}，工作时长（ms）：{}", DateTimeUtils.formatLDateTime(LocalDateTime.now()), (System.currentTimeMillis() - TIME_THREAD_LOCAL.get()));
        } finally {
            TIME_THREAD_LOCAL.remove();
            shutdownNow();
        }
    }

    public static String product(Robot robot) {
        int count = AicyiFactory.PRODUCT_COUNT;

        String work = robot.working();

        AicyiFactory.PRODUCT_COUNT = count + 1;

        return work;
    }

    public static class Worker implements Runnable {

        private String userNo;
        private String name;
        private Robot robot;

        public Worker(String userNo, String name, Robot robot) {
            this.userNo = userNo;
            this.name = name;
            this.robot = robot;
        }

        public void work() {

            logWork("开始干活了");

            DistributedLock distributedLock = this.robot.getLock();

            try {
                if (distributedLock == null || distributedLock.tryLock(1, TimeUnit.MINUTES)) {

                    logWork(AicyiFactory.product(robot));

                    logWork(String.format("生产商品【%s】完成", AicyiFactory.PRODUCT_COUNT));
                }
            } catch (InterruptedException e) {

                logWork("一直没有锁，不干了，下班");

            } finally {

                if (distributedLock != null) {

                    distributedLock.unlock();
                }
            }
        }


        public void logWork(String work) {

            LOGGER.info("{}-{}【{}】号员工，{}", Thread.currentThread().getId(), name, userNo, work);
        }

        @Override
        public void run() {

            this.work();
        }
    }

    /**
     * 工厂器械
     */
    public interface Robot {

        /**
         * 获取Key
         *
         * @return
         */
        DistributedLock getLock();

        /**
         * 开始工作
         *
         * @return
         */
        String working();
    }
}
