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
          <af:inputText label="Material:" readOnly="true"
            value="#{facilityReference.materialDefs.itemDesc[reportProfile.secretMaterialRow.material]}" />
        </afh:rowLayout>
        <afh:rowLayout halign="center">
          <af:inputText id="tsJust" rows="4"
            value="#{reportProfile.tsJust}" columns="60"
            maximumLength="400" readOnly="#{!reportProfile.editableM}">
          </af:inputText>
        </afh:rowLayout>
        <afh:rowLayout halign="center">
          <af:panelButtonBar>
            <af:commandButton text="OK"
              rendered="#{reportProfile.editableM}"
              action="#{reportProfile.applyEditTS}" />
<%--             <af:commandButton text="Cancel"
              rendered="#{reportProfile.editableM}" immediate="true"
              action="#{reportProfile.cancelEditTS}" />
            <af:commandButton text="Close"
              rendered="#{!reportProfile.editableM}"
              action="#{reportProfile.cancelEditTS}" /> --%>
          </af:panelButtonBar>
        </afh:rowLayout>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>
