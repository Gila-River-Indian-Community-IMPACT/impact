<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Delete Monitor Site">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page id="deleteMonitorSite">
				<af:messages />
				<af:panelForm>
					<af:objectSpacer width="100%" height="15" />
					<af:panelGroup layout="vertical">
						<af:outputFormatted rendered="#{!monitorSiteDetail.cannotDelete}"
							value="All data associated with this monitor site will be deleted. Would you like to continue?" />
						<af:outputFormatted rendered="#{monitorSiteDetail.cannotDelete}"
							value="#{monitorSiteDetail.cannotDeleteReason}" />
					</af:panelGroup>

					<af:objectSpacer height="10" />

					<afh:rowLayout halign="center">
						<af:table id="inspections_Table" emptyText=" " width="100%"
							value="#{monitorSiteDetail.monitorSite.inspectionsReferencedIn}"
							var="insp" bandingInterval="1" banding="row"
							rendered="#{monitorSiteDetail.displayReferencedInspectionsTable}">
							<af:column sortable="false" formatType="text"
								headerText="Inspection Id">
								<af:outputText value="#{insp}" />
							</af:column>
						</af:table>
					</afh:rowLayout>

					<f:facet name="footer">
						<af:panelButtonBar>
							<af:commandButton text="Yes" id="yesBtn"
								rendered="#{!monitorSiteDetail.cannotDelete}"
								action="#{monitorSiteDetail.deleteMonitorSite}" />
							<af:commandButton text="No" immediate="true" id="noBtn"
								rendered="#{!monitorSiteDetail.cannotDelete}"
								action="#{monitorSiteDetail.closeDialog}" />
							<af:commandButton text="Close" immediate="true"
								rendered="#{monitorSiteDetail.cannotDelete}"
								action="#{monitorSiteDetail.closeDialog}" />
						</af:panelButtonBar>
					</f:facet>
				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
</f:view>