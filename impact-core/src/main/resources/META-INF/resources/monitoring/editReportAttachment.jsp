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
      		rendered="#{(not empty monitorReportDetail.attachments.document.docTypeCd) && 
      					!monitorReportDetail.attachments.tradeSecretAllowed &&
      					monitorReportDetail.attachments.newAttachment &&
            			monitorReportDetail.attachments.publicAttachmentInfo == null}" 
            value="Do not attach any facility claimed confidential attachments"
      		inlineStyle="color: orange; font-weight: bold;"  />
        <af:selectOneChoice id="reportType"
          showRequired="true" label="Attachment Type :"
          rendered="#{monitorReportDetail.attachments.hasDocType}"
          unselectedLabel=" " autoSubmit="true" immediate="true"
          value="#{monitorReportDetail.attachments.document.docTypeCd}"
          readOnly="#{!monitorReportDetail.attachments.newAttachment}" >
          <f:selectItems
            value="#{monitorReportDetail.attachments.attachmentTypesDef.items[(empty monitorReportDetail.attachments.document.docTypeCd? '': monitorReportDetail.attachments.document.docTypeCd)]}" />
        </af:selectOneChoice>

        <af:inputText id="descriptionText" showRequired="true"
          label="Description :" maximumLength="500" 
          readOnly="#{!monitorReportDetail.attachmentEditAllowed}"
          value="#{monitorReportDetail.attachments.document.description}" />

		<af:panelForm maxColumns="2" width="600" labelWidth="160">
         <af:inputFile id="publicFile"
          label="#{monitorReportDetail.attachments.tradeSecretSupported ? 'Public File to Upload : ' : 'File to Upload : '}"
          rendered="#{monitorReportDetail.attachments.newAttachment &&
            monitorReportDetail.attachments.publicAttachmentInfo == null}"
          showRequired="true"
          value="#{monitorReportDetail.attachments.fileToUpload}"/>
         <af:inputText id="publicFileName"
          label="#{monitorReportDetail.attachments.tradeSecretSupported ? 'Public File to Upload : ' : 'File to Upload : '}"
          rendered="#{monitorReportDetail.attachments.publicAttachmentInfo != null}"
          value="#{monitorReportDetail.attachments.publicAttachmentInfo.fileName}"
          readOnly="true" />
		</af:panelForm>

		<af:panelForm maxColumns="2" width="600" labelWidth="160"
			rendered="#{!empty monitorReportDetail.attachments.document.docTypeCd}">
         <af:inputFile id="tradeSecretFile"
          label="Trade Secret File to Upload :"
          rendered="#{monitorReportDetail.attachments.newAttachment 
          	&& monitorReportDetail.attachments.tradeSecretSupported 
          	&& monitorReportDetail.attachments.tradeSecretAttachmentInfo == null 
          	&& monitorReportDetail.attachments.tradeSecretAllowed 
          	&&  !monitorReportDetail.publicReadOnlyUser}"
          value="#{monitorReportDetail.attachments.tsFileToUpload}"/>
         <af:inputText id="tradeSecretFileName"
          label="Trade Secret File to Upload :"
          rendered="#{monitorReportDetail.attachments.tradeSecretSupported 
          	&& monitorReportDetail.attachments.tradeSecretAttachmentInfo != null 
          	&& monitorReportDetail.attachments.tradeSecretAllowed 
          	&&  !monitorReportDetail.publicReadOnlyUser}"
          value="#{monitorReportDetail.attachments.tradeSecretAttachmentInfo.fileName}"
          readOnly="true" />
         <af:inputText id="tradeSecretReason"
          label="Trade Secret Justification :" rows="6" columns="100"
          readOnly="#{!monitorReportDetail.attachmentEditAllowed}"
          maximumLength="500"
          rendered="#{monitorReportDetail.attachments.tradeSecretSupported 
          	&& (monitorReportDetail.attachments.newAttachment || 
          		monitorReportDetail.attachments.tempDoc.tradeSecretDocId != null) 
          	&& monitorReportDetail.attachments.tradeSecretAllowed 
          	&&  !monitorReportDetail.publicReadOnlyUser}"
          value="#{monitorReportDetail.attachments.tempDoc.tradeSecretJustification}" />
        </af:panelForm>
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Apply"
              rendered="#{monitorReportDetail.attachments.newAttachment}"
              actionListener="#{monitorReportDetail.attachments.createAttachment}"
              disabled="#{!monitorReportDetail.attachmentEditAllowed}" />
            <af:commandButton text="Save"
              rendered="#{!monitorReportDetail.attachments.newAttachment}"
              disabled="#{!monitorReportDetail.attachmentEditAllowed}"
              actionListener="#{monitorReportDetail.attachments.updateAttachment}" />
            <af:commandButton text="Delete"
              rendered="#{monitorReportDetail.attachments.deletePermitted && !monitorReportDetail.attachments.newAttachment}"
              disabled="#{!monitorReportDetail.attachmentEditAllowed}"
              actionListener="#{monitorReportDetail.attachments.deleteAttachment}" />
            <af:commandButton text="Cancel" immediate="true"
              actionListener="#{monitorReportDetail.attachments.cancelAttachment}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>
