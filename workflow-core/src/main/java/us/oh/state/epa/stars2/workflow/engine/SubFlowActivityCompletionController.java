package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.workflow.Activity;


/**
 * <p>Title: SubFlowActivityCompletionController</p>
 *
 * <p>Description: Handles completion of an Activity whose sub-flow ProcessFlow
 *                 has completion.  In this case, we need to save state and
 *                 then do want the regular completion controller does.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: MentorGen, LLC</p>
 * @author J. E. Collier
 * @version 1.0
 */

public class SubFlowActivityCompletionController extends ActivityCompleteController
{
   /**
    * Constructor.  Takes a reference to the Activity whose sub-flow has
    * completed.
    *
    * @param a Activity.
    */
   public SubFlowActivityCompletionController (Activity a)
   {
      super (a) ;
   }

   /**
    * Framework method.  Marks this Activity as complete and updates its state
    * to the database.  Then calls a base class method to figure out what to
    * do next.  Any errors or failures are added to "resp".
    *
    * @param resp WorkFlowResponse object.
    */
   public void execute (WorkFlowResponse resp, WorkFlowRequest rqst)
   {
         //  Mark the Activity as done and save it.  If that fails, then we are
         //  kind of stuck.

      synchronized (this.activity)
      {
         try
         {
            this.completeActivity () ;
            Controller.logger.debug(": SubFlowActivityCompletionController.execute(): process_id=" + 
        			activity.getProcessId() + ", activity_template_id=" + 
        			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
            this.saveToDatabase () ;
         }
         catch (Exception e)
         {
             Controller.logger.error(e.getMessage(), e) ;
            resp.addError (e.getMessage ()) ;
            return ;
         }
      }
         //  Let the base class figure out what to do next.

      super.execute (resp, rqst) ;
   }
}
