<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="monthlyTimesheetReport"
		title="Monthly Timesheet Report">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<af:form>
			<af:panelForm rows="1" maxColumns="2" labelWidth="50">
				<af:selectOneChoice id="month" 
					label="Month: " value="#{timesheetEntry.reportMonth}"
					showRequired="true">
					<f:selectItems value="#{timesheetEntry.monthDefs}"/>
				</af:selectOneChoice>
				<af:selectOneChoice id="year" 
					label="Year: " value="#{timesheetEntry.reportYear}"
					showRequired="true">
					<f:selectItems value="#{infraDefs.years}"/>
				</af:selectOneChoice>
			</af:panelForm>
			<af:objectSpacer height="10" />
			<af:panelForm rows="1" maxColumns="2" labelWidth="50">
				<af:selectOneChoice id="reportTypeCd" 
					label="Report Type: " value="#{timesheetEntry.reportTypeCd}"
					showRequired="true">
					<f:selectItems value="#{timesheetEntry.monthlyReportTypeDefs}"/>
				</af:selectOneChoice>
			</af:panelForm>
			
			<af:objectSpacer height="20" />
				
			<afh:rowLayout halign="center">
				<af:panelButtonBar>
					<af:commandButton text="Generate Report"
						action="#{timesheetEntry.generateMonthlyReport}" 
						returnListener="#{timesheetEntry.closeDownloadDialog}"/>
					<af:commandButton text="Cancel"
						action="#{timesheetEntry.cancelMonthlyReportGeneration}"/>
				</af:panelButtonBar>
			</afh:rowLayout>
				 
		</af:form>
	</af:document>
</f:view>