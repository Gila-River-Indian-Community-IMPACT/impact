<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup layout="vertical">
  <af:panelHeader text="Emissions Unit">
    <af:panelForm labelWidth="44%" rows="1" maxColumns="2"
      fieldWidth="56%">
      <af:inputText label="EU ID :"
        value="#{permitDetail.selectedEU.fpEU.epaEmuId}" readOnly="true" />
      <af:inputText label="Company Equipment ID :" maximumLength="60"
        value="#{permitDetail.selectedEU.companyId}"
        readOnly="#{! permitDetail.editMode}" />
      <af:selectOneChoice label="EU Permit Status :"
        readOnly="#{! permitDetail.editMode || permitDetail.permit.finalIssuance.issuanceStatusCd != 'I'}"
        value="#{permitDetail.selectedEU.permitStatusCd}">
        <mu:selectItems value="#{permitReference.permitStatusDefs}" />
      </af:selectOneChoice>
      <af:selectOneChoice label="EU Group :" unselectedLabel="No group"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEUEUGroup}"
        rendered="#{! permitDetail.selectedEU.euGroup.individualEUGroup}">
        <f:selectItems value="#{permitDetail.euGroupSelectItems}" />
      </af:selectOneChoice>

      <af:selectInputDate label="Install Date :" readOnly="true"
        value="#{permitDetail.selectedEU.fpEU.euInstallDate}" />
      <af:selectInputDate label="Terminated Date :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.terminatedDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:selectInputDate label="Rescission Date :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.revocationDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
    </af:panelForm>

    <af:panelForm maxColumns="1" rows="1" labelWidth="21%"
      fieldWidth="79%">
      <af:inputText label="AQD Description :" columns="70" rows="2" maximumLength="1000"
        inlineStyle="#{!permitDetail.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
        value="#{permitDetail.selectedEU.dapcDescription}"
        readOnly="#{! permitDetail.editMode}" />
    </af:panelForm>

