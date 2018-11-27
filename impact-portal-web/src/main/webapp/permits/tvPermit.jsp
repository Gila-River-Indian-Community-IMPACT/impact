<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:panelGroup>
  <af:panelHeader text="Permit information" size="0">
    <%
    /* General info begin */
    %>
    <af:panelForm labelWidth="21%" fieldWidth="79%" partialTriggers="reasons">
      <af:selectManyCheckbox label="Reason(s) :" valign="top" id="reasons"
        readOnly="#{!permitDetail.editMode}" autoSubmit="true"
        value="#{permitDetail.permit.permitReasonCDs}">
        <f:selectItems value="#{permitDetail.permitReasons}" />
      </af:selectManyCheckbox>
      <af:inputText label="Original Permit No :" columns="20"
        rendered="#{permitDetail.originalPermitNeeded}"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.originalPermitNo}" />
    </af:panelForm>
    <af:panelForm maxColumns="2" rows="2" labelWidth="44%" fieldWidth="56%">
      <af:selectInputDate label="Effective date :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.effectiveDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:selectInputDate label="Mod. Effective date :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.modEffectiveDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:selectInputDate label="Expiration date :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.expirationDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
      <af:inputText label="ERAC case number :" columns="20"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.eracCaseNumber}" />
      <af:inputText label="Original permit number :" columns="20"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.eracCaseNumber}" />
    </af:panelForm>
    <%
    			 /*
    			 <af:panelForm labelWidth="21%" fieldWidth="79%">
    			 <af:inputText label="AQD comments :" columns="50" rows="2"
    			 readOnly="#{! permitDetail.editMode}"
    			 inlineStyle="#{!permitDetail.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
    			 value="#{permitDetail.permit.dapcComments}" />
    			 </af:panelForm>
    			 */
    %>
    <af:panelForm maxColumns="2" rows="2" labelWidth="44%" fieldWidth="56%">
      <af:selectBooleanCheckbox label="Acid rain :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.acidRain}" />
      <af:selectBooleanCheckbox label="CAM :"
        readOnly="#{! permitDetail.editMode}" value="#{permitDetail.permit.cam}" />
      <af:selectBooleanCheckbox label="Section 112(r ) :"
        readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.sec112}" />
      <af:inputText label="Description :" readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.acidDesc}" />
      <af:inputText label="Description :" readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.camDesc}" />
      <af:inputText label="Description :" readOnly="#{! permitDetail.editMode}"
        value="#{permitDetail.permit.sec112Desc}" />
    </af:panelForm>

    <afh:tableLayout width="100%">
      <afh:rowLayout>
        <afh:cellFormat width="50%" halign="left" valign="top">
          <afh:rowLayout>
            <afh:cellFormat halign="right" valign="top" width="177">
              <af:inputText label="Statement of basis :" readOnly="true" />
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
              <af:inputText label="Response to comments :" readOnly="true" />
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
    </afh:tableLayout>

    <afh:tableLayout width="100%">
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
