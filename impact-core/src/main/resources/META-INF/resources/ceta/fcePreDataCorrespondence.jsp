<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>



<h:panelGrid border="1" title="Correspondence" width="1500">
	<af:panelGroup layout="vertical">
		<af:panelHeader text="Correspondence: " size="0" />
		<af:showDetailHeader text="Correspondence List" size ="0" disclosed="true">
			<af:outputLabel value="Correspondence Date Generated/Receipt Date" />
			<af:panelForm rows="1" maxColumns="2" width="50%" partialTriggers="startDate">
				<afh:rowLayout halign="left" >
					<af:selectInputDate id="dcStartDate" label="Start Date: " autoSubmit="true"  
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangeDC.startDt}" valueChangeListener="#{fceDetail.dcDateRangeChanged}">
						<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
					<af:objectSpacer width="20" />
					<af:selectInputDate id="dcEndDate" label="End Date: " autoSubmit="true" 
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangeDC.endDt}" valueChangeListener="#{fceDetail.dcDateRangeChanged}" >
						<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
			</af:panelForm>
			<af:objectSpacer height="10" />
			<af:panelButtonBar>
				<af:commandButton id="search" text="Search"
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.searchDC}">
				</af:commandButton>
				<af:commandButton id="reset" text="Reset" immediate="true"
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.resetDC}">
				</af:commandButton>
				<af:commandButton id="set" text="Set Preserved List" partialTriggers="dcStartDate dcEndDate"
					rendered="true" disabled="#{fceDetail.dateRangeChangeDC || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.setPreservedListDC}">
				</af:commandButton>
				<af:commandButton id="recall" text="Recall Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedDC || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.recallPreservedListDC}">
				</af:commandButton>
				<af:commandButton id="clear" text="Clear Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedDC || fceDetail.inspectionReadOnly}"
					action="#{confirmWindow.confirm}"
					useWindow="true" windowWidth="600" windowHeight="300" >
					<t:updateActionListener property="#{confirmWindow.type}"
						value="#{confirmWindow.yesNo}" />
					<t:updateActionListener property="#{confirmWindow.method}"
						value="fceDetail.clearPreservedListDC" />
					<t:updateActionListener property="#{confirmWindow.message}"
						value="Do you want to Clear the Preserved List? Click Yes to proceed or No to cancel" />
					<%-- temporarily set this flag to true so that the start and end dates can refresh --%>
					<t:updateActionListener value="true" property="#{fceDetail.snaphotSearchDatesReadOnly}" />
				</af:commandButton>										
			</af:panelButtonBar>
			<af:objectSpacer height="10" />																
	
			<af:table id="fceCorrespondenceTable" width="98%" partialTriggers="startDate reset"
				value="#{fceDetail.correspondenceWrapper}" binding="#{fceDetail.correspondenceWrapper.table}"
				bandingInterval="1" banding="row" var="correspondenceList" rows="0">
	
				<af:column id="correspondenceID" headerText="Correspondence ID"
					sortable="true" sortProperty="correspondenceID" formatType="text" width="70px">
					<af:commandLink text="#{correspondenceList.corId}"
						action="#{correspondenceDetail.submitFromJsp}">
						<t:updateActionListener
							property="#{correspondenceDetail.correspondenceID}"
							value="#{correspondenceList.correspondenceID}" />
					</af:commandLink>
				</af:column>
			
				<af:column id="directionCd" headerText="Correspondence Direction"
					sortable="true" sortProperty="directionCd" formatType="text" width="150px">
					<af:selectOneChoice value="#{correspondenceList.directionCd}" readOnly="true">
						<f:selectItems value="#{correspondenceSearch.correspondenceDirectionDef}" />
					</af:selectOneChoice>
				</af:column>	
				
				<af:column id="correspondenceType" headerText="Correspondence Type"
					sortable="true" sortProperty="correspondenceTypeDescription" formatType="text" width="145px">
					<af:outputText value="#{correspondenceList.correspondenceTypeDescription}" />
				</af:column>
				
				<af:column id="correspondenceCategoryCd" headerText="Correspondence Category"
					sortable="true" sortProperty="correspondenceCategoryDesc"  formatType="text" width="80px">
					<af:selectOneChoice value="#{correspondenceList.correspondenceCategoryCd}" readOnly="true">
						<f:selectItems value="#{correspondenceSearch.correspondenceCategoryDef}" />
					</af:selectOneChoice>
				</af:column>	
				
				<af:column id="regarding" headerText="Subject"
					sortable="true" sortProperty="regarding"  formatType="text" width="200px">
					<af:outputText value="#{correspondenceList.regarding}"  truncateAt="100" 
						shortDesc="#{correspondenceList.regarding}"/>
				</af:column>
			
				<af:column id="dateGenerated" headerText="Date Generated"
					sortable="true" sortProperty="dateGenerated" formatType="text" width="62px">
					<af:selectInputDate value="#{correspondenceList.dateGenerated}" readOnly="true" />
				</af:column>
				
				<af:column id="receiptDate" headerText="Receipt Date" 
					sortProperty="receiptDate" sortable="true" formatType="text" width="62px">
					<af:selectInputDate value="#{correspondenceList.receiptDate}" readOnly="true" />
				</af:column>
				
				<af:column id="additionalInfo" headerText="Additional Info"
					sortProperty="additionalInfo" sortable="true" formatType="text" width="200px">
					<af:outputText value="#{correspondenceList.additionalInfo}" truncateAt="200"
						shortDesc="#{correspondenceList.additionalInfo}"/>
				</af:column>

				<af:column id="attachmentCount" headerText="Attachment Count"
					sortProperty="attachmentCount" sortable="true" formatType="text" width="70px">
					<af:outputText value="#{correspondenceList.attachmentCount}" />
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







