<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table width="98%" id="complianceReportsTable"
	value="#{complianceReportSearch.complianceReportList}"
	bandingInterval="1" banding="row" emptyText=" " var="report"
	rows="#{complianceReportSearch.pageLimit}">

	<af:column sortable="true" sortProperty="reportId" noWrap="true"
		formatType="text" headerText="Report ID"
		rendered="#{complianceReportSearch.internalApp}">
		<af:commandLink action="#{complianceReport.viewDetail}"
			text="#{report.reportCRPTId}">
			<t:updateActionListener property="#{complianceReport.reportId}"
				value="#{report.reportId}" />
			<t:updateActionListener
				property="#{menuItem_compReportDetail.disabled}" value="false" />
			<t:updateActionListener property="#{complianceReport.fromTODOList}"
				value="false" />
		</af:commandLink>
	</af:column>

	<af:column sortable="true" sortProperty="reportCRPTId" noWrap="true"
		formatType="text" headerText="Report ID"
		rendered="#{complianceReportSearch.portalApp}">
		<af:commandLink action="#{complianceReport.viewDetail}"
			text="#{report.reportCRPTId}">
			<t:updateActionListener
				property="#{complianceReport.readOnlyReportId}"
				value="#{report.reportId}" />
			<t:updateActionListener property="#{complianceReport.facilityId}"
				value="#{report.facilityId}" />
			<t:updateActionListener property="#{complianceReport.editable}"
				value="false" />
			<t:updateActionListener
				property="#{menuItem_compReportDetail.disabled}" value="false" />
		</af:commandLink>
	</af:column>

	<af:column sortable="true" sortProperty="reportCRPTId" noWrap="true"
		formatType="text" headerText="Report ID"
		rendered="#{complianceReportSearch.publicApp}">
		<af:commandLink action="#{complianceReport.viewDetail}"
			text="#{report.reportCRPTId}">
			<t:updateActionListener
				property="#{complianceReport.readOnlyReportId}"
				value="#{report.reportId}" />
			<t:updateActionListener property="#{complianceReport.facilityId}"
				value="#{report.facilityId}" />
			<t:updateActionListener property="#{complianceReport.editable}"
				value="false" />
		</af:commandLink>
	</af:column>

	<af:column headerText="Facility ID" sortable="true"
		sortProperty="facilityId" formatType="text"
		rendered="#{complianceReportSearch.internalApp && !complianceReportSearch.singleFacility}">
		<af:commandLink text="#{report.facilityId}"
			action="#{facilityProfile.submitProfileById}"
			inlineStyle="white-space: nowrap;">
			<t:updateActionListener property="#{facilityProfile.facilityId}"
				value="#{report.facilityId}" />
			<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{report.fpId}" />
			<t:updateActionListener property="#{facilityProfile.useFpId}"
				value="#{not empty report.fpId}" />
			<t:updateActionListener property="#{menuItem_facProfile.disabled}"
				value="false" />
		</af:commandLink>
	</af:column>

	<af:column headerText="Facility ID" sortable="true"
		sortProperty="facilityId" formatType="text"
		rendered="#{!complianceReportSearch.internalApp && !complianceReportSearch.singleFacility}">
		<af:panelHorizontal valign="middle" halign="center">
			<af:commandLink text="#{report.facilityId}"
				action="#{facilityProfile.submitProfileById}"
				inlineStyle="white-space: nowrap;">
				<t:updateActionListener property="#{facilityProfile.facilityId}"
					value="#{report.facilityId}" />
				<t:updateActionListener property="#{menuItem_facProfile.disabled}"
					value="false" />
			</af:commandLink>
		</af:panelHorizontal>
	</af:column>

	<af:column sortable="true" sortProperty="facilityAfsNumber"
		formatType="text" headerText="AFS ID">
		<af:inputText readOnly="true" value="#{report.facilityAfsNumber}" />
	</af:column>
	<af:column sortable="true" sortProperty="facilityName"
		rendered="#{!complianceReportSearch.singleFacility}" formatType="text"
		headerText="Facility Name" width="100px">
		<af:outputText value="#{report.facilityName}" />
	</af:column>

	<af:column sortProperty="permitClassCd" sortable="true"
		formatType="text" headerText="Facility Class" width="50px"
		rendered="#{complianceReportSearch.internalApp}">
		<af:outputText
			value="#{facilityReference.permitClassDefs.itemDesc[(empty report.permitClassCd ? '' : report.permitClassCd)]}" />
	</af:column>

	<af:column sortProperty="facilityTypeCd" sortable="true"
		formatType="text" headerText="Facility Type" noWrap="true"
		rendered="#{complianceReportSearch.internalApp}">
		<af:outputText
			value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty report.facilityTypeCd ? '' : report.facilityTypeCd)]}" />
	</af:column>

	<af:column sortProperty="cmpId" sortable="true" noWrap="true"
		rendered="#{!complianceReportSearch.singleFacility}" formatType="text"
		headerText="Company ID">
		<af:panelHorizontal valign="middle" halign="center">
			<af:commandLink action="#{companyProfile.submitProfile}"
				text="#{report.cmpId}" inlineStyle="white-space: nowrap;">
				<t:updateActionListener property="#{companyProfile.cmpId}"
					value="#{report.cmpId}" />
				<t:updateActionListener
					property="#{menuItem_companyProfile.disabled}" value="false" />
			</af:commandLink>
		</af:panelHorizontal>
	</af:column>

	<af:column sortProperty="companyName" sortable="true" noWrap="true"
		rendered="#{!complianceReportSearch.singleFacility}" formatType="text"
		headerText="Company Name">
		<af:outputText value="#{report.companyName}" />
	</af:column>

	<af:column sortProperty="doLaaCd" sortable="true" formatType="text"
		headerText="District" noWrap="true" rendered="#{infraDefs.districtVisible}">
		<af:selectOneChoice id="doLaa" readOnly="true"
			value="#{report.doLaaCd}">
			<f:selectItems value="#{reportSearch.doLaas}" />
		</af:selectOneChoice>
	</af:column>

	<af:column sortable="true" sortProperty="reportType" formatType="text"
		headerText="Report Type" noWrap="true">
		<af:selectOneChoice label="Report Type" readOnly="true"
			value="#{report.reportType}">
			<f:selectItems
				value="#{complianceReport.complianceReportTypesDef.items[(empty '')]}" />
		</af:selectOneChoice>
	</af:column>

	<af:column sortable="true" sortProperty="otherTypeDsc"
		formatType="text" headerText="Category">
		<af:outputText value="#{report.otherTypeDsc}" />
	</af:column>

	<af:column sortable="true"
		sortProperty="descriptionSearchDisplay" formatType="text"
		headerText="Description">
		<af:outputText truncateAt="80"
			value="#{report.descriptionSearchDisplay}"
			shortDesc="#{report.descriptionSearchDisplay}" />
	</af:column>

	<af:column sortable="true" sortProperty="receivedDate"
		formatType="text" headerText="Received Date">
		<af:panelHorizontal valign="baseline">
			<af:selectInputDate readOnly="true" value="#{report.receivedDate}" />
		</af:panelHorizontal>
	</af:column>

	<af:column sortable="true" sortProperty="reviewDate" formatType="text"
		headerText="Reviewed Date">
		<af:selectInputDate readOnly="true" value="#{report.reviewDate}" />
	</af:column>

	<af:column sortable="true" sortProperty="acceptable" formatType="text"
		headerText="Accepted" width="62px" noWrap="true">
		<af:selectOneChoice label="Accepted" readOnly="true"
			value="#{report.acceptable}">
			<f:selectItems
				value="#{complianceReport.complianceReportAcceptedDef.items[(empty '')]}" />
		</af:selectOneChoice>
	</af:column>

	<af:column sortable="true" sortProperty="reportStatus"
		formatType="text" headerText="Report Status"
		rendered="#{!complianceReportSearch.publicApp}" >
		<af:selectOneChoice readOnly="true" value="#{report.reportStatus}">
			<f:selectItems
				value="#{complianceReport.complianceReportStatusDef.items[(empty '')]}" />
		</af:selectOneChoice>
	</af:column>

	<af:column sortable="true"
		sortProperty="dapcReviewComments" formatType="text"
		headerText="Comments" rendered="#{complianceReportSearch.internalApp}">
		<af:outputText truncateAt="80" value="#{report.dapcReviewComments}"
			shortDesc="#{report.dapcReviewComments}" />
	</af:column>

	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="New Report" useWindow="true" id="newComplianceReport"
					rendered="#{complianceReportSearch.internalApp && complianceReportSearch.singleFacility}"
					disabled="#{complianceReportSearch.readOnlyUser}" windowWidth="600"
					windowHeight="215" action="#{complianceReport.startNewReport}" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>
