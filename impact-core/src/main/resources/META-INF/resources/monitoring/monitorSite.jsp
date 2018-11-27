<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Monitor Site Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}" id="monitorSite"
				title="Monitor Site Detail">
				<%@ include file="header.jsp"%>
				<af:inputHidden value="#{monitorSiteSearch.popupRedirect}" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<f:subview id="monitorSiteTop">
								<jsp:include page="monitorSiteTop.jsp" />
						</f:subview>
						<af:panelBorder>
							<af:panelGroup layout="vertical"
								rendered="#{monitorSiteDetail.siteId != null}">
								<af:panelHeader text="Monitor Site Information" size="0" />

									<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
										<af:inputText label="Local Site Name:"
											value="#{monitorSiteDetail.monitorSite.siteName}"
											id="siteName" columns="60" maximumLength="60"
											showRequired="true"
											readOnly="#{!monitorSiteDetail.editable}"/>
										<af:inputText label="Site Objective:"
											value="#{monitorSiteDetail.monitorSite.siteObjective}"
											id="siteObjective" rows="5" columns="60" maximumLength="250"
											showRequired="true"
											readOnly="#{!monitorSiteDetail.editable}"/>
									</af:panelForm>

									<af:panelForm rows="1" maxColumns="2" labelWidth="220px" width="98%"
										partialTriggers="inAqs">
										<af:selectOneRadio id="inAqs"
											label="In AQS? "
											readOnly="#{!monitorSiteDetail.editable}"
											layout="horizontal"
											showRequired="true"
											autoSubmit="true"
											value="#{monitorSiteDetail.monitorSite.inAqs}">
											<f:selectItem itemLabel="Yes" itemValue="true" />
											<f:selectItem itemLabel="No" itemValue="false" />
										</af:selectOneRadio>
										<af:inputText label="AQS Site ID:"
											value="#{monitorSiteDetail.monitorSite.aqsSiteId}"
											id="aqsSiteId" columns="11" maximumLength="11" tip="XX-XXX-XXXX"
											showRequired="#{monitorSiteDetail.monitorSite.inAqs eq 'true'}"
											readOnly="#{!monitorSiteDetail.editable}"/>
									</af:panelForm>

									<af:panelForm rows="3" maxColumns="2" labelWidth="220px" width="98%"
										partialTriggers="statusCd">
										<af:selectOneChoice label="Site Status:"
											readOnly="#{! monitorSiteDetail.editable}"
											value="#{monitorSiteDetail.monitorSite.status}"
											id="statusCd"
											autoSubmit="true"
											showRequired="true">
											<f:selectItems value="#{monitorDetail.statusDefs.items[0]}" />
										</af:selectOneChoice>
									<af:selectInputDate label="Start Date:"
										id="startDate"
										readOnly="#{! monitorSiteDetail.editable}"
										showRequired="true"
										value="#{monitorSiteDetail.monitorSite.startDate}">
										<af:validateDateTimeRange minimum="1900-01-01"
											maximum="#{monitorSiteDetail.maxDate}" />
									</af:selectInputDate>
									<af:selectInputDate label="End Date:"
										id="endDate"
										rendered="#{monitorSiteDetail.monitorSite.status ne monitorDetail.activeStatusCd}"
										readOnly="#{! monitorSiteDetail.editable}"
										showRequired="#{monitorSiteDetail.monitorSite.status ne monitorDetail.activeStatusCd}"
										value="#{monitorSiteDetail.monitorSite.endDate}">
										<af:validateDateTimeRange minimum="1900-01-01"
											maximum="#{monitorSiteDetail.maxDate}" />
									</af:selectInputDate>
									</af:panelForm>
										
									<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
										<af:inputText label="Site Latitude:"
											value="#{monitorSiteDetail.monitorSite.latitude}"
											id="latitude" columns="12" maximumLength="12"
											showRequired="true" tip="WGS-84" autoSubmit="true"
											shortDesc="The value must be between 41 ~ 45." 
											readOnly="#{!monitorSiteDetail.editable}">
										</af:inputText>
										<af:inputText label="Site Longitude:"
											value="#{monitorSiteDetail.monitorSite.longitude}"
											id="longitude" columns="12" maximumLength="12"
											showRequired="true" tip="WGS-84" autoSubmit="true"
											shortDesc="The value must be between -111.06 ~ -104.05." 
											readOnly="#{!monitorSiteDetail.editable}">
										</af:inputText>
										
										<af:selectOneChoice label="County:"
											readOnly="#{! monitorSiteDetail.editable}"
											value="#{monitorSiteDetail.monitorSite.county}"
											id="countyCd"
											showRequired="true">
											<f:selectItems value="#{infraDefs.counties}" />
										</af:selectOneChoice>

										<af:inputText label="Site Elevation (ft):"
											value="#{monitorSiteDetail.monitorSite.elevation}"
											id="elevation" columns="5" maximumLength="5"
											readOnly="#{!monitorSiteDetail.editable}"/>

									</af:panelForm>
									<af:panelForm rows="1" maxColumns="1" labelWidth="220px" width="98%">
										<af:inputText label="WYVISNET:"
											value="#{monitorSiteDetail.monitorSite.wyvisnet}"
											id="wyvisnet" columns="64" maximumLength="128"
											showRequired="false"
											readOnly="#{!monitorSiteDetail.editable}" rendered="#{monitorSiteDetail.editable}"/>
										<af:panelLabelAndMessage for="wyvisnetLink" label="WYVISNET:"
														rendered="#{!monitorSiteDetail.editable}" 
										               tip="Opens in a new browser window.">
											<af:goLink text="#{monitorSiteDetail.monitorSite.wyvisnet}" 
												targetFrame="blank" id="wyvisnetLink"
								              	destination="#{monitorSiteDetail.monitorSite.wyvisnet}"/>
											</af:panelLabelAndMessage>
									</af:panelForm>

								<af:showDetailHeader text="Monitors" disclosed="true" id="siteMonitors">
									<jsp:include flush="true" page="siteMonitorsTable.jsp" />
								</af:showDetailHeader>

								<af:showDetailHeader text="Other Monitors at this AQS Site Location" disclosed="true" 
									rendered="true" id="otherMonitors">
									<jsp:include flush="true" page="otherMonitorsTable.jsp" />
								</af:showDetailHeader>

								<af:showDetailHeader text="Notes" disclosed="true" id="siteNotes" 
									rendered="#{monitorSiteDetail.internalApp && !monitorDetail.monitorReportUploadUser}">
									<jsp:include flush="true" page="monitorSiteNotesTable.jsp" />
								</af:showDetailHeader>



								<af:objectSpacer height="25" />

								<afh:rowLayout halign="center"
									rendered="#{monitorSiteDetail.siteId != null}">
									<af:panelButtonBar>
										<af:commandButton text="Edit"
											action="#{monitorSiteDetail.editSite}"
											rendered="#{! monitorSiteDetail.editable && !monitorSiteDetail.readOnlyUser
															&& !monitorSiteDetail.monitorReportUploadUser && monitorSiteDetail.internalApp}" />
										<af:commandButton id="deleteSiteBtn" text="Delete"
											rendered="#{!monitorSiteDetail.editable && monitorSiteDetail.stars2Admin && monitorSiteDetail.internalApp}"
											disabled="#{monitorSiteDetail.associatedWithMonitors}"
											action="#{monitorSiteDetail.startDelete}" useWindow="true"
											windowWidth="600" windowHeight="300" />
										<af:commandButton text="Save"
											action="#{monitorSiteDetail.saveEditSite}"
											rendered="#{monitorSiteDetail.editable && monitorSiteDetail.internalApp}" />
										<af:commandButton text="Cancel"
											action="#{monitorSiteDetail.cancelEdit}"
											rendered="#{monitorSiteDetail.editable && monitorSiteDetail.internalApp}" />
									</af:panelButtonBar>
								</afh:rowLayout>
								<afh:rowLayout halign="center"
									rendered="#{monitorSiteDetail.siteId != null}">
									<af:panelButtonBar>
										<af:commandButton text="Show Associated Monitor Group"
											action="#{monitorSiteDetail.showAssociatedGroup}"
											rendered="#{! monitorSiteDetail.editable}" />
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
