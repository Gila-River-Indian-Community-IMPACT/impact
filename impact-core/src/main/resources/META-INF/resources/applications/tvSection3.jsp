<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup>

  <%
              // ***************************************************************************
              // Section 3
              // ***************************************************************************
  %>
  <af:showDetailHeader disclosed="true"
    text="#{applicationDetail.applicableRequirementsHeader}"
    rendered="#{applicationDetail.showApplicableRequirements }">
    <af:outputText value="State and Federally Enforceable Requirements "
      inlineStyle="font-size: 13px;font-weight:bold" />
    <af:outputText
      value=""
      inlineStyle="font-size:75%;color:#666" />
    <%
    //<t:div style="overflow:auto;width:1040px;height:150px;">
    %>
    <af:table id="applicableReqsTable" width="98%" emptyText=" "
      var="tvApplicableReq" bandingInterval="1" banding="row"
      binding="#{applicationDetail.applicableReqTable}"
      value="#{applicationDetail.applicableRequirements}"
      rows="0">
      <f:facet name="selection">
        <af:tableSelectMany id="appReqTableSelection"
          rendered="#{applicationDetail.editMode}" autoSubmit="true" />
      </f:facet>
      <af:column headerText="Allowable Limit"
        id="appReqAllowableLimitCol">
        <af:inputText id="appReqAllowableLimitText" readOnly="true"
          value="#{tvApplicableReq.allowableValue}" />
      </af:column>
      <af:column headerText="Pollutant" id="appReqPollutantCol">
        <af:selectOneChoice id="appReqPollutantChoice" readOnly="true"
          value="#{tvApplicableReq.pollutantCd}">
          <f:selectItems value="#{applicationReference.pollutantDefs}" />
        </af:selectOneChoice>
      </af:column>
      <af:column headerText="Rule Cite" id="appReqAllowableRuleCiteCol">
        <af:inputText id="appReqAllowableText" readOnly="true"
          value="#{tvApplicableReq.allowableRuleCiteDsc}" />
      </af:column>
      <af:column headerText="Permit Cite"
        id="appReqAllowablePermitCiteCol">
        <af:inputText id="appReqAllowablePermitCiteText" readOnly="true"
          value="#{tvApplicableReq.allowablePermitCite}" />
      </af:column>
      <af:column headerText="Monitoring">
        <af:column headerText="Requirement" id="appReqMonitoringReqCol">
          <af:inputText id="appReqMonitoringReqText" readOnly="true"
            value="#{tvApplicableReq.monitoringValue}" />
        </af:column>
        <af:column headerText="Rule Cite"
          id="appReqMonitoringRuleCiteCol">
          <af:inputText id="appReqAllowableRuleCiteText" readOnly="true"
            value="#{tvApplicableReq.monitoringRuleCiteDsc}" />
        </af:column>
        <af:column headerText="Permit Cite"
          id="appReqMonitoringPermitCiteCol">
          <af:inputText id="appReqAllowablePermitCiteText"
            readOnly="true"
            value="#{tvApplicableReq.monitoringPermitCite}" />
        </af:column>
      </af:column>
      <af:column headerText="Record Keeping">
        <af:column headerText="Requirement"
          id="appReqRecordKeepingReqCol">
          <af:inputText id="appReqRecordKeepingReqText" readOnly="true"
            value="#{tvApplicableReq.recordKeepingValue}" />
        </af:column>
        <af:column headerText="Rule Cite"
          id="appReqRecordKeepingRuleCiteCol">
          <af:inputText id="appReqAllowableRuleCiteText" readOnly="true"
            value="#{tvApplicableReq.recordKeepingRuleCiteDsc}" />
        </af:column>
        <af:column headerText="Permit Cite"
          id="appReqRecordKeepingPermitCiteCol">
          <af:inputText id="appReqAllowablePermitCiteText"
            readOnly="true"
            value="#{tvApplicableReq.recordKeepingPermitCite}" />
        </af:column>
      </af:column>
      <af:column headerText="Reporting">
        <af:column headerText="Requirement" id="appReqReportingReqCol">
          <af:selectOneChoice id="appReportingReqChoice" readOnly="true"
            value="#{tvApplicableReq.tvCompRptFreqCd}"
            rendered="#{tvApplicableReq.tvCompRptFreqCd != 'OTH'}">
            <f:selectItems
              value="#{applicationReference.tvCompRptFreqDefs}" />
          </af:selectOneChoice>
          <af:outputText id="apReportingReqOther"
            rendered="#{tvApplicableReq.tvCompRptFreqCd == 'OTH'}"
            value="Other: #{tvApplicableReq.reportingOtherDsc}"/>
        </af:column>
        <af:column headerText="Rule Cite"
          id="appReqReportingRuleCiteCol">
          <af:inputText id="appReqAllowableRuleCiteText" readOnly="true"
            value="#{tvApplicableReq.reportingRuleCiteDsc}" />
        </af:column>
        <af:column headerText="Permit Cite"
          id="appReqReportingPermitCiteCol">
          <af:inputText id="appReqAllowablePermitCiteText"
            readOnly="true"
            value="#{tvApplicableReq.reportingPermitCite}" />
        </af:column>
      </af:column>
      <af:column headerText="Testing">
        <af:column headerText="Requirement" id="appReqTestingReqCol">
          <af:inputText id="appReqTestingReqText" readOnly="true"
            value="#{tvApplicableReq.testingValue}" />
        </af:column>
        <af:column headerText="Rule Cite" id="appReqTestingRuleCiteCol">
          <af:inputText id="appReqAllowableRuleCiteText" readOnly="true"
            value="#{tvApplicableReq.testingRuleCiteDsc}" />
        </af:column>
        <af:column headerText="Permit Cite"
          id="appReqTestingPermitCiteCol">
          <af:inputText id="appReqAllowablePermitCiteText"
            readOnly="true" value="#{tvApplicableReq.testingPermitCite}" />
        </af:column>
      </af:column>
      <af:column headerText="Status">
        <af:commandLink id="complianceLink"
          rendered="#{!tvApplicableReq.complianceStatus}"
          text="Not in Compliance"
          disabled="#{applicationDetail.editMode}"
          action="#{applicationDetail.startViewTVAppReq}"
          useWindow="true" windowWidth="1100" windowHeight="1000"
          shortDesc="View compliance data">
          <t:updateActionListener
            property="#{applicationDetail.pollutantType}"
            value="#{applicationDetail.SEC3}" />
          <t:updateActionListener
            property="#{applicationDetail.appReqTableId}"
            value="tvApplicableReq" />
        </af:commandLink>
        <af:objectSpacer height="10"
          rendered="#{!tvApplicableReq.complianceStatus}" />
        <af:commandLink id="complianceObligLink"
          rendered="#{tvApplicableReq.complianceObligationsStatus}"
          text="Other Compliance Obligations"
          disabled="#{applicationDetail.editMode}"
          action="#{applicationDetail.startViewTVAppReq}"
          useWindow="true" windowWidth="1100" windowHeight="1000"
          shortDesc="View compliance obligations data">
          <t:updateActionListener
            property="#{applicationDetail.pollutantType}"
            value="#{applicationDetail.SEC3}" />
          <t:updateActionListener
            property="#{applicationDetail.appReqTableId}"
            value="tvApplicableReq" />
        </af:commandLink>
        <af:objectSpacer height="10"
          rendered="#{tvApplicableReq.complianceObligationsStatus}" />
        <af:commandLink id="exemptionsLink"
          rendered="#{tvApplicableReq.proposedExemptionsStatus}"
          text="Proposed Exemptions"
          disabled="#{applicationDetail.editMode}"
          action="#{applicationDetail.startViewTVAppReq}"
          useWindow="true" windowWidth="1100" windowHeight="1000"
          shortDesc="View proposed exemptions data">
          <t:updateActionListener
            property="#{applicationDetail.pollutantType}"
            value="#{applicationDetail.SEC3}" />
          <t:updateActionListener
            property="#{applicationDetail.appReqTableId}"
            value="tvApplicableReq" />
        </af:commandLink>
        <af:objectSpacer height="10"
          rendered="#{tvApplicableReq.proposedExemptionsStatus}" />
        <af:commandLink id="altLimitsLink"
          rendered="#{tvApplicableReq.proposedAltLimitsStatus}"
          text="Proposed Alternative Limits"
          disabled="#{applicationDetail.editMode}"
          action="#{applicationDetail.startViewTVAppReq}"
          useWindow="true" windowWidth="1100" windowHeight="1000"
          shortDesc="View proposed alternative limits data">
          <t:updateActionListener
            property="#{applicationDetail.pollutantType}"
            value="#{applicationDetail.SEC3}" />
          <t:updateActionListener
            property="#{applicationDetail.appReqTableId}"
            value="tvApplicableReq" />
        </af:commandLink>
        <af:objectSpacer height="10"
          rendered="#{tvApplicableReq.proposedAltLimitsStatus}" />
        <af:commandLink id="testChangesLink"
          rendered="#{tvApplicableReq.proposedTestChangesStatus}"
          text="Proposed Changes to Testing"
          disabled="#{applicationDetail.editMode}"
          action="#{applicationDetail.startViewTVAppReq}"
          useWindow="true" windowWidth="1100" windowHeight="1000"
          shortDesc="View changes to testing data">
          <t:updateActionListener
            property="#{applicationDetail.pollutantType}"
            value="#{applicationDetail.SEC3}" />
          <t:updateActionListener
            property="#{applicationDetail.appReqTableId}"
            value="tvApplicableReq" />
        </af:commandLink>
      </af:column>
      <f:facet name="footer">
      <afh:rowLayout halign="center">
        <af:panelButtonBar>
          <af:commandButton text="Add" id="addApplicableReqs"
            useWindow="true" windowWidth="1100" windowHeight="1000"
            rendered="#{applicationDetail.editMode}"
            returnListener="#{applicationDetail.tvAppReqDialogDone}"
            action="#{applicationDetail.startAddTVAppReqInfo}">
            <t:updateActionListener
              property="#{applicationDetail.pollutantType}"
              value="#{applicationDetail.SEC3}" />
            <t:updateActionListener
              property="#{applicationDetail.appReqTableId}"
              value="tvApplicableReq" />
          </af:commandButton>
          <af:commandButton text="Edit Selected Row"
            partialTriggers="appReqTableSelection"
            disabled="#{!applicationDetail.okToEditApplicableReqTable}"
            useWindow="true" windowWidth="1100" windowHeight="1000"
            actionListener="#{applicationDetail.initActionTable}"
            rendered="#{applicationDetail.editMode}"
            action="#{applicationDetail.startEditTVAppReq}">
            <t:updateActionListener
              property="#{applicationDetail.appReqTableId}"
              value="tvApplicableReq" />
            <t:updateActionListener
              property="#{applicationDetail.pollutantType}"
              value="#{applicationDetail.SEC3}" />
          </af:commandButton>
          <af:commandButton text="Clone Selected Row"
            partialTriggers="appReqTableSelection"
            disabled="#{!applicationDetail.okToEditApplicableReqTable}"
            useWindow="true" windowWidth="1100" windowHeight="1000"
            actionListener="#{applicationDetail.initActionTable}"
            rendered="#{applicationDetail.editMode}"
            action="#{applicationDetail.startCopyTVAppReq}">
            <t:updateActionListener
              property="#{applicationDetail.appReqTableId}"
              value="tvApplicableReq" />
            <t:updateActionListener
              property="#{applicationDetail.pollutantType}"
              value="#{applicationDetail.SEC3}" />
          </af:commandButton>
          <af:commandButton text="Delete Selected Row(s)"
            partialTriggers="appReqTableSelection"
            disabled="#{!applicationDetail.okToDeleteApplicableReqTable}"
            rendered="#{applicationDetail.editMode}"
            actionListener="#{applicationDetail.initActionTable}"
            action="#{applicationDetail.deleteActionTableRows}">
          </af:commandButton>
          <af:commandButton id="appReqPrintBtn"
            actionListener="#{tableExporter.printTable}"
            onclick="#{tableExporter.onClickScript}"
            text="Printable view" />
          <af:commandButton id="appReqExportBtn"
            actionListener="#{tableExporter.excelTable}"
            onclick="#{tableExporter.onClickScript}"
            text="Export to excel" />
        </af:panelButtonBar>
        </afh:rowLayout>
      </f:facet>
    </af:table>
    <%
    // </t:div>
    %>

    <af:objectSpacer width="100%" height="10" />

    <af:outputText value="State Only Enforceable Requirements :"
      inlineStyle="font-size: 13px;font-weight:bold" />
    <%
    //<t:div style="overflow:auto;width:1040px;height:150px;">
    %>
    <af:table id="facilityStateReqsTable" width="98%" emptyText=" "
      var="tvStateOnlyReq" bandingInterval="1" banding="row"
      binding="#{applicationDetail.stateOnlyReqTable}"
      value="#{applicationDetail.stateOnlyRequirements}"
      rows="0">
      <f:facet name="selection">
        <af:tableSelectMany id="stateOnlyTableSelection"
          rendered="#{applicationDetail.editMode}" autoSubmit="true" />
      </f:facet>
      <af:column headerText="Allowable Limit"
        id="stateReqAllowableLimitCol">
        <af:inputText id="stateReqAllowableLimitText" readOnly="true"
          value="#{tvStateOnlyReq.allowableValue}" />
      </af:column>
      <af:column headerText="Pollutant" id="stateReqPollutantCol">
        <af:selectOneChoice id="stateReqPollutantChoice" readOnly="true"
          value="#{tvStateOnlyReq.pollutantCd}">
          <f:selectItems value="#{applicationReference.pollutantDefs}" />
        </af:selectOneChoice>
      </af:column>
      <af:column headerText="Rule Cite"
        id="stateReqAllowableRuleCiteCol">
        <af:inputText id="stateReqAllowableRuleCiteText" readOnly="true"
          value="#{tvStateOnlyReq.allowableRuleCiteDsc}" />
      </af:column>
      <af:column headerText="Permit Cite"
        id="stateReqAllowablePermitCiteCol">
        <af:inputText id="stateReqAllowablePermitCiteText"
          readOnly="true" value="#{tvStateOnlyReq.allowablePermitCite}" />
      </af:column>
      <af:column headerText="Monitoring">
        <af:column headerText="Requirement"
          id="stateReqMonitoringReqCol">
          <af:inputText id="stateReqMonitoringReqText" readOnly="true"
            value="#{tvStateOnlyReq.monitoringValue}" />
        </af:column>
        <af:column headerText="Rule Cite"
          id="stateReqMonitoringRuleCiteCol">
          <af:inputText id="stateReqAllowableRuleCiteText"
            readOnly="true"
            value="#{tvStateOnlyReq.monitoringRuleCiteDsc}" />
        </af:column>
        <af:column headerText="Permit Cite"
          id="stateReqMonitoringPermitCiteCol">
          <af:inputText id="stateReqAllowablePermitCiteText"
            readOnly="true"
            value="#{tvStateOnlyReq.monitoringPermitCite}" />
        </af:column>
      </af:column>
      <af:column headerText="Record Keeping">
        <af:column headerText="Requirement"
          id="stateReqRecordKeepingReqCol">
          <af:inputText id="stateReqRecordKeepingReqText"
            readOnly="true" value="#{tvStateOnlyReq.recordKeepingValue}" />
        </af:column>
        <af:column headerText="Rule Cite"
          id="stateReqRecordKeepingRuleCiteCol">
          <af:inputText id="stateReqAllowableRuleCiteText"
            readOnly="true"
            value="#{tvStateOnlyReq.recordKeepingRuleCiteDsc}" />
        </af:column>
        <af:column headerText="Permit Cite"
          id="stateReqRecordKeepingPermitCiteCol">
          <af:inputText id="stateReqAllowablePermitCiteText"
            readOnly="true"
            value="#{tvStateOnlyReq.recordKeepingPermitCite}" />
        </af:column>
      </af:column>
      <af:column headerText="Reporting">
        <af:column headerText="Requirement" id="stateReqReportingReqCol">
          <af:selectOneChoice id="stateReportingReqChoice"
            readOnly="true" value="#{tvStateOnlyReq.tvCompRptFreqCd}">
            <f:selectItems
              value="#{applicationReference.tvCompRptFreqDefs}" />
          </af:selectOneChoice>
        </af:column>
        <af:column headerText="Rule Cite"
          id="stateReqReportingRuleCiteCol">
          <af:inputText id="stateReqAllowableRuleCiteText"
            readOnly="true"
            value="#{tvStateOnlyReq.reportingRuleCiteDsc}" />
        </af:column>
        <af:column headerText="Permit Cite"
          id="stateReqReportingPermitCiteCol">
          <af:inputText id="stateReqAllowablePermitCiteText"
            readOnly="true"
            value="#{tvStateOnlyReq.reportingPermitCite}" />
        </af:column>
      </af:column>
      <af:column headerText="Testing">
        <af:column headerText="Requirement" id="stateReqTestingReqCol">
          <af:inputText id="stateReqTestingReqText" readOnly="true"
            value="#{tvStateOnlyReq.testingValue}" />
        </af:column>
        <af:column headerText="Rule Cite"
          id="stateReqTestingRuleCiteCol">
          <af:inputText id="stateReqAllowableRuleCiteText"
            readOnly="true" value="#{tvStateOnlyReq.testingRuleCiteDsc}" />
        </af:column>
        <af:column headerText="Permit Cite"
          id="stateReqTestingPermitCiteCol">
          <af:inputText id="stateReqAllowablePermitCiteText"
            readOnly="true" value="#{tvStateOnlyReq.testingPermitCite}" />
        </af:column>
      </af:column>
      <af:column headerText="Status">
        <af:commandLink id="complianceLink"
          rendered="#{!tvStateOnlyReq.complianceStatus}"
          text="Not in Compliance"
          disabled="#{applicationDetail.editMode}"
          action="#{applicationDetail.startViewTVAppReq}"
          useWindow="true" windowWidth="1100" windowHeight="1000"
          shortDesc="View compliance data">
          <t:updateActionListener
            property="#{applicationDetail.pollutantType}"
            value="#{applicationDetail.SEC3}" />
          <t:updateActionListener
            property="#{applicationDetail.appReqTableId}"
            value="tvStateOnlyReq" />
        </af:commandLink>
        <af:objectSpacer height="10"
          rendered="#{!tvStateOnlyReq.complianceStatus}" />
        <af:commandLink id="complianceObligLink"
          rendered="#{tvStateOnlyReq.complianceObligationsStatus}"
          text="Other Compliance Obligations"
          disabled="#{applicationDetail.editMode}"
          action="#{applicationDetail.startViewTVAppReq}"
          useWindow="true" windowWidth="1100" windowHeight="1000"
          shortDesc="View compliance obligations data">
          <t:updateActionListener
            property="#{applicationDetail.pollutantType}"
            value="#{applicationDetail.SEC3}" />
          <t:updateActionListener
            property="#{applicationDetail.appReqTableId}"
            value="tvStateOnlyReq" />
        </af:commandLink>
        <af:objectSpacer height="10"
          rendered="#{tvStateOnlyReq.complianceObligationsStatus}" />
        <af:commandLink id="exemptionsLink"
          rendered="#{tvStateOnlyReq.proposedExemptionsStatus}"
          text="Proposed Exemptions"
          disabled="#{applicationDetail.editMode}"
          action="#{applicationDetail.startViewTVAppReq}"
          useWindow="true" windowWidth="1100" windowHeight="1000" 
          shortDesc="View proposed exemptions data">
          <t:updateActionListener
            property="#{applicationDetail.pollutantType}"
            value="#{applicationDetail.SEC3}" />
          <t:updateActionListener
            property="#{applicationDetail.appReqTableId}"
            value="tvStateOnlyReq" />
        </af:commandLink>
        <af:objectSpacer height="10"
          rendered="#{tvStateOnlyReq.proposedExemptionsStatus}" />
        <af:commandLink id="altLimitsLink"
          rendered="#{tvStateOnlyReq.proposedAltLimitsStatus}"
          text="Proposed Alternative Limits"
          disabled="#{applicationDetail.editMode}"
          action="#{applicationDetail.startViewTVAppReq}"
          useWindow="true" windowWidth="1100" windowHeight="1000"
          shortDesc="View proposed alternative limits data">
          <t:updateActionListener
            property="#{applicationDetail.pollutantType}"
            value="#{applicationDetail.SEC3}" />
          <t:updateActionListener
            property="#{applicationDetail.appReqTableId}"
            value="tvStateOnlyReq" />
        </af:commandLink>
        <af:objectSpacer height="10"
          rendered="#{tvStateOnlyReq.proposedAltLimitsStatus}" />
        <af:commandLink id="testChangesLink"
          rendered="#{tvStateOnlyReq.proposedTestChangesStatus}"
          text="Proposed Changes to Testing"
          disabled="#{applicationDetail.editMode}"
          action="#{applicationDetail.startViewTVAppReq}"
          useWindow="true" windowWidth="1100" windowHeight="1000"
          shortDesc="View proposed changes to testing data">
          <t:updateActionListener
            property="#{applicationDetail.pollutantType}"
            value="#{applicationDetail.SEC3}" />
          <t:updateActionListener
            property="#{applicationDetail.appReqTableId}"
            value="tvStateOnlyReq" />
        </af:commandLink>
      </af:column>

      <f:facet name="footer">
      <afh:rowLayout halign="center">
        <af:panelButtonBar>
          <af:commandButton text="Add" id="addStateOnlyReqs"
            useWindow="true" windowWidth="1100" windowHeight="1000"
            rendered="#{applicationDetail.editMode}"
            returnListener="#{applicationDetail.tvAppReqDialogDone}"
            action="#{applicationDetail.startAddTVAppReqInfo}">
            <t:updateActionListener
              property="#{applicationDetail.pollutantType}"
              value="#{applicationDetail.SEC3}" />
            <t:updateActionListener
              property="#{applicationDetail.appReqTableId}"
              value="tvStateOnlyReq" />
          </af:commandButton>
          <af:commandButton text="Edit Selected Row"
            partialTriggers="stateOnlyTableSelection"
            disabled="#{!applicationDetail.okToEditStateOnlyReqTable}"
            useWindow="true" windowWidth="1100" windowHeight="1000"
            actionListener="#{applicationDetail.initActionTable}"
            action="#{applicationDetail.startEditTVAppReq}"
            rendered="#{applicationDetail.editMode}">
            <t:updateActionListener
              property="#{applicationDetail.appReqTableId}"
              value="tvStateOnlyReq" />
            <t:updateActionListener
              property="#{applicationDetail.pollutantType}"
              value="#{applicationDetail.SEC3}" />
          </af:commandButton>
          <af:commandButton text="Clone Selected Row"
            partialTriggers="stateOnlyTableSelection"
            disabled="#{!applicationDetail.okToEditStateOnlyReqTable}"
            useWindow="true" windowWidth="1100" windowHeight="1000"
            actionListener="#{applicationDetail.initActionTable}"
            rendered="#{applicationDetail.editMode}"
            action="#{applicationDetail.startCopyTVAppReq}">
            <t:updateActionListener
              property="#{applicationDetail.appReqTableId}"
              value="tvStateOnlyReq" />
            <t:updateActionListener
              property="#{applicationDetail.pollutantType}"
              value="#{applicationDetail.SEC3}" />
          </af:commandButton>
          <af:commandButton text="Delete Selected Row(s)"
            partialTriggers="stateOnlyTableSelection"
            disabled="#{!applicationDetail.okToDeleteStateOnlyReqTable}"
            rendered="#{applicationDetail.editMode}"
            actionListener="#{applicationDetail.initActionTable}"
            action="#{applicationDetail.deleteActionTableRows}">
          </af:commandButton>
          <af:commandButton id="stateOnlyPrintBtn"
            actionListener="#{tableExporter.printTable}"
            onclick="#{tableExporter.onClickScript}"
            text="Printable view" />
          <af:commandButton id="stateOnlyExportBtn"
            actionListener="#{tableExporter.excelTable}"
            onclick="#{tableExporter.onClickScript}"
            text="Export to excel" />
        </af:panelButtonBar>
        </afh:rowLayout>
      </f:facet>
    </af:table>
    <%
    //</t:div>
    %>
  </af:showDetailHeader>
</af:panelGroup>
