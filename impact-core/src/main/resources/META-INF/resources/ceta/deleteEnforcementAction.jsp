<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Delete Enforcement Action">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page>
				<afh:rowLayout halign="center">
					<h:panelGrid>
						<af:panelForm>
							<af:messages />
							<afh:rowLayout halign="center">    
								<af:outputFormatted rendered="#{!enforcementActionDetail.disableDelete}"
									value="Warning: this will delete the Enforcement Action and all events, attachments, and notes associated with it. Click <b>Delete</b> to remove the Enforcement Action or <b>Cancel</b> to cancel the request." 
									inlineStyle="color: orange; font-weight: bold;"/>
								<af:outputFormatted  rendered="#{enforcementActionDetail.disableDelete}"
									value="#{enforcementActionDetail.disableDeleteMsg}" />
							</afh:rowLayout>
							
					<af:objectSpacer height="10" />
							<af:objectSpacer width="10" height="6"/>
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Delete"
										disabled="#{enforcementActionDetail.disableDelete}"
										action="#{enforcementActionDetail.deleteEnforcementAction}" />
									<af:objectSpacer width="10" />
									<af:commandButton text="Cancel"
										action="#{enforcementActionDetail.closePopup}" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
