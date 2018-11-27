<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table var="sv" bandingInterval="1" banding="row" width="100%"
	value="#{siteVisitSearch.siteVisits}"
	rows="#{siteVisitSearch.pageLimit}">

	<%@ include file="firstVisitColumns.jsp"%>
	<%@ include file="visitColumns.jsp"%>
	<af:column sortable="true" sortProperty="fceId" formatType="icon"
		headerText="Inspection ID">
		<af:commandLink rendered="true" text="#{sv.inspId}"
			action="#{fceDetail.submit}">
			<af:setActionListener from="#{sv.fceId}" to="#{fceDetail.fceId}" />
			<af:setActionListener from="#{sv.facilityId}"
				to="#{fceDetail.facilityId}" />
			<af:setActionListener from="#{true}"
				to="#{fceDetail.existingFceObject}" />
			<af:setActionListener from="#{sv.fceId}" to="#{fceSiteVisits.fceId}" />
		</af:commandLink>
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
