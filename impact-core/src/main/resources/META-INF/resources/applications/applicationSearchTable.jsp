<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table var="app" width="98%" bandingInterval="1" banding="row"
	value="#{applicationSearch.applications}" id="applicationsTable"
	rows="#{applicationSearch.pageLimit}">
	<%
		// Note: the actionListener below sets the "applicationNumber" attribute
			// of the ApplicationDetail class. This will cause the application to
			// be loaded from the READONLY schema in the portal application, which
			// is the desired behavior since the search table is used to display
			// applications that have already been submitted.
	%>

	<af:column rendered="true" sortable="true"
		sortProperty="applicationNumber" formatType="text"
		headerText="Request Number" width="65px">
		<af:commandLink rendered="true" text="#{app.applicationNumber}"
			action="#{applicationDetail.submitApplicationDetail}">
			<af:setActionListener from="#{app.applicationNumber}"
				to="#{applicationDetail.applicationNumber}" />
			<t:updateActionListener property="#{relocation.fromTODOList}"
				value="false" />
		</af:commandLink>

		<%
			//Relocation Requests handled differently
		%>

		<af:commandLink id="editITR"
			rendered="#{app.relocationRequest && 1==0}"
			text="#{app.applicationNumber}"
			returnListener="#{relocation.dialogDone}"
			action="#{relocation.startViewRequest}" useWindow="true"
			windowWidth="700" windowHeight="580">
			<t:updateActionListener property="#{relocation.relocateRequestID}"
				value="#{app.applicationId}" />
			<t:updateActionListener property="#{relocation.staging}"
				value="false" />
		</af:commandLink>
	</af:column>

	<af:column headerText="Facility ID" sortable="true"
		sortProperty="facilityId" formatType="text" width="70px"
		rendered="#{applicationSearch.fromFacility == 'false'}">
		<af:commandLink text="#{app.facilityId}"
			action="#{facilityProfile.submitProfile}"
			inlineStyle="white-space: nowrap; vertical-align: text-bottom;">
			<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{app.fpId}" />
			<t:updateActionListener property="#{menuItem_facProfile.disabled}"
				value="false" />
		</af:commandLink>
	</af:column>
	<af:column sortable="true" sortProperty="facilityName"
		headerText="Facility Name"
		rendered="#{applicationSearch.fromFacility == 'false'}">
		<af:inputText readOnly="true" value="#{app.facilityName}" />
	</af:column>

	<af:column sortProperty="cmpId" sortable="true" noWrap="true"
		formatType="text" headerText="Company ID" width="80px">
		<af:commandLink action="#{companyProfile.submitProfile}"
			text="#{app.cmpId}">
			<t:updateActionListener property="#{companyProfile.cmpId}"
				value="#{app.cmpId}" />
			<t:updateActionListener
				property="#{menuItem_companyProfile.disabled}" value="false" />
		</af:commandLink>
	</af:column>

	<af:column sortProperty="companyName" sortable="true" noWrap="true"
		formatType="text" headerText="Company Name">
		<af:outputText value="#{app.companyName}" />
	</af:column>

	<af:column sortable="true" sortProperty="doLaaCd" formatType="text"
		headerText="District" width="60px" noWrap="true" rendered="#{infraDefs.districtVisible}">
		<af:selectOneChoice id="doLaa" readOnly="true" value="#{app.doLaaCd}">
			<f:selectItems value="#{infraDefs.districts}" />
		</af:selectOneChoice>
	</af:column>

	<af:column sortable="true" sortProperty="countyCd" formatType="text"
		headerText="County">
		<af:selectOneChoice value="#{app.countyCd}" readOnly="true">
			<f:selectItems value="#{infraDefs.counties}" />
		</af:selectOneChoice>
	</af:column>

	<af:column sortable="true" sortProperty="applicationTypeCd"
		formatType="text" headerText="Request Type" width="100px" noWrap="true">
		<af:selectOneChoice value="#{app.applicationTypeCd}" readOnly="true">
			<f:selectItems value="#{applicationReference.applicationTypeDefs}" />
		</af:selectOneChoice>
	</af:column>
	<af:column sortable="true" sortProperty="receivedDate"
		formatType="text" headerText="Received Date" width="60px">
		<af:switcher defaultFacet="NoDate"
			facetName="#{empty app.receivedDate? 'NoDate' : 'Date'}">
			<f:facet name="NoDate">
				<af:outputText value="Not Available" />
			</f:facet>
			<f:facet name="Date">
				<af:selectInputDate readOnly="true" value="#{app.receivedDate}" />
			</f:facet>
		</af:switcher>
	</af:column>
	<af:column sortable="true" sortProperty="submittedDate"
		formatType="text" headerText="Submitted?" width="75px">
		<af:outputText value="#{empty app.submittedDate ? 'No': 'Yes'}" />
	</af:column>
	<af:column sortable="true" sortProperty="previousApplicationNumber"
		formatType="text" headerText="Previous Application Number" width="75px">
		<af:outputText
			value="#{empty app.previousApplicationNumber ? 'N/A' : app.previousApplicationNumber}" />
	</af:column>
	<af:column sortable="true" sortProperty="permitNumbers"
		formatType="text" headerText="Permit Number(s)" width="128px">
		<af:outputText value="#{app.permitNumbers}" />
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="New Request" useWindow="true"
					windowWidth="700" windowHeight="450" id="newApplicationRequest"
					action="#{applicationSearch.startNewApplication}"
					disabled="#{applicationSearch.readOnlyUser}"
					rendered="#{applicationSearch.fromFacility == 'true' && facilityProfile.dapcUser}" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>

