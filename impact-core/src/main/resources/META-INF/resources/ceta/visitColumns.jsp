<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:column sortProperty="visitDate" sortable="true"
	formatType="text" headerText="Visit Date" width="70px" noWrap="true">	
	<af:selectInputDate id="vdate" readOnly="true"
			value="#{sv.visitDate}" >
			<af:validateDateTimeRange minimum="1900-01-01" />
		</af:selectInputDate>
</af:column>
<af:column sortable="true" sortProperty="visitType"
	headerText="Visit Type" formatType="text" noWrap="true" width="60px">
	<af:selectOneChoice value="#{sv.visitType}" readOnly="true">
		<f:selectItems
			value="#{compEvalDefs.siteVisitTypeDef.items[(empty sv.visitType ? '' : sv.visitType)]}" />
	</af:selectOneChoice>
</af:column>
<af:column sortable="true" sortProperty="complianceIssued" 
	headerText="Compliance Issue" formatType="text" width="80px">
	<af:selectOneChoice value="#{sv.complianceIssued}" readOnly="true">
	<f:selectItem itemLabel="Yes" itemValue="Y" />
		<f:selectItem itemLabel="No" itemValue="N" />
	</af:selectOneChoice>
</af:column>
<af:column sortable="true" sortProperty="evaluatorNames"
	formatType="text" headerText="Evaluator(s)" noWrap="true" width="80px">
	<af:outputText id="eval" value="#{sv.evaluatorNames}" />
</af:column>
