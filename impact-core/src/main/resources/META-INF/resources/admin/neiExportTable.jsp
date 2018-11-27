<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table value="#{neiExport.neiList}" var="nei" width="500">
	<af:column headerText="File(s)">
		<af:outputText value="#{nei.name}" />
	</af:column>
	<af:column headerText="Link to File">
		<af:goLink destination="#{nei.value}" text="Link" />
	</af:column>
</af:table>
