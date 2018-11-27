<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document id="body" title="#{submitTask.title}">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<afh:rowLayout halign="center">
				<h:panelGrid>
					<af:panelForm>
						<af:inputHidden value="#{submitTask.logUserOff}"
							rendered="#{!facilityProfile.internalApp}" />
						<af:outputFormatted value="<b>#{submitTask.title}</b>"
							rendered="#{submitTask.prettyTitle}" />
						<af:outputFormatted
							value="This login is not recognized as the Responsible Official for this facility. 
							Before continuing with the Submit operation, an attestation file bearing the 
							signature of the Responsible Official for this facility must be uploaded below. 
							Once the file is uploaded, a company employee who is not a Responsible Official 
							may then submit using her/his own PIN and security question." />
						<af:inputFile label="Attestation File : " id="attestationFile"
							rendered="#{submitTask.uploadFileInfo == null}"
							value="#{submitTask.uploadFile}" />
						<af:inputText id="publicFileName" label="Attestation File : "
							rendered="#{submitTask.uploadFileInfo != null}"
							value="#{submitTask.uploadFileInfo.fileName}" readOnly="true" />
						<af:objectSpacer height="30" />
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton text="Save Document" id="submitButton"
				                  rendered="#{submitTask.uploadFileInfo == null}"
								  action="#{submitTask.saveAttestationDoc}"/>
				                <af:commandButton text="Submit"
				                  rendered="#{submitTask.uploadFileInfo != null}"
				                  action="#{submitTask.processConfirmAttestationDoc}"
				                  returnListener="#{submitTask.returnSelected}"
				                  useWindow="true" windowWidth="#{submitTask.width}"
				              	  windowHeight="#{submitTask.height + 150}"/>
								<af:commandButton text="Reset"
									rendered="#{!submitTask.completed}"
									action="#{submitTask.reset}"/>
								<af:commandButton text="Cancel"
									action="#{submitTask.cancelSaveAttestationDoc}" immediate="true"/>
							</af:panelButtonBar>
						</afh:rowLayout>
					</af:panelForm>
				</h:panelGrid>
			</afh:rowLayout>
		</af:form>
	</af:document>
</f:view>