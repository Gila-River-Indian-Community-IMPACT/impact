<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Notes">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>

      <af:page var="foo" value="#{menuModel.model}" title="Notes"
        id="page">
        <jsp:include flush="true" page="../util/header.jsp" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="1000">
            <jsp:include page="processInfo.jsp" flush="true" />
            <jsp:include flush="true" page="noteListTable.jsp" />
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>
