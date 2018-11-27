<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" title="New permit">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
    <af:messages/>
      <af:panelForm partialTriggers="DocTypeChoice">
        <af:selectOneChoice label="Permit type"
                            value="#{permitSearch.newPermitType}">
          <f:selectItem itemLabel="Please select" itemValue="#{null}" />
          <f:selectItems value="#{permitReference.permitObjectTypes}"/>
        </af:selectOneChoice>
        <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Create"
                                action="#{permitSearch.createNewPermit}"/>
              <af:commandButton text="Cancel"
                                immediate="true"
                                action="#{permitSearch.cancelNewPermit}"/>
            </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>
