<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%> 


<af:table id="fceTable" emptyText=" "
	value="#{fceSearch.fceList}" var="fce" bandingInterval="1"
	banding="row">
	<af:column sortable="true" sortProperty="id" formatType="text"
		headerText="Inspection ID">
		<af:outputText rendered="true" value="#{fce.inspId}"/>
	</af:column>
	<jsp:include flush="true" page="../ceta/fceList.jsp" />
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton
					actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}"
					text="Printable view" />
				<af:commandButton
					actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}"
					text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>
