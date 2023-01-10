package com.cat.android.network;

import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class IOExecutor implements Executor {
    private static volatile IOExecutor sInstance;

    private IOExecutor() {
    }

    public static IOExecutor getInstance() {
        if (sInstance == null) {
            synchronized (IOExecutor.class) {
                if (sInstance == null) {
                    sInstance = new IOExecutor();
                }
            }
        }
        return sInstance;
    }

    private final Object lock = new Object();
    private volatile Executor executor;

    private Executor ensureExecutor() {
        if (executor == null) {
            synchronized (lock) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                        new SynchronousQueue<>(), new IOThreadFactory());
                }
            }
        }
        return executor;
    }

    @Override
    public void execute(Runnable runnable) {
        ensureExecutor().execute(runnable);
    }

    private static class IOThreadFactory implements ThreadFactory {
        private static final String THREAD_NAME_STEM = "network_io_%d";

        private final AtomicInteger threadId = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            Thread t = new Thread(runnable);
            t.setName(String.format(THREAD_NAME_STEM, threadId.getAndIncrement()));
            return t;
        }
    }
}
