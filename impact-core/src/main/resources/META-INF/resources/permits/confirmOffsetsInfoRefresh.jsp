<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Refresh Offset Tracking Information">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page>
				<af:panelForm>
					<af:objectSpacer width="100%" height="15" />
					<af:panelGroup layout="vertical">
						<af:outputFormatted
							rendered="#{permitDetail.permit.legacyPermit}"
							inlineStyle="color: orange; font-weight: bold;"
							value="<b>WARNING: This is a legacy permit.</b>" />
						<af:outputText
							value="Clicking yes will delete all existing offset tracking data associated with this permit. Would you like to continue?" />
					</af:panelGroup>

					<f:facet name="footer">
						<af:panelButtonBar>
							<af:commandButton text="Yes"
								action="#{permitDetail.regenerateOffsetTrackingEntries}" />
							<af:commandButton text="No"
								action="#{permitDetail.closeDialog}">
							</af:commandButton>
						</af:panelButtonBar>
					</f:facet>
				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
</f:view>

