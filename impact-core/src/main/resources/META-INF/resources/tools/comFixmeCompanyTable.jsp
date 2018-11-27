<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table id="fixmeCompanyTable"
	value="#{bulkFixmeOwnership.fixmeCompanies}" width="1000"
	bandingInterval="1" banding="row" var="fixmeCompany"
	rows="#{bulkFixmeOwnership.pageLimit}">

	<af:column noWrap="true" formatType="text" headerText="Facility ID">
		<af:commandLink action="#{facilityProfile.submitProfile}"
			text="#{fixmeCompany.facilityId}">
			<t:updateActionListener property="#{facilityProfile.fpId}"
				value="#{fixmeCompany.fpId}" />
			<t:updateActionListener property="#{menuItem_facProfile.disabled}"
				value="false" />
		</af:commandLink>
	</af:column>
	<af:column formatType="text" headerText="Old Owner">
		<af:outputText value="#{fixmeCompany.oldCompanyName}" />
	</af:column>
	<af:column formatType="text" headerText="Start Date">
		<af:selectInputDate value="#{fixmeCompany.oldStartDate}"
			readOnly="true" />
	</af:column>
	<af:column formatType="text" headerText="End Date">
		<af:selectInputDate value="#{fixmeCompany.oldEndDate}" readOnly="true" />
	</af:column>
	<af:column formatType="text" headerText="New Owner">
		<af:outputText value="#{fixmeCompany.newCompanyName}" />
	</af:column>
	<af:column formatType="text" headerText="Start Date">
		<af:selectInputDate value="#{fixmeCompany.newStartDate}"
			readOnly="true" />
	</af:column>
	<af:column formatType="text" headerText="End Date">
		<af:selectInputDate value="#{fixmeCompany.newEndDate}" readOnly="true" />
	</af:column>
</af:table>