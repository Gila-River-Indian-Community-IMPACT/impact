<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document title="Permit Attachment">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<af:panelForm>
				<af:objectSpacer height="5"
					rendered="#{permitDetail.docInfo != null}" />
				<af:outputText rendered="#{permitDetail.docInfo != null}"
					value="#{permitDetail.docInfo}" />
				<af:objectSpacer height="5"
					rendered="#{permitDetail.docInfo != null}" />
				<af:selectOneChoice label="Attachment Type :" rendered = "#{permitDetail.tempDoc.fixed}"
					readOnly="#{permitDetail.tempDoc.permitDocTypeCD != null}"
					value="#{permitDetail.tempDoc.permitDocTypeCD}">
					<mu:selectItems value="#{permitDetail.permitFixedDocTypeInitialDefs}" />
				</af:selectOneChoice>
				<af:selectOneChoice label="Attachment Type :" rendered = "#{!permitDetail.tempDoc.fixed}"
					readOnly="#{permitDetail.tempDoc.permitDocTypeCD != null}"
					value="#{permitDetail.tempDoc.permitDocTypeCD}">
					<mu:selectItems value="#{permitDetail.permitDynaimDocTypeInitialDefs}" />
				</af:selectOneChoice>
				<af:inputText label="Description :" required="true"
					readOnly="#{permitDetail.docDescReadOnly}" maximumLength="512"
					value="#{permitDetail.tempDoc.description}" />
				<af:inputFile label="File to Upload :"
					rendered="#{permitDetail.dialogEdit && permitDetail.isDocUpload}"
					value="#{permitDetail.fileToUpload}" />
				<af:objectSpacer width="100%" height="10" />
				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Apply" rendered="#{permitDetail.dialogEdit}"
							actionListener="#{permitDetail.applyEditDoc}" />
						<af:commandButton text="Cancel"
							rendered="#{permitDetail.dialogEdit}" immediate="true"
							actionListener="#{permitDetail.cancelEditDoc}" />
						<af:commandButton text="Delete"
							rendered="#{permitDetail.dialogEdit && permitDetail.isDocUpdate}"
							actionListener="#{permitDetail.removeEditDoc}" />
						<af:commandButton text="Close"
							rendered="#{! permitDetail.dialogEdit}"
							actionListener="#{permitDetail.closeViewDoc}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>

