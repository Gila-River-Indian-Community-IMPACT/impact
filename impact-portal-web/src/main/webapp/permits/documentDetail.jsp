<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" title="Document detail">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />
      <af:panelForm>
        <af:selectOneChoice label="Doc Type"
          readOnly="#{permitDetail.tempDoc.permitDocTypeCD != null}"
          disabled="#{! permitDetail.editMode}"
          value="#{permitDetail.tempDoc.permitDocTypeCD}">
          <f:selectItem itemValue="T" itemLabel="Terms and conditions"
            itemDisabled="#{permitDetail.tempDoc.issuanceStageFlag != null}" />
          <f:selectItem itemValue="A" itemLabel="Attachment"
            itemDisabled="#{permitDetail.tempDoc.issuanceStageFlag != null}" />
          <f:selectItem itemValue="N" itemLabel="Newspaper notice"
            itemDisabled="#{permitDetail.tempDoc.issuanceStageFlag == 'F'}" />
          <f:selectItem itemValue="H" itemLabel="Hearing notice"
            itemDisabled="#{permitDetail.tempDoc.issuanceStageFlag == 'F'}" />
          <f:selectItem itemValue="C" itemLabel="Installation certificate"
            itemDisabled="#{permitDetail.tempDoc.issuanceStageFlag == 'D'}" />
          <f:selectItem itemValue="R" itemLabel="Response to comments"
            itemDisabled="#{permitDetail.tempDoc.issuanceStageFlag == 'D'}" />
        </af:selectOneChoice>
        <af:inputText label="Description" disabled="#{! permitDetail.editMode}"
          value="#{permitDetail.tempDoc.description}" />
        <af:inputFile label="File to upload"
          rendered="#{permitDetail.editMode && permitDetail.isDocUpload}"
          value="#{permitDetail.fileToUpload}" />
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Apply" rendered="#{permitDetail.editMode}"
              actionListener="#{permitDetail.applyEditDoc}" />
            <af:commandButton text="Cancel" rendered="#{permitDetail.editMode}"
              immediate="true" actionListener="#{permitDetail.cancelEditDoc}" />
            <af:commandButton text="Delete"
              rendered="#{permitDetail.editMode && permitDetail.isDocUpdate}"
              actionListener="#{permitDetail.removeEditDoc}" />
            <af:commandButton text="Close" rendered="#{! permitDetail.editMode}"
              actionListener="#{permitDetail.closeViewDoc}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

