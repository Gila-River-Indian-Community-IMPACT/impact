<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:switcher defaultFacet="view"
  facetName="#{(applicationDetail.editMode || applicationDetail.semiEditMode) ? 'edit': 'view'}">
  <f:facet name="view">
    <afh:tableLayout halign="center" cellSpacing="10">
      <f:facet name="separator">
        <af:objectSpacer width="10" height="10" />
      </f:facet>
      <afh:rowLayout halign="center">
        <af:panelButtonBar>
          <af:commandButton id="appEditBtn" text="Edit"
            rendered="#{applicationDetail.editAllowed}"
            action="#{applicationDetail.enterEditMode}" />           
          <af:commandButton text="Load Data From Files"		rendered="false"									
					 useWindow="true" windowWidth="500" windowHeight="100"	
					action="#{applicationDetail.loadExcelData}">
		  </af:commandButton>
		  <af:commandButton id="appSemiEditBtn" text="Edit"
            rendered="#{applicationDetail.semiEditAllowed}"
            action="#{applicationDetail.enterSemiEditMode}" />           
          <af:commandButton id="appSelectEusBtn" text="Select EUs"
            rendered="#{applicationDetail.editAllowed && applicationDetail.application.applicationTypeCD != 'RPE'}"
            action="#{applicationDetail.displayEUShuttle}" />
          <%-- Not required by AQD at this time  
          <af:commandButton id="appCreateEUGrpBtn"
            text="Create EU Group"
            rendered="#{applicationDetail.editAllowed && applicationDetail.tvApplication}"
            action="#{applicationDetail.createTvEuGroup}" />
          --%>  
          <af:commandButton id="appDeleteBtn" text="Delete Application"
            rendered="#{applicationDetail.deleteAllowed && applicationDetail.internalApp}"
            action="#{applicationDetail.startDelete}" useWindow="true"
            windowWidth="500" windowHeight="300"/>
          <af:commandButton id="appValidateBtn" text="Validate"
            rendered="#{applicationDetail.validateAllowed}"
            action="#{applicationDetail.validateApplication}" />
          <af:commandButton id="testSubmitBtn" text="TEST SUBMIT"
            rendered="false"
            action="#{applicationDetail.testSubmit}" />
          <af:switcher defaultFacet="internal"
            facetName="#{applicationDetail.internalApp ? 'internal' : 'external'}">
            <f:facet name="internal">
              <af:panelGroup>
                <af:commandButton id="appSubmitButton" text="Submit"
                  rendered="#{applicationDetail.submitAllowed}"
                  useWindow="true" windowWidth="500" windowHeight="300"
                  action="#{applicationDetail.startSubmitApplication}" />
                <af:commandButton id="appGeneratePermitButton"
                  text="Duplicate Permit/Workflow"                  
                  rendered="#{applicationDetail.renderGeneratePermitButton && !applicationDetail.readOnlyUser}"
                  useWindow="true" windowWidth="500" windowHeight="300"
                  action="#{applicationDetail.startSubmitApplication}" />
              </af:panelGroup>
            </f:facet>
            <f:facet name="external">
              <af:panelButtonBar>
	              <af:commandButton id="appExtSubmitButton" text="Submit"
	              	disabled="#{!myTasks.impactFullEnabled || !myTasks.hasSubmit}"
	                rendered="#{applicationDetail.submitAllowed}"
	                action="#{applicationDetail.applySubmitFromPortal}"
	                useWindow="true" windowWidth="#{submitTask.attestWidth}"
	                windowHeight="#{submitTask.attestHeight}" 
	                shortDesc="#{myTasks.hasSubmit ? '' : 
	                	'Submit'}">
	                <t:updateActionListener property="#{submitTask.type}"
	                  value="#{submitTask.yesNo}" />
	                <t:updateActionListener property="#{submitTask.task}"
	                  value="#{applicationDetail.task}" />
	              </af:commandButton>
	              <%-- hide the download attestation button until it is not supported in IMPACT
	              <af:goButton id="appExtAttestationDocButton" text="Download Attestation Document"
	                rendered="#{applicationDetail.submitAllowed}" targetFrame="_blank"
	                destination="#{applicationDetail.attestationDocURL}"/>--%>
              </af:panelButtonBar>
            </f:facet>
          </af:switcher>
          <af:commandButton id="appWorkflowTaskBtn" text="Workflow Task"
            rendered="#{applicationDetail.fromTODOList}"
            action="#{applicationDetail.goToCurrentWorkflow}" />

          <af:commandButton text="Returned" id="rprReturned"
            rendered="#{applicationDetail.application.applicationTypeCD == 'RPR' && !empty applicationDetail.application.submittedDate && applicationDetail.application.dispositionFlag == null}"
            returnListener="#{applicationDetail.rprReturned}"
            action="#{confirmWindow.confirm}" useWindow="true"
            windowWidth="#{confirmWindow.width}"
            windowHeight="#{confirmWindow.height}">
            <t:updateActionListener property="#{confirmWindow.type}"
              value="#{confirmWindow.yesNo}" />
          </af:commandButton>

          <af:commandButton text="Denied" id="rpeDenied"
            rendered="#{applicationDetail.application.applicationTypeCD == 'RPE' && !empty applicationDetail.application.submittedDate && applicationDetail.application.dispositionFlag == null}"
            returnListener="#{applicationDetail.rpeDenied}"
            action="#{confirmWindow.confirm}" useWindow="true"
            windowWidth="#{confirmWindow.width}"
            windowHeight="#{confirmWindow.height}">
            <t:updateActionListener property="#{confirmWindow.type}"
              value="#{confirmWindow.yesNo}" />
          </af:commandButton>
          
          <af:commandButton text="Dead-ended" id="rpeDeadEnded"
            rendered="#{applicationDetail.application.applicationTypeCD == 'RPE' && !empty applicationDetail.application.submittedDate && applicationDetail.application.dispositionFlag == null}"
            returnListener="#{applicationDetail.rpeDeadEnded}"
            action="#{confirmWindow.confirm}" useWindow="true"
            windowWidth="#{confirmWindow.width}"
            windowHeight="#{confirmWindow.height}">
            <t:updateActionListener property="#{confirmWindow.type}"
              value="#{confirmWindow.yesNo}" />
          </af:commandButton>

          <af:commandButton text="Accepted" id="pbrAccepted"
            action="dialog:confirmPBRAcceptance" useWindow="true"
            rendered="#{applicationDetail.internalApp && applicationDetail.application.applicationTypeCD == 'PBR' &&
            	applicationDetail.generalIssuanceUser && !empty applicationDetail.application.submittedDate && 
            	applicationDetail.application.dispositionFlag == 'r'}"
            windowWidth="600"
            windowHeight="200">
          </af:commandButton>
          <af:commandButton text="Denied" id="pbrDenied"
            returnListener="#{applicationDetail.pbrDenied}"
            action="#{confirmWindow.confirm}" useWindow="true"
            rendered="#{applicationDetail.internalApp && applicationDetail.application.applicationTypeCD == 'PBR' && !empty applicationDetail.application.submittedDate && applicationDetail.application.dispositionFlag == 'r'}"
            windowWidth="#{confirmWindow.width}"
            windowHeight="#{confirmWindow.height}">
            <t:updateActionListener property="#{confirmWindow.type}"
              value="#{confirmWindow.yesNo}" />
          </af:commandButton>
          <af:commandButton text="No Longer Applicable"
            id="pbrNoApplicable"
            returnListener="#{applicationDetail.pbrNoApplicable}"
            rendered="#{applicationDetail.internalApp && applicationDetail.application.applicationTypeCD == 'PBR' && !empty applicationDetail.application.submittedDate && applicationDetail.application.dispositionFlag == 'a'}"
            action="#{confirmWindow.confirm}" useWindow="true"
            windowWidth="#{confirmWindow.width}"
            windowHeight="#{confirmWindow.height}">
            <t:updateActionListener property="#{confirmWindow.type}"
              value="#{confirmWindow.yesNo}" />
          </af:commandButton>
        </af:panelButtonBar>
      </afh:rowLayout>
      <afh:rowLayout halign="center">
        <af:panelButtonBar>
          <af:commandButton id="appShowFPBtn"
            text="Show Associated Facility Inventory"
            rendered="#{(applicationDetail.showFacilityProfileBtn && applicationDetail.internalApp) || (!applicationDetail.internalApp && !empty applicationDetail.application.submittedDate)}"
            action="#{applicationDetail.showFacilityProfile}">
              <t:updateActionListener
                property="#{menuItem_facProfile.disabled}" value="false" />
          </af:commandButton>

          <af:commandButton id="appUpdateFPBtn"
            text="Associate with Current Facility Inventory"
            rendered="#{applicationDetail.okToUpdateFacilityProfile}"
            action="dialog:appSynchProfileDetail" useWindow="true"
            windowWidth="500" windowHeight="300">
          </af:commandButton>

          <af:commandButton id="synchFacEUBtn"
            text="Sync EUs with Facility"
            rendered="#{applicationDetail.okToSyncEUsWithProfile}"
            action="dialog:appSynchProfileDetail" useWindow="true"
            windowWidth="500" windowHeight="300">
          </af:commandButton>

          <af:commandButton id="associatedPermitsBtn"
            text="List Associated Permits"            
            rendered="#{applicationDetail.internalApp && !applicationDetail.readOnlyUser}"
            useWindow="true" windowWidth="500" windowHeight="300"
            action="dialog:associatedPermits">
          </af:commandButton>

          <af:commandButton id="printPublicBtn"
            text="#{applicationDetail.publicFormButtonLabel}"            
            rendered="#{applicationDetail.renderDisplayPublicFormButton}"
            useWindow="true" windowWidth="500" windowHeight="300"
            action="#{applicationDetail.preparePrintableDocs}">
            <t:updateActionListener 
              value="true" property="#{applicationDetail.hideTradeSecret}"/>
          </af:commandButton>

          <af:commandButton id="printTradeSecretBtn"
            text="Download/Print Trade Secret Version"
            rendered="#{applicationDetail.renderDisplayTradeSecretFormButton && !applicationDetail.publicReadOnlyUser}"
            useWindow="true" windowWidth="500" windowHeight="300"
            action="#{applicationDetail.preparePrintableDocs}">
			<t:updateActionListener 
              value="false" property="#{applicationDetail.hideTradeSecret}"/>
          </af:commandButton>
          
          <af:commandButton id="zipApplicationBtn"
            text="Generate Zip File"            
            rendered="#{applicationDetail.renderZipButton && !applicationDetail.readOnlyUser}"
            action="#{applicationDetail.zipApplication}"/>
        </af:panelButtonBar>
      </afh:rowLayout>
    </afh:tableLayout>
  </f:facet>
  <f:facet name="edit">
    <af:panelButtonBar>
      <af:commandButton id="appSaveBtn" text="Save"
        action="#{applicationDetail.updateApplication}">
	  </af:commandButton>       
      
      <af:commandButton id="appCancelBtn" text="Cancel"
        action="#{applicationDetail.undoApplication}" />
    </af:panelButtonBar>
  </f:facet>
</af:switcher>
