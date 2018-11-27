<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="fceDelete" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Purge Facilities Confirmation">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form id="form">
				<af:panelForm>
					<af:messages />
					<afh:rowLayout halign="left">
						<af:outputFormatted value="#{bulkOperationsCatalog.warningMessage}" />
					</afh:rowLayout>
					<af:objectSpacer height="10" />
					<afh:rowLayout halign="left">
						<af:outputFormatted inlineStyle="color: red; font-weight: bold; font-size: 20px" 
							value="This oeration cannot be reversed and will result in data being removed from the system’s database. 
									Make absolutely certain that the list of facilities to be purged is correct before proceeding."/>	
					</afh:rowLayout>					
					<af:objectSpacer height="10" />
				
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton id="purgeButton" text="Purge"
								action="#{bulkOperationsCatalog.bulkOperation.applyFinalAction}" />
							<af:commandButton id="cancelButton" text="Cancel"
								action="#{bulkOperationsCatalog.bulkOperation.reset}"
								actionListener="#{bulkOperationsCatalog.bulkOperationCancel}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
		</af:form>
	</af:document>
</f:view>
