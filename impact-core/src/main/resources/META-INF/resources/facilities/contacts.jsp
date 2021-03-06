<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Contacts">
		    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Contacts">
				<af:inputHidden value="#{submitTask.popupRedirect}" rendered="#{!facilityProfile.dapcUser}" />
				
				<jsp:include page="header.jsp" />
			
			<f:subview id="comFacContacts">
          			<jsp:include flush="true" page="comContacts.jsp" />
			</f:subview>
			
			</af:page>
			<af:iterator value="#{facilityProfile}" var="validationBean" id="v">
          		<%@ include file="../util/validationComponents.jsp"%>
            </af:iterator>
		</af:form>
	</af:document>
</f:view>

