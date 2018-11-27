package us.oh.state.epa.stars2.framework.util;

public class StopWatch {
    private long start;
    private long elapsed;
    private boolean running; // could use start == 0 to mean

    // !running
    public synchronized final void start() {
        // are we already started?
        //
        if (!running) {
            running = true;
            start = System.currentTimeMillis();
        }
    }

    public synchronized final void stop() {
        if (running) {
            long stop = System.currentTimeMillis();
            running = false;
            elapsed += (stop - start);
        }
    }

    public synchronized final void reset() {
        start = 0;
        elapsed = 0;
        running = false;
    }

    public synchronized final long elapsedTime() {
        long ret = elapsed;

        if (!running) {
            // get the elapsed time of a running stop watch ...
            //
            long stop = System.currentTimeMillis();
            ret = elapsed + (stop - start);
        }
        return ret;
    }

    public final String toString() {
        StringBuffer b = new StringBuffer("Total time: ");
        b.append(elapsedTime());
        b.append(" ms");
        return b.toString();
    }

    public final String toString(int reps) {
        StringBuffer b = new StringBuffer(toString());
        b.append("Average time: ");
        b.append(elapsedTime() / reps);
        return b.toString();
    }
}
