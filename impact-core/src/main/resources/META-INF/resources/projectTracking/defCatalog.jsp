<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Definitions">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Definitions">
        <jsp:include flush="true" page="../util/header.jsp" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1">
            <af:panelBorder>
              <f:facet name="left">
                <h:panelGrid columns="1" width="250">

                  <%
                  /*-- tree navigation */
                  %>
                  <t:tree2 id="defCatalog" showRootNode="false"
                    value="#{projectTrackingDefCatalog.treeData}" var="node"
                    varNodeToggler="t" clientSideToggle="false">


                    <%
                    /*-- render root node for tree (labelled 'Category') */
                    %>
                    <f:facet name="root">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage
                            value="/images/folder_open.gif" border="0"
                            rendered="#{t.nodeExpanded}" />
                          <t:graphicImage
                            value="/images/folder_closed.gif" border="0"
                            rendered="#{!t.nodeExpanded}" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{projectTrackingDefCatalog.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{projectTrackingDefCatalog.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{projectTrackingDefCatalog.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{projectTrackingDefCatalog.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>

                    <%
                    /*-- render categories and tables */
                    %>
                    <f:facet name="defTable">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage
                            value="/images/definitions.gif" border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{projectTrackingDefCatalog.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{projectTrackingDefCatalog.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{projectTrackingDefCatalog.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{projectTrackingDefCatalog.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                  </t:tree2>
                </h:panelGrid>
              </f:facet>
              <%
              /*-- body of page */
              %>
              <%
              /*-- default body */
              %>
              <f:facet name="right">
                <h:panelGrid columns="1" border="1" width="650">
                  <af:panelGroup layout="vertical"
                    rendered="#{projectTrackingDefCatalog.selectedTreeNode.type=='root'}">
                    <af:panelHeader text="Definitions" />
                    <af:panelForm>
                      <afh:rowLayout halign="left">
                        <af:outputText
                          value="Select a category to view, add, or edit definitions for that category" />
                      </afh:rowLayout>
                    </af:panelForm>
                  </af:panelGroup>

                  <%
                  /*-- def table listing */
                  %>
                  <af:panelGroup layout="vertical"
                    rendered="#{projectTrackingDefCatalog.selectedTreeNode.type == 'defTable' && !projectTrackingDefCatalog.custom }">
                    <af:panelHeader text="#{projectTrackingDefCatalog.label}" />
                    <af:panelForm>
                      <afh:rowLayout halign="left">
                        <af:outputText value="#{projectTrackingDefCatalog.description}" />
                      </afh:rowLayout>
                    </af:panelForm>
                    <af:panelForm>

                      <%
                      /*--  table begins */
                      %>
                      <af:table value="#{projectTrackingDefCatalog.detailData}"
                        bandingInterval="1" banding="row" width="98%"
                        rendered="#{!projectTrackingDefCatalog.retrieveError}"
                        var="detail">

                        <%
                        /*-- code column (includes link to editor) */
                        %>
                        <af:column sortable="false" formatType="text"
                          headerText="Code">
                          <af:commandLink text="#{detail.code}"
                            id="addDefinition" useWindow="true"
                            windowWidth="600" windowHeight="300"
                            returnListener="#{projectTrackingDefCatalog.dialogDone}"
                            disabled="#{!projectTrackingDefCatalog.editable}"
                            action="dialog:projectTrackingEditDefinitionDialog">
                            <t:updateActionListener
                              property="#{projectTrackingDefCatalog.editRecord}"
                              value="#{detail.code}" />
                            <t:updateActionListener
                              property="#{projectTrackingDefCatalog.newRecord}"
                              value="false" />
                          </af:commandLink>
                        </af:column>

                        <%
                        /*-- description column */
                        %>
                        <af:column sortable="false"
                          headerText="Description">
                          <af:outputText value="#{detail.description}" />
                        </af:column>

                        <%
                        /*-- deprecated column */
                        %>
                        <af:column sortable="false" headerText="Status"
                          inlineStyle="color:#FFF"
                          rendered="#{projectTrackingDefCatalog.deprecatable}">
                          <af:outputText
                            value="#{detail.deprecated ? 'Inactive' : 'Active'}" />
                        </af:column>

                        <f:facet name="footer">
                          <afh:rowLayout halign="center">
                            <af:panelButtonBar>

                              <af:commandButton text="Add Definition"
                                id="submit" rendered="true"
                                disabled="#{!projectTrackingDefCatalog.newPermitted}"
                                action="dialog:projectTrackingEditDefinitionDialog"
                                useWindow="true" windowWidth="600"
                                windowHeight="300">
                                <t:updateActionListener
                                  property="#{projectTrackingDefCatalog.newRecord}"
                                  value="true" />
                              </af:commandButton>
                              <af:commandButton text="Update System"
                                rendered="false"
                                actionListener="#{projectTrackingDefCatalog.flushSystem}" />
                          
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
                      <%
                      /*-- table ends here */
                      %>

                    </af:panelForm>
                    <af:objectSpacer height="20" />
                  </af:panelGroup>

                  <af:panelGroup layout="vertical"
                    rendered="#{projectTrackingDefCatalog.selectedTreeNode.type == 'defTable' && projectTrackingDefCatalog.custom }">
                    <af:panelHeader text="#{projectTrackingDefCatalog.label}" />
                    <af:panelForm>
                      <afh:rowLayout halign="left">
                        <af:outputFormatted value="#{projectTrackingDefCatalog.description}" />
                      </afh:rowLayout>
                    </af:panelForm>
                    <af:panelForm>

                      <af:table id="customDefTable" value="#{projectTrackingDefCatalog.customDetailData}"
                        bandingInterval="1" varStatus="vs" banding="row"
                        rendered="#{!projectTrackingDefCatalog.retrieveError}"
                        width="98%" var="rows">

                        <af:forEach var="label" varStatus="vsi"
                          begin="0" end="#{projectTrackingDefCatalog.columnCount-1}">
                          <af:column formatType="text"
                            rendered="#{vsi.index < projectTrackingDefCatalog.columnCount}"
                            sortable="false"
                            headerText="#{projectTrackingDefCatalog.customDetailColumnHeader}">
                            <af:commandLink text="#{rows.displayValue}"
                              id="editDefinition" useWindow="true"
                              windowWidth="600" windowHeight="500"
                              returnListener="#{projectTrackingDefCatalog.dialogDone}"
                              rendered="#{vsi.index==0}"
                              disabled="#{!projectTrackingDefCatalog.editable}"
                              action="dialog:projectTrackingEditCustomDefinitionDialog">

                              <t:updateActionListener
                                property="#{projectTrackingDefCatalog.editCustomRecord}"
                                value="#{vs.index}" />
                            </af:commandLink>
                            <af:outputText rendered="#{vsi.index>0}"
                              value="#{rows.displayValue}" />
                          </af:column>
                        </af:forEach>

                        <f:facet name="footer">
                        	<afh:rowLayout halign="center">
                            <af:panelButtonBar id="footerButtons">
                              <af:commandButton text="Add Definition"
                                id="addDefinition"
                                disabled="#{!projectTrackingDefCatalog.newPermitted}"
                                rendered="#{!projectTrackingDefCatalog.custom}"
                                action="#{projectTrackingDefCatalog.addNewRecord}" />
                              <af:commandButton text="Add Definition"
                                id="addCustomDefinition"
                                disabled="#{!projectTrackingDefCatalog.newPermitted}"
                                rendered="#{projectTrackingDefCatalog.custom}"
                                useWindow="true" windowWidth="600"
                                windowHeight="500"
                                returnListener="#{projectTrackingDefCatalog.dialogDone}"
                                action="dialog:projectTrackingEditCustomDefinitionDialog">
                                <t:updateActionListener
                                  property="#{projectTrackingDefCatalog.createCustomRecord}"
                                  value="new" />
                              </af:commandButton>
                              <af:commandButton text="Update System"
                                rendered="false"
                                actionListener="#{projectTrackingDefCatalog.flushSystem}" />
                              <af:commandButton
                              	id="customPrintView"
                                actionListener="#{tableExporter.printTable}"
                                onclick="#{tableExporter.onClickScript}"
                                text="Printable view" />
                              <af:commandButton
                              	id="customExport"
                                actionListener="#{tableExporter.excelTable}"
                                onclick="#{tableExporter.onClickScript}"
                                text="Export to excel" />
                            </af:panelButtonBar>
                            </afh:rowLayout>
                        </f:facet>
                      </af:table>
                    </af:panelForm>
                    <af:objectSpacer height="20" />
                    <af:panelForm>
                      <af:inputText label="Code:"
                        value="#{projectTrackingDefCatalog.data.code}"
                        rendered="#{projectTrackingDefCatalog.newRecord}" columns="4"
                        maximumLength="4" />
                    </af:panelForm>
                    <af:panelForm>
                      <af:inputText label="Description:"
                        value="#{projectTrackingDefCatalog.data.description}"
                        rendered="#{projectTrackingDefCatalog.newRecord}" columns="40"
                        maximumLength="40" />
                    </af:panelForm>



                    <afh:rowLayout halign="center">
                      <af:objectSpacer height="20" />
                      <af:panelButtonBar>
                        <af:commandButton text="Save"
                          action="#{projectTrackingDefCatalog.saveNew}"
                          rendered="#{projectTrackingDefCatalog.newRecord}" />
                        <af:commandButton text="Save"
                          action="#{projectTrackingDefCatalog.saveNewCustom}"
                          rendered="#{projectTrackingDefCatalog.newCustomRecord}" />
                        <af:commandButton text="Cancel"
                          action="#{projectTrackingDefCatalog.cancelNew}"
                          rendered="#{projectTrackingDefCatalog.newRecord || projectTrackingDefCatalog.newCustomRecord}" />
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

