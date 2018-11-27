<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
  
<f:view>
	<af:document id="body" title="Trade Secret Claims Made" onmousemove="#{infraDefs.iframeResize}" 
		onload="#{infraDefs.iframeReload}">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>			
				<jsp:include page="../facilities/valTradSecNotif.jsp" />				
		</af:form>
	</af:document>
</f:view>