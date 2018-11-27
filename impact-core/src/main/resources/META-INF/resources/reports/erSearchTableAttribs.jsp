<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:column sortProperty="emissionsInventoryId" sortable="true"
	rendered="#{reportSearch.internalApp || reportSearch.portalApp}"
	headerText="Inventory ID" formatType="text">
	<af:commandLink action="#{reportDetail.submit}">
		<af:outputText value="#{reportResults.emissionsInventoryId}" />
		<t:updateActionListener property="#{reportDetail.reportId}"
			value="#{reportResults.emissionsRptId}" />
		<t:updateActionListener property="#{reportDetail.fromTODOList}"
			value="false" />
		<t:updateActionListener property="#{menuItem_TVDetail.disabled}"
			value="false" />
	</af:commandLink>
</af:column>

<af:column sortProperty="emissionsInventoryId" sortable="true"
	rendered="#{reportSearch.publicApp}"
	headerText="Inventory ID" formatType="text">
	<af:commandLink action="#{reportDetail.submit}">
		<af:outputText value="#{reportResults.emissionsInventoryId}" />
		<t:updateActionListener property="#{reportDetail.reportId}"
			value="#{reportResults.emissionsRptId}" />
		<t:updateActionListener property="#{reportDetail.fromTODOList}"
			value="false" />
	</af:commandLink>
</af:column>


<af:column sortProperty="emissionsInventoryModifiedId" sortable="true"
	rendered="#{!reportSearch.publicApp}"
	headerText="Previous Inventory" formatType="text" width="60px">
	<af:commandLink action="#{reportDetail.submit}">
		<af:outputText value="#{reportResults.emissionsInventoryModifiedId}" />
		<t:updateActionListener property="#{reportDetail.reportId}"
			value="#{reportResults.reportModified}" />
		<t:updateActionListener property="#{reportDetail.fromTODOList}"
			value="false" />
		<t:updateActionListener property="#{menuItem_TVDetail.disabled}"
			value="false" />
	</af:commandLink>
</af:column>
<af:column sortProperty="fpId" sortable="true" headerText="Facility History ID"
	formatType="text" noWrap="true" width="65px">
	<af:commandLink action="#{facilityProfile.submitProfile}"
		rendered="#{reportResults.versionId == -1}">
		<af:outputText value="Current (#{reportResults.fpId})" />
		<t:updateActionListener property="#{facilityProfile.fpId}"
			value="#{reportResults.fpId}" />
	</af:commandLink>
	<af:commandLink action="#{facilityProfile.submitProfile}"
		rendered="#{reportResults.versionId != -1}">
		<af:outputText value="#{reportResults.fpId}" />
		<t:updateActionListener property="#{facilityProfile.fpId}"
			value="#{reportResults.fpId}" />
	</af:commandLink>
</af:column>
<af:column sortProperty="facilityId" sortable="true"
	headerText="Facility ID" formatType="text" noWrap="true"
	rendered="#{reportSearch.fromFacility == 'false'}">
	<af:commandLink action="#{facilityProfile.submitProfileById}"
              text="#{reportResults.facilityId}">
              <t:updateActionListener property="#{facilityProfile.facilityId}"
                value="#{reportResults.facilityId}" />
              <t:updateActionListener
                property="#{menuItem_facProfile.disabled}" value="false" />
    </af:commandLink>
</af:column>
<af:column sortProperty="facilityName" sortable="true"
	headerText="Facility Name" formatType="text"
	rendered="#{reportSearch.fromFacility == 'false'}">
	<af:outputText value="#{reportResults.facilityName}" />
</af:column>
<af:column sortProperty="cmpId" sortable="true" formatType="text"
	headerText="Company ID"
	rendered="#{reportSearch.fromFacility == 'false'}">
	<af:commandLink action="#{companyProfile.submitProfile}"
		text="#{reportResults.cmpId}">
		<t:updateActionListener property="#{companyProfile.cmpId}"
			value="#{reportResults.cmpId}" />
		<t:updateActionListener property="#{menuItem_companyProfile.disabled}"
			value="false" />
	</af:commandLink>
</af:column>
<af:column sortProperty="companyName" sortable="true" formatType="text"
	headerText="Company Name" noWrap="true"
	rendered="#{reportSearch.fromFacility == 'false'}">
	<af:outputText value="#{reportResults.companyName}" />
</af:column>
<af:column sortProperty="dolaaCd" sortable="true" formatType="text"
	headerText="District" noWrap="true" rendered="#{infraDefs.districtVisible}">
	<af:selectOneChoice id="doLaa" readOnly="true" 
		value="#{reportResults.dolaaCd}">
		<f:selectItems value="#{reportSearch.doLaas}" />
	</af:selectOneChoice>
</af:column>
<af:column sortProperty="years" sortable="true" headerText="Year"
	formatType="text">
	<af:outputText value="#{reportResults.years}" />
</af:column>
<af:column sortProperty="contentTypeCd" sortable="true"
	headerText="Content Type" formatType="text">
	<af:selectOneChoice id="contentType" readOnly="true"
		value="#{reportResults.contentTypeCd}">
		<f:selectItems
			value="#{facilityReference.contentTypeDefs.allSearchItems}" />
	</af:selectOneChoice>
</af:column>
<af:column sortProperty="regulatoryRequirementCdsString" sortable="true"
	headerText="Regulatory Requirements" formatType="text">
	<af:outputText
		value="#{reportResults.regulatoryRequirementCdsString}" />
</af:column>
<af:column sortProperty="reportingState" sortable="true"
	headerText="Reporting State" formatType="text">
	<af:outputText
		value="#{reportResults.reportingStateDesc}" />
</af:column>
<%--
<af:column sortProperty="eisStatusCd" sortable="true"
	headerText="EIS State" formatType="icon">
	<af:outputText
		 value="#{reportResults.eisStatusDesc}"/>
</af:column>
--%>
<af:column sortProperty="receivedDate" sortable="true"
	headerText="Received Date" formatType="text" width="60px">
	<af:selectInputDate label="Received Date" 
		readOnly="True" value="#{reportResults.receivedDate}" />
</af:column>
<af:column sortProperty="submitter" sortable="true"
	rendered="#{!reportSearch.publicApp}"
	headerText="Submitter" formatType="text" >
		<af:outputText value="#{reportResults.submitter}" />
</af:column>
<%--
	<af:column sortProperty="emissionRange" sortable="true"
		headerText="Range" formatType="text">
		<af:outputText value="#{reportResults.emissionRange}" />
	</af:column>
	<af:column sortProperty="totalEmissionsDigits" sortable="true"
		headerText="Chargeable" formatType="number">
		<af:outputText value="#{reportResults.totalEmissionsDigits}" />
	</af:column>
--%>
	<af:column sortProperty="totalReportedEmissions" sortable="true"
		rendered="#{!reportSearch.publicApp}"
		headerText="Total Raw Chargeable Emissions Reported (Tons)" formatType="number" width="75px">
		<af:outputText value="#{reportResults.totalReportedEmissions}" >
		
		<af:convertNumber type='number' locale="en-US"
														
		
		
		 />
		</af:outputText>							
	  													
	</af:column>
