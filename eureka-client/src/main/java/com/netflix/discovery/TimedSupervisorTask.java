package com.netflix.discovery;

import com.netflix.servo.monitor.Counter;
import com.netflix.servo.monitor.LongGauge;
import com.netflix.servo.monitor.MonitorConfig;
import com.netflix.servo.monitor.Monitors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A supervisor task that schedules subtasks while enforce a timeout.
 * Wrapped subtasks must be thread safe.
 *
 * @author David Qiang Liu
 */
public class TimedSupervisorTask extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(TimedSupervisorTask.class);

    private final Counter timeoutCounter;
    private final Counter rejectedCounter;
    private final Counter throwableCounter;
    private final LongGauge threadPoolLevelGauge;

    /**
     * 定时任务服务
     */
    private final ScheduledExecutorService scheduler;
    /**
     * 执行子任务线程池
     */
    private final ThreadPoolExecutor executor;
    /**
     *子 任务执行超时时间
     */
    private final long timeoutMillis;
    /**
     * 子任务
     */
    private final Runnable task;
    /**
     * 当前子任务执行频率
     */
    private final AtomicLong delay;
    /**
     * 最大子任务执行频率
     *
     * 子任务执行超时情况下使用
     */
    private final long maxDelay;

    public TimedSupervisorTask(String name, ScheduledExecutorService scheduler, ThreadPoolExecutor executor,
                               int timeout, TimeUnit timeUnit, int expBackOffBound, Runnable task) {
        this.scheduler = scheduler;
        this.executor = executor;
        this.timeoutMillis = timeUnit.toMillis(timeout);
        this.task = task;
        this.delay = new AtomicLong(timeoutMillis);
        this.maxDelay = timeoutMillis * expBackOffBound;

        // Initialize the counters and register.
        timeoutCounter = Monitors.newCounter("timeouts");
        rejectedCounter = Monitors.newCounter("rejectedExecutions");
        throwableCounter = Monitors.newCounter("throwables");
        threadPoolLevelGauge = new LongGauge(MonitorConfig.builder("threadPoolUsed").build());
        Monitors.registerObject(name, this);
    }

    @Override
    public void run() {
        Future<?> future = null;
        try {
            // 提交 任务
            future = executor.submit(task);
            //
            threadPoolLevelGauge.set((long) executor.getActiveCount());
            // 等待任务 执行完成 或 超时
            future.get(timeoutMillis, TimeUnit.MILLISECONDS);  // block until done or timeout
            // 设置 下一次任务执行频率
            delay.set(timeoutMillis);
            //
            threadPoolLevelGauge.set((long) executor.getActiveCount());
        } catch (TimeoutException e) {
            logger.error("task supervisor timed out", e);
            timeoutCounter.increment(); //

            // 设置 下一次任务执行频率
            long currentDelay = delay.get();
            long newDelay = Math.min(maxDelay, currentDelay * 2);
            delay.compareAndSet(currentDelay, newDelay);

        } catch (RejectedExecutionException e) {
            if (executor.isShutdown() || scheduler.isShutdown()) {
                logger.warn("task supervisor shutting down, reject the task", e);
            } else {
                logger.error("task supervisor rejected the task", e);
            }

            rejectedCounter.increment(); //
        } catch (Throwable e) {
            if (executor.isShutdown() || scheduler.isShutdown()) {
                logger.warn("task supervisor shutting down, can't accept the task");
            } else {
                logger.error("task supervisor threw an exception", e);
            }

            throwableCounter.increment(); //
        } finally {
            // 取消 未完成的任务
            if (future != null) {
                future.cancel(true);
            }

            // 调度 下次任务
            if (!scheduler.isShutdown()) {
                scheduler.schedule(this, delay.get(), TimeUnit.MILLISECONDS);
            }
        }
    }
}