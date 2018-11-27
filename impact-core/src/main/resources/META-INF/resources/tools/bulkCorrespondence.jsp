<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Facilities - Bulk Operations">
    	<f:verbatim>
      		<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    	</f:verbatim>
    	<af:form usesUpload="true">
      	<f:facet name="messages">
        	<af:messages />
      	</f:facet>
      	
		<afh:rowLayout halign="center">
        	<h:panelGrid border="1" columns="1" width="600">
				<af:panelBorder partialTriggers="correspondenceDirection">
            		<af:panelHeader text="New Correspondence" size="0" />
            		
            		<af:panelForm>
			  			<af:selectOneRadio label="Direction:"
							id="correspondenceDirection" showRequired="true"
							autoSubmit="true"
							value="#{bulkOperationsCatalog.bulkOperation.correspondence.directionCd}">
							<f:selectItems
								value="#{correspondenceSearch.correspondenceDirectionDef}" />
					  </af:selectOneRadio>
					</af:panelForm>
					
				   <af:objectSpacer width="100%" height="15" />
				   
					<af:panelForm rows="2" labelWidth="160px" width="98%">
						<af:selectOneChoice label="Type:"
							id="correspondenceType" showRequired="true"
							value="#{bulkOperationsCatalog.bulkOperation.correspondence.correspondenceTypeCode}">
							<f:selectItems
								value="#{correspondenceSearch.correspondenceDef}" />
						</af:selectOneChoice>
			 			<af:selectInputDate label="Receipt Date:"
							id="correspondenceReceiptDate"
							showRequired="#{bulkOperationsCatalog.bulkOperation.correspondence.incoming}"
							value="#{bulkOperationsCatalog.bulkOperation.correspondence.receiptDate}">
							<af:validateDateTimeRange minimum="1900-01-01"
								maximum="#{correspondenceDetail.maxDate}" />
				 		</af:selectInputDate>
						<af:selectOneChoice label="Category:"
							id="correspondenceCategory"
							showRequired="#{bulkOperationsCatalog.bulkOperation.correspondence.incoming}"
							value="#{bulkOperationsCatalog.bulkOperation.correspondence.correspondenceCategoryCd}">
							<f:selectItems
								value="#{correspondenceSearch.correspondenceCategoryDef}" />
			 			</af:selectOneChoice>
			 			<af:selectInputDate label="Date Generated:"
							id="correspondenceGeneratedDate"
							showRequired="#{bulkOperationsCatalog.bulkOperation.correspondence.outgoing}"
							value="#{bulkOperationsCatalog.bulkOperation.correspondence.dateGenerated}">
							<af:validateDateTimeRange minimum="1900-01-01"
								maximum="#{correspondenceDetail.maxDate}" />
						</af:selectInputDate>
					</af:panelForm>
					
					<af:showDetailHeader text="Message Details" disclosed="true"
						id="correspondenceMessage">
						<af:panelForm maxColumns="1" labelWidth="150px" width="98%">
							<af:inputText label="To:"
								rows="1" columns="50" maximumLength="150"
								value="#{bulkOperationsCatalog.bulkOperation.correspondence.toPerson}" />
							<af:inputText label="From:"
								rows="1" columns="50" maximumLength="150"
								value="#{bulkOperationsCatalog.bulkOperation.correspondence.fromPerson}" />
							<af:selectOneChoice id="divisionReviewer"
								label="Division Reviewer:"
								value="#{bulkOperationsCatalog.bulkOperation.correspondence.reviewerId}"
								unselectedLabel="None">
								<f:selectItems
									value="#{infraDefs.basicUsersDef.items[(empty correspondenceDetail.correspondence.reviewerId?0:correspondenceDetail.correspondence.reviewerId)]}" />
							</af:selectOneChoice>
							<af:inputText label="Subject:"
								rows="1" columns="100" maximumLength="150"
								value="#{bulkOperationsCatalog.bulkOperation.correspondence.regarding}" />
							<af:inputText label="Additional Info:" id="additionalInfo"
								rows="10" columns="150"  
								value="#{bulkOperationsCatalog.bulkOperation.correspondence.additionalInfo}" />
						</af:panelForm>
					</af:showDetailHeader>
					
					<af:objectSpacer width="100%" height="15"/>
					
					<af:showDetailHeader text="Attachment" disclosed="true">
						<af:panelForm rows="1" labelWidth="150px" width="98%">
							<af:inputFile id="attachment"
								rendered="#{bulkOperationsCatalog.bulkOperation.uploadedFileInfo == null}"
								label="File to upload:"
								value="#{bulkOperationsCatalog.bulkOperation.fileToUpload}"/>
							<af:inputText id="attachmentFileName"
								label="Uploaded File:"
								rendered="#{bulkOperationsCatalog.bulkOperation.uploadedFileInfo != null}"
								value="#{bulkOperationsCatalog.bulkOperation.uploadedFileInfo.fileName}"
								readOnly="true" />
							<af:commandButton id="deleteBtn"
								text="Delete Uploaded File"
								rendered="#{bulkOperationsCatalog.bulkOperation.uploadedFileInfo != null}"
								action="#{bulkOperationsCatalog.bulkOperation.deleteUploadedFile}"/>
						</af:panelForm>		
						<af:panelForm rows="2" labelWidth="150px" width="98%">		
							<af:selectOneChoice id="attachmentTypeCd"
								label="Attachment Type :"
								unselectedLabel=" "
								value="#{bulkOperationsCatalog.bulkOperation.attachmentTypeCd}">
								<f:selectItems
								  value="#{bulkOperationsCatalog.bulkOperation.attachmentTypesDef.items[(empty bulkOperationsCatalog.bulkOperation.attachmentTypeCd ? '': bulkOperationsCatalog.bulkOperation.attachmentTypeCd)]}" />
					        </af:selectOneChoice>
							<af:inputText id="attachmentDescription"
								label="Description:" maximumLength="500"
								value="#{bulkOperationsCatalog.bulkOperation.attachmentDescription}"/>		
						</af:panelForm>	
					</af:showDetailHeader>	
					
					<af:objectSpacer width="100%" height="15"/>
					
					<af:showDetailHeader text="Follow-up Information"
						disclosed="true" id="correspondenceFollowUp"
						partialTriggers="followUpAction">
						<af:panelForm rows="1" maxColumns="2" labelWidth="150px"
							width="98%">
							<af:selectBooleanCheckbox label="Follow-up Action?"
								id="followUpAction" autoSubmit="true"
								value="#{bulkOperationsCatalog.bulkOperation.correspondence.followUpAction}" />
							<af:selectInputDate label="Follow-up Action Date:"
								id="followUpActionDate" showRequired="true"
								rendered="#{bulkOperationsCatalog.bulkOperation.correspondence.followUpAction}"
								value="#{bulkOperationsCatalog.bulkOperation.correspondence.followUpActionDate}">
								<af:validateDateTimeRange
									minimum="#{correspondenceDetail.minFollowUpDate}"/>
							</af:selectInputDate>
						</af:panelForm>
						<af:panelForm rows="1" maxColumns="2" labelWidth="150px"
							width="98%">
							<af:inputText label="Follow-up Action Description:"
								id="followUpActionDescription" showRequired="true"
								rows="10" columns="100" maximumLength="1000"
								rendered="#{bulkOperationsCatalog.bulkOperation.correspondence.followUpAction}"
								value="#{bulkOperationsCatalog.bulkOperation.correspondence.followUpActionDescription}" />
						</af:panelForm>
					</af:showDetailHeader>
								
					<af:objectSpacer width="100%" height="15" />
					
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Apply"
								actionListener="#{bulkOperationsCatalog.applyFinalAction2}" />
							<af:commandButton text="Cancel" immediate="true"
								actionListener="#{bulkOperationsCatalog.bulkOperationCancel}" />
						</af:panelButtonBar>
              		</afh:rowLayout>
				</af:panelBorder>
			</h:panelGrid>
		</afh:rowLayout>

		<af:objectSpacer width="100%" height="15" />
		
		<afh:rowLayout halign="center">
			<h:panelGrid border="1"
				rendered="#{bulkOperationsCatalog.hasFacilitySyncSearchResults}">
	        	<af:panelBorder>
	          		<af:showDetailHeader text="Selected Facility List"
	            		disclosed="true">
	            			<af:table
								value="#{bulkOperationsCatalog.selectedFacilities}"
				              	rows="#{bulkOperationsCatalog.pageLimit}"
				              	bandingInterval="1" banding="row" var="facility">
								<af:column sortProperty="#{facility.facilityId}"
					                sortable="true" formatType="text"
					                headerText="Facility ID">
					                <af:outputText value="#{facility.facilityId}" />
								</af:column>
								<af:column sortProperty="#{facility.name}" sortable="true"
					                formatType="text" headerText="Facility Name">
					                <af:outputText value="#{facility.name}" />
								</af:column>
	              				<af:column sortProperty="#{facility.name}" sortable="true" noWrap="true"
									formatType="text" headerText="Company ID">
									<af:outputText value="#{facility.cmpId}"/>
								</af:column>
								<af:column sortProperty="#{facility.companyName}" sortable="true"
									formatType="text" headerText="Company Name">
									<af:outputText value="#{facility.companyName}" />
								</af:column>
								<af:column sortProperty="#{facility.doLaaCd}" sortable="true"
									formatType="text" headerText="District">
									<af:selectOneChoice value="#{facility.doLaaCd}" readOnly="true">
										<f:selectItems value="#{infraDefs.districts}"/>
									</af:selectOneChoice>	
								</af:column>	
								<af:column sortProperty="#{facility.operatingStatusCd}" sortable="true"
									formatType="text" headerText="Operating">
									<af:outputText
										value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facility.operatingStatusCd ? '' : facility.operatingStatusCd)]}" />
								</af:column>
			
								<af:column sortProperty="#{facility.permitClassCd}" sortable="true"
									formatType="text" headerText="Facility Class">
									<af:outputText
										value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
								</af:column>
								<af:column sortProperty="#{facility.facilityTypeCd}" sortable="true"
									formatType="text" headerText="Facility Type">
									<af:outputText
										value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty facility.facilityTypeCd ? '' : facility.facilityTypeCd)]}" />
								</af:column>
								<af:column sortProperty="#{facility.countyCd}" sortable="true"
									formatType="text" headerText="County">
									<af:selectOneChoice value="#{facility.countyCd}" readOnly="true">
										<f:selectItems value="#{infraDefs.counties}" />
									</af:selectOneChoice>
								</af:column>
								<af:column sortProperty="#{facility.phyAddr.latlong}" sortable="true" formatType="text"
									headerText="Lat/Long">
									<af:goLink text="#{facility.phyAddr.latlong}" targetFrame="_new"
										rendered="#{not empty facility.googleMapsURL}"
										destination="#{facility.googleMapsURL}"
										shortDesc="Clicking this will open Google Maps in a separate tab or window." />
								</af:column>
	              				
								<f:facet name="footer">
					                <afh:rowLayout halign="center">
					                  	<af:panelButtonBar>
					                    	<af:commandButton
							                      actionListener="#{tableExporter.printTable}"
							                      onclick="#{tableExporter.onClickScript}"
							                      text="Printable view" />
					                    	<af:commandButton
							                      actionListener="#{tableExporter.excelTable}"
							                      onclick="#{tableExporter.onClickScript}"
							                      text="Export to excel" />
					                  	</af:panelButtonBar>
					                </afh:rowLayout>
								</f:facet>
			      			</af:table>
	            		</af:showDetailHeader>
	            	</af:panelBorder>
	            </h:panelGrid>
            </afh:rowLayout>
		</af:form>
	</af:document>
</f:view>



