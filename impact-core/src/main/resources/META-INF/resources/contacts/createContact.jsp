<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Create Contact">
		<f:verbatim>
			<script>
                </f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
            </script>

		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" id="createContact"
				title="Create Contact">
				<af:inputHidden value="#{createContact.popupRedirect}" />
				<jsp:include page="../util/header.jsp" />

				<f:subview id="contact">
					<jsp:include flush="true" page="newContact.jsp" />
				</f:subview>
			</af:page>
		</af:form>
	</af:document>
</f:view>

