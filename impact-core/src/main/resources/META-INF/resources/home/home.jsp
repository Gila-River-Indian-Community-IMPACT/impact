<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document title="To Dos">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}" title="To Dos">
        <%@ include file="../util/header.jsp"%>

        <mu:setProperty property="#{toDoSearch.showFacility}"
          value="true" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="1100"
          	rendered="#{!toDoSearch.monitorReportUploadUser}">
            <af:panelBorder>
              <af:showDetailHeader text="Search Criteria"
                disclosed="true">
                <af:panelForm rows="2" maxColumns="3"
                  partialTriggers="workflowTypes">

                  <af:inputText columns="10" maximumLength="10" label="Facility ID"
                    tip="0000000000, 0%, %0%, *0*, 0*"
                    value="#{toDoSearch.facilityId}" />

                  <af:inputText columns="25" label="Facility Name"
                    tip="acme%, %acme%, *acme*, acme*"
                    value="#{toDoSearch.facilityNm}" />
                    
                  <af:selectOneChoice label="District" rendered="#{infraDefs.districtVisible}"
                    value="#{toDoSearch.doLaaCd}"
                    unselectedLabel="">
                    <f:selectItems value="#{facilitySearch.doLaas}"/>
                  </af:selectOneChoice>
                  <%-- This is to make the page Layout unchanged with District not rendered --%>
                  <af:inputHidden id="districtHidden" rendered="#{!infraDefs.districtVisible}"/>
                                      
                  <af:selectOneChoice label="Staff" tip=" "
                    unselectedLabel=" " value="#{toDoSearch.userId}">
                    <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
                  </af:selectOneChoice>

                  <af:selectOneChoice label="Workflow Type"
                    autoSubmit="TRUE" unselectedLabel=" "
                    id="workflowTypes"
                    value="#{toDoSearch.processTypeCd}">
                    <f:selectItems value="#{workFlowDefs.processTypeDefs}" />
                  </af:selectOneChoice>

                  <af:selectOneChoice label="Workflow Name"
                    unselectedLabel=" " value="#{toDoSearch.processTemplateNm}">
                    <f:selectItems value="#{toDoSearch.workflows}" />
                  </af:selectOneChoice>
                  
                  <af:selectBooleanCheckbox label="Looped"
                    value="#{toDoSearch.looped}" />

                  <af:selectOneChoice label="Task Name" tip=" "
                    unselectedLabel=" " value="#{toDoSearch.activityNm}">
                    <f:selectItems value="#{toDoSearch.activityNames}" />
                  </af:selectOneChoice>

                  <af:selectOneChoice label="Task State" tip=" "
                    unselectedLabel=" "
                    value="#{toDoSearch.activityStatusCd}">
                    <f:selectItems
                      value="#{workFlowDefs.activityStatus}" />
                  </af:selectOneChoice>

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{toDoSearch.submit}" />
                    <af:commandButton text="Reset"
                      action="#{toDoSearch.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />
        <afh:rowLayout halign="center" >
          <h:panelGrid border="1" width="1100"
            rendered="#{toDoSearch.hasSearchResults}">
            <af:panelBorder>
              <af:showDetailHeader text="To Do List" disclosed="true">
                <af:table bandingInterval="1" banding="row" var="todos"
                  emptyText=' ' rows="#{toDoSearch.pageLimit}"
                  width="99%" value="#{toDoSearch.shortActivities}">

                  <af:column sortProperty="loopCnt" sortable="true"
                    headerText="Task ID" formatType="text">
                   
                      <af:commandLink
                        action="#{activityProfile.submitProfile}">
                        <af:outputText
                          inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}"
                          value="#{todos.processId}-#{todos.activityTemplateId}-#{todos.loopCnt}" />
                        <t:updateActionListener
                          property="#{menuItem_activityProfile.disabled}"
                          value="false" />
                        <t:updateActionListener
                          property="#{activityProfile.processId}"
                          value="#{todos.processId}" />
                        <t:updateActionListener
                          property="#{workFlow2DDraw.processId}"
                          value="#{todos.processId}" />
                        <t:updateActionListener
                          property="#{activityProfile.loopCnt}"
                          value="#{todos.loopCnt}" />
                        <t:updateActionListener
                          property="#{activityProfile.activityTemplateId}"
                          value="#{todos.activityTemplateId}" />
                        <t:updateActionListener
                          property="#{activityProfile.fromExternal}"
                          value="#{toDoSearch.fromExternal}" />
                        <t:updateActionListener
                          property="#{activityProfile.aggregate}"
                          value="#{todos.aggregate}" />
                        <t:updateActionListener
                          property="#{menuItem_workflowProfile.disabled}"
                          value="false" />
                      </af:commandLink>
                   
                  </af:column>

                  <af:column sortProperty="facilityId" sortable="true"
                    noWrap="true" rendered="#{toDoSearch.showFacility}"
                    headerText="Facility ID" formatType="text">
                      <af:commandLink text="#{todos.facilityId}"
                        action="#{facilityProfile.submitProfile}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener
                          property="#{facilityProfile.fpId}"
                          value="#{todos.fpId}" />
                        <t:updateActionListener
                          property="#{menuItem_facProfile.disabled}"
                          value="false" />
                      </af:commandLink>
                  </af:column>

                  <af:column sortProperty="facilityNm" sortable="true"
                    rendered="#{toDoSearch.showFacility}"
                    headerText="Facility Name" formatType="text">
                      <af:inputText readOnly="true"
                        value="#{todos.facilityNm}" />
                  </af:column>
                  
                  <af:column sortProperty="cmpId" sortable="true"
               		formatType="text"
                  	rendered="#{toDoSearch.showFacility}"
                  	headerText="Company ID" >
                  	<af:commandLink
                  		inlineStyle="white-space: nowrap;"
                  		text="#{todos.cmpId}" 
                  		action="#{companyProfile.submitProfile}" >
                  		<t:updateActionListener 
                  			property="#{companyProfile.cmpId}" 
                  			value="#{todos.cmpId}" />
						<t:updateActionListener
							property="#{menuItem_companyProfile.disabled}"
							value="false" />
					</af:commandLink>	
                  </af:column>
                  
                  <af:column sortProperty="cmpName" sortable="true"
                  	formatType="text"
                  	rendered="#{toDoSearch.showFacility}"
                  	headerText="Company Name" >
                  	<af:inputText
                  		readOnly="true"
                  		value="#{todos.cmpName}" /> 
                  </af:column>		

                  <af:column sortProperty="reportId" sortable="true"
                    headerText="Report ID" formatType="text">
                      <af:commandLink
                        action="#{activityProfile.submitProfile}">
                        <af:outputText
                          inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}"
                          value="#{todos.reportId}" />
                        <t:updateActionListener
                          property="#{menuItem_activityProfile.disabled}"
                          value="false" />
                        <t:updateActionListener
                          property="#{activityProfile.processId}"
                          value="#{todos.processId}" />
                        <t:updateActionListener
                          property="#{workFlow2DDraw.processId}"
                          value="#{todos.processId}" />
                        <t:updateActionListener
                          property="#{activityProfile.loopCnt}"
                          value="#{todos.loopCnt}" />
                        <t:updateActionListener
                          property="#{activityProfile.activityTemplateId}"
                          value="#{todos.activityTemplateId}" />
                        <t:updateActionListener
                          property="#{activityProfile.fromExternal}"
                          value="#{toDoSearch.fromExternal}" />
                        <t:updateActionListener
                          property="#{activityProfile.aggregate}"
                          value="#{todos.aggregate}" />
                        <t:updateActionListener
                          property="#{menuItem_workflowProfile.disabled}"
                          value="false" />
                      </af:commandLink>
                  </af:column>

                  <af:column headerText="Task" bandingShade="light">

                    <af:column sortProperty="activityTemplateNm"
                      sortable="true" headerText="Name" formatType="text">
                        <af:commandLink
                          action="#{activityProfile.submitActProfile}">
                          <af:outputText
                            inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}"
                            value="#{todos.activityTemplateNm}" />
                          <t:updateActionListener
                            property="#{menuItem_activityProfile.disabled}"
                            value="false" />
                          <t:updateActionListener
                            property="#{activityProfile.processId}"
                            value="#{todos.processId}" />
                          <t:updateActionListener
                            property="#{workFlow2DDraw.processId}"
                            value="#{todos.processId}" />
                          <t:updateActionListener
                            property="#{activityProfile.loopCnt}"
                            value="#{todos.loopCnt}" />
                          <t:updateActionListener
                            property="#{activityProfile.activityTemplateId}"
                            value="#{todos.activityTemplateId}" />
                          <t:updateActionListener
                            property="#{activityProfile.fromExternal}"
                            value="#{toDoSearch.fromExternal}" />
                          <t:updateActionListener
                            property="#{activityProfile.aggregate}"
                            value="#{todos.aggregate}" />
                          <t:updateActionListener
                            property="#{menuItem_workflowProfile.disabled}"
                            value="false" />
                        </af:commandLink>
                    </af:column>

                    <af:column sortProperty="userName"
                      headerText="Staff Assigned" sortable="true"  formatType="text">
                        <af:inputText readOnly="true"
                          value="#{todos.userName}"
                          inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
                    </af:column>

                    <af:column sortProperty="activityStatusDesc"
                      sortable="true" headerText="State" formatType="text">
                        <af:inputText readOnly="true"
                          value="#{todos.activityStatusDesc}"
                          inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
                    </af:column>

                    <af:column sortProperty="duration" sortable="true"
                      headerText="Days" formatType="number">
                        <af:inputText readOnly="true"
                          value="#{todos.duration}" />
                    </af:column>

                    <af:column sortProperty="endDt" sortable="true"
                      headerText="End Date" formatType="text">
                        <af:selectInputDate value="#{todos.endDt}"
                          readOnly="true"
                          inlineStyle="#{todos.viewed ? 'font-weight:regular;' : 'font-weight:bold;'}" />
                    </af:column>
                  </af:column>

                  <af:column headerText="Workflow">

                    <af:column sortProperty="processTemplateNm"
                      sortable="true" headerText="Name" formatType="text">
                        <af:commandLink
                          action="#{workFlow2DDraw.submitProfile}">
                          <af:outputText
                            value="#{todos.processTemplateNm}" />
                          <t:updateActionListener
                            property="#{workFlow2DDraw.processId}"
                            value="#{todos.processId}" />
                          <t:updateActionListener
                            property="#{menuItem_workflowProfile.disabled}"
                            value="false" />
                        </af:commandLink>
                    </af:column>

                    <af:column sortProperty="status" sortable="true"
                      headerText="Status" formatType="text">
                        <h:panelGrid border="0" width="100%">
                          <af:selectOneChoice label="Status"
                            value="#{todos.status}"
                            readOnly="true">
                            <f:selectItems
                              value="#{workFlowDefs.statusDef}" />
                          </af:selectOneChoice>
                        </h:panelGrid>
                    </af:column>

                    <af:column sortProperty="processStartDt"
                      sortable="true" headerText="Start Date" formatType="text">
                        <af:selectInputDate
                          value="#{todos.processStartDt}"
                          readOnly="true" />
                    </af:column>

                    <af:column sortProperty="processDuration"
                      sortable="true" headerText="Days" formatType="number">
                        <af:inputText readOnly="true"
                          value="#{todos.processDuration}" />
                    </af:column>

                    <af:column sortProperty="processDueDt"
                      sortable="true" headerText="Due Date" formatType="text">
                        <af:selectInputDate
                          value="#{todos.processDueDt}" readOnly="true" />
                    </af:column>

                  </af:column>
                  
                  <af:column headerText="Permit" bandingShade="light">

                    <af:column sortProperty="permitTypeDesc"
                      sortable="true" headerText="Type" formatType="text">
                        <af:inputText readOnly="true"
                          value="#{todos.permitTypeDesc}" />
                    </af:column>

                    <af:column sortProperty="permitNumber"
                      sortable="true" headerText="Number" formatType="text">
                        <af:commandLink text="#{todos.permitNumber}"
                        	action="#{permitDetail.loadPermit}" >	
                          	<af:setActionListener to="#{permitDetail.permitID}"
								from="#{todos.permitId}" />
                          	<t:updateActionListener property="#{permitDetail.fromTODOList}"
								value="false" />
                      	</af:commandLink>      
                    </af:column>

                    <af:column headerText="Reason(s)" sortable="true"
                      sortProperty="permitReasonCd" formatType="text">
                        <af:selectManyCheckbox label="Reason(s) :"
                          valign="top" readOnly="true"
                          value="#{todos.permitReasonCDs}"
                          inlineStyle="white-space: nowrap;">
                          <f:selectItems
                            value="#{permitSearch.allPermitReasons}" />
                        </af:selectManyCheckbox>
                    </af:column>

                 	<%-- Task #1971
                 	
                 	The following lines are commented since Rush 
                 	column does not apply to AQD and IMPACT.   
                 	                 	          	                 	
                 	<af:column sortProperty="expedited" sortable="true"
                      headerText="Rush">
                      <af:inputText readOnly="true"
                        value="#{todos.expedited}" />
                    </af:column>
                    
                    --%>
                 
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
              	<af:outputText inlineStyle="font-size:75%;color:#666"
					value="* Aggregate activities are displayed in a single row." />
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>
