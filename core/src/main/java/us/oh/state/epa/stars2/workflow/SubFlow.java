package us.oh.state.epa.stars2.workflow;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef;

/**
 * <p>Title: SubFlow</p>
 *
 * <p>Description: This is a thin wrapper for sub-flows which are similar to
 *                 Activities in the workflow designer.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: MentorGen, LLC</p>
 * @author J. E. Collier
 * @version 1.0
 */

public class SubFlow extends Activity {
    public SubFlow(ActivityTemplate at, ActivityTemplateDef atd) {
        super(at, atd);
    }

    public SubFlow(Integer pseudoKey, ActivityTemplateDef atd) {
        super(pseudoKey, atd);
    }
}
