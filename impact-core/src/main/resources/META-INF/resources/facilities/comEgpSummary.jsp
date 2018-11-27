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
						<af:table value="#{facilityProfile.egpsWrapper}" width="98%"
							bandingInterval="1"
							binding="#{facilityProfile.egpsWrapper.table}" banding="row"
							var="egressPoint">
							<af:column sortProperty="c01" sortable="true" formatType="text"
								headerText="AQD ID" width="50px">
								<af:commandLink action="#{facilityProfile.submitEgressPoint}">
									<af:outputText value="#{egressPoint.releasePointId}" />
									<t:updateActionListener property="#{facilityProfile.egrPntId}"
										value="#{egressPoint.releasePointId}" />
									<t:updateActionListener
										property="#{facilityProfile.egrPointsEuIds}"
										value="#{egressPoint.associatedEpaEuIds}" />
								</af:commandLink>
							</af:column>
							<af:column sortProperty="c02" sortable="true" formatType="text"
								headerText="AQD Description">
								<af:outputText value="#{egressPoint.dapcDesc}" />
							</af:column>
							<af:column sortProperty="c03" sortable="true" formatType="text"
								headerText="Company Release Point ID" noWrap="true" width="60px">
								<af:outputText value="#{egressPoint.egressPointId}" />
							</af:column>
							<af:column sortProperty="c04" sortable="true" formatType="text"
								headerText="Company Release Point Description">
								<af:outputText value="#{egressPoint.regulatedUserDsc}" />
							</af:column>
							<af:column sortProperty="c05" sortable="true" formatType="text"
								headerText="Release Point Type" width=" 90px">
								<af:outputText
									value="#{facilityReference.egrPntTypeDefs.itemDesc[(empty egressPoint.egressPointTypeCd ? '' : egressPoint.egressPointTypeCd)]}" />
							</af:column>
							<af:column sortProperty="c06" sortable="true" formatType="text"
								headerText="Operating Status" width="60px" noWrap="true">
								<af:outputText
									value="#{facilityReference.egOperatingStatusDefs.itemDesc[(empty egressPoint.operatingStatusCd ? '' : egressPoint.operatingStatusCd)]}" />
							</af:column>
							<af:column sortProperty="c07" sortable="true" formatType="icon"
								headerText="CEM(s) Present" rendered="false">
								<af:selectBooleanCheckbox value="#{egressPoint.cemPresent}"
									readOnly="true" />
							</af:column>
							<af:column sortProperty="c08" sortable="true" formatType="text"
								headerText="Associated AQD Emissions Unit IDs" width="180px">
								<af:outputText value="#{egressPoint.associatedEpaEuIds}" />
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
