<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Add Monitor Site">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				id="createMonitorSite" title="Add Monitor Site">
				<f:facet name="messages">
				  <af:messages />
				</f:facet>
				<afh:rowLayout halign="center">
					<h:panelGrid border="0" width="1000px">
							<af:panelGroup layout="vertical">
									<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
										<af:inputText label="Local Site Name:"
											value="#{createMonitorSite.monitorSite.siteName}"
											id="siteName" columns="60" maximumLength="60"
											showRequired="true"
											readOnly="false"/>
										<af:inputText label="Site Objective:"
											value="#{createMonitorSite.monitorSite.siteObjective}"
											id="siteObjective" rows="5" columns="60" maximumLength="250"
											showRequired="true"
											readOnly="false"/>
									</af:panelForm>

									<af:panelForm rows="1" maxColumns="2" labelWidth="220px" width="98%"
										partialTriggers="inAqs">
										<af:selectOneRadio id="inAqs"
											label="In AQS? "
											readOnly="false"
											layout="horizontal"
											showRequired="true"
											autoSubmit="true"
											value="#{createMonitorSite.monitorSite.inAqs}">
											<f:selectItem itemLabel="Yes" itemValue="true" />
											<f:selectItem itemLabel="No" itemValue="false" />
										</af:selectOneRadio>
										<af:inputText label="AQS Site ID:"
											value="#{createMonitorSite.monitorSite.aqsSiteId}"
											id="aqsSiteId" columns="11" maximumLength="11" tip="XX-XXX-XXXX"
											showRequired="#{createMonitorSite.monitorSite.inAqs eq 'true'}"
											readOnly="false"/>
									</af:panelForm>

									<af:panelForm rows="3" maxColumns="2" labelWidth="220px" width="98%"
										partialTriggers="statusCd">
										<af:selectOneChoice label="Site Status:"
											readOnly="false"
											value="#{createMonitorSite.monitorSite.status}"
											id="statusCd"
											autoSubmit="true"
											showRequired="true">
											<f:selectItems value="#{monitorDetail.statusDefs.items[0]}" />
										</af:selectOneChoice>
									<af:selectInputDate label="Start Date:"
										id="startDate"
										readOnly="false"
										showRequired="true"
										value="#{createMonitorSite.monitorSite.startDate}">
										<af:validateDateTimeRange minimum="1900-01-01"
											maximum="#{monitorSiteDetail.maxDate}" />
									</af:selectInputDate>
									<af:selectInputDate label="End Date:"
										id="endDate"
										rendered="#{createMonitorSite.monitorSite.status ne monitorDetail.activeStatusCd}"
										readOnly="false"
										showRequired="#{createMonitorSite.monitorSite.status ne monitorDetail.activeStatusCd}"
										value="#{createMonitorSite.monitorSite.endDate}">
										<af:validateDateTimeRange minimum="1900-01-01"
											maximum="#{monitorSiteDetail.maxDate}" />
									</af:selectInputDate>
									</af:panelForm>

									<af:panelForm rows="2" maxColumns="2" labelWidth="220px" width="98%">
										<af:inputText label="Site Latitude:"
											value="#{createMonitorSite.monitorSite.latitude}"
											id="latitude" columns="12" maximumLength="12"
											showRequired="true" tip="WGS-84"
											shortDesc="The value must be between 41 ~ 45." 
											readOnly="false" autoSubmit="true" />
										<af:inputText label="Site Longitude:"
											value="#{createMonitorSite.monitorSite.longitude}"
											id="longitude" columns="12" maximumLength="12"
											showRequired="true" tip="WGS-84"
											shortDesc="The value must be between -111.06 ~ -104.05." 
											readOnly="false" autoSubmit="true" />
										
										<af:selectOneChoice label="County:"
											readOnly="false"
											value="#{createMonitorSite.monitorSite.county}"
											id="countyCd"
											showRequired="true">
											<f:selectItems value="#{infraDefs.counties}" />
										</af:selectOneChoice>
										
										<af:inputText label="Site Elevation (ft):"
											value="#{createMonitorSite.monitorSite.elevation}"
											id="elevation" columns="5" maximumLength="5"
											readOnly="false"/>

									</af:panelForm>
									<af:panelForm rows="1" maxColumns="1" labelWidth="220px" width="98%">
										<af:inputText label="WYVISNET:"
											value="#{createMonitorSite.monitorSite.wyvisnet}"
											id="wyvisnet" columns="64" maximumLength="128"
											showRequired="false"
											readOnly="false"/>
									</af:panelForm>								


								<af:objectSpacer height="25" />

								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Add"
											action="#{createMonitorSite.create}" />
										<af:commandButton text="Reset"
											action="#{createMonitorSite.reset}" />
										<af:commandButton text="Cancel"
											action="#{createMonitorSite.cancel}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelGroup>
					</h:panelGrid>
				</afh:rowLayout>
				
			</af:page>
		</af:form>
	</af:document>
		<f:verbatim><%@ include
			file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
	<f:verbatim><%@ include
			file="../scripts/wording-filter.js"%></f:verbatim>
	<f:verbatim><%@ include
			file="../scripts/facility-detail-location.js"%></f:verbatim>
</f:view>
