<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Send Email">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page>
				<af:messages />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" columns="1" width="400">
						<af:panelBorder>
							<af:panelHeader text="Send Email" size="0" />
							<af:panelForm>

								<af:objectSpacer height="20" />

								<af:inputText label="To: " readOnly="true"
									value="#{contactDetail.contact.emailAddressTxt}" columns="40"
									maximumLength="40" />

								<af:inputText label="Subject: " required="true"
									value="#{contactDetail.mailSubject}" columns="40"
									maximumLength="40" />

								<af:inputText label="Message: " required="true"
									value="#{contactDetail.mailMessage}" columns="40" rows="10"
									maximumLength="4000" />

								<af:objectSpacer height="20" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Send"
											action="#{contactDetail.sendEmail}" />
										<af:commandButton text="Cancel"
											action="#{contactDetail.cancelSendMail}" immediate="true" />
									</af:panelButtonBar>
								</afh:rowLayout>

							</af:panelForm>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
