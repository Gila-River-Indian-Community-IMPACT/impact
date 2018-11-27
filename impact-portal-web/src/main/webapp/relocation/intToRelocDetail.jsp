<%@ page  session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

    <f:view>
      <af:document id="body" onmousemove="#{infraDefs.iframeResize}" onload="#{infraDefs.iframeReload}" title="Intent To Relocate Detail">
    	<f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
          <af:page var="foo" value="#{menuModel.model}" title="Intent To Relocate Detail">
			<af:inputHidden value="#{submitTask.popupRedirect}" />
			<af:inputHidden value="#{submitTask.logUserOff}"
				rendered="#{!facilityProfile.internalApp}" />
			<jsp:include flush="true" page="header.jsp" />
			
            <h:panelGrid border="0" width="100%">
              <af:panelBorder>
              
                <f:facet name="top">
				  <f:subview id="relocationHeader">
                    <jsp:include page="relocationHeader.jsp"/>
                  </f:subview>
                </f:facet>
     
                <f:facet name="right">
					<h:panelGrid columns="1" border="0" width="100%">

						<f:subview id="relocDetail">
							<jsp:include page="portalRelocationDetail.jsp"/>
						</f:subview>
						
						<f:subview id="app_attachments">
                    		<jsp:include page="../applications/app_attachments.jsp" />
                  		</f:subview>
                     
                 		<afh:rowLayout halign="center">                    
                    		<af:panelButtonBar>
                      			<af:commandButton text="Save"
		              				rendered="#{relocation.editable}"
		              				action="#{relocation.saveEditRequest}" />
		              			<af:commandButton text="Edit"
		              				rendered="#{!relocation.editable}"
		              				action="#{relocation.startEditRequest}" />	
		            			<af:commandButton text="Submit"
		              				rendered="#{relocation.submitAllowed}"
		              				action="#{relocation.submitRequest}"
		              				useWindow="true" windowWidth="#{submitTask.attestWidth}"
              						windowHeight="#{submitTask.attestHeight}">
              						<t:updateActionListener property="#{submitTask.type}"
                						value="#{submitTask.yesNo}" />
                					<t:updateActionListener property="#{submitTask.task}"
                						value="#{relocation.task}" />
                				</af:commandButton>
                				<%-- hide the download attestation button until it is not supported in IMPACT
					            <af:goButton id="attestationDocButton" text="Download Attestation Document"
					                rendered="#{relocation.submitAllowed}" targetFrame="_blank"
					                destination="#{relocation.attestationDocURL}"/> --%>
		            			<af:commandButton text="Cancel" immediate="true"
		              				rendered="#{relocation.editable}" 
		              				action="#{relocation.cancelEditRequest}" />
                    		</af:panelButtonBar>
                   		</afh:rowLayout> 	
                	</h:panelGrid>
                </f:facet>
                 


              </af:panelBorder>
            </h:panelGrid>
          </af:page>
        </af:form>
      </af:document>
    </f:view>