<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<h:panelGrid border="1">
	<af:panelBorder>

		<f:facet name="top">
			<f:subview id="facilityHeader">
				<jsp:include page="comFacilityHeader2.jsp" />
			</f:subview>
		</f:facet>

		<f:facet name="right">
			<h:panelGrid columns="1" border="1"
				style="margin-left:auto;margin-right:auto;">
				<af:panelGroup layout="vertical">
					<af:objectSpacer height="10" />

					<af:panelForm>
						<af:table value="#{facilityProfile.facilityHistory}"
							bandingInterval="1" id="HistTab"
							partialTriggers="HistTab:SplitHistButton"
							binding="#{facilityProfile.historyTable}" banding="row"
							var="facility" width="98%">
							<af:column sortProperty="versionId" sortable="true"
								formatType="text" noWrap="true" headerText="Version ID" width="70px">
								<af:commandLink action="#{facilityProfile.submitHistoryProfile}">
									<af:outputText value="CURRENT"
										rendered="#{facility.versionId == -1}" />
									<af:outputText value="#{facility.fpId}"
										rendered="#{facility.versionId != -1}" />
									<af:setActionListener to="#{facilityProfile.fpId}"
										from="#{facility.fpId}" />
								</af:commandLink>
							</af:column>
							<af:column sortProperty="startDate" sortable="true" noWrap="true"
								formatType="text" headerText="Version Start Date" width="120px">
								<af:selectInputDate value="#{facility.startDate}"
									readOnly="true" />
							</af:column>
							<af:column sortProperty="endDate" sortable="true" noWrap="true"
								formatType="text" headerText="Version End Date" width="115px">
								<af:selectInputDate value="#{facility.endDate}" readOnly="true" />
							</af:column>
							<af:column sortProperty="copyOnChange" sortable="true"
								formatType="icon" headerText="Preserved">
								<af:selectBooleanCheckbox readOnly="true"
									value="#{facility.copyOnChange}" />
							</af:column>
							<f:facet name="footer">
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton actionListener="#{tableExporter.printTable}"
											onclick="#{tableExporter.onClickScript}"
											text="Printable view" />
										<af:commandButton actionListener="#{tableExporter.excelTable}"
											onclick="#{tableExporter.onClickScript}"
											text="Export to excel" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</f:facet>
						</af:table>
					</af:panelForm>
				</af:panelGroup>
			</h:panelGrid>
		</f:facet>

	</af:panelBorder>
</h:panelGrid>
