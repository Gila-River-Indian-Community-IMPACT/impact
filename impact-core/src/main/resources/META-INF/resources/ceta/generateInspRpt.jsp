<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Inspection Report">
		<f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim>
		
		
		<af:form>
			<af:page id="inspRpt" title="Inspection Report">
        	<af:messages />
        		
        		<afh:rowLayout halign="center">
		        	<af:panelGroup>
		        		<h:panelGrid columns="1" border="0" width="99%"
							style="margin-left:auto;margin-right:auto;">
							
							
							<afh:rowLayout halign="left">
								<af:outputFormatted value="Choose the information from this Inspection 
									to include in the Generated Inspection Report." />
							</afh:rowLayout>
							
							<af:objectSpacer width="100%" height="5" />
							
							<afh:rowLayout halign="center">
								<af:table id="inspRptTable" value="#{fceDetail.inspectionReportSectionsWrapper}"
									bandingInterval="1" binding="#{fceDetail.inspectionReportSectionsWrapper.table}"
									banding="row" var="section"
									selectionListener="#{fceDetail.selectedSections}" >
									
									<f:facet name="selection">
										<af:tableSelectMany shortDesc="Select" disabled="false" />
									</f:facet>
									
									<af:column sortProperty="fieldName" sortable="false" formatType="text">
										<f:facet name="header">
											<af:outputText value="Field Name" />
										</f:facet>
										<af:outputText value="#{section}" />
									</af:column>
								</af:table>
							</afh:rowLayout>
							
							<af:objectSpacer width="100%" height="20" />
		        		
		        		</h:panelGrid>
					</af:panelGroup>
				</afh:rowLayout>
				
				<afh:rowLayout halign="center">
					<af:panelForm width="99%">
						<af:panelButtonBar >
							<af:commandButton id="genButn" text="Generate Document"
								returnListener="#{fceDetail.closeInspectionReportGenPopup}"
								action="#{fceDetail.generateInspectionReportDocument}"
								useWindow="true" windowWidth="500" windowHeight="300">
	           	            </af:commandButton>
	            			<af:commandButton text="Cancel" id="cancel"
	            				action="#{fceDetail.cancelInspectionReportGeneration}" >
	            			</af:commandButton>
	            		</af:panelButtonBar>
	            	</af:panelForm>
	            </afh:rowLayout>
			
			</af:page>
		</af:form>
		
		<f:verbatim><script></f:verbatim><h:outputText value="#{fceDetail.selectAllJs}" /><f:verbatim></script></f:verbatim>
		
	</af:document>
</f:view>
