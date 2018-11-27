<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="IMPACT Home">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:inputHidden value="#{submitTask.logUserOff}"
				rendered="#{!facilityProfile.internalApp}" />
			<af:inputHidden value="#{myTasks.fixedTabs}"
				rendered="#{!facilityProfile.internalApp}" />
			<af:page var="foo" value="#{menuModel.model}"
				title="IMPACT Company Selector">
				<jsp:include flush="true" page="util/header.jsp" />
				<f:subview id="facSelector">
					<h:panelGrid border="1" width="950"
						style="margin-left:auto; margin-right:auto;">

						<af:panelBorder>

							<af:panelHeader text="Account Information" size="0" />
							<af:panelForm rows="2" maxColumns="2" width="650">
								<af:inputText label="Name:" readOnly="True"
									value="#{myTasks.loginName}" />
							</af:panelForm>

							<af:objectSpacer width="100%" height="20" />

							<af:showDetailHeader text="Choose Company" disclosed="true">

								<af:panelGroup layout="vertical">
									<af:panelForm>
										<af:outputFormatted
											value="To manage a company's facilities, select its AQD Company ID from the following list of authorized companies. To return to the company selector from another page, press the Company Selector link in the top right corner." />
										<af:objectSpacer width="100%" height="5" />

										<af:table value="#{companyProfile.contactRolesWrapper}"
											binding="#{companyProfile.contactRolesWrapper.table}"
											bandingInterval="1" banding="row" var="contactRole" width="98%"
											rows="#{companyProfile.pageLimit}">

											<af:column sortProperty="c01" sortable="true" noWrap="true"
												formatType="text" headerText="AQD Company ID">
												<af:commandLink action="#{myTasks.goHome}"
													text="#{contactRole.company.cmpId}">
													<t:updateActionListener property="#{myTasks.currentRole}"
														value="#{contactRole}" />
												</af:commandLink>
											</af:column>

											<af:column sortProperty="c02" sortable="true"
												formatType="text" headerText="AQD Company Name">
												<af:outputText value="#{contactRole.company.name}" />
											</af:column>


											<af:column sortProperty="c03" sortable="true"
												formatType="text" headerText="CROMERR ID">
												<af:outputText value="#{contactRole.company.externalOrganization.organizationId}" />
											</af:column>

											<af:column sortProperty="c04" sortable="true"
												formatType="text" headerText="Access Level">
												<af:outputText value="#{contactRole.externalRole.roleName}" />
											</af:column>

											<f:facet name="footer">
												<afh:rowLayout halign="center">
													<af:panelButtonBar>
														<af:commandButton
															actionListener="#{tableExporter.printTable}"
															onclick="#{tableExporter.onClickScript}"
															text="Printable view" />
														<af:commandButton
															actionListener="#{tableExporter.excelTable}"
															onclick="#{tableExporter.onClickScript}"
															text="Export to excel" />
													</af:panelButtonBar>
												</afh:rowLayout>
											</f:facet>
										</af:table>
									</af:panelForm>
								</af:panelGroup>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</f:subview>
			</af:page>
		</af:form>
	</af:document>
</f:view>
