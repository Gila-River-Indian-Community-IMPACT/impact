<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<h:panelGrid columns="1">
  <t:div style="overflow:auto;width:200px;height:400px">
    <t:tree2 value="#{applicationDetail.treeData}" var="node"
      clientSideToggle="false">
      <f:facet name="application">
        <h:panelGroup>
          <af:commandMenuItem action="#{applicationDetail.nodeClicked}"
            onclick="if (#{applicationDetail.editMode}) { alert('Please save or cancel your changes'); return false; }"
            shortDesc="view/edit facility-wide data for application">
            <af:switcher
              facetName="#{node.userObject.validated? 'validated': 'nonValidated'}"
              defaultFacet="nonValidated">
              <f:facet name="validated">
                <t:graphicImage border="0" value="/images/Check.gif"
                  title="Application validated" />
              </f:facet>
              <f:facet name="nonValidated">
                <t:graphicImage border="0" value="/images/Caution.gif"
                  title="Application not Validated " />
              </f:facet>
            </af:switcher>
            <h:outputText value="#{node.description}"
              style="#{applicationDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" title="Application"/>
            <af:setActionListener from="#{node}"
              to="#{applicationDetail.selectedTreeNode}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="moreNodes">
        <h:panelGroup>
          <af:commandMenuItem
            action="#{applicationDetail.nodeClicked}"
            onclick="if (#{applicationDetail.editMode}) { alert('Please save or cancel your changes'); return false; }">
            <t:graphicImage value="/images/vellipsis.gif" border="0"
              title="Collapsed Emissions Units, click to expand" />
            <t:outputText value="#{node.description}"
              title="Collapsed Emissions Units, click to expand"
              style="#{applicationDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
            <af:setActionListener from="#{node}"
              to="#{applicationDetail.selectedTreeNode}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="eu">
        <h:panelGroup>
          <af:commandMenuItem action="#{applicationDetail.nodeClicked}"
            onclick="if (#{applicationDetail.editMode}) { alert('Please save or cancel your changes'); return false; }"
            shortDesc="view/edit emissions unit - specific data">
            <af:switcher
              facetName="#{applicationDetail.application.facility.permitClassCd == 'tv' &&
                        !(empty node.userObject.fpEU.tvClassCd) && 
                        node.userObject.fpEU.tvClassCd == 'ins' ? 'insigEU' : 'regularEU'}"
              defaultFacet="regularEU">
              <f:facet name="regularEU">
                <af:switcher
                  facetName="#{node.userObject.validated? 'validated': 'nonValidated'}"
                  defaultFacet="nonValidated">
                  <f:facet name="validated">
                    <t:graphicImage border="0" value="/images/EU.gif"
                      title="Emissions Unit validated " />
                  </f:facet>
                  <f:facet name="nonValidated">
                    <t:graphicImage border="0" value="/images/EUcaution.gif"
                      title="Emissions Unit not validated " />
                  </f:facet>
                </af:switcher>
              </f:facet>
              <f:facet name="insigEU">
                <af:switcher
                  facetName="#{node.userObject.validated? 'validated': 'nonValidated'}"
                  defaultFacet="nonValidated">
                  <f:facet name="validated">
                    <t:graphicImage border="0" value="/images/insignificantEU.gif"
                      title="Emissions Unit validated " />
                  </f:facet>
                  <f:facet name="nonValidated">
                    <t:graphicImage border="0" value="/images/insignificantEUcaution.gif"
                      title="Emissions Unit not validated " />
                  </f:facet>
                </af:switcher>
              </f:facet>
            </af:switcher>
            <h:outputText value="#{node.description}"
              style="#{applicationDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" 
              title="#{node.userObject.fpEU.mouseOverTip}"/>
            <af:setActionListener from="#{node}"
              to="#{applicationDetail.selectedTreeNode}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="insignificantEU">
        <h:panelGroup>
          <af:commandMenuItem action="#{applicationDetail.nodeClicked}"
            onclick="if (#{applicationDetail.editMode}) { alert('Please save or cancel your changes'); return false; }"
            shortDesc="view/edit emissions unit - specific data">
            <af:switcher
              facetName="#{node.userObject.validated? 'validated': 'nonValidated'}"
              defaultFacet="nonValidated">
              <f:facet name="validated">
                <t:graphicImage border="0"
                  value="/images/insignificantEU.gif"
                  title="Insignificant Emissions Unit validated" />
              </f:facet>
              <f:facet name="nonValidated">
                <t:graphicImage border="0"
                  value="/images/insignificantEUcaution.gif"
                  title="Insignificant Emissions Unit not validated" />
              </f:facet>
            </af:switcher>
            <h:outputText value="#{node.description}"
              style="#{applicationDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
            <af:setActionListener from="#{node}"
              to="#{applicationDetail.selectedTreeNode}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="excludedEU">
        <h:panelGroup>
          <af:commandMenuItem action="#{applicationDetail.nodeClicked}"
            onclick="if (#{applicationDetail.editMode}) { alert('Please save or cancel your changes'); return false; }"
            shortDesc="include emissions unit in application">
            <t:graphicImage border="0" value="/images/ExcludedEU.gif"
              title="Emissions Unit excluded from application" />
            <h:outputText value="#{node.description}"
              style="#{applicationDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" 
              title="#{node.userObject.mouseOverTip}"/>
            <af:setActionListener from="#{node}"
              to="#{applicationDetail.selectedTreeNode}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="notIncludableEU">
        <h:panelGroup>
          <af:commandMenuItem action="#{applicationDetail.nodeClicked}"
            onclick="if (#{applicationDetail.editMode}) { alert('Please save or cancel your changes'); return false; }"
            shortDesc="view reason why Emissions Unit cannot be included in application">
            <t:graphicImage border="0"
              value="/images/NotIncludableEU.gif"
              title="Emissions Unit cannot be included in application" />
            <h:outputText value="#{node.description}"
              style="#{applicationDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
            <af:setActionListener from="#{node}"
              to="#{applicationDetail.selectedTreeNode}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="euGroup">
        <h:panelGroup>
          <af:commandMenuItem action="#{applicationDetail.nodeClicked}"
            onclick="if (#{applicationDetail.editMode}) { alert('Please save or cancel your changes'); return false; }"
            shortDesc="view Emissions Unit Group data">
            <t:graphicImage border="0" value="/images/tvAppEUGroup.gif"
              title="Emissions Unit Group" />
            <h:outputText value="#{node.description}"
              style="#{applicationDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
            <af:setActionListener from="#{node}"
              to="#{applicationDetail.selectedTreeNode}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
      <f:facet name="scenario">
        <h:panelGroup>
          <af:commandMenuItem action="#{applicationDetail.nodeClicked}"
            onclick="if (#{applicationDetail.editMode}) { alert('Please save or cancel your changes'); return false; }"
            shortDesc="view/edit Alternate Operating Scenario data">
            <t:graphicImage border="0" value="/images/scenario.gif"
              title="Alternate Operating Scenario" />
            <h:outputText value="#{node.description}"
              style="#{applicationDetail.selectedTreeNode == node ? 'color:#FFFFFF; background-color:#000000;' : ''}" />
            <af:setActionListener from="#{node}"
              to="#{applicationDetail.selectedTreeNode}" />
          </af:commandMenuItem>
        </h:panelGroup>
      </f:facet>
    </t:tree2>
  </t:div>
</h:panelGrid>

