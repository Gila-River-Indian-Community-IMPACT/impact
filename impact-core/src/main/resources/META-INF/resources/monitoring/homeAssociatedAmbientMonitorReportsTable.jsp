<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:table value="#{homeMonitorGroupDetail.associatedAmbientMonitorReportsWrapper}" bandingInterval="1"
	binding="#{homeMonitorGroupDetail.associatedAmbientMonitorReportsWrapper.table}" id="homeAssociatedAmbientMonitorReportsTab"
	banding="row" width="98%" var="report"
	rows="#{homeMonitorGroupDetail.pageLimit}">
	<af:column sortProperty="mrptId" sortable="true" formatType="text"
		headerText="Report ID">
		<af:commandLink text="#{report.mrptId}" id="viewReport" useWindow="true"
			windowWidth="650" windowHeight="300" disabled="false"
			returnListener="#{homeMonitorGroupDetail.dialogDone}"
			action="#{homeMonitorReportDetail.startViewReportHome}">
			<t:updateActionListener property="#{homeMonitorReportDetail.monitorReport}"
				value="#{report}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="submittedDate" sortable="true" formatType="text"
		headerText="Submitted Date">
		<af:selectInputDate value="#{report.submittedDate}" readOnly="true"/>
	</af:column>
	<af:column sortProperty="reportTypeCd" sortable="true" formatType="text"
		headerText="Report Type">
		<af:selectOneChoice
			value="#{report.reportTypeCd}" readOnly="true">
			<mu:selectItems value="#{homeMonitorReportDetail.monitorReportTypeDef}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="year" sortable="true" formatType="text"
		headerText="Year">
		<af:outputText value="#{report.year}" />
	</af:column>
	<af:column sortProperty="quarter" sortable="true" formatType="text"
		headerText="Quarter">
		<af:selectOneChoice value="#{report.quarter}" readOnly="true">
			<f:selectItem itemLabel="First" itemValue="1"/>
			<f:selectItem itemLabel="Second" itemValue="2"/>
			<f:selectItem itemLabel="Third" itemValue="3"/>
			<f:selectItem itemLabel="Fourth" itemValue="4"/>
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="description" sortable="true" formatType="text"
		headerText="Description">
		<af:outputText truncateAt="20"
			value="#{report.description}" 
			shortDesc="#{report.description}"/>
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Add Report" id="AddReportButton"
					useWindow="true" windowWidth="650" windowHeight="300"
					disabled="false"  rendered="false"
					returnListener="#{homeMonitorGroupDetail.dialogDone}"
					action="#{homeMonitorReportDetail.startAddReport}">
				</af:commandButton>
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>