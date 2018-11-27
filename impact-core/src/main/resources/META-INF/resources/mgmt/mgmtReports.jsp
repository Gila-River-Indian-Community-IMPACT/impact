<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Reports">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form partialTriggers="ThePage:form:delReport" usesUpload="true">
      <af:page id="ThePage" var="foo" value="#{menuModel.model}" title="Report Tree">
        <%@ include file="../util/header.jsp"%>
        <afh:rowLayout halign="center">
          <h:panelGrid border="1">
            <af:panelBorder>
              <f:facet name="left">
                <h:panelGrid columns="1" width="250">
                  <t:tree2 id="reports" showRootNode="false"
                    value="#{reportTree.treeData}" var="node"
                    varNodeToggler="t" clientSideToggle="false">
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
                            rendered="#{reportTree.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{reportTree.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{reportTree.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{reportTree.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                    <f:facet name="group">
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
                            rendered="#{reportTree.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{reportTree.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{reportTree.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{reportTree.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                    <f:facet name="report">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage
                            value="/images/definitions.gif" border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{reportTree.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{reportTree.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{reportTree.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{reportTree.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                  </t:tree2>
                </h:panelGrid>
              </f:facet>

              <f:facet name="innerLeft">
                <h:panelGrid columns="1" border="1" width="400">
                  <f:subview id="form">
                    <jsp:include page="mgmtReportsAttributesForm.jsp" />
                  </f:subview>
                  

                  <af:panelGroup layout="vertical"
                    rendered="false">

                    <af:panelHeader text="Report Input Values"
                      rendered="#{!reportTree.editingReport}" />
                    <af:panelForm binding="#{reportTree.data}" />

                    <af:panelForm>
                      <af:selectOneRadio
                        value="#{reportTree.outputType}" required="yes"
                        label="Type of Output : " layout="horizontal">
                        <af:objectSpacer height="1" width="400" />
                        <f:selectItem itemLabel="PDF" itemValue="PDF" />
                        <f:selectItem itemLabel="Word/RTF"
                          itemValue="RTF" />
                        <f:selectItem itemLabel="Excel" itemValue="CSV" />
                      </af:selectOneRadio>

                      <af:objectSpacer height="1" width="400" />

                      <afh:rowLayout halign="center">
                        <af:panelButtonBar
                          rendered="#{!reportTree.readOnlyUser}">
                          <af:commandButton text="Generate Report"
                            action="#{reportTree.generateReport}"
                            onclick="#{reportTree.onClickScript}" />
                        </af:panelButtonBar>
                      </afh:rowLayout>
                    </af:panelForm>
                  </af:panelGroup>
                  
                  <af:panelGroup layout="vertical"
                    rendered="#{reportTree.selectedTreeNode.type == 'report' && !reportTree.editingReport}">

                    <af:panelForm>
                      <afh:rowLayout halign="center">
                      
				        <af:goButton targetFrame="_blank"
				          destination="#{reportTree.report.reportDocument.docURL}"
				          text="Download Report"
				          rendered="#{!reportTree.readOnlyUser}" />

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

