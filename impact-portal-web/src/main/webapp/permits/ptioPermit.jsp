<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup>
  <af:panelHeader text="Permit information" size="0">
    <%
    /* General info begin */
    %>
    <af:panelForm rows="1">
      <af:selectBooleanCheckbox label="Federally Enforceable NSR (FENSR) :"
        rendered="#{! permitDetail.permit.tv}"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.feptio}" />
    </af:panelForm>
    <af:panelForm labelWidth="21%" fieldWidth="79%" partialTriggers="reasons">
      <af:selectManyCheckbox label="Reason(s) :" valign="top" id="reasons"
        readOnly="#{!permitDetail.editMode}" autoSubmit="true"
        value="#{permitDetail.permit.permitReasonCDs}">
        <f:selectItems value="#{permitDetail.permitReasons}" />
      </af:selectManyCheckbox>
      <af:selectOneChoice label="Original Permit No :"
        readOnly="#{! permitDetail.editMode}"
        rendered="#{permitDetail.originalPermitNeeded}"
        unselectedLabel="Please select"
        value="#{permitDetail.permit.originalPermitNo}">
        <mu:selectItems value="#{permitDetail.activePermits}" />
      </af:selectOneChoice>
      <%-- not valid for WY
      <af:selectBooleanCheckbox label="General Permit :"
        readOnly="#{! permitDetail.editMode}"
        disabled="#{! permitDetail.generalPermitAllowed}"
        value="#{permitDetail.permit.generalPermit}" />
      <af:selectBooleanCheckbox label="Express Permit :"
        readOnly="#{! permitDetail.editMode}"
        disabled="#{! permitDetail.expressPermitAllowed}"
        value="#{permitDetail.permit.generalPermit}" />
        --%>
    </af:panelForm>

    <afh:tableLayout width="100%" rendered="#{permitDetail.permit.converted}">
      <afh:rowLayout>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%">
            <af:selectBooleanCheckbox label="Converted :" id="converted"
              autoSubmit="TRUE" readOnly="true"
              value="#{permitDetail.permit.converted}" />
          </af:panelForm>
        </afh:cellFormat>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <af:panelForm labelWidth="44%" fieldWidth="56%"
            partialTriggers="converted">
            <af:selectInputDate label="Converted Date :"
              rendered="#{permitDetail.permit.converted}" readOnly="true"
              value="#{permitDetail.permit.convertedDate}" />
          </af:panelForm>
        </afh:cellFormat>
      </afh:rowLayout>
    </afh:tableLayout>

    <af:panelForm maxColumns="2" rows="2" labelWidth="44%" fieldWidth="56%">
      <af:selectInputDate label="Effective Date :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.effectiveDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:selectInputDate label="Mod. Effective Date :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.modEffectiveDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:selectInputDate label="Expiration Date :"
        rendered="#{! permitDetail.permit.tv}"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.expirationDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:selectOneChoice label="PER Due Date :" unselectedLabel="Please select"
        readOnly="#{! permitDetail.editMode}"
        rendered="#{! permitDetail.permit.tv}"
        value="#{permitDetail.permit.perDueDateCD}">
        <mu:selectItems value="#{permitReference.perDueDateDefs}" />
      </af:selectOneChoice>
      <af:inputText label="ERAC Case Number :" columns="20"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.eracCaseNumber}" />
    </af:panelForm>

    <af:panelForm maxColumns="3" rows="2" labelWidth="68%" fieldWidth="32%">
      <af:selectBooleanCheckbox label="Avoiding Major NSR Synthetic Minor :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.smtv}" />
      <af:selectBooleanCheckbox label="Netting :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.netting}" />
      <af:selectBooleanCheckbox label="Emissions Offsets :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.emissionsOffsets}" />
      <af:selectBooleanCheckbox label="Modeling Submitted :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.modelingSubmitted}" />
      <af:selectBooleanCheckbox label="PSD :"
        readOnly="#{! permitDetail.editMode}" value="#{permitDetail.permit.psd}" />
      <af:selectBooleanCheckbox label="Toxic Review :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.toxicReview}" />
      <af:selectBooleanCheckbox label="CEM :"
        readOnly="#{! permitDetail.editMode}" value="#{permitDetail.permit.cem}" />
    </af:panelForm>

    <af:panelForm maxColumns="3" rows="1" labelWidth="68%" fieldWidth="32%"
      partialTriggers="mact neshaps nsps">
      <af:selectBooleanCheckbox label="MACT :" id="mact" autoSubmit="TRUE"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.mact}" />
      <af:selectBooleanCheckbox label="NESHAPS :" id="neshaps" autoSubmit="TRUE"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.neshaps}" />
      <af:selectBooleanCheckbox label="NSPS :" id="nsps" autoSubmit="TRUE"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.nsps}" />

      <f:facet name="footer">
        <af:panelHeader text="Rules & Regs Applicability" size="4" id="fra"
          rendered="#{permitDetail.fraAllowed}">
          <afh:rowLayout halign="center">
            <t:div style="overflow:auto;width:680px">
              <af:table value="#{permitDetail.mactSubparts}" bandingInterval="1"
                banding="row" var="mactSubpart" id="mactTable"
                rendered="#{permitDetail.permit.mact}"
                partialTriggers="ThePage:ptiPermit:mactTable:addMact"
                binding="#{permitDetail.mactSubpartsTable}" width="98%">
                <f:facet name="selection">
                  <af:tableSelectMany rendered="#{permitDetail.editMode}" />
                </f:facet>
                <af:column sortProperty="value" sortable="true"
                  formatType="text" headerText="MACT Subpart">
                  <af:selectOneChoice value="#{mactSubpart.value}"
                    readOnly="#{! permitDetail.editMode}">
                    <f:selectItems value="#{permitReference.mactSubpartDefs}" />
                  </af:selectOneChoice>
                </af:column>
                <f:facet name="footer">
                  <afh:rowLayout halign="center">
                    <af:panelButtonBar>
                      <af:commandButton text="Add Subpart" id="addMact"
                        rendered="#{permitDetail.editMode}"
                        actionListener="#{permitDetail.addSubpart}">
                        <t:updateActionListener
                          property="#{permitDetail.subparts}"
                          value="#{permitDetail.mactSubparts}" />
                        <t:updateActionListener
                          property="#{permitDetail.subpartsTable}"
                          value="#{permitDetail.mactSubpartsTable}" />
                      </af:commandButton>
                      <af:commandButton text="Delete Selected Subparts"
                        rendered="#{permitDetail.editMode}"
                        action="#{facilityProfile.deleteSubparts}">
                        <t:updateActionListener
                          property="#{permitDetail.subparts}"
                          value="#{permitDetail.mactSubparts}" />
                        <t:updateActionListener
                          property="#{permitDetail.subpartsTable}"
                          value="#{permitDetail.mactSubpartsTable}" />
                      </af:commandButton>
                    </af:panelButtonBar>
                  </afh:rowLayout>
                </f:facet>
              </af:table>

              <af:objectSpacer height="5" />
              <af:table value="#{permitDetail.neshapsSubparts}"
                bandingInterval="1" banding="row" var="neshapsSubpart"
                rendered="#{permitDetail.permit.neshaps}"
                binding="#{permitDetail.neshapsSubpartsTable}" width="98%">
                <f:facet name="selection">
                  <af:tableSelectMany rendered="#{permitDetail.editMode}" />
                </f:facet>
                <af:column sortProperty="value" sortable="true"
                  formatType="text" headerText="NESHAPS Subpart">
                  <af:selectOneChoice value="#{neshapsSubpart.value}"
                    readOnly="#{! permitDetail.editMode}">
                    <f:selectItems value="#{permitReference.neshapsSubpartDefs}" />
                  </af:selectOneChoice>
                </af:column>
                <f:facet name="footer">
                  <afh:rowLayout halign="center">
                    <af:panelButtonBar>
                      <af:commandButton text="Add Subpart"
                        rendered="#{permitDetail.editMode}"
                        actionListener="#{permitDetail.addSubpart}">
                        <t:updateActionListener
                          property="#{permitDetail.subparts}"
                          value="#{permitDetail.neshapsSubparts}" />
                        <t:updateActionListener
                          property="#{permitDetail.subpartsTable}"
                          value="#{permitDetail.neshapsSubpartsTable}" />
                      </af:commandButton>
                      <af:commandButton text="Delete Selected Subparts"
                        rendered="#{permitDetail.editMode}"
                        action="#{facilityProfile.deleteSubparts}">
                        <t:updateActionListener
                          property="#{permitDetail.subparts}"
                          value="#{permitDetail.neshapsSubparts}" />
                        <t:updateActionListener
                          property="#{permitDetail.subpartsTable}"
                          value="#{permitDetail.neshapsSubpartsTable}" />
                      </af:commandButton>
                    </af:panelButtonBar>
                  </afh:rowLayout>
                </f:facet>
              </af:table>

              <af:objectSpacer height="5" />
              <af:table value="#{permitDetail.nspsSubparts}" bandingInterval="1"
                banding="row" var="nspsSubpart"
                rendered="#{permitDetail.permit.nsps}"
                binding="#{permitDetail.nspsSubpartsTable}" width="98%">
                <f:facet name="selection">
                  <af:tableSelectMany rendered="#{permitDetail.editMode}" />
                </f:facet>
                <af:column sortProperty="value" sortable="true"
                  formatType="text" headerText="NSPS Subpart">
                  <af:selectOneChoice value="#{nspsSubpart.value}"
                    readOnly="#{! permitDetail.editMode}">
                    <f:selectItems value="#{permitReference.nspsSubpartDefs}" />
                  </af:selectOneChoice>
                </af:column>
                <f:facet name="footer">
                  <afh:rowLayout halign="center">
                    <af:panelButtonBar>
                      <af:commandButton text="Add Subpart"
                        rendered="#{permitDetail.editMode}"
                        actionListener="#{permitDetail.addSubpart}">
                        <t:updateActionListener
                          property="#{permitDetail.subparts}"
                          value="#{permitDetail.nspsSubparts}" />
                        <t:updateActionListener
                          property="#{permitDetail.subpartsTable}"
                          value="#{permitDetail.nspsSubpartsTable}" />
                      </af:commandButton>
                      <af:commandButton text="Delete Selected Subparts"
                        rendered="#{permitDetail.editMode}"
                        action="#{facilityProfile.deleteSubparts}">
                        <t:updateActionListener
                          property="#{permitDetail.subparts}"
                          value="#{permitDetail.nspsSubparts}" />
                        <t:updateActionListener
                          property="#{permitDetail.subpartsTable}"
                          value="#{permitDetail.nspsSubpartsTable}" />
                      </af:commandButton>
                    </af:panelButtonBar>
                  </afh:rowLayout>
                </f:facet>
              </af:table>
            </t:div>
          </afh:rowLayout>
          <af:objectSpacer height="10" />
        </af:panelHeader>
      </f:facet>
    </af:panelForm>

    <af:panelForm maxColumns="2" rows="1" labelWidth="44%" fieldWidth="56%">
      <af:selectBooleanCheckbox label="Issue Draft ?"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.issueDraft}" />
      <af:selectBooleanCheckbox label="Hearing Requested :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.dapcHearingReqd}" />
    </af:panelForm>

    <afh:tableLayout width="100%">
      <afh:rowLayout>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <afh:rowLayout>
            <afh:cellFormat halign="right" valign="top" width="177"
              wrappingDisabled="false">
              <af:inputText label="Permit Strategy " readOnly="true" />
              <af:inputText label="Summary Write-up :" readOnly="true" />
            </afh:cellFormat>
            <afh:cellFormat>
              <af:switcher
                facetName="#{empty permitDetail.docsMap[permitReference.introPackageCD][permitReference.draftFlag]? 'noDoc': 'doc'}"
                defaultFacet="noDoc">
                <f:facet name="noDoc">
                  <af:commandButton text="Upload Document" useWindow="true"
                    windowWidth="500" windowHeight="300" id="UploadDDocButton"
                    returnListener="#{permitDetail.docDialogDone}"
                    action="#{permitDetail.uploadDoc}" />
                </f:facet>
                <f:facet name="doc">
                  <af:commandLink text="Open Document"
                    action="#{permitDetail.openDoc}">
                    <af:setActionListener
                      from="#{permitDetail.docsMap[permitReference.introPackageCD][permitReference.draftFlag]}"
                      to="#{requestScope.doc}" />
                  </af:commandLink>
                </f:facet>
              </af:switcher>
            </afh:cellFormat>
          </afh:rowLayout>
        </afh:cellFormat>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <afh:rowLayout>
            <afh:cellFormat width="177" halign="right" valign="top">
              <af:inputText label="Response to Comments :" readOnly="true" />
            </afh:cellFormat>
            <afh:cellFormat>
              <af:switcher
                facetName="#{empty permitDetail.docsMap[permitReference.introPackageCD][permitReference.finalFlag]? 'noDoc': 'doc'}"
                defaultFacet="noDoc">
                <f:facet name="noDoc">
                  <af:commandButton text="Upload Document" useWindow="true"
                    windowWidth="500" windowHeight="300" id="UploadDDocButton"
                    returnListener="#{permitDetail.docDialogDone}"
                    action="#{permitDetail.uploadDoc}" />
                </f:facet>
                <f:facet name="doc">
                  <af:commandLink text="Open Notice"
                    action="#{permitDetail.openDoc}">
                    <af:setActionListener
                      from="#{permitDetail.docsMap[permitReference.introPackageCD][permitReference.finalFlag]}"
                      to="#{requestScope.doc}" />
                  </af:commandLink>
                </f:facet>
              </af:switcher>
            </afh:cellFormat>
          </afh:rowLayout>
        </afh:cellFormat>
      </afh:rowLayout>

      <afh:rowLayout>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <%
          /* List of applications begin */
          %>
          <af:showDetailHeader text="Application/RPC numbers" size="2"
            disclosed="true">
            <af:table value="#{permitDetail.permit.applications}" var="row"
              emptyText=" " width="100%">
              <f:facet name="selection">
                <af:tableSelectMany rendered="#{permitDetail.editMode}" />
              </f:facet>
              <f:facet name="footer">
                <af:panelGroup layout="horizontal"
                  rendered="#{permitDetail.editMode}">
                  <af:inputText columns="10"
                    value="#{permitDetail.newReferencedAppNumber}" />
                  <af:commandButton text="Add"
                    actionListener="#{permitDetail.addReferencedApp}" />
                  <af:commandButton text="Delete selected items"
                    actionListener="#{permitDetail.deleteReferencedApp}" />
                </af:panelGroup>
              </f:facet>
              <af:column headerText="Number">
                <af:commandLink text="#{row.applicationNumber}"
                  action="applicationDetail">
                  <af:setActionListener from="#{row.applicationID}"
                    to="#{applicationDetail.applicationID}" />
                </af:commandLink>
              </af:column>
              <af:column headerText="Type">
                <af:outputText value="#{row.applicationTypeCD}" />
              </af:column>
            </af:table>
          </af:showDetailHeader>
          <%
          /* List of applications end */
          %>
        </afh:cellFormat>
        <afh:cellFormat width="50%" halign="left" valign="top">

        </afh:cellFormat>
      </afh:rowLayout>
    </afh:tableLayout>
  </af:panelHeader>
  <%
  /* General info end */
  %>

</af:panelGroup>
