<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" title="New Intent to Relocate Application" >   
    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim>
 	  <af:form usesUpload="true">     
		<af:page>	
        	<af:messages />
        	<af:panelForm partialTriggers="requestType" >
        		<af:inputText label="Facility ID :"
            		value="#{myTasks.facility.facilityId}"
            		maximumLength="20" readOnly="true"/>
            	<af:selectOneChoice id="requestType" label="Request type :"
            		unselectedLabel="Please select"
            		value="#{applicationSearch.newPortalApplicationClass}"
            		autoSubmit="true">
            	  	<f:selectItems
              			value="#{applicationReference.intentToRelocateClasses}" />
          		</af:selectOneChoice>
          		
          		<f:facet name="footer">
            		<af:panelButtonBar>
              			<af:commandButton text="Create"
                			actionListener="#{relocation.createNewITR}" />
              			<af:commandButton text="Cancel" immediate="true"
                			actionListener="#{relocation.cancelCreateNewITR}" />
            		</af:panelButtonBar>
          		</f:facet>
        	</af:panelForm>
		</af:page>
      </af:form>
  </af:document>
</f:view>