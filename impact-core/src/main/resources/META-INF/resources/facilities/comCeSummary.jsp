<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<h:panelGrid border="1">
	<af:panelBorder>

		<f:facet name="top">
			<f:subview id="facilityHeader">
				<jsp:include page="comFacilityHeader.jsp" />
			</f:subview>
		</f:facet>

		<f:facet name="right">
			<h:panelGrid columns="1" border="1"
				style="margin-left:auto;margin-right:auto;">
				<af:panelGroup layout="vertical">
					<af:objectSpacer height="10" />
					<af:panelForm>
						<af:table value="#{facilityProfile.cesWrapper}" width="98%"
							bandingInterval="1" binding="#{facilityProfile.cesWrapper.table}"
							banding="row" var="controlEquip">
							<af:column sortProperty="c01" sortable="true" formatType="text"
								headerText="AQD ID" noWrap="true" width="50px">
								<af:commandLink action="#{facilityProfile.submitControlEquip}">
									<af:outputText value="#{controlEquip.controlEquipmentId}" />
									<t:updateActionListener
										property="#{facilityProfile.cntEquipId}"
										value="#{controlEquip.controlEquipmentId}" />
									<t:updateActionListener
										property="#{facilityProfile.contEquipsEpaEuIds}"
										value="#{controlEquip.associatedEpaEuIds}" />
								</af:commandLink>
							</af:column>
							<af:column sortProperty="c02" sortable="true" formatType="text"
								headerText="AQD Description" width="25%">
								<af:outputText value="#{controlEquip.dapcDesc}" />
							</af:column>
							<af:column sortProperty="c03" sortable="true" formatType="text"
								headerText="Company Control Equipment ID" width="115px">
								<af:outputText value="#{controlEquip.companyId}" />
							</af:column>
							<af:column sortProperty="c04" sortable="true" formatType="text"
								headerText="Company Control Equipment Description">
								<af:outputText value="#{controlEquip.regUserDesc}" />
							</af:column>
							<af:column sortProperty="c05" sortable="true" formatType="text"
								headerText="Control Equipment Type" width="125px" noWrap="true">
								<af:outputText
									value="#{facilityReference.contEquipTypeDefs.itemDesc[(empty controlEquip.equipmentTypeCd ? '' : controlEquip.equipmentTypeCd)]}" />
							</af:column>
							<af:column sortProperty="c06" sortable="true" formatType="text"
								headerText="Operating Status" width="60px">
								<af:outputText
									value="#{facilityReference.ceOperatingStatusDefs.itemDesc[(empty controlEquip.operatingStatusCd ? '' : controlEquip.operatingStatusCd)]}" />
							</af:column>
							<af:column sortProperty="c07" sortable="true" formatType="text"
								headerText="Initial Installation Date" width="75px">
								<af:selectInputDate value="#{controlEquip.contEquipInstallDate}"
									readOnly="true" />
							</af:column>
							<af:column sortProperty="c08" sortable="true" formatType="text"
								headerText="Associated AQD Emissions Unit IDs" width="180px">
								<af:outputText value="#{controlEquip.associatedEpaEuIds}" />
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
