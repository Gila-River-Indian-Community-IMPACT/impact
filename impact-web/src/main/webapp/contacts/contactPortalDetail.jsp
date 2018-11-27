<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<h:panelGrid border="1">
	<af:panelBorder>

		<f:facet name="top">
			<f:subview id="contactHeader">
				<jsp:include page="contactHeader.jsp" />
			</f:subview>
		</f:facet>


		<f:facet name="right">
			<af:panelGroup layout="vertical">
				<af:panelBorder>
					<f:facet name="right">
						<h:panelGrid columns="1" border="1" width="950"
							style="margin-left:auto;margin-right:auto;">
							<af:panelForm>
								<af:panelForm>
									<af:panelHeader text="CROMERR Account Information" size="0" />
									<af:panelForm rows="2" maxColumns="2" labelWidth="150"
										width="600">
										<af:inputText label="CROMERR Username:"
											value="#{contactDetail.contact.externalUser.userName}"
											id="portalUsername" columns="14" maximumLength="50"
											readOnly="#{!contactDetail.editable}" />
									</af:panelForm>
									<af:objectSeparator />
									<af:panelForm rows="1" maxColumns="2" labelWidth="150"
										width="600">
										<af:inputText label="CROMERR Name:" 
										    value="#{contactDetail.contact.externalUser.firstName} #{contactDetail.contact.externalUser.lastName}"
											id="externalName" columns="14" maximumLength="14"
											readOnly="true" />
