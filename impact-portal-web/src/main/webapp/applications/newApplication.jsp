<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" title="New Application" >   
    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim>
 	  <af:form usesUpload="true">     
		<af:page>	
        <af:messages />
        <afh:rowLayout halign="center" >
	        <af:panelForm
	          partialTriggers="DocTypeChoice rpcTypeChoice 
	            correctApp amendApp cloneApp">
	          <af:inputText label="Facility ID :"
	            value="#{applicationSearch.newApplicationFacilityID}"
	            maximumLength="20"
	            readOnly="true"/>
	          <af:selectOneChoice id="requestType" label="Request type :"
	            unselectedLabel="Please select"
	            value="#{applicationSearch.newApplicationClass}"
	            readOnly="true">
	            <f:selectItems
	              value="#{applicationReference.applicationClasses}" />
	          </af:selectOneChoice>
	          <af:selectOneChoice id="rpcTypeChoice"
	            immediate="true"
	            label="Modification Type :"
	            unselectedLabel="Please select"
	            rendered="#{applicationSearch.rpcRequest}"
	            value="#{applicationSearch.rpcTypeCd}"
	            autoSubmit="true">
	            <f:selectItems value="#{applicationReference.rpcTypeDefs}"/>
	          </af:selectOneChoice>
	          <af:selectOneChoice id="permitChoice"
	            label="Permit Number :"
	            rendered="#{applicationSearch.permitList != null}"
	            value="#{applicationSearch.permitId}">
	            <f:selectItems
	              value="#{applicationSearch.permitList}" />
	          </af:selectOneChoice>
	          <af:inputText readOnly="true"
	              value="This drop down list contains only permits that have been issued final and have not expired."
	              rendered="#{applicationSearch.permitList != null && applicationSearch.newApplication.applicationTypeCD == 'RPC'}"
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
	            id="correctApp" rendered="#{applicationSearch.correctable}"
	            readOnly="#{applicationSearch.amendedApplication}"
	            value="#{applicationSearch.correctedApplication}"
	            autoSubmit="true" />
	          <af:outputText inlineStyle="font-size: 12px"
	            rendered="#{applicationSearch.correctedApplication && !applicationSearch.amendedApplication}"
	            value="#{applicationSearch.correctedApplicationPopupText}" />
	          <af:selectBooleanCheckbox
	            label="Copy data from existing application :" id="cloneApp"
	            rendered="#{applicationSearch.cloneable && !applicationSearch.correctedApplication}"
	            value="#{applicationSearch.clonedApplication}"
	            autoSubmit="true" />
	          <af:selectOneChoice label="Affected Application Number :"
	            unselectedLabel="Please select"
	            value="#{applicationSearch.previousApplicationNumber}"
	            rendered="#{(applicationSearch.correctable && applicationSearch.correctedApplication) || (applicationSearch.cloneable && applicationSearch.clonedApplication)}">
	            <f:selectItems
	              value="#{applicationSearch.previousApplicationNumbers}" />
	          </af:selectOneChoice>
	
	          <af:inputText label="Reason for Correction :" rows="4"
	            columns="50" maximumLength="500"
	            rendered="#{applicationSearch.correctedApplication && !applicationSearch.amendedApplication}"
	            value="#{applicationSearch.correctedReason}" />
	
	          <f:facet name="footer">
	            <af:panelButtonBar>
	              <af:commandButton text="Create"
	                actionListener="#{applicationSearch.createNewApplication}" 
	                disabled="#{applicationSearch.rpcRequest && applicationSearch.permitList == null}"
	                onclick="return isCreated();return false;" />
	              <af:commandButton text="Cancel" immediate="true"
	                actionListener="#{applicationSearch.cancelNewApplication}" />
	            </af:panelButtonBar>
	          </f:facet>
	        </af:panelForm>
        </afh:rowLayout>
      </af:page>
    </af:form>
    	<f:verbatim>
			<script type="text/javascript">
				var created = false;
				function isCreated() {
					if (created) {
						return false;
					} else {
						created = true;
						return true;
					}
				}
			</script>
		</f:verbatim>
  </af:document>
</f:view>
