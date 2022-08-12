package com.dua3.fx.util;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class intended for controlling possibly long-running update operations. Refresh happen mutually exclusive, i.e.
 * the update tasks do not have to be explicitly synchronized as long as not called directly from other code.
 * An example is updating a JavaFX node. I.e. if redraw requests come in before current drawing finishes,
 * the application becomes sluggish or burns CPU cycles for drawing outdated data. the FxRefresher automatically
 * skips intermediate frames if redraw requests come in too fast for the drawing to come up with.
 */
public final class FxRefresh {
    private static final Logger LOG = Logger.getLogger(FxRefresh.class.getName());
    
    /** The instance name (used in logging). */
    private final String name;

    /** The revision number of the last completed update operation. */
    private final AtomicInteger currentRevision = new AtomicInteger();
    /** The revision number of the last request. {@code currentRevision < requestedRevision} implies a pending update. */
    private final AtomicInteger requestedRevision = new AtomicInteger();

    // synchronization
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition trigger = lock.newCondition();

    /** The active state. Refresh requests in inactive state are put on hold until activated again. */
    private final AtomicBoolean active = new AtomicBoolean(false);

    /** The update thread, null if not running or stop requested. */ 
    private volatile Thread updateThread = null;
    
    /** the update task to execute. */
    private final Runnable task;

    /**
     * Create new instance. The initial state is active. 
     * @param name the name for the instance
     * @param task the task to call when refreshing
     * @return new instance
     */
    public static FxRefresh create(String name, Runnable task) {
        return create(name, task, true);
    }

    /**
     * Create new instance.
     * @param name the name for the instance
     * @param task the task to call when refreshing
     * @param active the initial active state
     * @return new instance
     */
    public static FxRefresh create(String name, Runnable task, boolean active) {
        FxRefresh r = new FxRefresh(name, task);
        r.setActive(active);
        return r;
    }

    /**
     * Create a refresher instance for a JavaFX {@link Node}.
     * The refresher will prevent updates when the node is hidden. The refresher is stopped when the node is
     * removed from the scene graph. The initial state is active.
     * @param name the refresher name
     * @param task the task to run on refresh
     * @param node the node associated with this refresher
     * @return new instance
     */
    public static FxRefresh create(String name, Runnable task, Node node) {
        return create(name, task, node, true);    
    }
    
    /**
     * Create a refresher instance for a JavaFX {@link Node}.
     * The refresher will prevent updates when the node is hidden. The refresher is stopped when the node is
     * removed from the scene graph.
     * @param name the refresher name
     * @param task the task to run on refresh
     * @param node the node associated with this refresher
     * @param active the initial active state
     * @return new instance
     */
    public static FxRefresh create(String name, Runnable task, Node node, boolean active) {
        FxRefresh r = new FxRefresh(name, () -> {
            if (!node.isVisible()) {
                LOG.fine("node is not visible, update skipped");
                return;
            }
            
            task.run();
        });

        // stop update thread when node is removed from scene graph
        node.parentProperty().addListener(  (v, o, n) -> {
            if (n == null) {
                LOG.fine("node was removed from parent, stopping refresher");
                r.stop();
            }
        } );

        node.sceneProperty().addListener( (v,o,n) -> {
            if (n==null) {
                LOG.fine("node was removed from scene graph, stopping refresher");
                r.stop();
            }
        } );

        r.setActive(active);
        
        return r;
    }

    /**
     * Constructor.
     * @param name the name for the instance
     * @param task the task to call when refreshing
     */
    private FxRefresh(String name, Runnable task) {
        this.name = Objects.requireNonNull(name);
        this.task = Objects.requireNonNull(task);
        this.updateThread = new Thread(this::refreshLoop);
        this.updateThread.start();
    }

    /**
     * Loop of the update thread. Waits for incoming requests and calls the update task. 
     */
    private void refreshLoop() {
        LOG.fine(() -> "["+name+"] entering refresh loop");
        do {
            lock.lock();
            try {
                // stay in loop as long as stop is not requested (updateThread!=null) and
                // refresher is inactive or no redraw request has been issued  
                while(updateThread!=null
                      && (!active.get() || requestedRevision.get()<=currentRevision.get())) {
                    trigger.await();
                }
            } catch (InterruptedException e) {
                LOG.info(() -> "["+name+"] interrupted, shutting down");
                Thread.currentThread().interrupt();
                stop();
            } finally {
                lock.unlock();
            }

            // run task and update revision
            if (active.get()) {
                int myRevision = requestedRevision.get();
                if (myRevision != currentRevision.getAndSet(myRevision)) {
                    try {
                        LOG.fine(() -> "[" + name + "] starting refresh with revision: " + myRevision);
                        task.run();
                        LOG.fine(() -> "[" + name + "] refreshed to revision: " + myRevision);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, "task aborted, exception swallowed, current revision %s might have inconsistent state".formatted(myRevision), e);
                    } finally {
                        currentRevision.set(myRevision);
                    }
                } else {
                    LOG.fine(() -> "[" + name + "] already at revision: " + myRevision);
                }
            }
        } while (updateThread!= null);
        LOG.fine(() -> "["+name+"] exiting refresh loop");
    }

    /**
     * Wake up the update thread.
     */
    private void signal() {
        LOG.fine(() -> "["+name+"] raise signal");
        lock.lock();
        try {
            trigger.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Check if the refresher has been started. Note that it's possible that the refresher is running,
     * but inactive, i.e. if the window is hidden.
     * 
     * @return true, if the refresher is running
     */
    public boolean isRunning() {
        return updateThread != null;
    }

    /**
     * Stop the refresher.
     */
    public synchronized void stop() {
        LOG.fine(() -> "["+name+"] stopping");
        this.updateThread = null;
        setActive(false);
    }

    /**
     * Set active state.
     * @param flag whether to activate or deactivate the refresher
     */
    public synchronized void setActive(boolean flag) {
        LOG.fine(() -> "["+name+"] setActive("+flag+")");
        this.active.set(flag);
        signal();
    }

    /**
     * Check active state.
     * @return true if active
     */
    public boolean isActive() {
        return active.get();
    }

    /**
     * Request refresh. The refresh will be performed ass soon as all the following are met:
     * <ul>
     *     <li>the refresher is running
     *     <li>the refresher's state is "active"
     *     <li>no other request running
     *     <li>no no newer request was queued (in that case, the older request will be skipped)
     * </ul>
     */
    public void refresh() {
        LOG.fine(() -> "["+name+"] refresh requested");
        lock.lock();
        try {
            int revision = requestedRevision.incrementAndGet();
            LOG.fine(() -> "["+name+"] requested revision "+revision);
            trigger.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
