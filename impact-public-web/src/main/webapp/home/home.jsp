<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<f:view>
	<af:document id="body" title="IMPACT Home" >
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" >
				
				<jsp:include flush="true" page="../util/header.jsp" />
				
				<af:outputFormatted value="</b>IMPACT Public Web Module</b>" />
			</af:page>
		</af:form>
	</af:document>
</f:view>