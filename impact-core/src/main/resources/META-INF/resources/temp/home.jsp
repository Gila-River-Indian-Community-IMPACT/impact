<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Home">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}" title="Home">
        <f:facet name="nodeStamp">
          <af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
            type="#{foo.type}" disabled="#{foo.disabled}" rendered="#{foo.rendered}" />
        </f:facet>
        <f:facet name="messages">
          <af:messages />
        </f:facet>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="950">
            <af:panelBorder>
              <af:showDetailHeader text="MyTasks" disclosed="true">
                <af:panelForm rows="2">
                  <af:table value="#{myTasks.tasks}" bandingInterval="2"
                    banding="row" var="task" width="98%">
                    <f:facet name="selection">
                      <af:tableSelectOne />
                    </f:facet>
                    <af:column sortProperty="taskType" sortable="true"
                      headerText="Task Type">
                      <af:outputText value="#{task.taskType}" />
                    </af:column>
                    <af:column sortProperty="taskName" sortable="true"
                      headerText="Task Name">
                      <af:commandLink text="#{task.taskName}"
                        action="#{myTasks.submitTask}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener value="#{task}" property="#{myTasks.task}"/>
                      </af:commandLink>
                    </af:column>
                    <af:column sortProperty="taskDescription" sortable="true"
                      headerText="Task Description">
                      <af:outputText value="#{task.taskDescription}" />
                    </af:column>
                    <f:facet name="footer">
                      <afh:rowLayout halign="center">
                        <af:panelButtonBar>
                          <af:commandButton
                            actionListener="#{tableExporter.printTable}"
                            onclick="#{tableExporter.onClickScript}"
                            text="Printable view" />
                          <af:commandButton
                            actionListener="#{tableExporter.excelTable}"
                            onclick="#{tableExporter.onClickScript}"
                            text="Export to excel" />
                        </af:panelButtonBar>
                      </afh:rowLayout>
                    </f:facet>
                  </af:table>
                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Add" action="#{myTasks.add}" />
                    <af:commandButton text="Delete" action="#{myTasks.delete}" />
                    <af:commandButton text="Submit" action="#{myTasks.submit}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <jsp:include flush="true" page="todoTable.jsp" />

      </af:page>
    </af:form>
  </af:document>
</f:view>
