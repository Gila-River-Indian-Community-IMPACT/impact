<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
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
          action="#{permitDetail.updatePermit}" />
        <af:commandButton text="Discard changes"
          action="#{permitDetail.undoPermit}" />
      </af:panelButtonBar>
    </f:facet>
  </af:switcher>

</afh:rowLayout>
