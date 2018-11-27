<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Stack Test Detail">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
			    title="Stack Test Detail">
			    <af:inputHidden value="#{submitTask.popupRedirect}"
					rendered="#{facilityProfile.portalApp}" />
				<af:inputHidden value="#{submitTask.logUserOff}"
					rendered="#{facilityProfile.portalApp}" />
				<af:inputHidden value="#{stackTests.popupRedirect}" 
					rendered="#{facilityProfile.internalApp}"/>
				
				<jsp:include flush="true" page="header.jsp" />
				
				<afh:rowLayout halign="center" width="1000" rendered="#{!stackTestDetail.error}">
				  <h:panelGrid border="1">
					<af:panelBorder rendered="#{!stackTestDetail.error}">
				      <%@ include file="stackTestDetail2.jsp"%>
				    </af:panelBorder>
				  </h:panelGrid>
				</afh:rowLayout>
			</af:page>
			<af:iterator value="#{stackTestDetail}" var="validationBean" id="v">
				<%@ include file="../util/validationComponents.jsp"%>
			</af:iterator>
		<%-- hidden controls for navigation to compliance report from popup --%>
		<%@ include file="../util/hiddenControls.jsp"%>
		</af:form>
	</af:document>
</f:view>
