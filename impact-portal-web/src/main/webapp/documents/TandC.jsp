<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<html>
  <body>
    <f:view>
      <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="TermsAndConditions">
            <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
          <af:page var="foo" value="#{menuModel.model}" title="Terms and Conditions">
            <f:facet name="nodeStamp">
              <af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
                type="#{foo.type}" />
            </f:facet>
            <afh:rowLayout halign="center">
              <h:panelGrid border="1">
                <af:panelBorder>
                  <f:facet name="left">
                    <h:panelGrid columns="1" width="250">
                      <t:tree2 id="reports" value="#{TandCTree.treeData}"
                        var="node" varNodeToggler="t" clientSideToggle="false">
                        <f:facet name="root">
                          <h:panelGroup>
                            <af:commandMenuItem>
                              <t:graphicImage value="/images/catalog.gif"
                                border="0" />
                              <t:outputText value="#{node.description}"
                                style="color:#FFFFFF; background-color:#000000"
                                rendered="#{TandCTree.current == node.identifier}" />
                              <t:outputText value="#{node.description}"
                                rendered="#{TandCTree.current != node.identifier}" />
                              <t:updateActionListener
                                property="#{TandCTree.selectedTreeNode}"
                                value="#{node}" />
                              <t:updateActionListener
                                property="#{TandCTree.current}"
                                value="#{node.identifier}" />
                            </af:commandMenuItem>
                          </h:panelGroup>
                        </f:facet>
                        <f:facet name="group">
                          <h:panelGroup>
                            <af:commandMenuItem>
                              <t:graphicImage value="/images/user.gif"
                                border="0" />
                              <t:outputText value="#{node.description}"
                                style="color:#FFFFFF; background-color:#000000"
                                rendered="#{TandCTree.current == node.identifier}" />
                              <t:outputText value="#{node.description}"
                                rendered="#{TandCTree.current != node.identifier}" />
                              <t:updateActionListener
                                property="#{TandCTree.selectedTreeNode}"
                                value="#{node}" />
                              <t:updateActionListener
                                property="#{TandCTree.current}"
                                value="#{node.identifier}" />
                            </af:commandMenuItem>
                          </h:panelGroup>
                        </f:facet>
                        <f:facet name="report">
                          <h:panelGroup>
                            <af:commandMenuItem>
                              <t:graphicImage value="/images/process.gif"
                                border="0" />
                              <t:outputText value="#{node.description}"
                                style="color:#FFFFFF; background-color:#000000"
                                rendered="#{TandCTree.current == node.identifier}" />
                              <t:outputText value="#{node.description}"
                                rendered="#{TandCTree.current != node.identifier}" />
                              <t:updateActionListener
                                property="#{TandCTree.selectedTreeNode}"
                                value="#{node}" />
                              <t:updateActionListener
                                property="#{TandCTree.current}"
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
                        rendered="#{TandCTree.selectedTreeNode.type == 'root' }">
                        <af:panelHeader text="Report Definition" />
                        <af:panelForm>
                          <af:inputText label="Name"
                            value="#{TandCTree.report.name}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:inputText label="Group Name"
                            value="#{TandCTree.report.groupNm}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:inputText label="Definition FileName"
                            value="#{TandCTree.report.jasperDefFile}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox label="Facility Name"
                            value="#{TandCTree.report.facilityNmEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox label="Facility ID"
                            value="#{TandCTree.report.facilityIdEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox
                            label="Permit Classification"
                            value="#{TandCTree.report.permitClassEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox label="Report Category"
                            value="#{TandCTree.report.reportCategoryEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox label="Start Date"
                            value="#{TandCTree.report.startDtEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox label="End Date"
                            value="#{TandCTree.report.endDtEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <afh:rowLayout halign="center">
                            <af:panelButtonBar>
                              <af:commandButton text="Add Report"
                                action="#{TandCTree.addReport}"
                                rendered="#{!TandCTree.addingReport}" />
                              <af:commandButton text="Save"
                                action="#{TandCTree.saveReport}"
                                rendered="#{TandCTree.addingReport}" />
                              <af:commandButton text="Cancel"
                                action="#{TandCTree.reset}"
                                rendered="#{TandCTree.addingReport}" />
                            </af:panelButtonBar>
                          </afh:rowLayout>
                        </af:panelForm>
                      </af:panelGroup>

                      <af:panelGroup layout="vertical"
                        rendered="#{TandCTree.selectedTreeNode.type == 'group' }">
                        <af:panelHeader text="Report Definition" />
                        <af:panelForm>
                          <af:inputText label="Name"
                            value="#{TandCTree.report.name}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:inputText label="Group Name"
                            value="#{TandCTree.report.groupNm}"
                            rendered="#{TandCTree.addingReport}" 
                            disabled="true"/>
                          <af:inputText label="Definition FileName"
                            value="#{TandCTree.report.jasperDefFile}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox label="Facility Name"
                            value="#{TandCTree.report.facilityNmEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox label="Facility ID"
                            value="#{TandCTree.report.facilityIdEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox
                            label="Permit Classification"
                            value="#{TandCTree.report.permitClassEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox label="Report Category"
                            value="#{TandCTree.report.reportCategoryEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox label="Start Date"
                            value="#{TandCTree.report.startDtEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <af:selectBooleanCheckbox label="End Date"
                            value="#{TandCTree.report.endDtEnabled}"
                            rendered="#{TandCTree.addingReport}" />
                          <afh:rowLayout halign="center">
                            <af:panelButtonBar>
                              <af:commandButton text="Add Report"
                                action="#{TandCTree.addReport}"
                                rendered="#{!TandCTree.addingReport}" />
                              <af:commandButton text="Save"
                                action="#{TandCTree.saveReport}"
                                rendered="#{TandCTree.addingReport}" />
                              <af:commandButton text="Cancel"
                                action="#{TandCTree.reset}"
                                rendered="#{TandCTree.addingReport}" />
                            </af:panelButtonBar>
                          </afh:rowLayout>
                        </af:panelForm>
                      </af:panelGroup>

                      <af:panelGroup layout="vertical"
                        rendered="#{TandCTree.selectedTreeNode.type == 'report' }">
                        <af:panelHeader text="Report Definition" />
                        <af:panelForm>
                          <af:inputText label="Name"
                            value="#{TandCTree.report.name}"
                            rendered="#{TandCTree.editingReport}" />
                          <af:inputText label="Group Name"
                            value="#{TandCTree.report.groupNm}"
                            rendered="#{TandCTree.editingReport}"/>
                          <af:inputText label="Definition FileName"
                            value="#{TandCTree.report.jasperDefFile}"
                            rendered="#{TandCTree.editingReport}" />
                          <af:selectBooleanCheckbox label="Facility Name"
                            value="#{TandCTree.report.facilityNmEnabled}"
                            rendered="#{TandCTree.editingReport}" />
                          <af:selectBooleanCheckbox label="Facility ID"
                            value="#{TandCTree.report.facilityIdEnabled}"
                            rendered="#{TandCTree.editingReport}" />
                          <af:selectBooleanCheckbox
                            label="Permit Classification"
                            value="#{TandCTree.report.permitClassEnabled}"
                            rendered="#{TandCTree.editingReport}" />
                          <af:selectBooleanCheckbox label="Report Category"
                            value="#{TandCTree.report.reportCategoryEnabled}"
                            rendered="#{TandCTree.editingReport}" />
                          <af:selectBooleanCheckbox label="Start Date"
                            value="#{TandCTree.report.startDtEnabled}"
                            rendered="#{TandCTree.editingReport}" />
                          <af:selectBooleanCheckbox label="End Date"
                            value="#{TandCTree.report.endDtEnabled}"
                            rendered="#{TandCTree.editingReport}" />
                          <afh:rowLayout halign="center">
                            <af:panelButtonBar>
                              <af:commandButton text="Edit Report"
                                action="#{TandCTree.editReport}"
                                rendered="#{!TandCTree.editingReport}" />
                              <af:commandButton text="Clone Report"
                                action="#{TandCTree.cloneReport}"
                                rendered="#{!TandCTree.editingReport}" />
                              <af:commandButton text="Save"
                                action="#{TandCTree.saveReport}"
                                rendered="#{TandCTree.editingReport}" />
                              <af:commandButton text="Cancel"
                                action="#{TandCTree.reset}"
                                rendered="#{TandCTree.editingReport}" />
                            </af:panelButtonBar>
                          </afh:rowLayout>
                        </af:panelForm>
                        <af:panelHeader text="Report Input Values" 
                        rendered="#{!TandCTree.editingReport}"/>
                        <af:panelForm rendered="#{!TandCTree.editingReport}">
                          <af:inputText label="Facility ID"
                            value="#{TandCTree.facilityId}"
                            rendered="#{TandCTree.report.facilityIdEnabled}" />
                          <af:inputText label="Facility Name"
                            value="#{TandCTree.facilityNm}"
                            rendered="#{TandCTree.report.facilityNmEnabled}" />
                          <af:selectOneChoice label="Permitting Classification"
                            value="#{TandCTree.permittingClassCd}"
                            rendered="#{TandCTree.report.permitClassEnabled}">
                            <f:selectItems value="#{infraDefs.permitClasses}" />
                          </af:selectOneChoice>
                          <af:selectOneChoice label="Reporting Category"
                            value="#{TandCTree.reportCategoryCd}"
                            rendered="#{TandCTree.report.reportCategoryEnabled}">
                            <f:selectItems value="#{infraDefs.reportingTypes.items[(empty TandCTree.reportCategoryCd ? '' : TandCTree.reportCategoryCd)]}" />
                          </af:selectOneChoice>
                          <af:selectInputDate label="Start Date"
                            value="#{TandCTree.startDate}"
                            rendered="#{TandCTree.report.startDtEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                          <af:selectInputDate label="End Date"
                            value="#{TandCTree.endDate}"
                            rendered="#{TandCTree.report.endDtEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                          <af:selectOneRadio value="#{TandCTree.outputType}"
                            required="yes" label="Type of Output"
                            layout="horizontal">
                            <af:objectSpacer height="1" width="400" />
                            <f:selectItem itemLabel="PDF" itemValue="PDF" />
                            <f:selectItem itemLabel="Word/RTF" itemValue="RTF" />
                          </af:selectOneRadio>
                          <af:objectSpacer height="1" width="400" />
                          <afh:rowLayout halign="center">
                            <af:panelButtonBar>
                              <af:commandButton text="Generate Report"
                                action="#{TandCTree.generateReport}"
                                onclick="#{TandCTree.onClickScript}" />
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
  </body>
</html>