<%-- 										<af:inputText label="CROMERR Email:" value="#{contactDetail.contact.externalUser.email}" --%>
<%-- 											id="externalEmail" columns="14" maximumLength="14" --%>
<%-- 											readOnly="true" /> --%>
									</af:panelForm>
								</af:panelForm>

								<af:showDetailHeader text="Company Permissions" disclosed="true">
									<af:panelForm>
										<af:table bandingInterval="1" banding="row"
											value="#{contactDetail.contactRolesWrapper}"
											binding="#{contactDetail.contactRolesWrapper.table}"
											var="contactRole" id="ExternalCompanyTab" width="98%">

											<af:column sortProperty="c01" sortable="true"
												formatType="text" headerText="Authorize">
												<af:selectBooleanCheckbox
													value="#{contactRole.active}"
													disabled="#{contactRole.company eq null || !contactDetail.editable}" />
											</af:column>
											<af:column sortProperty="c02" sortable="true"
												formatType="text" headerText="Company ID"
												headerNoWrap="true">
												<af:commandLink text="#{contactRole.company.cmpId}"
													action="#{companyProfile.submitProfile}">
													<t:updateActionListener property="#{companyProfile.cmpId}"
														value="#{contactRole.company.cmpId}" />
													<t:updateActionListener
														property="#{menuItem_companyProfile.disabled}"
														value="false" />
												</af:commandLink>
											</af:column>
											<af:column sortProperty="c03" sortable="true" width="200px"
												formatType="text" headerText="Company Name"
												headerNoWrap="true">
												<af:outputText value="#{contactRole.company.name}" />
											</af:column>
											<af:column sortProperty="c04" sortable="true"
												formatType="text" headerText="CROMERR ID" headerNoWrap="true">
												<af:outputText value="#{contactRole.externalRole.organization.organizationId}" />
											</af:column>
											<af:column sortProperty="c05" sortable="true"
												formatType="text" headerText="CROMERR Name"
												headerNoWrap="true" width="200px">
												<af:outputText value="#{contactRole.externalRole.organization.organizationName}" />
											</af:column>
                                            <af:column sortProperty="cA" sortable="true" headerNoWrap="true"
                                                formatType="text" headerText="CROMERR Role">
                                                <af:outputText value="#{contactRole.externalRole.roleName}" />
                                            </af:column>
                                            <af:column sortProperty="c09" sortable="true" headerNoWrap="true"
                                                formatType="text" headerText="CROMERR Status">
                                                <af:outputText value="#{contactRole.externalRole.userRoleStatus}" />
                                            </af:column>
											<af:column sortable="false" formatType="text"
												headerText="Facility Permissions" headerNoWrap="true">

												<af:column sortProperty="c06" sortable="true"
													formatType="number" headerText="Excluded Facilities"
													headerNoWrap="true">
													<af:outputText value="#{contactRole.totalExcludedFacilities}" />
												</af:column>

												<af:column sortProperty="c07" sortable="true"
													formatType="number" headerText="Total Facilities"
													headerNoWrap="true">
													<af:outputText value="#{contactRole.company.facilityCount}" />
												</af:column>

												<af:column sortProperty="c08" sortable="false"
													formatType="icon" headerText="Manage Exclusions"
													headerNoWrap="true">
													<af:commandButton useWindow="true" windowHeight="500"
														windowWidth="800"
														returnListener="#{contactDetail.dialogDone}"
														disabled="#{contactRole.company eq null || contactDetail.editable || !contactRole.active}"
														action="#{contactDetail.manageFacilityExclusionList}"
														text="Manage">
														<t:updateActionListener
															property="#{contactDetail.authorizedCmpId}"
															value="#{contactRole.company.cmpId}" />
													</af:commandButton>
												</af:column>

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
								</af:showDetailHeader>


								<af:showDetailHeader text="Facility Exclusion List"
									disclosed="true">
									<af:panelForm>
										<af:table
											value="#{contactDetail.facilityExclusionListWrapper}"
											binding="#{contactDetail.facilityExclusionListWrapper.table}"
											rows="#{contactDetail.pageLimit}" bandingInterval="1"
											banding="row" var="facility" partialTriggers="authCompany"
											width="98%">

											<af:column sortProperty="facilityId" sortable="true"
												noWrap="true" formatType="text" headerText="Facility ID">
												<af:commandLink action="#{facilityProfile.submitProfile}"
													text="#{facility.facilityId}">
													<t:updateActionListener property="#{facilityProfile.fpId}"
														value="#{facility.fpId}" />
													<t:updateActionListener
														property="#{menuItem_facProfile.disabled}" value="false" />
												</af:commandLink>
											</af:column>

											<af:column sortProperty="name" sortable="true"
												formatType="text" headerText="Facility Name">
												<af:outputText value="#{facility.name}" />
											</af:column>

											<af:column sortProperty="companyName" sortable="true"
												formatType="text" headerText="Company Name">
												<af:outputText value="#{facility.companyName}" />
											</af:column>

											<af:column sortProperty="operatingStatusCd" sortable="true"
												formatType="text" headerText="Operating">
												<af:outputText
													value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facility.operatingStatusCd ? '' : facility.operatingStatusCd)]}" />
											</af:column>

											<af:column sortProperty="permitClassCd" sortable="true"
												formatType="text" headerText="Facility Class">
												<af:outputText
													value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
											</af:column>

											<af:column sortProperty="facilityTypeCd" sortable="true"
												formatType="text" headerText="Facility Type">
												<af:outputText
													value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty facility.facilityTypeCd ? '' : facility.facilityTypeCd)]}" />
											</af:column>

											<af:column sortProperty="countyCd" sortable="true"
												formatType="text" headerText="County">
												<af:selectOneChoice value="#{facility.countyCd}"
													readOnly="true">
													<f:selectItems value="#{infraDefs.counties}" />
												</af:selectOneChoice>
											</af:column>

											<af:column sortProperty="c08" sortable="true"
												formatType="text" headerText="Lat/Long">
												<af:goLink text="#{facility.phyAddr.latlong}"
													targetFrame="_new"
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
									</af:panelForm>
								</af:showDetailHeader>

								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="20" />
										<af:commandButton text="Edit"
											disabled="#{contactDetail.portalDetailEditDisabled}"
											rendered="#{!contactDetail.editable}"
											action="#{contactDetail.editPortalDetail}" />
										<af:commandButton text="Save"
											disabled="#{contactDetail.portalDetailEditDisabled}"
											rendered="#{contactDetail.editable}"
											action="#{contactDetail.savePortalDetail}" />
										<af:commandButton text="Cancel"
											rendered="#{contactDetail.editable}"
											action="#{contactDetail.cancelEditPortalDetail}" />
									</af:panelButtonBar>
								</afh:rowLayout>


							</af:panelForm>
						</h:panelGrid>
					</f:facet>
				</af:panelBorder>


			</af:panelGroup>
		</f:facet>

	</af:panelBorder>
</h:panelGrid>
