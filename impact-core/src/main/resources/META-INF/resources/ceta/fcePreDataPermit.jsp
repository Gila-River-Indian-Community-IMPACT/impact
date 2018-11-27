<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>


<h:panelGrid border="1" title="Permits" width="1500">
	<af:panelGroup layout="vertical">
		<af:panelHeader text="Permits: " size="0" />
		
		<af:showDetailHeader text="Permit List" size ="0" disclosed="true" >
			<af:outputLabel value="Permit Final Issuance Date" />
			<af:panelForm rows="1" maxColumns="2" width="50%" partialTriggers="startDate">
				<afh:rowLayout halign="left" >
						<af:selectInputDate id="ptStartDate" label="Start Date: " autoSubmit="true"
							readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
							value="#{fceDetail.fce.fcePreData.dateRangePT.startDt}" valueChangeListener="#{fceDetail.ptDateRangeChanged}">
							<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>
						<af:objectSpacer width="20" />
						<af:selectInputDate id="ptEndDate" label="End Date: " autoSubmit="true"
							readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
							value="#{fceDetail.fce.fcePreData.dateRangePT.endDt}" valueChangeListener="#{fceDetail.ptDateRangeChanged}" >
							<af:validateDateTimeRange minimum="1900-01-01" maximum="#{infraDefs.currentDate}" />
						</af:selectInputDate>
				</afh:rowLayout>
			</af:panelForm>
			<af:objectSpacer height="10" />
			<af:panelButtonBar>
				<af:commandButton id="search" text="Search"
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.searchPT}">
				</af:commandButton>
				<af:commandButton id="reset" text="Reset" immediate="true"
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.resetPT}">
				</af:commandButton>
				<af:commandButton id="set" text="Set Preserved List" partialTriggers="ptStartDate ptEndDate"
					rendered="true" disabled="#{fceDetail.dateRangeChangePT || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.setPreservedListPT}">
				</af:commandButton>
				<af:commandButton id="recall" text="Recall Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedPT || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.recallPreservedListPT}">
				</af:commandButton>
				<af:commandButton id="clear" text="Clear Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedPT || fceDetail.inspectionReadOnly}"
					action="#{confirmWindow.confirm}"
					useWindow="true" windowWidth="600" windowHeight="300" >
					<t:updateActionListener property="#{confirmWindow.type}"
						value="#{confirmWindow.yesNo}" />
					<t:updateActionListener property="#{confirmWindow.method}"
						value="fceDetail.clearPreservedListPT" />
					<t:updateActionListener property="#{confirmWindow.message}"
						value="Do you want to Clear the Preserved List? Click Yes to proceed or No to cancel" />
					<%-- temporarily set this flag to true so that the start and end dates can refresh --%>	
					<t:updateActionListener value="true" property="#{fceDetail.snaphotSearchDatesReadOnly}" />
				</af:commandButton>							
			</af:panelButtonBar>
			<af:objectSpacer height="10" />																

			<af:table id="fcePermitTable" width="98%" partialTriggers="startDate"
				value="#{fceDetail.permitWrapper}" binding="#{fceDetail.permitWrapper.table}"
				bandingInterval="1" banding="row" var="permit" rows="0">
					
				<af:column headerText="Permit Number" sortable="true"
					sortProperty="permitNumber" formatType="text" width="60px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:commandLink text="#{permit.permitNumber}"
							action="#{permitDetail.loadPermit}">
							<af:setActionListener to="#{permitDetail.permitID}"
								from="#{permit.permitID}" />
							<t:updateActionListener property="#{permitDetail.fromTODOList}"
								value="false" />
						</af:commandLink>
					</af:panelHorizontal>
				</af:column>
			
				<af:column headerText="Legacy Permit No." sortable="true"
					sortProperty="legacyPermitNumber" formatType="text" width="70px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:commandLink text="#{permit.legacyPermitNumber}"
							action="#{permitDetail.loadPermit}">
							<af:setActionListener to="#{permitDetail.permitID}"
								from="#{permit.permitID}" />
							<t:updateActionListener property="#{permitDetail.fromTODOList}"
								value="false" />
						</af:commandLink>
					</af:panelHorizontal>
				</af:column>
					
				<af:column headerText="Type" sortable="true" sortProperty="permitType"
					formatType="text">
					<af:panelHorizontal valign="middle" halign="left">
						<af:selectOneChoice unselectedLabel=" " readOnly="true"
							value="#{permit.permitType}">
							<mu:selectItems value="#{permitReference.permitTypes}" />
						</af:selectOneChoice>  
					</af:panelHorizontal>
				</af:column>

				<af:column headerText="Permit Status" sortable="true"
					sortProperty="permitLevelStatusCd" formatType="text" width="55px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:selectOneChoice label="Permit status"
							value="#{permit.permitLevelStatusCd}" readOnly="true">
							<f:selectItems value="#{permitReference.permitLevelStatusDefs.items[
													(empty permit.permitLevelStatusCd
													? '' : permit.permitLevelStatusCd)]}" />
						</af:selectOneChoice>
					</af:panelHorizontal>
				</af:column>
				
				<af:column sortProperty="actionType" sortable="true" formatType="text"
					headerText="Action">
					<af:panelHorizontal valign="middle" halign="left">
					<af:outputText
						value="#{permitReference.permitActionTypeDefs.itemDesc[(empty permit.actionType ? '' : permit.actionType)]}" />
					</af:panelHorizontal>
				</af:column>
								
				<af:column headerText="Publication Stage" sortable="true"
					sortProperty="permitGlobalStatusCD" formatType="text" width="75px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:selectOneChoice label="Permit status"
							value="#{permit.permitGlobalStatusCD}" readOnly="true" id="soc2">
							<mu:selectItems value="#{permitReference.permitGlobalStatusDefs}" id="soc2" />
						</af:selectOneChoice>
					</af:panelHorizontal>
				</af:column>
					
				<af:column headerText="Reason(s)" sortable="true"
					sortProperty="primaryReasonCD" width="55px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:selectManyCheckbox label="Reason(s) :" valign="top"
							readOnly="true" value="#{permit.permitReasonCDs}">
							<f:selectItems value="#{permitSearch.allPermitReasons}" />
						</af:selectManyCheckbox>
					</af:panelHorizontal>
				</af:column>
					
				<af:column headerText="Expiration Date" sortable="true"
					sortProperty="expirationDate" formatType="text" width="60px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:selectInputDate readOnly="true" value="#{permit.expirationDate}" />
					</af:panelHorizontal>
				</af:column>
					
				<af:column headerText="Final Issuance Date" sortable="true"
					sortProperty="finalIssueDate" formatType="text" width="55px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:selectInputDate readOnly="true" value="#{permit.finalIssueDate}" />
					</af:panelHorizontal>
				</af:column>
					
				<af:column headerText="Permit Basis Date" sortable="true"
					sortProperty="permitBasisDt" formatType="text" width="55px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:selectInputDate readOnly="true" value="#{permit.permitBasisDt}" />
					</af:panelHorizontal>
				</af:column>
					
				<af:column headerText="Rescission Date" sortable="true"
					sortProperty="recissionDate" formatType="text" width="65px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:selectInputDate readOnly="true" value="#{permit.recissionDate}" />
					</af:panelHorizontal>
				</af:column>
					
				<af:column sortProperty="description" sortable="true"
					formatType="text" headerText="Description" noWrap="false">
					<af:panelHorizontal valign="middle" halign="left">
						<af:outputText truncateAt="300" value="#{permit.description}"  shortDesc="#{permit.description}"/>
					</af:panelHorizontal>
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