<%--     <af:panelForm maxColumns="1" rows="1" labelWidth="20%"
      partialTriggers="gpt" fieldWidth="80%"
      rendered="#{permitDetail.permit.generalPermit}">
      <af:selectOneChoice label="General Permit Category :" id="gpt"
        unselectedLabel="Not applicable" autoSubmit="true"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.generalPermitTypeCd}">
        <mu:selectItems value="#{permitReference.generalPermitTypeDefs}" />
      </af:selectOneChoice>
      <af:selectOneChoice label="General Permit Type :"
        unselectedLabel="Not applicable"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.modelGeneralPermitCd}">
        <mu:selectItems value="#{permitDetail.modelGeneralPermitDefs}" />
      </af:selectOneChoice>
    </af:panelForm> --%>

    <afh:tableLayout width="100%">

      <afh:rowLayout>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%">
            <af:commandLink action="#{permitDetail.loadPermit}"
              disabled="#{permitDetail.selectedEU.supersededPermitID == null || 
              	permitDetail.selectedEU.supersededPermitID == 1 || permitDetail.editMode}">
              <af:selectOneChoice label="Superseded Permit :"
                readOnly="#{! permitDetail.editMode}"
                unselectedLabel=""
                value="#{permitDetail.selectedEU.supersededPermitID}">
                <mu:selectItems
                  value="#{permitDetail.supersedablePermits}" />
              </af:selectOneChoice>
              <af:setActionListener to="#{permitDetail.permitID}"
                from="#{permitDetail.selectedEU.supersededPermitID}" />
            </af:commandLink>
          </af:panelForm>
        </afh:cellFormat>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%">
            <af:selectInputDate label="Superseded Date :"
              readOnly="true"
              rendered="#{!(permitDetail.selectedEU.supersededDate == null)}"
              value="#{permitDetail.selectedEU.supersededDate}" />
          </af:panelForm>
        </afh:cellFormat>
      </afh:rowLayout>
      
      <afh:rowLayout
        rendered="#{!(permitDetail.selectedEU.extensionApplicationID == null)}">
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%">
            <af:selectOneChoice label="Extension Number :"
              readOnly="true" id="extended" unselectedLabel="None"
              value="#{permitDetail.selectedEU.extensionApplicationID}">
              <mu:selectItems value="#{permitDetail.rpeNumbers}" />
            </af:selectOneChoice>
          </af:panelForm>
        </afh:cellFormat>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%">
            <af:selectInputDate label="Extension Date :" readOnly="true"
              value="#{permitDetail.selectedEU.extensionDate}" />
          </af:panelForm>
        </afh:cellFormat>
      </afh:rowLayout>

      <afh:rowLayout
        rendered="#{!(permitDetail.selectedEU.revocationApplicationID == null)}">
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%">
            <af:selectOneChoice label="Rescission Number :"
              readOnly="true" id="revocation" unselectedLabel="None"
              value="#{permitDetail.selectedEU.revocationApplicationID}">
              <mu:selectItems value="#{permitDetail.rprNumbers}" />
            </af:selectOneChoice>
          </af:panelForm>
        </afh:cellFormat>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%">
            <af:selectInputDate label="Rescission Date :"
              readOnly="true"
              value="#{permitDetail.selectedEU.revocationDate}" />
          </af:panelForm>
        </afh:cellFormat>
      </afh:rowLayout>

    </afh:tableLayout>
  </af:panelHeader>
  <%
  /* Emissions Unit end */
  %>

  <%-- not valid for WY <af:panelHeader text="Fee" size="1">
    <af:panelForm labelWidth="44%" rows="1" maxColumns="2"
      fieldWidth="56%"
      partialTriggers="category adjust adjustAmount fee">
      <af:selectOneChoice label="Category :" id="category"
        autoSubmit="true" readOnly="#{! permitDetail.editMode}"
        unselectedLabel="None"
        value="#{permitDetail.selectedEU.euFee.feeCategoryId}">
        <f:selectItems value="#{permitReference.euCategories}" />
      </af:selectOneChoice>
      <af:inputText label="Fee Name :" readOnly="true"
        rendered="#{permitDetail.selectedEU.euFee.feeCategoryId == null}"
        value="None">
      </af:inputText>
      <af:selectOneChoice label="Fee Name :" id="fee" autoSubmit="true"
        readOnly="#{! permitDetail.editMode}" 
        rendered="#{permitDetail.selectedEU.euFee.feeCategoryId != null}"
        value="#{permitDetail.selectedEU.euFee.feeId}">
        <f:selectItems value="#{permitDetail.euCategorieFees}" />
      </af:selectOneChoice>
      <af:selectOneChoice label="Adjustment :" id="adjust"
        autoSubmit="true" readOnly="#{! permitDetail.editMode}"
        disabled="#{permitDetail.selectedEU.euFee.feeCategoryId == null}"
        value="#{permitDetail.selectedEU.euFee.adjustmentCd}">
        <f:selectItems
          value="#{permitReference.permitFeeAdjustmentDefs}" />
      </af:selectOneChoice>
      <af:inputText label="Base Amount :" readOnly="true"
        value="#{permitDetail.selectedEU.euFee.fee.amount}">
        <af:convertNumber type='currency' locale="en-US"
          minFractionDigits="2" />
      </af:inputText>
      <af:inputText label="Adjustment Amount :"
        readOnly="#{! permitDetail.editMode}"
        rendered="#{permitDetail.selectedEU.euFee.adjustmentCd == 'O' && !permitDetail.editMode}"
        value="#{permitDetail.selectedEU.euFee.adjustmentAmount}">
        <af:convertNumber type='currency' locale="en-US"
          maxFractionDigits="2" minFractionDigits="2" />
      </af:inputText>
      <af:inputText label="Adjustment Amount :" id="adjustAmount"
        readOnly="#{! permitDetail.editMode}" autoSubmit="true" maximumLength="50"
        rendered="#{permitDetail.selectedEU.euFee.adjustmentCd == 'O' && permitDetail.editMode}"
        value="#{permitDetail.selectedEU.euFee.adjustmentAmount}">
      </af:inputText>
      <af:inputText label="Adjusted Amount :" readOnly="true"
        columns="20"
        value="#{permitDetail.selectedEU.euFee.adjustedAmount}">
        <af:convertNumber type='currency' locale="en-US"
          minFractionDigits="2" />
      </af:inputText>
    </af:panelForm>
  </af:panelHeader>
  <%
  /* Fee end */
  %> --%>

  <af:panelHeader rendered="false"
    text="Best Available Technology (BAT) [NSR permit]"
    size="1">
    <af:panelForm maxColumns="1" rows="1" labelWidth="20%"
      fieldWidth="80%">
      <af:selectOneChoice label="Pollutant :"
        readOnly="#{! permitDetail.editMode}"
        unselectedLabel="Please select" value="">
        <mu:selectItems value="#{applicationReference.pollutantDefs}" />
      </af:selectOneChoice>
    </af:panelForm>

    <af:panelForm labelWidth="44%" rows="1" maxColumns="2"
      fieldWidth="56%">
      <af:inputText label="Description :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.originalPermitNo}" columns="20" />
      <af:inputText label="Determination Costs :"
        readOnly="#{! permitDetail.editMode}" columns="20"
        value="#{permitDetail.permit.originalPermitNo}" />

      <af:inputText label="Air Quality Description :"
        readOnly="#{! permitDetail.editMode}" columns="20"
        value="#{permitDetail.permit.originalPermitNo}" />
      <af:selectOneChoice label="Rate Basis Code :"
        readOnly="#{! permitDetail.editMode}"
        unselectedLabel="Please select" value="">
        <f:selectItem itemLabel="Pounds/Hour" itemValue="N" />
        <f:selectItem itemLabel="Pounds/Day" itemValue="R" />
        <f:selectItem itemLabel="Pounds/Gallon" itemValue="I" />
        <f:selectItem itemLabel="Grains/DSCF" itemValue="S" />
      </af:selectOneChoice>
    </af:panelForm>
    <af:panelHeader text="Estimated Actual" size="2">
      <af:panelForm labelWidth="40%" rows="1" maxColumns="2"
        fieldWidth="60%">
        <af:inputText label="Short Term Rate :"
          readOnly="#{! permitDetail.editMode}" columns="20"
          value="#{permitDetail.permit.originalPermitNo}" />
        <af:inputText label="Tons/Yr Rate :"
          readOnly="#{! permitDetail.editMode}" columns="20"
          value="#{permitDetail.permit.originalPermitNo}" />
      </af:panelForm>
    </af:panelHeader>
    <af:panelHeader text="Permit Allowable" size="2">
      <af:panelForm labelWidth="40%" rows="1" maxColumns="2"
        fieldWidth="60%">
        <af:inputText label="Short Term Rate :" columns="20"
          readOnly="#{! permitDetail.editMode}"
          value="#{permitDetail.permit.originalPermitNo}" />
        <af:inputText label="Tons/Yr Rate :" columns="20"
          readOnly="#{! permitDetail.editMode}"
          value="#{permitDetail.permit.originalPermitNo}" />
      </af:panelForm>
    </af:panelHeader>
  </af:panelHeader>
  <%
  /* Best Available Technology end */
  %>

</af:panelGroup>
