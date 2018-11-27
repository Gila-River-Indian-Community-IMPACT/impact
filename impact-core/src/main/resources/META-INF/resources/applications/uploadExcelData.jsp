<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<f:verbatim>
		<script>
			</f:verbatim><h:outputText value="#{infraDefs.js}" />
			<f:verbatim>
		</script>
	</f:verbatim>

	<af:document title="NSR Application Loading">
		<af:form usesUpload="true">
			
							
					
				<af:panelForm width="800" inlineStyle="margin:auto;"  labelWidth="300">
				<af:showDetailHeader text="NSR Application Loading" disclosed="true">	
					<af:inputFile id="filePath" columns="65"
						label="NSR Application Excel file to Upload:"
						value="#{applicationDetail.fileToUpload}" />
					
					

			
						
				
						<t:div styleClass="buttonContainer" id="buttonContainer">
							<af:panelButtonBar>
								<af:commandButton text="Start to Load" id="migrateButton"									
									action="#{applicationDetail.startMigrateApplicationData}" >
								</af:commandButton>
							</af:panelButtonBar>
						</t:div>
				
				</af:showDetailHeader>
			
			
			
			
			
				</af:panelForm>
		
		</af:form>
		<f:verbatim><%@ include file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include	file="../scripts/wiseview-migration.js"%></f:verbatim>
		
	</af:document>	
	<af:objectSpacer height="300" />
	
	
</f:view>