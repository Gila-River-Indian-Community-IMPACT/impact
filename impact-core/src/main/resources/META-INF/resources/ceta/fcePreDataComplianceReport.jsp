<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>



<h:panelGrid border="1" title="Compliance Reports" width="1500">
	<af:panelGroup layout="vertical">
		<af:panelHeader text="Compliance Reports: " size="0" />
			<af:showDetailHeader text="Compliance Report List" size ="0" disclosed="true" >
				<af:outputLabel value="Compliance Report Received Date" />
				<af:panelForm rows="1" maxColumns="2" width="50%" partialTriggers="startDate">
					<afh:rowLayout halign="left" >
						<af:selectInputDate id="crStartDate" label="Start Date: " autoSubmit="true"
							readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
							value="#{fceDetail.fce.fcePreData.dateRangeCR.startDt}" valueChangeListener="#{fceDetail.crDateRangeChanged}">
							<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>
						<af:objectSpacer width="20" />
						<af:selectInputDate id="crEndDate" label="End Date: " autoSubmit="true"
							readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
							value="#{fceDetail.fce.fcePreData.dateRangeCR.endDt}" valueChangeListener="#{fceDetail.crDateRangeChanged}" >
							<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>
					</afh:rowLayout>
				</af:panelForm>
				<af:objectSpacer height="10" />
				<af:panelButtonBar>
					<af:commandButton id="search" text="Search"
						rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
						action="#{fceDetail.searchCR}">
					</af:commandButton>
					<af:commandButton id="reset" text="Reset" immediate="true"
						rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
						action="#{fceDetail.resetCR}">
					</af:commandButton>
					<af:commandButton id="set" text="Set Preserved List" partialTriggers="crStartDate crEndDate"
						rendered="true" disabled="#{fceDetail.dateRangeChangeCR || fceDetail.inspectionReadOnly}"
						action="#{fceDetail.setPreservedListCR}">
					</af:commandButton>
					<af:commandButton id="recall" text="Recall Preserved List"
						rendered="true" disabled="#{!fceDetail.hasPreservedCR || fceDetail.inspectionReadOnly}"
						action="#{fceDetail.recallPreservedListCR}">
					</af:commandButton>
					<af:commandButton id="clear" text="Clear Preserved List"
						rendered="true" disabled="#{!fceDetail.hasPreservedCR || fceDetail.inspectionReadOnly}"
						action="#{confirmWindow.confirm}"
						useWindow="true" windowWidth="600" windowHeight="300" >
						<t:updateActionListener property="#{confirmWindow.type}"
								value="#{confirmWindow.yesNo}" />
						<t:updateActionListener property="#{confirmWindow.method}"
								value="fceDetail.clearPreservedListCR" />
						<t:updateActionListener property="#{confirmWindow.message}"
								value="Do you want to Clear the Preserved List? Click Yes to proceed or No to cancel" />
						<%-- temporarily set this flag to true so that the start and end dates can refresh --%>
						<t:updateActionListener value="true" property="#{fceDetail.snaphotSearchDatesReadOnly}" />		
					</af:commandButton>										
				</af:panelButtonBar>
				<af:objectSpacer height="10" />																
	
				<af:table id="fceComplianceReportTable" width="98%" partialTriggers="startDate"
					value="#{fceDetail.complianceReportWrapper}" binding="#{fceDetail.complianceReportWrapper.table}"
					bandingInterval="1" banding="row" var="cReport" rows="0">
					<af:column id="reportCRPTId" headerText="Report ID"
						sortable="true" sortProperty="reportCRPTId" formatType="text" width="65px">
						<af:commandLink rendered="true" text="#{cReport.reportCRPTId}"
							action="#{complianceReport.viewDetail}">
							<af:setActionListener from="#{cReport.reportId}" to="#{complianceReport.reportId}" />
							<t:updateActionListener property="#{menuItem_compReportDetail.disabled}" value="false" />
							<t:updateActionListener property="#{complianceReport.fromTODOList}" value="false" />
						</af:commandLink>
					</af:column>
				
					<af:column id="reportType" headerText="Report Type" 
						sortable="true" sortProperty="reportType" formatType="text" width="65px" noWrap="true">
						<af:selectOneChoice value="#{cReport.reportType}" readOnly="true">
							<f:selectItems
								value="#{complianceReport.complianceReportTypesDef.items[(empty '')]}" />
						</af:selectOneChoice>
					</af:column>

					<af:column id="category" headerText="Category"
						sortable="true" sortProperty="otherTypeDsc" formatType="text" >
						<af:outputText value="#{cReport.otherTypeDsc}" />
					</af:column>

					<af:column id="description" headerText="Description"
						sortable="true" sortProperty="descriptionSearchDisplay" formatType="text">
						<af:outputText value="#{cReport.descriptionSearchDisplay}" truncateAt="80"
							shortDesc="#{cReport.descriptionSearchDisplay}" />
					</af:column>

					<af:column id="receivedDate" headerText="Received Date"
						sortable="true" sortProperty="receivedDate" formatType="text" >
						<af:panelHorizontal valign="baseline">
							<af:selectInputDate value="#{cReport.receivedDate}" readOnly="true" />
						</af:panelHorizontal>
					</af:column>
					
					<af:column id="complianceStatus" headerText="Compliance Status" 
						sortable="true" sortProperty="complianceStatusCd" formatType="text" width="65px" noWrap="true">
						<af:selectOneChoice value="#{cReport.complianceStatusCd}" readOnly="true">
							<f:selectItems value="#{complianceReport.complianceStatusDef.items[(empty '')]}"  />
						</af:selectOneChoice>
					</af:column>
					
					<af:column id="accepted" headerText="Accepted"
						sortable="true" sortProperty="acceptable" formatType="text" width="62px" noWrap="true">
						<af:selectOneChoice value="#{cReport.acceptable}" readOnly="true">
							<f:selectItems value="#{complianceReport.complianceReportAcceptedDef.items[(empty '')]}" />
						</af:selectOneChoice>
					</af:column>


					<af:column id="comments" headerText="Comments"
						sortable="true" sortProperty="dapcReviewComments" formatType="text" width="300px">
						<af:outputText value="#{cReport.dapcReviewComments}" truncateAt="200" 
							shortDesc="#{cReport.dapcReviewComments}" />
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

