<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Scheduler">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" />
    <f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}" title="Scheduler">
        <%@ include file="../util/header.jsp"%>
        <afh:rowLayout halign="center">
          <h:panelGrid border="1">
            <af:panelBorder>
              <f:facet name="left">
                <h:panelGrid columns="1" width="250">
                  <t:tree2 id="scheduler" value="#{scheduler.treeData}"
                    var="node" varNodeToggler="t"
                    clientSideToggle="false">
                    <f:facet name="root">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage value="/images/catalog.gif"
                            border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{scheduler.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{scheduler.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{scheduler.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{scheduler.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                    <f:facet name="job">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage value="/images/user.gif"
                            border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{scheduler.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{scheduler.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{scheduler.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{scheduler.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                    <f:facet name="trigger">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage value="/images/user.gif"
                            border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{scheduler.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{scheduler.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{scheduler.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{scheduler.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                  </t:tree2>
                </h:panelGrid>
              </f:facet>

              <f:facet name="innerLeft">
                <h:panelGrid columns="1" border="1" width="400">
                  <af:panelGroup layout="vertical"
                    rendered="#{scheduler.selectedTreeNode.type == 'root' }">
                    <af:panelHeader text="Jobs" />
                    <af:panelForm>
                      <af:inputText label="Name"
                        value="#{scheduler.jobName}"
                        rendered="#{scheduler.addingJob}" />
                      <af:inputText label="Class Name"
                        value="#{scheduler.jobClassName}"
                        rendered="#{scheduler.addingJob}" />
                      <afh:rowLayout halign="center">
                        <af:panelButtonBar>
                          <af:commandButton text="Add New Job"
                            action="#{scheduler.addJob}"
                            rendered="#{!scheduler.addingJob && scheduler.schedulerRunning}" />
                          <af:commandButton text="Save"
                            action="#{scheduler.saveJob}"
                            rendered="#{scheduler.addingJob}" />
                          <af:commandButton text="Cancel"
                            action="#{scheduler.reset}"
                            rendered="#{scheduler.addingJob}" />
                        </af:panelButtonBar>
                      </afh:rowLayout>
                    </af:panelForm>
                  </af:panelGroup>
                  <af:panelGroup layout="vertical"
                    rendered="#{scheduler.selectedTreeNode.type == 'job'}"
                    partialTriggers="scheduleType repeatType">
                    <af:panelHeader text="Job" />
                    <af:panelForm>
                      <af:inputText label="Name"
                        value="#{scheduler.job.name}" readOnly="true" />
                      <af:inputText label="Class Name"
                        value="#{scheduler.job.className}"
                        readOnly="#{!scheduler.editingJob}" />
                      <af:inputText label="Schedule Name"
                        value="#{scheduler.triggerName}" 
                        readOnly="#{scheduler.editingSchedule}"
                        rendered="#{scheduler.addingSchedule || scheduler.editingSchedule}" />
                      <af:selectOneRadio id="scheduleType"
                        value="#{scheduler.scheduleType}" required="yes"
                        layout="horizontal" autoSubmit="true"
                        rendered="#{scheduler.addingSchedule || scheduler.editingSchedule}"
                        attributeChangeListener="#{schedular}">
                        <f:selectItem itemLabel="One Time"
                          itemValue="Once" />
                        <f:selectItem itemLabel="Repeating"
                          itemValue="Repeating" />
                      </af:selectOneRadio>
                      <af:selectInputDate id="mdf2"
                        value="#{scheduler.onceDate}"
                        label="Day and Time"
                        rendered="#{scheduler.once && !scheduler.repeating}">
                        <f:convertDateTime pattern="yyyy/MM/dd HH:mm"
                          dateStyle="long" type="both" />
                      </af:selectInputDate>
                      <af:selectOneRadio id="repeatType"
                        value="#{scheduler.repeatType}" required="yes"
                        layout="vertical" autoSubmit="true"
                        rendered="#{!scheduler.once && scheduler.repeating}">
                        <f:selectItem itemLabel="Daily" itemValue="Daily" />
                        <f:selectItem itemLabel="Weekly" itemValue="Weekly" />
                        <f:selectItem itemLabel="Monthly" itemValue="Monthly" />
                        <f:selectItem itemLabel="Yearly" itemValue="Yearly" />
                      </af:selectOneRadio>
                      <af:selectOneChoice label="Day"
                        value="#{scheduler.dayOfWeek}" required="yes"
                        autoSubmit="true"
                        rendered="#{scheduler.repeatType == 'Weekly'}">
                        <f:selectItem itemLabel="Sunday" itemValue="Sunday" />
                        <f:selectItem itemLabel="Monday" itemValue="Monday" />
                        <f:selectItem itemLabel="Tuesday" itemValue="Tuesday" />
                        <f:selectItem itemLabel="Wednesday" itemValue="Wednesday" />
                        <f:selectItem itemLabel="Thursday" itemValue="Thursday" />
                        <f:selectItem itemLabel="Friday" itemValue="Friday" />
                        <f:selectItem itemLabel="Saturday" itemValue="Saturday" />
                      </af:selectOneChoice>
                      <af:selectOneChoice label="Month"
                        value="#{scheduler.monthOfYear}" required="yes"
                        autoSubmit="true"
                        rendered="#{scheduler.repeatType == 'Yearly'}">
                        <f:selectItem itemLabel="January" itemValue="January" />
                        <f:selectItem itemLabel="February" itemValue="February" />
                        <f:selectItem itemLabel="March" itemValue="March" />
                        <f:selectItem itemLabel="April" itemValue="April" />
                        <f:selectItem itemLabel="May" itemValue="May" />
                        <f:selectItem itemLabel="June" itemValue="June" />
                        <f:selectItem itemLabel="July" itemValue="July" />
                        <f:selectItem itemLabel="August" itemValue="August" />
                        <f:selectItem itemLabel="September" itemValue="September" />
                        <f:selectItem itemLabel="October" itemValue="October" />
                        <f:selectItem itemLabel="November" itemValue="November" />
                        <f:selectItem itemLabel="December" itemValue="December" />
                      </af:selectOneChoice>
                      <af:inputText label="Day of Month"
                        value="#{scheduler.dayOfMonth }"
                        autoSubmit="true"
                        rendered="#{scheduler.repeatType == 'Monthly' || scheduler.repeatType == 'Yearly'}">
                        <f:validateLongRange minimum="1" maximum="31" />
                      </af:inputText>
                      <af:inputText label="Time to Run"
                        value="#{scheduler.timeOfDay}"
                        rendered="#{scheduler.repeatType == 'Daily' ||
                            scheduler.repeatType == 'Weekly' ||
                            scheduler.repeatType == 'Monthly' ||
                            scheduler.repeatType == 'Yearly'}" />
                      <afh:rowLayout halign="center">
                        <h:panelGrid border="1"
                          rendered="#{(scheduler.addingSchedule || scheduler.editingSchedule)
                           && (scheduler.once || scheduler.repeating)}">
                          <af:table emptyText="No values"
                            var="dataValue"
                            value="#{scheduler.dataMapValues}">
                            <af:column sortable="true"
                              sortProperty="name" headerText="Name">
                              <af:outputText value="#{dataValue.name}" />
                            </af:column>
                            <af:column sortable="true"
                              sortProperty="value" headerText="Value">
                              <af:outputText value="#{dataValue.value}" />
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
                                  <af:commandButton text="Add Data"
                                    useWindow="true" windowWidth="500"
                                    windowHeight="300"
                                    action="#{scheduler.addDataMapValue}" />
                                </af:panelButtonBar>
                              </afh:rowLayout>
                            </f:facet>
                          </af:table>
                        </h:panelGrid>
                      </afh:rowLayout>
                      <afh:rowLayout halign="center">
                        <af:panelButtonBar>
                          <af:commandButton text="Add New Schedule"
                            action="#{scheduler.addSchedule}"
                            rendered="#{!scheduler.editingJob && !scheduler.removeJob && !scheduler.addingSchedule && !scheduler.editingSchedule}" />
                          <af:commandButton text="Edit Job"
                            action="#{scheduler.editJob}"
                            rendered="#{!scheduler.editingJob && !scheduler.removeJob && !scheduler.addingSchedule && !scheduler.editingSchedule}" />
                          <af:commandButton text="Remove Job"
                            action="#{scheduler.removeJob}"
                            rendered="#{!scheduler.editingJob && !scheduler.removeJob && !scheduler.addingSchedule && !scheduler.editingSchedule}" />
                          <af:commandButton text="Remove Job?"
                            action="#{scheduler.removeJob}"
                            rendered="#{scheduler.removeJob}" />
                          <af:commandButton text="Save"
                            action="#{scheduler.saveJob}"
                            rendered="#{scheduler.editingJob}" />
                          <af:commandButton text="Save"
                            action="#{scheduler.saveSchedule}"
                            rendered="#{scheduler.addingSchedule || scheduler.editingSchedule}" />
                          <af:commandButton text="Cancel"
                            action="#{scheduler.reset}"
                            rendered="#{scheduler.editingJob || scheduler.removeJob || scheduler.addingSchedule}" />
                        </af:panelButtonBar>
                      </afh:rowLayout>
                    </af:panelForm>
                  </af:panelGroup>
                  <af:panelGroup layout="vertical"
                    partialTriggers="tScheduleType tRepeatType"
                    rendered="#{scheduler.selectedTreeNode.type == 'trigger' }">
                    <af:panelHeader text="Schedule" />
                    <af:panelForm>
                      <af:inputText label="Name"
                        value="#{scheduler.job.name}" readOnly="true" />
					  <af:panelLabelAndMessage label="Status" for="triggerStatus" 
					  		tip="Status must be NORMAL in order for job to run.">
	                      <af:inputText id="triggerStatus"
	                        value="#{scheduler.triggerStatus}" />
                      </af:panelLabelAndMessage>
                      <af:inputText label="Class Name"
                        value="#{scheduler.job.className}"
                        readOnly="true" />
                      <af:inputText label="Schedule Name"
                        value="#{scheduler.triggerName}" required="true"
                        readOnly="true"
                        rendered="#{scheduler.addingSchedule || scheduler.editingSchedule}" />
                      <af:selectOneRadio id="tScheduleType"
                        value="#{scheduler.scheduleType}" required="yes"
                        layout="horizontal" autoSubmit="true"
                        readOnly="#{!scheduler.editingSchedule}"
                        rendered="#{scheduler.addingSchedule || scheduler.editingSchedule}"
                        attributeChangeListener="#{schedular}">
                        <f:selectItem itemLabel="One Time"
                          itemValue="Once" />
                        <f:selectItem itemLabel="Repeating"
                          itemValue="Repeating" />
                      </af:selectOneRadio>
                      <af:selectInputDate id="mdf2"
                        value="#{scheduler.onceDate}"
                        label="Day and Time"
                        readOnly="#{!scheduler.editingSchedule}"
                        rendered="#{scheduler.once && !scheduler.repeating}">
                        <f:convertDateTime pattern="yyyy/MM/dd HH:mm"
                          dateStyle="long" type="both" />
                      </af:selectInputDate>
                      <af:selectOneRadio id="tRepeatType"
                        value="#{scheduler.repeatType}" required="yes"
                        layout="vertical" autoSubmit="true"
                        readOnly="#{!scheduler.editingSchedule}"
                        rendered="#{!scheduler.once && scheduler.repeating}">
                        <f:selectItem itemLabel="Daily"
                          itemValue="Daily" />
                        <f:selectItem itemLabel="Weekly"
                          itemValue="Weekly" />
                        <f:selectItem itemLabel="Monthly"
                          itemValue="Monthly" />
                        <f:selectItem itemLabel="Yearly"
                          itemValue="Yearly" />
                      </af:selectOneRadio>
                      <af:selectOneChoice label="Day"
                        value="#{scheduler.dayOfWeek}" required="yes"
                        readOnly="#{!scheduler.editingSchedule}"
                        autoSubmit="true"
                        rendered="#{scheduler.repeatType == 'Weekly'}">
                        <f:selectItem itemLabel="Sunday"
                          itemValue="Sunday" />
                        <f:selectItem itemLabel="Monday"
                          itemValue="Monday" />
                        <f:selectItem itemLabel="Tuesday"
                          itemValue="Tuesday" />
                        <f:selectItem itemLabel="Wednesday"
                          itemValue="Wednesday" />
                        <f:selectItem itemLabel="Thursday"
                          itemValue="Thursday" />
                        <f:selectItem itemLabel="Friday"
                          itemValue="Friday" />
                        <f:selectItem itemLabel="Saturday"
                          itemValue="Saturday" />
                      </af:selectOneChoice>
                      <af:selectOneChoice label="Month"
                        value="#{scheduler.monthOfYear}" required="yes"
                        autoSubmit="true"
                        readOnly="#{!scheduler.editingSchedule}"
                        rendered="#{scheduler.repeatType == 'Yearly'}">
                        <f:selectItem itemLabel="January"
                          itemValue="January" />
                        <f:selectItem itemLabel="February"
                          itemValue="February" />
                        <f:selectItem itemLabel="March"
                          itemValue="March" />
                        <f:selectItem itemLabel="April"
                          itemValue="April" />
                        <f:selectItem itemLabel="May" itemValue="May" />
                        <f:selectItem itemLabel="June" itemValue="June" />
                        <f:selectItem itemLabel="July" itemValue="July" />
                        <f:selectItem itemLabel="August"
                          itemValue="August" />
                        <f:selectItem itemLabel="September"
                          itemValue="September" />
                        <f:selectItem itemLabel="October"
                          itemValue="October" />
                        <f:selectItem itemLabel="November"
                          itemValue="November" />
                        <f:selectItem itemLabel="December"
                          itemValue="December" />
                      </af:selectOneChoice>
                      <af:inputText label="Day of Month"
                        value="#{scheduler.dayOfMonth }"
                        autoSubmit="true"
                        readOnly="#{!scheduler.editingSchedule}"
                        rendered="#{scheduler.repeatType == 'Monthly' || scheduler.repeatType == 'Yearly'}">
                        <f:validateLongRange minimum="1" maximum="31" />
                      </af:inputText>
                      <af:inputText label="Time to Run"
                        value="#{scheduler.timeOfDay}"
                        readOnly="#{!scheduler.editingSchedule}"
                        rendered="#{scheduler.repeatType == 'Daily' ||
                            scheduler.repeatType == 'Weekly' ||
                            scheduler.repeatType == 'Monthly' ||
                            scheduler.repeatType == 'Yearly'}" />
                      <afh:rowLayout halign="center">
                        <h:panelGrid border="1">
                          <af:table emptyText="No values"
                            var="dataValue"
                            value="#{scheduler.dataMapValues}">
                            <f:facet name="selection">
                              <af:tableSelectMany rendered="#{scheduler.editingSchedule}" />
                            </f:facet>
                            <af:column sortable="true"
                              sortProperty="name" headerText="Name">
                              <af:outputText value="#{dataValue.name}" />
                            </af:column>
                            <af:column sortable="true"
                              sortProperty="value" headerText="Value">
                              <af:outputText value="#{dataValue.value}"/>
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
                                  <af:commandButton text="Add Data"
                                    useWindow="true" windowWidth="500"
                                    windowHeight="300"
                                    rendered="#{scheduler.editingSchedule || scheduler.addingSchedule}"
                                    action="#{scheduler.addDataMapValue}" />
                                  <af:commandButton text="Delete Selected Rows"
                                    rendered="#{scheduler.editingSchedule || scheduler.addingSchedule}"
                                    actionListener="#{scheduler.initActionTable}" 
                                    action="#{scheduler.deleteNameValueTableRows}">
                                  </af:commandButton>
                                </af:panelButtonBar>
                              </afh:rowLayout>
                            </f:facet>
                          </af:table>
                        </h:panelGrid>
                      </afh:rowLayout>
                      <afh:rowLayout halign="center">
                        <af:panelButtonBar>
                          <af:commandButton text="Edit Schedule"
                            action="#{scheduler.editSchedule}"
                            rendered="#{!scheduler.editingSchedule && !scheduler.removeSchedule}" />
                          <af:commandButton text="Remove Schedule"
                            action="#{scheduler.removeSchedule}"
                            rendered="#{!scheduler.editingSchedule && !scheduler.removeSchedule}" />
                          <af:commandButton text="Remove Schedule?"
                            action="#{scheduler.removeSchedule}"
                            rendered="#{scheduler.removeSchedule}" />
                          <af:commandButton text="Save"
                            action="#{scheduler.saveSchedule}"
                            rendered="#{scheduler.editingSchedule}" />
                          <af:commandButton text="Cancel"
                            action="#{scheduler.cancelTriggerEdit}"
                            rendered="#{scheduler.editingSchedule}" />
                        </af:panelButtonBar>
                      </afh:rowLayout>
                    </af:panelForm>
                  </af:panelGroup>
                </h:panelGrid>
              </f:facet>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>
      </af:page>
    </af:form>
  </af:document>
</f:view>

