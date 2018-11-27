<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document title="Compliance Report Type Selection:">
		<af:page title="Compliance Report Type Selection:">
			<af:table 
				value="#{complianceReport.complianceReportCategoryInfoWrapper}"	var="crptCatInfo"
				rows="0">
				<af:column formatType="text" headerText="Report Type">
					<af:inputText readOnly="true" value="#{crptCatInfo.reportTypeDesc}"/>
				</af:column>
				<af:column formatType="text" headerText="Report Category">
					<af:inputText readOnly="true" value="#{crptCatInfo.categoryTypeDesc}"/>
				</af:column>
				<af:column formatType="text" headerText="Explanation">
					<af:inputText readOnly="true" value="#{crptCatInfo.explanation}"/>
				</af:column>
			</af:table>
		</af:page>
	</af:document>
</f:view>