<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{monitorSiteDetail.siteMonitorsWrapper}" bandingInterval="1"
	binding="#{monitorSiteDetail.siteMonitorsWrapper.table}" id="SiteMonitorsTab"
	banding="row" width="98%" var="monitor"
	rows="#{monitorSiteDetail.pageLimit}">
	<af:column sortProperty="mntrId" sortable="true" formatType="text"
		headerText="Monitor ID">
		<af:commandLink action="#{monitorDetail.submitFromJsp}"
			disabled="#{monitorSiteDetail.editable}">
			<af:outputText value="#{monitor.mntrId}" />
			<t:updateActionListener
				property="#{monitorDetail.monitorId}"
				value="#{monitor.monitorId}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="type" sortable="true" formatType="text"
		headerText="Type">
		<af:selectOneChoice value="#{monitor.type}" readOnly="true">
			<f:selectItems value="#{monitorDetail.typeDefs.allItems}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="name" sortable="true" formatType="text"
		headerText="Name">
		<af:outputText value="#{monitor.name}" />
	</af:column>
	<af:column sortProperty="parameterDesc" sortable="true" formatType="text"
		headerText="Parameter">
		<af:outputText value="#{monitor.parameterDesc}" />
	</af:column>
	<af:column sortProperty="status" sortable="true" formatType="text"
		headerText="Status">
		<af:selectOneChoice value="#{monitor.status}" readOnly="true">
			<f:selectItems value="#{monitorDetail.statusDefs.allItems}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="startDate" sortable="true" formatType="text"
		headerText="Start Date">
		<af:selectInputDate value="#{monitor.startDate}" readOnly="true" />
	</af:column>
	<af:column sortProperty="endDate" sortable="true" formatType="text"
		headerText="End Date">
		<af:selectInputDate value="#{monitor.endDate}" readOnly="true" />
	</af:column>
	<af:column sortProperty="parameterCode" sortable="true" formatType="text"
		headerText="Parameter Code">
		<af:outputText value="#{monitor.parameterCode}" />
	</af:column>
	<af:column sortProperty="parameterOccurrenceCode" sortable="true" formatType="text"
		headerText="POC">
		<af:outputText value="#{monitor.parameterOccurrenceCode}" />
	</af:column>
	<af:column sortProperty="methodCode" sortable="true" formatType="text"
		headerText="Method Code">
		<af:outputText value="#{monitor.methodCode}">
			<af:convertNumber pattern="000" />
		</af:outputText>
	</af:column>
	<af:column sortProperty="durationCode" sortable="true" formatType="text"
		headerText="Duration Code">
		<af:outputText value="#{monitor.durationCode}" />
	</af:column>
	<af:column sortProperty="unitCode" sortable="true" formatType="text"
		headerText="Unit Code">
		<af:outputText value="#{monitor.unitCode}">
			<af:convertNumber pattern="000" />
		</af:outputText>
	</af:column>
	<af:column sortProperty="frequencyCode" sortable="true" formatType="text"
		headerText="Collection Frequency Code">
		<af:outputText value="#{monitor.frequencyCode}" />
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Add Monitor" id="AddMonitorButton"
					useWindow="true" windowWidth="650" windowHeight="650"
					disabled="#{monitorSiteDetail.readOnlyUser}"
					returnListener="#{monitorSiteDetail.addMonitorDialogDone}" 
					rendered="#{!monitorSiteDetail.readOnlyUser 
								&& !monitorSiteDetail.monitorReportUploadUser && monitorSiteDetail.internalApp}"
					action="#{monitorSiteDetail.startAddMonitor}">
				</af:commandButton>
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>