<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Control Equipment Catalog">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Control Equipment Catalog">
        <%@ include file="../util/header.jsp"%>
        <afh:rowLayout halign="center">
          <h:panelGrid border="1">
            <af:panelBorder>
              <f:facet name="left">
                <h:panelGrid columns="1" width="250">
                  <t:tree2 id="controlEquipmentCatalog"
                    value="#{contEquipCatalog.treeData}" var="node"
                    varNodeToggler="t" clientSideToggle="false">
                    <f:facet name="root">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage value="/images/catalog.gif"
                            border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{contEquipCatalog.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{contEquipCatalog.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{contEquipCatalog.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{contEquipCatalog.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>

                    <f:facet name="controlEquipment">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage value="/images/CE.gif"
                            border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{contEquipCatalog.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{contEquipCatalog.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{contEquipCatalog.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{contEquipCatalog.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                  </t:tree2>
                </h:panelGrid>
              </f:facet>

              <f:facet name="right">
                <h:panelGrid columns="1" border="1" width="650">
                  <af:panelGroup layout="vertical"
                    rendered="#{contEquipCatalog.selectedTreeNode.type=='root'}">
                    <af:panelHeader
                      text="Control Equipment Common Attributes" />
                    <af:panelForm>
                      <afh:rowLayout halign="left">
                        <af:outputText value="Operating Status" />
                      </afh:rowLayout>
                      <afh:rowLayout halign="left">
                        <af:outputText value="Company Control Equipment Description" />
                      </afh:rowLayout>
                      <afh:rowLayout halign="left">
                        <af:outputText value="Manufacturer" />
                      </afh:rowLayout>
                      <afh:rowLayout halign="Left">
                        <af:outputText value="Model" />
                      </afh:rowLayout>
                      <afh:rowLayout halign="Left">
                        <af:outputText value="Company Control Equipment ID" />
                      </afh:rowLayout>
                      <afh:rowLayout halign="Left">
                        <af:outputText value="AQD Description" />
                      </afh:rowLayout>
                    </af:panelForm>
                    <af:objectSpacer height="40" />
                    <af:panelForm>
                      <af:inputText label="Control Equipment Code:"
                        value="#{contEquipCatalog.ceType.code}"
                        rendered="#{contEquipCatalog.newCeType}"
                        columns="4" maximumLength="4" />
                    </af:panelForm>
                    <af:panelForm>
                      <af:inputText label="Control Equipment Type:"
                        value="#{contEquipCatalog.ceType.description}"
                        rendered="#{contEquipCatalog.newCeType}"
                        columns="40" maximumLength="40" />
                    </af:panelForm>

                    <afh:rowLayout halign="center">
                      <af:objectSpacer height="40" />
                      <af:panelButtonBar>
                        <af:commandButton
                          text="Add Control Equipment Type"
                          action="#{contEquipCatalog.addNewCeType}"
                          rendered="#{!contEquipCatalog.newCeType}" />
                      </af:panelButtonBar>
                      <af:commandButton text="Save Equipment Type"
                        action="#{contEquipCatalog.saveContEquipType}"
                        rendered="#{contEquipCatalog.newCeType}" />
                      <af:commandButton text="Cancel"
                        action="#{contEquipCatalog.reset}"
                        rendered="#{contEquipCatalog.newCeType}" />
                    </afh:rowLayout>
                  </af:panelGroup>

                  <af:panelGroup layout="vertical"
                    rendered="#{contEquipCatalog.selectedTreeNode.type == 'controlEquipment' }">
                    <af:panelHeader text="Control Equipment Attributes" />
                    <af:panelForm>
                      <af:table value="#{contEquipCatalog.detailData}"
                        bandingInterval="1" banding="row" width="98%"
                        var="detail">
                        <af:column sortProperty="dataDetailId"
                          sortable="true" formatType="text"
                          headerText="Attribute ID">
                          <af:commandLink text="#{detail.dataDetailId}"
                            id="addAttribute" useWindow="true"
                            windowWidth="600" windowHeight="300"
                            returnListener="#{contEquipCatalog.dialogDone}"
                            action="dialog:AddAttributeDialog">
                            <t:updateActionListener
                              property="#{contEquipCatalog.dataDetailId}"
                              value="#{detail.dataDetailId}" />
                          </af:commandLink>
                        </af:column>
                        <af:column sortProperty="dataDetailLbl"
                          sortable="true" headerText="Name">
                          <af:outputText value="#{detail.dataDetailLbl}" />
                        </af:column>
                        <af:column sortProperty="dataDetailDsc"
                          sortable="true" headerText="Description">
                          <af:outputText value="#{detail.dataDetailDsc}" />
                        </af:column>
                        <af:column sortProperty="dataTypeNm"
                          sortable="true" headerText="Type">
                          <af:outputText value="#{detail.dataTypeNm}"
                            rendered="#{detail.dataTypeId != 5}" />
                          <af:outputText
                            value="#{detail.enumDetails[0].enumDsc}"
                            rendered="#{detail.dataTypeId == 5}" />
                        </af:column>
                        <f:facet name="footer">
                          <afh:rowLayout halign="center">
                            <af:panelButtonBar>
                              <af:commandButton text="Add Attribute"
                                id="addAttribute" useWindow="true"
                                windowWidth="600" windowHeight="300"
                                returnListener="#{contEquipCatalog.dialogDone}"
                                action="dialog:AddAttributeDialog" />
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
                    <afh:rowLayout halign="center">
                      <af:objectSpacer height="20" />
                      <af:panelButtonBar>

                      </af:panelButtonBar>
                    </afh:rowLayout>
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

