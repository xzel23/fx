package com.dua3.fx.util;

import com.dua3.utility.lang.LangUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class FxRefresh {
    private static final Logger LOG = Logger.getLogger(FxRefresh.class.getName());
    
    private final String name;
    
    private final AtomicInteger currentRevision = new AtomicInteger();
    private final AtomicInteger requestedRevision = new AtomicInteger();

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition trigger = lock.newCondition();
    
    private final AtomicBoolean active = new AtomicBoolean(false);

    private Thread updateThread = null;
    
    private Runnable task;
    
    public static FxRefresh create(String name, Runnable task) {
        return new FxRefresh(name, task);
    }
    
    private FxRefresh(String name, Runnable task) {
        this.name = Objects.requireNonNull(name);
        this.task = Objects.requireNonNull(task);
    }

    private void refreshLoop() {
        while (true) {
            try {
                waitForRequest();
            } catch (InterruptedException e) {
                LOG.info(() -> "["+name+"] interrupted, shutting down");
                Thread.currentThread().interrupt();
                stop();
            }

            if (updateThread==null) {
                LOG.info(() -> "["+name+"] stopped");
                return;
            }
            
            // run task and update revision
            if (active.get()) {
                int myRevision = requestedRevision.get();
                LOG.fine(() -> "[" + name + "] starting refresh with revision: " + myRevision);
                task.run();
                currentRevision.set(myRevision);
                LOG.fine(() -> "[" + name + "] refreshed to revision: " + myRevision);
            }
        }
    }

    private void waitForRequest() throws InterruptedException {
        try {
            lock.lock();
            while(!active.get() || requestedRevision.get()<=currentRevision.get()) {
                trigger.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public synchronized void start() {
        if (this.updateThread!=null) {
            LOG.warning(() -> "["+name+"] start() called on running instance, ignoring");
            return;
        }

        LOG.fine(() -> "["+name+"] starting");
        this. updateThread = new Thread(this::refreshLoop);
        this.updateThread.start();
    }

    public synchronized void stop() {
        if (this.updateThread==null) {
            LOG.warning(() -> "["+name+"] stop() called on inactive running instance, ignoring");
            return;
        }

        LOG.fine(() -> "["+name+"] stopping");
        this. updateThread = new Thread(this::refreshLoop);
        this.updateThread.start();
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public boolean isActive() {
        return active.get();
    }
    
    public void refresh() {
        LOG.fine(() -> "["+name+"] refresh requested");
        try {
            lock.lock();
            requestedRevision.incrementAndGet();
            trigger.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void signal() {
        LOG.fine(() -> "["+name+"] refresh requested");
        try {
            lock.lock();
            trigger.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
