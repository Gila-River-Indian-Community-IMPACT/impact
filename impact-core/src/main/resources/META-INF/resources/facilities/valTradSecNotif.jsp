<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

			<afh:rowLayout halign="center">
				<h:panelGrid>
				    <afh:rowLayout halign="center">
						<af:outputFormatted value="<b>Trade Secret Claims Made</b>" />
					</afh:rowLayout>
					<af:objectSpacer height="5" width="100%" />
					<af:panelForm>
						<af:panelGroup layout="horizontal">
							<afh:cellFormat width="8%" />
							<afh:cellFormat halign="left" width="84%">
								<af:outputFormatted
									value="#{facilityProfile.validTradeSecretNotifMsg}" />
								<af:objectSpacer height="30" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="OK"
											action="#{facilityProfile.returnFromValidTradeSecretNotif}">
										</af:commandButton>
									</af:panelButtonBar>
								</afh:rowLayout>
							</afh:cellFormat>
							<afh:cellFormat width="8%" />
						</af:panelGroup>
					</af:panelForm>
				</h:panelGrid>
			</afh:rowLayout>
