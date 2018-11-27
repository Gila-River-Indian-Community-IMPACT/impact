<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:column sortable="true" sortProperty="testedEus" headerText="EUs" width="20px">
	<af:outputText value="#{st.testedEus}" />
</af:column>
<af:column sortable="true" sortProperty="stackTestMethodCd"
	formatType="text" headerText="Stack Test Method">
	<af:selectOneChoice value="#{st.stackTestMethodCd}" readOnly="true">
		<f:selectItems
			value="#{compEvalDefs.stackTestMethodDef.items[(empty st.stackTestMethodCd ? '' : st.stackTestMethodCd)]}" />
	</af:selectOneChoice>
</af:column>
<af:column sortable="true" sortProperty="listFailedPolls"
	headerText="Failed Pollutants">
	<af:outputText value="#{st.listFailedPolls}" />
</af:column>
<af:column sortable="true" sortProperty="firstVisitDate"
	formatType="text" headerText="Test Date(s)">
	<af:panelHorizontal>
		<af:outputText id="visitDt" value="#{st.datesStrings}" />		
		<af:objectSpacer width="3" height="3" />
		<af:objectImage source="/images/Lock_icon1.png"
			rendered="#{st.afsLocked && st.afsPartialLocked == null}" />
		<af:objectImage source="/images/Lock_icon1Partial.png"
			rendered="#{st.afsLocked && st.afsPartialLocked != null}" />
	</af:panelHorizontal>
</af:column>
<af:column sortable="true" sortProperty="reviewerFullName"
	formatType="text" headerText="Reviewer" width="55px" noWrap="true"
	rendered="#{!stackTests.publicApp}">
	<af:outputText value="#{st.reviewerFullName}" />
</af:column>
<af:column sortable="true" sortProperty="dateScheduled"
	formatType="text" headerText="Date Scheduled" width="60px" noWrap="true">
	<af:selectInputDate readOnly="true" value="#{st.dateScheduled}" />
</af:column>
<af:column sortable="true" sortProperty="dateReceived"
	formatType="text" headerText="Date Received" width="55px" noWrap="true">
	<af:selectInputDate readOnly="true" value="#{st.dateReceived}" />
</af:column>
<af:column sortable="true" sortProperty="dateEvaluated"
	formatType="text" headerText="Date Reviewed" width="60px" noWrap="true">
	<af:selectInputDate readOnly="true" value="#{st.dateEvaluated}" />
</af:column>
<af:column sortable="true" sortProperty="emissionTestState"
	formatType="text" headerText="Test State" width="60px"
	rendered="#{!stackTests.publicApp}" >
	<af:selectOneChoice value="#{st.emissionTestState}" readOnly="true">
		<f:selectItems
			value="#{compEvalDefs.emissionsTestStateDef.items[(empty st.emissionTestState ? '' : st.emissionTestState)]}" />
	</af:selectOneChoice>
	<af:selectInputDate readOnly="true" value="#{st.reminderDate}"
		rendered="#{st.emissionTestState == 'rmdr'}" />
</af:column>
<af:column sortable="true" sortProperty="conformedToTestMethod"
	headerText="Conformed to Test Method" width="70px">
	<af:selectOneChoice value="#{st.conformedToTestMethod}" readOnly="true">
		<f:selectItem itemLabel="Yes" itemValue="Y" />
		<f:selectItem itemLabel="No" itemValue="N" />
	</af:selectOneChoice>
</af:column>
