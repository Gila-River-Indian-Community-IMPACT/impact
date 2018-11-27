<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{monitorGroupDetail.associatedSitesWrapper}" bandingInterval="1"
	binding="#{monitorGroupDetail.associatedSitesWrapper.table}" id="AssociatedSitesTab"
	banding="row" width="98%" var="site"
	rows="#{monitorGroupDetail.pageLimit}">
	<af:column sortProperty="mstId" sortable="true" formatType="text"
		headerText="Site ID">
		<af:commandLink action="#{monitorSiteDetail.submitFromJsp}"
			disabled="#{monitorGroupDetail.editable}">
			<af:outputText value="#{site.mstId}" />
			<t:updateActionListener
				property="#{monitorSiteDetail.siteId}"
				value="#{site.siteId}" />
		</af:commandLink>
	</af:column>
	<af:column sortProperty="siteObjective" sortable="true" formatType="text"
		headerText="Site Objective">
		<af:outputText value="#{site.siteObjective}" />
	</af:column>
	<af:column sortProperty="aqsSiteId" sortable="true" formatType="text"
		headerText="AQS Site ID">
		<af:outputText value="#{site.aqsSiteId}" />
	</af:column>
	<af:column sortProperty="status" sortable="true" formatType="text"
		headerText="Status">
		<af:selectOneChoice value="#{site.status}" readOnly="true">
			<f:selectItems value="#{monitorDetail.statusDefs.allItems}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortProperty="startDate" sortable="true" formatType="text"
		headerText="Start Date">
		<af:selectInputDate value="#{site.startDate}" readOnly="true" />
	</af:column>
	<af:column sortProperty="endDate" sortable="true" formatType="text"
		headerText="End Date">
		<af:selectInputDate value="#{site.endDate}" readOnly="true" />
	</af:column>
	<af:column sortProperty="latLon" sortable="true" formatType="text"
		headerText="Lat/Long">
		<af:goLink text="#{site.latLon}" targetFrame="_new"
			rendered="#{not empty site.latLon}"
			destination="#{site.googleMapsURL}" inlineStyle="padding:5px;"
			shortDesc="Clicking this will open Google Maps in a separate tab or window." />
	</af:column>
	<af:column sortProperty="siteName" sortable="true" formatType="text"
		headerText="Site Name">
		<af:outputText value="#{site.siteName}" />
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Add Site" id="AddSiteButton"
					useWindow="true" windowWidth="650" windowHeight="650"
					disabled="#{monitorGroupDetail.readOnlyUser}"
					returnListener="#{monitorGroupDetail.associatedSitesDialogDone}" 
					rendered="#{monitorGroupDetail.internalApp && !monitorGroupDetail.readOnlyUser 
									&& !monitorGroupDetail.monitorReportUploadUser}"
					action="#{monitorGroupDetail.startAddSite}">
				</af:commandButton>
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>
