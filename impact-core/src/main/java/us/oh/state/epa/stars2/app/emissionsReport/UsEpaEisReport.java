package us.oh.state.epa.stars2.app.emissionsReport;

/*
 * LEFT TO DO LATER WHEN US EPA RESETS LAST EMISSIONS YEAR IN THE EUs.
 * Only set it when there will not be another report.
 * A comment that was written in preparation for this:
 * 
                                  // Since the EU is in this report, it was not marked shutdown until the calendar year of the report or later,
                                 // even if the date entered is further in the past.
                                 // Therefore if shutdown in the year of the report or earlier,
                                 // for Last Emissions Year we will specify the year of the report.
                                 // Otherwise, either earlier reports look to be in error by having emissions after shutdown
                                 // or this report is in error by specify shutdown earlier than previous emissions.

 */

/*
 * WHAT FACILITY OPERATING STATUS TO SEND
 * If there is an emissions report we have one of these cases:
    If  Shutdown this reporting year—mark facility (for EIS) as shutdown (if shutdown next year—for this year we do not tell US EPA shut down)
    Otherwise if zero emissions—mark facility Operating-No Emissions
    Otherwise mark facility Operating.

If there is no emissions report and the US EPA has this facility and US EPA has different status than Stars2 then it is one of these cases:
     If US EPA already has Shut down, then do nothing but write log error since should not be marked shutdown.
    Otherwise
        If Stars2 Shutdown—send them Shutdown
    Otherwise we have the state operating or inactive and the US EPA has operating or operating-no emissions—and they do not match.  We will ignore this mismatch.

 */

/*  What values does it use.
 *   If the alternate profile has Lat/Long it uses those values rather than the Lat/Long of the 
 *   profile tied to the year being reported.  If the profile for the year being reported
 *   is specified as portable, then no lat long provided.
 *
 *   If the EU has a AQD Description that is used; otherwise the User Description is used if it exists.
 */

/*
 * 
 * Older comments
 * 
 * If we change the facility to shutdown in or before the reporting year, then there should be no report and
 * we don't need to add any EU or process information in the mismatch file. will not be in the other files since
 * there will be no report.
 * 
 * Why don't I put facility operating status in the facility inventory file in all cases?  Should not be needed.
 * 
 * Why don't I put the facility operating status in the mismatch file when it is operating but the date is not correct.
 * 
 * RELEASE_POINT_STACK_DIAMETER_MEASURE missing from default stack--somehow a null gets in???
 * 
 * 
 * 
 * DATA FIX:
 * stack min height = 1
 * gas flow rate max = 12,000,000.

---------

We don't have the attribute RELEASE_POINT_FUGITIVE_ANGLE_MEASURE; so leave it out?

Do you want to supply a value for the Facility Category Code?
Why does State and Country FIPS Code have just Canada and Mexico?
Do you want to add Location Address Country Code="USA" to facility address?
How much do you want to add to Geographic Coordinates besides lat and long?
These are for facility:
    "cer:SourceMapScaleNumber"
    "cer:HorizontalAccuracyMeasure"
    "cer:HorizontalAccuracyUnitofMeasure"
    "cer:HorizontalCollectionMethodCode"
    "cer:HorizontalReferenceDatumCode"
    "cer:GeographicReferencePointCode"
    "cer:DataCollectionDate"
    "cer:GeographicComment"
    "cer:VerticalMeasure"
    "cer:VerticalUnitofMeasureCode"
    "cer:VerticalCollectionMethodCode"
    "cer:VerticalReferenceDatumCode"
    cer:VerificationMethodCode"
    "cer:CoordinateDataSourceCode"
    "cer:GeometricTypeCode"
    "cer:AreaWithinPerimeter"
    "cer:AreaWithinPerimeterUnitofMeasureCode"
    "cer:PercentofAreaProducingEmissions"


OK to leave FirstInventoryYear & LastInventoryYear out from ControlApproach Complex Type?

In ReportingPeriod, the pdf documentation indicates that there is attribute StartDate
but the file CER_StyleSheet.xslt has Effective Date instead.  I don't think we use it anyway.

Do we have/need a value for CalculationDataSource in ReportingPeriod?

It says that the EmissionsCreationDate is the Date that the data being submitted were created, or the date when the
model generating the data was run.  What can this date be for 2009 emissions data?  The example given
specifies 12/31/2009.  That seems wrong....
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import oracle.adf.view.faces.model.UploadedFile;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.DefaultStackParms;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsMaterialActionUnits;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsVariable;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FireRow;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.MultiEstablishment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.database.dbObjects.util.DbInteger;
import us.oh.state.epa.stars2.def.ActionsDef;
import us.oh.state.epa.stars2.def.County;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EISSubmissionTypeDef;
import us.oh.state.epa.stars2.def.EgOperatingStatusDef;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.def.NonToxicPollutantDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.UnitDef;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.facility.FacilityValidation;
import us.oh.state.epa.stars2.webcommon.reports.ControlInfoRow;
import us.oh.state.epa.stars2.webcommon.reports.EmissionRow;
import us.oh.state.epa.stars2.webcommon.reports.ReportTemplates;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class UsEpaEisReport extends ValidationBase {
	
	/*
	 * These are the error messages that can be generated These are message
	 * fragments that could be searched for in the generated log. There will be
	 * additional text prior to and often after this fragment. If a message
	 * below includes "  ...  ", that means there is more message text here but
	 * variable. this never occurs writing final tag failed exists in US EPA
	 * database but not in STARS2 database. Do manual cleanup! has no submitted
	 * report or approved report has no approved report; will use submitted
	 * report Failed to read report does not include an EIS; will use report
	 * anyway Did not locate profile Failed on getEmissionsReports() for
	 * facility VALIDATION ERROR Facility <--these are from running the report
	 * validate. Exception usEpaEisReport.process() failed close failed failed
	 * to getYearlyReportingInfo() for facility cannot translate operating
	 * status for facility missing shut down date for facility SCSC Id not set
	 * for facility there is no NAICS code for facility there is no City for
	 * facility there is no State for facility there is no zip code for facility
	 * latitude not specified for failed to find CountyDef obj for countyCd=
	 * could not locate EU with correlation id cannot translate emission unit
	 * operating status for facility missing shutdown date for facility missing
	 * operating dates for facility missing initial install date for facility
	 * Duplicate egress point in facility Latitude/Longitude not within county
	 * boundaries for getFacilityService().coordinatesInCountyBorders() failed
	 * for Latitude/Longitude not specified for call to
	 * retrieveDefaultStackParms() failed using SCC ID there are no default
	 * stack attributes for SCC Id one of height, diameter, temperature or flow
	 * is null in default stack parmeter record with SCC ID The default is null
	 * for scc id ... --must be a fugitive scc used for stack--using minimum
	 * outside of legal range for facility ... replaced with min or max
	 * ApplicationException at scc value not set for a process in report failed
	 * to find EmissionProcess for report amount of material is missing ...
	 * --skipped. measure(units) of material is missing ... --skipped. material
	 * code is missing ... --skipped. some schedule information is zero. ...
	 * Changed to: problem with schedule for emissions inventory Previous log4j
	 * errors about egress point are in report got ApplicationException at
	 * report had no stacks so looking for stacks in more recent profile Stack
	 * emissions specified in report ... for some pollutants but no stacks
	 * found--creating placeholder stack Method calculation code is missing.
	 * (will never occur) serializeEmpty() failed call to locatePeriodNames()
	 * failed for report failed to locate ReportTemplates for year failed to
	 * validate the facility failed to validate report egress Point ... exists
	 * only for US EPA--Identifier is marked ended. did not find EU. Emissions
	 * inventory there is no NAICS code for facility ... --skipped. failed to
	 * getYearlyReportingInfo() for facility cannot translate operating status
	 * for facility missing shut down date for facility ... --skipped. failed on
	 * locateUsingOldEuName() for facility Emission Unit ... exists only for US
	 * EPA--Identifier is marked shutdown. missing shutdown date for facility
	 * ... --replaced with emission Unit ... has no state set--ignored.
	 */

    /*  THIS PROGRAM DOES:
     * 
     * A report year must be provided and will default to the year before the current year.
     * 
     * THREE XML files are generated:
     * 1)  Reconciliation facility inventory which corrects the facility data held by the US EPA.
     * 2)  facility inventory corresponding to facilities with TV/SMTV reports
     * 3)  point data generated from the TV/SMTV report.
     * 
     * All three are send to the US EPA but only the last two are sent to LATCO???
     * This means that 2) and 3) must provide all attributes since information from the
     * previous year is not kept.
     * 
     * The complete report is generated from
     * data from all TV and SMTV approved emissions inventories for that year that include an EIS.
     * This is all approved TV reports and any approved SMTV reports that have added the EIS report.
     * If the report is in submitted state then an error listing is generated including each of
     * these submitted report Ids and facility Ids.
     * Note that EIS here means the OHIO EPA Emissions Inventory Statement--not 
     * the US EPA EIS (XML) Report that this program generates.
     * 
     * ???
     *   If shutdown then generate shutdown info (previous report may already indicate this)
     *   If NTV reporting category then generate low emissions info (previous report may already indicate this)
     *   If approved SMTV and EIS not inclued then generate low emissions info (previous report may already indicate this).
     *   If TV or SMTV and report not submitted generate error list with TV listed first.
     * 
     */

    /*
     *     Identifiers           pre-STARS2        STARS2
     *   cer:ProgramSystemCode     OHEPA          OHEPA
     *     facility              facilityId      facilityId
     *    emission unit           epaEmuId       epaEmuId
     *     egress point        generated name     correlationId
     *       process           generated name       sccId
     */

    /*
     * Reconcile US EPA values and Stars2 values by creating a Facility Inventory that
     * corrects the values held by the US EPA.
     * 
     * FACILITY LEVEL:
     * US EPA has facility id and STARS2 does not:  Just write an error message in the log for manual handling.  Skip everything else for the facility.
     * US EPA has facility id and STARS2 has the facility but no TV/SMTV emissions inventory:  set FACILITY_SITE_STATUS_CODE to "ONRE" meaning "Operating but State/Local/Tribe Not Reporting Emissions".
     * STARS2 has facility with TV/SMTV emissions inventory and US EPA does not:  add the "new" facility.
     * 
     * EMISSIONS UNIT LEVEL:
     * US EPA has EU and STARS2 does not:
     *   What do we do:
     *       Question:  What values that US EPA has do we change:  Set Shutdown?  Set Identifier End Date to some date?
     * STARS2 has EU and US EPA does not:  add the "new" EU.
     *   If operating then set operating and set year of completed installation as status change year.
     *   If shutdown, then set shutdown and set year of shutdown as status change year.
     *   For either case, set cer:UnitOperationDate to date of completed initial installation.
     *   For either case set Identifier Begin Date as year of report.  Since "real" EU, leave Identifier End Date blank.
     * Both have the EU:  Compare States of EU.  It should be either Operating or Shutdown.
     *   If changed to shutdown, then change the state and set year of shutdown as status change year.
     *   If changed to operating, then change the state and set year of completed installation as status change year.
     *   Question:  If changed to operating, do we actually set the status change year to the year of completed installation or to the year of this report.
     *   Question:  If already operating, do we set cer:UnitOperationDate to date of completed initial installation if not correct?
     *   Question:  If already shutdown, do we set status change year if not correct?
     *   
     * In both cases where STARS2 has the EU, provide additional information if the EU's processes specify emissions.
     * 
     * 
     * PROPOSAL
     * EMISSIONS UNIT LEVEL:
     * US EPA has EU and STARS2 does not:
     *   Set Identifier End Date to today's date.  We will set the end date on all processes and egress (release) points associated with that phantom EU.  Make no other changes.  
     * STARS2 has EU and US EPA does not:  add the "new" EU.
     *   If operating then set operating and set year of completed installation as status change year.
     *   If shutdown, then set shutdown and set year of shutdown as status change year.
     *   For either case, set cer:UnitOperationDate to date of completed initial installation.
     *   For either case set Identifier Begin Date to today's date.  Since "real" EU, leave Identifier End Date blank.
     * Both have the EU:  Compare States of EU.  It should be either Operating or Shutdown.
     *   If changed to shutdown, then change the state and set year of shutdown as status change year.
     *   If changed to operating, then change the state and set year of completed installation as status change year.  Also set cer:UnitOperationDate to the date the unit began operating.
     *   If already operating, then make the fields status change year and cer:UnitOperationDate correct if they are not.
     *   If already shutdown, set status change year if not correct.
     *   
     * In both cases where STARS2 has the EU, provide additional information if the EU's processes specify emissions.
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * PROCESS LEVEL:
     * The identifier label is the Ohio SCC code and the SCC code provided is the federal SCC code that this Ohio SCC code maps to.
     * US EPA has a process and STARS2 does not:  remove it by supplying an EndDate on identifier.  Skip everything else for the process.
     * STARS2 has EU and US EPA does not:  add the "new" process and set the Begin Date in identifer to the report year.
     * Both have the process:  Do not update the identifier.  However, make sure it has the correct SCC code.
     * 
     * In both cases where STARS2 has the process, provide additional information if the process is in the report specifies emissions.
     *  
     * 
     * EGRESS POINT LEVEL:
     * US EPA has EP and STARS2 does not (look at all EPs):  remove it by supplying an EndDate on identifier.
     * STARS2 has EP and US EPA does not (restrict to the small group to be reported):  add the "new" EP.
     */

    /*
     * Information will be provided to the US EPA for the following:
     *    All Facilities the US EPA has in their database for AQD plus facilities with TV/SMTV emissions inventories.
     *        If no TV/SMTV emissions inventory, then just make sure facility operating state/date set correctly.
     *        Do not update any facility attributes, information about EUs, Processes or Egress Points.
     *    All Emissions Units the US EPA has in their database for AQD plus EUs showing emissions
     *        If Emission Unit has no emissions in TV/SMTV emissions inventory, then just make
     *        sure the operating state/date set correctly.  Do not update any information about Processess
     *        or Egress Points.
     *    All Emissions Processes the US EPA has in their database for AQD plus those  showing emissions
     *        If Process has no emissions then only provide identifier End_Date (if appropriate), do not
     *        supply process_description or process_comment.
     *    All Egress Points the US EPA has in their database for AQD plus (at most) five selected by STARS2 per Emission Unit.
     *        If not one of the Egress Points selected because of the report, then only provide identifier
     *        End_Date (if appropriate), do not provide updated attribute values.
     *    
     *    The first year the new system is run, all the existing Egress Points will be "retired" since their
     *    identifiers are changed.  In subsequent years, the number of egress points known by the US EPA may
     *    increase above five if a different set of five egress points are provided than the previous year.
     *    
     *    Any time a process changes SCC code value, the old process is "retired" and a new one created since
     *    the process Id is the SCC code value.
     *    
     *    Three files are created.
     *    Mismatched facility inventory information:
     *       All operating changes and new or no-longer-existing objects are reported here
     *    Facility inventory information
     *       No operating information is provided here.  Everything in the report is already specified
     *       as existing (because of the mismatched facility inventroy information).
     *    Point information
     */
    
	private static final long serialVersionUID = 3794823393658535347L;

	public static String DEFAULT_AUTHOR_NAME = "UNICON International, Inc";
	public static String DEFAULT_AQD_CONTACT = "" ;
	public static String DEFAULT_EIS_LOGIN = "";
	
	private static String lastFacilityRead = null; // used to locate the facility the error an error occurred on.
    private static String nextLastFacilityRead = null; // used to locate the facility the error an error occurred on.

    // Limitations/validations implemented
    private final  int submittalCommentMaxSize = 400;
    private final  int unitDescriptionMaxSize = 100;
    private final  int facilityDescriptionMaxSize = 100;
    private final  int emissionsCommentMaxSize = 300;
    private final  int addressLineMaxSize = 60;

    //private final String OLD_PROGRAM_SYSTEM_CODE_VALUE = "OHEPA";
    private final String NEW_PROGRAM_SYSTEM_CODE_VALUE = "OHEPA";
    private String identifierStartDateTime = "";
    private String currentDateYYYMMDD = "";
    private String beginPeriodDateYYYMMDD = "";
    private String beforeBeginPeriodDateYYYMMDD = "";
    private long uniqueCode = 0;

    private final String HDR_DOCUMENT = "hdr:Document";
    private final String HDR_HEADER = "hdr:Header";
    private final String HDR_AUTHOR_NAME = "hdr:AuthorName";
    private final String HDR_ORGANIZATION_NAME = "hdr:OrganizationName";
    private final String HDR_ORGANIZATION_NAME_VALUE = "Ohio EPA, Division of Air Pollution Control";
    private final String HDR_DOCUMENT_TITLE = "hdr:DocumentTitle";
    private final String HDR_DOCUMENT_TITLE_VALUE = "EIS";
    private final String HDR_COMMENT = "hdr:Comment";
    private final String HDR_CREATION_DATE_TIME = "hdr:CreationDateTime";
    private final String HDR_DATAFLOW_NAME = "hdr:DataFlowName";
    private final String HDR_DATAFLOW_NAME_VALUE = "EIS_v1_0";
    private final String HDR_PROPERTY = "hdr:Property";
    ;    private final String HDR_PROPERTY_NAME = "hdr:PropertyName"; // SubmissionType  is "Production" or "QA"
    // DataCategory is "FacilityInventory" or "Point"
    private final String HDR_PROPERTY_VALUE = "hdr:PropertyValue";
    private final String HDR_PAYLOAD = "hdr:Payload";

    private final String CERS = "cer:CERS";
    private final String EMISSIONS_CREATION_DATE = "cer:EmissionsCreationDate";
    private final String USER_IDENTIFIER = "cer:UserIdentifier";
    private final String EMISSIONS_YEAR = "cer:EmissionsYear";
    private final String SUBMITTAL_COMMENT = "cer:SubmittalComment";

    private final String FACILITY_SITE = "cer:FacilitySite";
    private final String FACILITY_SITE_DESCRIPTION = "cer:FacilitySiteDescription";
    private final String FACILITY_SITE_COMMENT = "cer:FacilitySiteComment";
    private final String FACILITY_CATEGORY_CODE = "cer:FacilityCategoryCode";
    private final String FACILITY_SITE_NAME = "cer:FacilitySiteName";
    private final String FACILITY_SITE_STATUS_CODE = "cer:FacilitySiteStatusCode";  //ONRE -- Operating but State/Local/Tribe Not Reporting Emissions
    private final String FACILITY_SITE_STATUS_CODE_YEAR = "cer:FacilitySiteStatusCodeYear";
    private final String FACILITY_NAICS = "cer:FacilityNAICS";
    private final String NAICS_CODE = "cer:NAICSCode";
    private final String FACILITY_IDENTIFICATION = "cer:FacilityIdentification";
    private final String FACILITY_SITE_IDENTIFIER = "cer:FacilitySiteIdentifier";
    private final String PROGRAM_SYSTEM_CODE = "cer:ProgramSystemCode";
    private final String STATE_AND_COUNTY_FIPS_CODE = "cer:StateAndCountyFIPSCode";
    private final String FACILITY_SITE_ADDRESS = "cer:FacilitySiteAddress";
    private final String LOCATION_ADDRESS_TEXT = "cer:LocationAddressText";
    private final String SUPPLEMENTAL_LOCATION_TEXT = "cer:SupplementalLocationText";
    private final String LOCALITY_NAME = "cer:LocalityName";
    private final String LOCATION_ADDRESS_STATE_CODE = "cer:LocationAddressStateCode";
    private final String LOCATION_ADDRESS_POSTAL_CODE = "cer:LocationAddressPostalCode";
    private final String LOCATION_ADDRESS_COUNTRY_CODE = "cer:LocationAddressCountryCode";
    private final String FACILITY_SITE_GEOGRAPHIC_COORDINATES = "cer:FacilitySiteGeographicCoordinates";
    private final String LATITUDE_MEASURE = "cer:LatitudeMeasure";
    private final String LONGITUDE_MEASURE = "cer:LongitudeMeasure";
//    private final String SOURCE_MAP_SCALE_NUMBER = "cer:SourceMapScaleNumber";
//    private final String HORIZONTAL_ACCURACY_MEASURE = "cer:HorizontalAccuracyMeasure";  // TODO DENNIS around line 160 facility  FINISH
//    private final String HORIZONTAL_COLLECTION_METHOD_CODE = "cer:HorizontalCollectionMethodCode";
//    private final String HORIZONTAL_REFERENCE_DATUM_CODE = "cer:HorizontalReferenceDatumCode";
//    private final String GEOGRAPHIC_REFERENCE_POINT_CODE = "cer:GeographicReferencePointCode"; // TODO critical
//    private final String DATA_COLLECTION_DATA = "cer:DataCollectionDate"; // TODO critical
//    private final String GEOGRAPHIC_COMMENT = "cer:GeographicComment";
//    private final String VERIFICATION_METHOD_CODE = "cer:VerificationMethodCode";
//    private final String COORDINATE_DATA_SOURCE_CODE = "cer:CoordinateDataSourceCode"; // TODO critical
//    private final String GEOMETRIC_TYPE_CODE = "cer:GeometricTypeCode"; // TODO critical

    private final String EMISSIONS_UNIT = "cer:EmissionsUnit";
    private final String UNIT_DESCRIPTION = "cer:UnitDescription";
    private final String UNIT_TYPE_CODE = "cer:UnitTypeCode";
    private final String UNIT_DESIGN_CAPACITY = "cer:UnitDesignCapacity";
    private final String UNIT_DESIGN_CAPACITY_UNIT_OF_MEASURE_CODE = "cer:UnitDesignCapacityUnitofMeasureCode";
    private final String UNIT_STATUS_CODE = "cer:UnitStatusCode";
    private final String UNIT_STATUS_CODE_YEAR = "cer:UnitStatusCodeYear";
    private final String UNIT_OPERATION_DATE = "cer:UnitOperationDate";
    private final String UNIT_IDENTIFICATION = "cer:UnitIdentification";
    private final String IDENTIFIER = "cer:Identifier";
    private final String EFFECTIVE_DATE = "cer:EffectiveDate";
    private final String END_DATE = "cer:EndDate";
    private final String UNIT_COMMENT = "cer:UnitComment";
//    private final String UNIT_CONTROL_APPROACH = "cer:UnitControlApproach";  // not used--USE processControlApproach instead

    private final String UNIT_EMISSIONS_PROCESS = "cer:UnitEmissionsProcess";
    private final String PROCESS_IDENTIFICATION = "cer:ProcessIdentification";
    private final String PROCESS_COMMENT = "cer:ProcessComment";
    private final String REPORTING_PERIOD = "cer:ReportingPeriod";
    private final String REPORTING_PERIOD_TYPE_CODE = "cer:ReportingPeriodTypeCode";
    private final String EMISSION_OPERATING_TYPE_CODE = "cer:EmissionOperatingTypeCode";
    private final String CALCULATION_PARAMETER_TYPE_CODE = "cer:CalculationParameterTypeCode";
    private final String CALCULATION_PARAMETER_VALUE = "cer:CalculationParameterValue";
    private final String CALCULATION_PARAMETER_UNIT_OF_MEASURE= "cer:CalculationParameterUnitofMeasure";
    private final String CALCULATION_PARAMETER_MATERIAL_CODE = "cer:CalculationMaterialCode";
    private final String CALCULATION_DATA_YEAR = "cer:CalculationDataYear";
//    private final String CALCULATION_DATA_SOURCE = "cer:CalculationDataSource";
    private final String PROCESS_DESCRIPTION = "cer:ProcessDescription";
    private final String LAST_EMISSIONS_YEAR = "cer:LastEmissionsYear";
    private final String PROCESS_CONTROL_APPROACH = "cer:ProcessControlApproach";
//    private final String  CONTROL_APPROACH_DESCRIPTION = "cer:ControlApproachDescription";
    // PercentControlApproachCaptureEfficiency is computed as percent of flow that does not enter
    // first level control equipment (assumes all downstream control equipment has 100% capture.
    // Note that any flow that enters a stack counts.
    private final String  PERCENT_CONTROL_APPROACH_CAPTURE_EFFICIENCY = "cer:PercentControlApproachCaptureEfficiency";
    // PercentControlApproachEffectiveness is computed as 100% if there are no emission factor methods 
    // (because then no hours not operating) for the process
    // otherwise it is the [totalHours - (average uncontrolled hours)]*100/totalHours.
    private final String  PERCENT_CONTROL_APPROACH_EFFECTIVENESS = "cer:PercentControlApproachEffectiveness";
//    private final String  FIRST_INVENTORY_YEAR = "cer:FirstInventoryYear";
//    private final String  LAST_INVENTORY_YEAR = "cer:LastInventoryYear";
    private final String  CONTROL_MEASURE = "cer:ControlMeasure";
    private final String   CONTROL_MEASURE_CODE = "cer:ControlMeasureCode";
    private final String  CONTROL_POLLUTANT = "cer:ControlPollutant";
    private final String   POLLUTANT_CODE = "cer:PollutantCode";
    private final String   PERCENT_CONTROL_MEASURE_REDUCTION_EFFICIENCY = "cer:PercentControlMeasuresReductionEfficiency";

    private final String REPORTING_PERIOD_COMMENT = "cer:ReportingPeriodComment";
    private final String OPERATING_DETAILS = "cer:OperatingDetails";
    private final String ACTUAL_HOURS_PER_PERIOD = "cer:ActualHoursPerPeriod";
    private final String AVERAGE_DAYS_PER_WEEK = "cer:AverageDaysPerWeek";
    private final String AVERAGE_HOURS_PER_DAY = "cer:AverageHoursPerDay";
    private final String AVERAGE_WEEKS_PER_PERIOD = "cer:AverageWeeksPerPeriod";
    private final String PERCENT_WINTER_ACTIVITY = "cer:PercentWinterActivity";
    private final String PERCENT_SPRING_ACTIVITY = "cer:PercentSpringActivity";
    private final String PERCENT_SUMMER_ACTIVITY = "cer:PercentSummerActivity";
    private final String PERCENT_FALL_ACTIVITY ="cer:PercentFallActivity";

    private final String SOURCE_CLASSIFICATION_CODE = "cer:SourceClassificationCode";
    private final String RELEASE_POINT_APPORTIONMENT = "cer:ReleasePointApportionment";
    private final String AVERAGE_PERCENT_EMISSIONS = "cer:AveragePercentEmissions";
    private final String RELEASE_POINT_APPORTIONMENT_IDENTIFICATION = "cer:ReleasePointApportionmentIdentification";
    private final String REPORTING_PERIOD_EMISSIONS = "cer:ReportingPeriodEmissions";
    private final String TOTAL_EMISSIONS = "cer:TotalEmissions";
    private final String EMISSIONS_UNIT_OF_MEASURE_CODE = "cer:EmissionsUnitofMeasureCode";
    private final String EMISSION_FACTOR = "cer:EmissionFactor";
    private final String EMISSION_FACTOR_NUMERIC_UNI_OF_MEASURE_CODE = "cer:EmissionFactorNumeratorUnitofMeasureCode";
    private final String EMISSION_FACTOR_DENOMINATOR_UNIT_OF_MEASURE_CODE = "cer:EmissionFactorDenominatorUnitofMeasureCode";
    private final String EMISSION_CALCULATION_METHOD_CODE = "cer:EmissionCalculationMethodCode";
    private final String EMISSIONS_COMMENT = "cer:EmissionsComment";

    private final String SUPPLEMENTAL_CALCULATION_PARAMETER = "cer:SupplementalCalculationParameter";
    private final String SUPPLEMENTAL_CALCULATION_PARAMETER_TYPE = "cer:SupplementalCalculationParameterType";
    private final String parameterHeatContentType = "Heat Content";
    private final String parameterSulfurType = "Percent Sulfur Content";
    private final String parameterPercentAshType = "Percent Ash Content";
    private final String SUPPLEMENTAL_CALCULATION_PARAMETER_VALUE = "cer:SupplementalCalculationParameterValue";
    private final String SUPPLEMENTAL_CALCULATION_PARAMETER_NUMERATOR_UNIT = "cer:SupplementalCalculationParameterNumeratorUnitofMeasureCode";
    private final String SUPPLEMENTAL_CALCULATION_PARAMETER_DENOMINATOR_UNIT = "cer:SupplementalCalculationParameterDenominatorUnitofMeasureCode";

    private final String RELEASE_POINT = "cer:ReleasePoint";
    private final String EP_GEOGRAPHIC_COORDINATES = "cer:ReleasePointGeographicCoordinates";
    private final String RELEASE_POINT_IDENTIFICATION = "cer:ReleasePointIdentification";
    private final String RELEASE_POINT_TYPE_CODE = "cer:ReleasePointTypeCode";
    private final String RELEASE_POINT_DESCRIPTION = "cer:ReleasePointDescription";
    private final String RELEASE_POINT_STACK_HEIGHT_MEASURE = "cer:ReleasePointStackHeightMeasure";
    private final String RELEASE_POINT_STACK_HEIGHT_UNIT_O_MEASURE_CODE = "cer:ReleasePointStackHeightUnitofMeasureCode";
    private final String RELEASE_POINT_STACK_DIAMETER_MEASURE = "cer:ReleasePointStackDiameterMeasure";
    private final String RELEASE_POINT_STACK_DIAMETER_UNIT_OF_MEASURE_CODE = "cer:ReleasePointStackDiameterUnitofMeasureCode";
//    private final String RELEASE_POINT_EXIT_GAS_VELOCITY_MEASURE = "cer:ReleasePointExitGasVelocityMeasure";
//    private final String RELEASE_POINT_EXIT_GAS_VELOCITY_UNIT_OF_MEASURE_CODE = "cer:ReleasePointExitGasVelocityUnitofMeasureCode";
    private final String RELEASE_POINT_EXIT_GAS_FLOW_RATE_MEASURE = "cer:ReleasePointExitGasFlowRateMeasure";
    private final String RELEASE_POINT_EXIT_GAS_FLOW_RATE_UNIT_OF_MEASURE_CODE = "cer:ReleasePointExitGasFlowRateUnitofMeasureCode";
    private final String RELEASE_POINT_EXIT_GAS_TEMPERATURE_MEASURE = "cer:ReleasePointExitGasTemperatureMeasure";
    private final String RELEASE_POINT_FENCELINE_DISTANCE_MEASURE = "cer:ReleasePointFenceLineDistanceMeasure";
    private final String RELEASE_POINT_FENCELINE_DISTANCE_UNIT_OF_MEASURE = "cer:ReleasePointFenceLineDistanceUnitofMeasureCode";
    private final String RELEASE_POINT_FUGITIVE_HEIGHT_MEASURE = "cer:ReleasePointFugitiveHeightMeasure";
    private final String RELEASE_POINT_FUGITIVE_HEIGHT_UNIT_OF_MEASURE_CODE = "cer:ReleasePointFugitiveHeightUnitofMeasureCode";
    private final String RELEASE_POINT_FUGITIVE_WIDTH_MEASURE = "cer:ReleasePointFugitiveWidthMeasure";
    private final String RELEASE_POINT_FUGITIVE_WIDTH_UNIT_OF_MEASURE_CODE = "cer:ReleasePointFugitiveWidthUnitofMeasureCode";
    private final String RELEASE_POINT_FUGITIVE_LENGTH_MEASURE = "cer:ReleasePointFugitiveLengthMeasure";
    private final String RELEASE_POINT_FUGITIVE_LENGTH_UNIT_OF_MEASURE_CODE = "cer:ReleasePointFugitiveLengthUnitofMeasureCode";
