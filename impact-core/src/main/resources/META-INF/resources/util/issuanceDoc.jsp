<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Document Detail">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page>
        <af:messages />
        <af:panelForm>
          <af:inputText label="Description" required="true"
            readOnly="true"
            value="#{generalIssuance.tempDoc.description}" />
          <af:inputFile label="File to Upload"
            value="#{generalIssuance.fileToUpload}" />
          <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Apply"
                rendered="#{generalIssuance.editMode}"
                actionListener="#{generalIssuance.applyEditDoc}" />
              <af:commandButton text="Cancel"
                rendered="#{generalIssuance.editMode}" immediate="true"
                actionListener="#{generalIssuance.cancelEditDoc}" />
            </af:panelButtonBar>
          </f:facet>
        </af:panelForm>
      </af:page>
    </af:form>
  </af:document>
</f:view>

