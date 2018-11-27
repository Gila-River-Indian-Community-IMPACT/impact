<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Reassign Task">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Reassign Task">
        <jsp:include flush="true" page="../util/header.jsp" />

        <mu:setProperty property="#{reassignActivities.command}"
          value="setActivityTemplateId" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="0" width="1000">
            <af:panelBorder>
              <f:facet name="left">
                <h:panelGrid border="1" width="266">
                  <jsp:include page="activity.jsp" />
                </h:panelGrid>
              </f:facet>

              <jsp:include flush="true" page="reassignTable.jsp" />
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>
