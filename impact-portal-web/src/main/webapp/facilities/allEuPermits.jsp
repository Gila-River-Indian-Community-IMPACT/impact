<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Stars2 Emission Unit Permit History">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages/>
                      
      <af:panelForm>
		<af:inputText label="Facility ID:" value="#{facilityProfile.facility.facilityId}" 
			columns="10" maximumLength="10" readOnly="true" />
        <af:inputText label="Facility Name:" value="#{facilityProfile.facility.name}" 
			columns="60" maximumLength="60" readOnly="true" />
		<af:inputText label="AQD Emissions Unit ID:" value="#{facilityProfile.emissionUnit.epaEmuId}" 
			columns="9" maximumLength="9" readOnly="true" />
	  </af:panelForm>
	  <afh:rowLayout halign="center">
			<h:panelGrid border="1" width="100%" >            
				<jsp:include flush="true" page="permitTable.jsp"/>
			</h:panelGrid>
	  </afh:rowLayout> 
		<af:panelForm>					
          <f:facet name="footer">
          	<afh:rowLayout halign="center">
            	<af:panelButtonBar>
              		<af:commandButton text="close" action="#{facilityProfile.closeDialog}"/>
            	</af:panelButtonBar>
            </afh:rowLayout>
          </f:facet>         
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>