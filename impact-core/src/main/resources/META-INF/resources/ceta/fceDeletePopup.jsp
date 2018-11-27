<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="fceDelete" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Delete Inspection Confirmation">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="form">
				<af:panelForm>
					<af:messages />
					<afh:rowLayout halign="left">
						<af:outputFormatted
							value="#{fceDetail.cannotdeleteReason}" />
					</afh:rowLayout>
					<af:objectSpacer height="10" />
				<afh:rowLayout halign="center">
					<af:table id="complianceEvents_Table" emptyText=" " width="100%"
						value="#{fceDetail.fce.assocComplianceStatusEvents}"
						var="assoCse" bandingInterval="1" banding="row"
						rendered="#{fceDetail.displayComplianceEventsTable}">
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
				<afh:rowLayout halign="left">
						<af:outputFormatted rendered="#{fceDetail.afsWarning != null}"
							inlineStyle="color: orange; font-weight: bold;"
							value="#{fceDetail.afsWarning}" />
					</afh:rowLayout>
					<af:objectSpacer height="10" rendered="#{fceDetail.afsWarning != null}"/>
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Delete"
								disabled="#{fceDetail.cannotDelete}"
								action="#{fceDetail.deleteFCE2}" />
							<af:commandButton text="Cancel"
								action="#{fceDetail.close}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
		</af:form>
	</af:document>
</f:view>
