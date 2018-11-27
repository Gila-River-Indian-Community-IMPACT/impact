<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Workflow Cloning">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Workflow Cloning">
        <jsp:include flush="true" page="../util/header.jsp" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="1000">
            <jsp:include page="processInfo.jsp" flush="true" />
            <afh:rowLayout halign="center">
              <af:panelForm width="200">
                <t:outputText style="color:#FF0000;"
                  value="Click 'Submit' button to confirm the cloning." />
                <af:objectSpacer width="100%" height="15" />

                <af:panelButtonBar>
                  <af:commandButton text="Submit"
                    disabled="#{workFlow2DDraw.readOnlyUser}"
                    action="#{workFlow2DDraw.submitClone}" />
                  <af:commandButton text="Back to Workflow Diagram"
                    immediate="true" action="#{workFlow2DDraw.reload}" />
                </af:panelButtonBar>
              </af:panelForm>
            </afh:rowLayout>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>

