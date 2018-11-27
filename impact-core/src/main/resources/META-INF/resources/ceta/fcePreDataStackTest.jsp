<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>



<h:panelGrid border="1" title="Stack Tests" width="1500">
	<af:panelGroup layout="vertical">
		<af:panelHeader text="Stack Tests: " size="0" />
			<af:showDetailHeader text="Stack Test List" size ="0" disclosed="true" >
				<af:outputLabel value="Stack Test Date" />
				<af:panelForm rows="1" maxColumns="2" width="50%" partialTriggers="startDate">
					<afh:rowLayout halign="left" >
						<af:selectInputDate id="stStartDate" label="Start Date: " autoSubmit="true"
							readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
							value="#{fceDetail.fce.fcePreData.dateRangeST.startDt}" valueChangeListener="#{fceDetail.stDateRangeChanged}">
							<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>
						<af:objectSpacer width="20" />
						<af:selectInputDate id="stEndDate" label="End Date: " autoSubmit="true"
							readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
							value="#{fceDetail.fce.fcePreData.dateRangeST.endDt}" valueChangeListener="#{fceDetail.stDateRangeChanged}" >
							<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>
					</afh:rowLayout>
				</af:panelForm>
				<af:objectSpacer height="10" />
				<af:panelButtonBar>
					<af:commandButton id="search" text="Search"
						rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
						action="#{fceDetail.searchST}">
					</af:commandButton>
					<af:commandButton id="reset" text="Reset" immediate="true"
						rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
						action="#{fceDetail.resetST}">
					</af:commandButton>
					<af:commandButton id="set" text="Set Preserved List" partialTriggers="stStartDate stEndDate"
						rendered="true" disabled="#{fceDetail.dateRangeChangeST || fceDetail.inspectionReadOnly}"
						action="#{fceDetail.setPreservedListST}">
					</af:commandButton>
					<af:commandButton id="recall" text="Recall Preserved List"
						rendered="true" disabled="#{!fceDetail.hasPreservedST || fceDetail.inspectionReadOnly}"
						action="#{fceDetail.recallPreservedListST}">
					</af:commandButton>
					<af:commandButton id="clear" text="Clear Preserved List"
						rendered="true" disabled="#{!fceDetail.hasPreservedST || fceDetail.inspectionReadOnly}"
						action="#{confirmWindow.confirm}"
						useWindow="true" windowWidth="600" windowHeight="300" >
						<t:updateActionListener property="#{confirmWindow.type}"
								value="#{confirmWindow.yesNo}" />
						<t:updateActionListener property="#{confirmWindow.method}"
								value="fceDetail.clearPreservedListST" />
						<t:updateActionListener property="#{confirmWindow.message}"
								value="Do you want to Clear the Preserved List? Click Yes to proceed or No to cancel" />
						<%-- temporarily set this flag to true so that the start and end dates can refresh --%>
						<t:updateActionListener value="true" property="#{fceDetail.snaphotSearchDatesReadOnly}" />
					</af:commandButton>										
				</af:panelButtonBar>
				<af:objectSpacer height="10" />																
	
				<af:table id="fceStackTestTable" width="98%" partialTriggers="startDate"
					value="#{fceDetail.stackTestWrapper}" binding="#{fceDetail.stackTestWrapper.table}"
					bandingInterval="1" banding="row" var="stackTest" rows="0">
					<af:column id="stackTestId" headerText="Stack Test ID"
						sortable="true" sortProperty="stackTestId" formatType="text" width="65px">
						<af:commandLink rendered="true" text="#{stackTest.stckId}"
							action="#{stackTestDetail.submitStackTest}">
							<af:setActionListener from="#{stackTest.stackTestId}" to="#{stackTestDetail.id}" />
						</af:commandLink>
					</af:column>
				
					<af:column id="pollutantsTested" headerText="Pollutants Tested"
						sortable="true" sortProperty="pollutantsTested" formatType="text" width="200px">
						<af:outputText value="#{stackTest.pollutantsTested}"  truncateAt="200" 
							shortDesc="#{stackTest.pollutantsTested}"/>
					</af:column>
						
					<af:column id="eus" headerText="EUs"
						sortable="true" sortProperty="eus" formatType="text" width="150px">
						<af:outputText value="#{stackTest.eus}"  truncateAt="200" 
							shortDesc="#{stackTest.eus}"/>
					</af:column>
					
					
					<af:column id="stackTestMethodCd" headerText="Stack Test Method"
						sortable="true" sortProperty="stackTestMethodCd" formatType="text" width="150px" noWrap="true">
						<af:selectOneChoice value="#{stackTest.stackTestMethodCd}" readOnly="true">
							<f:selectItems value="#{compEvalDefs.stackTestMethodDef.items[(empty st.stackTestMethodCd ? '' : st.stackTestMethodCd)]}" />
						</af:selectOneChoice>
					</af:column>
					
					<af:column id="pollutantsFailed" headerText="Failed Pollutants"
						sortable="true" sortProperty="pollutantsFailed" formatType="text" width="200px">
						<af:outputText value="#{stackTest.pollutantsFailed}"  truncateAt="200" 
							shortDesc="#{stackTest.pollutantsFailed}"/>
					</af:column>
						
					<af:column id="testDates" headerText="Test Date(s)"
						sortable="true" sortProperty="firstVisitDate" formatType="text" width="60px">
						<af:outputText value="#{stackTest.testDatesString}"  truncateAt="200" 
							shortDesc="#{stackTest.testDatesString}"/>
					</af:column>
		
					<af:column id="dateReceived" headerText="Date Received" 
						sortable="true" sortProperty="dateReceived" formatType="text"  width="60px">
						<af:selectInputDate readOnly="true" value="#{stackTest.dateReceived}" />
					</af:column>
		
					<af:column id="dateReviewed" headerText="Date Reviewed" 
						sortable="true" sortProperty="dateReviewed" formatType="text"  width="60px">
						<af:selectInputDate readOnly="true" value="#{stackTest.dateReviewed}" />
					</af:column>
		
			
					<af:column id="conformedToTestMethod" headerText="Conformed to Test Method"
						sortable="true" sortProperty="conformedToTestMethod" formatType="text"  width="75px">
						<af:selectOneChoice value="#{stackTest.conformedToTestMethod}" readOnly="true">
							<f:selectItem itemLabel="Yes" itemValue="Y" />
							<f:selectItem itemLabel="No" itemValue="N" />
						</af:selectOneChoice>
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

