<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
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
						<af:table value="#{facilityProfile.emusWrapper}" width="98%"
							bandingInterval="1"
							binding="#{facilityProfile.emusWrapper.table}" banding="row"
							var="emissionUnit">
							<af:column sortProperty="c01" sortable="true" formatType="text" width="20px">
								<f:facet name="header">
									<af:outputText value="AQD ID" />
								</f:facet>
								<af:commandLink action="#{facilityProfile.submitEmissionUnit}">
									<af:outputText value="#{emissionUnit.epaEmuId}" />
									<t:updateActionListener property="#{facilityProfile.epaEmuId}"
										value="#{emissionUnit.epaEmuId}" />
								</af:commandLink>
							</af:column>
							<af:column sortProperty="c02" sortable="true" formatType="text">
								<f:facet name="header">
									<af:outputText value="AQD Description" />
								</f:facet>
								<af:outputText value="#{emissionUnit.euDesc}" truncateAt="80" 
								shortDesc="#{emissionUnit.euDesc}"/>
							</af:column>
							<af:column sortProperty="c03" sortable="true" formatType="text" width="60px" noWrap="true">
								<f:facet name="header">
									<af:outputText value="Company Equipment ID" />
								</f:facet>
								<af:outputText value="#{emissionUnit.companyId}" />
							</af:column>
							<af:column sortProperty="c04" sortable="true" formatType="text">
								<f:facet name="header">
									<af:outputText value="Company Equipment Description" />
								</f:facet>
								<af:outputText value="#{emissionUnit.regulatedUserDsc}"
									truncateAt="80" 
									shortDesc="#{emissionUnit.regulatedUserDsc}"/>
							</af:column>
							<af:column sortProperty="c05" sortable="true" formatType="text" width="65px" noWrap="true">
								<f:facet name="header">
									<af:outputText value="Emissions Unit Type" />
								</f:facet>
								<af:outputText value="#{emissionUnit.emissionUnitTypeName}"
									truncateAt="80" 
									shortDesc="#{emissionUnit.emissionUnitTypeName}"/>
							</af:column>
							<af:column sortProperty="c06" sortable="true" formatType="text" width="60px" noWrap="true">
								<f:facet name="header">
									<af:outputText value="Operating Status" />
								</f:facet>
								<af:outputText
									value="#{facilityReference.euOperatingStatusDefs.itemDesc[(empty emissionUnit.operatingStatusCd ? '' : emissionUnit.operatingStatusCd)]}" />
							</af:column>
							<af:column sortProperty="c07" sortable="true" formatType="text" width="75px">
								<f:facet name="header">
									<af:outputText value="Completion of Initial Installation Date" />
								</f:facet>
								<af:selectInputDate value="#{emissionUnit.euInitInstallDate}"
									readOnly="true" />
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
					<afh:rowLayout halign="center">
						<af:panelButtonBar rendered="#{!facilityProfile.publicApp}">
							<af:commandButton id="hideInvalidEUsBtn" text="Hide Invalid EUs"
								rendered="#{!facilityProfile.hideInvalidEUs}"
								action="#{facilityProfile.toggleEUList}">
								<t:updateActionListener
									property="#{facilityProfile.hideInvalidEUs}" value="true" />
							</af:commandButton>
							<af:commandButton id="showInvalidEUsBtn" text="Show Invalid EUs"
								rendered="#{facilityProfile.hideInvalidEUs}"
								action="#{facilityProfile.toggleEUList}">
								<t:updateActionListener
									property="#{facilityProfile.hideInvalidEUs}" value="false" />
							</af:commandButton>
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelGroup>
			</h:panelGrid>
		</f:facet>
	</af:panelBorder>
</h:panelGrid>