//    private final String RELEASE_POINT_FUGITIVE_ANGLE_MEASURE = "cer:ReleasePointFugitiveAngleMeasure";
    private final String RELEASE_POINT_COMMENT = "cer:ReleasePointComment";
    private final String RELEASE_POINT_STATUS_CODE = "cer:ReleasePointStatusCode";
    private final String RELEASE_POINT_STATUS_CODE_YEAR = "cer:ReleasePointStatusCodeYear";

    private transient Logger logger = Logger.getLogger(this.getClass());

    private String whatInReport = "All the facilities that owe a TV emissions inventory for the year are examined.&nbsp;&nbsp;The best report available is used.&nbsp;&nbsp;An error is logged if the report is not approved but the data is still used."
        + "An error is logged if no report exists.  A warning is logged if the EIS has not been approved."
        + "<br><br>The ID used for the <b>cer:FacilitySiteIdentifier</b> tag is the STARS2 Facility Id.&nbsp;&nbsp;"
        + "The <b>cer:FacilitySiteComment</b> tag specifies the STARS2 Ids of the facility, emissions inventory and profile associated with the report.&nbsp;&nbsp;"
        + "All the facilities of a multi-establishment are combined into one facility site."
        + "<br><br>A <b>cer:ReleasePoint</b> tag is generated for each egress point defined for the facility.&nbsp;&nbsp;"
        + "The ID used for the <b>cer:ReleasePointIdentification</b> tag is the correlation Id of the egress point.&nbsp;&nbsp;The <b>cer:ReleasePointDescription</b> tag value is the AQD description if it exists otherwise it is the user description.&nbsp;&nbsp;"
        + "The <b>cer:ReleasePointComment</b> tag value is the STARS2 egress point Id.&nbsp;&nbsp;"
        + "<br><br>Note that control equipment is not identified in the EIS Report except to specify the types of equipment used under the <b>cer:ProcessControlApproach</b> tag."
        + "<br><br>Emissions inventory group emissions are reassigned back to the processes that make up the group with each receiving an equal portion of the group emissions.&nbsp;&nbsp;"
        + "A <b>cer:EmissionsUnit</b> tag is generated for each emission unit specified in the report which has not been marked Engineering Guideline #71.&nbsp;&nbsp;"
        + "The ID used for the <b>cer:UnitIdentification</b> tag is the emission unit Correlation Id.&nbsp;&nbsp;The <b>cer:UnitComment</b> tag value is the emission unit OEPA EMU ID."
        + "<br><br>The ID used for the <b>cer:ProcessIdentification</b> tag is the SCC of the process.&nbsp;&nbsp;The <b>cer:SourceClassificationCode</b> tag value is the Federal SCC that corresponds to the SCC used by OEPA.&nbsp;&nbsp;"
        + "The value used for the <b>cer:ProcessComment</b> tag is the STARS2 process Id.&nbsp;&nbsp;"
        + "The Control Approach is specified for emissions processes under the <b>cer:ProcessControlApproach</b> tag; it is not reported directly under emissions units.&nbsp;&nbsp;"
        + "The <b>cer:PercentControlApproachCaptureEfficiency</b> tag value is computed by examining the stacks and control equipment connected directly to the process.&nbsp;&nbsp;"
        + "The flow factors to each and capture efficiency of each control equipment is used to determine the percent of the flow captured by the control equipment.&nbsp;&nbsp;"
        + "The capture efficiency of a single control equipment is computed as the average capture efficiency of all the pollutants it is specified to control.&nbsp;&nbsp;"
        + "Note that this approach assumes all equipment further downstream has 100% capture efficiency--even if it is specified in STARS2 differently.&nbsp;&nbsp;"
        + "The <b>cer:PercentControlApproachEffectiveness</b> tag value is computed as 100% if the process did not specify a factor method for any pollutants; "
        + "otherwise it is computed using the average number of hours uncontrolled for all the pollutants where the factor method is used.&nbsp;&nbsp;"
        + "<b><i>Tom, do you want to restrict this to non-EIS pollutants?</i></b>&nbsp;&nbsp;"
        + "The <b>cer:PercentControlMeasuresReductionEfficiency</b> tag value is determined by tracing through the attached equipment and for each pollutant using zero hours uncontrolled.&nbsp;&nbsp;"
        + "Any emissions not captured by the immediately connected control equipment is ignored.  The remaining emissions are traced through the rest of the equipment to determine reduction in emissions from that point on downstream."
        + "<br><br>The <b>cer:AveragePercentEmissions</b> tag under the <b>cer:ReleasePointApportionment</b> tag is computed by using each emissions row of the process and tracing an assumed amount of pollutant through the equipment, "
        + "determining the fraction that exits through each stack and the fraction that is fugitive.&nbsp;&nbsp;"
        + "The total amount exiting is gotten by multiplying the fraction by the actual total reported for that pollutant.&nbsp;&nbsp;"
        + "Each of these total amounts for each pollutant is added to get a total for each stack and for fugitive.&nbsp;&nbsp;"
        + "If there is more than one fugitive egress point defined then each is assigned an equal portion of the fugitive total."
        + "<br><br>The pollutants reported on are the CAPs whether zero emissions or not and the HAPs if greater than zero.&nbsp;&nbsp;The process information is not included if no individual pollutant information is included.&nbsp;&nbsp;"
        + "The emission unit information is not included if no process information is included."
        + "<br><br>The other information in the report is more directly derived from STARS2 data.&nbsp;&nbsp;<br><br>"
        + "Four input files are needed to determine how to update the information already held by the US EPA in their EIS database.&nbsp; "
        + "This must only be objects with the System Program Code <b>OHEPA</b> and should not include any where the identifier has an End Date set.&nbsp; "
        + "Each file must be in CSV (Comma Separated Values) format and be ordered first by FacilitySiteIdentifier and then by UnitIdentifier (if the file contains the emission unit id).&nbsp; "
        + "The files are:<br><b>UsEpaFacilityInfo</b> which contains the attributes FacilitySiteIdentifier,FacilitySiteStatusCode,FacilitySiteStatusCodeYear (yyyy)<br>"
        + "<b>UsEpaEgressPointInfo</b> which contains the attributes FacilitySiteIdentifier,releasePointIdentifier,releasePointStatusCode,releasePointStatusCodeYear<br>"
        + "<b>UsEpaEmissionUnitInfo</b> which contains the attributes FacilitySiteIdentifier,UnitIdentifier,UnitStatusCode,UnitStatusCodeYear,UnitOperationDate (yyyy-MM-dd)<br>"
        + "<b>UsEpaEmissionProcessInfo</b> which contains the attributes FacilitySiteIdentifier,unitIdentifier,EmissionsProcessIdentifier,SourceClassificationCode,LastEmissionsYear<br><br>"
        + "The resultant files contain the following:<br>"
        + "EIS_Facility_mismatch file contains the identifier objects to add new objects needed and set End Dates on those that no longer exist.<br>"
        + "EIS_Facility file contains the EIS Facility Inventory information assuming all the objects used already exist in the US EPA EIS database (the EIS_Facility_mismatch file has already updated the EIS database).<br>"
        + "EIS_Point contains the Point Emissions information (assuming the EIS_Facility_mismatch and EIS_Facility files have already updated the EIS database).<br>";

//  REMOVED FROM JSP PAGE.  THE COPY AND ZIP GENERATION TOOK WAY TOO LONG.
//    <afh:rowLayout halign="center" partialTriggers="year singleF">
//    <af:panelButtonBar
//        rendered="#{usEpaEisReport.packageGenerated && usEpaEisReport.allowInput}">
//        <af:commandButton
//            text="Generate Production Packages From EIS Report"
//            action="#{usEpaEisReport.productionPackage}" />
//        <af:commandButton text="Generate QA Packages From EIS Report"
//            action="#{usEpaEisReport.qaPackage}" />
//    </af:panelButtonBar>
//</afh:rowLayout>
//<af:objectSpacer height="5" />
//<afh:rowLayout halign="center"
//    rendered="#{usEpaEisReport.zipGenerated}">
//    <af:goLink id="prodLinkFacMM"
//        text="#{usEpaEisReport.zipFacilityMismatchName}"
//        rendered="#{usEpaEisReport.allowInput}"
//        destination="#{usEpaEisReport.facilityMismatchZipFileDoc.docURL}"
//        targetFrame="_blank" />
//    <af:objectSpacer height="5" width="10" />
//    <af:goLink id="prodLinkFac"
//        text="#{usEpaEisReport.zipFacilityName}"
//        rendered="#{usEpaEisReport.allowInput}"
//        destination="#{usEpaEisReport.facilityZipFileDoc.docURL}"
//        targetFrame="_blank" />
//    <af:objectSpacer height="5" width="10" />
//    <af:goLink id="prodLinkPnt" text="#{usEpaEisReport.zipPointName}"
//        rendered="#{usEpaEisReport.allowInput}"
//        destination="#{usEpaEisReport.pointZipFileDoc.docURL}"
//        targetFrame="_blank" />
//</afh:rowLayout>
//<af:objectSpacer height="5" />

    // Set from web page
    private int year = 0;  // set from Web page
    private int lastEmissionsYear = 2005;  // set from Web page; used when ending a process
    private String eisLogin = DEFAULT_EIS_LOGIN;  // set from Web page
    private boolean includeProcessControls = false; // set from Web page
    private boolean includeHAPS = true; // set from Web page
    private boolean includeWarns = false;  // set from Web page
    private String submittalComment = "";  // set from Web page
    private String authorName = DEFAULT_AUTHOR_NAME;  // set from Web page
    private UploadedFile usEpaFacilities;  // set from Web page
    private UploadedFile usEpaEUs;  // set from Web page
    private UploadedFile usEpaProcesses;  // set from Web page
    private UploadedFile usEpaEPs;  // set from Web page
    private String usEpaFacilitiesStr;  // read from Web page
    private String usEpaEUsStr;  // read from Web page
    private String usEpaProcessesStr;  // read from Web page
    private String usEpaEPsStr;  // read from Web page

    BufferedReader udEpaF;
    BufferedReader usEpaEU;
    BufferedReader usEpaP;
    BufferedReader usEpaEP;

    static protected boolean traceFiles = false;  // trace emission unit input file
    static private Integer numStacksToKeep;
    static Pattern delimiterPattern = Pattern.compile(",|\r\n");
    static protected String MISMATCH = "MISMATCH_XML: ";  // errors in creaing the mismatch XML
    static private String POINTEMIS = "POINT_EMIS_XML: ";
    static private String FACINVENT = "FACIL_INVEN_XML: ";
    static private String REPLACE = "REPLACEMENT: ";
    static private String BOTH = "BOTH_XML: "; // errors in facility and point

    private String singleFacility = null;
    private String xmlInfo = null;
    private Document facilityDom = null;
    private Document facilityMismatchDom = null;
    private Document pointDom = null;
    private Element facilityMismatchSiteElm = null;
    private boolean facilityAlreadyAdded = false;
    private Element emissionUnitMismatchElm = null;
    private String emissionUnitMismatchId = null;
    private boolean emissionUnitMismatchIdentifier = false;
    private static HashSet<Integer> usEpaEgressPoints = new HashSet<Integer>();
    private static HashSet<String> usEpaFictitiousEgressPoints = new HashSet<String>();
    private Element facilitySiteElm = null;
    private Element pointFacilitySiteElm = null;
    private ArrayList<String> newRptEuProcesses = new ArrayList<String>();  // string is EuId-SccId

    private Element naics = null;
    private Element facilitySiteCommentElm = null;
    private Element pointSiteCommentElm = null;
    private Element fSiteDescriptionElm = null;
    private Element pSiteDescriptionElm = null;
    private StringBuffer siteDesc = new StringBuffer(1000);
    private ArrayList<String> commentSCSCId = new ArrayList<String>();
    private ArrayList<Integer> commentFpId = new ArrayList<Integer>();
    private ArrayList<Integer> commentReportId = new ArrayList<Integer>();
    private XMLSerializer facilityMismatchSerializer = null;
    private XMLSerializer facilitySerializer = null;
    private XMLSerializer pointSerializer = null;
    private OutputFormat facilityFormat = null;
    private OutputFormat facilityMismatchFormat = null;
    private OutputFormat pointFormat = null;
    private String facilityXmlFileBase = null;
    private String facilityMismatchXmlFileBase = null;
    private String pointXmlFileBase = null;
    private String facilityMismatchZipFileBase = null;
    private String facilityZipFileBase = null;
    private String pointZipFileBase = null;
    private String facilityMismatchZipFileString = null;
    private String facilityZipFileString = null;
    private String pointZipFileString = null;
    private String logFileString = null;
    private String epaDataString = null;
    private String facilityMismatchXmlFileName = null;
    private String facilityXmlFileName = null;
    private String pointXmlFileName = null;
    private String logFileName = null;
    private String epaDataName = null;
    protected static FileWriter logFile = null;
    protected static FileWriter epaData = null;
    private String filePath = null;
    private FileOutputStream facilityXmlFile = null;
    private FileOutputStream facilityMismatchXmlFile = null;
    private FileOutputStream pointXmlFile = null;
    private us.oh.state.epa.stars2.database.dbObjects.document.Document facilityMismatchXmlFileDoc = 
        new us.oh.state.epa.stars2.database.dbObjects.document.Document();
    private us.oh.state.epa.stars2.database.dbObjects.document.Document facilityXmlFileDoc = 
        new us.oh.state.epa.stars2.database.dbObjects.document.Document();
    private us.oh.state.epa.stars2.database.dbObjects.document.Document pointXmlFileDoc = 
        new us.oh.state.epa.stars2.database.dbObjects.document.Document();
    private us.oh.state.epa.stars2.database.dbObjects.document.Document logFileDoc = 
        new us.oh.state.epa.stars2.database.dbObjects.document.Document();
    private us.oh.state.epa.stars2.database.dbObjects.document.Document facilityMismatchZipFileDoc = 
        new us.oh.state.epa.stars2.database.dbObjects.document.Document();
    private us.oh.state.epa.stars2.database.dbObjects.document.Document facilityZipFileDoc = 
        new us.oh.state.epa.stars2.database.dbObjects.document.Document();
    private us.oh.state.epa.stars2.database.dbObjects.document.Document pointZipFileDoc = 
        new us.oh.state.epa.stars2.database.dbObjects.document.Document();

    private String candidateIncludedStr = "";
    private String errorStr = null;
    private int percentProg = 0;
    private int numIncluded = 0;
    private int cnt = 0;
    private boolean cancelled = false;
    private boolean allowInput = true;
    private boolean createReportButton = true;
    private boolean cancelButton = false;
    private boolean showProgress = false;  // show progress bar on generating reports
    private boolean packageGenerated = false;
    private boolean zipGenerated = false;
    private String zipFacilityMismatchName = "";
    private String zipFacilityName = "";
    private String zipPointName = "";

    private HashSet<EgressPoint> egressPointSet = null;

    private EmissionsReportService erBO;
    protected static MultiEstablishment[] facilities; // facilities returned
    ReportTemplates scReports = null;

    DefaultStackParms minStackParms = null;
    DefaultStackParms maxStackParms = null;

    private static UsEpaEisReport usEpaEisReport = null;
    private String refreshStr = " ";
    private String refreshStrOn = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
        + "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
        + "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
        + "<META HTTP-EQUIV=\"Cache-Control\" "
        + "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
    
    private int finishSiteCnt = 0;
    private int startFacSiteCnt = 0;
    private int startPointSiteCnt = 0;

    static boolean processedGoodChars = false;
    static HashSet<String> good = new HashSet<String>(100);
    
    private EmissionsReportService emissionsReportService;
	private FacilityService facilityService;
	private InfrastructureService infrastructureService;
	
	private String submissionType;
	private String aqdContact = DEFAULT_AQD_CONTACT;
	
	private List<String> facilityClassList = new ArrayList<String>(
			Arrays.asList(PermitClassDef.NTV, PermitClassDef.TV, PermitClassDef.SMTV));

	private List<String> facilityTypeList = new ArrayList<String>();
	
	private boolean xmlGenerated = false;

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		
		this.infrastructureService = infrastructureService;
		
		// Assign values to system prop variables here.
		this.eisLogin = SystemPropertyDef.getSystemPropertyValue("EisLogin", null);
		this.aqdContact = SystemPropertyDef.getSystemPropertyValue("AqdContact", null);
		
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}
	
    private void setPaths() {
        if(!exists(singleFacility)) {
            facilityMismatchXmlFileBase = "EIS_facility_mismatch_" + year +  ".xml";
            facilityXmlFileBase = "EIS_facility_" + year + ".xml";
            pointXmlFileBase = "EIS_point_" + year + ".xml";
            facilityMismatchZipFileBase = "EIS_facility_mismatch_" + year +  ".zip";
            facilityZipFileBase = "EIS_facility_" + year +  ".zip";
            pointZipFileBase = "EIS_point_" + year +  ".zip";
            logFileString = "EIS"+ year + "_logfile" +  ".txt";
            epaDataString = "EIS"+ year + "_epadata" +  ".csv";
        } else {
            facilityMismatchXmlFileBase = "EIS_facility_mismatch_SingleFacility" +  ".xml";
            facilityXmlFileBase = "EIS_facility_SingleFacility" + ".xml";
            pointXmlFileBase = "EIS_point_SingleFacility" + ".xml";
            facilityMismatchZipFileBase = "EIS_facility_mismatch_SingleFacility" +  ".zip";
            facilityZipFileBase = "EIS_facility_SingleFacility" +  ".zip";
            pointZipFileBase = "EIS_point_SingleFacility" +  ".zip";
            logFileString = "EIS_SingleFacility" + "_logfile" +  ".txt";
            epaDataString = "EIS_SingleFacility" +  "_epadata" +  ".csv";
        }

        // Create the EIS directory.
        try {
            String path = File.separatorChar + "EIS";
            File dir = new File(path);
            if (!dir.exists()) {
                DocumentUtil.mkDir(path);
            }
        } catch (IOException e) {
            logger.error("Exception creating EIS directory ", e);
        }
        
        facilityMismatchXmlFileName = filePath + File.separatorChar + "EIS" + File.separator + facilityMismatchXmlFileBase;
        facilityXmlFileName = filePath + File.separatorChar + "EIS" + File.separator + facilityXmlFileBase;
        pointXmlFileName = filePath + File.separatorChar + "EIS" + File.separator + pointXmlFileBase;;
        logFileName = filePath + File.separatorChar + "EIS" + File.separator + logFileString;
        epaDataName = filePath + File.separatorChar + "EIS" + File.separator + epaDataString;
        // display generate package buttons if xml files exist so you don't have to create xml report again.
        File ff = new File(facilityXmlFileName);
        File fp = new File(pointXmlFileName);
        packageGenerated = false;
        if(ff.exists() && fp.exists()) {
            packageGenerated = true;
        }
    }

    public String initUsEpaEisReport() {
        cancelled = true;
        refreshStr = " ";
        createReportButton = true;
        showProgress = false;
        allowInput = true;
        cancelButton = false;
        Calendar cal = Calendar.getInstance();
        if(year == 0) {
            // Initialize year once
            year = cal.get(Calendar.YEAR) - 1;
            lastEmissionsYear = year -1;
        }
        candidateIncludedStr = "";
        errorStr = null;
        setPaths();
        numStacksToKeep = SystemPropertyDef.getSystemPropertyValueAsInteger("US_EPA_EIS_NUM_STACKS", null);
        String hdrSchamastuff = SystemPropertyDef.getSystemPropertyValue("US_EPA_EIS_HDR_SCHEMALOC", null);
        String hdrInfo = "<" + HDR_DOCUMENT + " " + hdrSchamastuff + ">/n";
        String hdrInfo2 = hdrInfo.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "");


