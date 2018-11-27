<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Delete Application">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page>
				<af:messages />
				<af:panelForm>
					<af:objectSpacer width="100%" height="15" />
					<af:panelGroup layout="vertical">
						<af:outputText
							rendered="#{!applicationDetail.disableDeleteOnPopup}"
							value="All data associated with this application will be deleted. Would you like to continue?" />
						<af:outputFormatted
							rendered="#{applicationDetail.disableDeleteOnPopup}"
							value="#{applicationDetail.disableDeleteMsgOnPopup}" />
					</af:panelGroup>

					<af:objectSpacer width="10" height="6" />
					<afh:rowLayout halign="center">
						<af:table id="inspections_Table" emptyText=" " width="100%"
							value="#{applicationDetail.application.inspectionsReferencedIn}"
							var="insp" bandingInterval="1" banding="row"
							rendered="#{applicationDetail.displayReferencedInspectionsTable}">
							<af:column sortable="false" formatType="text"
								headerText="Inspection Id">
								<af:outputText value="#{insp}" />
							</af:column>
						</af:table>
					</afh:rowLayout>
					<af:objectSpacer width="10" height="6" />

					<f:facet name="footer">
						<af:panelButtonBar>
							<af:commandButton text="Yes"
								rendered="#{!applicationDetail.disableDeleteOnPopup}"
								actionListener="#{applicationDetail.removeApplication}" />
							<af:commandButton text="No" immediate="true"
								rendered="#{!applicationDetail.disableDeleteOnPopup}">
								<af:returnActionListener />
							</af:commandButton>
							<af:commandButton text="Close" immediate="true"
								rendered="#{applicationDetail.disableDeleteOnPopup}">
								<af:returnActionListener />
							</af:commandButton>
						</af:panelButtonBar>
					</f:facet>
				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
</f:view>

