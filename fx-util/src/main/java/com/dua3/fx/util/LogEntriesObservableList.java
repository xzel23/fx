package com.dua3.fx.util;

import com.dua3.utility.logging.LogBuffer;
import com.dua3.utility.logging.LogEntry;
import javafx.collections.ObservableListBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class represents a table model for displaying log entries in a Swing LogPane.
 */
final class LogEntriesObservableList extends ObservableListBase<LogEntryBean> implements LogBuffer.LogBufferListener {
    private static final Logger LOG = LogManager.getLogger(LogEntriesObservableList.class);

    private final LogBuffer buffer;
    private volatile List<LogEntryBean> data = Collections.emptyList();
    private final AtomicInteger queuedRemoves = new AtomicInteger();

    private final ReadWriteLock updateLock = new ReentrantReadWriteLock();
    private final Lock updateReadLock = updateLock.readLock();
    private final Lock updateWriteLock = updateLock.writeLock();
    private final Condition updatesAvailableCondition = updateWriteLock.newCondition();

    /**
     * Constructs a new LogTableModel with the specified LogBuffer.
     *
     * @param buffer the LogBuffer to use for storing log messages
     * @throws NullPointerException if the buffer is null
     */
    LogEntriesObservableList(LogBuffer buffer) {
        this.buffer = buffer;
        buffer.addLogBufferListener(this);

        Thread updateThread = new Thread(() -> {
            while (true) {
                updateWriteLock.lock();
                try {
                    updatesAvailableCondition.await();

                    try {
                        beginChange();

                        int oldSz = data.size();
                        int remove = queuedRemoves.getAndSet(0);
                        int remainingRows = oldSz - remove;
                        List<LogEntryBean> dataToRemove = List.copyOf(data.subList(0, remove));
                        data = Arrays.stream(buffer.toArray()).map(LogEntryBean::new).toList();
                        int sz = data.size();
                        int addedRows = sz - remainingRows;

                        if (!dataToRemove.isEmpty()) {
                            nextRemove(0, dataToRemove);
                        }
                        if (addedRows > 0) {
                            nextAdd(sz - addedRows, sz);
                        }
                    } finally {
                        endChange();
                    }
                } catch (InterruptedException e) {
                    LOG.debug("interrupted", e);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    LOG.warn("unexpected exception in update thread: {}", e.getMessage(), e);
                } finally {
                    updateWriteLock.unlock();
                }
            }
        }, "LogTableModel Update Thread");
        updateThread.setDaemon(true);
        updateThread.start();
    }

    @Override
    public int size() {
        updateReadLock.lock();
        try {
            return data.size();
        } finally {
            updateReadLock.unlock();
        }
    }

    @Override
    public LogEntryBean get(int idx) {
        updateReadLock.lock();
        try {
            return data.get(idx);
        } finally {
            updateReadLock.unlock();
        }
    }

    @Override
    public void entries(Collection<LogEntry> entries, int replaced) {
        updateWriteLock.lock();
        try {
            queuedRemoves.addAndGet(replaced);
            updatesAvailableCondition.signalAll();
        } finally {
            updateWriteLock.unlock();
        }
    }

    @Override
    public void clear() {
        updateWriteLock.lock();
        try {
            queuedRemoves.set(data.size());
            updatesAvailableCondition.signalAll();
        } finally {
            updateWriteLock.unlock();
        }
    }

    public void executeRead(Runnable readTask) {
        updateReadLock.lock();
        try {
            readTask.run();
        } finally {
            updateReadLock.unlock();
        }
    }

}
