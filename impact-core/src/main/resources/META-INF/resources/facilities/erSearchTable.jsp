<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table emptyText=" " value="#{reportSearch.reports}" bandingInterval="1" width="98%"
	banding="row" var="reportResults" >
	<jsp:include page="../reports/erSearchTableAttribs.jsp" />
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="New Emissions Inventory"
					action="#{facilityProfile.createNewEmissionReport}"
					disabled="#{reportSearch.readOnlyUser}"
					rendered="#{!reportSearch.fromFacility=='false' && reportSearch.internalApp}"
					id="CreateNewReportButton2" useWindow="true" windowWidth="1200"
					windowHeight="600">
				</af:commandButton>
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>

