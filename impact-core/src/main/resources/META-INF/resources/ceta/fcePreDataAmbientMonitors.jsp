<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>


<h:panelGrid border="1" title="Ambient Monitors" width="1500">
	<af:panelGroup layout="vertical">
		<af:panelHeader text="Ambient Monitors: " size="0" />

		<af:showDetailHeader text="Ambient Monitors List" size="0"
			disclosed="true">
			<af:outputLabel value="Ambient Monitor Site Date Range" />
			<af:panelForm rows="1" maxColumns="2" width="50%"
				partialTriggers="startDate">
				<afh:rowLayout halign="left">
					<af:selectInputDate id="amStartDate" label="Start Date: " autoSubmit="true"
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangeAmbientMonitors.startDt}"
						valueChangeListener="#{fceDetail.ambientMonitorsDateRangeChanged}">
						<af:validateDateTimeRange minimum="1900-01-01"
							maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
					<af:objectSpacer width="20" />
					<af:selectInputDate id="amEndDate" label="End Date: " autoSubmit="true"
						readOnly="#{fceDetail.inspectionReadOnly || fceDetail.snaphotSearchDatesReadOnly}"
						value="#{fceDetail.fce.fcePreData.dateRangeAmbientMonitors.endDt}"
						valueChangeListener="#{fceDetail.ambientMonitorsDateRangeChanged}">
						<af:validateDateTimeRange minimum="1900-01-01"
							maximum="#{infraDefs.currentDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
			</af:panelForm>
			<af:objectSpacer height="10" />
			<af:panelButtonBar>
				<af:commandButton id="search" text="Search" 
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.searchAmbientMonitors}">
				</af:commandButton>
				<af:commandButton id="reset" text="Reset" immediate="true"
					rendered="true" disabled="#{fceDetail.inspectionReadOnly}"
					action="#{fceDetail.resetAmbientMonitors}">
				</af:commandButton>
				<af:commandButton id="set" text="Set Preserved List"
					partialTriggers="amStartDate amEndDate" rendered="true"
					disabled="#{fceDetail.dateRangeChangeAmbientMonitor || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.setPreservedListAmbientMonitor}">
				</af:commandButton>
				<af:commandButton id="recall" text="Recall Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedAmbientMonitor || fceDetail.inspectionReadOnly}"
					action="#{fceDetail.recallPreservedListAmbientMonitor}">
				</af:commandButton>
				<af:commandButton id="clear" text="Clear Preserved List"
					rendered="true" disabled="#{!fceDetail.hasPreservedAmbientMonitor || fceDetail.inspectionReadOnly}"
					action="#{confirmWindow.confirm}" useWindow="true"
					windowWidth="600" windowHeight="300">
					<t:updateActionListener property="#{confirmWindow.type}"
						value="#{confirmWindow.yesNo}" />
					<t:updateActionListener property="#{confirmWindow.method}"
						value="fceDetail.clearPreservedListAmbientMonitor" />
					<t:updateActionListener property="#{confirmWindow.message}"
						value="Do you want to Clear the Preserved List? Click Yes to proceed or No to cancel" />
					<%-- temporarily set this flag to true so that the start and end dates can refresh --%>
					<t:updateActionListener value="true" property="#{fceDetail.snaphotSearchDatesReadOnly}" />
				</af:commandButton>
			</af:panelButtonBar>
			<af:objectSpacer height="10" />

			<af:table id="ambientMonitorsTable" width="98%"
				partialTriggers="startDate"
				value="#{fceDetail.ambientMonitorsWrapper}"
				binding="#{fceDetail.ambientMonitorsWrapper.table}"
				bandingInterval="1" banding="row" var="monitor" rows="0">

				<af:column sortProperty="mstId" sortable="true" formatType="text"
					headerText="Site ID">
					<af:commandLink action="#{monitorSiteDetail.submitFromJsp}">
						<af:outputText value="#{monitor.mstId}" />
						<t:updateActionListener property="#{monitorSiteDetail.siteId}"
							value="#{monitor.siteId}" />
					</af:commandLink>
				</af:column>

				<af:column sortProperty="siteName" sortable="true" formatType="text"
					headerText="Site Name">
					<af:outputText value="#{monitor.siteName}" />
				</af:column>

				<af:column sortProperty="aqsSiteId" sortable="true"
					formatType="text" headerText="AQS Site ID">
					<af:outputText value="#{monitor.aqsSiteId}" />
				</af:column>

				<af:column sortProperty="siteStatus" sortable="true"
					formatType="text" headerText="Site Status">
					<af:selectOneChoice value="#{monitor.siteStatus}" readOnly="true">
						<f:selectItems value="#{monitorDetail.statusDefs.allItems}" />
					</af:selectOneChoice>
				</af:column>

				<af:column sortProperty="startDate" sortable="true"
					formatType="text" headerText="Start Date">
					<af:selectInputDate value="#{monitor.startDate}" readOnly="true" />
				</af:column>
				<af:column sortProperty="endDate" sortable="true" formatType="text"
					headerText="End Date">
					<af:selectInputDate value="#{monitor.endDate}" readOnly="true" />
				</af:column>

				<af:column sortProperty="latLon" sortable="true" formatType="text"
					headerText="Lat/Long">
					<af:goLink text="#{monitor.latLon}" targetFrame="_new"
						rendered="#{not empty monitor.latLon}"
						destination="#{monitor.googleMapsURL}" inlineStyle="padding:5px;"
						shortDesc="Clicking this will open Google Maps in a separate tab or window." />
				</af:column>

				<af:column sortProperty="mntrId" sortable="true" formatType="text"
					headerText="Monitor ID">
					<af:commandLink action="#{monitorDetail.submitFromJsp}">
						<af:outputText value="#{monitor.mntrId}" />
						<t:updateActionListener property="#{monitorDetail.monitorId}"
							value="#{monitor.monitorId}" />
					</af:commandLink>
				</af:column>

				<af:column sortProperty="name" sortable="true" formatType="text"
					headerText="Name">
					<af:outputText value="#{monitor.name}" />
				</af:column>

				<af:column sortProperty="parameterDesc" sortable="true"
					formatType="text" headerText="Parameter">
					<af:outputText value="#{monitor.parameterDesc}" />
				</af:column>

				<af:column sortProperty="status" sortable="true" formatType="text"
					headerText="Monitor Status">
					<af:selectOneChoice value="#{monitor.status}" readOnly="true">
						<f:selectItems value="#{monitorDetail.statusDefs.allItems}" />
					</af:selectOneChoice>
				</af:column>

				<af:column sortProperty="frequencyCode" sortable="true"
					formatType="text" headerText="Collection Frequency Code"
					width="80px">
					<af:selectOneChoice label=""
						readOnly="true" value="#{monitor.frequencyCode}">
						<f:selectItems value="#{monitorDetail.collFreqCdDefs.items[0]}" />
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

