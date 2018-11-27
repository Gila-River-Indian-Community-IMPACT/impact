<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>



<h:panelGrid border="1" title="Emissions Inventory" width="1500">
	<af:panelGroup layout="vertical">
		<af:panelHeader text="Emissions Inventory: " size="0" />
		<af:showDetailHeader text="Emissions Inventory List" size ="0" disclosed="true">
			<af:outputLabel value="Emissions Inventory Received Date" />
			<af:panelForm rows="1" maxColumns="2" width="50%" >
				<afh:rowLayout halign="left" >
					<af:selectInputDate id="eiStartDate" label="Start Date: " autoSubmit="true"  
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangeEI.startDt}" valueChangeListener="#{fceDetail.eiDateRangeChanged}">
						<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
					<af:objectSpacer width="20" />
					<af:selectInputDate id="eiEndDate" label="End Date: " autoSubmit="true" 
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangeEI.endDt}" valueChangeListener="#{fceDetail.eiDateRangeChanged}" >
						<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
			</af:panelForm>
			<af:objectSpacer height="10" />
			<af:panelButtonBar>
				<af:commandButton id="search" text="Search"
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.searchEI}">
				</af:commandButton>
				<af:commandButton id="reset" text="Reset" immediate="true"
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.resetEI}">
				</af:commandButton>
				<af:commandButton id="set" text="Set Preserved List" partialTriggers="eiStartDate eiEndDate"
					rendered="true" disabled="#{fceDetail.dateRangeChangeEI || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.setPreservedListEI}">
				</af:commandButton>
				<af:commandButton id="recall" text="Recall Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedEI || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.recallPreservedListEI}">
				</af:commandButton>
				<af:commandButton id="clear" text="Clear Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedEI || fceDetail.inspectionReadOnly}"
					action="#{confirmWindow.confirm}"
					useWindow="true" windowWidth="600" windowHeight="300" >
					<t:updateActionListener property="#{confirmWindow.type}"
						value="#{confirmWindow.yesNo}" />
					<t:updateActionListener property="#{confirmWindow.method}"
						value="fceDetail.clearPreservedListEI" />
					<t:updateActionListener property="#{confirmWindow.message}"
						value="Do you want to Clear the Preserved List? Click Yes to proceed or No to cancel" />
					<%-- temporarily set this flag to true so that the start and end dates can refresh --%>
					<t:updateActionListener value="true" property="#{fceDetail.snaphotSearchDatesReadOnly}" />
				</af:commandButton>										
			</af:panelButtonBar>
			<af:objectSpacer height="10" />																
	
			<af:table id="fceEmissionsInventoryTable" width="98%" partialTriggers="startDate reset"
				value="#{fceDetail.emissionsInventoryWrapper}" binding="#{fceDetail.emissionsInventoryWrapper.table}"
				bandingInterval="1" banding="row" var="eiReport" rows="0">
				<af:column id="emissionsInventoryId" headerText="Inventory ID"
					sortable="true" sortProperty="emissionsInventoryId" formatType="text">
					<af:commandLink action="#{reportDetail.submit}">
						<af:outputText value="#{eiReport.emissionsInventoryId}" />
						<t:updateActionListener property="#{reportDetail.reportId}"
							value="#{eiReport.emissionsRptId}" />
						<t:updateActionListener property="#{reportDetail.fromTODOList}"
							value="false" />
						<t:updateActionListener property="#{menuItem_TVDetail.disabled}"
							value="false" />
					</af:commandLink>
				</af:column>

				<af:column id="emissionsInventoryModifiedId" headerText="Previous Inventory"
					sortable="true" sortProperty="emissionsInventoryModifiedId" formatType="text" width="60px">
					<af:commandLink action="#{reportDetail.submit}">
						<af:outputText value="#{eiReport.emissionsInventoryModifiedId}" />
						<t:updateActionListener property="#{reportDetail.reportId}"
							value="#{eiReport.reportModified}" />
						<t:updateActionListener property="#{reportDetail.fromTODOList}"
							value="false" />
						<t:updateActionListener property="#{menuItem_TVDetail.disabled}"
							value="false" />
					</af:commandLink>
				</af:column>
					
				<af:column id="fpId" headerText="Facility History ID"
					sortable="true" sortProperty="fpId" formatType="text" noWrap="true" width="65px">
					<af:commandLink action="#{facilityProfile.submitProfile}"
						rendered="#{eiReport.versionId == -1}">
						<af:outputText value="Current (#{eiReport.fpId})" />
						<t:updateActionListener property="#{facilityProfile.fpId}"
							value="#{eiReport.fpId}" />
					</af:commandLink>
					<af:commandLink action="#{facilityProfile.submitProfile}"
						rendered="#{eiReport.versionId != -1}">
						<af:outputText value="#{eiReport.fpId}" />
						<t:updateActionListener property="#{facilityProfile.fpId}"
							value="#{eiReport.fpId}" />
					</af:commandLink>
				</af:column>
				
				<af:column id="year" headerText="Year"
					sortable="true" sortProperty="year" formatType="text">
					<af:outputText value="#{eiReport.reportYear}" />
				</af:column>
				
				<af:column id="contentTypeCd" headerText="Content Type" 
					sortable="true" sortProperty="contentTypeCd" formatType="text">
					<af:selectOneChoice value="#{eiReport.contentTypeCd}" readOnly="true">
						<f:selectItems value="#{facilityReference.contentTypeDefs.allSearchItems}" />
					</af:selectOneChoice>
				</af:column>
				
				<af:column id="regulatoryRequirementCds" headerText="Regulatory Requirements"
					sortable="true" sortProperty="regulatoryRequirementCdsString" formatType="text">
					<af:outputText value="#{eiReport.regulatoryRequirementCdsString}" />
				</af:column>
				
				<af:column id="reportingState" headerText="Reporting State"
					sortable="true" sortProperty="reportingState" formatType="text">
					<af:selectOneChoice value="#{eiReport.reportingStateCd}" readOnly="true">
						<f:selectItems value="#{facilityReference.reportOfEmissionsStateDefs.items[empty eiReport.reportingStateCd ? '' : eiReport.reportingStateCd]}" />
					</af:selectOneChoice>	
				</af:column>

				<af:column id="receivedDate" headerText="Received Date" 
					sortable="true" sortProperty="receivedDate" formatType="text" width="60px">
					<af:selectInputDate value="#{eiReport.receivedDate}" readOnly="true"  />
				</af:column>
				
				<af:column id="emissionsPM" headerText="PM Primary"
					sortable="true" sortProperty="floatEmissionsPM" formatType="number">
					<af:outputText value="#{eiReport.emissionsPM}" >
					</af:outputText>
				</af:column>				

				<af:column id="emissionsPM10" headerText="PM10 Primary"
					sortable="true" sortProperty="floatEmissionsPM10" formatType="number">
					<af:outputText value="#{eiReport.emissionsPM10}" >
					</af:outputText>
				</af:column>							

				<af:column id="emissionsPM25" headerText="PM2.5 Primary"
					sortable="true" sortProperty="floatEmissionsPM25" formatType="number">
					<af:outputText value="#{eiReport.emissionsPM25}">
					</af:outputText>
				</af:column>				
				
				<af:column id="emissionsCO" headerText="CO"
					sortable="true" sortProperty="floatEmissionsCO" formatType="number">
					<af:outputText value="#{eiReport.emissionsCO}">
					</af:outputText>
				</af:column>				
				
				<af:column id="emissionsNOx" headerText="NOx"
					sortable="true" sortProperty="floatEmissionsNOx" formatType="number">
					<af:outputText value="#{eiReport.emissionsNOx}">
					</af:outputText>
				</af:column>								

				<af:column id="emissionsSO2" headerText="SO2"
					sortable="true" sortProperty="floatEmissionsSO2" formatType="number">
					<af:outputText value="#{eiReport.emissionsSO2}">
					</af:outputText>
				</af:column>				
				
				<af:column id="emissionsVOC" headerText="VOC"
					sortable="true" sortProperty="floatEmissionsVOC" formatType="number">
					<af:outputText value="#{eiReport.emissionsVOC}">
					</af:outputText>
				</af:column>				

				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</af:showDetailHeader>
	</af:panelGroup>
</h:panelGrid>







