<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" title="Emissions Inventory Attachment">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />
      <af:panelForm partialTriggers="docTypeChoice docTypeChoice1 attachmentWarningMsg tradeSecretFile" maxColumns="2" width="700" labelWidth="200">
        <af:outputText id="attachmentWarningMsg" 
					rendered="#{(not empty reportProfile.emissionsDoc.emissionsDocumentTypeCD) && 
								!reportProfile.tradeSecretAllowed &&
								reportProfile.publicFileInfo == null && 
	            				reportProfile.emissionsDoc.documentId == null &&  
	            				!reportProfile.docUpdate}" 
					value="Do not attach any facility claimed confidential attachments" inlineStyle="color: orange; font-weight: bold;"  />
        <af:selectOneChoice label="Attachment Type :" id="docTypeChoice" rendered="#{reportProfile.internalApp}"
          unselectedLabel="Please select" autoSubmit="true" immediate="true" showRequired="true"
          value="#{reportProfile.emissionsDoc.emissionsDocumentTypeCD}"
          readOnly="#{!reportProfile.okToEdit}">
          <f:selectItems
            value="#{facilityReference.emissionsAttachmentTypeDefs.items[(empty reportProfile.emissionsDoc.emissionsDocumentTypeCD? '': reportProfile.emissionsDoc.emissionsDocumentTypeCD)]}" />
        </af:selectOneChoice>
        <af:selectOneChoice label="Attachment Type :" id="docTypeChoice1" rendered="#{!reportProfile.internalApp}"
          unselectedLabel="Please select" autoSubmit="true" immediate="true" showRequired="true"
          value="#{reportProfile.emissionsDoc.emissionsDocumentTypeCD}"
          readOnly="#{!reportProfile.okToEdit}">
          <f:selectItems
            value="#{facilityReference.emissionsAttachmentTypeDefs.items[(empty reportProfile.emissionsDoc.emissionsDocumentTypeCD? '': reportProfile.emissionsDoc.emissionsDocumentTypeCD)]}" />
        </af:selectOneChoice>
        
        <af:inputText id="descriptionText" label="Description :"
          value="#{reportProfile.emissionsDoc.description}" showRequired="true"
          maximumLength="500" rows="4" columns="75"
          readOnly="#{!reportProfile.okToEdit}"/>
          
        <af:panelForm maxColumns="2" width="700" labelWidth="200">
	        <af:inputFile label="Public File to Upload : " id="publicFile"
	          rendered="#{reportProfile.publicFileInfo == null && 
	            reportProfile.emissionsDoc.documentId == null &&  !reportProfile.docUpdate}" showRequired="true"
	          value="#{reportProfile.fileToUpload}" />
	        <af:inputText id="publicFileName" label="Public File to Upload : "
	          rendered="#{reportProfile.publicFileInfo != null}"
	          value="#{reportProfile.publicFileInfo.fileName}"
	          readOnly="true"/>
		</af:panelForm>          
		
		<af:panelForm maxColumns="2" width="700" labelWidth="200" rendered="#{!empty reportProfile.emissionsDoc.emissionsDocumentTypeCD}">          
	        <af:inputFile id="tradeSecretFile"
	          label="Trade Secret File to Upload :"
	          rendered="#{reportProfile.tsFileInfo == null 
	          	&& reportProfile.tradeSecretVisible 
	          	&& !reportProfile.docUpdate 
	          	&& reportProfile.tradeSecretAllowed 
	          	&&  !reportProfile.publicReadOnlyUser}"
	          value="#{reportProfile.tsFileToUpload}"/>
	        <af:inputText id="tsFileName" label="Trade Secret File to Upload : "
	          rendered="#{reportProfile.tsFileInfo != null 
	          	&& reportProfile.tradeSecretAllowed 
	          	&&  !reportProfile.publicReadOnlyUser}"
	          value="#{reportProfile.tsFileInfo.fileName}"
	          readOnly="true"/>
	        <af:inputText id="tradeSecretReason"
	          label="Trade Secret Justification :" rows="6"
	          columns="100" maximumLength="500"
	          value="#{reportProfile.emissionsDoc.tradeSecretReason}"
	          rendered="#{(!reportProfile.docUpdate || reportProfile.emissionsDoc.tradeSecretDocId != null) 
	          	&& reportProfile.tradeSecretAllowed 
	          	&&  !reportProfile.publicReadOnlyUser}" 
	          readOnly="#{!reportProfile.okToEdit || !reportProfile.tradeSecretVisible}"/>
        </af:panelForm>
          
        <af:objectSpacer width="100%" height="10" />
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Apply"
              actionListener="#{reportProfile.applyEditDoc}" 
              rendered="#{reportProfile.okToEdit && !reportProfile.readOnlyUser}"/>
            <af:commandButton text="Cancel"
              immediate="true"
              actionListener="#{reportProfile.cancelEditDoc}" />
            <af:commandButton text="Delete Attachment"
              actionListener="#{reportProfile.removeEditDoc}" 
              rendered="#{reportProfile.deleteDocAllowed && reportProfile.docUpdate && reportProfile.okToEdit && !reportProfile.readOnlyUser}"/>
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>