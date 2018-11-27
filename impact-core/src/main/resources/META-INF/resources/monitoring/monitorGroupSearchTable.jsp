<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table emptyText=" " value="#{monitorGroupSearch.resultsWrapper}"
	binding="#{monitorGroupSearch.resultsWrapper.table}" width="98%"
	bandingInterval="1" banding="row" var="monitorGroupList"
	rows="#{monitorGroupSearch.pageLimit}">

	<af:column sortProperty="mgrpId" sortable="true" formatType="text"
		headerText="Monitor Group ID">
		<af:commandLink action="#{monitorGroupDetail.submitFromJsp}">
			<af:outputText value="#{monitorGroupList.mgrpId}" />
			<t:updateActionListener
				property="#{monitorGroupDetail.groupId}"
				value="#{monitorGroupList.groupId}" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="groupName" sortable="true" formatType="text"
		headerText="Monitor Group Name">
		<af:commandLink action="#{monitorGroupDetail.submitFromJsp}">
			<af:outputText value="#{monitorGroupList.groupName}" />
			<t:updateActionListener
				property="#{monitorGroupDetail.groupId}"
				value="#{monitorGroupList.groupId}" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="companyName" sortable="true" formatType="text"
		rendered="#{!monitorGroupSearch.publicApp}"
		headerText="Company Name">
		<af:outputText value="#{monitorGroupList.companyName}" />
	</af:column>
	
	<af:column sortProperty="facilityId" sortable="true" formatType="text"
		rendered="#{!monitorGroupSearch.publicApp}"
		headerText="Facility ID">
		<af:commandLink action="#{facilityProfile.submitProfile}"
			text="#{monitorGroupList.facilityId}">
			<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{monitorGroupList.fpId}" />
		</af:commandLink>
	</af:column>
	
	<af:column sortProperty="facilityName" sortable="true" formatType="text"
		rendered="#{!monitorGroupSearch.publicApp}"
		headerText="Facility Name">
		<af:outputText value="#{monitorGroupList.facilityName}" />
	</af:column>

	<af:column sortProperty="monitorReviewerName" sortable="true"
		rendered="#{!monitorGroupSearch.publicApp}"
		formatType="text" headerText="Monitor Reviewer">
		<af:selectOneChoice label="Monitor Reviewer"
			readOnly="true" value="#{monitorGroupList.monitorReviewerId}">
			<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
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
