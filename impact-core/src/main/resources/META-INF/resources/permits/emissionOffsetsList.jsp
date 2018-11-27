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
			width="#{permitDetail.permitWidth}">
			
			<af:selectOneChoice label="Non-Attainment standard: "
				readOnly="true" value="#{areaEmissionsOffset.attainmentStandardCd}">
				<f:selectItems
					value="#{permitReference.offsetTrackingAttainmentStandardDefs.items[(empty areaEmissionsOffset.attainmentStandardCd ? '' : areaEmissionsOffset.attainmentStandardCd)]}" />
			</af:selectOneChoice>

			<af:panelGroup>
				<afh:rowLayout halign="center">
					<af:table id="emissionsOffsets" 
						value="#{areaEmissionsOffset.emissionsOffsets}"
						bandingInterval="1" banding="row" 
						width="#{permitDetail.permitTableWidth}"
						var="emissionsOffset" 
						rows="#{permitDetail.pageLimit}" emptyText=" ">

						<af:column id="pollutantCd" headerText="Pollutant" width="5%"
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
									label="Current (Tons)" readOnly="#{!permitDetail.editMode}"
									columns="10" maximumLength="10"
									value="#{emissionsOffset.currentOffset}">
									<af:convertNumber type="number" pattern="###,###.###" minFractionDigits="3"/>
								</af:inputText>	
							</af:panelHorizontal>
						</af:column>

						<af:column id="baseOffset" headerText="Base (Tons)" width="5%"
							sortProperty="baseOffset" sortable="true" formatType="number">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText
									label="Base (Tons)" readOnly="#{!permitDetail.editMode}"
									columns="10" maximumLength="10"
									value="#{emissionsOffset.baseOffset}">
									<af:convertNumber type="number" pattern="###,###.###" minFractionDigits="3" />
								</af:inputText>	
							</af:panelHorizontal>
						</af:column>

						<af:column id="delta" headerText="Delta (Tons)" width="5%"
							sortProperty="delta" sortable="true" formatType="number">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText
									label="Delta (Tons)" readOnly="true" value="#{emissionsOffset.delta}">
									<af:convertNumber type="number" pattern="###,###.###" minFractionDigits="3"/>
								</af:inputText>		
							</af:panelHorizontal>
						</af:column>
						
						<af:column id="emissionsReductionMultiple" headerText="Emissions Reduction Multiplier"
							width="5%" sortProperty="emissionsReductionMultiple" sortable="true"
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
							width="5%" sortProperty="offsetMultiple" sortable="true"
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
									rendered="#{!permitDetail.editMode}"
									truncateAt="40"
									value="#{emissionsOffset.comment}" 
									shortDesc="#{emissionsOffset.comment}"/>
								<af:inputText
									rendered="#{permitDetail.editMode}"
									rows="3" columns="100" maximumLength="200"
									label="Comment" readOnly="#{!permitDetail.editMode}"
									value="#{emissionsOffset.comment}" />
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


