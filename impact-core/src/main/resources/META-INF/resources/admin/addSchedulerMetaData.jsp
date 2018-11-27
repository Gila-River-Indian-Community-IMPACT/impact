<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Add Scheduler Data">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
    <af:messages/>
      <af:panelForm partialTriggers="DocTypeChoice">
        <af:inputText label="Data Name"
                      value="#{scheduler.dataMapName}"/>
        <af:inputText label="Data Value"
                            value="#{scheduler.dataMapValue}"/>
        <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Add"
                                actionListener="#{scheduler.addNewDataValue}"/>
              <af:commandButton text="Cancel"
                                immediate="true"
                                actionListener="#{scheduler.cancelNewDataValue}"/>
            </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>
