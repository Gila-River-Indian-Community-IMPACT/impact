<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:column sortable="true" sortProperty="facilityId"
	headerText="Facility ID" rendered="#{!fceSiteVisits.fromFacility}">
	<af:commandLink text="#{fce.facilityId}"
		action="#{facilityProfile.submitProfileById}"
		inlineStyle="white-space: nowrap;">
		<t:updateActionListener property="#{facilityProfile.facilityId}"
			value="#{fce.facilityId}" />
		<t:updateActionListener property="#{menuItem_facProfile.disabled}"
			value="false" />
	</af:commandLink>
</af:column>
<af:column sortable="true" sortProperty="facilityAfsNumber"
	formatType="text" headerText="AFS ID">
	<af:inputText readOnly="true" value="#{fce.facilityAfsNumber}" />
</af:column>
<af:column rendered="#{!fceSiteVisits.fromFacility}" sortable="true"
	sortProperty="facilityNm" formatType="text" headerText="Facility Name">
	<af:inputText readOnly="true" value="#{fce.facilityNm}" />
</af:column>
<af:column sortProperty="permitClassCd" sortable="true" formatType="text"
	headerText="Facility Class" width="50px">
	<af:outputText
		value="#{facilityReference.permitClassDefs.itemDesc[(empty fce.permitClassCd ? '' : fce.permitClassCd)]}" />
</af:column>

<af:column sortProperty="facilityTypeCd" sortable="true" formatType="text"
	headerText="Facility Type">
	<af:outputText
		value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty fce.facilityTypeCd ? '' : fce.facilityTypeCd)]}" />
</af:column>
<af:column sortProperty="cmpId" sortable="true" formatType="text"
	headerText="Company ID" rendered="#{!fceSiteVisits.fromFacility}">
		<af:commandLink action="#{companyProfile.submitProfile}"
			text="#{fce.cmpId}">
			<t:updateActionListener property="#{companyProfile.cmpId}"
				value="#{fce.cmpId}" />
			<t:updateActionListener
				property="#{menuItem_companyProfile.disabled}" value="false" />
		</af:commandLink>
</af:column>
<af:column sortProperty="companyName" sortable="true" formatType="text"
	headerText="Company Name" rendered="#{!fceSiteVisits.fromFacility}" noWrap="true">
	<af:outputText value="#{fce.companyName}" />
</af:column>
<af:column sortProperty="operatingStatusCd" sortable="true"
	rendered="#{!fceSiteVisits.fromFacility}"
	formatType="text" headerText="Operating Status" width="65px">
	<af:outputText
		value="#{facilityReference.operatingStatusDefs.itemDesc[(empty fce.operatingStatusCd ? '' : fce.operatingStatusCd )]}"
		inlineStyle="#{(fce.operatingStatusCd == 'sd') ? 'color: orange; font-weight: bold;' : '' }" />
	<af:selectInputDate value="#{fce.lastShutdownDate}"
		inlineStyle="color: orange; font-weight: bold;"
		readOnly="true"
		rendered="#{fce.lastShutdownDate != null}">
	</af:selectInputDate>
</af:column>
