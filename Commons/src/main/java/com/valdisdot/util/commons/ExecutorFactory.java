package com.valdisdot.util.commons;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory class for creating and managing {@link ScheduledExecutorService} instances. It provides
 * utilities for managing scheduled tasks and ensures that resources are cleaned up properly during
 * the application's lifecycle.
 */
public class ExecutorFactory {

    /** A collection of managed {@link ScheduledExecutorService} instances. */
    private static final Collection<ScheduledExecutorService> EXECUTOR_SERVICES = new LinkedList<>();

    /** A collection of {@link Future} objects representing tasks submitted to the executor services. */
    private static final Collection<Future<?>> FUTURES = new LinkedList<>();

    /** Logger for logging information and errors related to the executor services. */
    private static final Logger LOGGER = Logger.getLogger(ExecutorFactory.class.getName());

    static {
        // Initialize a cleaner task that runs periodically to remove completed or cancelled tasks and services.
        ScheduledExecutorService cleaner = scheduledExecutorService(1, true);
        cleaner.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LOGGER.log(Level.INFO, String.format("Calling executor factory's cleaner. ScheduledExecutorServices: %s, Futures: %s", EXECUTOR_SERVICES.size(), FUTURES.size()));
                FUTURES.removeIf(f -> f.isDone() || f.isCancelled());
                EXECUTOR_SERVICES.removeIf(s -> s.isShutdown() || s.isTerminated());
                LOGGER.log(Level.INFO, String.format("Done. ScheduledExecutorServices: %s, Futures: %s", EXECUTOR_SERVICES.size(), FUTURES.size()));
            }
        }, 10, 10, TimeUnit.MINUTES);

        // Add a shutdown hook to gracefully cancel tasks and shut down services when the application terminates.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger l = Logger.getLogger(ExecutorFactory.class.getName());
            l.log(Level.INFO, String.format("Canceling %s task(s) and shutting down %s schedule service(s).", FUTURES.size(), EXECUTOR_SERVICES.size()));
            for (ExecutorService s : EXECUTOR_SERVICES) s.shutdown();
            for (Future<?> f : FUTURES) if (!(f.isDone() || f.isCancelled())) f.cancel(true);
            for (ExecutorService s : EXECUTOR_SERVICES) {
                s.shutdownNow();
                try {
                    s.awaitTermination(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    l.log(Level.WARNING, e, () -> "An exception occurred during the schedule termination.");
                }
            }
            l.log(Level.INFO, "Schedule services have been shut down.");
        }) {{
            setName("executor-services-shutdown-hook");
        }});
    }

    /**
     * Creates a {@link ScheduledExecutorService} with the specified thread pool size.
     * If autocloseInShutdownHook is true, the service is wrapped to be managed and automatically
     * cleaned up during the application's shutdown.
     *
     * @param threadPoolSize           the size of the thread pool for the executor service
     * @param autocloseInShutdownHook  if true, the executor service will be managed and closed automatically at shutdown
     * @return a {@link ScheduledExecutorService} instance
     */
    public static ScheduledExecutorService scheduledExecutorService(int threadPoolSize, boolean autocloseInShutdownHook) {
        ScheduledExecutorService s = threadPoolSize <= 1 ? Executors.newSingleThreadScheduledExecutor() : Executors.newScheduledThreadPool(threadPoolSize);
        return autocloseInShutdownHook ? new WrappedScheduledExecutorService(s) : s;
    }

    /**
     * A wrapper for {@link ScheduledExecutorService} that automatically registers the service and its tasks
     * for management and cleanup.
     */
    private static class WrappedScheduledExecutorService implements ScheduledExecutorService {
        private final ScheduledExecutorService real;

        /**
         * Constructs a WrappedScheduledExecutorService that wraps the provided {@link ScheduledExecutorService}.
         *
         * @param real the original {@link ScheduledExecutorService} to wrap
         */
        private WrappedScheduledExecutorService(ScheduledExecutorService real) {
            this.real = real;
            EXECUTOR_SERVICES.add(real);
        }

        @Override
        public void execute(Runnable command) {
            real.execute(command);
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            ScheduledFuture<?> scheduledFuture = real.scheduleWithFixedDelay(command, initialDelay, delay, unit);
            FUTURES.add(scheduledFuture);
            return scheduledFuture;
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            ScheduledFuture<?> scheduledFuture = real.scheduleAtFixedRate(command, initialDelay, period, unit);
            FUTURES.add(scheduledFuture);
            return scheduledFuture;
        }

        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            ScheduledFuture<V> schedule = real.schedule(callable, delay, unit);
            FUTURES.add(schedule);
            return schedule;
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            ScheduledFuture<?> schedule = real.schedule(command, delay, unit);
            FUTURES.add(schedule);
            return schedule;
        }

        @Override
        public String toString() {
            return "wrapper: " + super.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return real.equals(obj);
        }

        @Override
        public int hashCode() {
            return real.hashCode();
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return real.invokeAny(tasks);
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return real.invokeAny(tasks, timeout, unit);
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            List<Future<T>> futures = real.invokeAll(tasks, timeout, unit);
            FUTURES.addAll(futures);
            return futures;
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            List<Future<T>> futures = real.invokeAll(tasks);
            FUTURES.addAll(futures);
            return futures;
        }

        @Override
        public Future<?> submit(Runnable task) {
            Future<?> submit = real.submit(task);
            FUTURES.add(submit);
            return submit;
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            Future<T> submit = real.submit(task, result);
            FUTURES.add(submit);
            return submit;
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            Future<T> submit = real.submit(task);
            FUTURES.add(submit);
            return submit;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return real.awaitTermination(timeout, unit);
        }

        @Override
        public boolean isTerminated() {
            return real.isTerminated();
        }

        @Override
        public boolean isShutdown() {
            return real.isShutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            return real.shutdownNow();
        }

        @Override
        public void shutdown() {
            real.shutdown();
        }
    }
}
