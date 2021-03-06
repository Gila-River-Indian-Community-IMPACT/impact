<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:column sortable="true" sortProperty="fullName" formatType="text"
	headerText="Staff Assigned" width="100px" noWrap="true">
	<af:outputText value="#{fce.fullName}" />
</af:column>
<af:column sortable="true" sortProperty="scheduledTimestamp"
	formatType="text" headerText="Quarter Scheduled" width="60px" noWrap="true">
		<af:outputText value="#{fce.scheduled}" />
</af:column>
<af:column sortable="true" sortProperty="evalFullName" formatType="text"
	headerText="Inspector" width="40px" noWrap="true">
	<af:outputText value="#{fce.evalFullName}" />
</af:column>
<af:column sortable="true" sortProperty="dateEvaluated"
	formatType="text" headerText="Date Inspection Completed" width="73px">
		<af:selectInputDate readOnly="true" value="#{fce.dateEvaluated}">
		        <f:convertDateTime pattern="MM/dd/yyyy"/>
		</af:selectInputDate>
</af:column>
<af:column sortable="true" 
	formatType="text" headerText="Report Document">
	<af:goLink text="Inspection Report"
			targetFrame="_blank"	rendered="#{null ne fce.inspectionReportDocumentUrl}"
              destination="#{fce.inspectionReportDocumentUrl}"/>
</af:column>

