<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document title="Bulk Operations">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Undelivered Mail">
 <%@ include file="/util/header.jsp"%>

      <af:panelForm>
        <af:inputText id="FacilityName"
          label="Facility: " rows="1"
          value="#{undeliveredMail.facility.name}"
          readOnly="true">
        </af:inputText>
        <af:inputText id="FacilityId"
          label="Facility ID: " rows="1"
          value="#{undeliveredMail.facility.facilityId}"
          readOnly="true">
        </af:inputText>
        
        <af:inputText id="rumID"
          label="RUM ID#: " rows="1"
          value="#{undeliveredMail.facilityRUM.rumId}"
          readOnly="true">
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
            <af:commandButton text="Save"
              rendered="#{undeliveredMail.editable}"
              actionListener="#{undeliveredMail.applyEditWFRUM}" />
            <af:commandButton text="Edit"  immediate="true"
              rendered="#{!undeliveredMail.editable}"
              actionListener="#{undeliveredMail.startEditWFRUM}" />
             <af:commandButton text="Cancel" immediate="true"
              rendered="#{undeliveredMail.editable}"
              action="#{undeliveredMail.cancelEditWFRUM}" />
               <af:commandButton text="Workflow Task" 
              rendered="#{!undeliveredMail.editable}"
              action="#{undeliveredMail.goToCurrentWorkflow}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
          </af:page>
    </af:form>

  </af:document>
</f:view>

