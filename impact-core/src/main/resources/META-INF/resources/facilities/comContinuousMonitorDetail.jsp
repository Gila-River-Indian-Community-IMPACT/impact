<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<h:panelGrid border="1">
	<af:panelBorder>

		<f:facet name="top">
			<f:subview id="facilityHeader">
				<jsp:include page="comFacilityHeader.jsp" />
			</f:subview>
		</f:facet>


		<f:facet name="right">
			<h:panelGrid columns="1" width="750"
				rendered="#{continuousMonitorDetail.continuousMonitor != null}">



				<af:panelForm>
					<af:panelBox width="1100">
						<af:panelForm rows="1" maxColumns="3">
							<af:inputText id="monId" label="Monitor ID:" readOnly="true"
								value="#{continuousMonitorDetail.continuousMonitor.monId}" />
							<af:selectOneChoice id="creatorId" label="Creator:"
								readOnly="true"
								rendered="#{!facilityProfile.publicApp}"
								value="#{continuousMonitorDetail.continuousMonitor.creatorId}">
								<f:selectItems
									value="#{infraDefs.basicUsersDef.items[(empty continuousMonitorDetail.continuousMonitor.creatorId ? 0 : continuousMonitorDetail.continuousMonitor.creatorId)]}" />
							</af:selectOneChoice>
							<af:selectInputDate id="addDate" label="Created Date:"
								readOnly="true"
								value="#{continuousMonitorDetail.continuousMonitor.addDate}" />
						</af:panelForm>
					</af:panelBox>

					<af:objectSpacer height="10" />

					<afh:rowLayout halign="left">
						<af:panelForm width="600px" labelWidth="200px">
							<af:inputText id="monitorDetails" label="Monitor Details:"
								readOnly="#{!continuousMonitorDetail.editable}" columns="100"
								rows="6" maximumLength="500" showRequired="true"
								value="#{continuousMonitorDetail.continuousMonitor.monitorDetails}" />
						</af:panelForm>
					</afh:rowLayout>

					<f:subview id="cem_com_cms_limits">
						<jsp:include flush="true"
							page="../facilities/facilityMonitorCemComLimitList.jsp" />
					</f:subview>

					<af:objectSpacer height="10" />
					<af:showDetailHeader
						disclosed="#{continuousMonitorDetail.hasAssociatedObjects}"
						text="Associated Object(s)">
						<af:panelHorizontal halign="center" valign="top">
							<af:selectManyShuttle size="15"
								leadingHeader="Available Emission Units"
								trailingHeader="Associated Emission Units"
								disabled="#{!continuousMonitorDetail.editable}"
								value="#{continuousMonitorDetail.associatedEUs}">
								<f:selectItems value="#{continuousMonitorDetail.availableEUs}" />
							</af:selectManyShuttle>

							<af:selectManyShuttle size="15"
								leadingHeader="Available Release Points"
								trailingHeader="Associated Release Points"
								disabled="#{!continuousMonitorDetail.editable}"
								value="#{continuousMonitorDetail.associatedEgressPoints}">
								<f:selectItems
									value="#{continuousMonitorDetail.availableEgressPoints}" />
							</af:selectManyShuttle>
						</af:panelHorizontal>

					</af:showDetailHeader>

					<af:objectSpacer height="10" />


					<f:subview id="tracking_monitor_eqts">
						<jsp:include flush="true"
							page="../continuousMonitoring/continuousMonitorEqts.jsp" />
					</f:subview>

					<af:objectSpacer height="5" />

					<af:showDetailHeader text="Notes" disclosed="true"
						rendered="#{continuousMonitorDetail.continuousMonitor.continuousMonitorId != null && continuousMonitorDetail.internalApp}"
						id="cmNotes">
						<jsp:include flush="true"
							page="../continuousMonitoring/continuousMonitorNotesTable.jsp" />
					</af:showDetailHeader>

					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Edit" id="edit"
								rendered="#{!continuousMonitorDetail.editable && continuousMonitorDetail.internalApp}"
								action="#{continuousMonitorDetail.startEditAction}"
								disabled="#{!continuousMonitorDetail.continuousMonitorEditAllowed || facilityProfile.disabledUpdateButton}" />
							<af:commandButton text="Save" id="save"
								rendered="#{continuousMonitorDetail.editable}"
								actionListener="#{continuousMonitorDetail.saveContinuousMonitor}" />
							<af:commandButton text="Cancel" id="cancel"
								rendered="#{continuousMonitorDetail.editable}"
								actionListener="#{continuousMonitorDetail.cancelEditAction}" />
							<af:commandButton text="Delete" id="delete"
								disabled="#{!continuousMonitorDetail.continuousMonitorDeleteAllowed}"
								action="#{continuousMonitorDetail.requestDelete}"
								useWindow="true" windowWidth="500" windowHeight="300"
								shortDesc="#{continuousMonitorDetail.continuousMonitorDeleteAllowed ? 'Delete' : 
	                						continuousMonitorDetail.continuousMonitorDeleteAllowedMsg}"
								rendered="#{continuousMonitorDetail.admin && !continuousMonitorDetail.editable}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>


			</h:panelGrid>
		</f:facet>

		<f:facet name="bottom">
		</f:facet>

	</af:panelBorder>
</h:panelGrid>
