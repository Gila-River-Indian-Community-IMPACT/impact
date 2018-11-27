<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>



<h:panelGrid border="1" title="Applications" width="1500">
	<af:panelGroup layout="vertical">
		<af:panelHeader text="Applications: " size="0" />
		<af:showDetailHeader text="Application List" size ="0" disclosed="true">
			<af:outputLabel value="Application Received Date" />
			<af:panelForm rows="1" maxColumns="2" width="50%" partialTriggers="startDate">
				<afh:rowLayout halign="left" >
					<af:selectInputDate id="paStartDate" label="Start Date: " autoSubmit="true"  
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangePA.startDt}" valueChangeListener="#{fceDetail.paDateRangeChanged}">
						<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
					<af:objectSpacer width="20" />
					<af:selectInputDate id="paEndDate" label="End Date: " autoSubmit="true" 
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangePA.endDt}" valueChangeListener="#{fceDetail.paDateRangeChanged}" >
						<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
			</af:panelForm>
			<af:objectSpacer height="10" />
			<af:panelButtonBar>
				<af:commandButton id="search" text="Search"
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.searchPA}">
				</af:commandButton>
				<af:commandButton id="reset" text="Reset" immediate="true"
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.resetPA}">
				</af:commandButton>
				<af:commandButton id="set" text="Set Preserved List" partialTriggers="paStartDate paEndDate"
					rendered="true" disabled="#{fceDetail.dateRangeChangePA || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.setPreservedListPA}">
				</af:commandButton>
				<af:commandButton id="recall" text="Recall Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedPA || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.recallPreservedListPA}">
				</af:commandButton>
				<af:commandButton id="clear" text="Clear Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedPA || fceDetail.inspectionReadOnly}"
					action="#{confirmWindow.confirm}"
					useWindow="true" windowWidth="600" windowHeight="300" >
					<t:updateActionListener property="#{confirmWindow.type}"
						value="#{confirmWindow.yesNo}" />
					<t:updateActionListener property="#{confirmWindow.method}"
						value="fceDetail.clearPreservedListPA" />
					<t:updateActionListener property="#{confirmWindow.message}"
						value="Do you want to Clear the Preserved List? Click Yes to proceed or No to cancel" />
					<%-- temporarily set this flag to true so that the start and end dates can refresh --%>
					<t:updateActionListener value="true" property="#{fceDetail.snaphotSearchDatesReadOnly}" />	
				</af:commandButton>										
			</af:panelButtonBar>
			<af:objectSpacer height="10" />																
	
			<af:table id="fceApplicationTable" width="98%" partialTriggers="startDate reset"
				value="#{fceDetail.applicationWrapper}" binding="#{fceDetail.applicationWrapper.table}"
				bandingInterval="1" banding="row" var="appPermit" rows="0">
				<af:column id="applicationId" headerText="Request Number"
					sortable="true" sortProperty="applicationNumber" formatType="text" width="65px">
					<af:commandLink rendered="true" text="#{appPermit.applicationNumber}"
						action="#{applicationDetail.submitApplicationDetail}">
						<af:setActionListener from="#{appPermit.applicationNumber}"
							to="#{applicationDetail.applicationNumber}" />
						<t:updateActionListener property="#{relocation.fromTODOList}"
							value="false" />
					</af:commandLink>
				</af:column>
				
				<af:column id="applicationDesc" headerText="Purpose of Application" 
					sortable="true" sortProperty="applicationDesc"  formatType="text" width="128px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:outputText value="#{appPermit.applicationDesc}"  truncateAt="200" 
							shortDesc="#{appPermit.applicationDesc}"/>	
					</af:panelHorizontal>
				</af:column>
	
				<af:column id="applicationType" headerText="Request Type" 
					sortable="true" sortProperty="applicationTypeCd" formatType="text"  width="100px" noWrap="true">
					<af:selectOneChoice value="#{appPermit.applicationTypeCd}" readOnly="true">
						<f:selectItems value="#{applicationReference.applicationTypeDefs}" />
					</af:selectOneChoice>
				</af:column>
				
				<af:column id="receivedDate" headerText="Received Date" 
					sortable="true" sortProperty="receivedDate" formatType="text"  width="60px">
					<af:selectInputDate readOnly="true" value="#{appPermit.receivedDate}" />
				</af:column>
				
				<af:column id="submitted" headerText="Submitted?"
					sortable="true" sortProperty="submittedDate" formatType="text"  width="75px">
					<af:outputText value="#{empty appPermit.submittedDate ? 'No': 'Yes'}" />
				</af:column>
				
				<af:column id="previousAppNbr" headerText="Previous Application Number"
					sortable="true" sortProperty="previousApplicationNumber" formatType="text"  width="75px">
					<af:outputText value="#{empty appPermit.previousApplicationNumber ? 'N/A' : appPermit.previousApplicationNumber}" />
				</af:column>
				
				<af:column id="permitNbr" headerText="Permit Number"
					sortable="true" sortProperty="permitNumber" formatType="text" width="75px">
					<af:outputText value="#{appPermit.permitNumber}" />
				</af:column>
				
				<af:column id="finalIssuanceDate" headerText="Permit Final Issuance Date" 
					sortable="true" sortProperty="finalIssuanceDate" formatType="text"  width="75px">
					<af:selectInputDate readOnly="true" value="#{appPermit.finalIssuanceDate}" />
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

