<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Workflow">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}" title="Workflow">
        <%@ include file="../util/header.jsp"%>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1">

            <af:panelBorder>
              <af:showDetailHeader text="Init Workflow" disclosed="true">
                <af:panelForm width="200">

                  <af:inputText label="Permit/Report ID" columns="5"
                    value="#{initWorkflow.externalId}" />

                  <af:selectOneChoice label="Process" required="true"
                    value="#{initWorkflow.workflowId}"
                    unselectedLabel="-- Select one --">
                    <f:selectItems value="#{workFlowDefs.workflows}" />
                  </af:selectOneChoice>

                  <af:selectOneChoice label="User" required="true"
                    value="#{initWorkflow.userId}"
                    unselectedLabel="-- Select one --">
                    <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
                  </af:selectOneChoice>

                  <f:facet name="footer">
                    <af:panelButtonBar>
                      <af:objectSpacer width="100%" height="20" />
                      <af:commandButton text="Init"
                        action="#{initWorkflow.submit}" />
                      <af:commandButton text="Reset" immediate="true"
                        action="#{initWorkflow.reset}" />
                    </af:panelButtonBar>
                  </f:facet>


                </af:panelForm>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>



      </af:page>
    </af:form>
  </af:document>
</f:view>

