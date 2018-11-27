<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <af:switcher defaultFacet="permit"
    facetName="#{permitDetail.selectedTreeNode.type}">
    <f:facet name="permit">
      <af:switcher defaultFacet="view"
        facetName="#{permitDetail.editMode? 'edit': 'view'}">
        <f:facet name="view">
          <af:panelGroup>
            <afh:rowLayout halign="center">
              <af:panelButtonBar>
                <af:commandButton text="Edit"
                  rendered="#{permitDetail.editAllowed}"
                  action="#{permitDetail.enterEditMode}" />
                <af:commandButton text="Sync EUs with Facility"
                  id="syncEUs" rendered="#{permitDetail.editAllowed}"
                  returnListener="#{permitDetail.syncEUsWithFacility}"
                  action="#{confirmWindow.confirm}" useWindow="true"
                  windowWidth="#{confirmWindow.width}"
                  windowHeight="#{confirmWindow.height}">
                  <t:updateActionListener
                    property="#{confirmWindow.type}"
                    value="#{confirmWindow.yesNo}" />
                  <t:updateActionListener
                    property="#{confirmWindow.message}"
                    value="This will update the Emissions Unit IDs and the AQD Description values for the emissions units in this permit to match those found in the current facility inventory for this facility. Click 'Yes' to continue or 'No' to cancel." />
                </af:commandButton>
                <af:commandButton text="Add/Remove EUs"
                  rendered="#{permitDetail.editAllowed}"
                  action="#{permitDetail.includeEUs}" />
                <af:commandButton text="Add EUs from Facility"
                  rendered="#{permitDetail.addEUsFromFacilityAllowed}"
                  action="#{permitDetail.includeFacilityEUs}" />
                <af:commandButton text="Copy Permit"
                  rendered="false"
                  action="#{permitDetail.copyPermit}" />
                <af:commandButton text="Create EU group"
                  rendered="#{permitDetail.editAllowed}"
                  action="#{permitDetail.createTempEUGroup}" />
                <af:commandButton text="Refresh" rendered="false"
                  action="#{permitDetail.reloadPermit}" />
              </af:panelButtonBar>
            </afh:rowLayout>
            <af:objectSpacer width="100%" height="5" />
            <afh:rowLayout halign="center">
              <af:panelButtonBar>
                <af:commandButton text="Dead-end" id="deadEnd"
                  rendered="#{permitDetail.editAllowed && permitDetail.permit.permitGlobalStatusCD != 'E' && permitDetail.permit.permitGlobalStatusCD != 'ID'}"
                  disabled="#{permitDetail.deadEndDisabled}"
                  returnListener="#{permitDetail.deadEndedPermit}"
                  action="#{confirmWindow.confirm}" useWindow="true"
                  windowWidth="#{confirmWindow.width}"
                  windowHeight="#{confirmWindow.height}">
                  <t:updateActionListener
                    property="#{confirmWindow.type}"
                    value="#{confirmWindow.yesNo}" />
                  <t:updateActionListener
                    property="#{confirmWindow.message}"
                    value="CAUTION: Only choose this option if you are absolutely sure you will never need to work on this permit. If appropriate, please ensure that you have cancelled the corresponding workflow for this permit." />
                </af:commandButton>
                <af:commandButton text="Restore Permit/Workflow" id="undeadEnd"
                  rendered="#{permitDetail.editAllowed && permitDetail.permit.permitGlobalStatusCD == 'E'}"
                  disabled="#{permitDetail.deadEndDisabled}"
                  action="#{permitDetail.requesUnDeadEnd}" useWindow="true"
                  windowWidth="#{confirmWindow.width}"
                  windowHeight="#{confirmWindow.height}">
                </af:commandButton>
                <af:commandButton text="Withdraw Permit" id="denyPermit"
                  rendered="#{permitDetail.deniable}"
                  returnListener="#{permitDetail.denyPermit}"
                  action="#{confirmWindow.confirm}" useWindow="true"
                  windowWidth="#{confirmWindow.width}"
                  windowHeight="#{confirmWindow.height}">
                  <t:updateActionListener
                    property="#{confirmWindow.type}"
                    value="#{confirmWindow.yesNo}" />
                </af:commandButton>
				<af:commandButton text="Delete Permit" id="deletePermit"
                  rendered="#{permitDetail.allowedToDeletePermit}"
                  action="#{confirmWindow.confirm}" useWindow="true"
                  windowWidth="#{confirmWindow.width}"
                  windowHeight="#{confirmWindow.height}">
                  <t:updateActionListener
                    property="#{confirmWindow.type}"
                    value="#{confirmWindow.yesNo}" />
                  <t:updateActionListener
                    property="#{confirmWindow.method}"
                    value="permitDetail.deleteLegacyPermit" />  
                  <t:updateActionListener
                    property="#{confirmWindow.message}"
                    value="All data associated with this permit will be deleted. Would you like to continue?" />  
                </af:commandButton>
                <af:commandButton text="Un-Withdraw" id="unDenyPermit"
                  rendered="#{permitDetail.unDeniable}"
                  returnListener="#{permitDetail.unDenyPermit}"
                  action="#{confirmWindow.confirm}" useWindow="true"
                  windowWidth="#{confirmWindow.width}"
                  windowHeight="#{confirmWindow.height}">
                  <t:updateActionListener
                    property="#{confirmWindow.type}"
                    value="#{confirmWindow.yesNo}" />
                </af:commandButton>
                <af:commandButton text="Workflow Task"
                  disabled="#{!permitDetail.fromTODOList}"
                  action="#{permitDetail.goToCurrentWorkflow}" />
                <af:commandButton immediate="true" text="Show Current Facility Inventory"
                  action="#{facilityProfile.submitProfileById}">
                  <t:updateActionListener
                    property="#{facilityProfile.facilityId}"
                    value="#{permitDetail.currentFacility.facilityId}" />
                  <t:updateActionListener
                    property="#{menuItem_facProfile.disabled}"
                    value="false" />
                </af:commandButton>
                <af:commandButton immediate="true" text="Generate Permit Info Doc"
                  useWindow="true" windowWidth="500" windowHeight="300"
                  action="#{permitDetail.generatePermitDoc}">                 
                </af:commandButton>
                <%--  not used by IMPACT              
                <af:commandButton text="Convert to PTI"
                  rendered="#{permitDetail.convertButton}"
                  id="convertTo"
                  returnListener="#{permitDetail.convertToPTI}"
                  action="#{confirmWindow.confirm}" useWindow="true"
                  windowWidth="#{confirmWindow.width}"
                  windowHeight="#{confirmWindow.height}">
                  <t:updateActionListener
                    property="#{confirmWindow.type}"
                    value="#{confirmWindow.yesNo}" />
                  <t:updateActionListener
                    property="#{confirmWindow.message}"
                    value="CAUTION: This function will change this issued NSR into a Title V NSR for this facility. In addition, it will update the EU Permit Status and EU PTI Status in the Facility Inventory. Are you sure you would like to proceed?" />
                </af:commandButton>
                --%> 
              </af:panelButtonBar>
            </afh:rowLayout>
          </af:panelGroup>
        </f:facet>
        <f:facet name="edit">
          <af:panelButtonBar>
            <af:commandButton text="Save changes" id="saveChanges"
              action="#{permitDetail.updatePermit}" />
            <af:commandButton text="Discard changes" immediate="true" id="discardChanges"
              action="#{permitDetail.undoPermit}" />
          </af:panelButtonBar>
        </f:facet>
      </af:switcher>
    </f:facet>
    <f:facet name="euGroup">
      <af:switcher defaultFacet="view"
        facetName="#{permitDetail.editMode? 'edit': 'view'}">
        <f:facet name="view">
          <af:panelGroup layout="vertical">
            <afh:rowLayout halign="center">
              <af:panelButtonBar>
                <af:commandButton text="Edit"
                  rendered="#{permitDetail.editAllowed}"
                  action="#{permitDetail.enterEditMode}" />
                <af:commandButton text="Delete"
                  shortDesc="If you wish to delete an EU group, you must first remove all EUs from the group and save your changes"
                  disabled="#{!(permitDetail.editAllowed && (empty permitDetail.selectedEUGroup.permitEUs))}"
                  action="#{permitDetail.removeEUGroup}" />
              </af:panelButtonBar>
            </afh:rowLayout>
            <af:panelForm>
              <af:outputText
                value="Note: if you wish to delete an EU group, you must first remove all EUs from the group and save your changes." />
            </af:panelForm>
          </af:panelGroup>
        </f:facet>
        <f:facet name="edit">
          <af:panelButtonBar>
            <af:commandButton text="Save changes"
              action="#{permitDetail.updateEUGroup}" />
            <af:commandButton text="Discard changes"
              action="#{permitDetail.reloadPermit}" />
          </af:panelButtonBar>
        </f:facet>
      </af:switcher>
    </f:facet>
    <f:facet name="eu">
      <af:switcher defaultFacet="view"
        facetName="#{permitDetail.editMode? 'edit': 'view'}">
        <f:facet name="view">
          <af:panelButtonBar>
            <af:commandButton text="Edit"
              rendered="#{permitDetail.editAllowed}"
              action="#{permitDetail.enterEditMode}" />
            <af:commandButton text="Exclude EU from permit"
              rendered="#{permitDetail.editAllowed}"
              action="#{permitDetail.excludeEU}" />
            <af:commandButton text="Remove EU from current group"
              rendered="#{permitDetail.editAllowed && ! permitDetail.selectedEU.euGroup.individualEUGroup}"
              action="#{permitDetail.removeEUFromCurrentGroup}" />
          </af:panelButtonBar>
        </f:facet>
        <f:facet name="edit">
          <af:panelButtonBar>
            <af:commandButton text="Save changes"
              action="#{permitDetail.updateEU}" />
            <af:commandButton text="Discard changes"
              action="#{permitDetail.reloadPermit}" />
          </af:panelButtonBar>
        </f:facet>
      </af:switcher>
    </f:facet>
    <f:facet name="fee">
      <af:switcher defaultFacet="view"
        facetName="#{permitDetail.editMode? 'edit': 'view'}">
        <f:facet name="view">
          <af:panelButtonBar>
            <af:commandButton text="Edit"
              rendered="#{permitDetail.editAllowed}"
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
        <af:commandButton text="Emissions Units"
          action="#{permitDetail.createTempEU}" />
      </af:panelButtonBar>
    </f:facet>
    
  </af:switcher>
</afh:rowLayout>
