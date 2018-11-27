<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<af:panelGroup layout="vertical" partialTriggers="ExemptCheckbox">

  <af:panelHeader text="Alternate or Multiple Operating Scenario">
    <af:inputText id="altScenarioNmText" label="Scenario Name : "
      readOnly="#{! applicationDetail.editMode}"
      value="#{applicationDetail.selectedScenario.tvEuOperatingScenarioNm}" 
      maximumLength="128"/>
  </af:panelHeader>

  <af:panelForm>
    <%
                /**********************************************************************/
                /* Item 1: Maximum Allowable Operating Schedule                                  */
                /**********************************************************************/
    %>
    <af:showDetailHeader disclosed="true"
      text="Maximum Allowable Operating Schedule">
      <af:panelForm partialTriggers="OpSchedTSCheckBox">
        <af:outputText id="NormOpSchedOutText"
          value="Provide the maximum allowable operating schedule for this alternate or 
          multiple operating scenario."
          inlineStyle="font-size: 13px;" />
        <af:inputText id="opSchedHrsDayText" label="Hours/day :"
          rendered="#{!applicationDetail.selectedScenario.opSchedTradeSecret || applicationDetail.tradeSecretVisible}"
          readOnly="#{! applicationDetail.editMode}"
          value="#{applicationDetail.selectedScenario.opSchedHrsDay}"
          inlineStyle="#{applicationDetail.selectedScenario.opSchedTradeSecret ? 'color: #ff0000;' : ''}">
          <f:validateLongRange minimum="0" maximum="24"/>
        </af:inputText>
        <af:inputText id="opSchedHrsYrText" label="Hours/year :"
          rendered="#{!applicationDetail.selectedScenario.opSchedTradeSecret || applicationDetail.tradeSecretVisible}"
          readOnly="#{! applicationDetail.editMode}"
          value="#{applicationDetail.selectedScenario.opSchedHrsYr}"
          inlineStyle="#{applicationDetail.selectedScenario.opSchedTradeSecret ? 'color: #ff0000;' : ''}">
          <f:validateLongRange minimum="0" maximum="8760"/>
        </af:inputText>
        <af:selectOneRadio id="OpSchedTSCheckBox"
          label="Is Maximum Allowable Operating Schedule a Trade Secret? "
          value="#{applicationDetail.selectedScenario.opSchedTradeSecret}"
          readOnly="#{! applicationDetail.editMode}" layout="horizontal"
          autoSubmit="true">
          <f:selectItem itemLabel="Yes" itemValue="true" />
          <f:selectItem itemLabel="No" itemValue="false" />
        </af:selectOneRadio>
        <af:panelForm>
          <af:outputText
            rendered="#{applicationDetail.selectedScenario.opSchedTradeSecret}"
            value="Provide the reason the maximum allowable operating schedule for this 
            emissions unit is a trade secret:"
            inlineStyle="font-size: 13px; font-weight: bold;" />
          <af:inputText  id="ReasonOpSchedTSText"
            value="#{applicationDetail.selectedScenario.opSchedTradeSecretReason}"
            columns="120" rows="4"
            rendered="#{applicationDetail.selectedScenario.opSchedTradeSecret}"
            readOnly="#{! applicationDetail.editMode}"
            maximumLength="1000" />
        </af:panelForm>
      </af:panelForm>
    </af:showDetailHeader>

  <%
              /**************************************************************************/
              /* Item 2: PTE Tables                                                             */
              /**************************************************************************/
  %>
  <f:subview id="tvEuPTE">
    <jsp:include page="tvEuPTE.jsp" />
  </f:subview>

    <%
    /* Item 4 */
    %>
    <%
    /* TODO: Need operating schedule, PTE and limitations on source operations for each alt op scenario */
    %>
  </af:panelForm>

</af:panelGroup>
