<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Confirm Clone Facility">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
         <af:messages />
      <af:page>
        <afh:rowLayout halign="center">
          <h:panelGrid>
            <af:panelForm>
              <af:outputFormatted value="The facility is going to be cloned. Only the data related to selected emission units, control equipment, processes and release points will be copied. Would you like to continue?" />
              	<afh:rowLayout halign="center">
	              <af:panelButtonBar>
	                <af:commandButton text="Yes"
	                  actionListener="#{cloneFacility.submitCloneFacility}"
	                  returnListener="#{cloneFacility.returnFirstSelected}"
	                  useWindow="true" windowWidth="600" windowHeight="200">
	                </af:commandButton>
	                <af:commandButton text="No"
	                  actionListener="#{cloneFacility.noConfirm}">      
	                </af:commandButton>
	              </af:panelButtonBar>
	          </afh:rowLayout>    
            </af:panelForm>
          </h:panelGrid>
        </afh:rowLayout>
      </af:page>
    </af:form>
  </af:document>
</f:view>