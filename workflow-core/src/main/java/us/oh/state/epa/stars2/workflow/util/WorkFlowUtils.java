package us.oh.state.epa.stars2.workflow.util;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataField;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.workflow.engine.ProcessFlow;

public class WorkFlowUtils {
    public static final String PROV = "prov";
    public static final String SFA = "sfa";
    public static final String DECOMPOSE = "Decompose";
    public static final String NOOP = "NoOp";
    private static final Integer DECOMPOSE_ORDER = new Integer(1);
    private static final Integer DECOMPOSE_PROPOSAL = new Integer(7);
    private static final Integer BILLING_READY = new Integer(2);
    private static final Integer PROPOSAL_READY = new Integer(3);
    private static final String NO_OP_TEMPLATE_NAME = "No-Op Branch";
    private static final int NO_OP_TEMPLATE_ID = 5;
    private static final String COMPLETION = "Completion";
    private static final Integer DFLT_EXP_DURATION = new Integer(3600);
    private static final Integer DFLT_JEO_DURATION = new Integer(2880);

    /**
     * Creates a new ActivityTemplate object.
     * 
     * @param initTemplate
     * @param type
     * @param processCd
     * 
     * @return ActivityTemplate New object.
     */
    public static ActivityTemplate initializeActivityTemplate(
            boolean initTemplate, String type, String processCd) {
        ActivityTemplate at = new ActivityTemplate();

        if (type == NOOP) {
            at.setActivityTemplateDefId(new Integer(NO_OP_TEMPLATE_ID));
            at.setActivityTemplateNm(NO_OP_TEMPLATE_NAME);
            at.setExpectedDuration(new Integer(3600));
        } else if (type == COMPLETION) {
            if (PROV.equals(processCd)) {
                at.setActivityTemplateDefId(DECOMPOSE_ORDER);
                at.setActivityTemplateNm("Decompose Order");
            } else {
                at.setActivityTemplateDefId(DECOMPOSE_PROPOSAL);
                at.setActivityTemplateNm("Decompose Proposal");
            }
            at.setExpectedDuration(new Integer(1));
            at.setServiceId(null);
        } else if (type == DECOMPOSE) {
            if (PROV.equals(processCd)) {
                at.setActivityTemplateDefId(BILLING_READY);
                at.setActivityTemplateNm("Billing Ready");
            } else {
                at.setActivityTemplateDefId(PROPOSAL_READY);
                at.setActivityTemplateNm("Proposal Ready");
            }
            at.setExpectedDuration(new Integer(1));
            at.setServiceId(null);
        }

        at.setDeprecatedInd("N");
        at.setInTransitionDefCd("IL");
        at.setOutTransitionDefCd("IL");
        at.setMilestoneInd("Y");

        if (initTemplate) {
            at.setInitTaskInd("Y");
        } else {
            at.setInitTaskInd("N");
        }

        return at;
    }

    /**
     * Creates a new ProcessTemplate object. This is a "dynamic"
     * ProcessTemplate, meaning that it represents an order or a proposal. The
     * object is not stored in the database. The name and description of the
     * template will be either "Order: xxx" or "Proposal: yyy" depending on the
     * value of "type".
     * 
     * @param type
     *            Either "prov" for orders or "sfa" for proposals.
     * @param extId
     *            Order Id or Proposal Id.
     * 
     * @return ProcessTemplate New object.
     */
    public static ProcessTemplate initializeProcessTemplate(String type,
            Integer extId) {
        // Pick a template name based on "type".

        String ptype = "Order: " + extId.toString();

        if (type.equals(SFA)) {
            ptype = "Proposal: " + extId.toString();
        }

        // Create the basic ProcessTemplate.

        ProcessTemplate pt = new ProcessTemplate();
        pt.setProcessCd(type);
        pt.setProcessTemplateNm(ptype);
        pt.setProcessTemplateDsc(ptype);
        pt.setDynamicInd("Y");
        pt.setDeprecatedInd("N");

        // For now, set the expected and jeopardy durations to their default
        // values.

        pt.setExpectedDuration(DFLT_EXP_DURATION);
        pt.setJeopardyDuration(DFLT_JEO_DURATION);

        return pt;
    }

    /**
     * Loads DataFields in ProcessFlow "pf" with values from the service details
     * in the database. The original DataFields, "ptFields" are associated with
     * the ProcessTemplate used to build the ProcessFlow "pf". These objects are
     * initialized with values from the database and are returned ready to be
     * the DataFields for the ProcessFlow.
     * 
     * @param pf
     *            ProcessFlow to load DataField values for.
     * @param ptFields
     *            DataFields associated with the ProcessTemplate.
     * 
     * @return DataField[] DataFields associated with "pf".
     * 
     * @throws java.lang.Exception
     *             Database access error.
     */
    public static DataField[] loadDataFields(ProcessFlow pf,
            DataField[] ptFields) {
        // If there are no DataFields, we have nothing to load.
        if ((ptFields != null) && (ptFields.length != 0)) {
            Integer processId = pf.getProcessId();

            // Initialize all of the DataFields to 'blank'. This way, they have
            // some sort of value if we don't find one in the database.
            for (DataField df : ptFields) {
                df.setProcessId(processId);
                if (df.getDataValue() == null) {
                    df.setDataValue("");
                }
            }
        }

        return ptFields;
    }

    /**
     * Creates a Transition object that links the "from" template to the "to"
     * template. The transition type is set to "transType". The name of the
     * Transition object is generated as "Trans <from template id>.<to template
     * id>", for example, "Trans 1234.5678". The Transition is not persisted to
     * the database.
     * 
     * @param from
     *            ActivityTemplate
     * @param to
     *            ActivityTemplate
     * @param transType
     *            Transition type (see "Transition_Def.transition_def_cd").
     * 
     * @return A Transition object.
     */
    public static Transition createTransition(ActivityTemplate from,
            ActivityTemplate to, String transType) {
        String transName = "Trans " + from.getActivityTemplateId().toString()
                + "." + to.getActivityTemplateId().toString();

        // We need a ProcessTemplateId for referential integrity. Either
        // input object will work.
        Integer processTemplateId = from.getProcessTemplateId();

        Transition t = new Transition(transName, from.getActivityTemplateId(),
                from.getActivityTemplateNm(), to.getActivityTemplateId(), to
                        .getActivityTemplateNm(), transType);

        t.setProcessTemplateId(processTemplateId);
        return t;
    }

    /**
     * Validates the workflow type to verify that it exists and identifies a
     * known workflow type. If either of these conditions is untrue, then the
     * parameter is invalid and an Exception is thrown. If this method returns
     * without an exception, then validation was successful.
     * 
     * @param type
     *            Workflow type.
     * @param name
     *            Parameter name.
     * 
     * @throws java.lang.Exception
     *             Null or invalid workflow type.
     */
    public static void validateType(String type, String name) throws Exception {
        if ((type == null) || (type.length() == 0)) {
            String errMsg = "Workflow type for name = [" + name + "] is NULL "
                    + "or empty.";

            throw new Exception(errMsg);
        }

        if (!type.equals(PROV) && !type.equals(SFA)) {
            String errMsg = "Invalid type for " + name + ", type = " + type
                    + ".";
            throw new Exception(errMsg);
        }
    }
}
