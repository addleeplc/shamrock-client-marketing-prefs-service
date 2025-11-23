/*
 * Copyright 2008 - 2025 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.shamrock.client.marketing.prefs.cache;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class SingletonCache<T> {
    private final AtomicReference<T> ref = new AtomicReference<>();
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private final Callable<T> supplier;
    private volatile Future<?> currentTask;

    public SingletonCache(Callable<T> supplier) {
        this.supplier = supplier;

        try {
            this.ref.set(supplier.call());

            Executors.newSingleThreadExecutor().submit(this::processQueue);
        } catch (Exception e) {
            throw new RuntimeException("Can't initiate cache: " + getClass().getSimpleName(), e);
        }
    }

    public T get() {
        return ref.get();
    }

    public synchronized void refresh() {
        if (currentTask != null && !currentTask.isDone()) {
            currentTask.cancel(true);
        }

        currentTask = CompletableFuture.runAsync(
                () -> {
                    try {
                        T newValue = supplier.call();
                        ref.set(newValue);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        throw new RuntimeException("Can't refresh cache: " + getClass().getSimpleName());
                    }
                },
                queue::offer
        );
    }

    private void processQueue() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Runnable task = queue.take();
                task.run();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