//      String cersDeclareBody = getParameter("US_EPA_EIS_CERS_STYLE_DECLARE");
//      String cerDeclare = "<?xml-stylesheet " + cersDeclareBody + "?>\n";
//      String cerDeclare2 = cerDeclare.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "");
        String cersSchemaBody = SystemPropertyDef.getSystemPropertyValue("US_EPA_EIS_CERS_SCHEMALOC", null);
        String cersSchema = "<" + CERS + " " + cersSchemaBody + ">" + "\n";
        String cersSchema2 = cersSchema.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "");
        xmlInfo = "<br>The first or early lines of the .xml file contain some of:<br>" + cersSchema2 + "<br>" + hdrInfo2 +
        "<br><br>" + "These specify the Style Sheet and other information about the schemas.&nbsp;&nbsp;If these lines are incorrect, change information in the Stars2 parms.xml file and re-create the report.&nbsp;&nbsp;The style sheet may need to be in the same directory as your EIS .xml file."; 
        xmlInfo.replace("<", "&lt;");
        xmlInfo.replace(">", "&gt;");
        xmlInfo.replace("\n", "");
        return "usEpaEisReport";
    }

    public String cancel() {
        cancelled = true;
        refreshStr = " ";
        createReportButton = true;
        showProgress = false;
        allowInput = true;
        cancelButton = false;
        candidateIncludedStr = "Cancelled at:  " + candidateIncludedStr;
        errorStr = "Report generation cancelled by user, partial report created";
        return null;
    }
    
    public boolean isXmlGenerated() {
		return xmlGenerated;
	}

	public void setXmlGenerated(boolean xmlGenerated) {
		this.xmlGenerated = xmlGenerated;
	}

	public String generateXMLFiles() {
		// synchronized on the class so that no two threads in the
		// system can generate the xml files simultaneously even
		// when the threads are invoked by different instances of
		// this class
		String ret = null;
		if (validate()) {
			synchronized (UsEpaEisReport.class) {
				usEpaEisReport = this;
				usEpaEisReport.setXmlGenerated(false);
				allowInput = false;
				RunReport reportThread = new RunReport();
				reportThread.start();
				try {
					reportThread.join();
				} catch (InterruptedException ie) {
				}
				if (usEpaEisReport.isXmlGenerated()) {
					ret = "dialog:downloadEISXmlDocuments";
				} else {
					DisplayUtil
							.displayError("Failed to generate XML documents");
				}
				allowInput = true;
			}
		}
		return ret;
    }
	
	private boolean validate() {
		boolean isValid = true;
		 
		if(Utility.isNullOrEmpty(authorName)) {
    		DisplayUtil.displayError("The Author field is empty");
    		isValid = false;
    	}
    	
    	if(Utility.isNullOrEmpty(eisLogin)) {
    		DisplayUtil.displayError("The EIS Login field is empty");
    		isValid = false;
    	}
    	
    	if(Utility.isNullOrEmpty(submissionType)) {
    		DisplayUtil.displayError("The Submission Type field is empty");
    		isValid = false;
    	}
    	
    	if(null == facilityClassList || facilityClassList.isEmpty()) {
    		DisplayUtil.displayError("The Facility Class field is empty");
    		isValid = false;
    	}
    	
    	return isValid;
	}
	
	private void startOperation() {
		boolean ret = false;
		String rootPath = getNodeValue("app.fileStore.rootPath");

		try {
			// create EIS directory in filestore if it does not exist
			String path = File.separatorChar + "EIS";
			File dir = new File(path);
			DocumentUtil.mkDir(path);
		} catch (IOException e) {
			logger.error("Exception creating EIS directory ", e);
		}

		pointXmlFileBase = "EIS_point_" + year + ".xml";
		facilityXmlFileBase = "EIS_facility_" + year + ".xml";

		pointXmlFileName = rootPath + File.separatorChar + "EIS"
				+ File.separatorChar + pointXmlFileBase;
		facilityXmlFileName = rootPath + File.separatorChar + "EIS"
				+ File.separatorChar + facilityXmlFileBase;

		// if facilityTypeList is empty then add a dummy value to the
		// list so that when replacing the positional parameters in the
		// XML query there won't be issues due to empty values in the
		// IN clause for facility type
		if (null == facilityTypeList) {
			facilityTypeList = new ArrayList<String>();
		}
		if (getFacilityTypeList().isEmpty() || !facilityTypeList.contains("-1")) {
			facilityTypeList.add("-1");
		}

		try {
			ret = emissionsReportService.generateXML(this);
		} catch (Exception e) {
			logger.error("Exception creating EIS XML file");
		}

		setXmlGenerated(ret);
	}
    
    public void process(int year, MultiEstablishment[] facilities, XMLSerializer facilityMismatchSerializer,
            XMLSerializer facilitySerializer, XMLSerializer pointSerializer, Document fMismatchDom,
            Document facilityDom, Document pointDom, boolean facDataCat)
    throws DAOException {
        try { 
            try {
                // serializeEmpty(facilitySerializer, facilityDom);  // put default declare into file
                facilityFormat.setOmitXMLDeclaration(true);
                DocumentFragment facilityDomF = facilityDom.createDocumentFragment();
//              String cersDeclareBody = getParameter("US_EPA_EIS_CERS_STYLE_DECLARE");
//              String cerDeclare = "<?xml-stylesheet " + cersDeclareBody + "?>\n";
//              byte[] bytes3 = cerDeclare.getBytes();
//              facilityXmlFile.write(bytes3);
                String hdrSchamastuff = SystemPropertyDef.getSystemPropertyValue("US_EPA_EIS_HDR_SCHEMALOC", null);
                String documentStart = "<" + HDR_DOCUMENT + " id=\"ID" + uniqueCode + "\" " + hdrSchamastuff + ">\n";
                byte[] bytes = documentStart.getBytes();
                facilityXmlFile.write(bytes);
                createHeaderInfo(facilityDom, facilityDomF, "FacilityInventory");
                serializeEmpty(facilitySerializer, facilityDomF);
                byte[] bytes2 = ("<" + HDR_PAYLOAD + ">\n").getBytes();
                facilityXmlFile.write(bytes2);
                String cersSchemaBody = SystemPropertyDef.getSystemPropertyValue("US_EPA_EIS_CERS_SCHEMALOC", null);
                String cersSchema = "<" + CERS + " " + cersSchemaBody + ">" + "\n";
                byte[] bytes4 = cersSchema.getBytes();
                facilityXmlFile.write(bytes4);
                createUserSubmittalInfo(facilityDom, facilityDomF);
                serializeEmpty(facilitySerializer, facilityDomF);

                facilityMismatchFormat.setOmitXMLDeclaration(true);
                DocumentFragment facilityMismatchDomF = facilityMismatchDom.createDocumentFragment();
                facilityMismatchXmlFile.write(bytes);
                createHeaderInfo(facilityMismatchDom, facilityMismatchDomF, "FacilityInventory");
                serializeEmpty(facilityMismatchSerializer, facilityMismatchDomF);
                facilityMismatchXmlFile.write(bytes2);
                facilityMismatchXmlFile.write(bytes4);
                createUserSubmittalInfo(facilityMismatchDom, facilityMismatchDomF);
                serializeEmpty(facilityMismatchSerializer, facilityMismatchDomF);

                //serializeEmpty(pointSerializer, pointDom);
                pointFormat.setOmitXMLDeclaration(true);
                DocumentFragment pointDomF = pointDom.createDocumentFragment();
                //pointXmlFile.write(bytes3);
                pointXmlFile.write(bytes);
                createHeaderInfo(pointDom, pointDomF, "Point");
                serializeEmpty(pointSerializer, pointDomF);
                pointXmlFile.write(bytes2);
                pointXmlFile.write(bytes4);
                createUserSubmittalInfo(pointDom, pointDomF);
                serializeEmpty(pointSerializer, pointDomF);
            }catch(IOException ioe) {
                String s = "ERROR:  writing initial tags failed";
                writeError(BOTH, logFile, s, ioe);
            }
            String prevScscId = null;
            String scscId = null;
            boolean scscGrouping = false;  // Do not group multiple facilities together.
            MultiEstablishment facilityPair = null;
            int nxtPairNdx = 0;
            if(facilities.length > 0) {
                // prime the loop
                facilityPair = facilities[nxtPairNdx];
            }
            while(facilityPair != null || !UsEpaFacilityBuffer.isEndOfFile(this)) {
                if(nxtPairNdx >= facilities.length) {
                    facilityPair = null;
                } else {
                    facilityPair = facilities[nxtPairNdx];
                }
                nxtPairNdx += 1;
                if(facilityPair != null) {
                    // this block requires a facility with a report
                    cnt++;
                    String facilityId = facilityPair.getFacilityId();
                    prevScscId = scscId;
                    scscId = facilityPair.getFederalSCSCId();
                    if(facilitySiteElm != null && (scscId == null || !scscId.equals(prevScscId) || !scscGrouping)) {
                        finishSite(facilityDom, pointDom, facilitySiteCommentElm, pointSiteCommentElm);
                    }
                    if(cancelled) {
                        break;
                    }

                    while (!UsEpaFacilityBuffer.isEndOfFile(this) && UsEpaFacilityBuffer.hasNoReport(facilityId, this)) {
                        UsEpaFacilityBuffer b = UsEpaFacilityBuffer.getBuf(this);
                        if(b.facilitySiteIdentifier.equals("0228002002")) {
                            lastFacilityRead = null; // for breakpoint debugging
                        }
                        Facility matchingFacility  = getFacilityService().retrieveFacility(b.facilitySiteIdentifier, false);
                        if(matchingFacility == null) {
                            // Stars2 does not know about this facility
                            String s = "Facility " + b.facilitySiteIdentifier + " exists in US EPA database but not in STARS2 database.";
                            writeError(MISMATCH, logFile, s);
                            clearOutThisFacility(b);
                        } else {
                            // since no report, just do facility level mismatch
                            boolean keepFacility = processUsEpaMismatchFacility(fMismatchDom, b,  matchingFacility);
                            if(keepFacility) {
                                processUsEpaEmissionUnitsFacility(facilityMismatchDom, b,  matchingFacility, null);  // correct other components US EPA has
                                processUsEpaEgressPointsFacility(facilityMismatchDom, b, matchingFacility, null);
                            } else {
                                // skip everything
                                clearOutThisFacility(b);
                            }
                            if(facilityMismatchSiteElm != null && facilityAlreadyAdded) {
                                finishSite(fMismatchDom);
                            }
                            facilityMismatchSiteElm = null;  // reset for next
                            facilityAlreadyAdded = false;
                            emissionUnitMismatchElm = null;
                            emissionUnitMismatchId = null;
                            emissionUnitMismatchIdentifier = false;
                        }
                        UsEpaFacilityBuffer.next(); // advance to next from US EPA
                    }
                    if(!UsEpaFacilityBuffer.isEndOfFile(this)) {
                        UsEpaFacilityBuffer b = UsEpaFacilityBuffer.getBuf(this);
                        // at this point we either have reached the facility with a report or gone beyond it
                        if(UsEpaFacilityBuffer.newFacility(facilityId, this)) {
                            // the facility with the report is new to the US EPA
                            if(!processReport(facilityPair, null)) {
                                continue;
                            }
                            // Don't advance to next from US EPA
                        } else if(UsEpaFacilityBuffer.hasReport(facilityId, this)) {
                            // the facility with the report is already known to the US EPA
                            if(!processReport(facilityPair, b)) {
                                continue;
                            }
                        } else {
                            writeError(BOTH, logFile, "this never occurs");
                        }

                        numIncluded++;
                        percentProg = (cnt * 100)/facilities.length;
                        this.setValue(percentProg);
                    } else {
                        // No more input .CVS files
                        if(!processReport(facilityPair, null)) {
                            continue;
                        }
                        numIncluded++;
                        percentProg = (cnt * 100)/facilities.length;
                        this.setValue(percentProg);
                    }
                } else {
                    // no reports left
                    while(!UsEpaFacilityBuffer.isEndOfFile(this)) {
                        UsEpaFacilityBuffer b = UsEpaFacilityBuffer.getBuf(this);
                        Facility matchingFacility  = getFacilityService().retrieveFacility(b.facilitySiteIdentifier, false);
                        if(matchingFacility == null) {
                            clearOutThisFacility(b);
                        } else {
                            // since no report, just do facility level mismatch
                            boolean keepFacility = processUsEpaMismatchFacility(fMismatchDom, b,  matchingFacility);
                            if(keepFacility) {
                                processUsEpaEmissionUnitsFacility(facilityMismatchDom, b,  matchingFacility, null);  // correct other components US EPA has
                                processUsEpaEgressPointsFacility(facilityMismatchDom, b,  matchingFacility, null);
                            } else {
                                // skip everything
                                clearOutThisFacility(b);
                            }
                            if(facilityMismatchSiteElm != null && facilityAlreadyAdded) {
                                finishSite(fMismatchDom);
                            }
                            facilityMismatchSiteElm = null;  // reset for next
                            facilityAlreadyAdded = false;
                            emissionUnitMismatchElm = null;
                            emissionUnitMismatchId = null;
                            emissionUnitMismatchIdentifier = false;
                        }
                        UsEpaFacilityBuffer.next(); // advance to next from US EPA
                    }
                }
            }
            if(facilitySiteElm != null) {
                finishSite(facilityDom, pointDom, facilitySiteCommentElm, pointSiteCommentElm);
            }
            // check that other input files are also at the end
            UsEpaEmissionUnitBuffer.confirmEndOfFile(this);
            UsEpaEgressPointBuffer.confirmEndOfFile(this);
            UsEpaEmissionProcessBuffer.confirmEndOfFile(this);
        }catch(RemoteException de) {
            String s = "Failed in converting to XML while on facility ";
            writeError(BOTH, logFile, s, de);
            throw new DAOException(s);
        }
        // write last ending tag
        try {
            byte[] bytes = null;
            StringBuffer a = new StringBuffer("</" + CERS + ">\n" + "</" + HDR_PAYLOAD + ">\n"
                    + "</" + HDR_DOCUMENT + ">\n");
            bytes = a.toString().getBytes();
            facilityMismatchXmlFile.write(bytes);
            facilityXmlFile.write(bytes);
            pointXmlFile.write(bytes);
        } catch(IOException ioe) {
            String s = "writing final tag failed";
            writeError(BOTH, logFile, s, ioe);
        }

        // Make sure count is correct.  The for loop does contain continue statements to skip code.
        candidateIncludedStr = facilities.length + " candidate facilities; " + numIncluded + " included;";
        createReportButton = true;
        showProgress = false;
        cancelButton = false;
        allowInput = true;
    }

    void clearOutThisFacility(UsEpaFacilityBuffer b) throws DAOException {
        // skip everything else
        /* UsEpaEgressPointBuffer epBuf = */ UsEpaEgressPointBuffer.getBuf(this);  // get first/next record
        while(!UsEpaEgressPointBuffer.isEndOfFacility(this, b.facilitySiteIdentifier)) {
//          s = "Facility " + b.facilitySiteIdentifier + " exists in US EPA database but not in STARS2 database.  Do manual cleanup!  Skipping Egress Point "
//          + epBuf.releasePointIdentifier;
//          writeError(MISMATCH, logFile, s);
            UsEpaEgressPointBuffer.next();
            /* epBuf = */ UsEpaEgressPointBuffer.getBuf(this);
        }
        UsEpaEmissionUnitBuffer euBuf = UsEpaEmissionUnitBuffer.getBuf(this);  // get first/next record
        if(traceFiles) writeWarn(logFile, "Emission Unit buffer with3 " + euBuf.facilitySiteIdentifier + " " + euBuf.unitIdentifier);
        while(!UsEpaEmissionUnitBuffer.isEndOfFacility(this, b.facilitySiteIdentifier)) {
//          s = "Facility " + b.facilitySiteIdentifier + " exists in US EPA database but not in STARS2 database.  Do manual cleanup!  Skipping Emission Unit "
//          + euBuf.unitIdentifier;
//          writeError(MISMATCH, logFile, s);
            // locate all the processes 
            /* UsEpaEmissionProcessBuffer pBuf = */ UsEpaEmissionProcessBuffer.getBuf(this);
            while(!UsEpaEmissionProcessBuffer.isEndOfEmissionUnit(this, euBuf.facilitySiteIdentifier, euBuf.unitIdentifier)) {
//              s = "Facility " + b.facilitySiteIdentifier + " exists in US EPA database but not in STARS2 database.  Do manual cleanup!  Skipping Emission Unit "
//              + euBuf.unitIdentifier + ", process " + pBuf.EmissionsProcessIdentifier;
//              writeError(MISMATCH, logFile, s);
                UsEpaEmissionProcessBuffer.next();
                /* pBuf = */ UsEpaEmissionProcessBuffer.getBuf(this);
            }
            UsEpaEmissionUnitBuffer.next();
            euBuf = UsEpaEmissionUnitBuffer.getBuf(this);
            if(traceFiles) writeWarn(logFile, "Emission Unit buffer with4 " + euBuf.facilitySiteIdentifier + " " + euBuf.unitIdentifier);
        }
    }

    void generateMismatchInfoEgressPoints(MultiEstablishment facilityPair, UsEpaFacilityBuffer b, Facility facility,
            Facility alternateFac) throws RemoteException {
        if(b != null) {
            // since facility known to US EPA, check for mismatch.
            boolean keepFacility = processUsEpaMismatchFacility(facilityMismatchDom, b,  facility);//DENNIS one
            if(keepFacility) {
                processUsEpaEgressPointsFacility(facilityMismatchDom, b,  facility, alternateFac);
            } else {
                // skip everything
                clearOutThisFacility(b);
            }
        }
        return;
    }

    void generateMismatchInfoEmissionUnits(MultiEstablishment facilityPair, UsEpaFacilityBuffer b, Facility facility, EmissionsReport rpt) throws RemoteException {
        if(b != null) {
            // since facility known to US EPA, check for mismatch.
            boolean keepFacility = processUsEpaMismatchFacility(facilityMismatchDom, b,  facility);
            if(keepFacility) {
                // proceed through entire facility
                processUsEpaEmissionUnitsFacility(facilityMismatchDom, b,  facility, rpt);
            } else {
                // skip everything
                clearOutThisFacility(b);
            }
        } else {
            // this is a new facility for US EPA, add in components used by report.
            if(!facilityAlreadyAdded) {
                facilityAlreadyAdded = true;
                facilityMismatchDom.appendChild(facilityMismatchSiteElm);
            }
            addMismatchRptEuInfo(facility, rpt, null);
        }
        return;
    }

    boolean processReport(MultiEstablishment facilityPair, UsEpaFacilityBuffer b) throws RemoteException {
        // b == null means US EPA does not know of this facility (with a report)
        String facilityId = facilityPair.getFacilityId();
        nextLastFacilityRead = lastFacilityRead;
        lastFacilityRead = facilityId;
        if(lastFacilityRead.equals("0228002002")) {
            lastFacilityRead = null;  // DENNIS for debugging
        }
        EmissionsReport report = null;
        // get latest approved report
        candidateIncludedStr = "Processing report for facility " + facilityId;
        report = erBO.retrieveLatestTvEmissionReport(year, facilityId);
        if(report == null) {
            // Check for submitted report--or any other kind of report...
            report = erBO.retrieveLatestSubmittedEmissionReport(year, facilityId);
            if(report == null) {
                String s = "Facility " + facilityId + " has no appropriate report";
                writeError(POINTEMIS, logFile, s);
                return false;
            } else {
                String c = ReportReceivedStatusDef.getData().getItems().getItemDesc(report.getRptReceivedStatusCd());
                String s = "Facility " + facilityId + " has no approved report; will use the \"" + c + "\" report that is available";
                writeWarn(POINTEMIS, logFile, s);
            }
        }
        report = erBO.retrieveEmissionsReport(report.getEmissionsRptId(), false);
        if(report == null) {
            String s = "Failed to read report " +  " for facility " + facilityId;
            writeError(POINTEMIS, logFile, s);
            logger.error(s);
            return false;
        }
        // confirm the report contains an EIS
        /*
        if(!report.isRptEIS()) {
            String s = "EReport " + report.getEmissionsRptId() 
            +  " for facility " + facilityId + " does not include an EIS; will use report anyway";
            writeError(POINTEMIS, logFile, s);
        } else {
            // See if EIS approved.
            if(report.getEisStatusCd() == null ||
                    !report.getEisStatusCd().equals(ReportEisStatusDef.EIS_APPROVED)) {
                String s = "Report " + report.getEmissionsRptId() 
                +  " for facility " + facilityId + " has an EIS that is not approved; will use report anyway";
                writeWarn(logFile, s);
            }
        }
		*/
        // Retrieve facility inventory
        Facility facility = getFacilityService().retrieveFacilityProfile(report.getFpId(), false);
        if(facility == null) {
            String e = "Did not locate profile " + report.getFpId() + " for report " + report.getEmissionsRptId();
            writeError(POINTEMIS, logFile, e);
            logger.error(e);
            return false;
        }
        
        // get current facility to get operating info and later possibly alternate facility to use
        Facility cFacility = getFacilityService().retrieveFacility(facility.getFacilityId());
        facility.setOperatingStatusCd(cFacility.getOperatingStatusCd());
        facility.setShutdownDate(cFacility.getShutdownDate());
        
        // update Facility Operating Status to be what should be correct this reporting year (from the US EPA EIS point of view)
        int yr = 0;
        if(facility.getShutdownDate() != null) {
            String y = getYearStr(facility.getShutdownDate());
            yr = Integer.parseInt(y);
            // if shutdown after this year, then not yet shut down
        }
        if(yr == 0 || yr > year) {
        	// not yet shutdown
        	// Are there any emissions?
        	boolean emissionsExist = false;
        	for(EmissionsReportEUGroup grpEmissionUnit : report.getEuGroups()) {
        		if(!grpEmissionUnit.zeroEmissions()) {
        				emissionsExist = true;
        		}
        	}
        	for(EmissionsReportEU e : report.getEus()) {
        		for(EmissionsReportPeriod erp : e.getPeriods()) {
        			if(!erp.zeroEmissions()) {
            			emissionsExist = true;
            			break;
        			}
        		}
        	}
            if(emissionsExist) {
            	facility.setOperatingStatusCd(OperatingStatusDef.OP);
            } else {
            	facility.setOperatingStatusCd(OperatingStatusDef.IA);
            }
        }
        
        writeWarn(logFile, "ProcessReport " + report.getEmissionsRptId() + "  for facility " + facilityId);
        erBO.locatePeriodNames(facility, report);
        if(exists(singleFacility)) {
            // Did not have the scsc id since query was not done for facilities.
            facilityPair.setFederalSCSCId(facility.getFederalSCSCId());
        }
        Facility alternateFacility = null;
        EmissionsReportSearch[] reports = null;
        try {
            EmissionsReportSearch searchObj = new EmissionsReportSearch();
            searchObj.setFacilityId(facilityId);
            searchObj.setYear(year + 1);
            reports = erBO.searchEmissionsReports(searchObj, false);
        } catch(RemoteException e) {
            String s = "Failed on getEmissionsReports() for facility ";
            writeError(FACINVENT, logFile, s, e);
            logger.error(s, e);
            return false;
        }
        Integer fpId;
        if(reports != null && reports.length > 0) {
            // Every report carries the fpId; even if it is a NTV.
            // highest report number is first.
            fpId = reports[0].getFpId();
        } else {
            fpId = cFacility.getFpId();  // get fpId of current
        }
        // Just using the newer alternate facility for:
        //    Stacks when profile has none
        //    facility lat/long when they exist in alternate facility and not in profile
        //    egress point lat long from alternate facility when egress point does not have them and profile does not have them.
        alternateFacility = getFacilityService().retrieveFacilityProfile(fpId, false);  // get detailed profile
        if(fpId.equals(facility.getFpId())) {
            alternateFacility = null;  // if same facility don't bother
        }
        if(b == null) {
            //  Create facility level xml info when new to US EPA
            createNewMismatchFacilityXML(facilityMismatchDom, facility, alternateFacility, facility.getFacilityId());  //DENNIS
        }

        // Do standard validations
        List<ValidationMessage> msgs = verifyFacilityReport(report, facility);
        if(msgs != null) {
            for(ValidationMessage m : msgs) {
                String s = "";
                if(ValidationMessage.Severity.ERROR == m.getSeverity()) {
                    s = "VALIDATION ERROR Facility " + facility.getFacilityId() + ": ";
                } else if(ValidationMessage.Severity.WARNING == m.getSeverity()) {
                    s = "VALIDATION WARNING Facility " + facility.getFacilityId() + ": ";
                } else if(ValidationMessage.Severity.INFO == m.getSeverity()) {
                    continue;  // don't print info ones
                }
                String name = "";
                if(m.getEuId() != null) name = m.getEuId();
                s = s + name + " " + m.getMessage();
                writeWarn(logFile, s);
            }
        }
        // get rid of groups
        erBO.explodeGroupsLocatePeriodNames(facility, report);

        newRptEuProcesses = new ArrayList<String>(); // initialize for facility
        generateMismatchInfoEmissionUnits(facilityPair, b, facility, report);

        startFacSiteCnt++;
        startPointSiteCnt++;
        if(facilitySiteElm == null) {
            // create the root element 
            facilitySiteElm = facilityDom.createElement(FACILITY_SITE);
            facilityDom.appendChild(facilitySiteElm);
        }
        if(pointFacilitySiteElm == null) {
            // create the root element 
            pointFacilitySiteElm = pointDom.createElement(FACILITY_SITE);
            pointDom.appendChild(pointFacilitySiteElm);
        }
        buildFacilitySite(facilityPair, b, facilityDom, pointDom, facilitySiteElm, pointFacilitySiteElm, facility, alternateFacility, report);
        return true;
    }

    public void productionPackage() {
        zipGenerated = false;
        // substitute Production for QA in both XMLs
        String user = InfrastructureDefs.getCurrentUserAttrs().getUserName();
        String subFacilityMismatchFileNameBase = File.separatorChar + "tmp" + File.separator 
        + user + File.separator + facilityMismatchXmlFileBase;
        String subFacilityMismatchFileName = filePath + subFacilityMismatchFileNameBase;
        String subFacilityFileNameBase = File.separatorChar + "tmp" + File.separator 
        + user + File.separator + facilityXmlFileBase;
        String subFacilityFileName = filePath + subFacilityFileNameBase;
        String subPointFileNameBase = File.separatorChar + "tmp" + File.separator 
        + user + File.separator + pointXmlFileBase;
        String subPointFileName = filePath + subPointFileNameBase;
        FileWriter subFacilityMismatchFile = null;
        FileWriter subFacilityFile = null;
        FileWriter subPointFile = null;
        FileReader copyFacilityMismatchFile = null;
        FileReader copyFacilityFile = null;
        FileReader copyPointFile = null;
        boolean quit = false;
        try {
            subFacilityMismatchFile = new FileWriter(new File(subFacilityMismatchFileName));
            subFacilityFile = new FileWriter(new File(subFacilityFileName));
            subPointFile = new FileWriter(new File(subPointFileName));
            copyFacilityMismatchFile = new FileReader(new File(facilityMismatchXmlFileName));
            copyFacilityFile = new FileReader(new File(facilityXmlFileName));
            copyPointFile = new FileReader(new File(pointXmlFileName));
        } catch (IOException ioe) {
            logErrorToQuit(ioe);
            quit = true;
        }
        if(quit) return;
        try {
            subFile(subFacilityMismatchFile, copyFacilityMismatchFile);
        }catch(IOException ioe) {
            logErrorToQuit(ioe);
            quit = true;
        }
        if(quit) return;
        try {
            subFile(subFacilityFile, copyFacilityFile);
        }catch(IOException ioe) {
            logErrorToQuit(ioe);
            quit = true;
        }
        if(quit) return;
        try {
            subFile(subPointFile, copyPointFile);
        }catch(IOException ioe) {
            logErrorToQuit(ioe);
            quit = true;
        }
        if(quit) return;
        String fileBaseName = "";  // don't zip into a directory
        try {
            String zipFileName = generateZipFileName("Production_" + facilityMismatchXmlFileBase);
            facilityMismatchZipFileString = "tmp" + File.separator + user + File.separator + zipFileName;
            DocumentUtil.createZipFile(subFacilityMismatchFileNameBase, 
                    File.separator + facilityMismatchZipFileString,
                    fileBaseName);
            zipFacilityMismatchName ="Production_" + facilityMismatchZipFileBase;

            zipFileName = generateZipFileName("Production_" + facilityXmlFileBase);
            facilityZipFileString = "tmp" + File.separator + user + File.separator + zipFileName;
            DocumentUtil.createZipFile(subFacilityFileNameBase, 
                    File.separator + facilityZipFileString,
                    fileBaseName);
            zipFacilityMismatchName ="Production_" + facilityZipFileBase;

            zipFileName = generateZipFileName("Production_" + pointXmlFileBase);
            pointZipFileString = "tmp" + File.separator + user + File.separator + zipFileName;
            DocumentUtil.createZipFile(subPointFileNameBase,
                    File.separator + pointZipFileString,
                    fileBaseName);
            zipPointName = "Production_" + pointZipFileBase;
            zipGenerated = true;
        }
        catch (Exception ex) {
            logErrorToQuit(ex);
        }
    }

    void logErrorToQuit(Exception ex) {
        String logStr = "Exception " + ex.getClass().getName() + ", Msg = "
        + ex.getMessage();
        writeError(BOTH, logFile, logStr, ex);
        DisplayUtil.displayError("Failed to generate zip files");
        DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
        StackTraceElement[] ste = ex.getStackTrace();
        for(StackTraceElement e : ste) {
            writeLog(logFile, e.toString());
            DisplayUtil.displayError(e.toString());
        }
    }

    public void qaPackage() {
        zipGenerated = false;
        String user = InfrastructureDefs.getCurrentUserAttrs().getUserName();
        String fileBaseName = "";  // don't zip into a directory
        try {
            String zipFileName = generateZipFileName("QA_" + facilityMismatchXmlFileBase);
            facilityMismatchZipFileString = "tmp" + File.separator + user + File.separator + zipFileName;
            DocumentUtil.createZipFile(File.separator  + "EIS" + File.separator + facilityMismatchXmlFileBase, 
                    File.separator + facilityMismatchZipFileString,
                    fileBaseName);
            zipFacilityMismatchName ="QA_" + facilityMismatchZipFileBase;

            zipFileName = generateZipFileName("QA_" + facilityXmlFileBase);
            facilityZipFileString = "tmp" + File.separator + user + File.separator + zipFileName;
            DocumentUtil.createZipFile(File.separator  + "EIS" + File.separator + facilityXmlFileBase, 
                    File.separator + facilityZipFileString,
                    fileBaseName);
            zipFacilityName ="QA_" + facilityZipFileBase;

            zipFileName = generateZipFileName("QA_" + pointXmlFileBase);
            pointZipFileString = "tmp" + File.separator + user + File.separator + zipFileName;
            DocumentUtil.createZipFile(File.separator  + "EIS" + File.separator + pointXmlFileBase,
                    File.separator + pointZipFileString,
                    fileBaseName);
            zipPointName = "QA_" + pointZipFileBase;
            zipGenerated = true;
        }
        catch (Exception ex) {
            logErrorToQuit(ex);
        }
    }

    private String generateZipFileName(String baseName) {
        Calendar cal = Calendar.getInstance();
        String date = Integer.toString(cal.get(Calendar.YEAR)) + "-" 
        + Integer.toString((cal.get(Calendar.MONTH) + 1)) + "-"
        + Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + "-"
        + Integer.toString(cal.get(Calendar.HOUR_OF_DAY)) + "-"
        + Integer.toString(cal.get(Calendar.MINUTE)) + "-"
        + Integer.toString(cal.get(Calendar.SECOND));
        return baseName + "-" + date + ".zip";
    }

    private void subFile(FileWriter subFile, FileReader copyFile)
    throws IOException {
        char[] buf = new char[30];
        int length = buf.length;
        int leftover = 0;
        String phrase = "<PropertyValue>QA";
        int bufPos = 0;
        boolean foundThePhrase = false;
        char startChar = '<';
        lp1: while (true) {
            int cnt = copyFile.read(buf, bufPos, length);
            if(cnt == -1) {
                // write out remaining
                if(leftover > 0) {
                    subFile.write(buf, 0, leftover); subFile.flush();
                }
                break lp1;
            } else {
                length = leftover + cnt;
                bufPos = 0;
            }
            // look for phrase
            int phraseNdx = 0;
            lp2: while(true) { // looking for first character.
                if(buf[bufPos] == startChar) {
                    // first character matched.  Don't continue unless enough characters in buf
                    if(bufPos + phrase.length() - 1 > length - 1) {
                        // write out previous characters, shift left and fill buffer
                        subFile.write(buf, 0, bufPos);  subFile.flush();
                        for(int i = 0; i < length - bufPos; i++) {
                            buf[i] = buf[bufPos + i];
                        }
                        leftover = length - bufPos;
                        length = bufPos;
                        bufPos = leftover;
                        continue lp1;
                    } else {
                        // look for complete match and look for start character again ('<')
                        while(true) { // lp3:
                            bufPos++;
                            if(buf[bufPos] == startChar) {
                                phraseNdx = 0;
                                continue lp2;
                            } else {
                                phraseNdx ++;
                                if(buf[bufPos] == phrase.charAt(phraseNdx)) {
                                    if(phraseNdx >= phrase.length() - 1) {
                                        // found complete match
                                        foundThePhrase = true;
                                        // Write first part out, then "Production", then the rest of buffer, then rest of file
                                        subFile.write(buf, 0, bufPos - 1);   subFile.flush();// dont' write out the "QA"
                                        char[] prod = "Production".toCharArray();
                                        subFile.write(prod, 0, prod.length);  subFile.flush();
                                        if(length > bufPos) {  // something to write.
                                            subFile.write(buf, bufPos + 1, length - bufPos - 1);  subFile.flush();
                                        }
                                        // write out rest of the file.
                                        while (true) {
                                            cnt = copyFile.read(buf, 0, buf.length);
                                            if(cnt == -1) {
                                                break lp1;
                                            }
                                            subFile.write(buf, 0, cnt);  subFile.flush();
                                        }
                                    }
                                } else {
                                    // no match
                                    phraseNdx = 0;
                                    continue lp2;
                                }
                            }
                        }
                    }
                } else { // keep looking for first character
                    bufPos++;
                    if(bufPos >= length) {
                        // not found.  write buffer out and start over
                        subFile.write(buf, 0, length);  subFile.flush();
                        bufPos = 0;
                        length = buf.length;
                        leftover = 0;
                        break lp2;
                    }
                }
            }
        }
        subFile.close();
        String name = copyFile.getEncoding();
        copyFile.close();
        if(!foundThePhrase) {
            throw new IOException("Failed to locate \" " + phrase + "\" in file " + name);
        }
    }

    void createHeaderInfo(Document dom, DocumentFragment domF, String dataCategory) {
        Element el, hdr;
        Text tx;
        hdr = dom.createElement(HDR_HEADER);
        domF.appendChild(hdr);
        el = dom.createElement(HDR_AUTHOR_NAME);
        hdr.appendChild(el);
        tx = dom.createTextNode(authorName);
        el.appendChild(tx);

        el = dom.createElement(HDR_ORGANIZATION_NAME);
        hdr.appendChild(el);
        tx = dom.createTextNode(HDR_ORGANIZATION_NAME_VALUE);
        el.appendChild(tx);

        el = dom.createElement(HDR_DOCUMENT_TITLE);
        hdr.appendChild(el);
        tx = dom.createTextNode(HDR_DOCUMENT_TITLE_VALUE);
        el.appendChild(tx);

        el = dom.createElement(HDR_CREATION_DATE_TIME);
        hdr.appendChild(el);
        tx = dom.createTextNode(identifierStartDateTime);
        el.appendChild(tx);

        el = dom.createElement(HDR_COMMENT);
        hdr.appendChild(el);
        tx = dom.createTextNode(submittalComment);
        el.appendChild(tx);

        el = dom.createElement(HDR_DATAFLOW_NAME);
        hdr.appendChild(el);
        tx = dom.createTextNode(HDR_DATAFLOW_NAME_VALUE);
        el.appendChild(tx);

        el = dom.createElement(HDR_PROPERTY);
        hdr.appendChild(el);
        Element e2 = dom.createElement(HDR_PROPERTY_NAME);
        el.appendChild(e2);
        tx = dom.createTextNode("SubmissionType");
        e2.appendChild(tx);
        e2 = dom.createElement(HDR_PROPERTY_VALUE);
        el.appendChild(e2);
        tx = dom.createTextNode("QA");
        e2.appendChild(tx);

        el = dom.createElement(HDR_PROPERTY);
        hdr.appendChild(el);
        e2 = dom.createElement(HDR_PROPERTY_NAME);
        el.appendChild(e2);
        tx = dom.createTextNode("DataCategory");
        e2.appendChild(tx);
        e2 = dom.createElement(HDR_PROPERTY_VALUE);
        el.appendChild(e2);
        tx = dom.createTextNode(dataCategory);
        e2.appendChild(tx);
    }

    void createUserSubmittalInfo(Document dom, DocumentFragment domF) {
        Element el;
        Text tx;
        el = dom.createElement(USER_IDENTIFIER);
        domF.appendChild(el);
        tx = dom.createTextNode(eisLogin);
        el.appendChild(tx);

        el = dom.createElement(PROGRAM_SYSTEM_CODE);
        domF.appendChild(el);
        tx = dom.createTextNode(NEW_PROGRAM_SYSTEM_CODE_VALUE);
        el.appendChild(tx);

        el = dom.createElement(EMISSIONS_YEAR);
        domF.appendChild(el);
        tx = dom.createTextNode(Integer.toString(year));
        el.appendChild(tx);

        el = dom.createElement(EMISSIONS_CREATION_DATE);
        domF.appendChild(el);
        tx = dom.createTextNode(currentDateYYYMMDD);
        el.appendChild(tx);

        el = dom.createElement(SUBMITTAL_COMMENT);
        domF.appendChild(el);
        tx = dom.createTextNode(submittalComment);
        el.appendChild(tx);
    }

    public String getRefreshStr() {
        return refreshStr;
    }

    String cleanString(String label, String s) {
    	if(s == null || s.length() == 0) return s;
    	if(!processedGoodChars) {
    		processedGoodChars = true;
    		// the first character is a horizontal tab
    		// String goodChars = "\t±°`² abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~!@#$%^&*()_-+={[}]|\\:;<,>.?/'\"";
    		String goodChars = "\t abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~!@#$%^&*()_-+={[}]|\\:;<,>.?/'\"";
    		for(int i = 0; i < goodChars.length(); i++) {
    			good.add(new String(goodChars.substring(i, i+1)));
    			writeLog(logFile, "Character Accepted=" + goodChars.codePointAt(i) + ", symbol is \"" + goodChars.substring(i, i+1) + "\"");
    		}
    	}

    	int c = 0;
    	int j = 0;
    	StringBuffer sb = new StringBuffer(s.length());
    	boolean badChars = false;
    	while(j < s.length()) {
    		c = s.codePointAt(j++);
    		String kar = s.substring(j - 1, j);
    		if(c == 8220 || c == 8221) kar = "??";  // slanted double quotes are illegal
    		else if(c == 8216 || c == 8217) kar = "??";              // remove the slanted single quotes     http://www.w3.org/TR/html4/sgml/entities.html
    		else if(!good.contains(kar) && c != 13 && c != 10) {
    			badChars = true;
    			kar = "??";
    			writeLog(logFile, "BADCHAR CODE=" + c);
    		}
    	    sb.append(kar);
    	}
    	if(badChars) {
    		writeLog(logFile, "Removed characters from " + label + ":  WAS (" + s + "), CHANGED TO (" + sb.toString() + ")");
    	}
    	return sb.toString();
    }
    
    class RunReport extends Thread {
        public void run() {
        	usEpaEisReport.startOperation();
        }
    }

    private void buildFacilitySite(MultiEstablishment facilityPair, UsEpaFacilityBuffer b,
            Document fDom, Document pDom, Element fSiteElm, 
            Element pSiteElm, Facility facility, Facility alternateFac, EmissionsReport report) throws RemoteException {
        Text tx = null;
        Integer yrTV = null;  // first year TV
        Integer yr1st = null; // first year any report required
        // Provide category code and year only if this is the first year that
        // the facility has a submitted TV report--as checked in the
        // category table.
        try {
            ArrayList<EmissionsRptInfo> lst = getEmissionsReportService().getYearlyReportingInfo(facility.getFacilityId());
            for(int i = lst.size() - 1; i >= 0; i--) {
                EmissionsRptInfo eri = lst.get(i);
                if(yrTV == null && eri.getState() != null &&
                        ReportReceivedStatusDef.REVISION_REQUESTED.compareTo(eri.getState()) <= 0) {
                    // for this, just look at submitted (or better) reports because
                    // if the report was not submitted, we would not include for US EPA
                    yrTV = eri.getYear();
                }
                if(yr1st == null && eri.getState() != null &&
                        !ReportReceivedStatusDef.isNotNeededCode(eri.getState())) {
                    yr1st = eri.getYear();
                }
            }
        } catch (RemoteException re) {
            String s = "failed to getYearlyReportingInfo() for facility " + facility.getFacilityId() +
            ".  the tags: " + FACILITY_CATEGORY_CODE + " and " + FACILITY_SITE_STATUS_CODE_YEAR +
            " are omitted.";
            writeError(BOTH, logFile, s, re);
        }
        // Only do it for the first year we provide the TV info
        if(yrTV != null && yrTV == year) {
            String category = "UNK";
            Element fCatElm = fDom.createElement(FACILITY_CATEGORY_CODE);
            fSiteElm.appendChild(fCatElm);
            tx = fDom.createTextNode(category);
            fCatElm.appendChild(tx);
            Element pCatElm = pDom.createElement(FACILITY_CATEGORY_CODE);
            pSiteElm.appendChild(pCatElm);
            tx = pDom.createTextNode(category);
            pCatElm.appendChild(tx);
        }

        // facility name
        Element fFacilityName = fDom.createElement(FACILITY_SITE_NAME);
        fSiteElm.appendChild(fFacilityName);
        Element pFacilityName = pDom.createElement(FACILITY_SITE_NAME);
        pSiteElm.appendChild(pFacilityName);
        // make facility name unique by adding facility id.
        tx = fDom.createTextNode(facility.getName() + " (" + facility.getFacilityId()
                + ")");
        fFacilityName.appendChild(tx);
        tx = pDom.createTextNode(facility.getName() + " (" + facility.getFacilityId()
                + ")");
        pFacilityName.appendChild(tx);

        if(exists(facility.getDesc())) {
            // description for facility category only
            if(fSiteDescriptionElm == null) {
                fSiteDescriptionElm = fDom.createElement(FACILITY_SITE_DESCRIPTION);
                fSiteElm.appendChild(fSiteDescriptionElm);
                pSiteDescriptionElm = pDom.createElement(FACILITY_SITE_DESCRIPTION);
                pSiteElm.appendChild(pSiteDescriptionElm);
            }
            if(siteDesc.length() > 0) {
                siteDesc.append( ";  ");
            }
            siteDesc.append(facility.getDesc());
        }

        // Only do it for the first year we provide the TV info
        if(yrTV != null && yrTV == year) {
            if(yr1st != null && (OperatingStatusDef.OP.equals(facility.getOperatingStatusCd()) ||
                    OperatingStatusDef.IA.equals(facility.getOperatingStatusCd()))) {
                Element fFacOp = fDom.createElement(FACILITY_SITE_STATUS_CODE);
                fSiteElm.appendChild(fFacOp);
                Element pFacOp = pDom.createElement(FACILITY_SITE_STATUS_CODE);
                pSiteElm.appendChild(pFacOp);
                // mark it operating, but only the first time.
                String op = getFpOperatingStatusCd(facility.getOperatingStatusCd());
                if(op.length() == 0) {
                    writeError(BOTH, logFile, "cannot translate operating status for facility " + facility.getFacilityId());
                }
                tx = fDom.createTextNode(op);
                fFacOp.appendChild(tx);
                tx = pDom.createTextNode(op);
                pFacOp.appendChild(tx);
                // Set first operation date
                Element fId = fDom.createElement(FACILITY_SITE_STATUS_CODE_YEAR);
                Element pId = pDom.createElement(FACILITY_SITE_STATUS_CODE_YEAR);
                Calendar cal = Calendar.getInstance();
                cal.set(yr1st, 1, 1);
                Timestamp opD = new Timestamp(cal.getTimeInMillis());
                Text fTx = fDom.createTextNode(getYearStr(opD));
                Text pTx = pDom.createTextNode(getYearStr(opD));
                fId.appendChild(fTx);
                pId.appendChild(pTx);
                fSiteElm.appendChild(fId);
                pSiteElm.appendChild(pId);
            }
        }

        // if shutdown, mark it as such every time--since we don't know when shutdown
        if(OperatingStatusDef.SD.equals(facility.getOperatingStatusCd())) {
            Element fFacOp = fDom.createElement(FACILITY_SITE_STATUS_CODE);
            Element pFacOp = pDom.createElement(FACILITY_SITE_STATUS_CODE);
            fSiteElm.appendChild(fFacOp);
            pSiteElm.appendChild(pFacOp);
            String op = getFpOperatingStatusCd(facility.getOperatingStatusCd());
            if(op.length() == 0) {
                writeError(BOTH, logFile, "cannot translate operating status for facility " + facility.getFacilityId());
            }
            Text fTx = fDom.createTextNode(op);
            fFacOp.appendChild(fTx);
            Text pTx = pDom.createTextNode(op);
            pFacOp.appendChild(pTx);
            // Set shutdown year
            Timestamp sdD = facility.getShutdownDate();
            if(getYearStr(sdD).length() == 0) {
                writeError(BOTH, logFile, "missing shut down date for facility " 
                        + facility.getFacilityId() + "--skipped.");
            } else {
                Element fId = fDom.createElement(FACILITY_SITE_STATUS_CODE_YEAR);
                Element pId = pDom.createElement(FACILITY_SITE_STATUS_CODE_YEAR);
                fTx = fDom.createTextNode(getYearStr(sdD));
                fId.appendChild(fTx);
                fSiteElm.appendChild(fId);
                pTx = pDom.createTextNode(getYearStr(sdD));
                pId.appendChild(pTx);
                pSiteElm.appendChild(pId);
            }
        }

        // put comment in both categories since good documentation of what facility inventories & reports
        if(facilitySiteCommentElm == null) {
            facilitySiteCommentElm = fDom.createElement(FACILITY_SITE_COMMENT);
            fSiteElm.appendChild(facilitySiteCommentElm);
        }
        if(pointSiteCommentElm == null) {
            pointSiteCommentElm = pDom.createElement(FACILITY_SITE_COMMENT);
            pSiteElm.appendChild(pointSiteCommentElm);
        }
        // accumulate the comment
        if(!exists(facility.getFederalSCSCId())) {
            writeError(BOTH, logFile, "SCSC Id not set for facility " 
                    + facility.getFacilityId());
            commentSCSCId.add("SCSC ID missing");
        } else {
            commentSCSCId.add(facility.getFederalSCSCId());
        }
        commentFpId.add(facility.getFpId());
        commentReportId.add(report.getEmissionsRptId());

        // NAICS -- for facility category only
        if(facility.getNaicsCds() == null || facility.getNaicsCds().size() == 0) {
            writeWarn(FACINVENT, logFile, "there is no NAICS code for facility " + facility.getFacilityId() + "--skipped.");
        } else {
            for(String naicsStr : facility.getNaicsCds()) {
                naics = fDom.createElement(FACILITY_NAICS);
                fSiteElm.appendChild(naics);
                Element naicsCd = fDom.createElement(NAICS_CODE);
                naics.appendChild(naicsCd);
                tx = fDom.createTextNode(naicsStr);
                naicsCd.appendChild(tx);
            }
        }

        // identification  -- for both categories
        addNewFacilityIdentification(fDom, fSiteElm, facility);
        addNewFacilityIdentification(pDom, pSiteElm, facility);

        generateSiteAddress(fDom, fSiteElm, facility, alternateFac);

        egressPointSet = new HashSet<EgressPoint>();
        // Emission Units
        buildEmissionUnits(fDom, pDom, fSiteElm, pSiteElm, facility, alternateFac, report);

        // release points  -- facility category only
        buildReleasePoints(fDom, fSiteElm, facility, alternateFac);
        // process through the egress points known by US EPA
        generateMismatchInfoEgressPoints(facilityPair, b, facility, alternateFac);  //DENNIS  we have report
        if(b != null) {
            UsEpaFacilityBuffer.next(); // advance to next from US EPA
        }
        buildMismatchReleasePoints(facilityMismatchDom, facility, alternateFac);
        if(facilityMismatchSiteElm != null && facilityAlreadyAdded) {  // postpone completing the mismatch facility info until point emissions complete.
            finishSite(facilityMismatchDom);
        }
        facilityMismatchSiteElm = null;  // reset for next
        facilityAlreadyAdded = false;
        emissionUnitMismatchElm = null;
        emissionUnitMismatchId = null;
        emissionUnitMismatchIdentifier = false;
    }

    private void generateSiteAddress(Document fDom, Element siteElm, Facility fac, Facility alternateFac) {
        if(fac.getPhyAddr() != null && !fac.getPortable()) {
            // facility address  -- facility category only
            Element id = fDom.createElement(FACILITY_SITE_ADDRESS);
            Element id2 = null;
            Text tx;
            siteElm.appendChild(id);
            String line = null;
            if(fac.getPhyAddr().getAddressLine1() != null) {
                line = fac.getPhyAddr().getAddressLine1();
                if(line.length() > addressLineMaxSize) {
                    line = line.substring(0, addressLineMaxSize - 1);
                }
                id2 = fDom.createElement(LOCATION_ADDRESS_TEXT);
                id.appendChild(id2);
                tx = fDom.createTextNode(line);
                id2.appendChild(tx);
            }
            if(fac.getPhyAddr().getAddressLine2() != null) {
                line = fac.getPhyAddr().getAddressLine2();
                if(line.length() > addressLineMaxSize) {
                    line = line.substring(0, addressLineMaxSize - 1);
                }
                id2 = fDom.createElement(SUPPLEMENTAL_LOCATION_TEXT);
                id.appendChild(id2);
                tx = fDom.createTextNode(line);
                id2.appendChild(tx);
            }
            if(exists(fac.getPhyAddr().getCityName())) {
                id2 = fDom.createElement(LOCALITY_NAME);
                id.appendChild(id2);
                String city = fac.getPhyAddr().getCityName();
                tx = fDom.createTextNode(city);
                id2.appendChild(tx);
            } else {
                writeWarn(FACINVENT, logFile, "there is no City for facility " + fac.getFacilityId() + "--skipped.");
            }
            if(exists(fac.getPhyAddr().getState())) {
                id2 = fDom.createElement(LOCATION_ADDRESS_STATE_CODE);
                id.appendChild(id2);
                tx = fDom.createTextNode(fac.getPhyAddr().getState());
                id2.appendChild(tx);
            } else {
                writeWarn(FACINVENT, logFile, "there is no State for facility " + fac.getFacilityId() + "--skipped.");
            }
            if(exists(fac.getPhyAddr().getZipCode())) {
                id2 = fDom.createElement(LOCATION_ADDRESS_POSTAL_CODE);
                id.appendChild(id2);
                tx = fDom.createTextNode(fac.getPhyAddr().getZipCode());
                id2.appendChild(tx);
            } else {
                writeWarn(FACINVENT, logFile, "there is no zip code for facility " + fac.getFacilityId() + "--skipped.");
            }
            id2 = fDom.createElement(LOCATION_ADDRESS_COUNTRY_CODE);
            id.appendChild(id2);
            tx = fDom.createTextNode("USA");
            id2.appendChild(tx);

            if(!fac.getPortable()) {
                Float facilityLat = null;
                Float facilityLong = null;
                // lat long -- facility category only
                String msg = "facility " + fac.getFacilityId();
                if(facilityLat == null || facilityLong == null) {
                    writeWarn(BOTH, logFile, "latitude not specified for " + msg);
//                } else {
//                    id = fDom.createElement(FACILITY_SITE_GEOGRAPHIC_COORDINATES);
//                    siteElm.appendChild(id);
//                    insertCoordinates(facilityLat, facilityLong, fDom, id, msg);
                }
            }
        }
    }

    private void addNewFacilityIdentification(Document dom, Element siteElm, Facility fac) {
        Text tx = null;
        Element id = dom.createElement(FACILITY_IDENTIFICATION);
        siteElm.appendChild(id);
        Element id2 = dom.createElement(FACILITY_SITE_IDENTIFIER);
        id.appendChild(id2);
        tx = dom.createTextNode(fac.getFacilityId());
        id2.appendChild(tx);

        id2 = dom.createElement(PROGRAM_SYSTEM_CODE);
        id.appendChild(id2);
        tx = dom.createTextNode(NEW_PROGRAM_SYSTEM_CODE_VALUE);
        id2.appendChild(tx);

        if(fac.getPortable()) {
            id2 = dom.createElement(STATE_AND_COUNTY_FIPS_CODE);
            id.appendChild(id2);
            tx = dom.createTextNode("39777");
            id2.appendChild(tx);
        } else if(fac.getPhyAddr() != null) {
            id2 = dom.createElement(STATE_AND_COUNTY_FIPS_CODE);
            id.appendChild(id2);
            String countyCd =  "";
            if(fac.getPhyAddr().getCountyCd() != null) {
                countyCd = fac.getPhyAddr().getCountyCd();
            }
            CountyDef countyDef;
            try {
                countyDef = getInfrastructureService().retrieveCounty(countyCd);
                String fipsCd = countyDef.getFipsCountyCd();
                tx = dom.createTextNode("39" + fipsCd);
                id2.appendChild(tx);
            } catch(RemoteException re) {
                writeError(MISMATCH, logFile, "failed to find CountyDef obj for countyCd=" + countyCd + ", facility " + fac.getFacilityId(), re);
            }
        }
    }

    private void addNewMismatchFacilityIdentification(Document dom, Element siteElm, Facility fac, String facilityId, boolean newToUsEpa) {
        Text tx = null;
        Element id = dom.createElement(FACILITY_IDENTIFICATION);
        siteElm.appendChild(id);
        Element id2 = dom.createElement(FACILITY_SITE_IDENTIFIER);
        id.appendChild(id2);
        tx = dom.createTextNode(facilityId);
        if("0819070134".equals(facilityId)) {
            facilityId = "0819070134";
        }
        id2.appendChild(tx);
        id2 = dom.createElement(PROGRAM_SYSTEM_CODE);
        id.appendChild(id2);
        tx = dom.createTextNode(NEW_PROGRAM_SYSTEM_CODE_VALUE);
        id2.appendChild(tx);
        if(fac.getPortable()) {
            id2 = dom.createElement(STATE_AND_COUNTY_FIPS_CODE);
            id.appendChild(id2);
            tx = dom.createTextNode("39777");
            id2.appendChild(tx);
        } else if(fac.getPhyAddr() != null) {
            id2 = dom.createElement(STATE_AND_COUNTY_FIPS_CODE);
            id.appendChild(id2);
            String countyCd =  "";
            if(fac.getPhyAddr().getCountyCd() != null) {
                countyCd = fac.getPhyAddr().getCountyCd();
            }
            CountyDef countyDef;
            try {
                countyDef = getInfrastructureService().retrieveCounty(countyCd);
                String fipsCd = countyDef.getFipsCountyCd();
                tx = dom.createTextNode("39" + fipsCd);
                id2.appendChild(tx);
            } catch(RemoteException re) {
                writeError(BOTH, logFile, "failed to find CountyDef obj for countyCd=" + countyCd + ", facility " + fac.getFacilityId(), re);
            }
        }
        if(newToUsEpa) {
            id2 = dom.createElement(EFFECTIVE_DATE);
            id.appendChild(id2);
            tx = dom.createTextNode(beginPeriodDateYYYMMDD); //  According to Martin Husk: date on which the Identifier was first being used.
            id2.appendChild(tx);
        }
    }

    private void buildEmissionUnits(Document fDom, Document pDom, Element facilitySiteElm, 
            Element pointFacilitySiteElm, Facility facility, Facility alternateFacility, EmissionsReport report) {
        Text tx = null;
        Element id = null;
        HashSet<Integer> reportedEUs = new HashSet<Integer>(report.getEus().size()); // keep correleation Ids.
        // Go through each EU in report
        for(EmissionsReportEU e : report.getEus()) {
            if(e.getExemptEG71()) {
                continue;  // skip EUs that are EG 71
            }
            if(e.isZeroEmissions()) {
                continue;  // skip because zero emissions
            }
            if(e.getPeriods() == null|| e.getPeriods().isEmpty()) {
                continue;  // skip those that don't apply.
            }
            EmissionUnit eu = facility.getMatchingEmissionUnit(e.getCorrEpaEmuId());
            if(eu == null) {
                writeError(POINTEMIS, logFile, "For report " + report.getEmissionsRptId() +  " could not locate EU with correlation id " + e.getCorrEpaEmuId());
            } else {
                Element fEuId = fDom.createElement(EMISSIONS_UNIT);
                Element pEuId = pDom.createElement(EMISSIONS_UNIT);
                // delay attaching euId until we see if any emissions.

                String desc = eu.getEuDesc();
                if(!exists(desc)) {
                    desc = eu.getRegulatedUserDsc();
                }
                if(!exists(desc)) {
                    desc = eu.getCompanyId();
                }
                
                desc = cleanString(UNIT_DESCRIPTION, desc);
                if(desc != null && desc.length() > unitDescriptionMaxSize) {
                    desc = desc.substring(0, unitDescriptionMaxSize - 1 - 3) + "...";
                }
                if(exists(desc)) { // facility category only
                    id = fDom.createElement(UNIT_DESCRIPTION);
                    fEuId.appendChild(id);
                    tx = fDom.createTextNode(desc);
                    id.appendChild(tx);
                }

                // facility category only
                id = fDom.createElement(UNIT_TYPE_CODE);
                fEuId.appendChild(id);
                tx = fDom.createTextNode(getUnitTypeCode(eu.getDesignCapacityCd()));
                id.appendChild(tx);

                if(!getUnitTypeCode(eu.getDesignCapacityCd()).equals("999")) {
                    String udc = eu.getDesignCapacityUnitsVal();
                    boolean udcExists = true;
                    if(!exists(udc)) udcExists = false;
                    float fUdc = 0;
                    if(udcExists) {
                        try {
                            fUdc = Float.parseFloat(udc);
                        } catch(NumberFormatException ne) {
                            udcExists = false;
                        }
                    }
                    if(udcExists) {
                        udc = measurementConvertToHundredths(fUdc);
                        id = fDom.createElement(UNIT_DESIGN_CAPACITY);
                        fEuId.appendChild(id);
                        tx = fDom.createTextNode(udc);
                        id.appendChild(tx);
                        id = fDom.createElement(UNIT_DESIGN_CAPACITY_UNIT_OF_MEASURE_CODE);
                        fEuId.appendChild(id);
                        tx = fDom.createTextNode(getunitDesignCapacityUnitOfMeasureCode(eu.getDesignCapacityUnitsCd()));
                        id.appendChild(tx);
                    }
                }

                Timestamp eff;
                boolean shutdownFlag = false;
                eff = eu.getEuInitInstallDate();
                if(eff == null) eff = eu.getEuStartupDate();
                if(eff == null) eff = eu.getEuInstallDate();
                if(eff == null) {
                    writeError(BOTH, logFile, "missing operating dates for facility " 
                            + facility.getFacilityId() + " and emission unit " + eu.getEpaEmuId() + "--skipped.");
                }

                String opStr = getEuOperatingStatusCd(eu.getOperatingStatusCd());
                String dtStr = null;
                if(opStr!= null && opStr.equals("PS")) {
                    shutdownFlag = true;
                    dtStr = getYearStrPlus1(eu.getEuShutdownDate());
                    if(!exists(dtStr)) {
                        opStr = "OP";
                        shutdownFlag = false;
                        writeWarn(BOTH, logFile, "missing shutdown date for facility " 
                                + facility.getFacilityId() + " and emission unit--leave operating");
                    }
                }

                if(exists(opStr)) {
                    id = fDom.createElement(UNIT_STATUS_CODE);
                    fEuId.appendChild(id);
                    tx = fDom.createTextNode(opStr);
                    id.appendChild(tx);
                    if(shutdownFlag) {
                        id = fDom.createElement(UNIT_STATUS_CODE_YEAR);
                        fEuId.appendChild(id);
                        tx = fDom.createTextNode(dtStr);
                        id.appendChild(tx);
                    }
                    if(getDateStr(eff).length() != 0) {
                        id = fDom.createElement(UNIT_OPERATION_DATE);
                        fEuId.appendChild(id);
                        tx = fDom.createTextNode(getDateStr(eff));
                        id.appendChild(tx);
                    }
                }

                id = fDom.createElement(UNIT_COMMENT);
                fEuId.appendChild(id);
                tx = fDom.createTextNode("Correlation Id = " + Integer.toString(eu.getCorrEpaEmuId()));
                id.appendChild(tx);
                reportedEUs.add(eu.getCorrEpaEmuId());

                // for facility and point categories
                id = fDom.createElement(UNIT_IDENTIFICATION);
                fEuId.appendChild(id);
                buildNewIdentifier(facilityDom, id, eu.getEpaEmuId(), false, false);
                id = pDom.createElement(UNIT_IDENTIFICATION);
                pEuId.appendChild(id);
                buildNewIdentifier(pointDom, id, eu.getEpaEmuId(), false, false);

                // provided detailed period information.
                boolean processEmissions = buildEmissionProcesses(fDom, pDom, fEuId, pEuId, facility, alternateFacility, report, eu, e);
                if(processEmissions) {
                    facilitySiteElm.appendChild(fEuId);
                    pointFacilitySiteElm.appendChild(pEuId);
                }
            }
        }
    }

    private void buildMismatchReleasePoints(Document fMismatchDom, Facility facility, Facility alternatefac) {
        Element id = null;
        Text tx = null;
        HashSet<Integer> corrSet = new HashSet<Integer>();
        for(EgressPoint egp : egressPointSet) {
            // Determine if these have not been seen yet by US EPA
            int cor = egp.getCorrelationId();
            if(cor > 0) {
                if(!corrSet.add(new Integer(cor))) {
                    writeError(BOTH, logFile, "using fMismatchDom, Duplicate egress point in facility " + facility.getFacilityId() + " correlation Id " + cor);
                    continue;
                }
            }
            boolean needToCreate = false;

            boolean hasFictitiousEP = usEpaFictitiousEgressPoints.contains(egp.getEgressPointId());
            if(egp.getCorrelationId().intValue() < 0 && !hasFictitiousEP) {
                needToCreate = true;
            }
            if(egp.getCorrelationId().intValue() >= 0 &&!usEpaEgressPoints.contains(egp.getCorrelationId())) {
                needToCreate = true;
            }
            if(needToCreate) {
                createMismatchFacilityXML(fMismatchDom, facility, facility.getFacilityId());  // create if not already created.
                Element egId = fMismatchDom.createElement(RELEASE_POINT);
                facilityMismatchSiteElm.appendChild(egId);

                id = fMismatchDom.createElement(RELEASE_POINT_TYPE_CODE);
                egId.appendChild(id);
                tx = fMismatchDom.createTextNode(getReleasePointTypeCd(egp.getEgressPointTypeCd()));
                id.appendChild(tx);

                if(EgrPointTypeDef.FUGITIVE.equals(egp.getEgressPointTypeCd()) ||
                        EgrPointTypeDef.FUGITIVE.equals(egp.getEgressPointTypeCd())) {
                    ;  // do nothing for fugitive
                } else { // Stack
                    reqStackParms(egp, fMismatchDom, facilityMismatchSiteElm, egId, facility);
                }
                if(OperatingStatusDef.SD.equals(facility.getOperatingStatusCd())) {  
                    id = fMismatchDom.createElement(RELEASE_POINT_STATUS_CODE);
                    egId.appendChild(id);
                    tx = fMismatchDom.createTextNode("PS");
                    id.appendChild(tx);
                } else if(exists(egp.getOperatingStatusCd()) &&  egp.getOperatingStatusCd().equals(EgOperatingStatusDef.OP)) {  
                    id = fMismatchDom.createElement(RELEASE_POINT_STATUS_CODE);
                    egId.appendChild(id);
                    tx = fMismatchDom.createTextNode("OP");
                    id.appendChild(tx);
                }
                
                if(OperatingStatusDef.SD.equals(facility.getOperatingStatusCd())) {
                    id = fMismatchDom.createElement(RELEASE_POINT_STATUS_CODE_YEAR);
                    egId.appendChild(id);
                    tx = fMismatchDom.createTextNode(Integer.toString(year));
                    id.appendChild(tx);
                }
                id = fMismatchDom.createElement(RELEASE_POINT_IDENTIFICATION);
                egId.appendChild(id);
                // if fictional use the correct identifier
                String s = Integer.toString(egp.getCorrelationId());
                if(egp.getCorrelationId() == -1) {
                    s = egp.getEgressPointId();
                }
                buildNewIdentifier(fMismatchDom, id, s, true, false);
            }
        }
    }

    private void buildReleasePoints(Document dom, Element fSiteElm, Facility fac, Facility altFac) {

        Element id = null;
        Text tx = null;
        HashSet<Integer> corrSet = new HashSet<Integer>();
        for(EgressPoint egp : egressPointSet) {
            int cor = egp.getCorrelationId();
            if(cor > 0) {
                if(!corrSet.add(new Integer(cor))) {
                    writeError(BOTH, logFile, "Using dom, Duplicate egress point in facility " + fac.getFacilityId() + " correlation Id " + cor);
                    continue;
                }
            }
            Element egId = dom.createElement(RELEASE_POINT);
            fSiteElm.appendChild(egId);

            id = dom.createElement(RELEASE_POINT_TYPE_CODE);
            egId.appendChild(id);
            tx = dom.createTextNode(getReleasePointTypeCd(egp.getEgressPointTypeCd()));
            id.appendChild(tx);

            String s = egp.getDapcDesc();
            if(!exists(s)) {
                s = egp.getRegulatedUserDsc();
            }
            s = cleanString(RELEASE_POINT_DESCRIPTION, s);
            if(exists(s)) {
                id = dom.createElement(RELEASE_POINT_DESCRIPTION);
                egId.appendChild(id);
                tx = dom.createTextNode(getNonNull(s));
                id.appendChild(tx);
            }

            if(EgrPointTypeDef.FUGITIVE.equals(egp.getEgressPointTypeCd()) ||
                    EgrPointTypeDef.FUGITIVE.equals(egp.getEgressPointTypeCd())) {
                if(egp.getStackFencelineDistance() != null) {
                    id = dom.createElement(RELEASE_POINT_FENCELINE_DISTANCE_MEASURE);
                    egId.appendChild(id);
                    tx = dom.createTextNode(measurementConvertToWhole(egp.getStackFencelineDistance()));
                    id.appendChild(tx);
                    id = dom.createElement(RELEASE_POINT_FENCELINE_DISTANCE_UNIT_OF_MEASURE);
                    egId.appendChild(id);
                    tx = dom.createTextNode(UnitDef.getUsEpaUnitCd("FT"));
                    id.appendChild(tx);
                }

                String fugH = null;
                String fugW = null;
                String fugL = null;
                boolean fugValid = true;
                if(EgrPointTypeDef.FUGITIVE.equals(egp.getEgressPointTypeCd())) {
                    if(egp.getReleaseHeight() != null && 
                            egp.getReleaseHeight() >= 0f && egp.getReleaseHeight() <= 500) {
                        // Only set release height for area sources.
                        fugH = measurementConvertToWhole(egp.getReleaseHeight());
                    } else fugValid = false;
                }
                if(fugitiveDimInRange(egp.getVolumeWidth())) {
                    fugW = measurementConvertToWhole(egp.getVolumeWidth());
                } else fugValid = false;
                if(fugitiveDimInRange(egp.getVolumeLength())) {
                    fugL = measurementConvertToWhole(egp.getVolumeLength());
                } else fugValid = false;
                if(fugValid) {
                    if(EgrPointTypeDef.FUGITIVE.equals(egp.getEgressPointTypeCd())) {
                        // Only set release height for area sources.
                        id = dom.createElement(RELEASE_POINT_FUGITIVE_HEIGHT_MEASURE);
                        egId.appendChild(id);
                        tx = dom.createTextNode(fugH);
                        id.appendChild(tx);
                        id = dom.createElement(RELEASE_POINT_FUGITIVE_HEIGHT_UNIT_OF_MEASURE_CODE);
                        egId.appendChild(id);
                        tx = dom.createTextNode(UnitDef.getUsEpaUnitCd("FT"));
                        id.appendChild(tx);
                    }
                    id = dom.createElement(RELEASE_POINT_FUGITIVE_WIDTH_MEASURE);
                    egId.appendChild(id);
                    tx = dom.createTextNode(fugW);
                    id.appendChild(tx);
                    id = dom.createElement(RELEASE_POINT_FUGITIVE_WIDTH_UNIT_OF_MEASURE_CODE);
                    egId.appendChild(id);
                    tx = dom.createTextNode(UnitDef.getUsEpaUnitCd("FT"));
                    id.appendChild(tx);
                    id = dom.createElement(RELEASE_POINT_FUGITIVE_LENGTH_MEASURE);
                    egId.appendChild(id);
                    tx = dom.createTextNode(fugL);
                    id.appendChild(tx);
                    id = dom.createElement(RELEASE_POINT_FUGITIVE_LENGTH_UNIT_OF_MEASURE_CODE);
                    egId.appendChild(id);
                    tx = dom.createTextNode(UnitDef.getUsEpaUnitCd("FT"));
                    id.appendChild(tx);
                }
            } else { // Stack
                reqStackParms(egp, dom, fSiteElm, egId, fac);
                if(egp.getStackFencelineDistance() != null) {
                    id = dom.createElement(RELEASE_POINT_FENCELINE_DISTANCE_MEASURE);
                    egId.appendChild(id);
                    tx = dom.createTextNode(measurementConvertToWhole(egp.getStackFencelineDistance()));
                    id.appendChild(tx);
                    id = dom.createElement(RELEASE_POINT_FENCELINE_DISTANCE_UNIT_OF_MEASURE);
                    egId.appendChild(id);
                    tx = dom.createTextNode(UnitDef.getUsEpaUnitCd("FT"));
                    id.appendChild(tx);
                }  
            }

            if(exists(egp.getEgressPointId())) {
                id = dom.createElement(RELEASE_POINT_COMMENT);
                egId.appendChild(id);
                tx = dom.createTextNode(egp.getEgressPointId());
                id.appendChild(tx);
            }
            
            if(OperatingStatusDef.SD.equals(fac.getOperatingStatusCd())) {
                id = dom.createElement(RELEASE_POINT_STATUS_CODE);
                egId.appendChild(id);
                tx = dom.createTextNode("PS");
                id.appendChild(tx);
            } else if(exists(egp.getOperatingStatusCd()) &&  egp.getOperatingStatusCd().equals(EgOperatingStatusDef.OP)) {  
                id = dom.createElement(RELEASE_POINT_STATUS_CODE);
                egId.appendChild(id);
                tx = dom.createTextNode("OP");
                id.appendChild(tx);
            }
            
            if(OperatingStatusDef.SD.equals(fac.getOperatingStatusCd())) {
                id = dom.createElement(RELEASE_POINT_STATUS_CODE_YEAR);
                egId.appendChild(id);
                tx = dom.createTextNode(Integer.toString(year));
                id.appendChild(tx);
            }

            id = dom.createElement(RELEASE_POINT_IDENTIFICATION);
            egId.appendChild(id);
            // if fictional use the correct identifier
            String ss = Integer.toString(egp.getCorrelationId());
            if(egp.getCorrelationId() == -1) {
                ss = egp.getEgressPointId();
            }
            buildNewIdentifier(dom, id, ss, false, false);
            if(!fac.getPortable()) {
                Float facilityLat = egp.getLatitudeNum();
                Float facilityLong = egp.getLongitudeNum();
                if(facilityLat != null && facilityLong != null) { // check within county
                    if(fac.getPhyAddr() == null || fac.getPhyAddr().getCountyCd() == null) return;  // skip if no county
                    if (County.OUT_OF_STATE_COUNTY.equals(fac.getPhyAddr().getCountyCd())) return;  // skip if out of state
                } else {
                    String msg = "facility " + fac.getFacilityId() + " and egress point " + egp.getEgressPointId();
                    writeWarn(BOTH, logFile, "Latitude/Longitude not specified for " + msg + "--trying facility lat/long");
                }
                String msg = "facility " + fac.getFacilityId();
                if(facilityLat == null || facilityLong == null) {
                    writeWarn(BOTH, logFile, "Latitude/Longitude not specified for " + msg);
                } else {
                    id = dom.createElement(EP_GEOGRAPHIC_COORDINATES);
                    egId.appendChild(id);
                    insertCoordinates(facilityLat, facilityLong, dom, id, msg);
                }
            }
        }
    }
    
    private boolean fugitiveDimInRange(Float f) {
        return f != null && f >= 1f && f <= 10000f;
    }

    private void reqStackParms(EgressPoint egp, Document dom, Element fSiteElm, Element egId, Facility facility) {
        DefaultStackParms defaultStackParms = null;
        try {
            defaultStackParms = erBO.retrieveDefaultStackParms(egp.getFedSCCId());
        } catch (RemoteException e){
            String ss = "call to retrieveDefaultStackParms() failed using SCC ID " + egp.getFedSCCId();
            writeError(BOTH, logFile, ss, e);
        }
        if(defaultStackParms == null) {
            String ss = "there are no default stack attributes for SCC Id " + egp.getFedSCCId() +
            " for facility " + facility.getFacilityId() + " and stack " + egp.getEgressPointId() + ".  Using minimum values.";
            writeWarn(BOTH, logFile, ss);
            defaultStackParms = minStackParms;
        } else {
            if(defaultStackParms.getAvgStackHeight() == null ||
                    defaultStackParms.getAvgStackDiameter() == null || defaultStackParms.getAvgExitGasTemp() == null ||
                    defaultStackParms.getCalcFlowRate() == null) {
                String ss = "one of height, diameter, temperature or flow is null in default stack parmeter record with SCC ID " + defaultStackParms.getSccId()
                + ";Fugitive=" + defaultStackParms.isFugitive() + ".  Using minimum instead";
                defaultStackParms = minStackParms;
                writeWarn(BOTH, logFile, ss);
            } else {
                if(defaultStackParms.getCalcFlowRate() != null) {
                    defaultStackParms.setCalcFlowRate(defaultStackParms.getCalcFlowRate().floatValue() * 60); // convert from cfps to cfpm
                }
            }
        }

        Float rh = egp.getReleaseHeight();
        if(rh == null) {
            rh = defaultStackParms.getAvgStackHeight();
            writeWarn(logFile, "Used default stack height for facility " + facility.getFacilityId() +
                    " and stack " + egp.getEgressPointId());
        }
        rh = checkParmRange(rh, "Stack Height", minStackParms.getAvgStackHeight(), maxStackParms.getAvgStackHeight(), facility, egp);

        String d = egp.getDiameter() == null? null : egp.getDiameter().toString();
        Float diameterVal = new Float(egp.getDiameter().floatValue());
        if(exists(d)) {
            try {
                diameterVal = checkParmRange(diameterVal, "Avg Stack Diameter", minStackParms.getAvgStackDiameter(), maxStackParms.getAvgStackDiameter(), facility, egp);

            } catch(NumberFormatException ne) {
                writeWarn(logFile, "Stack diameter is not a number (" + d + ") for facility " + facility.getFacilityId() +
                        " and stack " + egp.getEgressPointId() + "--using default.");
                d = null;
            }
        }

        if(!exists(d)) {
            diameterVal = defaultStackParms.getAvgStackDiameter();
            writeWarn(logFile, "Used default stack diameter for facility " + facility.getFacilityId() +
                    " and stack " + egp.getEgressPointId());
        }
        if(diameterVal > rh) {
            writeWarn(logFile, "Stack diameter is larger than stack height (" + d + ") for facility " + facility.getFacilityId() +
                    " and stack " + egp.getEgressPointId() + "--using default for both.");
            rh = defaultStackParms.getAvgStackHeight();
            diameterVal = defaultStackParms.getAvgStackDiameter();
        }
        d = measurementConvertToTenths(diameterVal);

        // put in stack height
        Element id;
        Text tx;
        if(rh != null) {
            id = dom.createElement(RELEASE_POINT_STACK_HEIGHT_MEASURE);
            egId.appendChild(id);
            tx = dom.createTextNode(measurementConvertToTenths(rh));
            id.appendChild(tx);
            id = dom.createElement(RELEASE_POINT_STACK_HEIGHT_UNIT_O_MEASURE_CODE);
            egId.appendChild(id);
            tx = dom.createTextNode("FT");
            id.appendChild(tx);
        }

        // put in diameter
        id = dom.createElement(RELEASE_POINT_STACK_DIAMETER_MEASURE);
        egId.appendChild(id);
        tx = dom.createTextNode(d);
        id.appendChild(tx);
        id = dom.createElement(RELEASE_POINT_STACK_DIAMETER_UNIT_OF_MEASURE_CODE);
        egId.appendChild(id);
        tx = dom.createTextNode(UnitDef.getUsEpaUnitCd("FT"));
        id.appendChild(tx);

        Float egf = egp.getExitGasFlowAvg() == null? null : new Float(egp.getExitGasFlowAvg().floatValue());
        if(egf == null) {
            egf = defaultStackParms.getCalcFlowRate();
            writeWarn(logFile, "Used default exit gas flow/velocity for facility " + facility.getFacilityId() +
                    " and stack " + egp.getEgressPointId());
        }
        if(egf == null) {
            String s = "The default is null for scc id " + defaultStackParms.getSccId() + "--must be a fugitive scc used for stack--using minimum";
            writeWarn(FACINVENT, logFile, s); 
        }
        egf = checkParmRange(egf, "Gas Flow Rate", minStackParms.getCalcFlowRate(), maxStackParms.getCalcFlowRate(), facility, egp);
        if(egf != null) {
            id = dom.createElement(RELEASE_POINT_EXIT_GAS_FLOW_RATE_MEASURE);
            egId.appendChild(id);
            tx = dom.createTextNode(measurementConvertToTenths(egf));
            id.appendChild(tx);
            id = dom.createElement(RELEASE_POINT_EXIT_GAS_FLOW_RATE_UNIT_OF_MEASURE_CODE);
            egId.appendChild(id);
            tx = dom.createTextNode(UnitDef.getUsEpaUnitCd("ACFM"));
            id.appendChild(tx);
        }
        // Need only gas flow rate or gas velocity -- not both
//      if(exists(egp.getDiameter()) && egp.getExitGasFlowAvg() != null) {
//      double d = EmissionsReport.convertStringToNum(egp.getCrossSectArea());
//      if(d > 0) {
//      float v = (float)(egp.getExitGasFlowAvg() / d);

//      id = dom.createElement(RELEASE_POINT_EXIT_GAS_VELOCITY_MEASURE);
//      egId.appendChild(id);
//      tx = dom.createTextNode(measurementConvertToTenths(v));
//      id.appendChild(tx);
//      id = dom.createElement(RELEASE_POINT_EXIT_GAS_VELOCITY_UNIT_OF_MEASURE_CODE);
//      egId.appendChild(id);
//      tx = dom.createTextNode(UnitDef.getUsEpaUnitCd("FPM"));
//      id.appendChild(tx);
//      }
//      }
        Float egt = egp.getExitGasTempAvg() == null? null : new Float(egp.getExitGasTempAvg().floatValue());
        if(egt == null) {
            egt = defaultStackParms.getAvgExitGasTemp();
            writeWarn(logFile, "Used default exit gas temperature for facility " + facility.getFacilityId() +
                    " and stack " + egp.getEgressPointId());
        }
        egt = checkParmRange(egt, "Gas Exit Temperature", minStackParms.getAvgExitGasTemp(), maxStackParms.getAvgExitGasTemp(), facility, egp);
        if(egt != null) {
            id = dom.createElement(RELEASE_POINT_EXIT_GAS_TEMPERATURE_MEASURE);
            egId.appendChild(id);
            tx = dom.createTextNode(measurementConvertToWhole(egt));
            id.appendChild(tx);
        }
    }

    private Float checkParmRange(Float rh, String attrib, Float min, Float max, 
            Facility facility, EgressPoint ep) {
        Float rtn = rh;
        boolean error = false;
        String msgFrag = "";
        if(rh == null) {
            error = true;
            rtn = min;
            msgFrag = "-was " + rh + "(replaced by min=" + rtn + ")";
        }
        if(!error && (min != null && rh.floatValue() < min.floatValue())) {
            error = true;
            rtn = min;
            msgFrag = "-was " + rh + "(replaced by min=" + rtn + ")";
        }
        if(!error && (max != null && rh.floatValue() > max.floatValue())) {
            error = true;
            rtn = max;
            msgFrag = "-was " + rh + "(replaced by max=" + rtn + ")";
        }
        if(error) {
            writeWarn(FACINVENT, logFile, attrib + " outside of legal range for facility " + facility.getFacilityId() +
                    " and stack " + ep.getEgressPointId() + msgFrag);
        }
        return rtn;
    }

    private boolean buildEmissionProcesses(Document fDom, Document pDom, Element fEuId, Element pEuId, 
            Facility fac, Facility altFac, EmissionsReport report, EmissionUnit eu, EmissionsReportEU e) {
        // If facDataCat == true then take no action in this routine other than return value.
        Text tx = null;
        Element id = null;
        Element id2 = null;
        Element id3 = null;
        //  keep track whether we build anything
        boolean processEmissions = false;
        //  Get fictional fugitive egress point for EU in case needed
        EgressPoint emissionUnitFugEP = null;
        // For fugitive; stick with original profile
        for(EgressPoint egp : eu.getAllEgressPoints()) {
            if(!(EgrPointTypeDef.FUGITIVE.equals(egp.getEgressPointTypeCd()) ||
                    EgrPointTypeDef.FUGITIVE.equals(egp.getEgressPointTypeCd()))) {
                continue;
            }
            if(emissionUnitFugEP == null) {
                emissionUnitFugEP = egp;
            } else {
                if(emissionUnitFugEP.getCorrelationId().intValue() < egp.getCorrelationId().intValue()) {
                    emissionUnitFugEP = egp;
                }
            }
        }

        // Create fictional fugitive egress point if needed.
        EgressPoint fictionalEmissionUnitFugEP = null;
        if(emissionUnitFugEP == null) {
            fictionalEmissionUnitFugEP = new EgressPoint();
            fictionalEmissionUnitFugEP.setEgressPointTypeCd(EgrPointTypeDef.FUGITIVE);
            fictionalEmissionUnitFugEP.setEgressPointId(eu.getCorrEpaEmuId() + ".F");
            // correlation id of -1 means fictional and use the egressPointId instead for eis identifier
            fictionalEmissionUnitFugEP.setCorrelationId(-1);
        }

        loop:
            for(EmissionsReportPeriod p : e.getPeriods()) {
                if(p.getHoursPerYear() != null && p.getHoursPerYear() == 0 ||
                        p.getCurrentMaus() != null && 
                        p.getCurrentMaus().getThroughputV() != null && p.getCurrentMaus().getThroughputV() == 0) {
                    continue;  // skip processes with no emissions.
                }

                ArrayList<EmissionRow> periodEmissions = null;
                try {
                    FireRow[] rows = new FireRow[0];
                    p.setFireRows(rows);
                    periodEmissions = EmissionRow.getEmissions(
                            report.getReportYear(), p, scReports, false, true, 0, logger);
                } catch(ApplicationException ae) {
                    String s = "ApplicationException at " 
                        + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId()
                        + " and scc " + p.getSccId() + "; these emissions skipped.";
                    writeError(BOTH, logFile, s, ae);
                    continue;
                }

                boolean tradeSecret = p.isTradeSecretS();
                Element fPId = fDom.createElement(UNIT_EMISSIONS_PROCESS);
                Element pPId = pDom.createElement(UNIT_EMISSIONS_PROCESS);
                // delay attaching to euId until we see if any emissions

                String fedSccId = p.getSccId();
                String sccId = p.getSccId();

                if(p.getSccCode() != null && p.getSccCode().getFedSccId() != null && p.getSccCode().getFedSccId().length() > 0) {
                    fedSccId = p.getSccCode().getFedSccId();
                }
                if(!exists(fedSccId)) {
                    writeError(BOTH, logFile, "scc value not set for a process in report " 
                            + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + SOURCE_CLASSIFICATION_CODE + "--skipped.");
                } else {
                    id = fDom.createElement(SOURCE_CLASSIFICATION_CODE);
                    fPId.appendChild(id);
                    tx = fDom.createTextNode(fedSccId);
                    id.appendChild(tx);

                    id = pDom.createElement(SOURCE_CLASSIFICATION_CODE);
                    pPId.appendChild(id);
                    tx = pDom.createTextNode(fedSccId);
                    id.appendChild(tx);
                }

                EmissionProcess euP = eu.findProcess(p.getSccId());
                if(euP == null) {
                    String s = "failed to find EmissionProcess for report " 
                        + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId();
                    writeError(BOTH, logFile, s);
                }
                if(euP != null && exists(euP.getEmissionProcessNm())) {
                    id = fDom.createElement(PROCESS_DESCRIPTION);
                    fPId.appendChild(id);
                    tx = fDom.createTextNode(cleanString(PROCESS_DESCRIPTION, euP.getEmissionProcessNm()));
                    id.appendChild(tx);
                }

                id = fDom.createElement(LAST_EMISSIONS_YEAR);
                fPId.appendChild(id);
                tx = fDom.createTextNode(Integer.toString(year));
                id.appendChild(tx);
                id = pDom.createElement(LAST_EMISSIONS_YEAR);
                pPId.appendChild(id);
                tx = pDom.createTextNode(Integer.toString(year));
                id.appendChild(tx);

                id = fDom.createElement(PROCESS_COMMENT);
                fPId.appendChild(id);
                tx = fDom.createTextNode(cleanString(PROCESS_COMMENT, p.getTreeLabel()));
                id.appendChild(tx);

                id = pDom.createElement(PROCESS_COMMENT);
                pPId.appendChild(id);
                tx = pDom.createTextNode(cleanString(PROCESS_COMMENT, p.getTreeLabel()));
                id.appendChild(tx);

                Element fId = fDom.createElement(PROCESS_IDENTIFICATION);
                fPId.appendChild(fId);
                Element pId = pDom.createElement(PROCESS_IDENTIFICATION);
                pPId.appendChild(pId);
                boolean newProcess = false;
                String euProcess = e.getEpaEmuId() + "-" + p.getSccId();
                if(newRptEuProcesses.contains(euProcess)) {
                    newProcess = true;
                }
                buildNewIdentifier(pDom, pId, sccId, false, false);
                buildNewIdentifier(fDom, fId, sccId, newProcess, false);

                id = pDom.createElement(REPORTING_PERIOD);
                pPId.appendChild(id);
                id2 = pDom.createElement(REPORTING_PERIOD_TYPE_CODE);
                id.appendChild(id2);
                tx = pDom.createTextNode("A");
                id2.appendChild(tx);
                id2 = pDom.createElement(EMISSION_OPERATING_TYPE_CODE);
                id.appendChild(id2);
                tx = pDom.createTextNode("R");  // for Routine.
                id2.appendChild(tx);

                EmissionsMaterialActionUnits emau = p.getCurrentMaus();
                String mat = "";
                String units = "";
                String val = "";
                String act = "";
                if(emau != null && emau.getMaterial() != null) {
                    mat =  emau.getMaterial();
                }
                if(emau != null && emau.getMeasure() != null) {
                    units = emau.getMeasure();
                }
                if(emau != null && emau.getThroughput() != null) {
                    val =  emau.getThroughput().replaceAll(",", "");
                }
                if(emau != null && emau.getAction() != null) {
                    act =  emau.getAction();
                }

                if(!tradeSecret) {
                    String actDirection = ActionsDef.getData().getItems().getItemDesc(act);
                    id2 = pDom.createElement(CALCULATION_PARAMETER_TYPE_CODE);
                    id.appendChild(id2);
                    tx = pDom.createTextNode(actDirection);
                    id2.appendChild(tx);
                    
                    if(val.length() == 0) {
                        writeError(POINTEMIS, logFile, "amount of material is missing " 
                                + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId() + "--skipped.");
                    } else {
                        id2 = pDom.createElement(CALCULATION_PARAMETER_VALUE);
                        id.appendChild(id2);
                        tx = pDom.createTextNode(val);
                        id2.appendChild(tx);
                    }

                    if(units.length() == 0) {
                        writeError(POINTEMIS, logFile, "measure(units) of material is missing " 
                                + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId() + "--skipped.");
                    } else {
                        id2 = pDom.createElement(CALCULATION_PARAMETER_UNIT_OF_MEASURE);
                        id.appendChild(id2);
                        tx = pDom.createTextNode( UnitDef.getUsEpaUnitCd(units));
                        id2.appendChild(tx);
                    }

                    if(mat.length() == 0) {
                        writeError(POINTEMIS, logFile, "material code is missing " 
                                + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId() + "--skipped.");
                    } else {
                        id2 = pDom.createElement(CALCULATION_PARAMETER_MATERIAL_CODE);
                        id.appendChild(id2);
                        tx = pDom.createTextNode(mat);
                        id2.appendChild(tx);
                    }
                }

                id2 = pDom.createElement(CALCULATION_DATA_YEAR);
                id.appendChild(id2);
                tx = pDom.createTextNode(Integer.toString(report.getReportYear()));
                id2.appendChild(tx);

                id2 = pDom.createElement(REPORTING_PERIOD_COMMENT);
                id.appendChild(id2);
                tx = pDom.createTextNode(p.getTreeLabel());
                id2.appendChild(tx);

                id2 = pDom.createElement(OPERATING_DETAILS);
                id.appendChild(id2);

                if(!tradeSecret) {
                    try {
                        double actH = p.getHoursPerYear()!= null?p.getHoursPerYear():0;
                        int weeksPerY = p.getWeeksPerYear()!= null?p.getWeeksPerYear():0;
                        int dayPW = p.getDaysPerWeek()!= null?p.getDaysPerWeek():0;
                        int hourPD = p.getHoursPerDay()!= null?p.getHoursPerDay():0;
                        float chgWeeksPerY = weeksPerY;
                        float chgDayPW = dayPW;
                        float chgHourPD = hourPD;
                        boolean changedHours = false;

                        if(actH < 2080) {
                            if(weeksPerY == 0) {
                                chgWeeksPerY = 1;
                                changedHours = true;
                            }
                            if(dayPW == 0) {
                                chgDayPW = .1f;
                                changedHours = true;
                            }
                            if(hourPD == 0) {
                                chgHourPD = .1f;
                                changedHours = true;
                            }
                        } else {
                            if(weeksPerY == 0) {
                                chgWeeksPerY = 52;
                                changedHours = true;
                            }
                            if(dayPW == 0) {
                                chgDayPW = 5;
                                changedHours = true;
                            }
                            if(hourPD == 0) {
                                chgHourPD = 8;
                                changedHours = true;
                            }
                        }
                        if(changedHours) {
                            String s = OPERATING_DETAILS + " some schedule information is zero.  hoursPerYear="
                            + actH + ", weeksPerYear=" + weeksPerY + ", daysPerWeek=" + dayPW + ", hoursPerDay=" + hourPD 
                            + ".  Changed to: " + actH + ", " + chgWeeksPerY + ", " + chgDayPW + ", " + chgHourPD + " for emissions inventory "
                            + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId();
                            writeWarn(POINTEMIS, logFile, s);
                        }
                        if(p.getHoursPerYear() != null) {
                            id3 = pDom.createElement(ACTUAL_HOURS_PER_PERIOD);
                            id2.appendChild(id3);
                            tx = pDom.createTextNode(Double.toString(actH));
                            id3.appendChild(tx);
                        }

                        if(p.getDaysPerWeek() != null) {
                            id3 = pDom.createElement(AVERAGE_DAYS_PER_WEEK);
                            id2.appendChild(id3);
                            tx = pDom.createTextNode(measurementConvertToTenths(chgDayPW));
                            id3.appendChild(tx);
                        }

                        if(p.getHoursPerDay() != null) {
                            id3 = pDom.createElement(AVERAGE_HOURS_PER_DAY);
                            id2.appendChild(id3);
                            tx = pDom.createTextNode(measurementConvertToTenths(chgHourPD));
                            id3.appendChild(tx);
                        }

                        if(p.getWeeksPerYear() != null) {
                            id3 = pDom.createElement(AVERAGE_WEEKS_PER_PERIOD);
                            id2.appendChild(id3);
                            tx = pDom.createTextNode(measurementConvertToTenths(chgWeeksPerY));
                            id3.appendChild(tx);
                        }
                    } catch(Exception ee) {
                        String s = OPERATING_DETAILS + " problem with schedule for emissions inventory "
                        + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId();
                        writeError(POINTEMIS, logFile, s, ee);
                    }
                }

                if(p.getWinterThroughputPct() != null) {
                    id3 = pDom.createElement(PERCENT_WINTER_ACTIVITY);
                    id2.appendChild(id3);
                    tx = pDom.createTextNode(Integer.toString(p.getWinterThroughputPct()));
                    id3.appendChild(tx);
                }

                if(p.getSpringThroughputPct() != null) {
                    id3 = pDom.createElement(PERCENT_SPRING_ACTIVITY);
                    id2.appendChild(id3);
                    tx = pDom.createTextNode(Integer.toString(p.getSpringThroughputPct()));
                    id3.appendChild(tx);
                }

                if(p.getSummerThroughputPct() != null) {
                    id3 = pDom.createElement(PERCENT_SUMMER_ACTIVITY);
                    id2.appendChild(id3);
                    tx = pDom.createTextNode(Integer.toString(p.getSummerThroughputPct()));
                    id3.appendChild(tx);
                }

                if(p.getFallThroughputPct() != null) {
                    id3 = pDom.createElement(PERCENT_FALL_ACTIVITY);
                    id2.appendChild(id3);
                    tx = pDom.createTextNode(Integer.toString(p.getFallThroughputPct()));
                    id3.appendChild(tx);
                }

                if(p.getVars() != null && !tradeSecret) {
                    for(EmissionsVariable v : p.getVars()) {
                        if(!"A".equals(v.getVariable()) && !"S".equals(v.getVariable()) && 
                                !"HCs".equals(v.getVariable()) && !"HCl".equals(v.getVariable()) && !"HCg".equals(v.getVariable())) {
                            continue;
                        }
                        
                        String supValue = v.getValue();
                        if("HCs".equals(v.getVariable()) && supValue != null && supValue.length() > 0) {
                            Double d = v.getValueV() * 2000;  // convert to TONs
                            supValue = EmissionsReport.numberToString(d).replaceAll(",", "");
                        } else {
                            supValue = supValue.replaceAll(",", "");
                        }
              
                        if("HCs".equals(v.getVariable()) || "HCl".equals(v.getVariable()) || "HCg".equals(v.getVariable())) {
                            id2 = pDom.createElement(SUPPLEMENTAL_CALCULATION_PARAMETER);
                            id.appendChild(id2);
                            id3 = pDom.createElement(SUPPLEMENTAL_CALCULATION_PARAMETER_TYPE);
                            id2.appendChild(id3);
                            tx = pDom.createTextNode(parameterHeatContentType);
                            id3.appendChild(tx);
                            id3 = pDom.createElement(SUPPLEMENTAL_CALCULATION_PARAMETER_VALUE);
                            id2.appendChild(id3);
                            tx = pDom.createTextNode(supValue);
                            id3.appendChild(tx);
                            id3 = pDom.createElement(SUPPLEMENTAL_CALCULATION_PARAMETER_NUMERATOR_UNIT);
                            id2.appendChild(id3);
                            tx = pDom.createTextNode("BTU");
                            id3.appendChild(tx);
                            id3 = pDom.createElement(SUPPLEMENTAL_CALCULATION_PARAMETER_DENOMINATOR_UNIT);
                            id2.appendChild(id3);
                            if("HCs".equals(v.getVariable())) {
                                tx = pDom.createTextNode("TON");
                            } else if("HCl".equals(v.getVariable())) {
                                tx = pDom.createTextNode("GAL");
                            } else { // HCg
                                tx = pDom.createTextNode("SCF");
                            }
                            id3.appendChild(tx);
                        } else if("A".equals(v.getVariable())) {
                            if(withinRange(supValue, .01f, 20f)) {
                                id2 = pDom.createElement(SUPPLEMENTAL_CALCULATION_PARAMETER);
                                id.appendChild(id2);
                                id3 = pDom.createElement(SUPPLEMENTAL_CALCULATION_PARAMETER_TYPE);
                                id2.appendChild(id3);
                                tx = pDom.createTextNode(parameterPercentAshType);
                                id3.appendChild(tx);
                                id3 = pDom.createElement(SUPPLEMENTAL_CALCULATION_PARAMETER_VALUE);
                                id2.appendChild(id3);
                                tx = pDom.createTextNode(supValue);
                                id3.appendChild(tx);
                            } else {
                                String s = parameterSulfurType + " has value " + supValue + " which is out of range for emissions inventory "
                                + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId() + ", ignored.";
                                writeWarn(POINTEMIS, logFile, s);
                            }
                        } else if("S".equals(v.getVariable())) {
                            if(withinRange(supValue, .01f, 10f)) {
                                id2 = pDom.createElement(SUPPLEMENTAL_CALCULATION_PARAMETER);
                                id.appendChild(id2);
                                id3 = pDom.createElement(SUPPLEMENTAL_CALCULATION_PARAMETER_TYPE);
                                id2.appendChild(id3);
                                tx = pDom.createTextNode(parameterSulfurType);
                                id3.appendChild(tx);
                                id3 = pDom.createElement(SUPPLEMENTAL_CALCULATION_PARAMETER_VALUE);
                                id2.appendChild(id3);
                                tx = pDom.createTextNode(supValue);
                                id3.appendChild(tx);
                            } else {
                                String s = parameterPercentAshType + " has value " + supValue + " which is out of range for emissions inventory "
                                + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId() + ", ignored.";
                                writeWarn(POINTEMIS, logFile, s);
                            }
                        }
                    }
                }

                boolean hadEmissions = buildEmissions(fac, pDom, id, units, report, tradeSecret, periodEmissions, eu, e, p);
                if(!hadEmissions) {  // since no emissions, skip the entire period/process
                    continue loop;
                }

                if(euP != null) {
                    Element controlApproachId = null;
                    boolean have_CONTROL_MEASURE = false;
                    boolean have_CONTROL_POLLUTANT = false;
                    if(includeProcessControls && euP.getControlEquipments().size() > 0) {
                        // if no control equipment leave out Process Control Approach and Control Measure
                        controlApproachId = fDom.createElement(PROCESS_CONTROL_APPROACH);
                        id2 = fDom.createElement(PERCENT_CONTROL_APPROACH_CAPTURE_EFFICIENCY);
                        controlApproachId.appendChild(id2);
                        float percent = euP.percentCaptureEfficencyOverall();
                        if(percent < 1.0f) {
                            String s = PERCENT_CONTROL_APPROACH_CAPTURE_EFFICIENCY + " is less than 1% for report " 
                            + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId();
                            writeWarn(logFile, s);
                        }
                        String percentStr = percentConvert_1p0to100p0(percent);
                        tx = fDom.createTextNode(percentStr);
                        id2.appendChild(tx);
                        id2 = fDom.createElement(PERCENT_CONTROL_APPROACH_EFFECTIVENESS);  // percent of time operating
                        controlApproachId.appendChild(id2);
                        percent = p.percentControlledAverage();
                        percentStr = percentConvert_1p0to100p0(percent);
                        tx = fDom.createTextNode(percentStr);
                        id2.appendChild(tx);

                        // since we have control equipment, we do have control measures.
                        HashSet<String> ceSet = euP.getUniqueControlEquip();
                        for(String type : ceSet) {
                            id2 = fDom.createElement(CONTROL_MEASURE);
                            have_CONTROL_MEASURE = true;
                            controlApproachId.appendChild(id2);
                            id3 = fDom.createElement(CONTROL_MEASURE_CODE);
                            id2.appendChild(id3);
                            String ceCode = getCeType(type);
                            tx = fDom.createTextNode(ceCode);
                            id3.appendChild(tx);
                        }
                    }

                    double actualStackTotal = 0d;
                    double actualFugitiveTotal = 0d;
                    HashMap<EgressPoint, Double> totStacks = new HashMap<EgressPoint, Double>();
                    boolean needStkForSomePollutant = false;
                    for(EmissionRow er : periodEmissions) {
                        // skip what not interested in
                        String neiCode = null;
                        try {
                            neiCode = NonToxicPollutantDef.getNeiCode(er.getPollutantCd());
                        }catch(ApplicationException ae) {
                            String s = "got ApplicationException at report " 
                                + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId()
                                + " and scc " + p.getSccId() + " and pollutantCd " + er.getPollutantCd()
                                + ".  Emission Control will be included.";
                            writeError(POINTEMIS, logFile, s, ae);
                        }

                        if(neiCode == null || neiCode.equalsIgnoreCase("Inactive")) {
                            // Leave these pollutants out
                            continue;
                        }
                        if(er.getOrder() == null && !includeHAPS) {
                            // skip when emissions not in first table (Criteria pollutants & Lead)
                            continue;
                        }
                        if(er.getTotalEmissions() != null && er.getTotalEmissionsV() != 0) {
                            // Only look at those where there were emissions
                            List<ControlInfoRow> calc = ControlInfoRow.generateControlMatrix(fac, euP, p, er, false, false);
                            if(ControlInfoRow.isProblems()) {
                                String s = ControlInfoRow.getProblems().toString() + " for report " +
                                report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
                                + p.getSccId() + ", pollutantCd " + er.getPollutantCd();
                                writeError(BOTH, logFile, s);
                            }
                            boolean needFug = false;
                            boolean needStk = false;
                            if(!EmissionCalcMethodDef.isFactorMethod(er.getEmissionCalcMethodCd()) && er.getStackEmissionsV() > 0 && ControlInfoRow.stackTotal(calc) == 0) {
                                // user has specified stack emissions but the profile does not support it
                                needStk = true;
                                needStkForSomePollutant = true;
                            }
                            if(!EmissionCalcMethodDef.isFactorMethod(er.getEmissionCalcMethodCd()) && er.getFugitiveEmissionsV() > 0 && ControlInfoRow.fugitiveTotal(calc) == 0) {
                                // user has specified fugitive emissions but the profile does not support it
                                needFug = true;
                            }
                            if(needFug || needStk) {
                                // try again by adjusting control equipment efficiencies
                                calc = ControlInfoRow.generateControlMatrix(fac, euP, p, er, needFug, needStk);
                                if(ControlInfoRow.isProblems()) {
                                    String s = ControlInfoRow.getProblems().toString() + " for report " +
                                    report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
                                    + p.getSccId() + ", pollutantCd " + er.getPollutantCd();
                                    writeError(BOTH, logFile, s);
                                }
                            }

                            boolean skipAdding = false;
                            if(er.getOrder() == null && !includeHAPS) {
                                // skip when emissions not in first table (Criteria pollutants & Lead)
                                skipAdding = true;
                            }
                            if(er.getPollutantCd().equals(PollutantDef.PM25FIL_CD) ) {
                                skipAdding = true;
                            }

                            if(!skipAdding) {
                                actualStackTotal += er.getStackEmissionsV();
                                actualFugitiveTotal += er.getFugitiveEmissionsV();
                                HashMap<EgressPoint, Double> stacks = new HashMap<EgressPoint, Double>();
                                boolean ok = ControlInfoRow.addUpStackEmissions(calc, stacks, er.getTotalEmissionsV());
                                addTotals(totStacks, stacks);
                                if(!ok) {
                                    // need more detail about where problem occurred that were logged in function.
                                    String s = "Previous log4j errors about egress point are in report " +
                                    report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
                                    + p.getSccId() + ", pollutantCd " + er.getPollutantCd();
                                    writeError(BOTH, logFile, s);
                                }
                            }

                            if(controlApproachId != null) {
                                er.setAnnualAdjustV(0d); // want control when working
                                calc = ControlInfoRow.generateControlMatrix(fac, euP, p, er, needFug, needStk);
                                if(ControlInfoRow.isProblems()) {
                                    String s = ControlInfoRow.getProblems().toString() + " for report " +
                                    report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
                                    + p.getSccId() + ", pollutantCd " + er.getPollutantCd();
                                    writeError(BOTH, logFile, s);
                                }
                                double percentControl = ControlInfoRow.afterControlTotal(calc);
                                if(percentControl == 100) percentControl = 99.9;
                                if(percentControl > 0) {  // If no controls for this pollutant--leave out
                                    have_CONTROL_POLLUTANT = true;
                                    id2 = fDom.createElement(CONTROL_POLLUTANT);
                                    id3 = fDom.createElement(POLLUTANT_CODE);
                                    id2.appendChild(id3);
                                    tx = fDom.createTextNode(neiCode);
                                    id3.appendChild(tx);
                                    id3 = fDom.createElement(PERCENT_CONTROL_MEASURE_REDUCTION_EFFICIENCY);
                                    id2.appendChild(id3);
                                    if(percentControl < 1.0f) {
                                        String s = PERCENT_CONTROL_MEASURE_REDUCTION_EFFICIENCY + " is less than 1% for report " 
                                        + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId();
                                        writeWarn(logFile, s);
                                    }
                                    String percentStr = percentConvert_1p0to100p0((float)percentControl);
                                    tx = fDom.createTextNode(percentStr);
                                    id3.appendChild(tx);
                                    controlApproachId.appendChild(id2);
                                }
                            }
                        }
                    }

                    if(controlApproachId != null && !have_CONTROL_MEASURE) {
                        String s = "No " + CONTROL_MEASURE + " component for report " 
                        + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId();
                        writeWarn(logFile, s);
                    }

                    if(controlApproachId != null && !have_CONTROL_POLLUTANT ) {
                        String s = "No " + CONTROL_POLLUTANT + " component for report " 
                        + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc " + p.getSccId();
                        writeWarn(logFile, s);
                    }

                    if(controlApproachId != null && have_CONTROL_POLLUTANT && have_CONTROL_MEASURE) {
                        fPId.appendChild(controlApproachId);
                    }

                    boolean haveStacks = false;
                    if(needStkForSomePollutant) {
                        for(Entry<EgressPoint, Double> entry : totStacks.entrySet()) {
                            if(entry.getKey() != null) { // is a stack
                                haveStacks = true;
                                break;
                            }
                        }
                        EmissionProcess euPAlt = null;
                        if(!haveStacks && altFac != null) {
                            // Try over with other alternate profile.
                            EmissionUnit euAlt = altFac.getMatchingEmissionUnit(eu.getCorrEpaEmuId());
                            boolean giveUp = false;
                            if(euAlt != null) {
                                euPAlt = euAlt.findProcess(p.getSccId());
                                if(euPAlt == null) giveUp = true;
                            } else giveUp = true;
                            if(!giveUp) {
                                String s = "In report " + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
                                + p.getSccId() + " had no stacks so looking for stacks in more recent profile";
                                writeError(REPLACE, logFile, s);
                                actualStackTotal = 0d;
                                actualFugitiveTotal = 0d;
                                totStacks = new HashMap<EgressPoint, Double>();
                                for(EmissionRow er : periodEmissions) {
                                    if(er.getTotalEmissions() != null && er.getTotalEmissionsV() != 0) {
                                        // Only look at those where there were emissions
                                        if(er.getPollutantCd().equals(PollutantDef.PM25FIL_CD) ) {
                                            continue; // skip those that would double count since PM2.5 is included in PM10
                                        }
                                        try {
                                            String neiCode = NonToxicPollutantDef.getNeiCode(er.getPollutantCd());
                                            if(neiCode == null || neiCode.equalsIgnoreCase("Inactive")) {
                                                // Leave these pollutants out
                                                // This includes "OC"  Organic Compounds
                                                continue;
                                            }

                                            actualStackTotal += er.getStackEmissionsV();
                                            actualFugitiveTotal += er.getFugitiveEmissionsV();

                                            List<ControlInfoRow> calc = ControlInfoRow.generateControlMatrix(altFac, euPAlt, p, er, false, false);
                                            if(ControlInfoRow.isProblems()) {
                                                String s2 = ControlInfoRow.getProblems().toString() + " for report " +
                                                report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
                                                + p.getSccId() + ", pollutantCd " + er.getPollutantCd();
                                                writeError(BOTH, logFile, s2);
                                            }
                                            boolean needFug = false;
                                            boolean needStk = false;
                                            if(!EmissionCalcMethodDef.isFactorMethod(er.getEmissionCalcMethodCd()) && er.getStackEmissionsV() > 0 && ControlInfoRow.stackTotal(calc) == 0) {
                                                // user has specified stack emissions but the profile does not support it
                                                needStk = true;
                                                needStkForSomePollutant = true;
                                            }
                                            if(!EmissionCalcMethodDef.isFactorMethod(er.getEmissionCalcMethodCd()) && er.getFugitiveEmissionsV() > 0 && ControlInfoRow.fugitiveTotal(calc) == 0) {
                                                // user has specified fugitive emissions but the profile does not support it
                                                needFug = true;
                                            }
                                            if(needFug || needStk) {
                                                // try again by adjusting control equipment efficiencies
                                                calc = ControlInfoRow.generateControlMatrix(altFac, euPAlt, p, er, needFug, needStk);
                                                if(ControlInfoRow.isProblems()) {
                                                    String s2 = ControlInfoRow.getProblems().toString() + " for report " +
                                                    report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
                                                    + p.getSccId() + ", pollutantCd " + er.getPollutantCd();
                                                    writeError(BOTH, logFile, s2);
                                                }
                                            }
                                            HashMap<EgressPoint, Double> stacks = new HashMap<EgressPoint, Double>();
                                            boolean ok = ControlInfoRow.addUpStackEmissions(calc, stacks, er.getTotalEmissionsV());
                                            addTotals(totStacks, stacks);
                                            if(!ok) {
                                                // need more detail about where problem occurred that were logged in function.
                                                String ss = "Previous log4j errors about egress point are in report " +
                                                report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
                                                + p.getSccId() + ", pollutantCd " + er.getPollutantCd();
                                                writeError(BOTH, logFile, ss);
                                            }
                                        } catch (ApplicationException ae) {
                                            String ss = "got ApplicationException at report " 
                                                + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
                                                + p.getSccId() + ", pollutantCd " + er.getPollutantCd();
                                            writeError(POINTEMIS, logFile, ss, ae);
                                            break; // leave for loop
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    EgressPoint bestFugitiveEP = null;
                    // For fugitive; stick with original profile
                    for(EgressPoint egp : euP.getAllEgressPoints()) {
                        if(!(EgrPointTypeDef.FUGITIVE.equals(egp.getEgressPointTypeCd()) ||
                                EgrPointTypeDef.FUGITIVE.equals(egp.getEgressPointTypeCd()))) {
                            continue;
                        }
                        if(bestFugitiveEP == null) {
                            bestFugitiveEP = egp;
                        } else {
                            if(bestFugitiveEP.getCorrelationId().intValue() < egp.getCorrelationId().intValue()) {
                                bestFugitiveEP = egp;
                            }
                        }
                    }
                    if(bestFugitiveEP == null) {
                        bestFugitiveEP = emissionUnitFugEP;
                    }

                    int tot = 0;
                    int unique = 0;
                    EgressPoint[] egPs = new EgressPoint[numStacksToKeep];
                    Double[] values = new Double[numStacksToKeep];
                    int numStacks = 0;
                    for(Entry<EgressPoint, Double> entry : totStacks.entrySet()) {
                        // If we have found a Stack which is the same as our best Fugitive, then skip it
                        // because it must have been found in the alternate facility inventory and the user must have
                        // modified the best Fugitive from the profile such that they changed it
                        // to a stack in the alternate facility inventory.
                        if(bestFugitiveEP != null && entry.getKey() != null && 
                                bestFugitiveEP.getCorrelationId().equals(entry.getKey().getCorrelationId())) {
                            String er = "Facility " + fac.getFacilityId() + ", report " + report.getEmissionsRptId() +
                            " EU " + eu.getEpaEmuId() + ": Skipping the stack " + entry.getKey().getEgressPointId() + " with correlation Id " +
                            entry.getKey().getCorrelationId() + " because it is the same as the fugitive being used.";
                            writeWarn(logFile, er);
                            continue;
                        }
                        if(entry.getKey() != null) { // handle stacks
                            if(numStacks < numStacksToKeep) {
                                egPs[numStacks] = entry.getKey();
                                values[numStacks] = entry.getValue();
                                numStacks++;
                            } else {
                                // keep largest ones.
                                // locate smallest one to replace
                                int smallestNdx = 0;
                                for(int i = 1; i < numStacksToKeep; i++) {
                                    if(values[i].doubleValue() < values[smallestNdx].doubleValue()) {
                                        smallestNdx = i;
                                    } else if(values[i].doubleValue() == values[smallestNdx].doubleValue() &&
                                            egPs[i].getCorrelationId().intValue() < egPs[smallestNdx].getCorrelationId().intValue()) {
                                        smallestNdx = i;
                                    }
                                }
                                // replace smallest?
                                if(entry.getValue().doubleValue() < values[smallestNdx]) {
                                    continue;
                                }
                                if(entry.getValue().doubleValue() == values[smallestNdx].doubleValue() &&
                                        entry.getKey().getCorrelationId().intValue() < egPs[smallestNdx].getCorrelationId().intValue()) {
                                    continue;
                                }
                                values[smallestNdx] = entry.getValue();
                                egPs[smallestNdx] = entry.getKey();
                            }
                        }
                    }

                    double computedStackEmissions = 0d;
                    for(Entry<EgressPoint, Double> entry : totStacks.entrySet()) {
                        if(entry.getKey() == null) continue;
                        computedStackEmissions += entry.getValue();
                    }

                    // total of computed stack for reduced number of stacks.
                    double computedReducedStackEmissions = 0d;
                    for(int i = 0; i < numStacks; i++) {
                        computedReducedStackEmissions += values[i];
                    }

                    // The % to each stack is determined by computation performed by ControlInfoRow.generateControlMatrix
                    // However, the actual stack emissions and fugitive emissions are taken from the emissions
                    // provided in the process section of the report itself.

                    TreeMap<Integer, Text> textNodes = new TreeMap<Integer, Text>();
                    if(actualStackTotal > 0d && numStacks == 0) {
                        String s = "Stack emissions specified in report " 
                            + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
                            + p.getSccId() + " for some pollutants but no stacks found--creating placeholder stack";
                        writeError(FACINVENT, logFile, s);
                        EgressPoint fictionalStackEP = new EgressPoint();
                        fictionalStackEP.setEgressPointTypeCd(EgrPointTypeDef.VERTICAL);
                        String stkName;
                        if(euP.getSccCode() == null) {
                            stkName = eu.getCorrEpaEmuId() + "." + "S";
                        } else {
                            stkName = eu.getCorrEpaEmuId() + "." + euP.getSccId() + "S";
                        }
                        fictionalStackEP.setEgressPointId(stkName);
                        // correlation id of -1 means fictional and use the egressPointId instead for eis identifier
                        fictionalStackEP.setCorrelationId(-1);
                        // set up so following code uses this fictional stack.
                        numStacks = 1;
                        egPs[0] = fictionalStackEP;
                        values[0] = actualStackTotal;
                        computedReducedStackEmissions = actualStackTotal;
                        computedStackEmissions = actualStackTotal;

                    }
   
                    for(int i = 0; i < numStacks; i++) {
                        EgressPoint egp = egPs[i];
                        if(haveStacks) {
                            // attempt to locate bettter egress point
                            // Don't do this--just use default values if necessary.
//                          if(altFac != null) {
//                          EgressPoint egpAlt = altFac.getEgressPointByCorr(egp.getCorrelationId());
//                          if(egpAlt != null) {
//                          String s = "In report " + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + " and scc "
//                          + p.getSccId() + " replaced egress point " + egp.getEgressPointId() + " with " + egpAlt.getEgressPointId() + " from more recent profile";
//                          writeError(REPLACE, logFile, s);
//                          egp = egpAlt;
//                          }
//                          }
                        }
                        egp.setFedSCCId(fedSccId); // save an SCC that uses this egress point
                        egressPointSet.add(egp);  // keep track for facility
                        Double d = values[i];
                        double percentD = 0d;
                        if(computedStackEmissions > 0) {
                            // this is  percent of stack emissions*  adjustment to make it a % of total emissions         * factor to adjust stack %s up because some left out
                            //percentD = (d/computedStackEmissions)*actualStackTotal/(actualStackTotal+ actualFugitiveTotal)*(computedStackEmissions/computedReducedStackEmissions) * 100d;
                            // simplification
                            percentD = d*actualStackTotal/(actualStackTotal+ actualFugitiveTotal)/computedReducedStackEmissions * 100d;
                        }
                        String percentStr = percentConvert((float)percentD);
                        id = fDom.createElement(RELEASE_POINT_APPORTIONMENT);
                        fPId.appendChild(id);
                        id2 = fDom.createElement(AVERAGE_PERCENT_EMISSIONS);
                        id.appendChild(id2);
                        int lastP = Integer.parseInt(percentStr);
                        if(lastP == 0) {
                            lastP = 1;
                            percentStr = "1";
                        }
                        tx = fDom.createTextNode(percentStr);
                        tot += lastP;
                        textNodes.put(new Integer(-lastP*1000 - (++unique)), tx);
                        id2.appendChild(tx);
                        id2 = fDom.createElement(RELEASE_POINT_APPORTIONMENT_IDENTIFICATION);
                        id.appendChild(id2);
                        //  if fictional use the correct identifier
                        String ss = Integer.toString(egp.getCorrelationId());
                        if(egp.getCorrelationId() == -1) {
                            ss = egp.getEgressPointId();
                        }
                        buildNewIdentifier(fDom, id2, ss, false, false);
                    }

                    int lastP = 0;
                    if(actualFugitiveTotal > 0) {
                        if(bestFugitiveEP != null) {
                            bestFugitiveEP.setFedSCCId(fedSccId); // save an SCC that uses this egress point
                            egressPointSet.add(bestFugitiveEP);  // keep track for facility
                            // Do not want to change the fugitive %.
                            double fPcent = actualFugitiveTotal/(actualStackTotal+ actualFugitiveTotal) * 100d;
                            String pcentStr = percentConvert((float)fPcent);
                            id = fDom.createElement(RELEASE_POINT_APPORTIONMENT);
                            fPId.appendChild(id);
                            id2 = fDom.createElement(AVERAGE_PERCENT_EMISSIONS);
                            id.appendChild(id2);
                            lastP = Integer.parseInt(pcentStr);
                            if(lastP == 0) {
                                lastP = 1;
                                pcentStr = "1";
                            }
                            tx = fDom.createTextNode(pcentStr);
                            textNodes.put(new Integer(-lastP*1000 - (++unique)), tx);
                            id2.appendChild(tx);
                            id2 = fDom.createElement(RELEASE_POINT_APPORTIONMENT_IDENTIFICATION);
                            id.appendChild(id2);
                            // if fictional use the correct identifier
                            String ss = Integer.toString(bestFugitiveEP.getCorrelationId());
                            if(bestFugitiveEP.getCorrelationId() == -1) {
                                ss = bestFugitiveEP.getEgressPointId();
                            }
                            buildNewIdentifier(fDom, id2, ss, false, false);
                        } else {
                            // use fictional one.
                            fictionalEmissionUnitFugEP.setFedSCCId(fedSccId); // save an SCC that uses this egress point
                            egressPointSet.add(fictionalEmissionUnitFugEP);
                            double fPcent = actualFugitiveTotal/(actualStackTotal+ actualFugitiveTotal) * 100d;
                            String pcentStr = percentConvert((float)fPcent);
                            id = fDom.createElement(RELEASE_POINT_APPORTIONMENT);
                            fPId.appendChild(id);
                            id2 = fDom.createElement(AVERAGE_PERCENT_EMISSIONS);
                            id.appendChild(id2);
                            lastP = Integer.parseInt(pcentStr);
                            if(lastP == 0) {
                                lastP = 1;
                                pcentStr = "1";
                            }
                            tx = fDom.createTextNode(pcentStr);
                            textNodes.put(new Integer(-lastP*1000 - (++unique)), tx);
                            id2.appendChild(tx);
                            id2 = fDom.createElement(RELEASE_POINT_APPORTIONMENT_IDENTIFICATION);
                            id.appendChild(id2);
                            // if fictional use the correct identifier
                            String ss = Integer.toString(fictionalEmissionUnitFugEP.getCorrelationId());
                            if(fictionalEmissionUnitFugEP.getCorrelationId() == -1) {
                                ss = fictionalEmissionUnitFugEP.getEgressPointId();
                            }
                            buildNewIdentifier(fDom, id2, ss, false, false);
                        }
                    }
                    tot += lastP;
                    if(textNodes.size() > 0 && tot != 100) {
                        // make the total come out to 100%
                        int diff = 100 - tot;
                        int adj = 1;
                        if(diff < 0) {
                            adj = -1;
                            diff = - diff;
                        }
                        // Example case.  If the percents are .6 .6 .6 .6 .6 .6 .6 and 95.8
                        // Then they are rounded to 1 1 1 1 1 1 1 and 96 which add up to 103.
                        // In this case you can only subtract from the largest (three times).
                        while(diff > 0) { // may require more than one pass
                            for(Text t : textNodes.values()) {
                                int v = Integer.parseInt(t.getData()) + adj;
                                if(v == 0) continue;  // don't go below 1%
                                t.setData(Integer.toString(v));
                                diff -= 1;
                                if(diff == 0) break;
                            }
                        }
                    }
                }

                if(hadEmissions) {
                    pEuId.appendChild(pPId);
                    fEuId.appendChild(fPId);
                    processEmissions = true;
                }
            }
        return processEmissions;
    }

    private boolean buildEmissions(Facility fac, Document dom, Element pId, String materialUnits, EmissionsReport report, 
            boolean ts, List<EmissionRow> periodEmissions, EmissionUnit eu, EmissionsReportEU e,
            EmissionsReportPeriod p) {
        Text tx = null;
        Element id = null;
        Element id2 = null;
        boolean hasEmissions = false;
        try {
            FireRow[] rows = new FireRow[0];
            p.setFireRows(rows);
            periodEmissions = EmissionRow.getEmissions(
                    report.getReportYear(), p, scReports, false, true, 0, logger);
        } catch(ApplicationException ae) {
            String s = "got ApplicationException at report " 
                + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId()
                + " and scc " + p.getSccId() + "; these emissions skipped.";
            writeError(POINTEMIS, logFile, s, ae);
            return hasEmissions;
        }

        for(EmissionRow em : periodEmissions) {
            String neiCode = null;
            if(em.getPollutantCd() == null) {
                String s = report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId()
                + " and scc " + p.getSccId()  + ".  PollutantCd is null.";
                writeError(POINTEMIS, logFile, s);
                continue;
            }
            try {
                neiCode = NonToxicPollutantDef.getNeiCode(em.getPollutantCd());
            }catch(ApplicationException ae) {
                String s = "got ApplicationException at report " 
                    + report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId()
                    + " and scc " + p.getSccId() + " and pollutantCd " + em.getPollutantCd()
                    + ".  Emission will be included.";
                writeError(POINTEMIS, logFile, s, ae);
            }

            if(neiCode == null || neiCode.equalsIgnoreCase("Inactive")) {
                // Leave these pollutants out
                continue;
            }
            if(EmissionsReport.convertStringToNum(em.getTotalEmissions()) == 0d) {
                // skip when emissions is zero
                continue;
            }
            if(em.getOrder() == null && !includeHAPS) {
                // skip when emissions not in first table (Criteria pollutants & Lead)
                continue;
            }
            hasEmissions = true;
            id = dom.createElement(REPORTING_PERIOD_EMISSIONS);
            pId.appendChild(id);
            id2 = dom.createElement(POLLUTANT_CODE);
            id.appendChild(id2);
            tx = dom.createTextNode(neiCode);
            id2.appendChild(tx);
            if(em.getTotalEmissions() != null) {
                String toUnits = "TON";
                if(em.getPollutantCd().equals("7439921")) toUnits = "LB";  // if lead use pounds
                if(em.getOrder() == null) toUnits = "LB";  // if HAPs then use pounds
                String fugE = convertToFourSig(em.getFugitiveEmissions(), em.getEmissionsUnitNumerator(), toUnits);
                String stkE = convertToFourSig(em.getStackEmissions(), em.getEmissionsUnitNumerator(), toUnits);
                if(em.getFugitiveEmissions() == null) {
                    fugE = "";
                }
                if(em.getStackEmissions() == null) {
                    stkE = "";
                }
                String output = report.getReportYear().toString() + "," + fac.getFacilityId() + "," + eu.getEpaEmuId() + "," +
                p.getSccId() + "," + em.getPollutantCd() + "," + fugE + "," + stkE + "," + toUnits;
                writeLog(epaData, output);

                id2 = dom.createElement(TOTAL_EMISSIONS);
                id.appendChild(id2);
                // express as four significant digits
                tx = dom.createTextNode(convertToFourSig(em.getTotalEmissions(), em.getEmissionsUnitNumerator(), toUnits));
                id2.appendChild(tx);
                id2 = dom.createElement(EMISSIONS_UNIT_OF_MEASURE_CODE);
                id.appendChild(id2);
                tx = dom.createTextNode(UnitDef.getUsEpaUnitCd(toUnits));
                id2.appendChild(tx);
            }
            if(em.getFactorNumericValue() != null && !ts) {
                id2 = dom.createElement(EMISSION_FACTOR);
                id.appendChild(id2);
                String s = em.getFactorNumericValue();
                if(s != null) {
                    s = s.replaceAll(",", "");
                }
                tx = dom.createTextNode(s);
                id2.appendChild(tx);
                id2 = dom.createElement(EMISSION_FACTOR_NUMERIC_UNI_OF_MEASURE_CODE);
                id.appendChild(id2);
                tx = dom.createTextNode("LB");
                id2.appendChild(tx);
                id2 = dom.createElement(EMISSION_FACTOR_DENOMINATOR_UNIT_OF_MEASURE_CODE);
                id.appendChild(id2);
                tx = dom.createTextNode(UnitDef.getUsEpaUnitCd(materialUnits));
                id2.appendChild(tx);
            }
            id2 = dom.createElement(EMISSION_CALCULATION_METHOD_CODE);
            id.appendChild(id2);
            if(em.getEmissionCalcMethodCd() != null) {
                tx = dom.createTextNode(getEmissionCalculationMethodCode(em.getEmissionCalcMethodCd()));
                id2.appendChild(tx);
            } else {
                String s = report.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId()
                + " and scc " + p.getSccId()  + " and PollutantCd " + neiCode + ".  Method calculation code is missing.";
                writeError(POINTEMIS, logFile, s);
            }
            if(exists(em.getExplanation()) && !ts) {
                id2 = dom.createElement(EMISSIONS_COMMENT);
                id.appendChild(id2);
                String desc = cleanString(EMISSIONS_COMMENT, em.getExplanation());
                if(desc != null && desc.length() > emissionsCommentMaxSize) {
                    desc = desc.substring(0, emissionsCommentMaxSize - 1 - 3) + "...";
                }
                tx = dom.createTextNode(desc);
                id2.appendChild(tx);
            }
        }
        return hasEmissions;
    }

    void buildNewIdentifier(Document dom, Element id, String ident, boolean setBeginDate, boolean setEndDate) {
        Element id2 = null;
        Text tx = null;
        id2 = dom.createElement(IDENTIFIER);
        id.appendChild(id2);
        tx = dom.createTextNode(ident);
        id2.appendChild(tx);
        id2 = dom.createElement(PROGRAM_SYSTEM_CODE);
        id.appendChild(id2);
        tx = dom.createTextNode(NEW_PROGRAM_SYSTEM_CODE_VALUE);
        id2.appendChild(tx);
        if(setBeginDate) {
            id2 = dom.createElement(EFFECTIVE_DATE);
            id.appendChild(id2);
            tx = dom.createTextNode(beginPeriodDateYYYMMDD); //  According to Martin Husk: date on which the Identifier was first being used.
            id2.appendChild(tx);
        }
        if(setEndDate) {
            id2 = dom.createElement(END_DATE);
            id.appendChild(id2);
            tx = dom.createTextNode(beforeBeginPeriodDateYYYMMDD);
            id2.appendChild(tx);
        }
    }

    private void insertCoordinates(Float lat, Float lng, Document dom, Element id, String msg) {
        String format = "###.#####";
        DecimalFormat decFormat = new DecimalFormat(format);
        Element id2 = null;
        Text tx = null;
        if(lat != null) {
            id2 = dom.createElement(LATITUDE_MEASURE);
            id.appendChild(id2);
            StringBuffer result = new StringBuffer();
            StringBuffer notUsed = new StringBuffer();
            FieldPosition unUsed = new FieldPosition(1);
            result = decFormat.format(new Double(lat), notUsed, unUsed);
            tx = dom.createTextNode(result.toString());
            id2.appendChild(tx);
        } else {
            writeError(BOTH, logFile, "(will never occur) Latitude not specified for " + msg);
        }

        if(lng != null) {
            id2 = dom.createElement(LONGITUDE_MEASURE);
            id.appendChild(id2);
            StringBuffer result = new StringBuffer();
            StringBuffer notUsed = new StringBuffer();
            FieldPosition unUsed = new FieldPosition(1);
            result = decFormat.format(new Double(lng), notUsed, unUsed);
            tx = dom.createTextNode(result.toString());
            id2.appendChild(tx);
        } else {
            writeError(BOTH, logFile, "(will never occur) Longitude not specified for " + msg);
        }
    }

    private void finishSite(Document facDom, Document poiDom, Element facElm, Element poiElm) {
        finishSiteCnt ++;
        //  finish site comment
        StringBuffer comment = new StringBuffer(30*commentSCSCId.size());
        String suffix = "";
        if(commentSCSCId.size() > 1) {
            suffix = "s";
        }
        comment.append("Federal SCSC ID" + suffix);
        for(String s : commentSCSCId) {
            comment.append(" " + s);
        }
        comment.append("; FpId" + suffix);
        for(Integer s : commentFpId) {
            comment.append(" " + s);
        }
        comment.append("; ReportId" + suffix);
        for(Integer s : commentReportId) {
            comment.append(" " + s);
        }
        comment.append(";");
        Text tx = facDom.createTextNode(comment.toString());
        facElm.appendChild(tx);
        tx = poiDom.createTextNode(comment.toString());
        poiElm.appendChild(tx);

        // finish site description--only for facility category
        if(fSiteDescriptionElm != null) {
        	String desc = null;
            if(siteDesc != null) {
            	desc = cleanString(FACILITY_SITE_DESCRIPTION, siteDesc.toString());
            }
            if(desc != null && desc.length() > facilityDescriptionMaxSize) {
                desc = siteDesc.substring(0, facilityDescriptionMaxSize - 1 - 3).toString() + "...";
            }
            tx = facDom.createTextNode(desc);
            fSiteDescriptionElm.appendChild(tx);
            tx = poiDom.createTextNode(desc);
            pSiteDescriptionElm.appendChild(tx);
        }

        // write it out
        try {
            serializeEmpty(facilitySerializer, facDom);
            serializeEmpty(pointSerializer, poiDom);
        }catch(IOException ioe) {
            String s = "serializeEmpty() failed";
            writeError(BOTH, logFile, s, ioe);
        }
        resetForNext();
    }

    private void finishSite(Document facMismatchDom) {
        // write it out
        try {
            serializeEmpty(facilityMismatchSerializer, facMismatchDom);;
        }catch(IOException ioe) {
            String s = "serializeEmpty() failed";
            writeError(MISMATCH, logFile, s, ioe);
        }
        resetMismatchForNext();
    }

    private void resetMismatchForNext() {
//      initialize for next time
        facilityMismatchSiteElm = null;
        facilityAlreadyAdded = false;
        emissionUnitMismatchElm = null;
        emissionUnitMismatchId = null;
        emissionUnitMismatchIdentifier = false;
        usEpaEgressPoints = new HashSet<Integer>();
        usEpaFictitiousEgressPoints = new HashSet<String>();
    }

    private void resetForNext() {
//      initialize for next time
        naics = null;
        facilitySiteElm = null;
        pointFacilitySiteElm = null;
        facilitySiteCommentElm = null;
        pointSiteCommentElm = null;
        commentSCSCId = new ArrayList<String>();
        commentFpId = new ArrayList<Integer>();
        commentReportId = new ArrayList<Integer>();
        siteDesc = new StringBuffer(1000);
        fSiteDescriptionElm = null;
        pSiteDescriptionElm = null;
    }

    private void addTotals(HashMap<EgressPoint, Double> totStacks,
            HashMap<EgressPoint, Double> stacks) {
        // Add in the values after multiplying the total by the fraction for the stack.
        double totalEmissions = 0d;
        for(Double d : stacks.values()) {
            totalEmissions += d;
        }
        if(totalEmissions > 0d) {
            for(Entry<EgressPoint, Double> entry : stacks.entrySet()) {
                // get the fraction that goes to this egress point
                // Fraction determined by equipment; total from report.
                double newAmount = entry.getValue();
                EgressPoint ep = entry.getKey();
                if(totStacks.containsKey(ep)) {
                    Double oldAmount = totStacks.get(ep);
                    if(oldAmount != null) {
                        newAmount = oldAmount + newAmount;
                    }
                }
                totStacks.put(ep, new Double(newAmount));
            }
        }
    }

    String percentConvert(float percent) {
        String format = "##0";
        DecimalFormat decFormat = new DecimalFormat(format);
        return decFormat.format(percent);
    }

    String percentConvert_1p0to100p0(float percent) {
        // Range must be greater or equal to 1.0; one decimal place.
        String format = "##0.0";
        DecimalFormat decFormat = new DecimalFormat(format);
        if(percent < 1.0f) {
            percent = 1.0f;
        }
        return decFormat.format(percent);
    }

    String percentConvert_1p00to100p00(float percent) {
        // Range must be greater or equal to 1.0; one decimal place.
        String format = "##0.00";
        DecimalFormat decFormat = new DecimalFormat(format);
        if(percent < 1.0f) {
            percent = 1.0f;
        }
        return decFormat.format(percent);
    }

    String convertToFourSig(String value, String units, String newUnits) {
        if(value == null || value.length() == 0) {
            return null;
        }
        String noComma = value.replaceAll(",", "");
        if(noComma.length() == 0) return null;
        DecimalFormat decFormat = new DecimalFormat("0.###E00");
        float f = Float.valueOf(noComma);
        if(!units.equals(newUnits)) {
            if(newUnits.equals("LB")) {
                f = f * 2000f;
            } else if(newUnits.equals("TON")) {
                f = f / 2000f;
            }
        }
        String s = decFormat.format(f);
        return s;
    }
    
    boolean withinRange(String number, float low, float high) {
        float f = Float.valueOf(number.replaceAll(",", ""));
        if(f < low || f > high) return false;
        return true;
    }

    String measurementConvertToTenths(float meas) {
        String format = "###########0.0";
        DecimalFormat decFormat = new DecimalFormat(format);
        return decFormat.format(meas);
    }

    String measurementConvertToHundredths(float meas) {
        String format = "###########0.00";
        DecimalFormat decFormat = new DecimalFormat(format);
        return decFormat.format(meas);
    }

    String measurementConvertToWhole(float meas) {
        String format = "###########0.";
        DecimalFormat decFormat = new DecimalFormat(format);
        return decFormat.format(meas);
    }

    String getEmissionCalculationMethodCode(String s) {
        if(s.equals("1")) { // factor:CEM
            return "30";
        }
        if(s.equals("2")) { // factor:ENG. JUDGEMENT
            return "30";
        }
        if(s.equals("3")) { // factor:MATERIAL BALANCE
            return "30";
        }
        if(s.equals("4")) { // factor:STACK TEST
            return "30";
        }
        if(s.equals("7")) { // factor:MFR. SPEC
            return "33";
        }
        if(s.equals("11")) { // factor:VENDOR
            return "31";
        }
        if(s.equals("10")) { // factor:SITE-SPECIFIC
            return "30";
        }
        if(s.equals("12")) { // factor:TRADE GROUP
            return "32";
        }
        if(s.equals("109")) { // factor:OEPA (auto calculate)
            return "28";
        }
        if(s.equals("201")) { // emissions:CEM
            return "1";
        }
        if(s.equals("202")) { // emissions:ENG. JUDGEMENT
            return "2";
        }
        if(s.equals("203")) { // emissions:MAT. BALANCE
            return "3";
        }
        if(s.equals("204")) { // emissions:STACK TEST
            return "4";
        }
        return "";
    }

    String getReleasePointTypeCd(String type) {
        if(type == null) {

        }
        if(type.equals(EgrPointTypeDef.FUGITIVE)) {
            return "1";
        }
        if(type.equals(EgrPointTypeDef.FUGITIVE)) {
            return "1";
        }
        if(type.equals(EgrPointTypeDef.HORIZONTAL)) {
            return "3";
        }
        if(type.equals(EgrPointTypeDef.VERTICAL)) {
            return "2";
        }
        if(type.equals(EgrPointTypeDef.VERTICAL)) {
            return "5";
        }
        return "99";
    }

    String getCeType(String type) {
        if(type.equals("CA")) {  // Adsorber
            return "48";
        } else if(type.equals("CE")) { // Catalytic Incinerator 116-->109
            return "109";
        } else if(type.equals("CD")) { // Condenser
            return "132";
        } else if(type.equals("CM")) { // Cyclone/Multiclone
            return "121";
        } else if(type.equals("EP")) { // Electrostatic Precipitator
            return "128";
        } else if(type.equals("FB")) { // Filter/Baghouse
            return "100";
        } else if(type.equals("DS")) { // Dry Scrubber
            return "119";
        } else if(type.equals("WS")) { // Wet Scrubber
            return "141";
        } else if(type.equals("FR")) { // Flare
            return "23";
        } else if(type.equals("PB")) { // Passive Filter
            return "127";
        } else if(type.equals("TI")) { // Thermal Incinerator
            return "133";
        } else if(type.equals("FS")) { // Fugitive Dust Suppression
            return "217";
        } else if(type.equals("CC")) { // Catalytic Converter
            return "203";
        } else if(type.equals("CR")) { // NOx Reduction Technology  107-->140
            return "140";
        } else if(type.equals("SC")) { // Settling Chamber
            return "209";
        } else if(type.equals("OT")) { // Other
            return "99";
        } else {
            return "99";  // none of the above
        }
    }

    String getYearStr(Timestamp ts) {
        if(ts == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String rtn = sdf.format(ts);
        return rtn;
    }

    String getYearStrPlus1(Timestamp ts) {
        if(ts == null) {
            return "";
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts.getTime());
        int y = c.get(Calendar.YEAR) + 1;
        String rtn = Integer.toString(y);
        return rtn;
    }

    String getDateStr(Timestamp ts) {
        if(ts == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String rtn = sdf.format(ts);
        return rtn;
    }

    String getFileDate(Timestamp ts) {
        if(ts == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String rtn = sdf.format(ts);
        return rtn;
    }

    String getunitDesignCapacityUnitOfMeasureCode(String cd) {
        if(cd == null) {
            return "";
        }
        if(cd.equals("mw")) {
            return UnitDef.getUsEpaUnitCd("MW");
        }
        if(cd.equals("mb")) {
            return UnitDef.getUsEpaUnitCd("E6BTU/HR");
        }
        return "";
    }

    String getFpOperatingStatusCd(String cd) {
        if(cd == null) {
            return "";
        }
        if(cd.equals(OperatingStatusDef.OP)) {
            return "OP";
        }
        if(cd.equals(OperatingStatusDef.SD)) {
            return "PS";
        }
        if(cd.equals(OperatingStatusDef.IA)) { // inactive
            // inactive just means not producing anything right now.  The
            // facilty still needs to submit emissions inventories (to indicate zero emissions).
            return "ONRE";
        }
        return "";
    }

    String getEuOperatingStatusCd(String cd) {
        if(cd == null) {
            return "";
        }
        if(cd.equals(OperatingStatusDef.OP)) {
            return "OP";
        }
        if(cd.equals(OperatingStatusDef.SD)) {
            return "PS";
        }
        if(cd.equals(OperatingStatusDef.IA)) { // invalid
            return "";
        }
        return "";
    }

    String getUnitTypeCode(String cd) {
        if(cd == null) {
            return "999";
        }
        if(cd.equals("bo")) {
            return "100";
        }
        if(cd.equals("tu")) {
            return "120";
        }
        if(cd.equals("ge")) {
            return "160";
        }
        return "999";
    }

    private void serializeEmpty(XMLSerializer serializer, DocumentFragment dom) 
    throws DAOException {
        try {
            serializer.serialize(dom);
        }catch(IOException ioe) {
            String s = "serializeEmpty() failed";
            writeError(BOTH, logFile, s, ioe);
            throw new DAOException(s);
        }
        //          Keep same dom object but empty it.
        NodeList nl = dom.getChildNodes();
        for(int i = nl.getLength() - 1; i >= 0 ; i--) {
            org.w3c.dom.Node n = nl.item(i);
            dom.removeChild(n);
        }
    }

    private void serializeEmpty(XMLSerializer serializer, Document domF) 
    throws DAOException {
        try {
            serializer.serialize(domF);
        }catch(IOException ioe) {
            String s = "serializeEmpty() failed";
            writeError(BOTH, logFile, s, ioe);
            throw new DAOException(s);
        }
        //          Keep same dom object but empty it.
        NodeList nl = domF.getChildNodes();
        for(int i = nl.getLength() - 1; i >= 0 ; i--) {
            org.w3c.dom.Node n = nl.item(i);
            domF.removeChild(n);
        }
    }

    private void writeError(String type, FileWriter logFile, String s, Exception e){
        try {
            logFile.write(type + "ERROR: " + s + ".  Message=" + e.getMessage() + "\n");
            logger.error(s, e);
        } catch(IOException ex) {
            ;
        }
    }

    protected void writeError(String type, FileWriter logFile, String s){
        try {
            logFile.write(type + "ERROR: " + s + "\n");
        } catch(IOException ex) {
            ;
        }
    }
    
    protected void writeWarn(String type, FileWriter logFile, String s){
        if(includeWarns) {
            try {
                logFile.write(type + "WARNING: " + s + "\n");
            } catch(IOException ex) {
                ;
            }
        }
    }

    protected void writeWarn(FileWriter logFile, String s){
        if(includeWarns) {
            try {
                logFile.write("WARNING: " + s + "\n");
            } catch(IOException ex) {
                ;
            }
        }
    }

    private void writeLog(FileWriter logFile, String str)
    {
        try {
            logFile.write(str + "\n");
        } catch(IOException ex) {
            logger.error("logFile.write failed", ex);
        }
    }

    private String getNonNull(String s) {
        String rtn = s;
        if(s == null){
            rtn = "";
        }
        return rtn;
    }

    protected final List<ValidationMessage> verifyFacilityReport(EmissionsReport report, Facility facility) {
        try {
            erBO.locatePeriodNames(facility, report);
        } catch(RemoteException re) {
            writeError(POINTEMIS, logFile, "call to locatePeriodNames() failed for report " + report.getEmissionsRptId(), re);
        }
        scReports = new ReportTemplates();
        try {
            scReports = getEmissionsReportService().retrieveSCEmissionsReports(report.getReportYear(),
            		report.getContentTypeCd(),
                    facility.getFacilityId());
        } catch(RemoteException re) {
            String s = "failed to locate ReportTemplates for year " + report.getReportYear() + " and type " +
            " for report " + report.getEmissionsRptId();
            writeError(POINTEMIS, logFile, s, re);
        }
        /*
        if(!report.isRptEIS()) {
            // If EIS turned off, then clear the EIS report template.
            scReports.setScEIS((SCEmissionsReport)null);
        }
        if(!report.isRptES()) {
            scReports.setScES((SCEmissionsReport)null);
        }
        */

        //Basically a copy of internalSubmitVerify from ReportProfileBase.java.
        List<ValidationMessage> validationMessages = null;
        // Do Facility Validate of the EUs: if not EG71
        ArrayList<Integer> euListBasic = new ArrayList<Integer>();
        ArrayList<Integer> euList = new ArrayList<Integer>();
        report.getReportEmuIds(facility, scReports, euListBasic, euList);
        // List<ValidationMessage> vml0 = new ArrayList<ValidationMessage>();
        // List<ValidationMessage> vml1 = new ArrayList<ValidationMessage>();
        List<ValidationMessage> vml2 = new ArrayList<ValidationMessage>();
        try {
           //  vml0 = FacilityValidation.validateBasicEmissionsReport(facility, euListBasic, true);
            // vml1 = FacilityValidation.validateFERandESemissionReport(facility.getFpId(), euList, true);
            //if(report.isRptEIS() && report.getReportYear() > 2007) {
            if(report.getReportYear() > 2007) {
                vml2 = FacilityValidation.validateEISemissionReport(report, facility, euListBasic, euList, true);
            }
        } catch (ValidationException ve) {
            String s = "failed to validate the facility" + facility.getFacilityId() + "--partial validation performed";
            writeError(POINTEMIS, logFile, s, ve);
        }
        List<ValidationMessage> billingErr = FacilityValidation.determineMissingBilling(facility, report.getReportYear(), true);
        if(billingErr.size() > 0) {
            // Could not find billing contact appropriate for report
            // check for current billing contact.
            if(facility.getBillingContact() == null) {
                ValidationMessage m = billingErr.get(0);
                ValidationMessage vMsg = new ValidationMessage("MissingCurrentBilling",
                        "Facility:  There is no active Billing Contact",
                        ValidationMessage.Severity.ERROR, "contact");
                vMsg.setReferenceID(m.getReferenceID());  // use existing reference ID
                billingErr.add(vMsg);
            }
        }
        try {
            validationMessages = report.submitVerify(getEmissionsReportService(),
                    vml2.size() != 0);  // removed:  || vml1.size() != 0  vml0.size() != 0 || 
            validationMessages.addAll(billingErr);
            // validationMessages.addAll(vml0);
            // validationMessages.addAll(vml1);
            validationMessages.addAll(vml2);
        } catch (ApplicationException ae) {
            String  s = "failed to validate report " + report.getEmissionsRptId() + "--partial validation performed";
            writeError(POINTEMIS, logFile, s, ae);
        }
//        } catch (RemoteException re) {
//            String  s = "failed to validate report " + report.getEmissionsRptId() + "--partial validation performed";
//            writeError(POINTEMIS, logFile, s, re);
//        }
        return validationMessages;
    }

    public String getCandidateIncludedStr() {
        return candidateIncludedStr;
    }

    public String getErrorStr() {
        return errorStr;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        setLastEmissionsYear(year - 1);
    }

    public String getSingleFacility() {
        return singleFacility;
    }

    public void setSingleFacility(String singleFacility) {
        this.singleFacility = singleFacility;
    }

    public String getFacilityMismatchXmlFileStat() {
        setPaths();
        String rtn = facilityMismatchXmlFileBase;
        File f = new File(facilityMismatchXmlFileName);
        if(f.exists()) {
            String date = getFileDate(new Timestamp(f.lastModified()));
            rtn = rtn + " (size = " + f.length() + ", date = " + date + ")";
        } else {
            rtn = rtn + " --does not exist";
        }
        return rtn;
    }

    public String getFacilityXmlFileStat() {
        setPaths();
        String rtn = facilityXmlFileBase;
        File f = new File(facilityXmlFileName);
        if(f.exists()) {
            String date = getFileDate(new Timestamp(f.lastModified()));
            rtn = rtn + " (size = " + f.length() + ", date = " + date + ")";
        } else {
            rtn = rtn + " --does not exist";
        }
        return rtn;
    }

    public String getPointXmlFileStat() {
        setPaths();
        String rtn = pointXmlFileBase;
        File f = new File(pointXmlFileName);
        if(f.exists()) {
            String date = getFileDate(new Timestamp(f.lastModified()));
            rtn = rtn + " (size = " + f.length() + ", date = " + date + ")";
        } else {
            rtn = rtn + " --does not exist";
        }
        return rtn;
    }

    public String getLogFileStat() {
        setPaths();
        String rtn = logFileString;
        File f = new File(logFileName);
        if(f.exists()) {
            String date = getFileDate(new Timestamp(f.lastModified()));
            rtn = rtn + " (size = " + f.length() + ", date = " + date + ")";
        } else {
            rtn = rtn + " --does not exist";
        }
        return rtn;
    }

    public boolean isCreateReportButton() {
        return createReportButton;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public boolean isCancelButton() {
        return cancelButton;
    }

    public boolean isAllowInput() {
        return allowInput;
    }

    public String getSubmittalComment() {
        return submittalComment;
    }

    public void setSubmittalComment(String submittalComment) {
        this.submittalComment = submittalComment;
    }

    public String getEisLogin() {
        return eisLogin;
    }

    public void setEisLogin(String eisLogin) {
        this.eisLogin = eisLogin;
    }

    public String getXmlInfo() {
        return xmlInfo;
    }

    public String getWhatInReport() {
        return whatInReport;
    }

    public us.oh.state.epa.stars2.database.dbObjects.document.Document getLogFileDoc() {
        logFileDoc.setBasePath("EIS" + File.separator + logFileString);
        return logFileDoc;
    }

    public us.oh.state.epa.stars2.database.dbObjects.document.Document getPointXmlFileDoc() {
        pointXmlFileDoc.setBasePath("EIS" + File.separator + pointXmlFileBase);
        return pointXmlFileDoc;
    }

    public us.oh.state.epa.stars2.database.dbObjects.document.Document getFacilityXmlFileDoc() {
        facilityXmlFileDoc.setBasePath("EIS" + File.separator + facilityXmlFileBase);
        return facilityXmlFileDoc;
    }

    public us.oh.state.epa.stars2.database.dbObjects.document.Document getFacilityMismatchXmlFileDoc() {
        facilityMismatchXmlFileDoc.setBasePath("EIS" + File.separator + facilityMismatchXmlFileBase);
        return facilityMismatchXmlFileDoc;
    }

    public boolean isPackageGenerated() {
        setPaths();
        return packageGenerated;
    }

    public boolean isZipGenerated() {
        return zipGenerated;
    }

    public String getZipFacilityName() {
        return zipFacilityName;
    }

    public String getZipPointName() {
        return zipPointName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public us.oh.state.epa.stars2.database.dbObjects.document.Document getFacilityMismatchZipFileDoc() {
        facilityMismatchZipFileDoc.setBasePath(facilityMismatchZipFileString);
        return facilityMismatchZipFileDoc;
    }

    public us.oh.state.epa.stars2.database.dbObjects.document.Document getFacilityZipFileDoc() {
        facilityZipFileDoc.setBasePath(facilityZipFileString);
        return facilityZipFileDoc;
    }

    public us.oh.state.epa.stars2.database.dbObjects.document.Document getPointZipFileDoc() {
        pointZipFileDoc.setBasePath(pointZipFileString);
        return pointZipFileDoc;
    }

    public int getSubmittalCommentMaxSize() {
        return submittalCommentMaxSize;
    }

    public String getFacilityZipFileBase() {
        return facilityZipFileBase;
    }

    public String getPointZipFileBase() {
        return pointZipFileBase;
    }

    public boolean isIncludeHAPS() {
        return includeHAPS;
    }

    public void setIncludeHAPS(boolean includeHAPS) {
        this.includeHAPS = includeHAPS;
    }

    public UploadedFile getUsEpaEPs() {
        return usEpaEPs;
    }

    public void setUsEpaEPs(UploadedFile usEpaEPs) {
        this.usEpaEPs = usEpaEPs;
        if(usEpaEPs != null) usEpaEPsStr = usEpaEPs.getFilename();
    }

    public UploadedFile getUsEpaEUs() {
        return usEpaEUs;
    }

    public void setUsEpaEUs(UploadedFile usEpaEUs) {
        this.usEpaEUs = usEpaEUs;
        if(usEpaEUs != null) usEpaEUsStr = usEpaEUs.getFilename();
    }

    public UploadedFile getUsEpaFacilities() {
        return usEpaFacilities;
    }

    public void setUsEpaFacilities(UploadedFile usEpaFacilities) {
        this.usEpaFacilities = usEpaFacilities;
        if(usEpaFacilities != null) usEpaFacilitiesStr = usEpaFacilities.getFilename();
    }

    public UploadedFile getUsEpaProcesses() {
        return usEpaProcesses;
    }

    public void setUsEpaProcesses(UploadedFile usEpaProcesses) {
        this.usEpaProcesses = usEpaProcesses;
        if(usEpaProcesses != null) usEpaProcessesStr = usEpaProcesses.getFilename();
    }

    public String getUsEpaEPsStr() {
        return usEpaEPsStr;
    }

    public String getUsEpaEUsStr() {
        return usEpaEUsStr;
    }

    public String getUsEpaFacilitiesStr() {
        return usEpaFacilitiesStr;
    }

    public String getUsEpaProcessesStr() {
        return usEpaProcessesStr;
    }

    public String getZipFacilityMismatchName() {
        return zipFacilityMismatchName;
    }

    private void processUsEpaEgressPointsFacility(Document facMismatchDom, UsEpaFacilityBuffer fBuf,
            Facility matchingFac, Facility alternateFac) throws DAOException {
        // Go through all EGs the US EPA knows of and set appropriately.
        UsEpaEgressPointBuffer epBuf = UsEpaEgressPointBuffer.getBuf(this);  // get first/next record
        while(!UsEpaEgressPointBuffer.isEndOfFacility(this, fBuf.facilitySiteIdentifier)) {
            String euCorrIdStr = null;
            // Determine if this is a fictional egress point
            // If Stack then  "correlationId" . S
            // or             "correlationId" . "SCCId" S
            // If fugitive egress point then  "correlationId" . F
            boolean egDoesNotExist = true;
            String epIdentifier = epBuf.releasePointIdentifier;
            if(epIdentifier.length() > 0 && (epIdentifier.endsWith("S") || epIdentifier.endsWith("F"))) {
                if(epIdentifier.length() > 2 && epIdentifier.endsWith("S")) {
                    if(epIdentifier.endsWith(".S")) {
                        // no SCCId
                        euCorrIdStr = epIdentifier.subSequence(0, epIdentifier.length() - 2).toString();
                    } else if(epIdentifier.length() > 10 && epIdentifier.lastIndexOf('.') == epIdentifier.length() - 10){
                        // includes SCCid
                        euCorrIdStr = epIdentifier.subSequence(0, epIdentifier.lastIndexOf('.')).toString();
                    }
                } else if(epIdentifier.length() > 2 && epIdentifier.endsWith(".F")) {
                    euCorrIdStr = epIdentifier.subSequence(0, epIdentifier.length() - 2).toString();
                }
                // Determine if the EU exists--which means keep the fictitious egress point
                if(euCorrIdStr != null) {
                    EmissionUnit eu = null;
                    try {
                        Integer euCorrId = new Integer(euCorrIdStr);
                        eu = matchingFac.getMatchingEmissionUnit(euCorrId);
                    } catch(NumberFormatException nfe) {
                        // since not a number, cannot be a correlation Id.
                    }
                    if(eu != null) {
                        // Is a "real" fictitious egress point--keep it & use it if needed
                        egDoesNotExist = false;
                        usEpaFictitiousEgressPoints.add(epIdentifier); // keep track of it so we don't create it again 
                    }
                }
            } else {
                EgressPoint ep = null;
                Integer corrId = null;
                try {
                    corrId = new Integer(epBuf.releasePointIdentifier);
                    ep = matchingFac.getEgressPointByCorr(corrId);
                    if(ep == null && alternateFac != null) {
                        // check alternate
                        ep = alternateFac.getEgressPointByCorr(corrId);
                    }
                } catch(NumberFormatException nfe) {
                    // since not a number, cannot be a correlation Id.
                }
                if(ep != null) {
                    //  both have the same EU
                    egDoesNotExist = false;
                    usEpaEgressPoints.add(corrId); // keep track of egress points the US EPA already knows of.
                }
            }
            if(egDoesNotExist) {
                // egress point is unknown.  Set the end date on identifier.
                if(markMismatchEgressPointDead(facMismatchDom, matchingFac, epBuf)) {
                    writeWarn(MISMATCH, logFile, "egress Point " + epBuf.releasePointIdentifier +  " in facility " 
                            + epBuf.facilitySiteIdentifier + " exists only for US EPA--Identifier is marked ended.");
                }
            }
            UsEpaEgressPointBuffer.next();
            epBuf = UsEpaEgressPointBuffer.getBuf(this);
        }
    }

    private void processUsEpaEmissionUnitsFacility(Document facMismatchDom, UsEpaFacilityBuffer fBuf,
            Facility matchingFac,  EmissionsReport rpt) throws DAOException {
        // Go through all EUs the US EPA knows of and set appropriately.
        UsEpaEmissionUnitBuffer euBuf = UsEpaEmissionUnitBuffer.getBuf(this);  // get first/next record
        if(traceFiles) writeWarn(logFile, "Emission Unit buffer with1 " + euBuf.facilitySiteIdentifier + " " + euBuf.unitIdentifier);
        HashSet<String> euIds = new HashSet<String>();
        while(!UsEpaEmissionUnitBuffer.isEndOfFacility(this, fBuf.facilitySiteIdentifier)) {
            emissionUnitMismatchIdentifier = false;
            String currentEuName = processUsEpaEmissionUnit(facMismatchDom, euBuf, matchingFac);
            euIds.add(currentEuName); // keep track of what seen
            UsEpaEmissionUnitBuffer.next();
            euBuf = UsEpaEmissionUnitBuffer.getBuf(this);
            if(traceFiles) writeWarn(logFile, "Emission Unit buffer with2 " + euBuf.facilitySiteIdentifier + " " + euBuf.unitIdentifier);
        }
        if(rpt != null) {
            // Go through all EUs in the report and add any not already known.
            addMismatchRptEuInfo(matchingFac, rpt, euIds);
        }
    }

    private void addMismatchRptEuInfo(Facility matchingFac, EmissionsReport rpt, HashSet<String> euIds) {
        for(EmissionsReportEU reu : rpt.getEus()) {
            boolean includeEu = false;
            if( reu.isEg71OrZero()) continue;
            if(euIds != null && euIds.contains(reu.getEpaEmuId())) continue;
            if(reu.getEmissionProcesses() != null && reu.getPeriods().size() > 0) {
                for(EmissionsReportPeriod ep : reu.getPeriods()) {
                    if(ep.isZeroHours()) continue;
                    includeEu = true;
                    break;
                }
            }
            emissionUnitMismatchIdentifier = false;
            if(includeEu) {
                EmissionUnit eu =  matchingFac.getMatchingEmissionUnit(reu.getCorrEpaEmuId());
                if(eu == null) {
                    // We expected to find the EU since referenced by report.
                    writeError(BOTH, logFile, "did not find EU. Emissions inventory  " + rpt.getEmissionsRptId() 
                            + ", facility "+ matchingFac.getFacilityId() + " and EU CorrEpaEmuId " + reu.getCorrEpaEmuId());
                } else {
                    // Add EU and its processes
                    // Note that for EUs the US EPA already knew about, we added any missing processes.
                    boolean sdFlag = OperatingStatusDef.SD.equals(eu.getOperatingStatusCd());
                    if(eu.getEuShutdownDate() == null) sdFlag = false;  // if no date; treat as operating
                    createMismatchEmissionUnitXML(facilityMismatchDom, matchingFac, eu, matchingFac.getFacilityId(),
                            reu.getEpaEmuId(), true, sdFlag);
                    if(!emissionUnitMismatchIdentifier) {
                        Element id = facilityMismatchDom.createElement(UNIT_IDENTIFICATION);
                        emissionUnitMismatchElm.appendChild(id);
                        buildNewIdentifier(facilityMismatchDom, id, reu.getEpaEmuId(), true, false);
                        emissionUnitMismatchIdentifier = true;
                    }
                    for(EmissionsReportPeriod ep : reu.getPeriods()) {
                        if(ep.isZeroHours()) continue;
                        createMismatchProcess(facilityMismatchDom, rpt, eu, ep);
                    }
                }
            }
        }
    }

    private void createMismatchFacilityXML(Document facMismatchDom, Facility f, String facilityId) {
        if(facilityMismatchSiteElm != null) return;
        Element facSiteElm = facMismatchDom.createElement(FACILITY_SITE);
        // DENNIS   don't do this early...  facMismatchDom.appendChild(facSiteElm);
        addNewMismatchFacilityIdentification(facMismatchDom, facSiteElm, f, facilityId, false);
        facilityMismatchSiteElm = facSiteElm;
    }

    private void createNewMismatchFacilityXML(Document facMismatchDom, Facility f, Facility altF, String facilityId) {
        if(facilityMismatchSiteElm != null) return;
        writeError(MISMATCH, logFile, "Just information:  Facility " + f.getFacilityId() + " is new to the US EPA");
        facilityMismatchSiteElm = facMismatchDom.createElement(FACILITY_SITE);
        // DENNIS   don't do this early...  facMismatchDom.appendChild(facilityMismatchSiteElm);
        createFacilitySiteStatus(facMismatchDom, f);
        // NAICS -- for facility category only
        if(f.getNaicsCds() == null || f.getNaicsCds().size() == 0) {
            writeWarn(MISMATCH, logFile, "there is no NAICS code for facility " + f.getFacilityId() + "--skipped.");
        } else {
            for(String naicsStr : f.getNaicsCds()) {
                naics = facMismatchDom.createElement(FACILITY_NAICS);
                facilityMismatchSiteElm.appendChild(naics);
                Element naicsCd = facMismatchDom.createElement(NAICS_CODE);
                naics.appendChild(naicsCd);
                Text tx = facMismatchDom.createTextNode(naicsStr);
                naicsCd.appendChild(tx);
            }
        }
        addNewMismatchFacilityIdentification(facMismatchDom, facilityMismatchSiteElm, f, facilityId, true);
        generateSiteAddress(facMismatchDom, facilityMismatchSiteElm, f, altF);
    }

    private void createFacilitySiteStatus(Document facMismatchDom, Facility f) {
        Text tx = null;
        Integer yrTV = null;  // first year TV
        Integer yr1st = null; // first year any report required
        try {
            ArrayList<EmissionsRptInfo> lst = getEmissionsReportService().getYearlyReportingInfo(f.getFacilityId());
            for(int i = lst.size() - 1; i >= 0; i--) {
                EmissionsRptInfo eri = lst.get(i);
                if(yrTV == null && eri.getState() != null &&
                        ReportReceivedStatusDef.REVISION_REQUESTED.compareTo(eri.getState()) <= 0) {
                    // for this, just look at submitted (or better) reports because
                    // if the report was not submitted, we would not include for US EPA
                    yrTV = eri.getYear();
                }
                if(yr1st == null && eri.getState() != null &&
                        !ReportReceivedStatusDef.isNotNeededCode(eri.getState())) {
                    yr1st = eri.getYear();
                }
            }
        } catch (RemoteException re) {
            String s = "failed to getYearlyReportingInfo() for facility " + f.getFacilityId() +
            ".  the tags: " + FACILITY_CATEGORY_CODE + " and " + FACILITY_SITE_STATUS_CODE_YEAR +
            " are omitted.";
            writeError(MISMATCH, logFile, s, re);
        }
        // Only do it for the first year we provide the TV info
        if(yrTV != null && yrTV == year) {
            String category = "UNK";
            Element fCatElm = facMismatchDom.createElement(FACILITY_CATEGORY_CODE);
            facilityMismatchSiteElm.appendChild(fCatElm);
            tx = facMismatchDom.createTextNode(category);
            fCatElm.appendChild(tx);
        }

        // facility name
        Element fFacilityName = facMismatchDom.createElement(FACILITY_SITE_NAME);
        facilityMismatchSiteElm.appendChild(fFacilityName);
        // make facility name unique by adding facility id.
        tx = facMismatchDom.createTextNode(f.getName() + " (" + f.getFacilityId()
                + ")");
        fFacilityName.appendChild(tx);

        if(yr1st != null && (OperatingStatusDef.OP.equals(f.getOperatingStatusCd()) ||
                OperatingStatusDef.IA.equals(f.getOperatingStatusCd()))) {
            Element fFacOp = facMismatchDom.createElement(FACILITY_SITE_STATUS_CODE);
            facilityMismatchSiteElm.appendChild(fFacOp);
            // mark it operating, but only the first time.
            String op = getFpOperatingStatusCd(f.getOperatingStatusCd());
            if(op.length() == 0) {
                writeError(MISMATCH, logFile, "cannot translate operating status for facility " + f.getFacilityId());
            }
            tx = facMismatchDom.createTextNode(op);
            fFacOp.appendChild(tx);
            // Set first operation date
            Element fId = facMismatchDom.createElement(FACILITY_SITE_STATUS_CODE_YEAR);
            Calendar cal = Calendar.getInstance();
            cal.set(yr1st, 1, 1);
            Timestamp opD = new Timestamp(cal.getTimeInMillis());
            Text fTx = facMismatchDom.createTextNode(getYearStr(opD));
            fId.appendChild(fTx);
            facilityMismatchSiteElm.appendChild(fId);
        }

        // if shutdown, mark it as such every time--since we don't know when shutdown
        if(OperatingStatusDef.SD.equals(f.getOperatingStatusCd())) {
            Element fFacOp = facMismatchDom.createElement(FACILITY_SITE_STATUS_CODE);
            facilityMismatchSiteElm.appendChild(fFacOp);
            String op = getFpOperatingStatusCd(f.getOperatingStatusCd());
            if(op.length() == 0) {
                writeError(MISMATCH, logFile, "cannot translate operating status for facility " + f.getFacilityId());
            }
            Text fTx = facMismatchDom.createTextNode(op);
            fFacOp.appendChild(fTx);;
            // Set shutdown year
            Timestamp sdD = f.getShutdownDate();
            if(getYearStr(sdD).length() == 0) {
                writeWarn(MISMATCH, logFile, "missing shut down date for facility " 
                        + f.getFacilityId() + "--skipped.");
            } else {
                Element fId = facMismatchDom.createElement(FACILITY_SITE_STATUS_CODE_YEAR);
                fTx = facMismatchDom.createTextNode(getYearStr(sdD));
                fId.appendChild(fTx);
                facilityMismatchSiteElm.appendChild(fId);;
            }
        }
    }

    // return false if already shutdown.  In that case we don't want to generate anything.
    private boolean processUsEpaMismatchFacility(Document facMismatchDom, UsEpaFacilityBuffer fBuf, Facility matchingFac) {
        if(facilityMismatchSiteElm != null) {
            return true; // since we set this variable, we want to generate for this facility
        }
        facilityMismatchSiteElm = facMismatchDom.createElement(FACILITY_SITE);
        // facMismatchDom.appendChild(facilityMismatchSiteElm); <-- delay attaching facility XML
        // Is the operational state the same?
        boolean setState = false;
        boolean ignoreThisFacility = false;
        String y = null;
        String opStat = getFpOperatingStatusCd(matchingFac.getOperatingStatusCd());
        if(opStat.length() != 0) {  // ignore if status is not known
            if(matchingFac.getShutdownDate() != null) {
                y = getYearStr(matchingFac.getShutdownDate());
            }
            String translatedStat = fBuf.facilitySiteStatusCode;
            if("ONP".equals(translatedStat)) translatedStat = "OP";
            if(!translatedStat.equals(opStat)) {
                setState = true;
                if(opStat.equals("OP")) {
                    // Since EIS already has facility shutdown and Stars2 has it operating, we need manual intervention
                    ignoreThisFacility = true;
                    writeError(MISMATCH, logFile, "Manual Intervention needed for " 
                            + fBuf.facilitySiteIdentifier + " because EIS has facility state of " + fBuf.facilitySiteStatusCode +
                            " and Stars2 has facility state of " + opStat);
                }
            } else {
                // state has not changed
                if("PS".equals(opStat)) {
                    // if already shutdown, we are going to ignore the facility
                    ignoreThisFacility = true;
                }
                if("PS".equals(opStat) && y != null) {   //  <-- THIS CODE HAS NO EFFECT
                    if(!exists(fBuf.facilitySiteStatusCodeYear)) {
                        setState = true;
                    } else {
                        if(!y.equals(fBuf.facilitySiteStatusCodeYear)) {
                            setState = true;
                        }
                    }
                }
            }
        }
        if(setState) {
            createFacilitySiteStatus(facMismatchDom, matchingFac);
        }
        // NAICS -- this is a required field; supply it in case missing
        if(matchingFac.getNaicsCds() == null || matchingFac.getNaicsCds().size() == 0) {
            writeWarn(FACINVENT, logFile, "there is no NAICS code for facility " + matchingFac.getFacilityId() + "--skipped.");
        } else {
            for(String naicsStr : matchingFac.getNaicsCds()) {
                naics = facMismatchDom.createElement(FACILITY_NAICS);
                facilityMismatchSiteElm.appendChild(naics);
                Element naicsCd = facMismatchDom.createElement(NAICS_CODE);
                naics.appendChild(naicsCd);
                Text tx = facMismatchDom.createTextNode(naicsStr);
                naicsCd.appendChild(tx);
            }
        }
        addNewMismatchFacilityIdentification(facMismatchDom, facilityMismatchSiteElm, matchingFac, fBuf.facilitySiteIdentifier, false);
        if(!ignoreThisFacility) {
            if(!facilityAlreadyAdded) {
                facilityAlreadyAdded = true;
                facMismatchDom.appendChild(facilityMismatchSiteElm);
            }
        } else return false;
        return true;
    }

    private void createMismatchEmissionUnitXML(Document facMismatchDom, Facility fac, EmissionUnit euForType, String facilityId, String euId,
            boolean needToCreate, boolean markShutdown) {
        createMismatchFacilityXML(facMismatchDom, fac, facilityId);  // create if not already created.
        if(!euId.equals(emissionUnitMismatchId)) {
            emissionUnitMismatchId = euId;
            emissionUnitMismatchElm = facMismatchDom.createElement(EMISSIONS_UNIT);
            facilityMismatchSiteElm.appendChild(emissionUnitMismatchElm);
            Element id = null;
            if(needToCreate) {
                // note that if needToCreate is true, then euForType will be set.
                if(euForType != null) {
                    id = facMismatchDom.createElement(UNIT_TYPE_CODE);
                    emissionUnitMismatchElm.appendChild(id);
                    Text tx = facMismatchDom.createTextNode(getUnitTypeCode(euForType.getDesignCapacityCd()));
                    id.appendChild(tx);
                }
            }
            // set the state
            id = facMismatchDom.createElement(UNIT_STATUS_CODE);
            emissionUnitMismatchElm.appendChild(id);
            Text tx = facMismatchDom.createTextNode("OP");
            if(markShutdown) { // may be new and already shutdown
                tx = facMismatchDom.createTextNode("PS");
                id.appendChild(tx);
                // Set the status year
                id = facMismatchDom.createElement(UNIT_STATUS_CODE_YEAR);
                emissionUnitMismatchElm.appendChild(id);
                String sdYear = null;
                if(euForType != null && euForType.getEuShutdownDate() != null) {
                    sdYear = getYearStrPlus1(euForType.getEuShutdownDate());
                } else {
                    sdYear = Integer.toString(lastEmissionsYear + 1);
                }
                tx = facMismatchDom.createTextNode(sdYear);
                id.appendChild(tx);
            } else {
                id.appendChild(tx);
            }
            if(needToCreate) {
                // note that if needToCreate is true, then euForType will be set.
                Timestamp dt = euForType.getEuInitInstallDate();
                if(dt == null) dt = euForType.getEuStartupDate();
                if(dt == null) dt = euForType.getEuInstallDate();
                String opDate = getDateStr(dt);
                if(exists(opDate)) {
                    id = facMismatchDom.createElement(UNIT_OPERATION_DATE);
                    emissionUnitMismatchElm.appendChild(id);
                    tx = facMismatchDom.createTextNode(opDate);
                    id.appendChild(tx);
                } else {
                    writeWarn(MISMATCH, logFile, "missing operation date for facility " 
                            + facilityId + " and emission unit " + euForType.getEpaEmuId());
                }
            }  
        }
    }

    private String processUsEpaEmissionUnit(Document facMismatchDom, UsEpaEmissionUnitBuffer euBuf, Facility matchingFac) throws DAOException {
        // Is the operational state the same?
        String y = null; String newName = null;
        DbInteger[] corrIds = null;
        EmissionUnit eu = matchingFac.getEmissionUnit(euBuf.unitIdentifier);
        if(eu == null) {
            // did not find it based upon name;  lets see if the name changed
            try {
                corrIds = erBO.locateUsingOldEuName(matchingFac.getFacilityId(), matchingFac.getFpId(), euBuf.unitIdentifier);
            } catch(RemoteException re) {
                String s = "failed on locateUsingOldEuName() for facility " + matchingFac.getFacilityId() + ", fpId " + matchingFac.getFpId()
                + " old EU name " + euBuf.unitIdentifier;
                writeError(UsEpaEisReport.MISMATCH, logFile, s);
                throw new DAOException(s);
            }
            if(corrIds != null && corrIds.length > 0) {
                if(corrIds.length == 1) {
                    eu =  matchingFac.getMatchingEmissionUnit(corrIds[0].getInteger());
                    if(eu == null) {
                        writeWarn(UsEpaEisReport.logFile, "Did not locate new EU Name using correlation Id " + corrIds[0].getInteger() +
                                " (old name was "
                                + euBuf.unitIdentifier + ") in facility " + euBuf.facilitySiteIdentifier);
                    } else {
                        writeWarn(UsEpaEisReport.logFile, "Located new EU Name " + eu.getEpaEmuId() + " (old name was "
                                + euBuf.unitIdentifier + ") in facility " + euBuf.facilitySiteIdentifier);
                    }
                } else {
                    EmissionUnit best = null;
                    int numFound = 0;
                    for(DbInteger dbI : corrIds) {
                        eu =  matchingFac.getMatchingEmissionUnit(dbI.getInteger());
                        if(eu != null) {
                            best = eu;
                            numFound++;
                            writeWarn(UsEpaEisReport.logFile, "Located new EU Name " + eu.getEpaEmuId() + " (old name was "
                                    + euBuf.unitIdentifier + ") in facility " + euBuf.facilitySiteIdentifier + 
                                    ", using correlation Id " + dbI.getInteger());
                        } else {
                            writeWarn(UsEpaEisReport.logFile, "Did not locate new EU Name using correlation Id " + dbI.getInteger() +
                                    " (old name was "
                                    + euBuf.unitIdentifier + ") in facility " + euBuf.facilitySiteIdentifier + "--other correlation Ids to check");
                        }
                    }
                    if(numFound > 0) {
                        eu = best;
                        writeWarn(UsEpaEisReport.logFile, "Located new EU Name " + eu.getEpaEmuId() + " (old name was "
                                + euBuf.unitIdentifier + ") in facility " + euBuf.facilitySiteIdentifier + 
                                ", number of times an EU located was " + numFound + " out of " +corrIds.length);
                    }
                }
            }
            if(eu != null) {
                // found the renamed EU.
                newName = eu.getEpaEmuId();
            } else {
                newName = euBuf.unitIdentifier; // still old name  
            }
        } else {
            newName = euBuf.unitIdentifier; // name had not changed
        }
        if(eu == null || (exists(eu.getOperatingStatusCd()) && eu.getOperatingStatusCd().equals(OperatingStatusDef.IA))) {
            // emission unit is unknown.  Set the unit to shutdown with previous report year.
            if(!euBuf.emissionUnitStatusCode.equals("PS")) {
                if(corrIds == null || corrIds.length == 0) {
                    // not in any profiles so get rid of it.
                    createMismatchEmissionUnitXML(facMismatchDom, matchingFac, null, euBuf.facilitySiteIdentifier, euBuf.unitIdentifier, false, true);
                    if(!emissionUnitMismatchIdentifier) {
                        if(newName.equals(euBuf.unitIdentifier)) {
                            Element id = facMismatchDom.createElement(UNIT_IDENTIFICATION);
                            emissionUnitMismatchElm.appendChild(id);
                            buildNewIdentifier(facMismatchDom, id, euBuf.unitIdentifier, false, false);
                        } else {
                            Element id = facMismatchDom.createElement(UNIT_IDENTIFICATION);
                            emissionUnitMismatchElm.appendChild(id);
                            buildNewIdentifier(facMismatchDom, id, euBuf.unitIdentifier, false, true);
                            id = facMismatchDom.createElement(UNIT_IDENTIFICATION);
                            emissionUnitMismatchElm.appendChild(id);
                            buildNewIdentifier(facMismatchDom, id, newName, true, false);
                        }
                        writeWarn(MISMATCH, logFile, "Emission Unit " + euBuf.unitIdentifier +  " in facility " 
                                + euBuf.facilitySiteIdentifier + " exists only for US EPA--Identifier is marked shutdown.");
                        emissionUnitMismatchIdentifier = true;
                    }
                } else {
                    writeWarn(UsEpaEisReport.logFile, "Did not locate new EU Name but old name ("
                            + euBuf.unitIdentifier + ") in facility " + euBuf.facilitySiteIdentifier + 
                    " appears in some profiles so left unchanged with US EPA");
                }
            }
            // Ignore processes under this EU.
            /* UsEpaEmissionProcessBuffer pBuf = */ UsEpaEmissionProcessBuffer.getBuf(this);
            while(!UsEpaEmissionProcessBuffer.isEndOfEmissionUnit(this, euBuf.facilitySiteIdentifier, euBuf.unitIdentifier)) {
                // markMismatchProcessDead(facMismatchDom, pBuf);
                UsEpaEmissionProcessBuffer.next();
                /* pBuf = */ UsEpaEmissionProcessBuffer.getBuf(this);
            }
        } else { // both have the same EU
            String opStat = getEuOperatingStatusCd(eu.getOperatingStatusCd());
            Timestamp dt = null;
            dt = eu.getEuInitInstallDate();
            if(dt == null) dt = eu.getEuStartupDate();
            if(dt == null) dt = eu.getEuInstallDate();
            boolean shutdownFlag = false;
            if("PS".equals(opStat) && eu.getEuShutdownDate() != null) {
                y = getYearStrPlus1(eu.getEuShutdownDate());
            }
            if(exists(opStat)) {  // ignore if status is not known
                if("PS".equals(opStat) && eu.getEuShutdownDate() == null) {
                    opStat = "OP";  // treat it as operating
                    writeWarn(MISMATCH, logFile, "missing shutdown date for facility " 
                            + matchingFac.getFacilityId() + " and emission unit " + eu.getEpaEmuId() + "--treat as operating");
                }
                if("PS".equals(opStat)) {
                    shutdownFlag = true;
                    if(eu.getEuShutdownDate() != null) {
                        y = getYearStrPlus1(eu.getEuShutdownDate());
                    }
                }
                boolean createdEu = false;
                boolean existing = false;
                if(!euBuf.emissionUnitStatusCode.equals(opStat)) {
                    // set the state
                    createMismatchEmissionUnitXML(facMismatchDom, matchingFac, eu, euBuf.facilitySiteIdentifier, euBuf.unitIdentifier, false, shutdownFlag); // if not already created
                    createdEu = true;
                    
                } else if(shutdownFlag && (!exists(euBuf.emissionUnitStatusCodeYear) || !euBuf.emissionUnitStatusCodeYear.equals(y))) {
                    if(exists(y) && !exists(euBuf.emissionUnitStatusCodeYear)) {
                        // Set the status year
                        // But do not change shutdown status year if EIS already has some date.
                        createMismatchEmissionUnitXML(facMismatchDom, matchingFac, null, euBuf.facilitySiteIdentifier, euBuf.unitIdentifier, false, false); // if not already created
                        createdEu = true;
                        if(exists(y)) {
                            Element id = facMismatchDom.createElement(UNIT_STATUS_CODE_YEAR);
                            emissionUnitMismatchElm.appendChild(id);
                            Text tx = facMismatchDom.createTextNode(y);
                            id.appendChild(tx);
                        }
                    }
                }

                String opDate = getDateStr(dt);    // DDENNIS  get here for second use of B007
                if(!opDate.equals(euBuf.emissionUnitOperationDate)) {
                    createMismatchEmissionUnitXML(facMismatchDom, matchingFac, null, euBuf.facilitySiteIdentifier, euBuf.unitIdentifier, false, false); // if not already created
                    createdEu = true;
                    if(exists(opDate)) {
                        Element id = facMismatchDom.createElement(UNIT_OPERATION_DATE);
                        emissionUnitMismatchElm.appendChild(id);
                        Text tx = facMismatchDom.createTextNode(opDate);
                        id.appendChild(tx);
                    } else {
                        writeWarn(MISMATCH, logFile, "missing operation date for facility " 
                                + matchingFac.getFacilityId() + " and emission unit " + eu.getEpaEmuId());
                    }
                }
                if(existing) {
                    Element id = facMismatchDom.createElement(UNIT_IDENTIFICATION);
                    emissionUnitMismatchElm.appendChild(id);
                    buildNewIdentifier(facMismatchDom, id, euBuf.unitIdentifier, false, false);
                }
                if(createdEu) {
                    if(!emissionUnitMismatchIdentifier) {
                        if(newName.equals(euBuf.unitIdentifier)) {
                            Element id = facMismatchDom.createElement(UNIT_IDENTIFICATION);
                            emissionUnitMismatchElm.appendChild(id);
                            buildNewIdentifier(facMismatchDom, id, euBuf.unitIdentifier, false, false);
                        } else {
                            Element id = facMismatchDom.createElement(UNIT_IDENTIFICATION);
                            emissionUnitMismatchElm.appendChild(id);
                            buildNewIdentifier(facMismatchDom, id, euBuf.unitIdentifier, false, true);
                            id = facMismatchDom.createElement(UNIT_IDENTIFICATION);
                            emissionUnitMismatchElm.appendChild(id);
                            buildNewIdentifier(facMismatchDom, id, newName, true, false);
                        }
                        emissionUnitMismatchIdentifier = true;
                    }
                }
                // Check that emission unit has the processes known to US EPA
                // But if shutdown prior to this reporting year, ignore processes
                boolean skipOldEisInfo = false;
                String euOp = eu.getOperatingStatusCd();
                if(exists(euOp) && euOp.equals(OperatingStatusDef.SD)) {
                    if(eu.getEuShutdownDate() != null) {
                        y = getYearStr(eu.getEuShutdownDate());
                        int yr = Integer.parseInt(y);
                        // If shutdown prior to this reporting year, do not attempt to fix processes.
                        if(yr < year) skipOldEisInfo = true;
                    }
                }
                UsEpaEmissionProcessBuffer pBuf = UsEpaEmissionProcessBuffer.getBuf(this);
                while(!UsEpaEmissionProcessBuffer.isEndOfEmissionUnit(this, euBuf.facilitySiteIdentifier, euBuf.unitIdentifier)) {
                    if(!skipOldEisInfo) {
                        EmissionProcess ep = eu.findProcess(pBuf.EmissionsProcessIdentifier);
                        if(ep == null) {
                            //  Do not supply the process because the EIS want both the SCC code and Release Point Apportionment.
                            // markMismatchProcessDead(facMismatchDom, matchingFac, pBuf);
                        }
                    }
                    UsEpaEmissionProcessBuffer.next();
                    pBuf = UsEpaEmissionProcessBuffer.getBuf(this);
                }
            } else {
                // log that the EU has no state.
                writeWarn(UsEpaEisReport.MISMATCH, logFile, "emission Unit " + euBuf.unitIdentifier +  " in facility " 
                        + euBuf.facilitySiteIdentifier + " has no state set--ignored.");
                // ignore all the processes
                /* UsEpaEmissionProcessBuffer pBuf = */ UsEpaEmissionProcessBuffer.getBuf(this);
                while(!UsEpaEmissionProcessBuffer.isEndOfEmissionUnit(this, euBuf.facilitySiteIdentifier, euBuf.unitIdentifier)) {
                    UsEpaEmissionProcessBuffer.next();
                    /* pBuf = */ UsEpaEmissionProcessBuffer.getBuf(this);
                }
            }
        }
        return newName;
    }

//  private void markMismatchProcessDead(Document fMismatchDom, Facility matchingFac, UsEpaEmissionProcessBuffer pBuf) {
//    if(exists(pBuf.LastEmissionsYear)) {
//      return; // nothing to do.
//    }
//    createMismatchEmissionUnitXML(fMismatchDom, matchingFac, null, pBuf.facilitySiteIdentifier,
//          pBuf.unitIdentifier, false, false); // if not already created then already existed and not dead.
//    if(!emissionUnitMismatchIdentifier) {
//      Element id = fMismatchDom.createElement(UNIT_IDENTIFICATION);
//      emissionUnitMismatchElm.appendChild(id);
//      buildNewIdentifier(fMismatchDom, id, pBuf.unitIdentifier, false, false);
//      emissionUnitMismatchIdentifier = true;
//    }
//    Element fPId = fMismatchDom.createElement(UNIT_EMISSIONS_PROCESS);
//    emissionUnitMismatchElm.appendChild(fPId);
//
//    Element id;
//    Text tx;
////  id = fMismatchDom.createElement(SOURCE_CLASSIFICATION_CODE); // DENNIS:  Is it OK to leave out?
////  fPId.appendChild(id);
////  tx = fMismatchDom.createTextNode(pBuf.SourceClassificationCode);
////  id.appendChild(tx);
//
//    id = fMismatchDom.createElement(LAST_EMISSIONS_YEAR);
//    fPId.appendChild(id);
//    int lstYr = lastEmissionsYear;
//    try {
//      SccCode sccCode = null;
//      InfrastructureService dBO = infrastructureBO();
//      sccCode = dBO.retrieveSccCode(pBuf.SourceClassificationCode);
//      if(sccCode != null) {
//          Integer depYr = sccCode.getDeprecatedYear();
//          if(depYr != null) {
//              int depYrTemp = depYr - 1;
//              if(depYrTemp < lstYr) {
//                  lstYr = depYrTemp;
//              }
//          }
//      }
//    } catch(RemoteException re) {
//      writeError(BOTH, logFile, "Did not find SCC " + pBuf.SourceClassificationCode, re);
//    }
//    tx = fMismatchDom.createTextNode(Integer.toString(lstYr));
//    id.appendChild(tx);
//
//
//    Element fId = fMismatchDom.createElement(PROCESS_IDENTIFICATION);
//    fPId.appendChild(fId);
//	  buildNewIdentifier(fMismatchDom, fId, pBuf.EmissionsProcessIdentifier, false, false);
//  }   

    private void createMismatchProcess(Document fMismatchDom, EmissionsReport rpt, EmissionUnit eu, EmissionsReportPeriod p) {
        newRptEuProcesses.add(eu.getEpaEmuId() + "-" + p.getSccId());
        return;
        // This following code was to put new processes into mismatch XML.  However we
        // are no longer doing that because it also requires release point apportionment.

//      Element fPId = fMismatchDom.createElement(UNIT_EMISSIONS_PROCESS);
//      emissionUnitMismatchElm.appendChild(fPId);
//      Element id;
//      Text tx;

//      String fedSccId = p.getSccId();
//      String sccId = p.getSccId();

//      if(p.getSccCode() != null && p.getSccCode().getFedSccId() != null && p.getSccCode().getFedSccId().length() > 0) {
//      fedSccId = p.getSccCode().getFedSccId();
//      }
//      if(!exists(fedSccId)) {
//      writeError(BOTH, logFile, "scc value not set for a process in report " 
//      + rpt.getEmissionsRptId() + " and emission unit " + eu.getEpaEmuId() + SOURCE_CLASSIFICATION_CODE + "--skipped.");
//      } else {
//      id = fMismatchDom.createElement(SOURCE_CLASSIFICATION_CODE);
//      fPId.appendChild(id);
//      tx = fMismatchDom.createTextNode(fedSccId);
//      id.appendChild(tx);
//      }

//      id = fMismatchDom.createElement(LAST_EMISSIONS_YEAR);
//      fPId.appendChild(id);
//      tx = fMismatchDom.createTextNode(Integer.toString(year)); // current year
//      id.appendChild(tx);

//      Element fId = fMismatchDom.createElement(PROCESS_IDENTIFICATION);
//      fPId.appendChild(fId);
//      buildNewIdentifier(fMismatchDom, fId, sccId, true, false);
    }

    private boolean markMismatchEgressPointDead(Document fMismatchDom, Facility f, UsEpaEgressPointBuffer epBuf) {
        if("TS".equals(epBuf.releasePointStatusCode) || "PS".equals(epBuf.releasePointStatusCode)) {
            // already marked ended; do nothing
            return false;
        }
        createMismatchFacilityXML(fMismatchDom, f, epBuf.facilitySiteIdentifier); // if not already created then already existed and not dead.
        Element egId = fMismatchDom.createElement(RELEASE_POINT);
        facilityMismatchSiteElm.appendChild(egId);
        Element id = fMismatchDom.createElement(RELEASE_POINT_STATUS_CODE);
        egId.appendChild(id);
        Text tx = fMismatchDom.createTextNode("PS"); // must mark shutdown to give endDate
        id.appendChild(tx);
        id = fMismatchDom.createElement(RELEASE_POINT_STATUS_CODE_YEAR);
        egId.appendChild(id);
        tx = fMismatchDom.createTextNode(Integer.toString(year));
        id.appendChild(tx);

        id = fMismatchDom.createElement(RELEASE_POINT_IDENTIFICATION);
        egId.appendChild(id);
        buildNewIdentifier(fMismatchDom, id, epBuf.releasePointIdentifier, false, false);  // don't set the end date
        return true;
    }

    public int getLastEmissionsYear() {
        return lastEmissionsYear;
    }

    public void setLastEmissionsYear(int lastEmissionsYear) {
        this.lastEmissionsYear = lastEmissionsYear;
    }

    public boolean isIncludeProcessControls() {
        return includeProcessControls;
    }

    public void setIncludeProcessControls(boolean includeProcessControls) {
        this.includeProcessControls = includeProcessControls;
    }

    public boolean isIncludeWarns() {
        return includeWarns;
    }

    public void setIncludeWarns(boolean includeWarns) {
        this.includeWarns = includeWarns;
    }
    
    public String getNodeValue(String node) {
		
		String value = null;
		String jndiName = Config.findNode(node).getAsString("jndiName");
		try {
			// TODO potential optimization opportunity?
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			value = (String) envContext.lookup(jndiName);
		} catch (NamingException ne) {
			throw new RuntimeException(ne);
		}
		
		return value;
	}
    
    public String getEIXmlDocURL() {
    	return DocumentUtil.getFileStoreBaseURL() + File.separator + "EIS" + File.separator + pointXmlFileBase;
    }
    
    public String getFacilityXmlDocURL() {
    	return DocumentUtil.getFileStoreBaseURL() + File.separator	+ "EIS" + File.separator + facilityXmlFileBase;
      }
    
    public final DefSelectItems getEISSubmissionTypes() {
      	return EISSubmissionTypeDef.getData().getItems();
    }
    
    public String getEISSubmissionTypeDesc(String submissionType) {
    	return EISSubmissionTypeDef.getData().getItems().getItemDesc(submissionType);
    }
    
	public String getSubmissionType() {
		return submissionType;
	}

	public void setSubmissionType(String submissionType) {
		this.submissionType = submissionType;
	}

	public String getFacilityXmlFileName() {
		return facilityXmlFileName;
	}

	public void setFacilityXmlFileName(String facilityXmlFileName) {
		this.facilityXmlFileName = facilityXmlFileName;
	}

	public String getPointXmlFileName() {
		return pointXmlFileName;
	}

	public void setPointXmlFileName(String pointXmlFileName) {
		this.pointXmlFileName = pointXmlFileName;
	}

	public String getAqdContact() {
		return aqdContact;
	}

	public void setAqdContact(String aqdContact) {
		this.aqdContact = aqdContact;
	}

	public List<String> getFacilityClassList() {
		if(null == this.facilityClassList) {
			this.facilityClassList = new ArrayList<String>();
		}
		return facilityClassList;
	}

	public void setFacilityClassList(List<String> facilityClassList) {
		this.facilityClassList = facilityClassList;
	}

	public List<String> getFacilityTypeList() {
		if(null == this.facilityTypeList) {
			this.facilityTypeList = new ArrayList<String>();
		}
		return facilityTypeList;
	}

	public void setFacilityTypeList(List<String> facilityTypeList) {
		this.facilityTypeList = facilityTypeList;
	}
	
	public void reset() {
		this.year = Calendar.getInstance().get(Calendar.YEAR) - 1;
		this.lastEmissionsYear = year - 1;
		this.authorName = DEFAULT_AUTHOR_NAME;
		this.eisLogin = SystemPropertyDef.getSystemPropertyValue("EisLogin", null);
		this.aqdContact = SystemPropertyDef.getSystemPropertyValue("AqdContact", null);
		this.facilityClassList = new ArrayList<String>(Arrays.asList(
		PermitClassDef.NTV, PermitClassDef.TV, PermitClassDef.SMTV));
		this.facilityTypeList = new ArrayList<String>();
		this.submissionType = null;
		this.submittalComment = null;
	}
}

class UsEpaFacilityBuffer {
    String facilitySiteIdentifier;
    String facilitySiteStatusCode;
    String facilitySiteStatusCodeYear;

    static Scanner s;
    static Logger logger = Logger.getLogger(UsEpaFacilityBuffer.class);
    static UsEpaFacilityBuffer buf;
    static int rowCnt = 0;

    static boolean alreadyRead = false;
    static boolean endOfFile = false;
    static String prevFacilitySiteIdentifier = null;

    UsEpaFacilityBuffer(String f1, String f2, String f3) {
        facilitySiteIdentifier = f1;
        facilitySiteStatusCode = f2;
        facilitySiteStatusCodeYear = f3;
    }

    private static String trim(String s) {
        if(s == null) return s;
        return s.trim();
    }

    static private void reloadBuffer(UsEpaEisReport epaClass) throws DAOException {
        if(!alreadyRead && !endOfFile) {
            // read next record
            rowCnt += 1;
            int fieldNum = 0;
            try {
                if(buf != null) prevFacilitySiteIdentifier = buf.facilitySiteIdentifier;
                String fId = trim(s.next());
                fieldNum += 1;
//              if(fId.length() != 10) {
//              String s = "Facility Id (" + fId + ") not 10 characters in row " + rowCnt +
//              " in US EPA Facility File (value in previous row was " + prevFacilitySiteIdentifier;
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
//              ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              throw new DAOException(s);
//              }
                if(UsEpaEisReport.exists(prevFacilitySiteIdentifier) && 0 <= prevFacilitySiteIdentifier.compareTo(fId)) {
                    String s = "Facility Id (" + fId + ") not after previous Facility Id in row " + rowCnt +
                    " in US EPA Facility File (value in previous row was " + prevFacilitySiteIdentifier;
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
                    ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    throw new DAOException(s);
                }
                String stat = trim(s.next());
                fieldNum += 1;
                String yr = trim(s.next());
                fieldNum += 1;
                buf = new UsEpaFacilityBuffer(fId, stat, yr);
                alreadyRead = true;
                if(UsEpaEisReport.traceFiles) epaClass.writeWarn(UsEpaEisReport.logFile, "Read facility row " + rowCnt + ":" + fId);
            } catch (NoSuchElementException e) {
                endOfFile = true;
                if(fieldNum != 0) {
                    String s = "Partial last row (" + rowCnt + ") in US EPA Facility File";
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
                    ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    throw new DAOException(s);
                }
            }
        }
    }

    static void next() {
        alreadyRead = false;
    }

    static UsEpaFacilityBuffer getBuf(UsEpaEisReport epaClass) throws DAOException {
        reloadBuffer(epaClass);
        return buf;
    }

    static boolean hasNoReport(String eisFacId, UsEpaEisReport epaClass) throws DAOException  {
        reloadBuffer(epaClass);
        // true if usEpaFacility has smaller id than next one with a report
        // in this case check to see if STARS3 has the facility
        if(endOfFile) return true;
        return 0 > buf.facilitySiteIdentifier.compareTo(eisFacId);   
    }

    static boolean hasReport(String eisFacId, UsEpaEisReport epaClass) throws DAOException  {
        reloadBuffer(epaClass);
        if(endOfFile) return false;
        return 0 == buf.facilitySiteIdentifier.compareTo(eisFacId); 
    }

    static boolean newFacility(String eisFacId, UsEpaEisReport epaClass) throws DAOException  {
        reloadBuffer(epaClass);
        // true if usEpaFacility is larger than next one with report
        // in this case the facility is new to the US EPA
        // (note that if a new facility has no report then we do not inform
        // the US EPA about this new facility.
        if(endOfFile) return false;
        return 0 < buf.facilitySiteIdentifier.compareTo(eisFacId); 
    }

    static boolean isEndOfFile(UsEpaEisReport epaClass) throws DAOException  {
        reloadBuffer(epaClass);
        return endOfFile;
    }

    static void init(UploadedFile usEpaFacilities) throws IOException {
        alreadyRead = false;
        endOfFile = false;
        buf = null;
        prevFacilitySiteIdentifier = null;
        rowCnt = 0;
        try {
            s = new Scanner(usEpaFacilities.getInputStream()).useDelimiter(UsEpaEisReport.delimiterPattern);
        } catch (IOException e) {
            UsEpaFacilityBuffer.logger.error(e.getMessage(), e);
            throw e;
        }
    }
}

class UsEpaEmissionUnitBuffer {
    // File format is CSV:  FacilitySiteIdentifier,UnitIdentifier,UnitStatusCode,UnitStatusCodeYear,UnitOperationDate (yyyy-MM-dd)
    String facilitySiteIdentifier;
    String unitIdentifier;
    String emissionUnitStatusCode;
    String emissionUnitStatusCodeYear;
    String emissionUnitOperationDate;

    static Scanner s;
    static Logger logger = Logger.getLogger(UsEpaEmissionUnitBuffer.class);
    static UsEpaEmissionUnitBuffer buf;
    static int rowCnt = 0;

    static boolean alreadyRead = false;
    static boolean endOfFile = false;
    static String prevFacilitySiteIdentifier = null;
    static String prevEmissionUnitIdentifier = null;

    UsEpaEmissionUnitBuffer(String f1, String f2, String f3, String f4, String f5) {
        facilitySiteIdentifier = f1;
        unitIdentifier = f2;
        emissionUnitStatusCode = f3;
        emissionUnitStatusCodeYear = f4;
        emissionUnitOperationDate = f5;
    }

    private static String trim(String s) {
        if(s == null) return s;
        return s.trim();
    }

    static private void reloadBuffer(UsEpaEisReport epaClass) throws DAOException {
        if(!alreadyRead && !endOfFile) {
            // read next record
            rowCnt += 1;
            int fieldNum = 0;
            try {
                if(buf != null) {
                    prevFacilitySiteIdentifier = buf.facilitySiteIdentifier;
                    prevEmissionUnitIdentifier = buf.unitIdentifier;

                }
                String fId = trim(s.next());
                fieldNum += 1;
//              if(fId.length() != 10) {
//              String s = "Facility Id (" + fId + ") not 10 characters in row " + rowCnt +
//              " in US EPA Emission Unit File (value in previous row was " + prevFacilitySiteIdentifier + ")";
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
//              ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              throw new DAOException(s);
//              }
                if(UsEpaEisReport.exists(prevFacilitySiteIdentifier) && 0 < prevFacilitySiteIdentifier.compareTo(fId)) {
                    String s = "Facility Id (" + fId + ") not after previous Facility Id in row " + rowCnt +
                    " in US EPA Emission Unit File (value in previous row was " + prevFacilitySiteIdentifier + ")";
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
                    ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    throw new DAOException(s);
                }
                if(!fId.equals(prevFacilitySiteIdentifier)) {
                    prevEmissionUnitIdentifier = null;  // reset for next facility
                }
                String euId = trim(s.next());
                fieldNum += 1;
                if(UsEpaEisReport.exists(prevEmissionUnitIdentifier) && 0 <= prevEmissionUnitIdentifier.compareTo(euId)) {
                    String s = "Facility Id (" + fId + ") Emission Unit " + euId + " not after previous Emission Unit in row " + rowCnt +
                    " in US EPA Emission Unit File (value in previous row was " + prevFacilitySiteIdentifier + "," + prevEmissionUnitIdentifier + ")";
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
                    ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    throw new DAOException(s);
                }
                String stat = trim(s.next());
                fieldNum += 1;
                String yr = trim(s.next());
                fieldNum += 1;
                String opDate = trim(s.next());
                fieldNum += 1;
                buf = new UsEpaEmissionUnitBuffer(fId, euId, stat, yr, opDate);
                if(UsEpaEisReport.traceFiles) epaClass.writeWarn(UsEpaEisReport.logFile, "Read Emission Unit row " + rowCnt + ":" + fId + " " + euId);
                if(!UsEpaEisReport.exists(fId) || !UsEpaEisReport.exists(euId)) {
                    epaClass.writeWarn(UsEpaEisReport.logFile, "US EPA Emission Unit file row " + rowCnt + ":" + fId + " is missing identifiers--skipped");
                    reloadBuffer(epaClass);
                    return;
                }
                // Note that this test does not work because you eventually read beyond with the file and the
                // previous file has not been read yet.....
                // The problem will be found when the generation is complete because
                // there will be remaining records.
//              if(!fId.equals(UsEpaFacilityBuffer.buf.facilitySiteIdentifier)) {
//              String s = "Facility Id (" + fId + " Emission Unit " + euId +
//              ") in US EPA Emission Unit File is not supported by a corresponding record in the US EPA Facility file";
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
//              ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              throw new DAOException(s);
//              }
                alreadyRead = true;
            } catch (NoSuchElementException e) {
                endOfFile = true;
                if(fieldNum != 0) {
                    String s = "Partial last row (" + rowCnt + ") in US EPA Emission Unit File";
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    throw new DAOException(s);
                }
            }
        }
    }

    static void confirmEndOfFile(UsEpaEisReport epaClass) throws DAOException {
        // next();
        getBuf(epaClass);
        if(!isEndOfFile(epaClass)) {
            String s = "Remaining records starting in row " + rowCnt + " in US EPA Emission Unit File";
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
            ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            throw new DAOException(s);
        }
    }

    static void next() {
        alreadyRead = false;
    }

    static UsEpaEmissionUnitBuffer getBuf(UsEpaEisReport epaClass) throws DAOException {
        reloadBuffer(epaClass);
        return buf;
    }

    static boolean isEndOfFile(UsEpaEisReport epaClass) throws DAOException {
        reloadBuffer(epaClass);
        return endOfFile;
    }

    static boolean isEndOfFacility(UsEpaEisReport epaClass, String facId) throws DAOException {
        reloadBuffer(epaClass);
        if(endOfFile) {
            return true;
        }
        if(buf.facilitySiteIdentifier.compareTo(facId) < 0) {
            String s = "Facility Id from UsEpaEmissionUnitBuffer (" + buf.facilitySiteIdentifier + ") in row " + rowCnt +
            " should not be before facility Id which is the argument " + facId;
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
            ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            throw new DAOException(s);
        }
        return !alreadyRead || !buf.facilitySiteIdentifier.equals(facId); // Note that alreadyRead cannot be false--either a record was read or it is end of file.
    }

    static void init(UploadedFile usEpaFile) throws IOException {
        alreadyRead = false;
        endOfFile = false;
        buf = null;
        prevFacilitySiteIdentifier = null;
        prevEmissionUnitIdentifier = null;
        rowCnt = 0;
        try {
            s = new Scanner(usEpaFile.getInputStream()).useDelimiter(UsEpaEisReport.delimiterPattern);
        } catch (IOException e) {
            UsEpaEmissionUnitBuffer.logger.error(e.getMessage(), e);
            throw e;
        }
    }
}

class UsEpaEmissionProcessBuffer {
    // File format is CSV: FacilitySiteIdentifier,unitIdentifier,EmissionsProcessIdentifier,SourceClassificationCode,LastEmissionsYear
    String facilitySiteIdentifier;
    String unitIdentifier;
    String EmissionsProcessIdentifier;
    String SourceClassificationCode;
    String LastEmissionsYear;

    static Scanner s;
    static Logger logger = Logger.getLogger(UsEpaEmissionProcessBuffer.class);
    static UsEpaEmissionProcessBuffer buf;
    static int rowCnt = 0;

    static boolean alreadyRead = false;
    static boolean endOfFile = false;
    static String prevFacilitySiteIdentifier = null;
    static String prevEmissionUnitIdentifier = null;
    static String prevEmissionsProcessIdentifier = null;

    UsEpaEmissionProcessBuffer(String f1, String f2, String f3, String f4, String f5) {
        facilitySiteIdentifier = f1;
        unitIdentifier = f2;
        EmissionsProcessIdentifier = f3;
        SourceClassificationCode = f4;
        LastEmissionsYear = f5;
    }

    private static String trim(String s) {
        if(s == null) return s;
        return s.trim();
    }

    static boolean isEndOfEmissionUnit(UsEpaEisReport epaClass, String facId, String euId) throws DAOException {
        reloadBuffer(epaClass);
        if(endOfFile) {
            return true;
        }
        if(buf.facilitySiteIdentifier.compareTo(facId) < 0) {
            String s = "Facility Id from UsEpaEmissionProcessBuffer (" + buf.facilitySiteIdentifier + ") in row " + rowCnt +
            " should not be before facility Id which is the argument " + facId;
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
            ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            throw new DAOException(s);
        }
        if(buf.facilitySiteIdentifier.equals(facId)) {
            if(buf.unitIdentifier.compareTo(euId) < 0) {
                String s = "For facility " + facId + " Unit Id from UsEpaEmissionProcessBuffer (" + buf.unitIdentifier + ") in row " + rowCnt +
                " should not be before Unit Id which is the argument " + euId;
                epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
                ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
                epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                throw new DAOException(s);
            }
        }
        return alreadyRead && (!buf.facilitySiteIdentifier.equals(facId) || !buf.unitIdentifier.equals(euId));
    }

    static private void reloadBuffer(UsEpaEisReport epaClass) throws DAOException {
        if(!alreadyRead && !endOfFile) {
            // read next record
            rowCnt += 1;
            int fieldNum = 0;
            try {
                if(buf != null) {
                    prevFacilitySiteIdentifier = buf.facilitySiteIdentifier;
                    prevEmissionUnitIdentifier = buf.unitIdentifier;
                    prevEmissionsProcessIdentifier = buf.EmissionsProcessIdentifier;
                }
                String fId = trim(s.next());
                fieldNum += 1;
//              if(fId.length() != 10) {
//              String s = "Facility Id (" + fId + ") not 10 characters in row " + rowCnt +
//              " in US EPA Emission Process File (value in previous row was " + prevFacilitySiteIdentifier + "," + prevEmissionUnitIdentifier + "," + prevEmissionsProcessIdentifier + ")";
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
//              ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              throw new DAOException(s);
//              }
                if(UsEpaEisReport.exists(prevFacilitySiteIdentifier) && 0 < prevFacilitySiteIdentifier.compareTo(fId)) {
                    String s = "Facility Id (" + fId + ") not after previous Facility Id in row " + rowCnt +
                    " in US EPA Emission Processs File (value in previous row was " + prevFacilitySiteIdentifier + "," + prevEmissionUnitIdentifier + "," + prevEmissionsProcessIdentifier + ")";
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
                    ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    throw new DAOException(s);
                }
                if(!fId.equals(prevFacilitySiteIdentifier)) {
                    prevEmissionUnitIdentifier = null;  // reset for next facility
                }
                String euId = trim(s.next());
                fieldNum += 1;
                if(UsEpaEisReport.exists(prevEmissionUnitIdentifier) && 0 < prevEmissionUnitIdentifier.compareTo(euId)) {
                    String s = "Facility Id (" + fId + ") Emission Unit " + euId + " not after previous Emission Unit in row " + rowCnt +
                    " in US EPA Emission Unit File (value in previous row was " + prevFacilitySiteIdentifier + "," + prevEmissionUnitIdentifier + "," + prevEmissionsProcessIdentifier + ")";
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
                    ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    throw new DAOException(s);
                }
                String pId = trim(s.next());
                fieldNum += 1;
                String scc = trim(s.next());
                fieldNum += 1;
                String lastYear = trim(s.next());
                fieldNum += 1;
                buf = new UsEpaEmissionProcessBuffer(fId, euId, pId, scc, lastYear);
                if(UsEpaEisReport.traceFiles) epaClass.writeWarn(UsEpaEisReport.logFile, "Read Emission Process row " + rowCnt + ":" + fId + " " + euId + " " + pId);
                if(!UsEpaEisReport.exists(fId) || !UsEpaEisReport.exists(euId) || !UsEpaEisReport.exists(pId)) {
                    epaClass.writeWarn(UsEpaEisReport.logFile, "US EPA Emission Process file row " + rowCnt + ":" + fId + " is missing identifiers--skipped");
                    reloadBuffer(epaClass);
                    return;
                }
//              if(!euId.equals(UsEpaEmissionUnitBuffer.buf.unitIdentifier)) {
//              String s = "(Facility Id " + fId + " Emission Unit " + euId + " Emissions Process " + pId +
//              ") in US EPA Emissions Process File is not supported by a corresponding record in the US EPA Emissions Unit file";
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
//              ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              throw new DAOException(s);
//              }
                alreadyRead = true;
            } catch (NoSuchElementException e) {
                endOfFile = true;
                if(fieldNum != 0) {
                    String s = "Partial last row " + rowCnt + ") in US EPA Emission Process File";
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
                    ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    throw new DAOException(s);
                }
            }
        }
    }

    static void confirmEndOfFile(UsEpaEisReport epaClass) throws DAOException {
        // next();
        getBuf(epaClass);
        if(!isEndOfFile(epaClass)) {
            String s = "Remaining records starting in row " + rowCnt + " in US EPA Emission Process File";
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
            ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            throw new DAOException(s);
        }
    }

    static void next() {
        alreadyRead = false;
    }

    static UsEpaEmissionProcessBuffer getBuf(UsEpaEisReport epaClass) throws DAOException {
        reloadBuffer(epaClass);
        return buf;
    }

    static boolean isEndOfFile(UsEpaEisReport epaClass) throws DAOException {
        reloadBuffer(epaClass);
        return endOfFile;
    }

    static void init(UploadedFile usEpaFile) throws IOException {
        alreadyRead = false;
        endOfFile = false;
        buf = null;
        prevFacilitySiteIdentifier = null;
        prevEmissionUnitIdentifier = null;
        rowCnt = 0;
        try {
            s = new Scanner(usEpaFile.getInputStream()).useDelimiter(UsEpaEisReport.delimiterPattern);
        } catch (IOException e) {
            UsEpaEmissionProcessBuffer.logger.error(e.getMessage(), e);
            throw e;
        }
    }
}

class UsEpaEgressPointBuffer {
    // File format is CSV: FacilitySiteIdentifier,releasePointIdentifier,releasePointStatusCode,releasePointStatusCodeYear
    String facilitySiteIdentifier;
    String releasePointIdentifier;
    String releasePointStatusCode;
    String releasePointStatusCodeYear;

    static Scanner s;
    static Logger logger = Logger.getLogger(UsEpaEgressPointBuffer.class);
    static UsEpaEgressPointBuffer buf;
    static int rowCnt = 0;

    static boolean alreadyRead = false;
    static boolean endOfFile = false;
    static String prevFacilitySiteIdentifier = null;
    static String prevReleasePointIdentifier = null;

    UsEpaEgressPointBuffer(String f1, String f2, String f3, String f4) {
        facilitySiteIdentifier = f1;
        releasePointIdentifier = f2;
        releasePointStatusCode = f3;
        releasePointStatusCodeYear = f4;
    }

    private static String trim(String s) {
        if(s == null) return s;
        return s.trim();
    }

    static boolean isEndOfFacility(UsEpaEisReport epaClass, String facId) throws DAOException {
        reloadBuffer(epaClass);
        if(endOfFile) {
            return true;
        }
        if(buf.facilitySiteIdentifier.compareTo(facId) < 0) {
            String s = "Facility Id from UsEpaEgressPointBuffer (" + buf.facilitySiteIdentifier + ") in row " + rowCnt +
            " should not be before facility Id which is the argument " + facId;
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
            ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            throw new DAOException(s);
        }
        return !alreadyRead || !buf.facilitySiteIdentifier.equals(facId); // Note that alreadyRead cannot be false--either a record was read or it is end of file.
    }

    static private void reloadBuffer(UsEpaEisReport epaClass) throws DAOException {
        if(!alreadyRead && !endOfFile) {
            // read next record
            rowCnt += 1;
            int fieldNum = 0;
            try {
                if(buf != null) {
                    prevFacilitySiteIdentifier = buf.facilitySiteIdentifier;
                    prevReleasePointIdentifier = buf.releasePointIdentifier;
                }
                String fId = trim(s.next());
                fieldNum += 1;
//              if(fId.length() != 10) {
//              String s = "Facility Id (" + fId + ") not 10 characters in row " + rowCnt +
//              " in US EPA Egress Point File (value in previous row was " + prevFacilitySiteIdentifier + "," +  prevReleasePointIdentifier + ")";
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
//              ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              throw new DAOException(s);
//              }
                if(UsEpaEisReport.exists(prevFacilitySiteIdentifier) && 0 < prevFacilitySiteIdentifier.compareTo(fId)) {
                    String s = "Facility Id (" + fId + ") not after previous Facility Id in row " + rowCnt +
                    " in US EPA Egress Point File (value in previous row was " + prevFacilitySiteIdentifier + "," +  prevReleasePointIdentifier + ")";
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
                    ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
                    throw new DAOException(s);
                }

                String rpId = trim(s.next());
                fieldNum += 1;
                String opCode = trim(s.next());
                fieldNum += 1;
                String lastYear = trim(s.next());
                fieldNum += 1;
                buf = new UsEpaEgressPointBuffer(fId, rpId, opCode, lastYear);
                if(UsEpaEisReport.traceFiles) epaClass.writeWarn(UsEpaEisReport.logFile, "Read Egress Point row " + rowCnt + ":" + fId + " " + rpId);
                if(!UsEpaEisReport.exists(fId) || !UsEpaEisReport.exists(rpId)) {
                    epaClass.writeWarn(UsEpaEisReport.logFile, "US EPA Egress Point file row " + rowCnt + ":" + fId + " is missing identifiers--skipped");
                    reloadBuffer(epaClass);
                    return;
                }
//              if(!fId.equals(UsEpaFacilityBuffer.buf.facilitySiteIdentifier)) {
//              String s = "Facility Id (" + fId + " Egress Point " + rpId +
//              ") in US EPA Egress Point File is not supported by a corresponding record in the US EPA Facility file";
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
//              ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
//              epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
//              throw new DAOException(s);
//              }
                alreadyRead = true;
            } catch (NoSuchElementException e) {
                endOfFile = true;
                if(fieldNum != 0) {
                    epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, "Partial last row in US EPA Egress Point File");
                }
            }
        }
    }

    static void confirmEndOfFile(UsEpaEisReport epaClass) throws DAOException {
        // next();
        getBuf(epaClass);
        if(!isEndOfFile(epaClass)) {
            String s = "Remaining records starting in row " + rowCnt + " in US EPA Egress Point File";
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            s = "Row Positions: facility " + UsEpaFacilityBuffer.rowCnt + ", unit " + UsEpaEmissionUnitBuffer.rowCnt +
            ", process " + UsEpaEmissionProcessBuffer.rowCnt + ", release point " + UsEpaEgressPointBuffer.rowCnt;
            epaClass.writeError(UsEpaEisReport.MISMATCH, UsEpaEisReport.logFile, s);
            throw new DAOException(s);
        }
    }

    static void next() {
        alreadyRead = false;
    }

    static UsEpaEgressPointBuffer getBuf(UsEpaEisReport epaClass) throws DAOException {
        reloadBuffer(epaClass);
        return buf;
    }

    static boolean isEndOfFile(UsEpaEisReport epaClass) throws DAOException {
        reloadBuffer(epaClass);
        return endOfFile;
    }

    static void init(UploadedFile usEpaFile) throws IOException {
        alreadyRead = false;
        endOfFile = false;
        buf = null;
        prevFacilitySiteIdentifier = null;
        prevReleasePointIdentifier = null;
        rowCnt = 0;
        try {
            s = new Scanner(usEpaFile.getInputStream()).useDelimiter(UsEpaEisReport.delimiterPattern);
        } catch (IOException e) {
            UsEpaEgressPointBuffer.logger.error(e.getMessage(), e);
            throw e;
        }
    }
    
	
}