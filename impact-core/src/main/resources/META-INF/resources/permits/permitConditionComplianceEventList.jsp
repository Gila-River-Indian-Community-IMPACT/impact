<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" >
	<af:showDetailHeader text="Compliance Status History" disclosed="true">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>

			<%@ include file="../scripts/navigate.js"%>
		</f:verbatim>
		<%
			/* Content begin */
		%>
		<h:panelGrid columns="1" border="1"
			width="#{permitDetail.permitWidth}">
			<af:panelGroup>
				<afh:rowLayout halign="center" valign="top">
					<af:table value="#{permitConditionDetail.complianceStatusEventsWrapper}"
						binding="#{permitConditionDetail.complianceStatusEventsWrapper.table}"
						bandingInterval="1" banding="row" id="complianceStatusEventTab"
						width="#{permitDetail.permitTableWidth}"
						var="complianceStatusEvent" rows="#{permitDetail.pageLimit}"
						emptyText=" " varStatus="complianceStatusEventTableVs">

						<af:column id="edit" formatType="text" width="175px"
							headerText="Compliance Status ID" sortProperty="cStatusId"
							sortable="true" noWrap="true" headerNoWrap="true">
							<af:commandLink
								text="#{complianceStatusEvent.cStatusId}" 
								useWindow="true" windowWidth="700"
								windowHeight="350" inlineStyle="padding-left:5px;"
								action="#{permitConditionDetail.startToEditPermitConditionComplainceStatusEvent}">
								<t:updateActionListener property="#{permitConditionDetail.modifyComplianceStatusEvent}"
									value="#{complianceStatusEvent}" />
							</af:commandLink>
						</af:column>

						<af:column headerText="Event Date" formatType="text"
							sortProperty="eventDate" sortable="true" id="csEventDate"
							noWrap="true" headerNoWrap="true">
							<af:selectInputDate label="Event Date" readOnly="true"
								value="#{complianceStatusEvent.eventDate}" />
						</af:column>

						<af:column headerText="Event Type" formatType="text"
							sortProperty="eventTypeCd" sortable="true" id="csEventTypeCd"
							noWrap="true" headerNoWrap="true">
							<af:selectOneChoice label="Event Type :" readOnly="true"
								value="#{complianceStatusEvent.eventTypeCd}" unselectedLabel="">
								<f:selectItems
									value="#{permitReference.complianceTrackingEventTypeDefs.items[
										(empty complianceStatusEvent.eventTypeCd ? '' : complianceStatusEvent.eventTypeCd)]}" />
							</af:selectOneChoice>
						</af:column>

						<af:column headerText="Reference" formatType="text"
							sortProperty="c04" sortable="true" id="csReference" noWrap="true"
							headerNoWrap="true">

							<af:commandLink onclick="setFocus('pcHiddenControls')">
								<t:updateActionListener
									value="#{complianceStatusEvent.eventTypeCd}"
									property="#{permitConditionDetail.complianceEventTypeCd}" />
								<t:updateActionListener
									value="#{complianceStatusEvent.stackTestReference}"
									property="#{permitConditionDetail.complianceReferenceId}" />
								<t:updateActionListener value="false"
									property="#{menuItem_stackTestDetail.disabled}" />
								<af:selectOneChoice label="Reference:" unselectedLabel=""
									readOnly="true"
									rendered="#{complianceStatusEvent.eventTypeCd == 'ST'}"
									value="#{complianceStatusEvent.stackTestReference}">
									<f:selectItems value="#{permitConditionDetail.stackTestIdsForFacility}" />
								</af:selectOneChoice>
							</af:commandLink>

							<af:commandLink onclick="setFocus('pcHiddenControls')">
								<t:updateActionListener
									value="#{complianceStatusEvent.eventTypeCd}"
									property="#{permitConditionDetail.complianceEventTypeCd}" />
								<t:updateActionListener
									value="#{complianceStatusEvent.complianceReportReference}"
									property="#{permitConditionDetail.complianceReferenceId}" />
								<t:updateActionListener value="false"
									property="#{menuItem_compReportDetail.disabled}" />
								<af:selectOneChoice label="Reference:" unselectedLabel=""
									readOnly="true"
									rendered="#{complianceStatusEvent.eventTypeCd == 'CR'}"
									value="#{complianceStatusEvent.complianceReportReference}">
									<f:selectItems
										value="#{permitConditionDetail.complianceReportIdsForFacility}" />
								</af:selectOneChoice>
							</af:commandLink>
							
							<af:commandLink onclick="setFocus('pcHiddenControls')">
								<t:updateActionListener
									value="#{complianceStatusEvent.eventTypeCd}"
									property="#{permitConditionDetail.complianceEventTypeCd}" />
								<t:updateActionListener
									value="#{complianceStatusEvent.inspectionReference}"
									property="#{permitConditionDetail.complianceReferenceId}" />
								<t:updateActionListener value="false"
									property="#{menuItem_fceDetail.disabled}" />
								<af:selectOneChoice label="Reference:" unselectedLabel=""
									readOnly="true"
									rendered="#{complianceStatusEvent.eventTypeCd == 'IN'}"
									value="#{complianceStatusEvent.inspectionReference}">
									<f:selectItems value="#{permitConditionDetail.inspectionIdsForFacility}" />
								</af:selectOneChoice>
							</af:commandLink>
						</af:column>

						<af:column headerText="Status" formatType="text"
							sortProperty="status" sortable="true" id="csStatus"
							noWrap="false" headerNoWrap="true">
							<af:outputText truncateAt="60"
								value="#{complianceStatusEvent.status}" />
						</af:column>

						<af:column headerText="Last Updated By" formatType="text"
							sortProperty="lastUpdatedByName" sortable="true"
							id="csLastUpdatedById" noWrap="true" headerNoWrap="true">
							<af:selectOneChoice readOnly="true"
								value="#{complianceStatusEvent.lastUpdatedById}">
								<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
							</af:selectOneChoice>
						</af:column>

						<af:column headerText="Last Updated Date" formatType="text"
							sortProperty="lastUpdatedDate" sortable="true"
							id="csLastUpdatedDate" noWrap="true" headerNoWrap="true">
							<af:selectInputDate label="Last Updated Date" readOnly="true"
								valign="top" required="true"
								value="#{complianceStatusEvent.lastUpdatedDate}">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
						</af:column>

						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
									<af:commandButton text="Add" id="addComplianceStatusEvent"
										rendered="#{!permitConditionDetail.fromFacilityPermitConditionSearch
													&& !permitConditionDetail.readOnlyUser}"
										useWindow="true" windowWidth="700" windowHeight="350"
										action="#{permitConditionDetail.startToAddPermitConditionComplainceStatusEvent}" />
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelGroup>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</afh:rowLayout>

				<af:objectSpacer height="10" />
			</af:panelGroup>
		</h:panelGrid>
		<%
			/* Content end */
		%>

	</af:showDetailHeader>

</af:panelGroup>


