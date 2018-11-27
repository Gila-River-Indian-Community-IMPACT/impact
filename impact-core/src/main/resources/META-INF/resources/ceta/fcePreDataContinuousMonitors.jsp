<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>


<h:panelGrid border="1" width="1500">
	<af:panelGroup layout="vertical">
		<af:panelHeader text="CEM/COM/CMS: " size="0" />

		<af:showDetailHeader text="CEM/COM/CMS List" size="0" disclosed="true">
			<af:outputLabel value="Monitor Install Date" />
			<af:panelForm rows="1" maxColumns="2" width="50%"
				partialTriggers="startDate">
				<afh:rowLayout halign="left">
					<af:selectInputDate id="cmStartDate" label="Start Date: " autoSubmit="true"
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangeContinuousMonitors.startDt}"
						valueChangeListener="#{fceDetail.continuousMonitorsDateRangeChanged}">
						<af:validateDateTimeRange minimum="1900-01-01"
							maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
					<af:objectSpacer width="20" />
					<af:selectInputDate id="cmEndDate" label="End Date: " autoSubmit="true"
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangeContinuousMonitors.endDt}"
						valueChangeListener="#{fceDetail.continuousMonitorsDateRangeChanged}">
						<af:validateDateTimeRange minimum="1900-01-01"
							maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
			</af:panelForm>
			<af:objectSpacer height="10" />
			<af:panelButtonBar>
				<af:commandButton id="search" text="Search" 
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.searchContinuousMonitorLimits}">
				</af:commandButton>
				<af:commandButton id="reset" text="Reset" immediate="true"
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.resetContinuousMonitorLimits}">
				</af:commandButton>
				<af:commandButton id="set" text="Set Preserved List"
					partialTriggers="cmStartDate cmEndDate" rendered="true"
					disabled="#{fceDetail.dateRangeChangeContinuousMonitor || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.setPreservedListContinuousMonitorLimits}">
				</af:commandButton>
				<af:commandButton id="recall" text="Recall Preserved List"
					rendered="true"
					disabled="#{!fceDetail.hasPreservedContinuousMonitor || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.recallPreservedListContinuousMonitorLimits}">
				</af:commandButton>
				<af:commandButton id="clear" text="Clear Preserved List"
					rendered="true"
					disabled="#{!fceDetail.hasPreservedContinuousMonitor || fceDetail.inspectionReadOnly}"
					action="#{confirmWindow.confirm}" useWindow="true"
					windowWidth="600" windowHeight="300">
					<t:updateActionListener property="#{confirmWindow.type}"
						value="#{confirmWindow.yesNo}" />
					<t:updateActionListener property="#{confirmWindow.method}"
						value="fceDetail.clearPreservedListContinuousMonitorLimits" />
					<t:updateActionListener property="#{confirmWindow.message}"
						value="Do you want to Clear the Preserved List? Click Yes to proceed or No to cancel" />
					<%-- temporarily set this flag to true so that the start and end dates can refresh --%>
					<t:updateActionListener value="true" property="#{fceDetail.snaphotSearchDatesReadOnly}" />
				</af:commandButton>
			</af:panelButtonBar>
			<af:objectSpacer height="10" />

			<af:table id="fceContinuousMonitorsTable" width="98%"
				partialTriggers="startDate"
				value="#{fceDetail.continuousMonitorsWrapper}"
				binding="#{fceDetail.continuousMonitorsWrapper.table}"
				bandingInterval="1" banding="row" var="limit" rows="0">

				<af:column id="edit" formatType="text" headerText="Limit ID"
					noWrap="true" sortProperty="limId" sortable="true" width="50px">
					<af:commandLink useWindow="true" windowWidth="700"
						windowHeight="600" rendered="true" 
						action="#{fceDetail.viewFacilityCemComLimit}">
						<af:inputText value="#{limit.limId}" readOnly="true"
							valign="middle">
						</af:inputText>
					</af:commandLink>
				</af:column>

				<af:column rendered="true" formatType="text" headerText="Monitor ID"
					sortable="true" sortProperty="monitorId">
					<af:commandLink rendered="true" text="#{limit.monId}"
						action="#{fceDetail.viewContinuousMonitor}">
						<af:setActionListener from="#{limit.monitorId}"
							to="#{continuousMonitorDetail.continuousMonitorId}" />
						<af:setActionListener from="#{facilityProfile.staging}"
							to="#{continuousMonitorDetail.staging}" />
					</af:commandLink>
				</af:column>

				<af:column rendered="true" formatType="text"
					headerText="Monitor Description" sortable="true"
					sortProperty="monitorDesc" noWrap="false">
					<af:inputText value="#{limit.monitorDesc}" readOnly="true"
						valign="middle">
					</af:inputText>
				</af:column>

				<af:column headerText="Monitor Install Date" formatType="text"
					width="65px" sortProperty="monitorInstallDate" sortable="true"
					id="monitorInstallDate">
					<af:selectInputDate label="Monitor Install Date" readOnly="true"
						valign="middle" required="true"
						value="#{limit.monitorInstallDate}">
					</af:selectInputDate>
				</af:column>

				<af:column headerText="Date of Last Audit" formatType="text"
					width="65px" sortProperty="lastAuditDate" sortable="true"
					id="lastAuditDate">
					<af:selectInputDate label="Date of Last Audit" readOnly="true"
						valign="middle" required="true" value="#{limit.lastAuditDate}">
					</af:selectInputDate>
				</af:column>

				<af:column formatType="text" headerText="Limit Description"
					sortProperty="limitDesc" sortable="true" id="limitDesc">
					<af:inputText value="#{limit.limitDesc}" readOnly="true"
						valign="middle">
					</af:inputText>
				</af:column>

				<af:column formatType="text" headerText="Source of Limit"
					sortProperty="limitSource" sortable="true" id="limitSource"
					noWrap="false">
					<af:inputText value="#{limit.limitSource}" readOnly="true"
						valign="middle">
					</af:inputText>
				</af:column>

				<af:column headerText="Start Monitoring Limit" formatType="text"
					width="65px" sortProperty="limitStartDate" sortable="true"
					id="limitStartDate">
					<af:selectInputDate label="Start Monitoring Limit" readOnly="true"
						valign="middle" required="true" value="#{limit.limitStartDate}">
					</af:selectInputDate>
				</af:column>

				<af:column headerText="End Monitoring Limit" formatType="text"
					width="65px" sortProperty="limitEndDate" sortable="true"
					id="limitEndDate">
					<af:selectInputDate label="End Monitoring Limit" readOnly="true"
						valign="middle" required="true" value="#{limit.limitEndDate}">
					</af:selectInputDate>
				</af:column>

				<af:column formatType="text" headerText="Additional Information"
					sortProperty="addlInfo" sortable="true" id="addlInfo">
					<af:inputText value="#{limit.addlInfo}" readOnly="true"
						valign="middle">
					</af:inputText>
				</af:column>

				<af:column sortable="false" formatType="icon"
					headerText="Periodic Limit Trend Report" rendered="true"
					width="80px">
					<af:commandButton text="Show Report"
						action="#{fceDetail.showPeriodicLimitTrendReport}">
						<t:updateActionListener
							property="#{facilityProfile.trendRptLimId}"
							value="#{limit.limId}" />
						<t:updateActionListener
							property="#{facilityProfile.trendRptLimitDesc}"
							value="#{limit.limitDesc}" />
						<t:updateActionListener
							property="#{facilityProfile.trendRptMonId}"
							value="#{limit.monId}" />
						<t:updateActionListener
							property="#{facilityProfile.trendRptMonitorId}"
							value="#{limit.monitorId}" />
						<t:updateActionListener
							property="#{facilityProfile.trendRptCorrLimitId}"
							value="#{limit.corrLimitId}" />
					</af:commandButton>
				</af:column>

				<af:column sortable="false" formatType="icon"
					headerText="Audit Limit Trend Report" width="72px">
					<af:commandButton text="Show Report"
						action="#{fceDetail.showAuditLimitTrendReport}">
						<t:updateActionListener
							property="#{facilityProfile.trendRptLimId}"
							value="#{limit.limId}" />
						<t:updateActionListener
							property="#{facilityProfile.trendRptLimitDesc}"
							value="#{limit.limitDesc}" />
						<t:updateActionListener
							property="#{facilityProfile.trendRptMonId}"
							value="#{limit.monId}" />
						<t:updateActionListener
							property="#{facilityProfile.trendRptMonitorId}"
							value="#{limit.monitorId}" />
						<t:updateActionListener
							property="#{facilityProfile.trendRptCorrLimitId}"
							value="#{limit.corrLimitId}" />
					</af:commandButton>
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