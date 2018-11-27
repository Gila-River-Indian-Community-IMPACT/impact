package us.oh.state.epa.stars2.framework.daemon;

import org.apache.log4j.Logger;

/**
 * This class contains a hook that allows for a graceful shutdown when the VM
 * shuts down. Any subclass that implements <code>run()</code> should watch
 * for the value of <code>initShutdown()<code> to return <code>
 * true</code>. Once the subclass has done any clean up and is ready for
 * shutdown, it should call <code>shutdownComplete()</code>
 *
 * @author wilcoxa
 * @version $Revision: 1.9 $
 * @see java.lang.Thread
 * @see us.oh.state.epa.stars2.framework.daemon.ManagedComponent
 */
public abstract class ManagedThread extends Thread {
    protected transient Logger logger = Logger.getLogger(this.getClass());
    protected boolean initShutdown;
    protected boolean shutdownComplete;

    private ShutdownHook sd = new ShutdownHook(this);

    public ManagedThread() {
        Runtime.getRuntime().addShutdownHook(new Thread(sd));
    }

    public abstract void run();

    class ShutdownHook implements Runnable {
        ManagedThread parent;

        ShutdownHook(ManagedThread t) {
            parent = t;
        }

        public void run() {

            logger.debug("Shutdown initiated.");
            parent.initShutdown = true;
            parent.shutdown();

            while (!parent.shutdownComplete) {
                waitShutdown();
            }
                
            logger.debug("Shutdown complete for " + parent.getName());
            parent = null;
        }

        public synchronized void waitShutdown() {

            try {
                if (!parent.shutdownComplete) {
                    this.wait();
                }
            }
            catch (InterruptedException e) {
                // eat the exception and exit
                e.printStackTrace();
            }
        }

        public synchronized void shutdownComplete() {
            this.notify();
        }

    }

    public synchronized void shutdown() {
        initShutdown = true;
        notify();
    }

    /**
     * @return <code>true</code> when the VM is shutting down.
     */
    public final boolean initShutdown() {
        return initShutdown;
    }

    /**
     * This should be called the a subclass after the <code>initShutdown()
     * </code>
     * returns <code>true</code> and this subclass has done any required
     * shutdown stuff (usually just finishing what it's doing).
     * 
     */
    public synchronized void shutdownComplete() {
        shutdownComplete = true;
        if (sd != null) {
            sd.shutdownComplete();
            sd = null;
        }
    }
}
