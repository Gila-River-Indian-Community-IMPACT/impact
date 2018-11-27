<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
  
<f:view>
  <af:document title="Edit Definition">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages/>
                      
      <af:panelForm>
		<af:inputText label="Code" 
		    value="#{workflowDefCatalog.data.code}" 
		    rendered="true" 
		    readOnly="#{!workflowDefCatalog.newRecord}" 
		    columns="4" maximumLength="#{workflowDefCatalog.data.codeColumnSize}" />
	  </af:panelForm> 
	  <af:panelForm>
		<af:inputText label="Description" 
			required="true"
		    value="#{workflowDefCatalog.data.description}" 
		    rendered="true" 
		    columns="40" maximumLength="#{workflowDefCatalog.data.descriptionColumnSize}" />
	  </af:panelForm>
	  <af:panelForm>
		<af:selectBooleanCheckbox
			 rendered="#{workflowDefCatalog.deprecatable}"
             value="#{workflowDefCatalog.data.deprecated}"
             label="Inactive" />  
	    </af:panelForm>
      <af:panelForm>         
        <af:objectSpacer height="20" /> 
          <afh:rowLayout halign="center">           
           <af:panelButtonBar>
            <af:commandButton text="Save"
                actionListener="#{workflowDefCatalog.save}"/>
            <af:commandButton text="Cancel"
                immediate="true"
                actionListener="#{workflowDefCatalog.cancel}"/>
           </af:panelButtonBar>
          </afh:rowLayout>
        </af:panelForm>
          
    </af:form>
  </af:document>
</f:view>