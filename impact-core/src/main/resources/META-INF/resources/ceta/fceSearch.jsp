<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Inspection Search">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<f:verbatim><%@ include	file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include file="../scripts/FacilityType-Option.js"%></f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Inspection Search">
				<%@ include file="../util/header.jsp"%>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">
						<af:panelBorder>
							<af:showDetailHeader text="Inspection Search Criteria" disclosed="true">
								<af:panelForm rows="4" maxColumns="10"  partialTriggers="inspectionSearch_FromFFYCompletedYear inspectionSearch_ToFFYCompletedYear inspectionSearch_Completed">
									<af:inputText label="Facility ID"
										value="#{fceSearch.facilityId}" columns="10"
										maximumLength="10" tip="0000000000, 0%, %0%, *0*, 0*"
										id="inspectionSearch_FacilityID" />
									<af:inputText tip="acme%, %acme% *acme*, acme*" columns="25"
										label="Facility Name" value="#{fceSearch.facilityName}"
										valign="top"
										id="inspectionSearch_FacilityName" />
									<af:selectOneChoice label="District" value="#{fceSearch.doLaaCd}" rendered="#{infraDefs.districtVisible}"
										unselectedLabel="" inlineStyle="width:136px"
										id="inspectionSearch_District" >
										<f:selectItems value="#{facilitySearch.doLaas}" />
									</af:selectOneChoice>
									<%-- This is to make the page Layout unchanged with District not rendered --%>
									<af:inputHidden id="inspectionSearch_DistHidden" rendered="#{!infraDefs.districtVisible}"/>
									
									<af:selectOneChoice label="County"
										value="#{fceSearch.countyCd}" unselectedLabel=""
										id="inspectionSearch_County"  >
										<f:selectItems value="#{infraDefs.counties}"/>
									</af:selectOneChoice>
									<af:selectOneChoice label="Facility Class"
										value="#{fceSearch.permitClassCd}" unselectedLabel=""
										id="inspectionSearch_FacilityClass" >
										<f:selectItems
											value="#{facilityReference.permitClassDefs.items[0]}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Facility Type"
										value="#{fceSearch.facilityTypeCd}" unselectedLabel=""
										styleClass="FacilityTypeClass x6"
										id="inspectionSearch_FacilityType" >
										<f:selectItems
											value="#{facilityReference.facilityTypeDefs.items[0]}" />
									</af:selectOneChoice>
									<af:inputText columns="15" label="Inspection ID" tip="INSP000002, INSP00000%, *0*, INSP*, *2"
										value="#{fceSearch.inspId}"
										id="inspectionSearch_InspectionID" />
									<af:selectOneChoice label="Company" value="#{fceSearch.cmpId}"
										unselectedLabel=""
										id="inspectionSearch_Company" >
										<f:selectItems value="#{companySearch.allCompanies}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Staff Assigned" tip=" "
										unselectedLabel=" " value="#{fceSearch.staffAssigned}"
										id="inspectionSearch_StaffAssigned" >
										<f:selectItems value="#{infraDefs.basicUsersDef.allSearchItems}" />
									</af:selectOneChoice>
									<af:selectOneChoice label="Inspector" tip=" "
										unselectedLabel=" " value="#{fceSearch.reviewer}"
										id="inspectionSearch_Inspector" >
										<f:selectItems value="#{infraDefs.basicUsersDef.allSearchItems}" />
									</af:selectOneChoice>
									<af:selectManyListbox id="inspectionReportState" label="State"
										value="#{fceSearch.inspectionReportStateCds}">
										<f:selectItems
											value="#{fceSearch.inspectionReportStatusDefs.items[
												(empty fceSearch.inspectionReportStateCds ? '' : fceSearch.inspectionReportStateCds)]}" />
									</af:selectManyListbox>
									<af:inputText  label=" " value="" readOnly="true" rendered="#{fceSearch.beginFedYearComp != null || fceSearch.endFedYearComp != null}"/>
									<af:panelForm rows="2" maxColumns="2" rendered="#{fceSearch.completed == 'N'}">
										<af:inputText  label=" " value="" readOnly="true" />
										<af:inputText  label=" " value="" readOnly="true" />
									</af:panelForm>
									<af:outputText value=" " />
									<af:panelForm rows="2" maxColumns="1">
										<af:selectOneChoice label="From FFY Scheduled Year" unselectedLabel=""
											value="#{fceSearch.beginFedYearSched}"
											id="inspectionSearch_FromFFYScheduledYear" >
											<mu:convertSigDigNumber pattern="####" />
											<f:selectItems value="#{fceSearch.yearsForFCEsDef.items[(empty fceSearch.beginFedYearSched ? '' : fceSearch.beginFedYearSched)]}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="To FFY Scheduled Year" unselectedLabel=""
											value="#{fceSearch.endFedYearSched}"
											id="inspectionSearch_ToFFYScheduledYear" >
											<mu:convertSigDigNumber pattern="####" />
											<f:selectItems value="#{fceSearch.yearsForFCEsDef.items[(empty fceSearch.endFedYearSched ? '' : fceSearch.endFedYearSched)]}" />
										</af:selectOneChoice>
									</af:panelForm>
								<af:objectSpacer height="25" />
								<af:panelForm rows="2" maxColumns="1" rendered="#{fceSearch.completed != 'N'}">
										<af:selectOneChoice label="From FFY Completed Year" unselectedLabel="" id="inspectionSearch_FromFFYCompletedYear"  autoSubmit="true"
											value="#{fceSearch.beginFedYearComp}">
											<mu:convertSigDigNumber pattern="####" />
											<f:selectItems value="#{fceSearch.yearsForFCEsDef.items[(empty fceSearch.beginFedYearComp ? '' : fceSearch.beginFedYearComp)]}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="To FFY Completed Year" unselectedLabel=""
											value="#{fceSearch.endFedYearComp}" id="inspectionSearch_ToFFYCompletedYear"  autoSubmit="true">
											<mu:convertSigDigNumber pattern="####" />
											<f:selectItems value="#{fceSearch.yearsForFCEsDef.items[(empty fceSearch.endFedYearComp ? '' : fceSearch.endFedYearComp)]}" />
										</af:selectOneChoice>
								</af:panelForm>
							</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:objectSpacer width="100%" height="25" />
										<af:commandButton text="Submit"
											action="#{fceSearch.submitSearch}"
											id="inspectionSearch_SubmitBtn" >
										</af:commandButton>
										<af:commandButton text="Reset" action="#{fceSearch.reset}"
										id="inspectionSearch_ResetBtn" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
				<af:objectSpacer width="100%" height="15" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" rendered="#{fceSearch.hasSearchResults}">
						<af:panelBorder>
							<af:showDetailHeader text="Inspections"
								disclosed="true">
								<af:table id="fceTable" emptyText=" "
									value="#{fceSearch.fceList}" var="fce" bandingInterval="1"
									banding="row">
									<af:column sortable="true" sortProperty="id" formatType="text"
										headerText="Inspection ID">
										<af:commandLink rendered="true" text="#{fce.inspId}"
											action="#{fceDetail.submit}">
											<af:setActionListener from="#{fce.id}"
												to="#{fceDetail.fceId}" />
											<af:setActionListener from="#{fce.facilityId}"
												to="#{fceDetail.facilityId}" />
											<af:setActionListener from="#{true}"
												to="#{fceDetail.existingFceObject}" />
											<af:setActionListener from="#{fce.id}"
												to="#{fceSiteVisits.fceId}" />
										</af:commandLink>
									</af:column>
									<jsp:include flush="true" page="../ceta/firstFceList.jsp" />
									<jsp:include flush="true" page="../ceta/fceList.jsp" />
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

			</af:page>
		</af:form>
	</af:document>
</f:view>
