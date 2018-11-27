<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup layout="vertical">
  <af:panelHeader text="Emissions Unit">
    <af:panelForm labelWidth="44%" rows="1" maxColumns="2" fieldWidth="56%">
      <af:inputText label="EU ID :"
        value="#{permitDetail.selectedEU.fpEU.epaEmuId}" readOnly="true" />
      <af:inputText label="Company Equipment ID :"
        value="#{permitDetail.selectedEU.companyId}"
        readOnly="#{! permitDetail.editMode}" />
      <af:selectOneChoice label="EU Permit Status :"
        readOnly="#{! permitDetail.editMode || permitDetail.permit.finalIssuance.issuanceStatusCd != 'I'}"
        value="#{permitDetail.selectedEU.permitStatusCd}">
        <mu:selectItems value="#{permitReference.permitStatusDefs}" />
      </af:selectOneChoice>
      <af:inputText label="AQD description :"
        value="#{permitDetail.selectedEU.dapcDescription}"
        readOnly="#{! permitDetail.editMode}" />

      <af:selectOneChoice label="EU group :" unselectedLabel="No group"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEUEUGroup}"
        rendered="#{! permitDetail.selectedEU.euGroup.individualEUGroup}">
        <f:selectItems value="#{permitDetail.euGroupSelectItems}" />
      </af:selectOneChoice>
      <af:selectInputDate label="Install date :" readOnly="true"
        value="#{permitDetail.selectedEU.fpEU.euInstallDate}" />
      <af:selectInputDate label="Terminated date :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.terminatedDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
    </af:panelForm>

    <af:panelForm maxColumns="2" rows="1" labelWidth="44%" fieldWidth="56%"
      rendered="#{permitDetail.permit.generalPermit}">
      <af:selectOneChoice label="General permit type :"
        unselectedLabel="Not applicable" readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.generalPermitTypeCd}">
        <mu:selectItems value="#{permitReference.generalPermitTypeDefs}" />
      </af:selectOneChoice>
      <af:inputText label="Model General Permit No :" columns="20"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.selectedEU.modelGeneralPermitCd}" />
    </af:panelForm>

    <afh:tableLayout width="100%">
      <afh:rowLayout>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%">
            <af:selectOneChoice label="RPE Number :" autoSubmit="TRUE"
              readOnly="#{! permitDetail.editMode}" id="extended"
              unselectedLabel="Please select"
              value="#{permitDetail.selectedEU.extensionApplicationID}">
              <mu:selectItems value="#{permitDetail.rpeNumbers}" />
            </af:selectOneChoice>
          </af:panelForm>
        </afh:cellFormat>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%"
            partialTriggers="extended">
            <af:selectInputDate label="Extension date :" readOnly="true"
              rendered="#{!(permitDetail.selectedEU.extensionApplicationID == null)}"
              value="#{permitDetail.selectedEU.extensionDate}" />
          </af:panelForm>
        </afh:cellFormat>
      </afh:rowLayout>

      <afh:rowLayout>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%">
            <af:selectOneChoice label="RPR Number :" autoSubmit="TRUE"
              readOnly="#{! permitDetail.editMode}" id="revocation"
              unselectedLabel="Please select"
              value="#{permitDetail.selectedEU.revocationApplicationID}">
              <mu:selectItems value="#{permitDetail.rprNumbers}" />
            </af:selectOneChoice>
          </af:panelForm>
        </afh:cellFormat>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%"
            partialTriggers="revocation">
            <af:selectInputDate label="Rescission date :" readOnly="true"
              rendered="#{!(permitDetail.selectedEU.revocationApplicationID == null)}"
              value="#{permitDetail.selectedEU.revocationDate}" />
          </af:panelForm>
        </afh:cellFormat>
      </afh:rowLayout>

      <afh:rowLayout>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%">
            <af:selectOneChoice label="Superseded Permit :" autoSubmit="TRUE"
              readOnly="#{! permitDetail.editMode}" id="supersededPermit"
              unselectedLabel="Please select"
              value="#{permitDetail.selectedEU.supersededPermitID}">
              <mu:selectItems value="#{permitDetail.activePermits}" />
            </af:selectOneChoice>
          </af:panelForm>
        </afh:cellFormat>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%"
            partialTriggers="supersededPermit">
            <af:selectInputDate label="Superseded date :" readOnly="true"
              rendered="#{!(permitDetail.selectedEU.supersededPermitID == null)}"
              value="#{permitDetail.selectedEU.supersededDate}" />
          </af:panelForm>
        </afh:cellFormat>
      </afh:rowLayout>
    </afh:tableLayout>
  </af:panelHeader>
  <%
  /* Emissions Unit end */
  %>

  <af:panelHeader text="Fee [zero for TV NSR permits]" size="1">
    <af:panelForm labelWidth="44%" rows="1" maxColumns="2" fieldWidth="56%"
      partialTriggers="category adjust">
      <af:selectOneChoice label="Category :" id="category" autoSubmit="true"
        readOnly="#{! permitDetail.editMode}" unselectedLabel="Please select"
        value="#{permitDetail.selectedEU.euFee.feeCategoryCd}">
        <f:selectItem itemLabel="Fuel-burning Equipment - 0 to 9 mmBtu per hour"
          itemValue="200" />
        <f:selectItem
          itemLabel="Fuel-burning Equipment - 10 to 99 mmBtu per hour"
          itemValue="400" />
        <f:selectItem
          itemLabel="Fuel-burning Equipment - 100 to 299 mmBtu per hour"
          itemValue="1000" />
        <f:selectItem
          itemLabel="Fuel-burning Equipment - 300 to 499 mmBtu per hour"
          itemValue="2250" />
        <f:selectItem
          itemLabel="Fuel-burning Equipment - 500 to 999 mmBtu per hour"
          itemValue="3750" />
        <f:selectItem
          itemLabel="Fuel-burning Equipment - 1000 to 4999 mmBtu per hour"
          itemValue="6000" />
        <f:selectItem
          itemLabel="Fuel-burning Equipment - 5000 or more mmBtu per hour"
          itemValue="9000" />
        <f:selectItem itemLabel="Combustion Turbines - 0 to 9 megawatts"
          itemValue="25" />
        <f:selectItem itemLabel="Combustion Turbines - 10 to 24 megawatts"
          itemValue="150" />
        <f:selectItem itemLabel="Combustion Turbines - 25 to 49 megawatts"
          itemValue="300" />
        <f:selectItem itemLabel="Combustion Turbines - 50 to 99 megawatts"
          itemValue="500" />
        <f:selectItem itemLabel="Combustion Turbines - 100 to 250 megawatts"
          itemValue="1000" />
        <f:selectItem
          itemLabel="Combustion Turbines - Greater than 250 megawatts"
          itemValue="2000" />
      </af:selectOneChoice>
      <af:selectOneChoice label="Adjustment :" id="adjust" autoSubmit="true"
        readOnly="#{! permitDetail.editMode}" unselectedLabel="Please select"
        value="#{permitDetail.selectedEU.euFee.adjustmentCd}">
        <f:selectItem itemLabel="None" itemValue="N" />
        <f:selectItem itemLabel="Double" itemValue="D" />
        <f:selectItem itemLabel="Half" itemValue="H" />
        <f:selectItem itemLabel="No fee" itemValue="NF" />
        <f:selectItem itemLabel="Other" itemValue="O" />
      </af:selectOneChoice>
      <af:inputText label="Base Amount :" readOnly="true"
        value="#{permitDetail.selectedEU.euFee.feeCategoryCd}" />
      <af:inputText label="Adjusted Amount :"
        readOnly="true"
        columns="20" value="$ #{permitDetail.selectedEU.euFee.adjustedAmount}" />
    </af:panelForm>
  </af:panelHeader>
  <%
  /* Fee end */
  %>

  <af:panelHeader
    text="Best Available Technology (BAT) [NSR permit]"
    size="1">
    <af:panelForm labelWidth="44%" rows="1" maxColumns="2" fieldWidth="56%">
      <af:inputText label="Description :" readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.originalPermitNo}" columns="20" />
      <af:selectOneChoice label="Pollutant :"
        readOnly="#{! permitDetail.editMode}" unselectedLabel="Please select"
        value="">
        <mu:selectItems value="#{applicationReference.pollutantDefs}" />
      </af:selectOneChoice>
      <af:inputText label="Determination Costs :"
        readOnly="#{! permitDetail.editMode}" columns="20"
        value="#{permitDetail.permit.originalPermitNo}" />

      <af:inputText label="Air Quality Description :"
        readOnly="#{! permitDetail.editMode}" columns="20"
        value="#{permitDetail.permit.originalPermitNo}" />
      <af:selectOneChoice label="Rate Basis Code :"
        readOnly="#{! permitDetail.editMode}" unselectedLabel="Please select"
        value="">
        <f:selectItem itemLabel="Pounds/Hour" itemValue="N" />
        <f:selectItem itemLabel="Pounds/Day" itemValue="R" />
        <f:selectItem itemLabel="Pounds/Gallon" itemValue="I" />
        <f:selectItem itemLabel="Grains/DSCF" itemValue="S" />
      </af:selectOneChoice>
    </af:panelForm>
    <af:panelHeader text="Estimated Actual" size="2">
      <af:panelForm labelWidth="40%" rows="1" maxColumns="2" fieldWidth="60%">
        <af:inputText label="Short Term Rate :"
          readOnly="#{! permitDetail.editMode}" columns="20"
          value="#{permitDetail.permit.originalPermitNo}" />
        <af:inputText label="Tons/Yr Rate :"
          readOnly="#{! permitDetail.editMode}" columns="20"
          value="#{permitDetail.permit.originalPermitNo}" />
      </af:panelForm>
    </af:panelHeader>
    <af:panelHeader text="Permit Allowable" size="2">
      <af:panelForm labelWidth="40%" rows="1" maxColumns="2" fieldWidth="60%">
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
