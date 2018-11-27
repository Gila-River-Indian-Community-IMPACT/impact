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
      <af:commandButton id="euEditBtn" text="Edit"
        rendered="#{applicationDetail.editAllowed}"
        action="#{applicationDetail.enterEditMode}" />
      <af:commandButton id="euCopyEUDataBtn" text="Copy EU Data"
        rendered="#{applicationDetail.renderCopyEUDataButton}"
        action="#{applicationDetail.displayEUCopyShuttle}" />
      <af:commandButton id="euExcludeEUBtn"
        text="Exclude EU from application"
        rendered="#{applicationDetail.renderExcludeEUButton}"
        action="#{applicationDetail.excludeEU}" />
      <af:commandButton id="euShowFPBtn" text="Show Associated Facility Inventory"
        action="#{applicationDetail.showFacilityProfile}" />
    </af:panelButtonBar>
  </f:facet>
  <f:facet name="edit">
    <af:panelButtonBar>
      <af:commandButton id="euEditSaveBtn" text="Save"
        action="#{applicationDetail.updateEU}" />
      <af:commandButton id="euEditCancelBtn" text="Cancel"
        action="#{applicationDetail.undoEU}" />
    </af:panelButtonBar>
  </f:facet>
</af:switcher>
