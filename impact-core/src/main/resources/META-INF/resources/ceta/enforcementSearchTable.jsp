<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
		
<af:table var="action" bandingInterval="1" banding="row" width="100%"
  value="#{enforcementSearch.enforcementActions}" rows="#{enforcementSearch.pageLimit}">
    
  <af:column rendered="true" formatType="text" headerText="Enforcement Action ID"
  	sortable="true" sortProperty="enfId" width="90px">
  	<af:commandLink rendered="true" text="#{action.enfId}"
      action="#{enforcementActionDetail.submit}">
      <af:setActionListener from="#{action.enforcementActionId}"
        to="#{enforcementActionDetail.enforcementActionId}" />   
      <af:setActionListener from="#{false}"
        to="#{enforcementActionDetail.newEnforcementAction}" />   
        <t:updateActionListener
          property="#{menuItem_enforcementActionDetail.disabled}" value="false" />   

    </af:commandLink>
  </af:column>
  <af:column rendered="#{!enforcementSearch.fromFacility}" formatType="text" 
  	headerText="Facility ID" sortable="true" sortProperty="facilityId" width="60px">
      <af:commandLink text="#{action.facilityId}"
        action="#{facilityProfile.submitProfileById}"
        inlineStyle="white-space: nowrap;">
        <t:updateActionListener property="#{facilityProfile.facilityId}"
          value="#{action.facilityId}" />
        <t:updateActionListener
          property="#{menuItem_facProfile.disabled}" value="false" />
      </af:commandLink>
  </af:column>

	<af:column rendered="#{!enforcementSearch.fromFacility}"
		sortable="true" sortProperty="facilityNm" formatType="text"
		headerText="Facility Name">
		<af:inputText readOnly="true" value="#{action.facilityNm}" />
	</af:column>

	<af:column sortable="true" sortProperty="districtCd" rendered="#{infraDefs.districtVisible}"
		formatType="text" headerText="District" noWrap="true">
		<af:selectOneChoice id="district" readOnly="true"
			value="#{action.districtCd}" unselectedLabel="">
			<f:selectItems value="#{facilitySearch.doLaas}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortable="true" sortProperty="countyNm"
		formatType="text" headerText="County">
		<af:selectOneChoice id="county" readOnly="true"
			value="#{action.countyCd}" unselectedLabel="">
			<f:selectItems value="#{infraDefs.counties}" />
		</af:selectOneChoice>
	</af:column>

	<af:column rendered="#{!enforcementSearch.fromFacility}" sortProperty="companyId" sortable="true" formatType="text"
		headerText="Company ID" width="80px">
		<af:commandLink action="#{companyProfile.submitProfile}"
			text="#{action.companyId}">
			<t:updateActionListener property="#{companyProfile.cmpId}"
				value="#{action.companyId}" />
			<t:updateActionListener
				property="#{menuItem_companyProfile.disabled}" value="false" />
		</af:commandLink>
	</af:column>
	<af:column rendered="#{!enforcementSearch.fromFacility}" sortProperty="companyName" sortable="true" formatType="text"
		headerText="Company Name" noWrap="true">
		<af:outputText value="#{action.companyName}" />
	</af:column>
	<af:column
		sortable="true" sortProperty="docketNumber" formatType="text"
		headerText="Docket Number">
		<af:inputText readOnly="true" value="#{action.docketNumber}" />
	</af:column>
	<af:column headerText="NOV Issued Date" sortable="true"
		sortProperty="dateNOVIssued" formatType="text" width="80px">
		<af:panelHorizontal valign="baseline" halign="left">
			<af:inputText readOnly="true" 
				value="#{action.dateNOVIssued}">
				<af:convertDateTime pattern="MM/dd/yyyy" />
			</af:inputText>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Day Zero Date" sortable="true"
		sortProperty="dateDayZero" formatType="text" width="75px">
		<af:panelHorizontal valign="baseline" halign="left">
			<af:inputText readOnly="true" 
				value="#{action.dateDayZero}">
				<af:convertDateTime pattern="MM/dd/yyyy" />
			</af:inputText>
		</af:panelHorizontal>
	</af:column>
	<af:column sortable="true" sortProperty="enforcementActionTypeDsc" formatType="text"
		headerText="Enforcement Action Type" width="100px">
		<af:selectOneChoice readOnly="true" unselectedLabel=" "
			value="#{action.enforcementActionType}">
			<f:selectItems
				value="#{enforcementSearch.enforcementActionTypeDef.items[(empty action.enforcementActionType ? 0 : action.enforcementActionType)]}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortable="true" sortProperty="enforcementActionHPVCriterion" formatType="text"
		headerText="HPV Criteria">
		<af:selectOneChoice readOnly="true" unselectedLabel=" "
			value="#{action.enforcementActionHPVCriterion}">
			<f:selectItems
				value="#{enforcementActionDetail.enforcementActionHPVCriterionDef.items[(empty action.enforcementActionHPVCriterion ? 0 : action.enforcementActionHPVCriterion)]}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortable="true" sortProperty="enforcementActionFRVType" formatType="text"
		headerText="FRV Type">
		<af:selectOneChoice readOnly="true" unselectedLabel=" "
			value="#{action.enforcementActionFRVType}">
			<f:selectItems
				value="#{enforcementActionDetail.enforcementActionFRVTypeDef.items[(empty action.enforcementActionFRVType ? 0 : action.enforcementActionFRVType)]}" />
		</af:selectOneChoice>
	</af:column>
	<af:column headerText="Referred to AG Date" sortable="true"
		sortProperty="dateReferredToAG" formatType="text" width="80px">
		<af:panelHorizontal valign="baseline" halign="left">
			<af:inputText readOnly="true" 
				value="#{action.dateReferredToAG}">
				<af:convertDateTime pattern="MM/dd/yyyy" /> 
			</af:inputText>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Final Settlement Agreement Date" sortable="true"
		sortProperty="dateFinalSettlementAgreement" formatType="text" width="120px">
		<af:panelHorizontal valign="baseline" halign="left">
			<af:inputText readOnly="true" 
				value="#{action.dateFinalSettlementAgreement}">
				<af:convertDateTime pattern="MM/dd/yyyy" />
			</af:inputText>
		</af:panelHorizontal>
	</af:column>
	<af:column sortable="true" sortProperty="sepFlag" formatType="text"
		headerText="SEP?">
		<af:selectOneChoice id="sepFlag" readOnly="true" unselectedLabel=" "
			value="#{action.sepFlag}">
			<f:selectItem itemLabel="Yes" itemValue="Y" />
			<f:selectItem itemLabel="No" itemValue="N" />
		</af:selectOneChoice>
	</af:column>
	<af:column headerText="Check Received Date" sortable="true"
		sortProperty="dateCheckReceived" formatType="text" width="100px">
		<af:panelHorizontal valign="baseline" halign="left">
			<af:inputText readOnly="true" 
				value="#{action.dateCheckReceived}">
				<af:convertDateTime pattern="MM/dd/yyyy" />
			</af:inputText>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="Penalty Amount" sortProperty="penaltyAmount"
		sortable="true" formatType="number" width="70px" id="penaltyAmount">
		<af:panelHorizontal valign="baseline" halign="right">
			<af:inputText readOnly="true"
				value="#{action.penaltyAmountString}">
			</af:inputText>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="SEP Offset Amount" sortProperty="sepOffsetAmount"
		sortable="true" formatType="number" width="75px" id="sepOffsetAmount">
		<af:panelHorizontal valign="baseline" halign="right">
			<af:inputText readOnly="true"
				value="#{action.sepOffsetAmountString}">
			</af:inputText>
		</af:panelHorizontal>
	</af:column>
	<af:column headerText="NOV Closed Date" sortable="true"
		sortProperty="dateNOVClosed" formatType="text" width="55px">
		<af:panelHorizontal valign="baseline" halign="left">
			<af:inputText readOnly="true" 
				value="#{action.dateNOVClosed}">
				<af:convertDateTime pattern="MM/dd/yyyy" />
			</af:inputText>
		</af:panelHorizontal>
	</af:column>
	

	<f:facet name="footer">
    <afh:rowLayout halign="center">
      <af:panelButtonBar>
        <af:commandButton text="Create Enforcement Action" id="createEnforcementAction"
          rendered="#{enforcementSearch.fromFacility}"
          disabled="#{!enforcementActionDetail.enforcementActionCreateAllowed}"
          action="#{enforcementActionDetail.startNewEnforcementAction}">
          <t:updateActionListener property="#{enforcementActionDetail.facilityId}"
          	value="#{facilityProfile.facilityId}"/>
		</af:commandButton>
        <af:commandButton actionListener="#{tableExporter.printTable}"
          onclick="#{tableExporter.onClickScript}" text="Printable view" />
        <af:commandButton actionListener="#{tableExporter.excelTable}"
          onclick="#{tableExporter.onClickScript}"
          text="Export to excel" />
      </af:panelButtonBar>
    </afh:rowLayout>
  </f:facet>
</af:table>

