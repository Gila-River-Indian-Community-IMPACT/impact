package us.oh.state.epa.stars2.workflow.engine;


/**
 * Title: WorkFlowRunner.
 * 
 * <p>
 * Description: This is a simple (and primitive) utility that is used to run the
 * workflow engine in its own JVM on a Windows or Unix machine, i.e., not as a
 * Daemon process. Using technique, a remote debugger can easily be attached to
 * the JVM.
 * </p>
 * 
 * <p>
 * Using this technique requires some support from a shell environment of some
 * sort. On windows, I strongly recommend that you download and use the "cygwin"
 * Unix emulator package (it's free from www.cygwin.com). It even offers an
 * X-Server package, XFree86, that may be usable with the workflow designer.
 * </p>
 * 
 * <p>
 * I have a "bash" shell script that can be used to bring up the workflow engine
 * via this utility. It is "/home/jec/mozart/start_workflow" and you are welcome
 * to copy it. In its current form, the script is fairly primitive, but will get
 * a workflow engine up and running. The big problem is that in order to
 * terminate the JVM, you have to exit the window running the JVM (at least this
 * is the case on Windows). The work-around is to run the script in its own
 * window.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 */

public class WorkFlowRunner {
    public WorkFlowRunner() {
    }

    public static void main(String[] args) {
    	// decouple workflow
//        ComponentLauncher.main(new String[] { "app.WorkFlow" });
    }
}
