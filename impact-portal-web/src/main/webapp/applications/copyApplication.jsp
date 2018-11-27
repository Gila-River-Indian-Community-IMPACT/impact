<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" title="Copy/Correct Application">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page>
        <af:messages />
        <af:panelForm partialTriggers="correctApp">
          <af:inputText id="previousAppNumberText"
            label="Previous Application Number : "
            readOnly="true"
            value="#{applicationSearch.selectedApp.applicationNumber}"/>
          <af:selectBooleanCheckbox
            label="Facility-requested correction to application :"
            id="correctApp" rendered="#{applicationSearch.selectedAppCorrectable}"
            value="#{applicationSearch.correctedApplication}"
            autoSubmit="true" />
          <af:outputText inlineStyle="font-size: 12px"
            rendered="#{applicationSearch.correctedApplication}"
            value="#{applicationSearch.correctedApplicationPopupText}" />

          <af:inputText label="Reason for Correction :" rows="4"
            columns="50" maximumLength="500"
            rendered="#{applicationSearch.correctedApplication}"
            value="#{applicationSearch.correctedReason}" />

          <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Create"
                actionListener="#{applicationSearch.createApplicationCopy}">
                <af:setActionListener from="#{app.facility.facilityId}"
                  to="#{applicationSearch.newApplicationFacilityID}" />
              </af:commandButton>
              <af:commandButton text="Cancel" immediate="true"
                actionListener="#{applicationSearch.cancelApplicationCopy}" />
            </af:panelButtonBar>
          </f:facet>
        </af:panelForm>
      </af:page>
    </af:form>
  </af:document>
</f:view>
