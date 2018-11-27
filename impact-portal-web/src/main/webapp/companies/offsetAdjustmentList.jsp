<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" rendered="true">
	<af:showDetailHeader text="Emissions Offset Adjustments" disclosed="true">
		<h:panelGrid columns="1" border="1"
			width="1000">
			
			<af:panelGroup>
				<afh:rowLayout halign="center">
					<af:table id="offsetAdjustments" 
						value="#{companyProfile.emissionsOffsetAdjustmentWrapper}"
						binding="#{companyProfile.emissionsOffsetAdjustmentWrapper.table}"
						bandingInterval="1" banding="row" 
						var="offsetAdjustment" varStatus="offsetAdjustmentTableVs" 
						rows="#{companyProfile.pageLimit}" emptyText=" "
						width="995">
						
						<af:column id="rowId" formatType="text" headerText="Row Id"
							width="5%">
							<af:panelHorizontal valign="middle" halign="left">
								<af:inputText value="#{offsetAdjustmentTableVs.index+1}"
									readOnly="true" valign="middle">
									<af:convertNumber pattern="000" />
								</af:inputText>
							</af:panelHorizontal>
						</af:column>
						
						<af:column id="nonAttainmentAreaCd" headerText="Non-Attainment Area" width="12%"
							sortProperty="nonAttainmentAreaCd" sortable="true" formatType="text">
							<af:panelHorizontal valign="middle" halign="left">
								<af:selectOneChoice label="Non-Attainment Area"
									readOnly="true"
										value="#{offsetAdjustment.nonAttainmentAreaCd}">
									<f:selectItems
										value="#{permitReference.offsetTrackingNonAttainmentAreaDefs.items[(empty offsetAdjustment.nonAttainmentAreaCd ? '' : offsetAdjustment.nonAttainmentAreaCd)]}" />
								</af:selectOneChoice>
							</af:panelHorizontal>
						</af:column>
						
						<af:column id="date" headerText="Date" width="8%"
							sortProperty="date" sortable="true" formatType="text">
							<af:panelHorizontal valign="middle" halign="left">
								<af:selectInputDate label="Date"
									readOnly="true"
									value="#{offsetAdjustment.date}" />
							</af:panelHorizontal>
						</af:column>
						
						<af:column id="pollutantCd" headerText="Pollutant" width="25%"
							sortable="true" sortProperty="pollutantCd" formatType="text">
								<af:panelHorizontal valign="middle" halign="left">		
									<af:selectOneChoice label="Pollutant"
										readOnly="true"
										value="#{offsetAdjustment.pollutantCd}">
										<f:selectItems
											value="#{facilityReference.pollutantDefs.items[(empty offsetAdjustment.pollutantCd ? '' : offsetAdjustment.pollutantCd)]}" />
									</af:selectOneChoice>
							</af:panelHorizontal>
						</af:column>

						<af:column id="amount" headerText="Offset Used(+) or Generated(-) (Tons)" width="5%"
							sortProperty="amount" sortable="true" formatType="number">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText
									label="Offset Used(+) or Generated(-) (Tons)"
									readOnly="true"
									value="#{offsetAdjustment.amount}" >
									<af:convertNumber type="number" pattern="###,###.###" 
										minFractionDigits="3" maxFractionDigits="3" />
								</af:inputText>	
							</af:panelHorizontal>
						</af:column>

						<af:column id="comment" headerText="Comment" width="5%"
							sortProperty="comment" sortable="true" formatType="text">
							<af:panelHorizontal valign="middle" halign="left">
								<af:outputText 
									truncateAt="20"
									value="#{offsetAdjustment.comment}" 
									shortDesc="#{offsetAdjustment.comment}"/>
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