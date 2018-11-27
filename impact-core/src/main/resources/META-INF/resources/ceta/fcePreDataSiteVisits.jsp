<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>


<h:panelGrid border="1" width="1500">
	<af:panelGroup layout="vertical">
		<af:panelHeader text="Site Visits: " size="0" />

		<af:showDetailHeader text="Site Visits List" size="0" disclosed="true">
			<af:outputLabel value="Visit Date" />
			<af:panelForm rows="1" maxColumns="2" width="50%"
				partialTriggers="startDate">
				<afh:rowLayout halign="left">
					<af:selectInputDate id="svStartDate" label="Start Date: " autoSubmit="true"
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangeSiteVisits.startDt}"
						valueChangeListener="#{fceDetail.siteVisitsDateRangeChanged}">
						<af:validateDateTimeRange minimum="1900-01-01"
							maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
					<af:objectSpacer width="20" />
					<af:selectInputDate id="svEndDate" label="End Date: " autoSubmit="true"
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangeSiteVisits.endDt}"
						valueChangeListener="#{fceDetail.siteVisitsDateRangeChanged}">
						<af:validateDateTimeRange minimum="1900-01-01"
							maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
			</af:panelForm>
			<af:objectSpacer height="10" />
			<af:panelButtonBar>
				<af:commandButton id="search" text="Search" 
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.searchSiteVisit}">
				</af:commandButton>
				<af:commandButton id="reset" text="Reset" immediate="true"
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.resetSiteVisit}">
				</af:commandButton>
				<af:commandButton id="set" text="Set Preserved List" partialTriggers="svStartDate svEndDate"
					rendered="true" disabled="#{fceDetail.dateRangeChangeSiteVisit || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.setPreservedListSiteVisit}">
				</af:commandButton>
				<af:commandButton id="recall" text="Recall Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedSiteVisit || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.recallPreservedListSiteVisit}">
				</af:commandButton>
				<af:commandButton id="clear" text="Clear Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedSiteVisit || fceDetail.inspectionReadOnly}"
					action="#{confirmWindow.confirm}"
					useWindow="true" windowWidth="600" windowHeight="300" >
					<t:updateActionListener property="#{confirmWindow.type}"
						value="#{confirmWindow.yesNo}" />
					<t:updateActionListener property="#{confirmWindow.method}"
						value="fceDetail.clearPreservedListSiteVisit" />
					<t:updateActionListener property="#{confirmWindow.message}"
						value="Do you want to Clear the Preserved List? Click Yes to proceed or No to cancel" />
					<%-- temporarily set this flag to true so that the start and end dates can refresh --%>
					<t:updateActionListener value="true" property="#{fceDetail.snaphotSearchDatesReadOnly}" />
				</af:commandButton>
			</af:panelButtonBar>
			<af:objectSpacer height="10" />

			<af:table id="fceSiteVisitsTable" width="98%"
				partialTriggers="startDate" value="#{fceDetail.siteVisitsWrapper}"
				binding="#{fceDetail.siteVisitsWrapper.table}" bandingInterval="1"
				banding="row" var="sv" rows="0">

				<af:column headerText="Site Visit ID" sortable="true"
					sortProperty="c01" formatType="text" width="80px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:commandLink text="#{sv.siteId}"
							action="#{siteVisitDetail.submitVisit}"
							rendered="#{!sv.stackTest}">
							<t:updateActionListener value="#{sv.id}"
								property="#{siteVisitDetail.visitId}" />
						</af:commandLink>
						<af:commandLink text="#{sv.siteId}"
							action="#{siteVisitDetail.submitStackTestVisitType}"
							rendered="#{sv.stackTest}">
							<t:updateActionListener value="#{sv.facilityId}"
								property="#{siteVisitDetail.facilityIdForStackTestVisitType}" />
							<t:updateActionListener value="#{sv.visitDate}"
								property="#{siteVisitDetail.visitDate}" />
							<t:updateActionListener value="#{sv}"
								property="#{siteVisitDetail.siteVisit}" />
							<t:updateActionListener value="#{sv.id}"
								property="#{siteVisitDetail.visitId}" />
						</af:commandLink>
					</af:panelHorizontal>
				</af:column>

				<af:column headerText="Visit Date" sortable="true"
					sortProperty="c02" formatType="text" width="70px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:selectInputDate id="vdate" readOnly="true"
							value="#{sv.visitDate}">
							<af:validateDateTimeRange minimum="1900-01-01" />
						</af:selectInputDate>
					</af:panelHorizontal>
				</af:column>

				<af:column headerText="Visit Type" sortable="true"
					sortProperty="c03" formatType="text" width="150px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:selectOneChoice value="#{sv.visitType}" readOnly="true">
							<f:selectItems
								value="#{compEvalDefs.siteVisitTypeDef.items[(empty sv.visitType ? '' : sv.visitType)]}" />
						</af:selectOneChoice>
					</af:panelHorizontal>
				</af:column>

				<af:column headerText="Compliance Issue" sortable="true"
					sortProperty="c04" formatType="text" width="150px">
					<af:panelHorizontal valign="middle" halign="left">
						<af:selectOneChoice value="#{sv.complianceIssued}" readOnly="true">
							<f:selectItem itemLabel="Yes" itemValue="Y" />
							<f:selectItem itemLabel="No" itemValue="N" />
						</af:selectOneChoice>
					</af:panelHorizontal>
				</af:column>

				<af:column headerText="Memo" sortProperty="c05" sortable="true"
					formatType="text">
					<af:panelHorizontal valign="middle" halign="left">
						<af:outputText truncateAt="300" value="#{sv.memo}"
							shortDesc="#{sv.memo}" />
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

