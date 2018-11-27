<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Stars2 Facility Undelivered Mail Detail">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form usesUpload="true">
      <af:messages />

      <af:panelForm>
        <af:inputText id="FacilityName" label="Facility: " rows="1"
          value="#{undeliveredMail.facility.name}" readOnly="true">
        </af:inputText>
        <af:inputText id="FacilityId" label="Facility ID: " rows="1"
          value="#{undeliveredMail.facility.facilityId}" readOnly="true">
        </af:inputText>

        <af:selectInputDate label="Mailing Date: " id="mailingDate"
          value="#{undeliveredMail.facilityRUM.originalMailDt}"
          required="true" readOnly="#{! undeliveredMail.editable}">
        </af:selectInputDate>

        <af:selectOneChoice label="Disposition: " required="true"
          readOnly="#{! undeliveredMail.editable}"
          value="#{undeliveredMail.facilityRUM.disposition}">
          <f:selectItems
            value="#{infraDefs.RUMDispositionTypes.items[(empty undeliveredMail.facilityRUM.disposition ? '' : undeliveredMail.facilityRUM.disposition)]}" />
        </af:selectOneChoice>

        <af:selectOneChoice label="Category: " required="true"
          readOnly="#{! undeliveredMail.editable}"
          value="#{undeliveredMail.facilityRUM.categoryCd}">
          <f:selectItems
            value="#{infraDefs.RUMCategoryTypes.items[(empty undeliveredMail.facilityRUM.categoryCd ? '' : undeliveredMail.facilityRUM.categoryCd)]}" />
        </af:selectOneChoice>

        <af:inputText id="addressTxt"
          label="Undeliverable Address/Contact: " rows="2"
          value="#{undeliveredMail.facilityRUM.undeliverableAddress}"
          columns="50" maximumLength="256" required="true"
          readOnly="#{! undeliveredMail.editable}">
        </af:inputText>

        <af:selectOneChoice label="Reason for return: " required="true"
          readOnly="#{! undeliveredMail.editable}"
          value="#{undeliveredMail.facilityRUM.reasonCd}">
          <f:selectItems
            value="#{infraDefs.RUMReasonTypes.items[(empty undeliveredMail.facilityRUM.reasonCd ? '' : undeliveredMail.facilityRUM.reasonCd)]}" />
        </af:selectOneChoice>

        <af:inputText id="noteTxt" label="Comment: " rows="2"
          value="#{undeliveredMail.facilityRUM.dapcNote}" columns="50"
          maximumLength="256" required="false"
          readOnly="#{! undeliveredMail.editable}">
        </af:inputText>

        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Save & Submit"
              rendered="#{undeliveredMail.editable && !undeliveredMail.rumModify}"
              actionListener="#{undeliveredMail.applyEditRUM}" />
            <af:commandButton text="Save"
              rendered="#{undeliveredMail.editable && undeliveredMail.rumModify}"
              actionListener="#{undeliveredMail.applyEditRUM}" />
            <af:commandButton immediate="true"
              text="Show Current Facility Inventory"
              rendered="#{!undeliveredMail.editable}"
              action="#{facilityProfile.submitProfileById}">
              <t:updateActionListener
                property="#{facilityProfile.facilityId}"
                value="#{undeliveredMail.facility.facilityId}" />
              <t:updateActionListener
                property="#{menuItem_facProfile.disabled}" value="false" />
            </af:commandButton>
            <af:commandButton text="Edit"
              disabled="#{undeliveredMail.disabledUpdateButton}"
              rendered="#{!undeliveredMail.editable}"
              actionListener="#{undeliveredMail.startEditRUM}" />
            <af:commandButton text="Cancel" immediate="true"
              action="#{undeliveredMail.cancelEditRUM}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

