<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document id="body" title="Monthly Complaint Totals">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:messages />
			<af:panelForm>

				<af:selectOneChoice id="doLaaChoice" label="DO/LAA :" required="true"
					value="#{complaintDetail.complaint.doLaaCd}"
					readOnly="true">
					<f:selectItems value="#{facilitySearch.doLaas}" />
				</af:selectOneChoice>

				<af:selectOneChoice id="yearChoice" label="Year :" required="true"
					value="#{complaintDetail.complaint.year}"
					readOnly="#{complaintDetail.complaint.complaintId != null}">
					<f:selectItems
						value="#{complianceReportSearch.reportingYearsDef.items[(empty complaintDetail.complaint.year ? '' : complaintDetail.complaint.year)]}" />
				</af:selectOneChoice>
				<af:selectOneChoice id="monthChoice" label="Month :" required="true"
					value="#{complaintDetail.complaint.month}"
					readOnly="#{complaintDetail.complaint.complaintId != null}">
					<f:selectItems
						value="#{compEvalDefs.monthDef.items[(empty complaintDetail.complaint.month ? '' : complaintDetail.complaint.month)]}" />
				</af:selectOneChoice>

				<af:inputText id="highPriorityText" label="High Priority Complaints: "
					readOnly="#{!complaintDetail.editMode}"
					value="#{complaintDetail.complaint.highPriority}" />

				<af:inputText id="nonHighPriorityText" label="Non-High Priority Complaints: "
					readOnly="#{!complaintDetail.editMode}"
					value="#{complaintDetail.complaint.nonHighPriority}" />

				<af:inputText id="otherText" label="Other Complaints: "
					readOnly="#{!complaintDetail.editMode}"
					value="#{complaintDetail.complaint.other}" />

				<af:inputText id="openBurningText" label="Open Burning Complaints: "
					readOnly="#{!complaintDetail.editMode}"
					value="#{complaintDetail.complaint.openBurning}" />

				<af:inputText id="antiTamperingInspectionsText" label="Anti-Tampering Inspections: "
					readOnly="#{!complaintDetail.editMode}"
					value="#{complaintDetail.complaint.antiTamperingInspections}" />
					
				<af:inputText id="asbestos" label="Asbestos: "
					readOnly="#{!complaintDetail.editMode}"
					value="#{complaintDetail.complaint.asbestos}" />
					
				<af:inputText id="asbestosNonNotifier" label="asbestosNonNotifier: "
					readOnly="#{!complaintDetail.editMode}"
					value="#{complaintDetail.complaint.asbestosNonNotifier}" />					

				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Edit" rendered="#{!complaintDetail.editMode}"
							actionListener="#{complaintDetail.enterEditComplaint}" />
						<af:commandButton text="Save" rendered="#{complaintDetail.editMode}"
							actionListener="#{complaintDetail.applyEditComplaint}" />
						<af:commandButton text="#{complaintDetail.editMode?'Cancel':'Close'}" immediate="true"
							actionListener="#{complaintDetail.cancelEditComplaint}" />
						<af:objectSpacer width="20" />
						<af:commandButton text="Delete" rendered="#{!complaintDetail.editMode && complaintDetail.complaint.complaintId != null}"
							actionListener="#{complaintDetail.applyRemoveComplaint}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
