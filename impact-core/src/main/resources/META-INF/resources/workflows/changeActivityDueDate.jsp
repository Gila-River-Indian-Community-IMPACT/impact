<%@ page session="true" contentType="text/html;charset=utf-8"
  autoFlush="true"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Change Due Date">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Change Due Date">
        <jsp:include flush="true" page="../util/header.jsp"/>

        <af:panelBorder>

          <f:facet name="left">
            <h:panelGrid border="1" width="266">
              <jsp:include page="activity.jsp" />
            </h:panelGrid>
          </f:facet>

          <af:panelForm>
            <af:selectInputDate label="Jeopardy Date:" required="true" showRequired="true"
              value="#{activityProfile.changeJeopardyDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
            <af:selectInputDate label="Due Date:" required="true" showRequired="true"
              value="#{activityProfile.changeDueDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

          </af:panelForm>

          <af:objectSpacer width="100%" height="15" />
          <afh:rowLayout halign="center">
            <af:panelButtonBar>
              <af:commandButton text="Submit"
                action="#{activityProfile.changeDueDate}" />
            </af:panelButtonBar>
          </afh:rowLayout>

        </af:panelBorder>

      </af:page>
    </af:form>
  </af:document>
</f:view>

