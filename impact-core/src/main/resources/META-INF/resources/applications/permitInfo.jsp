<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Permits Associated with Application">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true"
      partialTriggers="ruleCiteTypeChoice complianceBox otherObligationsBox 
      proposedExemptionsBox proposedAltLimitsBox proposedTestChangesBox">
      <af:messages />
      <af:panelForm>

        <af:objectSpacer width="100%" height="15" />

        <af:outputText value="Permits" 
          rendered="#{not empty applicationDetail.associatedPermitsList}"/>

        <af:table id="permitTable"
          value="#{applicationDetail.associatedPermitsList}"
          bandingInterval="1" width="100%" banding="row"
          rendered="#{not empty applicationDetail.associatedPermitsList}"
          var="permit">
          <af:column sortable="true" formatType="text"
            headerText="Permit">
            <af:commandLink text="#{permit.permitNumber}"
              actionListener="#{applicationDetail.showRelatedPermit}"
              immediate="true">
              <af:setActionListener to="#{applicationDetail.relatedPermitId}"
                from="#{permit.permitID}" />
            </af:commandLink>
          </af:column>
          <af:column sortable="true" formatType="text" headerText="Final Issue Date">
            <af:selectInputDate readOnly="true" value="#{permit.finalIssueDate}" />
          </af:column>
        </af:table>
        <af:outputText
          rendered="#{not empty applicationDetail.associatedPermitsList}"
          value="Select a link in the above table to navigate to the Permit 
          Detail page for the selected permit." />
        <af:outputText
          rendered="#{empty applicationDetail.associatedPermitsList}"
          value="There are no permits associated with this application." />

        <af:objectSpacer width="100%" height="20" />

        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Close" immediate="true">
              <af:returnActionListener />
            </af:commandButton>
          </af:panelButtonBar>
        </f:facet>

      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

