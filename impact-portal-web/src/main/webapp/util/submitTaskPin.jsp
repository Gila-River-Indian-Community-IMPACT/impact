<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="#{submitTask.title}">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:messages />
			<af:page>
				<afh:rowLayout halign="center">
					<af:panelGroup>
					<af:panelForm
							rendered="#{!submitTask.completed}">
							<af:outputText
								value="Submission may take several minutes 
								depending on the amount of data being processed."/>
					        <af:objectSpacer height="20" />
							<af:outputFormatted value="#{submitTask.completeMessage}"
								inlineStyle="color: orange; font-weight: bold;" />
							<af:panelGroup rendered="#{not empty submitTask.documents}">
						        <af:inputText label="Username:"
						          value="#{submitTask.userId}" readOnly="true" columns="40"
						          maximumLength="50" />
						        <af:inputText label="Password:" secret="true"
						          value="#{submitTask.password}" columns="40"
						          maximumLength="50" required="true" />
						        <af:objectSpacer height="20" />
								<af:outputFormatted
									value="<b>Security Question:</b> #{submitTask.securityQuestion.questionText}"/>
						        <af:inputText label="Answer:" secret="true"
						          value="#{submitTask.userProvidedSecurityAnswer}" columns="40"
						          maximumLength="50" required="true" />
							</af:panelGroup>
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Submit"
										rendered="#{!submitTask.logUserOffFlag}"
										action="#{submitTask.submitTask}"
										onclick="return issubmited();return false;">
									</af:commandButton>
									<af:commandButton text="Cancel"
										rendered="#{!submitTask.logUserOffFlag}"
										action="#{submitTask.cancelGetQuestion}" immediate="true">
									</af:commandButton>
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>
					<af:panelForm rendered="#{submitTask.completed}">
							<af:outputFormatted value="#{submitTask.alertMessage}"
								inlineStyle="color: orange; font-weight: bold; font-weight: bold;" />
							<af:outputFormatted value="#{submitTask.completeMessage}" />
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Close" id="closeButton"
										action="#{submitTask.closeSubmitTask}">
									</af:commandButton>
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>
					</af:panelGroup>
				</afh:rowLayout>
			</af:page>
		</af:form>
		<f:verbatim>
			<script type="text/javascript">
				var submited = false;
				function issubmited() {
					if (submited) {
						return false;
					} else {
						submited = true;
						return true;
					}
				}
			</script>
		</f:verbatim>
	</af:document>
</f:view>