<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Workflow Search">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Workflow Search">
        <jsp:include flush="true" page="../util/header.jsp" />

        <mu:setProperty property="#{workflowSearch.by}"
          value="#{param.by}" />
        <mu:setProperty property="#{workflowSearch.section}"
          value="#{param.section}" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="1000">
            <af:panelBorder>
              <af:showDetailHeader text="Search Criteria"
                disclosed="true">
                <af:panelForm rows="3" maxColumns="10" width="200"
                  partialTriggers="workflowSearch_WorkflowTypes">

                  <af:inputText columns="10" maximumLength="10" label="Facility ID"
                    value="#{workflowSearch.facilityId}"
                    tip="0000000000, 0%, %0%, *0*, 0*"
                    id="workflowSearch_FacilityID" />

                  <af:inputText columns="25" label="Facility Name"
                    value="#{workflowSearch.facilityNm}"
                    tip="acme%, %acme%, *acme*, acme*"
                    id="workflowSearch_FacilityName" />
                   <t:div style="text-align:right; width: 80px;">
      					 <af:outputLabel value="" />
   					 </t:div>
                  <af:selectOneChoice label="Workflow Type"
                    autoSubmit="TRUE" unselectedLabel=" "
                    id="workflowSearch_WorkflowTypes"
                    value="#{workflowSearch.processTypeCd}">
                    <f:selectItems value="#{workFlowDefs.processTypeDefs}" />
                  </af:selectOneChoice>

                  <af:selectOneChoice label="Workflow Name"
                    unselectedLabel=" "
                    value="#{workflowSearch.processTemplateNm}"
                    id="workflowSearch_WorkflowName" >
                    <f:selectItems value="#{workflowSearch.workflows}" />
                  </af:selectOneChoice>
                  
                  <af:selectManyListbox label="Workflow State"
					size="3"
					value="#{workflowSearch.workFlowStates}"
					id="workflowSearch_WorkflowState" >
					<f:selectItem itemLabel="In Process" itemValue="IP"/>
					<f:selectItem itemLabel="Completed" itemValue="CM"/>
					<f:selectItem itemLabel="Cancelled" itemValue="CNC"/>
					</af:selectManyListbox>

                  <af:selectOneChoice label="Created By"
                    unselectedLabel=" " value="#{workflowSearch.userId}"
                    id="workflowSearch_CreateBy" >
                    <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
                  </af:selectOneChoice>

                  <af:selectInputDate label="Start Date From"
                    value="#{workflowSearch.startDateFrom}"
                    id="workflowSearch_StartDateFrom" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                  <af:selectInputDate label="To"
                    value="#{workflowSearch.startDateTo}"
                    id="workflowSearch_StartDateTo" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{workflowSearch.submit}"
                      id="workflowSearch_SubmitBtn" />
                    <af:commandButton text="Reset"
                      action="#{workflowSearch.reset}"
                      id="workflowSearch_ResetBtn" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="1000"
            rendered="#{workflowSearch.hasSearchResults}">
            <af:panelBorder>
              <af:showDetailHeader text="Workflow List" disclosed="true">
                <af:table bandingInterval="1" banding="row"
                  var="processes" emptyText=" "
                  rows="#{workflowSearch.pageLimit}" width="98%"
                  value="#{workflowSearch.processes}">

                  <af:column sortProperty="processId" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Workflow Id" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:commandLink
                        action="#{workFlow2DDraw.submitProfile}">
                        <af:outputText value="#{processes.processId}" />
                        <t:updateActionListener
                          property="#{workFlow2DDraw.processId}"
                          value="#{processes.processId}" />
                        <t:updateActionListener
                          property="#{menuItem_workflowProfile.disabled}"
                          value="false" />
                      </af:commandLink>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="facilityIdString" sortable="true"
                    noWrap="true">
                    <f:facet name="header">
                      <af:outputText value="Facility Id" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:commandLink
                        text="#{processes.facilityIdString}"
                        action="#{facilityProfile.submitProfile}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener
                          property="#{facilityProfile.fpId}"
                          value="#{processes.facilityId}" />
                        <t:updateActionListener
                          property="#{menuItem_facProfile.disabled}"
                          value="false" />
                      </af:commandLink>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="facilityNm" sortable="true"
                    inlineStyle="color: rgb(255,255,0)">
                    <f:facet name="header">
                      <af:outputText value="Facility Name" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText readOnly="true"
                        value="#{processes.facilityNm}" />
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="reportId" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Report ID" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:commandLink
                        action="#{workflowSearch.navigate}">
                        <af:outputText value="#{processes.reportId}" />
                        <t:updateActionListener
                          property="#{workflowSearch.process}"
                          value="#{processes}" />
                        <t:updateActionListener
                          property="#{menuItem_workflowProfile.disabled}"
                          value="false" />
                      </af:commandLink>
                    </af:panelHorizontal>
                  </af:column>



                  <af:column sortProperty="processCd" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Workflow Type" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:selectOneChoice label="Workflow Type"
                        value="#{processes.processCd}" readOnly="true">
                        <f:selectItems
                          value="#{workFlowDefs.processTypes}" />
                      </af:selectOneChoice>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="processTemplateId"
                    sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Workflow Name" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:selectOneChoice label="Workflow"
                        value="#{processes.processTemplateId}"
                        readOnly="true">
                        <f:selectItems value="#{workFlowDefs.workflows}" />
                      </af:selectOneChoice>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="startDt" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Start Date" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText readOnly="true"
                        value="#{processes.startDt}">
                        <f:convertDateTime type="date" dateStyle="short" />
                      </af:inputText>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="dueDt" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Due Date" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText readOnly="true"
                        value="#{processes.dueDt}">
                        <f:convertDateTime type="date" dateStyle="short" />
                      </af:inputText>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="state" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="State" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:selectOneChoice label="State"
                        value="#{processes.state}" readOnly="true">
                        <f:selectItems value="#{workFlowDefs.stateDef}" />
                      </af:selectOneChoice>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="status" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Status" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <h:panelGrid border="0" width="100%"
                        align="center">
                        <af:selectOneChoice label="Status"
                          value="#{processes.status}" readOnly="true"
                          inlineStyle="#{true ? 'font-weight:bold;background-color:#' : ''}#{processes.statusColor}">
                          <f:selectItems
                            value="#{workFlowDefs.statusDef}" />
                        </af:selectOneChoice>
                      </h:panelGrid>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="userName" sortable="true"
                    width="20%">
                    <f:facet name="header">
                      <af:outputText value="Created By" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText readOnly="true" value="#{processes.userName}"/>
                    </af:panelHorizontal>
                  </af:column>


                  <af:column headerText="Permit" bandingShade="light">

                    <af:column sortProperty="permitTypeDesc"
                      sortable="true" headerText="Type">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{processes.permitTypeDesc}" />
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="permitNumber"
                      sortable="true" headerText="Number">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{processes.permitNumber}" />
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="permitReasonDsc"
                      sortable="true" headerText="Reason">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:selectOneChoice label="Reason(s) :"
                          valign="top" readOnly="true"
                          value="#{processes.permitReasonCd}"
                          >
                          <f:selectItems
                            value="#{permitSearch.allPermitReasons}" />
                        </af:selectOneChoice>
                      </af:panelHorizontal>
                    </af:column>

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
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>
