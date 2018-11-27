<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <af:switcher defaultFacet="permit"
    facetName="#{permitDetail.selectedTreeNode.type}">
    <f:facet name="permit">
      <af:switcher defaultFacet="view"
        facetName="#{permitDetail.editMode? 'edit': 'view'}">
        <f:facet name="view">
          <af:panelButtonBar>
            <af:commandButton text="Edit" rendered="#{permitDetail.editAllowed}"
              action="#{permitDetail.enterEditMode}" />
            <af:commandButton text="Include EUs"
              rendered="#{permitDetail.editAllowed}"
              action="#{permitDetail.includeEUs}" />
            <af:commandButton text="Create EU group"
              rendered="#{permitDetail.editAllowed}"
              action="#{permitDetail.createTempEUGroup}" />
            <af:commandButton text="Refresh" rendered="false"
              action="#{permitDetail.reloadPermit}" />
            <af:commandButton text="Dead-ended"
              rendered="#{permitDetail.editAllowed}"
              action="#{permitDetail.deadEndedPermit}" />
            <af:commandButton text="Current workflow activity"
              disabled="#{!permitDetail.fromTODOList}"
              action="#{permitDetail.goToCurrentWorkflow}" />
            <af:commandButton text="#{permitDetail.switchButtonText}"
              rendered="#{permitDetail.switchButton}"
              action="#{permitDetail.switchTo}" />
          </af:panelButtonBar>
        </f:facet>
        <f:facet name="edit">
          <af:panelButtonBar>
            <af:commandButton text="Save changes"
              action="#{permitDetail.updatePermit}" />
            <af:commandButton text="Discard changes"
              action="#{permitDetail.undoPermit}" />
          </af:panelButtonBar>
        </f:facet>
      </af:switcher>
    </f:facet>
    <f:facet name="euGroup">
      <af:switcher defaultFacet="view"
        facetName="#{permitDetail.editMode? 'edit': 'view'}">
        <f:facet name="view">
          <af:panelButtonBar>
            <af:commandButton text="Edit" rendered="#{permitDetail.editAllowed}"
              action="#{permitDetail.enterEditMode}" />
            <af:commandButton text="Delete"
              rendered="#{permitDetail.editAllowed && (empty selectedEUGroup.permitEUs)}"
              action="#{permitDetail.removeEUGroup}" />
          </af:panelButtonBar>
        </f:facet>
        <f:facet name="edit">
          <af:panelButtonBar>
            <af:commandButton text="Save changes"
              action="#{permitDetail.updateEUGroup}" />
            <af:commandButton text="Discard changes"
              action="#{permitDetail.undoEUGroup}" />
          </af:panelButtonBar>
        </f:facet>
      </af:switcher>
    </f:facet>
    <f:facet name="eu">
      <af:switcher defaultFacet="view"
        facetName="#{permitDetail.editMode? 'edit': 'view'}">
        <f:facet name="view">
          <af:panelButtonBar>
            <af:commandButton text="Edit" rendered="#{permitDetail.editAllowed}"
              action="#{permitDetail.enterEditMode}" />
            <af:commandButton text="Exclude EU from permit"
              rendered="#{permitDetail.editAllowed}"
              action="#{permitDetail.excludeEU}" />
          </af:panelButtonBar>
        </f:facet>
        <f:facet name="edit">
          <af:panelButtonBar>
            <af:commandButton text="Save changes"
              action="#{permitDetail.updateEU}" />
            <af:commandButton text="Discard changes"
              action="#{permitDetail.undoEU}" />
          </af:panelButtonBar>
        </f:facet>
      </af:switcher>
    </f:facet>
    <f:facet name="fee">
      <af:switcher defaultFacet="view"
        facetName="#{permitDetail.editMode? 'edit': 'view'}">
        <f:facet name="view">
          <af:panelButtonBar>
            <af:commandButton text="Edit" rendered="#{permitDetail.editAllowed}"
              action="#{permitDetail.enterEditMode}" />
          </af:panelButtonBar>
        </f:facet>
        <f:facet name="edit">
          <af:panelButtonBar>
            <af:commandButton text="Save changes"
              action="#{permitDetail.updateEU}" />
            <af:commandButton text="Discard changes"
              action="#{permitDetail.undoEU}" />
          </af:panelButtonBar>
        </f:facet>
      </af:switcher>
    </f:facet>
    <f:facet name="excludedEU">
      <af:panelButtonBar>
        <af:commandButton text="Include EU in permit"
          action="#{permitDetail.createTempEU}" />
      </af:panelButtonBar>
    </f:facet>
  </af:switcher>
</afh:rowLayout>
