<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<h:panelGrid columns="1" width="#{permitDetail.treeWidth}">
  <t:div style="overflow:auto;width:#{permitDetail.treeWidth}px;height:600px">
    <t:tree2 value="#{permitDetail.treeData}" var="node"
      clientSideToggle="false">
      <f:facet name="permit">
        <h:panelGroup>
          <af:commandMenuItem action="#{permitDetail.nodeClicked}"
            shortDesc="Permit"
            onclick="if (#{permitDetail.editMode}) { alert('Please save or discard your changes'); return false; }">
            <t:graphicImage border="0" value="/images/permit.gif" />
            <t:outputText value="#{node.description}"
              style="#{permitDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
            <t:updateActionListener property="#{permitDetail.selectedTreeNode}"
              value="#{node}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="moreGroups">
        <h:panelGroup>
          <af:commandMenuItem
            shortDesc="Collapsed EU Groups, click to expand"
            action="#{permitDetail.nodeClicked}"
            onclick="if (#{permitDetail.editMode}) { alert('Please save or discard your changes'); return false; }">
            <t:graphicImage value="/images/vellipsis.gif" border="0" />
            <t:outputText value="#{node.description}"
              style="#{permitDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
            <t:updateActionListener property="#{permitDetail.selectedTreeNode}"
              value="#{node}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="euGroup">
        <h:panelGroup>
          <af:commandMenuItem action="#{permitDetail.nodeClicked}"
            shortDesc="EU Group"
            onclick="if (#{permitDetail.editMode}) { alert('Please save or discard your changes'); return false; }">
            <t:graphicImage border="0" value="/images/permitEUGroup.gif" />
            <t:outputText value="#{node.description}"
              style="#{permitDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
            <t:updateActionListener value="#{node}"
              property="#{permitDetail.selectedTreeNode}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="moreEUs">
        <h:panelGroup>
          <af:commandMenuItem
            shortDesc="Collapsed Emissions Units, click to expand"
            action="#{permitDetail.nodeClicked}"
            onclick="if (#{permitDetail.editMode}) { alert('Please save or discard your changes'); return false; }">
            <t:graphicImage value="/images/vellipsis.gif" border="0" />
            <t:outputText value="#{node.description}"
              style="#{permitDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
            <t:updateActionListener property="#{permitDetail.selectedTreeNode}"
              value="#{node}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="eu">
        <h:panelGroup>
          <af:commandMenuItem action="#{permitDetail.nodeClicked}"
            shortDesc="Emissions Unit"
            onclick="if (#{permitDetail.editMode}) { alert('Please save or discard your changes'); return false; }">
            <t:graphicImage border="0" value="/images/EU.gif" />
            <t:outputText value="#{node.description}"
              style="#{permitDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
            <t:updateActionListener property="#{permitDetail.selectedTreeNode}"
              value="#{node}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="excludedEU">
        <h:panelGroup>
          <af:commandMenuItem action="#{permitDetail.nodeClicked}"
            shortDesc="Excluded Emissions Unit"
            onclick="if (#{permitDetail.editMode}) { alert('Please save or discard your changes'); return false; }">
            <t:graphicImage border="0" value="/images/ExcludedEU.gif" />
            <t:outputText value="#{node.description}"
              style="#{permitDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
            <t:updateActionListener property="#{permitDetail.selectedTreeNode}"
              value="#{node}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
    </t:tree2>
  </t:div>
</h:panelGrid>
