<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Add Monitor">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				id="createMonitor" title="Add Monitor">
				<f:facet name="messages">
				  <af:messages />
				</f:facet>
				<afh:rowLayout halign="center">
					<h:panelGrid border="0" width="1000px">
							<af:panelGroup layout="vertical"
								partialTriggers="status type parameter parameterMet">

									<af:panelForm rows="6" maxColumns="2" labelWidth="220px" width="98%">
										<af:selectOneChoice label="Monitor Type:"
											readOnly="false"
											value="#{createMonitor.monitor.type}"
											unselectedLabel=" "
											valueChangeListener="#{createMonitor.monitorTypeChanged}"
											id="type"
											autoSubmit="true"
											showRequired="true">
											<f:selectItems value="#{monitorDetail.typeDefs.items[0]}" />
										</af:selectOneChoice>
										<af:inputText label="Name:"
											value="#{createMonitor.monitor.name}"
											id="name" columns="60" maximumLength="60"
											showRequired="true"
											readOnly="false"/>
										<af:selectOneChoice label="Parameter:"
											rendered="#{createMonitor.monitor.typeAmbient}"
											readOnly="false"
											value="#{createMonitor.monitor.parameter}"
											unselectedLabel=" "
											id="parameter"
											autoSubmit="true"
											showRequired="#{createMonitor.monitor.typeAmbient}">
											<f:selectItems value="#{monitorDetail.paramTypeDefs.items[0]}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="Parameter:"
											rendered="#{createMonitor.monitor.typeMeteorological}"
											readOnly="false"
											value="#{createMonitor.monitor.parameterMet}"
											unselectedLabel=" "
											id="parameterMet"
											autoSubmit="true"
											showRequired="#{createMonitor.monitor.typeMeteorological}">
											<f:selectItems value="#{monitorDetail.metParamTypeDefs.items[0]}" />
										</af:selectOneChoice>
										<af:selectOneChoice label="Status:"
											readOnly="false"
											value="#{createMonitor.monitor.status}"
											id="status"
											autoSubmit="true"
											showRequired="true">
											<f:selectItems value="#{monitorDetail.statusDefs.items[0]}" />
										</af:selectOneChoice>
										<af:selectInputDate label="Start Date:"
											id="startDate"
											readOnly="false"
											showRequired="true"
											value="#{createMonitor.monitor.startDate}">
											<af:validateDateTimeRange minimum="1900-01-01"
												maximum="#{monitorDetail.maxDate}" />
										</af:selectInputDate>
										<af:selectInputDate label="End Date:"
											id="endDate"
											readOnly="false"
											rendered="#{createMonitor.monitor.status ne monitorDetail.activeStatusCd}"
											showRequired="#{createMonitor.monitor.status ne monitorDetail.activeStatusCd}"
											value="#{createMonitor.monitor.endDate}">
											<af:validateDateTimeRange minimum="1900-01-01"
												maximum="#{monitorDetail.maxDate}" />
										</af:selectInputDate>
										<af:inputText label="Parameter Code:"
											value="#{createMonitor.monitor.parameterCode}"
											id="parameterCd" columns="5" maximumLength="5"
											showRequired="false"
											readOnly="false"/>
										<af:inputText label="Parameter Occurrence Code:"
											value="#{createMonitor.monitor.parameterOccurrenceCode}"
											id="parameterOccurrenceCd" columns="2" maximumLength="2"
											showRequired="false"
											readOnly="false"/>
										<af:inputText label="Method Code:"
											value="#{createMonitor.monitor.methodCode}"
											id="methodCd" columns="3" maximumLength="3"
											showRequired="false"
											readOnly="false"/>
										<af:selectOneChoice label="Duration Code:"
											readOnly="false"
											value="#{createMonitor.monitor.durationCode}"
											id="durationCd"
											showRequired="false">
											<f:selectItems value="#{monitorDetail.durationCdDefs.items[0]}" />
										</af:selectOneChoice>
										<af:inputText label="Unit Code:"
											value="#{createMonitor.monitor.unitCode}"
											id="unitCd" columns="3" maximumLength="3"
											showRequired="false"
											readOnly="false"/>
										<af:selectOneChoice label="Collection Frequency Code:"
											readOnly="false"
											value="#{createMonitor.monitor.frequencyCode}"
											id="frequencyCd"
											showRequired="false">
											<f:selectItems value="#{monitorDetail.collFreqCdDefs.items[0]}" />
										</af:selectOneChoice>
									</af:panelForm>
								<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
						          <af:inputText id="comments" label="Comments: " rows="4"
								        showRequired="false"
							            value="#{createMonitor.monitor.comments}"
										readOnly="false"
							            columns="80" maximumLength="255" />
									
								</af:panelForm>
					


								<af:objectSpacer height="25" />

								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Add"
											action="#{createMonitor.create}" />
										<af:commandButton text="Reset"
											action="#{createMonitor.reset}" />
										<af:commandButton text="Cancel"
											action="#{createMonitor.cancel}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelGroup>
					</h:panelGrid>
				</afh:rowLayout>
				
			</af:page>
		</af:form>
	</af:document>
</f:view>
