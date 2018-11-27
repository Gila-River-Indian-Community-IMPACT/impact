<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" rendered="true">
	<af:showDetailHeader text="#{areaEmissionsOffset.sectionLabel}" disclosed="true">
		<h:panelGrid columns="1" border="1"
			width="1000">
			
			<af:selectOneChoice label="Non-Attainment standard: "
				readOnly="true" value="#{areaEmissionsOffset.attainmentStandardCd}">
				<f:selectItems
					value="#{permitReference.offsetTrackingAttainmentStandardDefs.items[(empty areaEmissionsOffset.attainmentStandardCd ? '' : areaEmissionsOffset.attainmentStandardCd)]}" />
			</af:selectOneChoice>

			<af:panelGroup>
				<afh:rowLayout halign="center">
					<af:table id="emissionsOffsets" 
						value="#{areaEmissionsOffset.emissionsOffsetRowList}"
						bandingInterval="1" banding="row" 
						var="emissionsOffset" 
						rows="#{companyProfile.pageLimit}" emptyText=" "
						width="995">
						
						<af:column id="facilityId" headerText="Facility ID" width="5%"
							sortProperty="facilityId" sortable="true" formatType="text">
							<af:panelHorizontal valign="middle" halign="left">
								<af:commandLink text="#{emissionsOffset.facilityId}"
									action="#{facilityProfile.submitProfile}"
									inlineStyle="white-space: nowrap;">
									<t:updateActionListener property="#{facilityProfile.fpId}"
										value="#{emissionsOffset.fpId}" />
									<t:updateActionListener property="#{menuItem_facProfile.disabled}"
										value="false" />
								</af:commandLink>
							</af:panelHorizontal>
						</af:column>
						
						<af:column id="facilityName" headerText="Facility Name" width="12%"
							sortProperty="facilityName" sortable="true" formatType="text">
							<af:panelHorizontal valign="middle" halign="left">
								<af:inputText
									label="Facility Name" readOnly="true" value="#{emissionsOffset.facilityName}" />
							</af:panelHorizontal>
						</af:column>
						
						<af:column id="permitNumber" headerText="Permit Number" width="5%"
							sortProperty="permitNumber" sortable="true" formatType="text">
							<af:panelHorizontal valign="middle" halign="left">
								<af:commandLink text="#{emissionsOffset.permitNumber}"
									action="#{permitDetail.loadPermit}">
									<af:setActionListener to="#{permitDetail.permitID}"
										from="#{emissionsOffset.permitId}" />
									<t:updateActionListener property="#{permitDetail.fromTODOList}"
										value="false" />
								</af:commandLink>
							</af:panelHorizontal>
						</af:column>
						
						<af:column id="permitFinalIssuanceDate" headerText="Permit Final Issuance Date" width="8%"
							sortProperty="permitFinalIssuanceDate" sortable="true" formatType="text">
							<af:panelHorizontal valign="middle" halign="left">
								<af:selectInputDate
									label="Permit Final Issuance Date" readOnly="true" 
									value="#{emissionsOffset.permitFinalIssuanceDate}" />
							</af:panelHorizontal>
						</af:column>
						
						<af:column id="pollutantCd" headerText="Pollutant" width="25%"
							sortable="true" sortProperty="pollutantCd" formatType="text">
								<af:panelHorizontal valign="middle" halign="left">		
									<af:selectOneChoice label="Pollutant" readOnly="true"
										value="#{emissionsOffset.pollutantCd}">
										<f:selectItems
											value="#{facilityReference.pollutantDefs.items[(empty emissionsOffset.pollutantCd ? '' : emissionsOffset.pollutantCd)]}" />
									</af:selectOneChoice>
							</af:panelHorizontal>
						</af:column>

						<af:column id="currentOffset" headerText="Current (Tons)" width="5%"
							sortable="true" sortProperty="currentOffset" formatType="number">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText
									label="Current (Tons)" 
									readOnly="true"
									value="#{emissionsOffset.currentOffset}">
									<af:convertNumber type="number" pattern="###,###.###" minFractionDigits="3"/>
								</af:inputText>	
							</af:panelHorizontal>
						</af:column>

						<af:column id="baseOffset" headerText="Base (Tons)" width="5%"
							sortProperty="baseOffset" sortable="true" formatType="number">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText
									label="Base (Tons)" readOnly="true"
									value="#{emissionsOffset.baseOffset}">
									<af:convertNumber type="number" pattern="###,###.###" minFractionDigits="3"/>
								</af:inputText>	
							</af:panelHorizontal>
						</af:column>

						<af:column id="delta" headerText="Delta (Tons)" width="5%"
							sortProperty="delta" sortable="true" formatType="number">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText
									label="Delta (Tons)" readOnly="true" value="#{emissionsOffset.delta}" >
									<af:convertNumber type="number" pattern="###,###.###" minFractionDigits="3"/>
								</af:inputText>	
							</af:panelHorizontal>
						</af:column>
						
						<af:column id="emissionsReductionMultiple" headerText="Emissions Reduction Multiplier"
							width="8%" sortProperty="emissionsReductionMultiple" sortable="true"
							formatType="number">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText
									label="Emissions Reduction Multiplier" readOnly="true"
									value="#{emissionsOffset.emissionsReductionMultiple}" >
									<af:convertNumber type="number" pattern="#######.##" minFractionDigits="2"/>
								</af:inputText>	
							</af:panelHorizontal>
						</af:column>

						<af:column id="offsetMultiple" headerText="Emissions Increase Multiplier"
							width="8%" sortProperty="offsetMultiple" sortable="true"
							formatType="number">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText
									label="Emissions Increase Multiplier" readOnly="true"
									value="#{emissionsOffset.offsetMultiple}" >
									<af:convertNumber type="number" pattern="#######.##" minFractionDigits="2"/>
								</af:inputText>	
							</af:panelHorizontal>
						</af:column>

						<af:column id="offsetAmount" headerText="Offset Used(+) or Generated(-) (Tons)" width="5%"
							sortProperty="offsetAmount" sortable="true" formatType="number">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText
									label="Offset Used(+) or Generated(-) (Tons)" readOnly="true"
									value="#{emissionsOffset.offsetAmount}" >
									<af:convertNumber type="number" pattern="#,###,###.###" 
										minFractionDigits="3" maxFractionDigits="3"/>
								</af:inputText>	
							</af:panelHorizontal>
						</af:column>

						<af:column id="comment" headerText="Comment" width="5%"
							sortable="true" sortProperty="comment" formatType="text">
							<af:panelHorizontal valign="middle" halign="left">
								<af:outputText
									truncateAt="20"
									value="#{emissionsOffset.comment}" 
									shortDesc="#{emissionsOffset.comment}"/>
							</af:panelHorizontal>
						</af:column>
						 
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelGroup>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</afh:rowLayout>

				<af:objectSpacer height="10" />

			</af:panelGroup>
		</h:panelGrid>
	</af:showDetailHeader>
</af:panelGroup>


