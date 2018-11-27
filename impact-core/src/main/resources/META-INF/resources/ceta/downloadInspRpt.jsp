<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Inspection Documents">
		<f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim>
		
		<af:form>
			<af:messages />
			<af:panelForm>
				
				<af:objectSpacer width="100%" height="15" />
				<af:outputText value="Inspection Report document generated." />
		        <af:objectSpacer width="100%" height="10" />
		        
		        <af:goLink id="documentLink" targetFrame="_blank" 
					  text="Inspection Report"
		              destination="#{fceDetail.inspectionReportURL}" />
		        
		        <af:objectSpacer width="100%" height="10" />
		        
		        <af:outputText value="Select the link above to download the report to save it." />
		        
		        <af:objectSpacer width="100%" height="10" />
	
		        <f:facet name="footer">
		          <af:panelButtonBar>
		            <af:commandButton text="Close" >
		              <af:returnActionListener />
		            </af:commandButton>
		          </af:panelButtonBar>
		        </f:facet>
			
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
