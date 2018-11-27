<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Change Due Date">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Change Due Date">
        <jsp:include flush="true" page="../util/header.jsp" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="1000">
            <jsp:include page="processInfo.jsp" flush="true" />
            <afh:rowLayout halign="center">
              <af:panelForm width="200">
                <af:selectInputDate label="Jeopardy Date:" required="true" showRequired="true"
                  value="#{workFlow2DDraw.process.jeopardyDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                <af:selectInputDate label="Due Date:" required="true" showRequired="true"
                  value="#{workFlow2DDraw.process.dueDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      rendered="#{!workFlow2DDraw.readOnlyUser}"
                      action="#{workFlow2DDraw.changeDueDate}" />
                    <af:commandButton text="Back to Workflow Diagram"
                      immediate="true" action="#{workFlow2DDraw.reload}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:panelForm>
            </afh:rowLayout>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>
