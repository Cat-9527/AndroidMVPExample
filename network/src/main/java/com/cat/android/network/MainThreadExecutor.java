package com.cat.android.network;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public final class MainThreadExecutor implements Executor {
    private static volatile MainThreadExecutor sInstance;

    private MainThreadExecutor() {
    }

    public static MainThreadExecutor getsInstance() {
        if (sInstance == null) {
            synchronized (MainThreadExecutor.class) {
                if (sInstance == null) {
                    sInstance = new MainThreadExecutor();
                }
            }
        }
        return sInstance;
    }

    private final Object lock = new Object();
    private volatile Handler mainHandler;

    private boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    private void postToMainThread(Runnable runnable) {
        if (mainHandler == null) {
            synchronized (lock) {
                if (mainHandler == null) {
                    mainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        mainHandler.post(runnable);
    }

    @Override
    public void execute(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            postToMainThread(runnable);
        }
    }
}
