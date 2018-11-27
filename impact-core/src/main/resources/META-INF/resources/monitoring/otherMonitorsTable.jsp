<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{monitorSiteDetail.otherMonitorsWrapper}" bandingInterval="1"
	binding="#{monitorSiteDetail.otherMonitorsWrapper.table}" id="OtherMonitorsTab"
	banding="row" width="98%" var="omonitor"
	rows="#{monitorSiteDetail.pageLimit}">
	<af:column sortProperty="mntrId" sortable="true" formatType="text"
		headerText="Monitor ID">
		<af:commandLink action="#{monitorDetail.submitFromJsp}" rendered="#{monitorDetail.internalApp}"
			disabled="#{monitorSiteDetail.editable}">
			<af:outputText value="#{omonitor.mntrId}" />
			<t:updateActionListener
				property="#{monitorDetail.monitorId}"
				value="#{omonitor.monitorId}" />
		</af:commandLink>
		<af:outputText value="#{omonitor.mntrId}"  rendered="#{!monitorDetail.internalApp}"/>
	</af:column>
	<af:column sortProperty="mgrpId" sortable="true" formatType="text"
		headerText="Group ID">
		<af:outputText value="#{omonitor.mgrpId}" />
	</af:column>
	<af:column sortProperty="mstId" sortable="true" formatType="text"
		headerText="Site ID">
		<af:outputText value="#{omonitor.mstId}" />
	</af:column>
	
	<af:column sortProperty="name" sortable="true" formatType="text"
		headerText="Name">
		<af:outputText value="#{omonitor.name}" />
	</af:column>
	
	<af:column sortProperty="parameterDesc" sortable="true" formatType="text"
		headerText="Parameter">
		<af:outputText value="#{omonitor.parameterDesc}" />
	</af:column>
	
	<af:column sortProperty="status" sortable="true" formatType="text"
		headerText="Status">
		<af:selectOneChoice value="#{omonitor.status}" readOnly="true">
			<f:selectItems value="#{monitorDetail.statusDefs.allItems}" />
		</af:selectOneChoice>
	</af:column>

	<af:column sortProperty="companyName" sortable="true" formatType="text"
		headerText="Company">
		<af:outputText value="#{omonitor.companyName}" />
	</af:column>


	<af:column sortProperty="startDate" sortable="true" formatType="text"
		headerText="Start Date">
		<af:selectInputDate value="#{omonitor.startDate}" readOnly="true" />
	</af:column>
	<af:column sortProperty="endDate" sortable="true" formatType="text"
		headerText="End Date">
		<af:selectInputDate value="#{omonitor.endDate}" readOnly="true" />
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