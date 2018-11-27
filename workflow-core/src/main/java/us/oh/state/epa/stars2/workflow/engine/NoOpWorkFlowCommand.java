package us.oh.state.epa.stars2.workflow.engine;

/**
 * <p>
 * Title: NoOpWorkFlowCommand
 * </p>
 * 
 * <p>
 * Description: This command fulfills the automatic activity class definition by
 * doing nothing. It simply fulfills the interface.
 * </p>
 * 
 * <p>
 * This command is intended to support Activities in the "Activity_Template_Def"
 * that need to be automatic activities, but don't have any actual work to do.
 * The current example of this is the "Decompose Order" command. When this
 * activity is being readied, it simply wants to pass the ready command through
 * to the next downstream activity.
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
 * @version 1.0
 */

public class NoOpWorkFlowCommand extends AbstractWorkFlowCommand {
    public NoOpWorkFlowCommand() {
    }

    public void execute(WorkFlowCmdData wfData) throws Exception {
    }
}
