<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Delete Stack Test">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page>
				<afh:rowLayout halign="center">
					<h:panelGrid>
						<af:panelForm>
							<af:messages />
							<afh:rowLayout halign="center">
								<af:outputFormatted rendered="#{!stackTestDetail.disableDelete}"
									value="Click <b>Delete</b> to remove the Stack Test" />
								<af:outputFormatted rendered="#{stackTestDetail.disableDelete}"
									value="#{stackTestDetail.disableDeleteMsg}" />
							</afh:rowLayout>
							<af:objectSpacer width="10" height="6" />
							<afh:rowLayout halign="center">
								<af:table id="complianceEvents_Table" emptyText=" " width="100%"
									value="#{stackTestDetail.stackTest.assocComplianceStatusEvents}"
									var="assoCse" bandingInterval="1" banding="row"
									rendered="#{stackTestDetail.displayComplianceEventsTable}">
									<af:column sortable="false" formatType="text"
										headerText="Permit Condition Id">
										<af:outputText value="#{assoCse.pCondId}" />
									</af:column>
									<af:column sortable="false" formatType="text"
										headerText="Compliance Status Event Id">
										<af:outputText value="#{assoCse.cStatusId}" />
									</af:column>
								</af:table>
							</afh:rowLayout>
							<af:objectSpacer width="10" height="6" />
							<afh:rowLayout halign="center">
								<af:table id="inspections_Table" emptyText=" " width="100%"
									value="#{stackTestDetail.stackTest.inspectionsReferencedIn}"
									var="insp" bandingInterval="1" banding="row"
									rendered="#{stackTestDetail.displayReferencedInspectionsTable}">
									<af:column sortable="false" formatType="text"
										headerText="Inspection Id">
										<af:outputText value="#{insp}" />
									</af:column>
								</af:table>
							</afh:rowLayout>
							<afh:rowLayout halign="left">
								<af:outputFormatted
									rendered="#{stackTestDetail.afsWarning != null}"
									inlineStyle="color: orange; font-weight: bold;"
									value="#{stackTestDetail.afsWarning}" />
							</afh:rowLayout>
							<af:objectSpacer height="10"
								rendered="#{stackTestDetail.afsWarning != null}" />
							<af:objectSpacer width="10" height="6" />
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Delete"
										disabled="#{stackTestDetail.disableDelete}"
										action="#{stackTestDetail.deleteEmissionsTest}" />
									<af:objectSpacer width="10" />
									<af:commandButton text="Cancel"
										action="#{stackTestDetail.closePopup}" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
