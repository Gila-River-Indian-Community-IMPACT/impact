<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Purge Facilities">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Purge Facilities" >
				<%@ include file="../util/header.jsp"%>
				<af:objectSpacer height="10" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">
					<af:panelBorder>
					<af:showDetailHeader disclosed="true"
						text="Facility List">
						<af:objectSpacer height="10" width="300" />
						
						<af:table id="facilityPurgeTable" 
							value="#{bulkOperationsCatalog.fpurgeSearchResultWrapper}" binding="#{bulkOperationsCatalog.fpurgeSearchResultWrapper.table}"
							bandingInterval="1" banding="row" var="facility" rows="#{bulkOperationsCatalog.pageLimit}">
					  		<f:facet name="header">
								<afh:rowLayout halign="left">
									<af:panelButtonBar>
										<af:commandButton action="#{bulkOperationsCatalog.selectAllPurgeCandidates}"
											text="Select All" />
									</af:panelButtonBar>
									<af:objectSpacer height="1" width="6" />
									<af:panelButtonBar>
										<af:commandButton action="#{bulkOperationsCatalog.selectNonePurgeCandidates}"
											text="Select None" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</f:facet>
		
							<af:column id="selection" headerText="Select" 
								sortable="true" sortProperty="selected" formatType="icon" >
								<af:selectBooleanCheckbox label="Selected"
									value="#{facility.selected}" />
							</af:column>
	
							<af:column id="facilityId" sortProperty="facilityId" sortable="true" noWrap="true"
								formatType="text" headerText="Facility ID">
								<af:commandLink action="#{facilityProfile.submitProfile}"
									text="#{facility.facilityId}">
									<t:updateActionListener property="#{facilityProfile.fpId}"
										value="#{facility.fpId}" />
									<t:updateActionListener
										property="#{menuItem_facProfile.disabled}" value="false" />
								</af:commandLink>
							</af:column>
		
							<af:column id="facilityName" sortProperty="facilityName" sortable="true" formatType="text"
								headerText="Facility Name">
								<af:outputText value="#{facility.facilityName}" />
							</af:column>
		
							<af:column id="companyId" sortProperty="cmpId" sortable="true" noWrap="true"
								formatType="text" headerText="Company ID">
								<af:commandLink action="#{companyProfile.submitProfile}"
									text="#{facility.cmpId}">
									<t:updateActionListener property="#{companyProfile.cmpId}"
										value="#{facility.cmpId}" />
									<t:updateActionListener
										property="#{menuItem_companyProfile.disabled}" value="false" />
								</af:commandLink>

							</af:column>
		
							<af:column id="companyName" sortProperty="companyName" sortable="true" noWrap="true"
								formatType="text" headerText="Company Name">
								<af:outputText value="#{facility.companyName}" />
							</af:column>
		
							<af:column id="operatingStatus" sortProperty="operatingStatusCd" sortable="true" formatType="text"
								headerText="Operating Status">
								<af:outputText
									value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facility.operatingStatusCd ? '' : facility.operatingStatusCd)]}" />
							</af:column>
							
							<af:column id="shutdownDate" sortProperty="shutdownDate" sortable="true" noWrap="true"
								formatType="text" headerText="Facility Shutdown Date">
								<af:selectInputDate readOnly="True" value="#{facility.shutdownDate}" />
							</af:column>
		
							<af:column id="permitClass" sortProperty="permitClassCd" sortable="true" formatType="text"
								headerText="Facility Class">
								<af:outputText
									value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
							</af:column>
		
							<af:column id="facilityType" sortProperty="facilityTypeCd" sortable="true" formatType="text"
								headerText="Facility Type">
								<af:outputText
									value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty facility.facilityTypeCd ? '' : facility.facilityTypeCd)]}" />
							</af:column>
		
							<af:column id="latlong" sortProperty="latlong" sortable="true" formatType="text"
								headerText="Lat/Long">
								<af:goLink text="#{facility.phyAddr.latlong}" targetFrame="_new"
									rendered="#{not empty facility.googleMapsURL}"
									destination="#{facility.googleMapsURL}"
									shortDesc="Clicking this will open Google Maps in a separate tab or window." />
							</af:column>
		
							<f:facet name="footer">
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton id="purgeFacilitiesButton"
											text="#{bulkOperationsCatalog.bulkOperation.buttonName}"
											useWindow="true" windowWidth="600" windowHeight="300"
					    					action="#{bulkOperationsCatalog.setSelectedPurgeCandidates}" />
										<af:commandButton actionListener="#{tableExporter.printTable}"
											onclick="#{tableExporter.onClickScript}" text="Printable view" />
										<af:commandButton actionListener="#{tableExporter.excelTable}"
											onclick="#{tableExporter.onClickScript}" text="Export to excel" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</f:facet>
						</af:table>
					</af:showDetailHeader>
					</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>			
			</af:page>
		</af:form>
	</af:document>
</f:view>
