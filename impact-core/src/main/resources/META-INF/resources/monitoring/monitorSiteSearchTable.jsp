<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table emptyText=" " value="#{monitorSiteSearch.resultsWrapper}"
	binding="#{monitorSiteSearch.resultsWrapper.table}" width="98%"
	bandingInterval="1" banding="row" var="monitorSiteList"
	rows="#{monitorSiteSearch.pageLimit}">
	
	<af:column sortProperty="mstId" sortable="true" formatType="text"
		headerText="Monitor Site ID">
		<af:commandLink action="#{monitorSiteDetail.submitFromJsp}">
			<af:outputText value="#{monitorSiteList.mstId}" />
			<t:updateActionListener
				property="#{monitorSiteDetail.siteId}"
				value="#{monitorSiteList.siteId}" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="siteName" sortable="true" formatType="text"
		headerText="Monitor Site Name">
		<af:commandLink action="#{monitorSiteDetail.submitFromJsp}">
			<af:outputText value="#{monitorSiteList.siteName}" />
			<t:updateActionListener
				property="#{monitorSiteDetail.siteId}"
				value="#{monitorSiteList.siteId}" />
		</af:commandLink>
	</af:column>

	<af:column sortProperty="mgrpId" sortable="true" formatType="text"
		headerText="Monitor Group ID">
		<af:commandLink action="#{monitorGroupDetail.submitFromJsp}">
			<af:outputText value="#{monitorSiteList.mgrpId}" />
			<t:updateActionListener
				property="#{monitorGroupDetail.groupId}"
				value="#{monitorSiteList.groupId}" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="groupName" sortable="true" formatType="text"
		headerText="Monitor Group Name">
		<af:commandLink action="#{monitorGroupDetail.submitFromJsp}">
			<af:outputText value="#{monitorSiteList.groupName}" />
			<t:updateActionListener
				property="#{monitorGroupDetail.groupId}"
				value="#{monitorSiteList.groupId}" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="facilityId" sortable="true" formatType="text"
		headerText="Facility ID">
		<af:commandLink action="#{facilityProfile.submitProfile}"
			text="#{monitorSiteList.facilityId}">
			<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{monitorSiteList.fpId}" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="facilityName" sortable="true" formatType="text"
		headerText="Facility Name">
		<af:outputText value="#{monitorSiteList.facilityName}" />
	</af:column>
	
	<af:column sortProperty="companyName" sortable="true" formatType="text"
		headerText="Company Name">
		<af:outputText value="#{monitorSiteList.companyName}" />
	</af:column>
	
	<af:column sortProperty="county" sortable="true" formatType="text"
		headerText="County">
		<af:outputText value="#{monitorSiteList.countyName}" />
	</af:column>
	
	<af:column sortProperty="status" sortable="true" formatType="text"
		headerText="Status">
		<af:selectOneChoice value="#{monitorSiteList.status}" readOnly="true">
			<f:selectItems value="#{monitorDetail.statusDefs.allItems}" />
		</af:selectOneChoice>
	</af:column>
	
	<af:column sortProperty="aqsSiteId" sortable="true" formatType="text"
		headerText="AQS Site ID">
		<af:outputText value="#{monitorSiteList.aqsSiteId}" />
	</af:column>
	
	<af:column sortProperty="latLon" sortable="true" formatType="text"
		headerText="Lat/Long">
		<af:goLink text="#{monitorSiteList.latLon}" targetFrame="_new"
			rendered="#{not empty monitorSiteList.latLon}"
			destination="#{monitorSiteList.googleMapsURL}" inlineStyle="padding:5px;"
			shortDesc="Clicking this will open Google Maps in a separate tab or window." />
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
