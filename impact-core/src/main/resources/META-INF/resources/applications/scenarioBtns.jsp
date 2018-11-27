<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:switcher defaultFacet="view"
  facetName="#{applicationDetail.editMode? 'edit': 'view'}">
  <f:facet name="view">
    <af:panelButtonBar>
      <af:commandButton id="scenarioEditBtn" text="Edit"
        rendered="#{applicationDetail.editAllowed}"
        action="#{applicationDetail.enterEditMode}" />

      <af:commandButton id="scenarioDeleteBtn" text="Delete Scenario"
        rendered="#{applicationDetail.editAllowed}"
        action="dialog:scenarioDeleteDetail" useWindow="true"
        windowWidth="500" windowHeight="300" />
    </af:panelButtonBar>
  </f:facet>
  <f:facet name="edit">
    <af:panelButtonBar>
      <af:commandButton id="scenarioEditSaveBtn" text="Save"
        action="#{applicationDetail.updateScenario}" />
      <af:commandButton id="scenarioEditCancelBtn" text="Cancel"
        action="#{applicationDetail.undoScenario}" />
    </af:panelButtonBar>
  </f:facet>
</af:switcher>
