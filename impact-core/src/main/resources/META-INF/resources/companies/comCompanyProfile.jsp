<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<h:panelGrid border="0">
	<af:panelBorder>

		<f:facet name="right">
			<h:panelGrid columns="1" border="1" width="1000"
				rendered="#{companyProfile.company != null}"
				style="margin-left:auto;margin-right:auto;">


				<f:subview id="company">
					<jsp:include flush="true" page="../companies/company.jsp" />
				</f:subview>


			</h:panelGrid>
		</f:facet>

		<f:facet name="bottom">
		</f:facet>

	</af:panelBorder>
</h:panelGrid>
