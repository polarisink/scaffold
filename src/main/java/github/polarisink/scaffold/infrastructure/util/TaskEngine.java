package github.polarisink.scaffold.infrastructure.util;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 定时任务引擎
 */
public final class TaskEngine {

    /**
     * 任务引擎实例
     */
    private static final TaskEngine instance = new TaskEngine();

    /**
     * 任务引擎实例（单一实例）
     *
     * @return 任务引擎实例
     */
    public static TaskEngine getInstance() {
        return instance;
    }

    /**
     *  定时时间
     */
    private Timer timer;

    /**
     * 任务执行者
     */
    private ExecutorService executor;

    /**
     * The wrapped tasks.
     */
    private final Map<TimerTask, TimerTaskWrapper> wrappedTasks = new ConcurrentHashMap<TimerTask, TimerTaskWrapper>();


    private TaskEngine() {
        timer = new Timer("TaskEngine-timer", true);
        executor = Executors.newCachedThreadPool(new ThreadFactory() {

            final AtomicInteger threadNumber = new AtomicInteger(1);

            public Thread newThread(@NotNull Runnable runnable) {
                Thread thread = new Thread(Thread.currentThread().getThreadGroup(), runnable, "TaskEngine-pool-" + threadNumber.getAndIncrement(), 0);
                thread.setDaemon(true);
                if (thread.getPriority() != Thread.NORM_PRIORITY) {
                    thread.setPriority(Thread.NORM_PRIORITY);
                }
                return thread;
            }
        });
    }


    /**
     * 提交任务
     * @param task Runnable 任务线程
     * @return Future<?>
     */
    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    public void schedule(TimerTask task, long delay) {
        timer.schedule(new TimerTaskWrapper(task), delay);
    }

    public void schedule(TimerTask task, Date time) {
        timer.schedule(new TimerTaskWrapper(task), time);
    }

    public void schedule(TimerTask task, long delay, long period) {
        TimerTaskWrapper taskWrapper = new TimerTaskWrapper(task);
        wrappedTasks.put(task, taskWrapper);
        timer.schedule(taskWrapper, delay, period);
    }

    public void schedule(TimerTask task, Date firstTime, long period) {
        TimerTaskWrapper taskWrapper = new TimerTaskWrapper(task);
        wrappedTasks.put(task, taskWrapper);
        timer.schedule(taskWrapper, firstTime, period);
    }

    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        TimerTaskWrapper taskWrapper = new TimerTaskWrapper(task);
        wrappedTasks.put(task, taskWrapper);
        timer.scheduleAtFixedRate(taskWrapper, delay, period);
    }

    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
        TimerTaskWrapper taskWrapper = new TimerTaskWrapper(task);
        wrappedTasks.put(task, taskWrapper);
        timer.scheduleAtFixedRate(taskWrapper, firstTime, period);
    }


    /**
     * 取消定时任务
     * @param task TimerTask 定时任务
     */
    public void cancelScheduledTask(TimerTask task) {
        TimerTaskWrapper taskWrapper = wrappedTasks.remove(task);
        if (taskWrapper != null) {
            taskWrapper.cancel();
        }
    }

    /**
     * 关闭任务引擎服务
     * Shuts down the task engine service.
     */
    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private class TimerTaskWrapper extends TimerTask {

        /**
         * 任务
         */
        private final TimerTask task;

        /**
         * 实例化新的计时器任务包装器
         * Instantiates a new timer task wrapper.
         *
         * @param task TimerTask 计时器任务
         */
        public TimerTaskWrapper(TimerTask task) {
            this.task = task;
        }

        @Override
        public void run() {
            executor.submit(task);
        }
    }
}
