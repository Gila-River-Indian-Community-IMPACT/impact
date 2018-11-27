<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Transfer Emission Units">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:inputHidden value="#{trEu.popupRedirect}" />
			<af:page var="foo" id="trEu" value="#{menuModel.model}"
				title="Transfer Emission Units">

				<jsp:include page="header.jsp" />

				<afh:rowLayout halign="center">
					<f:subview id="comTrEu">
						<jsp:include flush="true" page="comTrEu.jsp" />
					</f:subview>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>