package us.oh.state.epa.stars2;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2006 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @author Kbradley
 *
 */
public final class CommonConst {
    /**
     * Apply constant.
     */
    public static final String APPLYSTRING = "Apply";

    /**
     * Clear contstant.
     */
    public static final String CLEARSTRING = "Clear";

    /**
     * Constants for report exporting types.
     */
    public static enum ExportType {
        HTML, EXCEL, PDF, RTF, CSV
    }

    /**
     * Constants for bulk operations types.
     */
    public static enum BulkOperationsType {
        FACILITY_ROLE, TV_APP_REMINDER, TV_EXP_REMINDER, PER_FORM_GEN
    }
    
    public static final String EXTERNAL_APP = "stars2web";
    public static final String INTERNAL_APP = "stars2";
    public static final String PUBLIC_APP = "impactpub";
    
    /**
     * Database schemas
     */
    public static final String STAGING_SCHEMA = "Staging";
    public static final String READONLY_SCHEMA = "ReadOnly";
    
    /**
     * Pre-defined users.
     * Note: these must correspond to the appropriate values set in the
     * DB load script: B00_Stars2CommonTableLoad.sql
     *
     */
    public static final int ADMIN_USER_ID = 1;
    public static final int LEGACY_USER_ID = 2;
    public static final int GATEWAY_USER_ID = -100;
    public static final int UNKNOWN_USER_ID = 4;
    
    /* Number of facility roles */
//    public static final int NUM_FACILITY_ROLES = 16;
    
    public static final String PREVIOUS_SUBMIT_IN_PROCESS_MSG = 
        "was previously submitted and is still processing";
    public static final String PREVIOUS_SUBMIT_SUCCEEDED_MSG = 
        "was previously submitted and completed processing";
    public static final String PREVIOUS_SUBMIT_FAILED_MSG = 
        "was previously submitted and failed processing";
    public static final String INVALID_SUBMIT_PIN_MSG =
    	"The pin entered is not valid for account"; 
    
//    public static final int GATEWAY_ITR_DUMMY_FPID = 490;
//    public static final String GATEWAY_ITR_DUMMY_FAC_ID = "EBIZ_ITR_FACILITY";
    
    public static final String TINYMCE_MODE_READONLY = "readonly";
    public static final String TINYMCE_MODE_DESIGN = "design";
    
    private CommonConst() {
    }
}
