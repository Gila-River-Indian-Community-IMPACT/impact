<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document id="body" title="Application Attachment">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:panelForm>
				<af:outputText id="attachmentWarningMsg" 
					rendered="#{!applicationDetail.tradeSecretAllowed && empty applicationDetail.applicationDoc.documentId && empty applicationDetail.publicFileInfo}" 
					value="Do not attach any facility claimed confidential attachments" inlineStyle="color: orange; font-weight: bold;"  />
				<af:inputText label="Attachment Type : "
					rendered="#{!(applicationDetail.applicationType == 'TV')}" 
					value="#{applicationReference.applicationDocTypeDefs.itemDesc[applicationDetail.applicationDoc.applicationDocumentTypeCD]}"
					readOnly="true"/>
				<af:inputText id="descriptionText" label="Description :"
					readOnly="#{!applicationDetail.okToEditAttachment}"
          			value="#{applicationDetail.applicationDoc.description}" maximumLength="500" showRequired="true" />
				<af:inputFile label="Public File to Upload : " id="requiredDocFile"
					rendered="#{empty applicationDetail.applicationDoc.documentId && empty applicationDetail.publicFileInfo}"
					value="#{applicationDetail.fileToUpload}" />
				 <af:inputText id="publicFileName"
	          		label="Public File to Upload : "
	          		rendered="#{applicationDetail.publicFileInfo != null}"
	         		value="#{applicationDetail.publicFileInfo.fileName}"
	          		readOnly="true" />
				<af:inputFile id="tradeSecretFile"
          			label="Trade Secret File to Upload :"
          			rendered="#{applicationDetail.tradeSecretAllowed && !applicationDetail.pbrRprRpe 
          				&& applicationDetail.tsFileInfo == null && applicationDetail.applicationDoc.tradeSecretDocId == null
          				&& empty applicationDetail.applicationDoc.documentId}"
          		    value="#{applicationDetail.tsFileToUpload}"/>
        		<af:inputText id="tsFileName"
          			label="Trade Secret File to Upload :"
          			rendered="#{applicationDetail.tsFileInfo != null && empty applicationDetail.applicationDoc.documentId}"
                    value="#{applicationDetail.tsFileInfo.fileName}"
          			readOnly="true" />
        		<af:inputText id="tradeSecretReason"
          			label="Trade Secret Justification :" rows="6" columns="100"
          			maximumLength="500"
          			rendered="#{applicationDetail.tradeSecretAllowed 
          				&& !applicationDetail.pbrRprRpe
          				&& (applicationDetail.applicationDoc.documentId == null || 
          					applicationDetail.applicationDoc.tradeSecretDocId != null)
       				 	&&  !applicationDetail.publicReadOnlyUser}"
          			readOnly="#{!applicationDetail.okToEditAttachment}"
          	        value="#{applicationDetail.applicationDoc.tradeSecretReason}"/>
				<af:inputText label="Title V Attachment Type : "
					rendered="#{applicationDetail.applicationType == 'TV'}" 
					value="#{applicationReference.tvApplicationDocTypeDefs.itemDesc[applicationDetail.applicationDoc.applicationDocumentTypeCD]}"
					readOnly="true"/>
				<af:objectSpacer height="10" />
				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Apply"
							rendered="#{applicationDetail.okToEditAttachment || applicationDetail.editAllowed}"
							actionListener="#{applicationDetail.applyRequiredDoc}" />
						<af:commandButton text="Cancel" immediate="true"
							actionListener="#{applicationDetail.closeViewDoc}" />
						<af:commandButton text="Delete"
							actionListener="#{applicationDetail.removeRequiredDocAttachment}"
							rendered="#{applicationDetail.deleteDocAllowed && applicationDetail.okToEditAttachment && applicationDetail.docUpdate && applicationDetail.applicationDoc.documentId != null}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>