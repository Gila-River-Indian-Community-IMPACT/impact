<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:column sortable="true" sortProperty="siteId" formatType="text"
	headerText="Site Visit ID">
	<af:commandLink text="#{sv.siteId}" action="#{siteVisitDetail.submitVisit}" rendered="#{!sv.stackTest}">
		<t:updateActionListener value="#{sv.id}"
			property="#{siteVisitDetail.visitId}" />
	</af:commandLink>
	<af:commandLink text="#{sv.siteId}" action="#{siteVisitDetail.submitStackTestVisitType}" rendered="#{sv.stackTest}">
			<t:updateActionListener value="#{sv.facilityId}"
				property="#{siteVisitDetail.facilityIdForStackTestVisitType}" />
			<t:updateActionListener value="#{sv.visitDate}"
				property="#{siteVisitDetail.visitDate}" />
			<t:updateActionListener value="#{sv}"
				property="#{siteVisitDetail.siteVisit}" />
			<t:updateActionListener value="#{sv.id}"
			property="#{siteVisitDetail.visitId}" />
	</af:commandLink>
</af:column>
	
<af:column rendered="#{!siteVisitSearch.fromFacility}" formatType="text"
	headerText="Facility ID" sortable="true" sortProperty="facilityId">
	<af:commandLink text="#{sv.facilityId}"
		action="#{facilityProfile.submitProfileById}"
		inlineStyle="white-space: nowrap;">
		<t:updateActionListener property="#{facilityProfile.facilityId}"
			value="#{sv.facilityId}" />
		<t:updateActionListener property="#{menuItem_facProfile.disabled}"
			value="false" />
	</af:commandLink>
</af:column>
<af:column rendered="#{!siteVisitSearch.fromFacility}" sortable="true" noWrap="true"
	sortProperty="facilityNm" formatType="text" headerText="Facility Name">
	<af:inputText readOnly="true" value="#{sv.facilityNm}" />
</af:column>
<af:column rendered="#{!siteVisitSearch.fromFacility}" sortProperty="permitClassCd" sortable="true"
		formatType="text" headerText="Facility Class">
		<af:outputText
			value="#{facilityReference.permitClassDefs.itemDesc[(empty sv.permitClassCd ? '' : sv.permitClassCd)]}" />
	</af:column>

<af:column rendered="#{!siteVisitSearch.fromFacility}" sortProperty="facilityTypeCd" sortable="true" noWrap="true"
		formatType="text" headerText="Facility Type">
		<af:outputText
			value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty sv.facilityTypeCd ? '' : sv.facilityTypeCd)]}" />
</af:column>
<af:column sortProperty="cmpId" sortable="true" formatType="text"
	headerText="Company ID">
	<af:commandLink action="#{companyProfile.submitProfile}"
		text="#{sv.cmpId}">
		<t:updateActionListener property="#{companyProfile.cmpId}"
			value="#{sv.cmpId}" />
		<t:updateActionListener
			property="#{menuItem_companyProfile.disabled}" value="false" />
	</af:commandLink>
</af:column>
<af:column sortProperty="companyName" sortable="true" formatType="text"
	headerText="Company Name" noWrap="true">
	<af:outputText value="#{sv.companyName}" />
</af:column>
<af:column sortProperty="operatingStatusCd" sortable="true"
	rendered="#{!siteVisitSearch.fromFacility}" formatType="text"
	headerText="Operating Status">
	<af:outputText
		value="#{facilityReference.operatingStatusDefs.itemDesc[(empty sv.operatingStatusCd ? '' : sv.operatingStatusCd )]}"
		inlineStyle="#{(sv.operatingStatusCd == 'sd') ? 'color: orange; font-weight: bold;' : '' }" />
	<af:selectInputDate value="#{sv.lastShutdownDate}"
		inlineStyle="color: orange; font-weight: bold;" readOnly="true"
		rendered="#{sv.lastShutdownDate != null}">
	</af:selectInputDate>
</af:column>