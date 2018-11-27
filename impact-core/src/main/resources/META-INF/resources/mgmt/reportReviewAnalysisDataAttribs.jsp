<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:column sortProperty="emissionsRptId" sortable="true"
	headerText="Report" formatType="text">
	<af:commandLink action="#{reportDetail.submit}">
		<af:outputText value="#{reportResults.emissionsRptId}" />
		<t:updateActionListener property="#{reportDetail.reportId}"
			value="#{reportResults.emissionsRptId}" />
		<t:updateActionListener property="#{reportDetail.emissionsRptCd}"
			value="#{reportResults.emissionsReportingCategory}" />
		<t:updateActionListener property="#{reportDetail.fromTODOList}"
			value="false" />
		<t:updateActionListener property="#{menuItem_TVDetail.disabled}"
			value="false" />
	</af:commandLink>
</af:column>
<af:column sortProperty="facilityId" sortable="true"
	headerText="Facility Id" formatType="text" noWrap="true"
	rendered="#{reportSearch.fromFacility == 'false'}">
	<af:outputText value="#{reportResults.facilityId}" />
</af:column>
<af:column sortProperty="facilityName" sortable="true"
	headerText="Facility Name" formatType="text"
	rendered="#{reportSearch.fromFacility == 'false'}">
	<af:outputText value="#{reportResults.facilityName}" />
</af:column>
<af:column sortProperty="dolaaCd" sortable="true" formatType="text"
	headerText="District">
	<af:selectOneChoice id="doLaa" readOnly="true"
		value="#{reportResults.dolaaCd}">
		<f:selectItems value="#{reportSearch.doLaas}" />
	</af:selectOneChoice>
</af:column>
<af:column sortProperty="years" sortable="true" headerText="Year"
	formatType="text">
	<af:outputText value="#{reportResults.years}" />
</af:column>
<af:column sortProperty="emissionsReportingCategory" sortable="true"
	headerText="Category" formatType="icon">
	<af:selectOneChoice id="category" readOnly="true"
		value="#{reportResults.emissionsReportingCategory}">
		<f:selectItems value="#{reportSearch.reportingTypes}" />
	</af:selectOneChoice>
</af:column>
<af:column sortProperty="reportingStateDesc" sortable="true"
	headerText="Reporting State" formatType="icon">
	<af:outputText
		value="#{reportResults.reportingStateDesc}" />
</af:column>
<af:column sortProperty="rptReceivedStatusDate" sortable="true"
	headerText="Submitted Date" formatType="text">
	<af:selectInputDate label="Submitted Date" 
		readOnly="True" value="#{reportResults.rptReceivedStatusDate}" />
</af:column>
<af:column sortProperty="approvedDate" sortable="true"
	headerText="Approved/Completed Date" formatType="text">
	<af:selectInputDate label="Approved/Completed Date" 
		readOnly="True" value="#{reportResults.approvedDate}" />
</af:column>
<af:column sortProperty="daysToApprove" sortable="true"
	headerText="Days" formatType="number">
	<af:outputText value="#{reportResults.daysToApprove}" inlineStyle="#{reportResults.background}" />
</af:column>
