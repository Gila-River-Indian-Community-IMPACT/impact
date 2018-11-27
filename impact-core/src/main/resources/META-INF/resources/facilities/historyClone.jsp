<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Stars2 Clone Facility History">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />

      <af:panelForm>
        <af:selectInputDate readOnly="true" label="Start Date: "
          value="#{facilityProfile.selectedHistory.startDate}"/>
        <af:selectInputDate readOnly="true" tip="blank end date is current date"
          label="End Date: "
          value="#{facilityProfile.selectedHistory.endDate}" />
        <af:selectInputDate
          tip="New date must be between start and end dates"
          required="true" label="Revised End Date:" id="mdf3"
          value="#{facilityProfile.revisedHistoryDate}">
          <af:validateDateTimeRange
            minimum="#{facilityProfile.selectedHistory.startDate}"
            maximum="#{facilityProfile.selectedHistory.endDate}" />
        </af:selectInputDate>
        <af:inputText id="noteTxt" label="Note: " rows="4"
          value="#{facilityProfile.historyNote.noteTxt}" columns="80"
          maximumLength="256">
        </af:inputText>
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Split"
              actionListener="#{facilityProfile.applyCloneHistFacilityProfile}" />
            <af:commandButton text="Cancel" immediate="true"
              actionListener="#{facilityProfile.cancelCloneHistFacilityProfile}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>
