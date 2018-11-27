<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" title="New Application">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form usesUpload="true">
      <af:page id ="newApplcation">
        <af:messages />
        <afh:rowLayout halign="center" >
	        <af:panelForm rendered="true"
	          partialTriggers="DocTypeChoice rpcTypeChoice 
	            correctApp amendApp cloneApp requestType futureAddress targetCounty disposition jfoRecommendation">
	
	          <af:inputText label="Facility ID :"
	            value="#{applicationSearch.newApplicationFacilityID}"
	            maximumLength="20" readOnly="true" />
	
	          <af:selectOneChoice id="requestType" label="Request type :"
	            unselectedLabel="Please select"
	            value="#{applicationSearch.newApplicationClass}"
	            autoSubmit="true">
	            <f:selectItems
	              value="#{applicationReference.applicationClasses}" />
	          </af:selectOneChoice>
	
	          <af:panelForm
	            rendered="#{!applicationSearch.relocationClass && !applicationSearch.delegationClass}">
	            <af:selectOneChoice id="rpcTypeChoice" immediate="true"
	              label="Modification Type :"
	              unselectedLabel="Please select"
	              rendered="#{applicationSearch.rpcRequest}"
	              value="#{applicationSearch.rpcTypeCd}" autoSubmit="true">
	              <f:selectItems value="#{applicationReference.rpcTypeDefs}" />
	            </af:selectOneChoice>
	
	            <af:selectOneChoice id="permitChoice"
	              label="Permit Number :"
	              rendered="#{applicationSearch.permitList != null}"
	              value="#{applicationSearch.permitId}">
	              <f:selectItems value="#{applicationSearch.permitList}" />
	            </af:selectOneChoice>
	            <af:inputText readOnly="true"
	              value="This drop down list contains only permits that have been issued final and have not expired."
	              rendered="#{applicationSearch.permitList != null && applicationSearch.newApplication.applicationTypeCD == 'RPC'}"
	              inlineStyle="font-size: 12px" />
	            <af:inputText readOnly="true"
	              value="This drop down list contains only permits that have been issued final"
	              rendered="#{applicationSearch.permitList != null && applicationSearch.newApplication.applicationTypeCD == 'RPR'}"
	              inlineStyle="font-size: 12px" />
	            <af:inputText readOnly="true"
	              value="and contain at least one Active or Extended EU not Permanently Shutdown."
	              rendered="#{applicationSearch.permitList != null && applicationSearch.newApplication.applicationTypeCD == 'RPR'}"
	              inlineStyle="font-size: 12px" />
	            <af:inputText readOnly="true"
	              value="This drop down list contains only those NSR permits that have"
	              rendered="#{applicationSearch.permitList != null && applicationSearch.newApplication.applicationTypeCD == 'RPE'}"
	              inlineStyle="font-size: 12px" />
	            <af:inputText readOnly="true"
	              value="been issued final with reason as Initial install or Chapter 31 and"
	              rendered="#{applicationSearch.permitList != null && applicationSearch.newApplication.applicationTypeCD == 'RPE'}"
	              inlineStyle="font-size: 12px" />
	            <af:inputText readOnly="true"
	              value="an effective date within the last 18 months."
	              rendered="#{applicationSearch.permitList != null && applicationSearch.newApplication.applicationTypeCD == 'RPE'}"
	              inlineStyle="font-size: 12px" />
	            <af:selectOneChoice label="PBR Type :"
	              unselectedLabel="Please select"
	              rendered="#{applicationSearch.pbrNotification}"
	              value="#{applicationSearch.pbrTypeCd}">
	              <f:selectItems
	                value="#{applicationReference.pbrTypeDefs.items[(empty applicationDetail.pbrTypeCd? '': applicationDetail.pbrTypeCd)]}" />
	            </af:selectOneChoice>
	            <af:selectBooleanCheckbox
	              label="Facility-requested correction to application :"
	              id="correctApp"
	              rendered="#{applicationSearch.correctable && !applicationSearch.amendedApplication 
	              && !applicationSearch.clonedApplication}"
	              value="#{applicationSearch.correctedApplication}"
	              autoSubmit="true" />
	            <af:outputText inlineStyle="font-size: 12px"
	              rendered="#{applicationSearch.correctedApplication}"
	              value="#{applicationSearch.correctedApplicationPopupText}" />
	            <af:selectBooleanCheckbox
	              label="AQD Amendment to application :" id="amendApp"
	              rendered="#{applicationSearch.amendable && !applicationSearch.correctedApplication
	              && !applicationSearch.clonedApplication}"
	              value="#{applicationSearch.amendedApplication}"
	              autoSubmit="true" />
	            <af:selectBooleanCheckbox
	              label="Copy data from existing application :"
	              id="cloneApp"
	              rendered="#{applicationSearch.cloneable && !applicationSearch.correctedApplication
	              && !applicationSearch.amendedApplication}"
	              value="#{applicationSearch.clonedApplication}"
	              autoSubmit="true" />
	            <af:selectOneChoice label="Affected Application Number :"
	              unselectedLabel="Please select"
	              value="#{applicationSearch.previousApplicationNumber}"
	              rendered="#{applicationSearch.correctedApplication || applicationSearch.amendedApplication || applicationSearch.clonedApplication}">
	              <f:selectItems
	                value="#{applicationSearch.previousApplicationNumbers}" />
	            </af:selectOneChoice>
	
	            <af:inputText label="Reason for Correction :" rows="4"
	              columns="50" maximumLength="500"
	              rendered="#{applicationSearch.correctable && applicationSearch.correctedApplication && !applicationSearch.amendedApplication}"
	              value="#{applicationSearch.correctedReason}" />
	
	            <f:facet name="footer">
	              <af:panelButtonBar>
	                <af:commandButton text="Create"
	                  actionListener="#{applicationSearch.createNewApplication}"
	                  disabled="#{applicationSearch.rpcRequest && applicationSearch.permitList == null}" />
	                <af:commandButton text="Cancel" immediate="true"
	                  actionListener="#{applicationSearch.cancelNewApplication}" />
	              </af:panelButtonBar>
	            </f:facet>
	          </af:panelForm>
	
	
	
	
	          <af:panelForm rendered="#{applicationSearch.relocationClass}">
	            <af:inputText id="requstNum" label="Relocation Request #"
	              rows="1"
	              value="#{relocation.relocateRequest.requestDisplayId}"
	              rendered="#{!relocation.relocateRequest.newRecord}"
	              readOnly="true">
	            </af:inputText>
	
	            <af:selectInputDate label="Date Request Received: "
	              showRequired="true" id="mailingDate"
	              value="#{relocation.relocateRequest.receivedDate}"
	              rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
	              readOnly="#{! relocation.editable}">
	            </af:selectInputDate>
	
	            <af:selectInputDate label="Date Request Submitted: "
	              id="submittedDate"
	              value="#{relocation.relocateRequest.submittedDate}"
	              rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
	              readOnly="true">
	            </af:selectInputDate>
	
	            <af:selectBooleanCheckbox label="ITR Form Complete :"
	              id="itrForm"
	              rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
	              readOnly="#{! relocation.editable}"
	              value="#{relocation.relocateRequest.formComplete}" />
	
	            <af:selectBooleanCheckbox label="Facility compliant :"
	              id="facCompliant"
	              rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
	              readOnly="#{! relocation.editable}"
	              value="#{relocation.relocateRequest.facilityCompliant}" />
	
	            <af:selectBooleanCheckbox label="Site pre-approved :"
	              id="sitePreapproved" readOnly="#{! relocation.editable}"
	              rendered="#{relocation.relocateRequest.applicationTypeCD=='' && (relocation.relocateRequest.requestType=='RPS' || relocation.relocateRequest.requestType=='ITR')}"
	              value="#{relocation.relocateRequest.sitePreApproved}" />
	
	            <af:selectOneChoice label="JFO Recommendation: "
	              id="jfoRecommendation" readOnly="#{! relocation.editable}"
	              autoSubmit="true"
	              value="#{relocation.relocateRequest.jfoRecommendation}"
	              rendered="#{relocation.relocateRequest.applicationTypeCD!='' && relocation.relocateRequest.applicationTypeCD != 'RPS' }">
	              <f:selectItems
	                value="#{relocation.relocateRequest.JFODef.items[(empty relocation.relocateRequest.jfoRecommendation ? '' : relocation.relocateRequest.jfoRecommendation)]}" />
	            </af:selectOneChoice>
	            
	            <af:selectOneChoice label="Request Disposition: " id="disposition"
	    		  readOnly="#{! relocation.editable}"
	    		  rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
	   		      value="#{relocation.relocateRequest.requestDisposition}">
	   			<f:selectItems
	      		  value="#{relocation.relocateRequest.dispositionDef.items[(empty relocation.relocateRequest.requestDisposition ? '' : relocation.relocateRequest.requestDisposition)]}" />
	            </af:selectOneChoice>
	
	            <af:selectOneChoice label="Future Address: "
	              id="futureAddressChoice" readOnly="#{relocation.readOnly}"
	              rendered="#{relocation.relocateToPreApproved}"
	              value="#{relocation.preApprovedAddressCd}">
	              <f:selectItems
	                value="#{relocation.preApprovedAddressList}" />
	            </af:selectOneChoice>
	           
	            <af:inputText id="futureAddress"
	              label="#{relocation.relocateToPreApproved ? '' : 'Future Address: '}" rows="2"
	              
	              tip="#{relocation.relocateToPreApproved ? 
	              'Please choose a future address from the drop down list above. Only use the text box if the pre-approved address is not included in the drop down list.' : ''}"
	              value="#{relocation.relocateRequest.futureAddress}"
	              rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
	              columns="50" maximumLength="150"
	              readOnly="#{! relocation.editable}">
	            </af:inputText>
	
	            <af:selectOneChoice label="Target County: "
	              id="targetCounty" readOnly="#{! relocation.editable}"
	              rendered="#{relocation.nonRPS}"
	              value="#{relocation.relocateRequest.targetCountyCd}">
	              <f:selectItems value="#{infraDefs.counties}" />
	            </af:selectOneChoice>
	
	            <af:showDetailHeader text="Issuances" size="2"
	              disclosed="true"
	              rendered="#{relocation.relocateRequest.submittedDate != null}">
	              <jsp:include page="../util/issuancesTable.jsp"
	                flush="true" />
	            </af:showDetailHeader>
	
	            <f:facet name="footer">
	              <af:panelButtonBar>
	                <af:commandButton text="Save"
	                  rendered="#{relocation.relocateRequest.applicationTypeCD != '' && relocation.editable}"
	                  actionListener="#{relocation.save}" />
	                <af:commandButton text="Submit"
	                  rendered="#{!relocation.editable && relocation.relocateRequest.applicationTypeCD != '' && relocation.relocateRequest.submittedDate==null && relocation.relocateRequest.requestId>0}"
	                  actionListener="#{relocation.submit}" />
	                <af:commandButton text="Delete"
	                  rendered="#{!relocation.editable && !relocation.relocateRequest.newRecord && relocation.relocateRequest.submittedDate==null}"
	                  actionListener="#{relocation.deleteRequest}" />
	                <af:commandButton text="Edit" disabled="false"
	                  rendered="#{(!relocation.editable && relocation.relocateRequest.submittedDate==null) || (!relocation.editable && relocation.admin)}"
	                  actionListener="#{relocation.startEditRequest}" />
	                <af:commandButton text="Cancel" immediate="true"
	                  rendered="#{relocation.editable && !relocation.relocateRequest.newRecord}"
	                  actionListener="#{relocation.cancelEditRequest}" />
	                <af:commandButton text="Cancel" immediate="true"
	                  rendered="#{relocation.relocateRequest.newRecord}"
	                  actionListener="#{relocation.cancelNewApplication}" />
	              </af:panelButtonBar>
	            </f:facet>
	          </af:panelForm>
	
	
	          <af:panelForm rendered="#{applicationSearch.delegationClass}">
	
	            <af:selectInputDate label="Date Request Received: "
	              showRequired="true" id="mailingDate"
	              value="#{delegation.delegationRequest.receivedDate}"
	              rendered="#{delegation.delegationRequest.applicationTypeCD!=''}"
	              readOnly="#{! delegation.editable}">
	            </af:selectInputDate>
	
	            <f:facet name="footer">
	              <af:panelButtonBar>
	                <af:commandButton text="Save"
	                  rendered="#{delegation.editable}"
	                  actionListener="#{delegation.save}" />
	                <af:commandButton text="Submit"
	                  rendered="#{!delegation.editable && delegation.delegationRequest.applicationTypeCD != '' && delegation.delegationRequest.submittedDate==null && delegation.delegationRequest.applicationId>0}"
	                  actionListener="#{delegation.submit}" />
	                <af:commandButton text="Delete"
	                  rendered="#{!delegation.editable && !delegation.delegationRequest.newRecord && delegation.delegationRequest.submittedDate==null}"
	                  actionListener="#{delegation.deleteRequest}" />
	                <af:commandButton text="Edit" disabled="false"
	                  rendered="#{(!delegation.editable && delegation.delegationRequest.submittedDate==null) || (!delegation.editable && delegation.admin)}"
	                  actionListener="#{delegation.startEditRequest}" />
	                <af:commandButton text="Cancel" immediate="true"
	                  rendered="#{delegation.editable && !delegation.delegationRequest.newRecord}"
	                  actionListener="#{delegation.cancelEditRequest}" />
	                <af:commandButton text="Cancel" immediate="true"
	                  rendered="#{delegation.delegationRequest.newRecord}"
	                  actionListener="#{delegation.cancelNewApplication}" />
	              </af:panelButtonBar>
	            </f:facet>
	          </af:panelForm>
	        </af:panelForm>
        </afh:rowLayout>
      </af:page>
    </af:form>
  </af:document>
</f:view>
