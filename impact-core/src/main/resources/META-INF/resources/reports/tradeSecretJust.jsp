<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="#{reportProfile.justificationStr}">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />

      <af:panelForm maxColumns="1">
        <afh:rowLayout halign="left">
          <af:outputFormatted value="#{reportProfile.justificationStr}" />
        </afh:rowLayout>
        <afh:rowLayout halign="left">
          <af:inputText label="Pollutant:" readOnly="true"
            value="#{reportProfile.secretEmissionRow.pollutant}" />
        </afh:rowLayout>
        <afh:rowLayout halign="center">
          <af:inputText id="tsJust" rows="22"
            value="#{reportProfile.tsJust}" columns="120"
            maximumLength="2000" readOnly="#{!reportProfile.editable}">
          </af:inputText>
        </afh:rowLayout>
        <afh:rowLayout halign="center">
          <af:panelButtonBar>
            <af:commandButton text="OK"
              rendered="#{reportProfile.editable}"
              action="#{reportProfile.applyEditTS}" />
            <af:commandButton text="Cancel"
              rendered="#{reportProfile.editable}" immediate="true"
              action="#{reportProfile.cancelEditTS}" />
            <af:commandButton text="Close"
              rendered="#{!reportProfile.editable}"
              action="#{reportProfile.cancelEditTS}" />
          </af:panelButtonBar>
        </afh:rowLayout>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

