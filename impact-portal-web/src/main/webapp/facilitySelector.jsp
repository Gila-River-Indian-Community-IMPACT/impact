<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="IMPACT Home">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:inputHidden value="#{submitTask.logUserOff}"
				rendered="#{!facilityProfile.internalApp}" />
			<af:inputHidden value="#{myTasks.fixedTabs}"
				rendered="#{!facilityProfile.internalApp}" />
			<af:page var="foo" value="#{menuModel.model}"
				title="IMPACT Facility Selector">
				<jsp:include flush="true" page="util/header.jsp" />
				<f:subview id="facSelector">
					<h:panelGrid border="1" width="950"
						style="margin-left:auto; margin-right:auto;">

						<af:panelBorder>

							<af:panelHeader text="Account Information" size="0" />
							<af:panelForm rows="2" maxColumns="2" width="650">
								<af:inputText label="Name:" readOnly="True"
									value="#{myTasks.loginName}" />
								<af:inputText label="CROMERR Company Id:" readOnly="True"
									value="#{myTasks.currentRole.externalRole.organization.organizationId}" />
								<af:inputText label="Company Name:" readOnly="true"
									value="#{companyProfile.company.name}"
									rendered="#{companyProfile.company != null}" />
								<af:inputText label="Access:" readOnly="true" value="#{myTasks.currentRole.externalRole.roleName}" />
							</af:panelForm>

							<af:showDetailHeader text="Choose Facility" disclosed="true">
								<af:panelGroup layout="vertical">
									<af:panelForm>
										<af:outputFormatted
											value="To manage a facility, select its Facility ID from the following list of authorized facilities. To return to the facility selector from another page, press the Facility Selector link in the top right corner." />
										<af:objectSpacer width="100%" height="5" />
										<af:table value="#{companyProfile.cmpFacChoiceWrapper}"
											binding="#{companyProfile.cmpFacChoiceWrapper.table}"
											bandingInterval="1" banding="row" var="cmpFac" width="98%"
											rows="200">

											<af:column sortProperty="c01" sortable="true" noWrap="true"
												formatType="text" headerText="Facility ID">
												<af:commandLink action="#{myTasks.goHome}"
													text="#{cmpFac.facilityId}">
													<t:updateActionListener property="#{myTasks.facilityId}"
														value="#{cmpFac.facilityId}" />
													<t:updateActionListener
														property="#{menuItem_facSelector.rendered}" value="true" />
												</af:commandLink>
											</af:column>

											<af:column sortProperty="c02" sortable="true"
												formatType="text" headerText="Facility Name">
												<af:outputText value="#{cmpFac.name}" />
											</af:column>


											<af:column sortProperty="c04" sortable="true"
												formatType="text" headerText="Operating">
												<af:outputText
													value="#{facilityReference.operatingStatusDefs.itemDesc[(empty cmpFac.operatingStatusCd ? '' : cmpFac.operatingStatusCd)]}" />
											</af:column>

											<af:column sortProperty="c05" sortable="true"
												formatType="text" headerText="Facility Class">
												<af:outputText
													value="#{facilityReference.permitClassDefs.itemDesc[(empty cmpFac.permitClassCd ? '' : cmpFac.permitClassCd)]}" />
											</af:column>

											<af:column sortProperty="c06" sortable="true"
												formatType="text" headerText="Facility Type">
												<af:outputText
													value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty cmpFac.facilityTypeCd ? '' : cmpFac.facilityTypeCd)]}" />
											</af:column>

											<af:column sortProperty="c07" sortable="true"
												formatType="text" headerText="County">
												<af:selectOneChoice value="#{cmpFac.countyCd}"
													readOnly="true">
													<f:selectItems value="#{infraDefs.counties}" />
												</af:selectOneChoice>
											</af:column>

											<af:column sortProperty="c08" sortable="true"
												formatType="text" headerText="Lat/Long">
												<af:goLink text="#{cmpFac.phyAddr.latlong}"
													targetFrame="_new"
													rendered="#{not empty cmpFac.googleMapsURL}"
													destination="#{cmpFac.googleMapsURL}"
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
									</af:panelForm>
								</af:panelGroup>
							</af:showDetailHeader>
							<af:showDetailHeader text="Facility Creation Requests" 
								disclosed="#{companyProfile.facilityRequestCount != 0}">
								<af:panelGroup layout="vertical">
									<af:panelForm>
										<af:outputFormatted value="" />
										<af:objectSpacer width="100%" height="5" />
										<af:table value="#{companyProfile.cmpFacRequestWrapper}"
											binding="#{companyProfile.cmpFacRequestWrapper.table}"
											bandingInterval="1" banding="row" var="request"
											width="98%" rows="#{companyProfile.pageLimit}">

											<af:column sortProperty="reqId" sortable="true" noWrap="true"
												formatType="text" headerText="Request ID">
												<af:outputText value="#{request.reqId}" />
											</af:column>

											<af:column sortProperty="name" sortable="true"
												formatType="text" headerText="Facility Name">
												<af:outputText value="#{request.name}" />
											</af:column>

											<af:column sortProperty="truncatedMemo" sortable="true"
												formatType="text" headerText="Memo">
												<af:outputText value="#{request.truncatedMemo}" />
											</af:column>

											<af:column formatType="text" headerText="Requester">
												<af:column sortProperty="lastNm" sortable="true"
													formatType="text" headerText="Last Name">
													<af:outputText value="#{request.lastNm}" />
												</af:column>

												<af:column sortProperty="firstNm" sortable="true"
													formatType="text" headerText="First Name">
													<af:outputText value="#{request.firstNm}" />
												</af:column>

												<af:column sortProperty="externalUsername" sortable="true"
													formatType="text" headerText="CROMERR Username">
													<af:outputText value="#{request.externalUsername}" />
												</af:column>
											</af:column>

											<af:column sortProperty="operatingStatusCd" sortable="true"
												formatType="text" headerText="Operating">
												<af:outputText
													value="#{facilityReference.operatingStatusDefs.itemDesc[(empty request.operatingStatusCd ? '' : request.operatingStatusCd)]}" />
											</af:column>

											<af:column sortProperty="facilityTypeDsc" sortable="true"
												formatType="text" headerText="Facility Type">
												<af:outputText
													value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty request.facilityTypeCd ? '' : request.facilityTypeCd)]}" />
											</af:column>

											<af:column sortProperty="countyNm" sortable="true"
												formatType="text" headerText="County">
												<af:selectOneChoice value="#{request.countyCd}"
													readOnly="true">
													<f:selectItems value="#{infraDefs.counties}" />
												</af:selectOneChoice>
											</af:column>

											<af:column sortable="true" sortProperty="submitDate"
												formatType="icon" headerText="Date Submitted">
												<af:selectInputDate readOnly="true"
													value="#{request.submitDate}" />
											</af:column>

											<af:column sortProperty="requestStatusCd" sortable="true"
												formatType="text" headerText="Request State">
												<af:outputText
													value="#{facilityReference.facilityRequestStatusDefs.itemDesc[(empty request.requestStatusCd ? '' : request.requestStatusCd)]}" />
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
									</af:panelForm>
								</af:panelGroup>
							</af:showDetailHeader>
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Request creation of a new facility"
										id="NewFacilityRequestButton"
										action="#{newFacilityRequest.createNewFacilityRequest}"
										useWindow="true"
										windowWidth="2000"
										windowHeight="800"/>
								</af:panelButtonBar>
							</afh:rowLayout>
							
							<af:objectSpacer height="20" />
							
							<afh:rowLayout halign="center" rendered="false">
								<af:panelButtonBar>
									<af:commandButton text="Show Offset Tracking Information" 
										useWindow="true" windowWidth="1000"	windowHeight="800"
										disabled="#{!companyProfile.offsetTrackingInfoAvailable}"
										shortDesc="#{companyProfile.offsetTrackingInfoAvailable
														? 'Show Offset Tracking Information'
														: 'Offset Tracking information is either not applicable or is not available'}"
										action="#{companyProfile.displayOffsetTrackingSummary}"/>
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelBorder>
					</h:panelGrid>
				</f:subview>
			</af:page>
		</af:form>
	</af:document>
</f:view>
