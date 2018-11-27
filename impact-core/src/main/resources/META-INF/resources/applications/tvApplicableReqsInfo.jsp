<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Title V Applicable Requirements">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true"
      partialTriggers="ruleCiteTypeChoice complianceBox otherObligationsBox 
      proposedExemptionsBox proposedAltLimitsBox proposedTestChangesBox
      reportingReqChoice">
      <af:messages />
      <af:panelForm>
        <af:objectSpacer width="100%" height="15" />
        <af:selectOneChoice id="ruleCiteTypeChoice"
          label="Requirement Basis :"
          readOnly="#{! applicationDetail.editMode}"
          value="#{applicationDetail.applicableReq.tvRuleCiteTypeCd}"
          autoSubmit="true">
          <f:selectItems
            value="#{applicationReference.ruleCiteTypeDefs}" />
        </af:selectOneChoice>

        <af:objectSpacer width="100%" height="20" />

        <af:inputText id="allowableLimitText" label="Allowable Limit : "
          readOnly="#{! applicationDetail.editMode}" maximumLength="40" columns="43"
          value="#{applicationDetail.applicableReq.allowableValue}" />
        <af:selectOneChoice id="pollutantChoice" label="Pollutant :"
          unselectedLabel="Please select"
          value="#{applicationDetail.applicableReq.pollutantCd}"
          readOnly="#{! applicationDetail.editMode}">
          <f:selectItems value="#{applicationDetail.tvPollutantDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="allowableRuleCiteChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="Please select"
          rendered="#{applicationDetail.applicableReq.ruleCitation}"
          value="#{applicationDetail.applicableReq.allowableRuleCiteCd}">
          <f:selectItems
            value="#{applicationReference.ruleCitationDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="allowableMACTChoice" label="Rule Cite :"
          readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.mactCitation}"
          value="#{applicationDetail.applicableReq.allowableRuleCiteCd}">
          <f:selectItems value="#{applicationReference.mactSubpartDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="allowableNESHAPChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.neshapCitation}"
          value="#{applicationDetail.applicableReq.allowableRuleCiteCd}">
          <f:selectItems
            value="#{applicationReference.neshapSubpartDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="allowableNSPSChoice" label="Rule Cite :"
          readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.nspsCitation}"
          value="#{applicationDetail.applicableReq.allowableRuleCiteCd}">
          <f:selectItems value="#{applicationReference.nspsSubpartDefs}" />
        </af:selectOneChoice>
        <af:inputText id="allowablePermitCiteText"
          label="Permit Cite : "  maximumLength="40" columns="43"
          readOnly="#{! applicationDetail.editMode}"
          value="#{applicationDetail.applicableReq.allowablePermitCite}" />

        <af:objectSpacer width="100%" height="20" />

        <af:inputText id="monitoringReqText"
          label="Monitoring Requirement : "  maximumLength="40" columns="43"
          readOnly="#{! applicationDetail.editMode}"
          value="#{applicationDetail.applicableReq.monitoringValue}" />
        <af:selectOneChoice id="monitoringRuleCiteChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.ruleCitation}"
          value="#{applicationDetail.applicableReq.monitoringRuleCiteCd}">
          <f:selectItems
            value="#{applicationReference.ruleCitationDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="monitoringMACTChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.mactCitation}"
          value="#{applicationDetail.applicableReq.monitoringRuleCiteCd}">
          <f:selectItems value="#{applicationReference.mactSubpartDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="monitoringNESHAPChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.neshapCitation}"
          value="#{applicationDetail.applicableReq.monitoringRuleCiteCd}">
          <f:selectItems
            value="#{applicationReference.neshapSubpartDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="monitoringNSPSChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.nspsCitation}"
          value="#{applicationDetail.applicableReq.monitoringRuleCiteCd}">
          <f:selectItems value="#{applicationReference.nspsSubpartDefs}" />
        </af:selectOneChoice>
        <af:inputText id="monitoringPermitCiteText"
          label="Permit Cite : "  maximumLength="40" columns="43"
          readOnly="#{! applicationDetail.editMode}"
          value="#{applicationDetail.applicableReq.monitoringPermitCite}" />


        <af:objectSpacer width="100%" height="20" />

        <af:inputText id="recordKeepingReqText"
          label="Record Keeping Requirement : "  maximumLength="40" columns="43"
          readOnly="#{! applicationDetail.editMode}"
          value="#{applicationDetail.applicableReq.recordKeepingValue}" />
        <af:selectOneChoice id="recordKeepingRuleCiteChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.ruleCitation}"
          value="#{applicationDetail.applicableReq.recordKeepingRuleCiteCd}">
          <f:selectItems
            value="#{applicationReference.ruleCitationDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="recordKeepingMACTChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.mactCitation}"
          value="#{applicationDetail.applicableReq.recordKeepingRuleCiteCd}">
          <f:selectItems value="#{applicationReference.mactSubpartDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="recordKeepingNESHAPChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.neshapCitation}"
          value="#{applicationDetail.applicableReq.recordKeepingRuleCiteCd}">
          <f:selectItems
            value="#{applicationReference.neshapSubpartDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="recordKeepingNSPSChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.nspsCitation}"
          value="#{applicationDetail.applicableReq.recordKeepingRuleCiteCd}">
          <f:selectItems value="#{applicationReference.nspsSubpartDefs}" />
        </af:selectOneChoice>
        <af:inputText id="recordKeepingPermitCiteText"
          label="Permit Cite : "  maximumLength="40" columns="43"
          readOnly="#{! applicationDetail.editMode}"
          value="#{applicationDetail.applicableReq.recordKeepingPermitCite}" />

        <af:objectSpacer width="100%" height="20" />

        <af:selectOneChoice id="reportingReqChoice"
          label="Reporting Requirement : "
          readOnly="#{! applicationDetail.editMode}"
          unselectedLabel=" " autoSubmit="true" 
          value="#{applicationDetail.applicableReq.tvCompRptFreqCd}">
          <f:selectItems
            value="#{applicationReference.tvCompRptFreqDefs}" />
        </af:selectOneChoice>
        <af:inputText id="reportingOtherText"
          rendered="#{applicationDetail.applicableReq.tvCompRptFreqCd == 'OTH'}"
          label="Other Reporting Requirement : "
          value="#{applicationDetail.applicableReq.reportingOtherDsc}"
          maximumLength="256" columns="70"/>
        <af:selectOneChoice id="reportingRuleCiteChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.ruleCitation}"
          value="#{applicationDetail.applicableReq.reportingRuleCiteCd}">
          <f:selectItems
            value="#{applicationReference.ruleCitationDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="reportingMACTChoice" label="Rule Cite :"
          readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.mactCitation}"
          value="#{applicationDetail.applicableReq.reportingRuleCiteCd}">
          <f:selectItems value="#{applicationReference.mactSubpartDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="reportingNESHAPChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.neshapCitation}"
          value="#{applicationDetail.applicableReq.reportingRuleCiteCd}">
          <f:selectItems
            value="#{applicationReference.neshapSubpartDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="reportingNSPSChoice" label="Rule Cite :"
          readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.nspsCitation}"
          value="#{applicationDetail.applicableReq.reportingRuleCiteCd}">
          <f:selectItems value="#{applicationReference.nspsSubpartDefs}" />
        </af:selectOneChoice>
        <af:inputText id="reportingPermitCiteText"
          label="Permit Cite : "  maximumLength="40" columns="43"
          readOnly="#{! applicationDetail.editMode}"
          value="#{applicationDetail.applicableReq.reportingPermitCite}" />

        <af:objectSpacer width="100%" height="20" />

        <af:inputText id="testingReqText" label="Testing Requirement : "
          readOnly="#{! applicationDetail.editMode}"  maximumLength="40" columns="43"
          value="#{applicationDetail.applicableReq.testingValue}" />
        <af:selectOneChoice id="testingRuleCiteChoice"
          label="Rule Cite :" readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.ruleCitation}"
          value="#{applicationDetail.applicableReq.testingRuleCiteCd}">
          <f:selectItems
            value="#{applicationReference.ruleCitationDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="testingMACTChoice" label="Rule Cite :"
          readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.mactCitation}"
          value="#{applicationDetail.applicableReq.testingRuleCiteCd}">
          <f:selectItems value="#{applicationReference.mactSubpartDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="testingNESHAPChoice" label="Rule Cite :"
          readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.neshapCitation}"
          value="#{applicationDetail.applicableReq.testingRuleCiteCd}">
          <f:selectItems
            value="#{applicationReference.neshapSubpartDefs}" />
        </af:selectOneChoice>
        <af:selectOneChoice id="testingNSPSChoice" label="Rule Cite :"
          readOnly="#{! applicationDetail.editMode}"
          unselectedLabel="N/A"
          rendered="#{applicationDetail.applicableReq.nspsCitation}"
          value="#{applicationDetail.applicableReq.testingRuleCiteCd}">
          <f:selectItems value="#{applicationReference.nspsSubpartDefs}" />
        </af:selectOneChoice>
        <af:inputText id="testingPermitCiteText" label="Permit Cite : "
          readOnly="#{! applicationDetail.editMode}"  maximumLength="40" columns="43"
          value="#{applicationDetail.applicableReq.testingPermitCite}" />

        <af:objectSpacer width="100%" height="20" />

        <af:selectOneRadio id="complianceBox" label="In compliance? "
          value="#{applicationDetail.applicableReq.complianceStatus}"
          readOnly="#{! applicationDetail.editMode}" layout="horizontal"
          autoSubmit="true">
          <f:selectItem itemLabel="Yes" itemValue="true" />
          <f:selectItem itemLabel="No" itemValue="false" />
        </af:selectOneRadio>
        <af:table id="tvComplianceReqsTable" emptyText=" "
          var="tvComplianceReqs" bandingInterval="1" banding="row"
          rendered="#{!applicationDetail.applicableReq.complianceStatus}"
          value="#{applicationDetail.applicableReq.complianceReqs}"
          width="98%">
          <f:facet name="selection">
            <af:tableSelectMany rendered="#{applicationDetail.editMode}" />
          </f:facet>
          <af:column id="complianceReqCol" formatType="text"
            headerText="Requirement">
            <af:inputText id="complianceReqText"  maximumLength="40"
              readOnly="#{! applicationDetail.editMode}"
              value="#{tvComplianceReqs.complianceApproachReq}" />
          </af:column>
          <af:column id="complianceCol" formatType="text"
            headerText="Proposed Approach to Achieve Compliance">
            <af:inputText id="complianceApproachText"
              readOnly="#{! applicationDetail.editMode}" maximumLength="1000"
              value="#{tvComplianceReqs.complianceApproach}"
              columns="80" rows="2" />
          </af:column>
          <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Add" id="addComplianceReqBtn"
                rendered="#{applicationDetail.editMode}"
                actionListener="#{applicationDetail.initActionTable}"
                action="#{applicationDetail.addActionTableRow}">
                <t:updateActionListener
                  property="#{applicationDetail.actionTableNewObject}"
                  value="#{applicationDetail.newTVComplianceObject}" />
              </af:commandButton>
              <af:commandButton text="Delete Selected Row(s)"
                rendered="#{applicationDetail.editMode}"
                actionListener="#{applicationDetail.initActionTable}"
                action="#{applicationDetail.deleteActionTableRows}">
              </af:commandButton>
            </af:panelButtonBar>
          </f:facet>
        </af:table>

        <af:selectOneRadio id="otherObligationsBox"
          label="Other Compliance Obligations?"
          value="#{applicationDetail.applicableReq.complianceObligationsStatus}"
          readOnly="#{! applicationDetail.editMode}" layout="horizontal"
          autoSubmit="true">
          <f:selectItem itemLabel="Yes" itemValue="true" />
          <f:selectItem itemLabel="No" itemValue="false" />
        </af:selectOneRadio>

        <af:table id="tvComplianceObligationsTable" emptyText=" "
          var="tvComplianceObligations" bandingInterval="1"
          banding="row"
          rendered="#{applicationDetail.applicableReq.complianceObligationsStatus}"
          value="#{applicationDetail.applicableReq.complianceObligationsReqs}"
          width="98%">
          <f:facet name="selection">
            <af:tableSelectMany rendered="#{applicationDetail.editMode}" />
          </f:facet>
          <af:column id="otherObligationsReqCol" formatType="text"
            headerText="Requirement">
            <af:inputText id="otherObligationsReqText"
              readOnly="#{! applicationDetail.editMode}"  maximumLength="40"
              value="#{tvComplianceObligations.complianceObligationsReq}" />
          </af:column>
          <af:column id="otherObligationsLimitCol" formatType="text"
            headerText="Limit">
            <af:inputText id="otherObligationsLimitText"
              readOnly="#{! applicationDetail.editMode}"  maximumLength="40"
              value="#{tvComplianceObligations.complianceObligationsLimit}" />
          </af:column>
          <af:column id="otherObligationsBasisCol" formatType="text"
            headerText="Basis">
            <af:inputText id="otherObligationsBasisText"
              readOnly="#{! applicationDetail.editMode}"  maximumLength="40"
              value="#{tvComplianceObligations.complianceObligationsBasis}" />
          </af:column>
          <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Add"
                id="addComplianceObligationsBtn"
                rendered="#{applicationDetail.editMode}"
                actionListener="#{applicationDetail.initActionTable}"
                action="#{applicationDetail.addActionTableRow}">
                <t:updateActionListener
                  property="#{applicationDetail.actionTableNewObject}"
                  value="#{applicationDetail.newTVComplianceObligationsObject}" />
              </af:commandButton>
              <af:commandButton id="delComplianceObligationsBtn"
                text="Delete Selected Row(s)"
                rendered="#{applicationDetail.editMode}"
                actionListener="#{applicationDetail.initActionTable}"
                action="#{applicationDetail.deleteActionTableRows}">
              </af:commandButton>
            </af:panelButtonBar>
          </f:facet>
        </af:table>

        <af:selectOneRadio id="proposedExemptionsBox"
          label="Proposed Exemptions?"
          value="#{applicationDetail.applicableReq.proposedExemptionsStatus}"
          readOnly="#{! applicationDetail.editMode}" layout="horizontal"
          autoSubmit="true">
          <f:selectItem itemLabel="Yes" itemValue="true" />
          <f:selectItem itemLabel="No" itemValue="false" />
        </af:selectOneRadio>

        <af:table id="tvProposedExemptionsTable" emptyText=" "
          var="tvProposedExemptions" bandingInterval="1" banding="row"
          rendered="#{applicationDetail.applicableReq.proposedExemptionsStatus}"
          value="#{applicationDetail.applicableReq.proposedExemptionsReqs}"
          width="98%">
          <f:facet name="selection">
            <af:tableSelectMany rendered="#{applicationDetail.editMode}" />
          </f:facet>
          <af:column id="proposedExemptionsReqCol" formatType="text"
            headerText="Requirement">
            <af:inputText id="proposedExemptionsReqText"
              readOnly="#{! applicationDetail.editMode}"  maximumLength="40"
              value="#{tvProposedExemptions.proposedExemptionsReq}" />
          </af:column>
          <af:column id="proposedExemptionsCol" formatType="text"
            headerText="Proposed Exemption(s)">
            <af:inputText id="proposedExemptionsText"
              readOnly="#{! applicationDetail.editMode}" maximumLength="1000"
              value="#{tvProposedExemptions.proposedExemptions}"
              columns="80" rows="2" />
          </af:column>
          <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Add" id="addComplianceReqBtn"
                rendered="#{applicationDetail.editMode}"
                actionListener="#{applicationDetail.initActionTable}"
                action="#{applicationDetail.addActionTableRow}">
                <t:updateActionListener
                  property="#{applicationDetail.actionTableNewObject}"
                  value="#{applicationDetail.newTVProposedExemptionsObject}" />
              </af:commandButton>
              <af:commandButton text="Delete Selected Row(s)"
                rendered="#{applicationDetail.editMode}"
                actionListener="#{applicationDetail.initActionTable}"
                action="#{applicationDetail.deleteActionTableRows}">
              </af:commandButton>
            </af:panelButtonBar>
          </f:facet>
        </af:table>

        <af:selectOneRadio id="proposedAltLimitsBox"
          label="Proposed Alternative Limits?"
          value="#{applicationDetail.applicableReq.proposedAltLimitsStatus}"
          readOnly="#{! applicationDetail.editMode}" layout="horizontal"
          autoSubmit="true">
          <f:selectItem itemLabel="Yes" itemValue="true" />
          <f:selectItem itemLabel="No" itemValue="false" />
        </af:selectOneRadio>

        <af:table id="tvProposedAltLimitsTable" emptyText=" "
          var="tvProposedAltLimits" bandingInterval="1" banding="row"
          rendered="#{applicationDetail.applicableReq.proposedAltLimitsStatus}"
          value="#{applicationDetail.applicableReq.proposedAltLimitsReqs}"
          width="98%">
          <f:facet name="selection">
            <af:tableSelectMany rendered="#{applicationDetail.editMode}" />
          </f:facet>
          <af:column id="proposedAltLimitsReqCol" formatType="text"
            headerText="Requirement">
            <af:inputText id="proposedAltLimitsReqText"
              readOnly="#{! applicationDetail.editMode}"  maximumLength="40"
              value="#{tvProposedAltLimits.proposedAltLimitsReq}" />
          </af:column>
          <af:column id="proposedAltLimitsCol" formatType="text"
            headerText="Proposed Alternative Emissions Limit(s) and Associated Basis">
            <af:inputText id="proposedAltLimitsText"
              readOnly="#{! applicationDetail.editMode}"  maximumLength="1000"
              value="#{tvProposedAltLimits.proposedAltLimits}"
              columns="80" rows="2" />
          </af:column>
          <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Add" id="addComplianceReqBtn"
                rendered="#{applicationDetail.editMode}"
                actionListener="#{applicationDetail.initActionTable}"
                action="#{applicationDetail.addActionTableRow}">
                <t:updateActionListener
                  property="#{applicationDetail.actionTableNewObject}"
                  value="#{applicationDetail.newTVProposedAltLimitsObject}" />
              </af:commandButton>
              <af:commandButton text="Delete Selected Row(s)"
                rendered="#{applicationDetail.editMode}"
                actionListener="#{applicationDetail.initActionTable}"
                action="#{applicationDetail.deleteActionTableRows}">
              </af:commandButton>
            </af:panelButtonBar>
          </f:facet>
        </af:table>

        <af:selectOneRadio id="proposedTestChangesBox"
          label="Proposed Changes to Testing?"
          value="#{applicationDetail.applicableReq.proposedTestChangesStatus}"
          readOnly="#{! applicationDetail.editMode}" layout="horizontal"
          autoSubmit="true">
          <f:selectItem itemLabel="Yes" itemValue="true" />
          <f:selectItem itemLabel="No" itemValue="false" />
        </af:selectOneRadio>

        <af:table id="tvProposedTestChangesTable" emptyText=" "
          var="tvProposedTestChanges" bandingInterval="1" banding="row"
          rendered="#{applicationDetail.applicableReq.proposedTestChangesStatus}"
          value="#{applicationDetail.applicableReq.proposedTestChangesReqs}"
          width="98%">
          <f:facet name="selection">
            <af:tableSelectMany rendered="#{applicationDetail.editMode}" />
          </f:facet>
          <af:column id="proposedTestChangesReqCol" formatType="text"
            headerText="Requirement">
            <af:inputText id="proposedTestChangesReqText"
              readOnly="#{! applicationDetail.editMode}"  maximumLength="40"
              value="#{tvProposedTestChanges.proposedTestChangesReq}" />
          </af:column>
          <af:column id="proposedTestChangesCol" formatType="text"
            headerText="Proposed changes to the existing test method(s)
            using compliance monitoring, record keeping or reporting methods">
            <af:inputText id="proposedTestChangesText"
              readOnly="#{! applicationDetail.editMode}" maximumLength="1000"
              value="#{tvProposedTestChanges.proposedTestChanges}"
              columns="80" rows="2" />
          </af:column>
          <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Add" id="addProposedTestChangesBtn"
                rendered="#{applicationDetail.editMode}"
                actionListener="#{applicationDetail.initActionTable}"
                action="#{applicationDetail.addActionTableRow}">
                <t:updateActionListener
                  property="#{applicationDetail.actionTableNewObject}"
                  value="#{applicationDetail.newTVProposedTestChangesObject}" />
              </af:commandButton>
              <af:commandButton id="delProposedTestChangesBtn"
                text="Delete Selected Row(s)"
                rendered="#{applicationDetail.editMode}"
                actionListener="#{applicationDetail.initActionTable}"
                action="#{applicationDetail.deleteActionTableRows}">
              </af:commandButton>
            </af:panelButtonBar>
          </f:facet>
        </af:table>

        <af:objectSpacer width="100%" height="20" />

        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Save"
              rendered="#{applicationDetail.editMode}"
              actionListener="#{applicationDetail.applyEditTVAppReq}" />
            <af:commandButton text="Cancel" immediate="true"
              action="#{applicationDetail.cancelEditTVAppReq}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

