<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="CEM/COM/CMS Monitors">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" id="continuousMonitors" value="#{menuModel.model}"
				title="CEM/COM/CMS Monitors">
				<af:inputHidden value="#{continuousMonitorSearch.popupRedirect}" />

				<jsp:include page="facilityHeader.jsp" />

				<f:subview id="comContinuousMonitors">
					<jsp:include flush="true" page="comContinuousMonitors.jsp" />
				</f:subview>


			</af:page>
			<af:iterator value="#{continuousMonitorDetail}" var="validationBean"
				id="v">
				<%@ include file="../util/validationComponents.jsp"%>
			</af:iterator>
			<%-- hidden controls for navigation to compliance report from popup --%>
			<%@ include file="../util/hiddenControls.jsp"%>
		</af:form>
	</af:document>
</f:view>

