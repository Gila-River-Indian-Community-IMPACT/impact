<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" title="#{complianceReport.complianceReport.title}" partialTriggers="messages reportType">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
    	<%@ include file="../util/validate.js"%>
      <af:messages id="message" />
      <af:panelForm partialTriggers="reportTypePartial reportType" >
   		
   		<af:panelForm rows="2" maxColumns="2">
	   		<af:selectOneChoice id="reportTypePartial" autoSubmit="true" readOnly="false" 
	   		    rendered="#{!complianceReport.admin && complianceReport.internalApp}" 
	   			label= "Report Type: " 
	   			value="#{complianceReport.reportType}">
	         	<f:selectItems value="#{complianceReport.complianceReportTypesPartialDef.items[(empty '')]}" />
	        </af:selectOneChoice> 
	        
	        <af:selectOneChoice id="reportType" autoSubmit="true" 
	            rendered="#{complianceReport.admin || complianceReport.portalApp}" 
	   			label= "Report Type: " 
	   			value="#{complianceReport.reportType}" >
	         	<f:selectItems value="#{complianceReport.complianceReportTypesDef.items[(empty '')]}" />
	        </af:selectOneChoice> 
	        
	        <af:selectOneChoice id="reportCategory" label= "Category" 
	   					   showRequired="true" 
	   					   value="#{complianceReport.complianceReport.otherCategoryCd}">
	         				 <f:selectItems value="#{complianceReport.reportTypeSubCategories}" />
	        </af:selectOneChoice> 				  
	        
	        <af:commandLink id="help" text="Help me select the Type and Category"
	       		useWindow="true" windowHeight="600" windowWidth="1000" blocking="false"
	       		returnListener="#{complianceReport.dialogDone}"
	       		action="#{complianceReport.displayComplianceReportTypeHelpInfo}"/>
        </af:panelForm>    
		  	
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Create Report"
              actionListener="#{complianceReport.createNewReport}" />
            <af:commandButton text="Cancel" immediate="true"
              actionListener="#{complianceReport.cancelNewReport}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>
