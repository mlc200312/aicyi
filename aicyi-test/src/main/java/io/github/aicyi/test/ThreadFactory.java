package io.github.aicyi.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Mr.Min
 * @description 线程工厂类
 * @date 2025/8/19
 **/
public class ThreadFactory {
    protected final ExecutorService executorService;
    private final int maxThreads;

    /**
     * 构造函数
     *
     * @param maxThreads 最大同时工作线程数
     */
    public ThreadFactory(int maxThreads) {
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("线程数必须大于0");
        }
        this.maxThreads = maxThreads;
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * 提交任务到线程池
     *
     * @param task 要执行的任务
     */
    public void submitTask(Runnable task) {
        executorService.submit(task);
    }

    /**
     * 优雅关闭线程池
     *
     * @param timeout 超时时间
     * @param unit    时间单位
     * @throws InterruptedException
     */
    public void shutdown(long timeout, TimeUnit unit) throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(timeout, unit)) {
            executorService.shutdownNow();
        }
    }

    /**
     * 立即关闭线程池
     */
    public void shutdownNow() {
        executorService.shutdownNow();
    }

    /**
     * 获取当前活跃线程数（近似值）
     *
     * @return 活跃线程数
     */
    public int getActiveThreadCount() {
        return ((java.util.concurrent.ThreadPoolExecutor) executorService).getActiveCount();
    }

    /**
     * 获取最大线程数
     *
     * @return 最大线程数
     */
    public int getMaxThreads() {
        return maxThreads;
    }
}