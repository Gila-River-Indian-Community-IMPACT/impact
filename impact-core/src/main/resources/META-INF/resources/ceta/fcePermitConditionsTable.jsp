<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:column headerText="Permit Condition ID" sortable="true"
	sortProperty="c02" formatType="text" noWrap="true">
	<af:commandLink useWindow="true"
		windowWidth="#{permitConditionDetail.permitConditionWindowWidth}"
		windowHeight="#{permitConditionDetail.permitConditionWindowHeight}"
		inlineStyle="padding-left:5px;" text="#{permitCondition.pcondId}"
		action="#{permitConditionDetail.showPermitConditionDetailFromSearch}">
		<t:updateActionListener
			property="#{permitConditionDetail.permitConditionId}"
			value="#{permitCondition.permitConditionId}" />
		<t:updateActionListener
			property="#{permitConditionDetail.fromFacilityPermitConditionSearch}"
			value="true" />
	</af:commandLink>
</af:column>

<af:column headerText="Associated Permit ID" sortable="true"
	sortProperty="c03" formatType="text" noWrap="false">
	<af:commandLink text="#{permitCondition.permitNbr}"
		action="#{permitDetail.loadPermitFromPermitConditionSearch}">
		<t:updateActionListener property="#{permitDetail.tempPermitId}"
			value="#{permitCondition.permitId}" />
		<t:updateActionListener property="#{permitDetail.fromTODOList}"
			value="false" />
	</af:commandLink>
</af:column>

<af:column headerText="Permit Type" sortable="true" sortProperty="c04"
	formatType="text">
	<af:selectOneChoice readOnly="true"
		value="#{permitCondition.permitTypeCd}">
		<f:selectItems value="#{permitSearch.permitTypes}" />
	</af:selectOneChoice>
</af:column>

<af:column headerText="Permit Status" sortable="true" sortProperty="c05"
	formatType="text">
	<af:outputText
		value="#{permitReference.permitLevelStatusDefs.itemDesc[
								(empty permitCondition.permitLevelStatusCd ? '' : permitCondition.permitLevelStatusCd)]}" />
</af:column>

<af:column headerText="Final Issuance Date" sortable="true"
	sortProperty="c06" formatType="text" noWrap="true">
	<af:selectInputDate readOnly="true"
		value="#{permitCondition.finalIssuanceDate}" />
</af:column>

<af:column headerText="Permit Basis Date" sortable="true"
	sortProperty="c07" formatType="text" noWrap="true">
	<af:selectInputDate readOnly="true"
		value="#{permitCondition.permitBasisDate}" />
</af:column>

<af:column headerText="Permit Condition Number" sortable="true"
	sortProperty="c08" formatType="text" noWrap="true">
	<af:outputText value="#{permitCondition.permitConditionNumber}" />
</af:column>

<af:column headerText="Condition Text" sortable="true"
	sortProperty="c09" width="300px" formatType="text" noWrap="false">
	<af:outputText value="#{permitCondition.conditionTextPlain}"
		truncateAt="200" shortDesc="#{permitCondition.conditionTextPlain}" />
</af:column>

<af:column headerText="Associated EUs" sortable="true"
	sortProperty="c10" formatType="text" noWrap="false">
	<af:outputText value="#{permitCondition.associatedEUsValue}" />
</af:column>

<af:column headerText="Category" sortable="true" sortProperty="c11"
	formatType="text" noWrap="false" headerNoWrap="true">
	<af:outputText value="#{permitCondition.categoryCdsDescription}" />
</af:column>

<af:column headerText="Permit Condition Status" sortable="true"
	sortProperty="c12" formatType="text">
	<af:commandLink useWindow="true" windowWidth="600" windowHeight="250"
		text="#{permitReference.permitConditionStatusDefs.itemDesc[
										(empty permitCondition.permitConditionStatusCd ? '' : permitCondition.permitConditionStatusCd)]}"
		action="#{permitConditionDetail.displayPermitConditionStatusFromSearch}">
		<t:updateActionListener
			property="#{permitConditionDetail.permitConditionSearchLineItem}"
			value="#{permitCondition}" />
		<t:updateActionListener
			property="#{permitConditionDetail.fromFacilityPermitConditionSearch}"
			value="true" />
		<t:updateActionListener
			property="#{permitConditionDetail.permitConditionId}"
			value="#{permitCondition.permitConditionId}" />
	</af:commandLink>
</af:column>

<af:column headerText="Compliance Status History" sortable="true"
	sortProperty="c13" formatType="text">
	<af:commandLink useWindow="true"
		windowWidth="#{permitConditionDetail.permitConditionWindowWidth}"
		windowHeight="#{permitConditionDetail.permitConditionWindowHeight}"
		inlineStyle="padding-left:5px;"
		text="Events(#{permitCondition.associatedComplianceStatusEventsCount})"
		action="#{permitConditionDetail.displayComplianceEventHistoryFromSearch}">
		<t:updateActionListener
			property="#{permitConditionDetail.permitConditionSearchLineItem}"
			value="#{permitCondition}" />
		<t:updateActionListener property="#{permitConditionDetail.facilityId}"
			value="#{facilityProfile.facilityId}" />
		<t:updateActionListener
			property="#{permitConditionDetail.fromFacilityPermitConditionSearch}"
			value="true" />
	</af:commandLink>
</af:column>