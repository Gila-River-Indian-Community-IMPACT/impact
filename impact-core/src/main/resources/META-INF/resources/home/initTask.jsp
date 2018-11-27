<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Task">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}" title="Task">
        <%@ include file="../util/header.jsp"%>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1">

            <af:panelBorder>
              <af:showDetailHeader text="New Task" disclosed="true">
                <af:panelForm width="200">

                  <af:inputText label="Facility Id" columns="20" showRequired="true"
                    value="#{initWorkflow.facilityId}" />

                  <mu:setProperty property="#{initWorkflow.workflowId}"
                    value="0" />

                  <af:selectOneChoice label="User"
                    unselectedLabel="-- To all roles in selected Facility --"
                    value="#{initWorkflow.userId}">
                    <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
                  </af:selectOneChoice>

                  <af:selectInputDate label="Due Date"
                  	showRequired="true"
                    value="#{initWorkflow.dueDate}" > 
                    <af:validateDateTimeRange minimum="#{initWorkflow.minimumDueDt}"/>
                  </af:selectInputDate>

                  <af:inputText label="Task Name" columns="20" showRequired="true"
                    value="#{initWorkflow.subject}" />

                  <af:inputText label="Comment" columns="20" rows="5" showRequired="true"
                    value="#{initWorkflow.comment}" />

                  <f:facet name="footer">
                    <af:panelButtonBar>
                      <af:commandButton text="Create"
                        action="#{initWorkflow.submit}" 
                        disabled="#{facilityProfile.readOnlyUser}"/>
                      <af:commandButton text="Reset"
                    	immediate="true"
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

