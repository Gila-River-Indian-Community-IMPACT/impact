<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="User Catalog">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}" title="User Catalog">
        <%@ include file="../util/header.jsp"%>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1">
            <af:panelBorder>
              <f:facet name="left">
                <h:panelGrid columns="1" width="250">
                  <t:tree2 id="roles" value="#{userTree.treeData}"
                    var="node" varNodeToggler="t"
                    clientSideToggle="false">
                    <f:facet name="root">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage value="/images/catalog.gif"
                            border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{userTree.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{userTree.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{userTree.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{userTree.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                    <f:facet name="active">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage value="/images/folder_closed.gif"
                            border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{userTree.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{userTree.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{userTree.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{userTree.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                    <f:facet name="inactive">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage value="/images/folder_closed.gif"
                            border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{userTree.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{userTree.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{userTree.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{userTree.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                    <f:facet name="activeUser">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage value="/images/user.gif"
                            border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{userTree.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{userTree.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{userTree.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{userTree.current}"
                            value="#{node.identifier}" />
                        </af:commandMenuItem>
                      </h:panelGroup>
                    </f:facet>
                    <f:facet name="inactiveUser">
                      <h:panelGroup>
                        <af:commandMenuItem>
                          <t:graphicImage value="/images/inactive_user.gif"
                            border="0" />
                          <t:outputText value="#{node.description}"
                            style="color:#FFFFFF; background-color:#000000"
                            rendered="#{userTree.current == node.identifier}" />
                          <t:outputText value="#{node.description}"
                            rendered="#{userTree.current != node.identifier}" />
                          <t:updateActionListener
                            property="#{userTree.selectedTreeNode}"
                            value="#{node}" />
                          <t:updateActionListener
                            property="#{userTree.current}"
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
                    rendered="#{userTree.selectedTreeNode.type == 'activeUser' 
                    	|| userTree.selectedTreeNode.type == 'inactiveUser'}">
                    <af:panelHeader text="User information" />
                    <af:panelForm partialTriggers="UserActiveBox">
                      <af:inputText label="UserId:"
                        value="#{userTree.user.networkLoginNm}"
                        disabled="true" />
                      <af:inputText label="First Name:"
                        maximumLength="50" value="#{userTree.user.userFirstNm}"
                        disabled="#{!userTree.editable || !userTree.user.active}" />
                      <af:inputText label="Last Name:"
                        maximumLength="50" value="#{userTree.user.userLastNm}"
                        disabled="#{!userTree.editable || !userTree.user.active}" />
                      <af:inputText label="Position #:"
                        maximumLength="18" value="#{userTree.user.positionNumber}"
                        valueChangeListener="#{userTree.positionNumberChanged}"
                        disabled="#{!userTree.editable || !userTree.user.active}" />
                        
                      <af:selectOneRadio id="UserActiveBox"
                        label="Active?"
                        value="#{userTree.user.activeInd}"
                        readOnly="true" 
                        layout="horizontal">
                        <f:selectItem itemLabel="Yes" itemValue="Y" />
                        <f:selectItem itemLabel="No" itemValue="N" />
                      </af:selectOneRadio>
                      
                      <af:selectManyShuttle leadingHeader="All Roles"
                        trailingHeader="User's Current Roles" size="15"
                        value="#{userTree.currentUserRoles}"
                        disabled="#{!userTree.editable || !userTree.user.active}">
                        <f:selectItems value="#{userTree.allRoles}" />
                      </af:selectManyShuttle>
                      <afh:rowLayout halign="center">
                        <af:panelButtonBar>
                          <af:commandButton text="Edit User"
                            action="#{userTree.editUser}"
                            rendered="#{!userTree.editable}" />
                          <af:commandButton text="Deactivate User"
                            action="#{userTree.deactivateUser}"
                            rendered="#{!userTree.editable && userTree.user.active}" />
                          <af:commandButton text="Reactivate User"
                            action="#{userTree.reactivateUser}"
                            rendered="#{!userTree.editable && !userTree.user.active}" />
                          <af:commandButton text="Save User"
                            action="#{userTree.saveUser}"
                            rendered="#{userTree.editable}" />
                          <af:commandButton text="Cancel"
                            action="#{userTree.cancelEdit}"
                            rendered="#{userTree.editable}" />
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

