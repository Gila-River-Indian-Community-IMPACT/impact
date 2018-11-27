<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}"
    onload="#{infraDefs.iframeReload}" title="New Attachment">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form usesUpload="true">
      <af:messages />
      <af:panelForm partialTriggers="reportType attachmentWarningMsg tradeSecretFile" maxColumns="2" width="600" labelWidth="160">
      	<af:outputText id="attachmentWarningMsg" 
      		rendered="#{(not empty attachments.document.docTypeCd) && 
      					!attachments.tradeSecretAllowed &&
      					attachments.newAttachment &&
            			attachments.publicAttachmentInfo == null}" 
            value="Do not attach any facility claimed confidential attachments"
      		inlineStyle="color: orange; font-weight: bold;"  />
        <af:selectOneChoice id="reportType"
          showRequired="true" label="Attachment Type :"
          rendered="#{attachments.hasDocType}"
          unselectedLabel=" " autoSubmit="true" immediate="true"
          value="#{attachments.document.docTypeCd}"
          readOnly="#{!attachments.newAttachment}" >
          <f:selectItems
            value="#{attachments.attachmentTypesDef.items[(empty attachments.document.docTypeCd? '': attachments.document.docTypeCd)]}" />
        </af:selectOneChoice>

        <af:inputText id="descriptionText" readOnly="#{attachments.locked && !attachments.newAttachment}" showRequired="true"
          label="Description :" maximumLength="500" 
          value="#{attachments.document.description}" />

		<af:panelForm maxColumns="2" width="600" labelWidth="160">
         <af:inputFile id="publicFile"
          label="#{attachments.tradeSecretSupported ? 'Public File to Upload : ' : 'File to Upload : '}"
          rendered="#{attachments.newAttachment &&
            attachments.publicAttachmentInfo == null}"
          showRequired="true"
          value="#{attachments.fileToUpload}"/>
         <af:inputText id="publicFileName"
          label="#{attachments.tradeSecretSupported ? 'Public File to Upload : ' : 'File to Upload : '}"
          rendered="#{attachments.publicAttachmentInfo != null}"
          value="#{attachments.publicAttachmentInfo.fileName}"
          readOnly="true" />
		</af:panelForm>

		<af:panelForm maxColumns="2" width="600" labelWidth="160"
			rendered="#{!empty attachments.document.docTypeCd}">
         <af:inputFile id="tradeSecretFile"
          label="Trade Secret File to Upload :"
          rendered="#{attachments.newAttachment && 
            attachments.tradeSecretSupported && attachments.tradeSecretAttachmentInfo == null && attachments.tradeSecretAllowed}"
          value="#{attachments.tsFileToUpload}"/>
         <af:inputText id="tradeSecretFileName"
          label="Trade Secret File to Upload :"
          rendered="#{attachments.tradeSecretSupported && attachments.tradeSecretAttachmentInfo != null && attachments.tradeSecretAllowed}"
          value="#{attachments.tradeSecretAttachmentInfo.fileName}"
          readOnly="true" />
         <af:inputText id="tradeSecretReason"
          label="Trade Secret Justification :" rows="6" columns="100"
          readOnly="#{attachments.locked && !attachments.newAttachment}"
          maximumLength="500"
          rendered="#{attachments.tradeSecretSupported 
          	&& (attachments.newAttachment || 
          		attachments.tempDoc.tradeSecretDocId != null) 
          	&& attachments.tradeSecretAllowed 
          	&&  !attachments.publicReadOnlyUser}"
          value="#{attachments.tempDoc.tradeSecretJustification}" />
        </af:panelForm>
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Apply"
              rendered="#{attachments.newAttachment}"
              actionListener="#{attachments.createAttachment}" />
            <af:commandButton text="Save"
              rendered="#{!attachments.newAttachment}"
              disabled="#{attachments.locked}"
              actionListener="#{attachments.updateAttachment}" />
            <af:commandButton text="Delete"
              rendered="#{attachments.deletePermitted && !attachments.newAttachment}"
              disabled="#{attachments.locked}"
              actionListener="#{attachments.deleteAttachment}" />
            <af:commandButton text="Cancel" immediate="true"
              actionListener="#{attachments.cancelAttachment}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>
