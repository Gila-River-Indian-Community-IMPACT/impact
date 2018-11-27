<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <af:document title="Workflow List">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:page id="ThePage" var="foo" value="#{menuModel.model}"
        title="Workflow List">
        <%@ include file="../permits/header.jsp"%>
        
        <h:panelGrid border="1">
          <af:panelBorder>

            <f:facet name="top">
              <f:subview id="permitDetailTop">
                <jsp:include page="permitDetailTop.jsp" />
              </f:subview>
            </f:facet>

            <afh:rowLayout halign="center">
              <h:panelGrid border="1" width="#{permitDetail.permitWidth}">
                <af:panelBorder>
                  <jsp:include flush="true" page="../home/todoTable.jsp" />
                </af:panelBorder>
              </h:panelGrid>
            </afh:rowLayout>
          </af:panelBorder>
        </h:panelGrid>
      </af:page>
    </af:form>
  </af:document>
</f:view>
