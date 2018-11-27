<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="PER Overdue Report">
		<f:facet name="metaContainer">
			<f:verbatim>
				<h:outputText value="#{facilityProfile.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" />
    <f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="PER Overdue Report">
				<%@ include file="../util/header.jsp"%>
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="600">
						<af:panelBorder>
							<af:showDetailHeader text="PER Overdue Report Filter"
								disclosed="true">
								<af:panelForm rows="1">
									<af:selectOneChoice label="County"
										value="#{perOverdueReport.countyCd}" unselectedLabel="">
										<f:selectItems value="#{infraDefs.counties}" />
									</af:selectOneChoice>

									<af:selectOneChoice label="District"
										value="#{perOverdueReport.doLaaCd}" unselectedLabel="">
										<f:selectItems value="#{facilitySearch.doLaas}" />
									</af:selectOneChoice>
								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit" rendered="#{perOverdueReport.renderSubmit}"
											action="#{perOverdueReport.submit}" />
										<af:commandButton text="Reset"
											action="#{perOverdueReport.reset}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
				
				<af:panelGroup layout="vertical"
					rendered="#{perOverdueReport.showProgressBar}">
					<af:progressIndicator id="progressid"
						value="#{perOverdueReport}">
						<af:outputFormatted value="Searching for overdue PER reports. " />
					</af:progressIndicator>
				</af:panelGroup>

				<af:objectSpacer width="100%" height="15" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1100"
						rendered="#{perOverdueReport.hasResults}">
						<af:panelBorder>

							<af:showDetailHeader text="PER Overdue Report" disclosed="true">
								<af:table bandingInterval="2" banding="row" var="details"
									emptyText='No results found.'
									rows="#{perOverdueReport.pageLimit}" width="99%"
									value="#{perOverdueReport.details}">

									<af:column headerText="Facility Name" bandingShade="light">
										<af:column sortable="false" noWrap="true">
											<af:inputText readOnly="true" value="#{details.facilityName}" />
										</af:column>
									</af:column>

									<af:column headerText="Facility Id">
										<af:inputText readOnly="true" value="#{details.facilityId}" />
									</af:column>

									<af:column headerText="EU Id">
										<af:inputText readOnly="true" value="#{details.epaEmuId}" />
									</af:column>

									<af:column headerText="EU Description">
										<af:outputText value="#{details.euDescription}" />
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
							</af:showDetailHeader>

						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

			</af:page>
		</af:form>
	</af:document>
</f:view>
