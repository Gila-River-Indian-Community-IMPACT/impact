<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Monitor Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}" id="monitorGroup"
				title="Monitor Detail">
				<%@ include file="header.jsp"%>
				<af:inputHidden value="#{monitorDetail.popupRedirect}" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<f:subview id="monitorTop">
								<jsp:include page="monitorTop.jsp" />
						</f:subview>
						<af:panelBorder>
							<af:panelGroup layout="vertical"
								partialTriggers="status type parameter parameterMet">
								<af:panelHeader text="Monitor Information" size="0" />


									<af:panelForm rows="6" maxColumns="2" labelWidth="220px" width="98%">
										<af:selectOneChoice label="Monitor Type:"
											readOnly="#{!monitorDetail.editable}"
											value="#{monitorDetail.monitor.type}"
											unselectedLabel=" "
											valueChangeListener="#{monitorDetail.monitorTypeChanged}"
											id="type"
											autoSubmit="true"
											showRequired="true">
											<f:selectItems value="#{monitorDetail.typeDefs.items[0]}" />
										</af:selectOneChoice>
										<af:inputText label="Name:"
											value="#{monitorDetail.monitor.name}"
											id="name" columns="60" maximumLength="60"
											showRequired="true"
											readOnly="#{!monitorDetail.editable}"
											/>
										<af:selectOneChoice label="Parameter:"
											rendered="#{monitorDetail.monitor.typeAmbient}"
											readOnly="#{!monitorDetail.editable}"
											value="#{monitorDetail.monitor.parameter}"
											unselectedLabel=" "
											id="parameter"
											autoSubmit="true"
											showRequired="#{monitorDetail.monitor.typeAmbient}">
											<f:selectItems value="#{monitorDetail.paramTypeDefs.items[0]}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="Parameter:"
											rendered="#{monitorDetail.monitor.typeMeteorological}"
											readOnly="#{!monitorDetail.editable}"
											value="#{monitorDetail.monitor.parameterMet}"
											unselectedLabel=" "
											id="parameterMet"
											autoSubmit="true"
											showRequired="#{monitorDetail.monitor.typeMeteorological}">
											<f:selectItems value="#{monitorDetail.metParamTypeDefs.items[0]}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="Status:"
											readOnly="#{!monitorDetail.editable}"
											value="#{monitorDetail.monitor.status}"
											id="status"
											autoSubmit="true"
											showRequired="true">
											<f:selectItems value="#{monitorDetail.statusDefs.items[0]}" />
										</af:selectOneChoice>
										<af:selectInputDate label="Start Date:"
											id="startDate"
											readOnly="#{!monitorDetail.editable}"
											showRequired="true"
											value="#{monitorDetail.monitor.startDate}">
											<af:validateDateTimeRange minimum="1900-01-01"
												maximum="#{monitorDetail.maxDate}" />
										</af:selectInputDate>
										<af:selectInputDate label="End Date:"
											id="endDate"
											rendered="#{monitorDetail.monitor.status ne monitorDetail.activeStatusCd}"
											readOnly="#{!monitorDetail.editable}"
											showRequired="#{monitorDetail.monitor.status ne monitorDetail.activeStatusCd}"
											value="#{monitorDetail.monitor.endDate}">
											<af:validateDateTimeRange minimum="1900-01-01"
												maximum="#{monitorDetail.maxDate}" />
										</af:selectInputDate>
										<af:inputText label="Parameter Code:"
											value="#{monitorDetail.monitor.parameterCode}"
											id="parameterCd" columns="5" maximumLength="5"
											showRequired="false"
											readOnly="#{!monitorDetail.editable}"
											/>
										<af:inputText label="Parameter Occurrence Code:"
											value="#{monitorDetail.monitor.parameterOccurrenceCode}"
											id="parameterOccurrenceCd" columns="2" maximumLength="2"
											showRequired="false"
											readOnly="#{!monitorDetail.editable}"
											/>
										<af:inputText label="Method Code:"
											value="#{monitorDetail.monitor.methodCode}"
											id="methodCd" columns="3" maximumLength="3"
											showRequired="false"
											readOnly="#{!monitorDetail.editable}"
											>
												<af:convertNumber pattern="000" />
										</af:inputText>
										<af:selectOneChoice label="Duration Code:"
											readOnly="#{!monitorDetail.editable}"
											value="#{monitorDetail.monitor.durationCode}"
											id="durationCd"
											showRequired="false">
											<f:selectItems value="#{monitorDetail.durationCdDefs.items[0]}" />
										</af:selectOneChoice>
										<af:inputText label="Unit Code:"
											value="#{monitorDetail.monitor.unitCode}"
											id="unitCd" columns="3" maximumLength="3"
											showRequired="false"
											readOnly="#{!monitorDetail.editable}"
											>
												<af:convertNumber pattern="000" />
											</af:inputText>
										<af:selectOneChoice label="Collection Frequency Code:"
											readOnly="#{!monitorDetail.editable}"
											value="#{monitorDetail.monitor.frequencyCode}"
											id="frequencyCd"
											showRequired="false">
											<f:selectItems value="#{monitorDetail.collFreqCdDefs.items[0]}" />
										</af:selectOneChoice>
									</af:panelForm>
								<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
						          <af:inputText id="comments" label="Comments: " rows="4"
								        showRequired="false"
							            value="#{monitorDetail.monitor.comments}"
										readOnly="#{!monitorDetail.editable}"
							            columns="80" maximumLength="255" />
									
								</af:panelForm>
					
								<af:showDetailHeader text="Notes" disclosed="true" 
									id="monitorNotes" 
									rendered="#{monitorDetail.internalApp && !monitorDetail.monitorReportUploadUser}">
									<jsp:include flush="true" page="monitorNotesTable.jsp" />
								</af:showDetailHeader>

								<af:objectSpacer height="25" />

								<afh:rowLayout halign="center"
									rendered="#{monitorDetail.monitorId != null}">
									<af:panelButtonBar>
										<af:commandButton text="Edit"
											action="#{monitorDetail.editMonitor}"
											rendered="#{! monitorDetail.editable && !monitorDetail.readOnlyUser 
															&& !monitorSiteDetail.monitorReportUploadUser && monitorDetail.internalApp}" />
										<af:commandButton id="deleteBtn" text="Delete"
											rendered="#{!monitorDetail.editable && monitorDetail.stars2Admin && monitorDetail.internalApp}"
											action="dialog:deleteMonitor" useWindow="true"
											windowWidth="600" windowHeight="300" />
										<af:commandButton text="Save"
											action="#{monitorDetail.saveEditMonitor}"
											rendered="#{monitorDetail.editable && monitorDetail.internalApp}" />
										<af:commandButton text="Cancel"
											action="#{monitorDetail.cancelEdit}"
											rendered="#{monitorDetail.editable && monitorDetail.internalApp}" />
									</af:panelButtonBar>
								</afh:rowLayout>
								<afh:rowLayout halign="center"
									rendered="#{monitorDetail.monitorId != null}">
									<af:panelButtonBar>
										<af:commandButton text="Show Associated Monitor Site"
											action="#{monitorDetail.showAssociatedSite}"
											rendered="#{! monitorDetail.editable}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelGroup>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>






			</af:page>
		</af:form>
<f:verbatim><%@ include
		file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
<f:verbatim><%@ include file="../scripts/wording-filter.js"%></f:verbatim>
<f:verbatim><%@ include
		file="../scripts/FacilityType-Option.js"%></f:verbatim>
<f:verbatim><%@ include
		file="../scripts/facility-detail.js"%></f:verbatim>
	</af:document>
</f:view>
