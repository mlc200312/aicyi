package com.aichuangyi.test;

import com.aichuangyi.commons.logging.Logger;
import com.aichuangyi.commons.logging.LoggerFactory;
import com.aichuangyi.test.util.RandomGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Mr.Min
 * @description 功能描述
 * @date 2019-06-13
 **/
public class AicyiFactory {
    private static final Logger logger = LoggerFactory.getLogger(AicyiFactory.class);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final ThreadLocal<Long> TIME_THREAD_LOCAL = new ThreadLocal<>();
    public static int PRODUCT_COUNT = 0;

    private int workerCount;
    private Project project;

    public AicyiFactory(int worker, Project project) {
        this.workerCount = worker;
        this.project = project;
    }

    /**
     * 运作
     */
    public void startRun() {
        long startTime = System.currentTimeMillis();
        TIME_THREAD_LOCAL.set(startTime);
        logger.info("开始工作时间:{}",
                LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logger.info("【{}】工厂共{}人！", AicyiFactory.class.getSimpleName(), workerCount);
        for (int i = 0; i < workerCount; i++) {
            EXECUTOR_SERVICE.execute(new Worker(String.format("%06d", i), RandomGenerator.generateFullName(), project));
        }
    }

    /**
     * 停止运作
     */
    public void stopRun() {
        ThreadPoolExecutor service = (ThreadPoolExecutor) EXECUTOR_SERVICE;
        //判断是否还有线程工作
        while (service.getActiveCount() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        logger.info("【{}】共生产了{}件商品！", AicyiFactory.class.getSimpleName(), AicyiFactory.PRODUCT_COUNT);
        logger.info("【{}】工厂停工！", AicyiFactory.class.getSimpleName());
        EXECUTOR_SERVICE.shutdown();
        long startTime = TIME_THREAD_LOCAL.get();
        TIME_THREAD_LOCAL.remove();
        long endTime = System.currentTimeMillis();
        logger.info("结束工作时间:{}，工作时长（ms）：{}",
                LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), (endTime - startTime));
    }

    public static class Worker implements Runnable {

        private String userNo;
        private String name;
        private Project project;

        public Worker(String userNo, String name, Project project) {
            this.userNo = userNo;
            this.name = name;
            this.project = project;
        }

        public String work() {
            logger.info("【{}】号员工：{}，开始干活了！", userNo, name);
            return project.work();
        }

        @Override
        public void run() {
            PRODUCT_COUNT++;
            logger.info("【{}】号员工：{} - 线程ID：{}：，工作：{}！", userNo, name, Thread.currentThread().getId(), this.work());
        }
    }

    /**
     * 项目
     */
    public interface Project {

        /**
         * 工作
         *
         * @return
         */
        String work();
    }
}
