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
				<af:inputHidden value="#{homeMonitorDetail.popupRedirect}" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<f:subview id="monitorTop">
								<jsp:include page="homeMonitorTop.jsp" />
						</f:subview>
						<af:panelBorder>
							<af:panelGroup layout="vertical"
								partialTriggers="status type parameter parameterMet">
								<af:panelHeader text="Monitor Information" size="0" />


									<af:panelForm rows="6" maxColumns="2" labelWidth="220px" width="98%">
										<af:selectOneChoice label="Monitor Type:"
											readOnly="#{!homeMonitorDetail.editable}"
											value="#{homeMonitorDetail.monitor.type}"
											unselectedLabel=" "
											valueChangeListener="#{homeMonitorDetail.monitorTypeChanged}"
											id="type"
											autoSubmit="true"
											showRequired="true">
											<f:selectItems value="#{homeMonitorDetail.typeDefs.items[0]}" />
										</af:selectOneChoice>
										<af:inputText label="Name:"
											value="#{homeMonitorDetail.monitor.name}"
											id="name" columns="60" maximumLength="60"
											showRequired="true"
											readOnly="#{!homeMonitorDetail.editable}"
											/>
										<af:selectOneChoice label="Parameter:"
											rendered="#{homeMonitorDetail.monitor.typeAmbient}"
											readOnly="#{!homeMonitorDetail.editable}"
											value="#{homeMonitorDetail.monitor.parameter}"
											unselectedLabel=" "
											id="parameter"
											autoSubmit="true"
											showRequired="#{homeMonitorDetail.monitor.typeAmbient}">
											<f:selectItems value="#{homeMonitorDetail.paramTypeDefs.items[0]}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="Parameter:"
											rendered="#{homeMonitorDetail.monitor.typeMeteorological}"
											readOnly="#{!homeMonitorDetail.editable}"
											value="#{homeMonitorDetail.monitor.parameterMet}"
											unselectedLabel=" "
											id="parameterMet"
											autoSubmit="true"
											showRequired="#{homeMonitorDetail.monitor.typeMeteorological}">
											<f:selectItems value="#{homeMonitorDetail.metParamTypeDefs.items[0]}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="Status:"
											readOnly="#{!homeMonitorDetail.editable}"
											value="#{homeMonitorDetail.monitor.status}"
											id="status"
											autoSubmit="true"
											showRequired="true">
											<f:selectItems value="#{homeMonitorDetail.statusDefs.items[0]}" />
										</af:selectOneChoice>
										<af:selectInputDate label="Start Date:"
											id="startDate"
											readOnly="#{!homeMonitorDetail.editable}"
											showRequired="true"
											value="#{homeMonitorDetail.monitor.startDate}">
											<af:validateDateTimeRange minimum="1900-01-01"
												maximum="#{homeMonitorDetail.maxDate}" />
										</af:selectInputDate>
										<af:selectInputDate label="End Date:"
											id="endDate"
											rendered="#{homeMonitorDetail.monitor.status ne homeMonitorDetail.activeStatusCd}"
											readOnly="#{!homeMonitorDetail.editable}"
											showRequired="#{homeMonitorDetail.monitor.status ne homeMonitorDetail.activeStatusCd}"
											value="#{homeMonitorDetail.monitor.endDate}">
											<af:validateDateTimeRange minimum="1900-01-01"
												maximum="#{homeMonitorDetail.maxDate}" />
										</af:selectInputDate>
										<af:inputText label="Parameter Code:"
											value="#{homeMonitorDetail.monitor.parameterCode}"
											id="parameterCd" columns="5" maximumLength="5"
											showRequired="false"
											readOnly="#{!homeMonitorDetail.editable}"
											/>
										<af:inputText label="Parameter Occurrence Code:"
											value="#{homeMonitorDetail.monitor.parameterOccurrenceCode}"
											id="parameterOccurrenceCd" columns="2" maximumLength="2"
											showRequired="false"
											readOnly="#{!homeMonitorDetail.editable}"
											/>
										<af:inputText label="Method Code:"
											value="#{homeMonitorDetail.monitor.methodCode}"
											id="methodCd" columns="3" maximumLength="3"
											showRequired="false"
											readOnly="#{!homeMonitorDetail.editable}"
											>
												<af:convertNumber pattern="000" />
										</af:inputText>
										<af:selectOneChoice label="Duration Code:"
											readOnly="#{!homeMonitorDetail.editable}"
											value="#{homeMonitorDetail.monitor.durationCode}"
											id="durationCd"
											showRequired="false">
											<f:selectItems value="#{homeMonitorDetail.durationCdDefs.items[0]}" />
										</af:selectOneChoice>
										<af:inputText label="Unit Code:"
											value="#{homeMonitorDetail.monitor.unitCode}"
											id="unitCd" columns="3" maximumLength="3"
											showRequired="false"
											readOnly="#{!homeMonitorDetail.editable}"
											>
												<af:convertNumber pattern="000" />
											</af:inputText>
										<af:selectOneChoice label="Collection Frequency Code:"
											readOnly="#{!homeMonitorDetail.editable}"
											value="#{homeMonitorDetail.monitor.frequencyCode}"
											id="frequencyCd"
											showRequired="false">
											<f:selectItems value="#{homeMonitorDetail.collFreqCdDefs.items[0]}" />
										</af:selectOneChoice>
									</af:panelForm>
								<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
						          <af:inputText id="comments" label="Comments: " rows="4"
								        showRequired="false"
							            value="#{homeMonitorDetail.monitor.comments}"
										readOnly="#{!homeMonitorDetail.editable}"
							            columns="80" maximumLength="255" />
									
								</af:panelForm>
					
								<af:objectSpacer height="25" />

								<afh:rowLayout halign="center"
									rendered="#{homeMonitorDetail.monitorId != null}">
									<af:panelButtonBar>
										<af:commandButton text="Show Associated Monitor Site"
											action="#{homeMonitorDetail.showAssociatedSite}"
											rendered="#{! homeMonitorDetail.editable}" />
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
