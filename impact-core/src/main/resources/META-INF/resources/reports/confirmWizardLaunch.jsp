<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Confirm Wizard Launch">
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
							inlineStyle="color: orange; font-weight: bold;"
							value="<b>WARNING:</b>" />
						<af:objectSpacer width="100%" height="10" />
						<af:outputText
							value="You are launching the data entry wizard which will clear all the data that has been previously entered for this emissions inventory. Would you like to proceed?" />
						<af:objectSpacer width="100%" height="10" />	
					</af:panelGroup>

					<f:facet name="footer">
						<af:panelButtonBar>
							<af:commandButton id="yesBtn" text="Yes" 
								useWindow="true" windowWidth="1400"	windowHeight="800"
								returnListener="#{reportProfile.closeWizardAndRefresh}"
								action="#{reportProfile.launchWizard}" />
							<af:commandButton id="noBtn" text="No" 
								action="#{reportProfile.closeDialog}">
							</af:commandButton>
						</af:panelButtonBar>
					</f:facet>
				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
</f:view>

