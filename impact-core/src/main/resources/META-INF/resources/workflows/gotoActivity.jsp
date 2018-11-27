<%@ page session="true" contentType="text/html;charset=utf-8"
  autoFlush="true"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Loop Back">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}" title="Loop Back">
        <jsp:include flush="true" page="../util/header.jsp"/>
        
        <af:panelBorder>

          <f:facet name="left">
            <h:panelGrid border="1" width="266">
              <jsp:include page="activity.jsp" />
            </h:panelGrid>
          </f:facet>

          <af:showDetailHeader text="Select a Task to loop back."
            disclosed="true">
            <af:panelForm labelWidth="15%">
              <af:selectOneChoice label="Loop back to:"
                disabled="#{activityProfile.activityTemplates.size == 0}"
                value="#{activityProfile.gotoActivityTemplateId}">
                <f:selectItems
                  value="#{activityProfile.activityTemplates}" />
              </af:selectOneChoice>
              <af:inputText columns="50" label="Note:" required="true"
                value="#{activityProfile.note}" rows="5" />
            </af:panelForm>
            <af:objectSpacer width="100%" height="15" />
            <afh:rowLayout halign="left">
              <af:panelButtonBar>
                <af:commandButton text="Submit"
                  rendered="#{!activityProfile.readOnlyUser}"
                  action="#{activityProfile.gotoSubmit}" />
              </af:panelButtonBar>
            </afh:rowLayout>
          </af:showDetailHeader>
        </af:panelBorder>

      </af:page>
    </af:form>
  </af:document>
</f:view>

