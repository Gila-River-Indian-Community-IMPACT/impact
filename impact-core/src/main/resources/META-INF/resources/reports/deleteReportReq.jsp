<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Delete Period">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page id="deleteReportReq">
				<afh:rowLayout halign="center">
					<h:panelGrid>
						<af:panelForm>
							<afh:rowLayout halign="center">
								<af:outputText rendered="#{!reportProfile.deleteDisabled}"
									value="Clicking Delete Report will permanently delete the entire emissions inventory including all emissions and any attachments" />
								<af:outputFormatted rendered="#{reportProfile.deleteDisabled}"
									value="#{reportProfile.deleteDisabledMsg}" />
							</afh:rowLayout>

							<af:objectSpacer width="10" height="6" />

							<afh:rowLayout halign="center">
								<af:table id="inspections_Table" emptyText=" " width="100%"
									value="#{reportProfile.report.inspectionsReferencedIn}"
									var="insp" bandingInterval="1" banding="row"
									rendered="#{reportProfile.displayReferencedInspectionsTable}">
									<af:column sortable="false" formatType="text"
										headerText="Inspection Id">
										<af:outputText value="#{insp}" />
									</af:column>
								</af:table>
							</afh:rowLayout>

							<af:objectSpacer width="10" height="6" />

							<afh:rowLayout halign="center">
								<af:panelButtonBar rendered="#{!reportProfile.deleteDisabled}">
									<af:commandButton text="Delete Report" id="deleteReport"
										disabled="#{!reportProfile.stars2Admin && !reportProfile.generalUser}"
										action="#{reportProfile.deleteReport}" />
									<af:objectSpacer width="10" />
									<af:commandButton text="Cancel" id="cancel"
										action="#{reportProfile.closeDialog}" />
								</af:panelButtonBar>
								<af:panelButtonBar rendered="#{reportProfile.deleteDisabled}">
									<af:commandButton text="Close"
										action="#{reportProfile.closeDialog}" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
