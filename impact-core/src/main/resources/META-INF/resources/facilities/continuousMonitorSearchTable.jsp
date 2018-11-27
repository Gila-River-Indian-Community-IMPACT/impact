<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table var="monitor" bandingInterval="1" banding="row" width="100%"
	value="#{facilityProfile.continuousMonitorsWrapper}"
	binding="#{facilityProfile.continuousMonitorsWrapper.table}">

	<af:column rendered="true" formatType="text" headerText="Monitor ID"
		sortable="true" sortProperty="monId" width="70px">
		<af:commandLink rendered="true" text="#{monitor.monId}"
			action="#{continuousMonitorDetail.submit}">
			<af:setActionListener from="#{monitor.continuousMonitorId}"
				to="#{continuousMonitorDetail.continuousMonitorId}" />
			<af:setActionListener from="#{facilityProfile.staging}"
				to="#{continuousMonitorDetail.staging}" />
		</af:commandLink>
	</af:column>
	
	<af:column sortable="true" sortProperty="monitorDetails"
		formatType="text" headerText="Monitor Details" width="100px" noWrap="true">
		<af:inputText readOnly="true" value="#{monitor.monitorDetails}" />
	</af:column>

	<af:column sortable="true" sortProperty="currentManufacturer"
		formatType="text" headerText="Manufacturer" width="90px" noWrap="true">
		<af:inputText readOnly="true" value="#{monitor.currentManufacturer}" />
	</af:column>

	<af:column sortable="true" sortProperty="currentModelNumber"
		formatType="text" headerText="Model Number" width="95px" noWrap="true">
		<af:inputText readOnly="true" value="#{monitor.currentModelNumber}" />
	</af:column>

	<af:column sortable="true" sortProperty="activeLimits"
		formatType="text" headerText="Active Limits" width="100px">
		<af:inputText readOnly="true" value="#{monitor.activeLimits}" />
	</af:column>
	
	<af:column sortable="true" sortProperty="associatedObjects"
		formatType="text" headerText="Associated Object(s)">
		<af:inputText readOnly="true" value="#{monitor.associatedObjects}" />
	</af:column>

	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Create Monitor"
					rendered="#{facilityProfile.internalApp}"
					disabled="#{!continuousMonitorDetail.continuousMonitorCreateAllowed || facilityProfile.disabledUpdateButton}"
					action="#{continuousMonitorDetail.startNewContinuousMonitor}">
					<t:updateActionListener
						property="#{continuousMonitorDetail.facilityId}"
						value="#{facilityProfile.facilityId}" />
				</af:commandButton>
				<af:commandButton text="Show Periodic Facility Trend Report"
					rendered="#{facilityProfile.internalApp}"
					action="#{facilityProfile.showPeriodicFacilityTrendReport}">
				</af:commandButton>
				<af:commandButton text="Show Audit Overall Facility Trend Report"
					rendered="#{facilityProfile.internalApp}"
					action="#{facilityProfile.showAuditOverallFacilityTrendReport}">
				</af:commandButton>
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>

