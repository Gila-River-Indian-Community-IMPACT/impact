<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Facility CEM/COM/CMS Limits">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" id="homeFacilityCemComLimits"
				value="#{menuModel.model}" title="Facility CEM/COM/CMS Limits">

				<jsp:include page="header.jsp" />

				<f:subview id="comFacilityCemComLimits">
					<jsp:include flush="true" page="comFacilityCemComLimits.jsp" />
				</f:subview>

			</af:page>
		</af:form>
	</af:document>
</f:view>