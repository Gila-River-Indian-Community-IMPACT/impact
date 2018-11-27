<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Summary">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}" title="Summary">
      <jsp:include flush="true" page="../util/header.jsp" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="1000">
            <jsp:include page="processInfo.jsp" flush="true" />
            <af:showDetailHeader text="Task List" disclosed="true">
              <af:table bandingInterval="1" banding="row" var="tasks"
                emptyText=" " rows="10" width="98%"
                value="#{workFlow2DDraw.activities}">

                <af:column sortProperty="activityTemplateId"
                  sortable="true" width="40">
                  <f:facet name="header">
                    <af:outputText value="Task" />
                  </f:facet>
                  <af:commandLink rendered="#{tasks.processId != null}"
                    action="#{activityProfile.submitProfile}">
                    <af:outputText value="#{tasks.activityTemplateNm}" />
                    <t:updateActionListener
                      property="#{menuItem_activityProfile.disabled}"
                      value="false" />
                    <t:updateActionListener
                      property="#{activityProfile.processId}"
                      value="#{tasks.processId}" />
                    <t:updateActionListener
                      property="#{activityProfile.loopCnt}"
                      value="#{tasks.loopCnt}" />
                    <t:updateActionListener
                      property="#{activityProfile.fromExternal}"
                      value="true" />
                    <t:updateActionListener
                      property="#{activityProfile.activityTemplateId}"
                      value="#{tasks.activityTemplateId}" />
                    <t:updateActionListener
                      property="#{activityProfile.aggregate}"
                      value="#{tasks.aggregate}" />
                  </af:commandLink>
                    <af:outputText value="#{tasks.activityTemplateNm}" rendered="#{tasks.processId == null}"/>
                </af:column>

                <af:column sortProperty="loopCnt" sortable="true">
                  <f:facet name="header">
                    <af:outputText value="Loop" />
                  </f:facet>
                  <af:outputText value="#{tasks.loopCnt}" />
                </af:column>

                <af:column sortProperty="userId" sortable="true">
                  <f:facet name="header">
                    <af:outputText value="Who" />
                  </f:facet>
                  <af:selectOneChoice value="#{tasks.userId}"
                    readOnly="true">
                    <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
                  </af:selectOneChoice>
                </af:column>

                <af:column sortProperty="startDt" sortable="true">
                  <f:facet name="header">
                    <af:outputText value="Start Date" />
                  </f:facet>
                  <af:selectInputDate readOnly="true" value="#{tasks.startDt}" />
                </af:column>

                <af:column sortProperty="endDt" sortable="true">
                  <f:facet name="header">
                    <af:outputText value="End Date" />
                  </f:facet>
                  <af:selectInputDate readOnly="true" value="#{tasks.endDt}" />
                </af:column>

                <af:column sortProperty="activityStatusCd"
                  sortable="true">
                  <f:facet name="header">
                    <af:outputText value="State" />
                  </f:facet>
                  <af:selectOneChoice label="State"
                    value="#{tasks.activityStatusCd}" readOnly="true">
                    <f:selectItems
                      value="#{workFlowDefs.activityStatus}" />
                  </af:selectOneChoice>
                </af:column>

                <af:column sortProperty="status" sortable="true">
                  <f:facet name="header">
                    <af:outputText value="Status" />
                  </f:facet>
                  <h:panelGrid border="0" width="100%" align="center"
                    bgcolor="#{todos.statusColor}">
                    <af:selectOneChoice label="Status"
                      value="#{tasks.status}" readOnly="true">
                      <f:selectItems value="#{workFlowDefs.statusDef}" />
                    </af:selectOneChoice>
                  </h:panelGrid>
                </af:column>

                <af:column sortProperty="duration" sortable="true">
                  <f:facet name="header">
                    <af:outputText value="Company" />
                  </f:facet>
                  <af:outputText value="#{tasks.companyDuration}" />
                </af:column>

                <af:column sortProperty="duration" sortable="true">
                  <f:facet name="header">
                    <af:outputText value="AQD" />
                  </f:facet>
                  <af:outputText value="#{tasks.aqdDuration}" />
                </af:column>

               <af:column sortProperty="duration" sortable="true">
                  <f:facet name="header">
                    <af:outputText value="Legal" />
                  </f:facet>
                  <af:outputText value="#{tasks.legalDuration}" />
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
            </af:showDetailHeader>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>
