<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

    <f:view>
      <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Release Points">
            <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
          <af:page var="foo" id="egressPoints" value="#{menuModel.model}" title="Release Points">

			<jsp:include page="facilityHeader.jsp" />		
			
          	<f:subview id="comEgpSummary">
          		<jsp:include flush="true" page="comEgpSummary.jsp" />
          	</f:subview>
                  
          </af:page>
        </af:form>
      </af:document>
    </f:view>