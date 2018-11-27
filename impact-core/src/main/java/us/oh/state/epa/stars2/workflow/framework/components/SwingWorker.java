package us.oh.state.epa.stars2.workflow.framework.components;

import javax.swing.SwingUtilities;

import org.springframework.context.ApplicationContext;

import us.wy.state.deq.impact.App;

/**
 * <p>
 * Title: SwingWorker
 * </p>
 * 
 * <p>
 * Description: This is my version of the SwingWorker3 class from the Java Swing
 * tutorial.
 * </p>
 * 
 * <p>
 * The purpose of the SwingWorker is to execute a "worker" Thread to perform
 * some potentially long-term action, e.g., a database retrieval or update. This
 * leaves the Swing event processing thread free to respond to other user
 * actions. When the "worker" thread completes, a second Thread, called the
 * "Swing" thread, is created and added to Swing event handler thread to update
 * Swing components with new contents.
 * </p>
 * 
 * <p>
 * This version of the SwingWorker also has provision for allowing the Swing
 * thread to retrieve an Exception that may have occurred in the worker thread.
 * </p>
 * 
 * <p>
 * This class is intended to be extended to perform application-specific
 * actions. The derived class must implement the "construct()" method and will
 * nearly always need to implement the "finished()" method. Probably the best
 * way to extend this class is to have your "construct()" method do the long
 * term activity, e.g., a database retrieval or file update, and return the
 * results of that activity. At the same time, the results of the activity
 * should be saved in a data member of the class (the "finished()" method does
 * not accept any input parameters). The "finished()" method should get the data
 * from the data member and then update GUI contents (or whatever else it wants
 * to do).
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 */
public abstract class SwingWorker {
    protected ThreadVar threadVar; // Holds the worker thread
    private Object value; // The output of "construct()"
    private Exception exception; // Any exception from "construct()"
    
    private ApplicationContext appContext = App.getApplicationContext();

    /**
     * Constructor. Creates the "worker" and "Swing" threads, but does not start
     * either thread. In order to start the worker thread, call the "start()"
     * method (as is typically done for Threads).
     */
    public SwingWorker() {
        Runnable doFinished = new Finished(this);
        Runnable doConstruct = new Worker(this, doFinished);

        Thread t = new Thread(doConstruct, "SwingWorker");
        t.setDaemon(true);
        this.threadVar = new ThreadVar(t);
    }
    
    protected ApplicationContext getApplicationContext() {
    	return appContext;
    }

    /**
     * Abstract method to allow derived class to create an appropriate data
     * object to contain the result of "worker" thread. This is a template
     * method that is called by the SwingWorker class as the appropriate time.
     * The derived class should perform its "worker" operation (the potentially
     * time consuming activity) in this method.
     * 
     * @return Object An object that indicates that the worker thread has
     *         completed. Usually, this is the data retrieved by the worker
     *         thread.
     */
    public abstract Object construct();

    /**
     * Method invoked from the "Swing" thread to allow the contents of the GUI
     * to be updated. The default implementation of this method does nothing.
     * The derived class should overload this method to perform its GUI content
     * update. Note that the GUI update is expected and assumed to be "fast".
     */
    public void finished() {
    }

    /**
     * Interrupts the "worker" thread, thereby terminating its activity. Note
     * that the "Swing" thread is not affected by this method.
     */
    public final void interrupt() {
        Thread t = this.threadVar.get();

        if (t == null) {
            return;
        }

        t.interrupt();
        this.threadVar.clear();
    }

    /**
     * Returns the object from the "worker" thread, i.e., the data object. Note
     * that if the worker thread is still active, the current thread (usually
     * the application's "main" thread) will block and wait for the worker
     * thread to complete. Returns "null" if the worker thread gets interrupted.
     * 
     * @return Object the results of the "construct()" method provided by the
     *         derived class.
     */
    public final Object get() {
        while (true) {
            Thread t = this.threadVar.get();

            // If the worker thread has already stopped, just return the value.

            if (t == null) {
                return this.getValue();
            }

            // Wait on the worker thread to complete its activity. If it gets
            // interrupted, then we have no result to return.
            try {
                t.join();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }

    /**
     * Starts the worker thread. Note that this method must be called after
     * constructing the SwingWorker or nothing will happen. This is analogous to
     * the way Threads are used.
     */
    public final void start() {
        Thread t = this.threadVar.get();

        if (t != null) {
            t.start();
        }
    }

    /**
     * Returns the current value from the "construct()" method or worker thread.
     * Returns "null" if the worker thread hasn't completed yet. This method is
     * intended primarily for use by the "finished()" method.
     * 
     * @return Object from the "construct()" method.
     */
    protected synchronized Object getValue() {
        return this.value;
    }

    /**
     * Framework method. Sets the current value from the "construct()" method.
     * 
     * @param o
     *            Object from the "construct()" method.
     */
    protected synchronized void setValue(Object o) {
        this.value = o;
    }

    /**
     * Framework method. Allows the "construct()" method to preserve any
     * Exception for later retrieval by the "finished()" method.
     * 
     * @param e
     *            The exception that occurred in the worker thread.
     */
    protected synchronized void setException(Exception e) {
        this.exception = e;
    }

    /**
     * Framework method. Allows the "finished()" method to retrieve any
     * Exception saved by the "construct()" method.
     * 
     * @return The exception that occurred in the worker thread.
     */
    protected synchronized Exception getException() {
        return this.exception;
    }

    // As you can tell, I find "anonymous inner classes" annoying.
    //
    // Holds on to the "worker" thread for us.
    private static class ThreadVar {
        private Thread thread; // The worker thread.

        // Constructor.

        ThreadVar(Thread t) {
            this.thread = t;
        }

        // Returns the worker thread.

        synchronized Thread get() {
            return this.thread;
        }

        // Releases the worker thread and sets reference to null.

        synchronized void clear() {
            this.thread = null;
        }
    }

    // The "finished" thread, i.e., the thread that updates the GUI.

    private static class Finished implements Runnable {
        private SwingWorker worker; // The SwingWorker that created us.

        // Constructor.

        Finished(SwingWorker sw) {
            this.worker = sw;
        }

        // Standard thread method. Calls "finished()" on "sw".

        public void run() {
            this.worker.finished();
        }
    }

    // The "worker" thread, i.e., the thread that does the data operation.

    private static class Worker implements Runnable {
        private SwingWorker worker; // SwingWorker that owns this object.
        private Runnable finished; // The thread we kick off when done.

        // Constructor. We need the SwingWorker that created us and the
        // "finished" thread so we can kick it off when we are done.

        Worker(SwingWorker sw, Runnable finished) {
            this.worker = sw;
            this.finished = finished;
        }

        // Standard thread method. Calls "construct()" on "sw".

        public void run() {
            try {
                this.worker.setValue(this.worker.construct());
            } finally {
                this.worker.threadVar.clear();
            }

            // After the worker is done, place the "finished" thread on the
            // Swing event queue.

            SwingUtilities.invokeLater(this.finished);
        }
    }
}
