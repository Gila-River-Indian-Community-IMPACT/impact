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
						<f:subview id="homeMonitorSiteTop">
								<jsp:include page="homeMonitorSiteTop.jsp" />
						</f:subview>
						<af:panelBorder>
							<af:panelGroup layout="vertical"
								rendered="#{homeMonitorSiteDetail.siteId != null}">
								<af:panelHeader text="Monitor Site Information" size="0" />

									<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
										<af:inputText label="Local Site Name:"
											value="#{homeMonitorSiteDetail.monitorSite.siteName}"
											id="siteName" columns="60" maximumLength="60"
											showRequired="true"
											readOnly="#{!homeMonitorSiteDetail.editable}"/>
										<af:inputText label="Site Objective:"
											value="#{homeMonitorSiteDetail.monitorSite.siteObjective}"
											id="siteObjective" rows="5" columns="60" maximumLength="250"
											showRequired="true"
											readOnly="#{!homeMonitorSiteDetail.editable}"/>
									</af:panelForm>

									<af:panelForm rows="1" maxColumns="2" labelWidth="220px" width="98%"
										partialTriggers="inAqs">
										<af:selectOneRadio id="inAqs"
											label="In AQS? "
											readOnly="#{!homeMonitorSiteDetail.editable}"
											layout="horizontal"
											showRequired="true"
											autoSubmit="true"
											value="#{homeMonitorSiteDetail.monitorSite.inAqs}">
											<f:selectItem itemLabel="Yes" itemValue="true" />
											<f:selectItem itemLabel="No" itemValue="false" />
										</af:selectOneRadio>
										<af:inputText label="AQS Site ID:"
											value="#{homeMonitorSiteDetail.monitorSite.aqsSiteId}"
											id="aqsSiteId" columns="11" maximumLength="11" tip="XX-XXX-XXXX"
											showRequired="#{homeMonitorSiteDetail.monitorSite.inAqs eq 'true'}"
											readOnly="#{!homeMonitorSiteDetail.editable}"/>
									</af:panelForm>

									<af:panelForm rows="3" maxColumns="2" labelWidth="220px" width="98%"
										partialTriggers="statusCd">
										<af:selectOneChoice label="Site Status:"
											readOnly="#{! homeMonitorSiteDetail.editable}"
											value="#{homeMonitorSiteDetail.monitorSite.status}"
											id="statusCd"
											autoSubmit="true"
											showRequired="true">
											<f:selectItems value="#{monitorDetail.statusDefs.items[0]}" />
										</af:selectOneChoice>
									<af:selectInputDate label="Start Date:"
										id="startDate"
										readOnly="#{! homeMonitorSiteDetail.editable}"
										showRequired="true"
										value="#{homeMonitorSiteDetail.monitorSite.startDate}">
										<af:validateDateTimeRange minimum="1900-01-01"
											maximum="#{homeMonitorSiteDetail.maxDate}" />
									</af:selectInputDate>
									<af:selectInputDate label="End Date:"
										id="endDate"
										rendered="#{homeMonitorSiteDetail.monitorSite.status ne monitorDetail.activeStatusCd}"
										readOnly="#{! homeMonitorSiteDetail.editable}"
										showRequired="#{homeMonitorSiteDetail.monitorSite.status ne monitorDetail.activeStatusCd}"
										value="#{homeMonitorSiteDetail.monitorSite.endDate}">
										<af:validateDateTimeRange minimum="1900-01-01"
											maximum="#{homeMonitorSiteDetail.maxDate}" />
									</af:selectInputDate>
									</af:panelForm>
										
									<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
										<af:inputText label="Site Latitude:"
											value="#{homeMonitorSiteDetail.monitorSite.latitude}"
											id="latitude" columns="12" maximumLength="12"
											showRequired="true" tip="WGS-84" autoSubmit="true"
											shortDesc="The value must be between 41 ~ 45." 
											readOnly="#{!homeMonitorSiteDetail.editable}">
										</af:inputText>
										<af:inputText label="Site Longitude:"
											value="#{homeMonitorSiteDetail.monitorSite.longitude}"
											id="longitude" columns="12" maximumLength="12"
											showRequired="true" tip="WGS-84" autoSubmit="true"
											shortDesc="The value must be between -111.06 ~ -104.05." 
											readOnly="#{!homeMonitorSiteDetail.editable}">
										</af:inputText>
										
										<af:selectOneChoice label="County:"
											readOnly="#{! homeMonitorSiteDetail.editable}"
											value="#{homeMonitorSiteDetail.monitorSite.county}"
											id="countyCd"
											showRequired="true">
											<f:selectItems value="#{infraDefs.counties}" />
										</af:selectOneChoice>

										<af:inputText label="Site Elevation (ft):"
											value="#{homeMonitorSiteDetail.monitorSite.elevation}"
											id="elevation" columns="5" maximumLength="5"
											readOnly="#{!homeMonitorSiteDetail.editable}"/>

									</af:panelForm>
									<af:panelForm rows="1" maxColumns="1" labelWidth="220px" width="98%">
										<af:inputText label="WYVISNET:"
											value="#{homeMonitorSiteDetail.monitorSite.wyvisnet}"
											id="wyvisnet" columns="64" maximumLength="128"
											showRequired="false"
											readOnly="#{!homeMonitorSiteDetail.editable}" rendered="#{homeMonitorSiteDetail.editable}"/>
										<af:panelLabelAndMessage for="wyvisnetLink" label="WYVISNET:"
														rendered="#{!homeMonitorSiteDetail.editable}" 
										               tip="Opens in a new browser window.">
											<af:goLink text="#{homeMonitorSiteDetail.monitorSite.wyvisnet}" 
												targetFrame="blank" id="wyvisnetLink"
								              	destination="#{homeMonitorSiteDetail.monitorSite.wyvisnet}"/>
											</af:panelLabelAndMessage>
									</af:panelForm>

								<af:showDetailHeader text="Monitors" disclosed="true" id="siteMonitors">
									<jsp:include flush="true" page="homeSiteMonitorsTable.jsp" />
								</af:showDetailHeader>

								<af:showDetailHeader text="Other Monitors at this AQS Site Location" disclosed="true" 
									rendered="true" id="otherMonitors">
									<jsp:include flush="true" page="homeOtherMonitorsTable.jsp" />
								</af:showDetailHeader>

								<af:objectSpacer height="25" />

								<afh:rowLayout halign="center"
									rendered="#{homeMonitorSiteDetail.siteId != null}">
									<af:panelButtonBar>
										<af:commandButton text="Show Associated Monitor Group"
											action="#{homeMonitorSiteDetail.showAssociatedGroup}"
											rendered="#{! homeMonitorSiteDetail.editable}" />
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
