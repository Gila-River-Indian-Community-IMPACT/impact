<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="complianceDelete"
		onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Delete Compliance Report Confirmation">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
			<af:panelForm>
				<af:messages />
				<afh:rowLayout halign="left">
					<af:outputFormatted rendered="#{!complianceReport.cannotDelete}"
						value="Click <b>Delete</b> to remove the Compliance Report." />
					<af:outputFormatted rendered="#{complianceReport.cannotDelete}"
						value="#{complianceReport.cannotDeleteReason}" />
				</afh:rowLayout>
				<af:objectSpacer height="10" />
				<afh:rowLayout halign="center">
					<af:table id="complianceEvents_Table" emptyText=" " width="100%"
						value="#{complianceReport.complianceReport.assocComplianceStatusEvents}"
						var="assoCse" bandingInterval="1" banding="row"
						rendered="#{complianceReport.displayComplianceEventsTable}">
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

				<af:objectSpacer height="10" />

				<afh:rowLayout halign="center">
					<af:table id="inspections_Table" emptyText=" " width="100%"
						value="#{complianceReport.complianceReport.inspectionsReferencedIn}"
						var="insp" bandingInterval="1" banding="row"
						rendered="#{complianceReport.displayReferencedInspectionsTable}">
						<af:column sortable="false" formatType="text"
							headerText="Inspection Id">
							<af:outputText value="#{insp}" />
						</af:column>
					</af:table>
				</afh:rowLayout>


				<afh:rowLayout halign="left">
				</afh:rowLayout>
				<af:objectSpacer height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar rendered="#{!complianceReport.cannotDelete}">
						<af:commandButton text="Delete"
							action="#{complianceReport.confirmDelete}" />
						<af:commandButton text="Cancel" action="#{complianceReport.close}" />
					</af:panelButtonBar>
					<af:panelButtonBar rendered="#{complianceReport.cannotDelete}">
						<af:commandButton text="Close" action="#{complianceReport.close}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
