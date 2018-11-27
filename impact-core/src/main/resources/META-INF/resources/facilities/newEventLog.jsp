<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Stars2 Facility Note Detail">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />

      <af:panelForm>
        <af:inputText label="Facility"  required="true" readOnly="true"
          value="#{facilityProfile.newEventLog.facilityId}" />

        <af:selectOneChoice label="Staff" required="true"
          readOnly="true" value="#{facilityProfile.newEventLog.userId}"
          unselectedLabel="-- Select one --">
          <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
        </af:selectOneChoice>

        <af:selectOneChoice label="Event Type" readOnly="true"
          required="true" unselectedLabel="-- Select one --"
          value="#{facilityProfile.newEventLog.eventTypeDefCd}">
          <f:selectItems value="#{eventLog.eventType}" />
        </af:selectOneChoice>

        <af:inputText label="Note" required="true"
          value="#{facilityProfile.newEventLog.note}" />

        <af:selectInputDate label="Date" required="true" readOnly="true"
          value="#{facilityProfile.newEventLog.date}" />

        <f:facet name="footer">
          <af:panelButtonBar>
            <af:objectSpacer width="100%" height="20" />
            <af:commandButton text="Save"
              actionListener="#{facilityProfile.saveNewEventLog}" />
            <af:commandButton text="Cancel" immediate="true"
              action="#{facilityProfile.closeDialog}" />

          </af:panelButtonBar>
        </f:facet>

      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

