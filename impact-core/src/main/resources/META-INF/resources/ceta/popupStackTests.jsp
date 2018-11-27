<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Stack Tests corresponding to (Auto-Generated) Site Visit">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form id="fceForm">
			<%@ include file="../util/validate.js"%>
			<af:panelHeader
				text="Witnessed Stack Tests for #{stackTests.visitDateStr}">
				<af:inputHidden value="#{stackTests.popupRedirect}" />
				<af:messages />
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}" />
				</f:facet>
				<af:panelBox background="light" width="1000">
					<af:panelForm rows="2" maxColumns="2">
						<af:inputText label="Facility Id:" readOnly="True"
							value="#{stackTests.facilityForStackTestPopup.facilityId}" />
						<af:inputText label="Facility Name:" readOnly="True"
							value="#{stackTests.facilityForStackTestPopup.name}" />
						<af:inputText label="Associated Inspection ID:" readOnly="true"
							rendered="#{siteVisitDetail.fce != null}"
							value="#{siteVisitDetail.siteVisit.fceId}" />
					</af:panelForm>
				</af:panelBox>
				<afh:rowLayout>
					<afh:cellFormat valign="top" halign="left">
						<af:panelForm>
							<af:selectOneChoice label="Visit Type:" unselectedLabel=""
								value="#{siteVisitDetail.siteVisit.visitType}" readOnly="true"
								id="visitType">
								<f:selectItems
									value="#{compEvalDefs.siteVisitTypeDef.items[(empty siteVisitDetail.siteVisit.visitType ? '' : siteVisitDetail.siteVisit.visitType)]}" />
							</af:selectOneChoice>
							<afh:rowLayout>
								<af:selectInputDate id="visitDate" label="Visit Date:"
									readOnly="true" value="#{siteVisitDetail.siteVisit.visitDate}">
									<af:validateDateTimeRange minimum="1900-01-01" />
								</af:selectInputDate>
								<af:objectSpacer width="3" height="3" />
								<af:objectImage source="/images/Lock_icon1.png"
									rendered="#{siteVisitDetail.siteVisit.afsId!=null}" />
							</afh:rowLayout>
							<%--
							<af:inputText rendered="#{siteVisitDetail.stars2Admin}"
								value="#{siteVisitDetail.siteVisit.afsId}" label="AFS ID:"
								id="afsId" readOnly="#{!siteVisitDetail.editable}"
								inlineStyle="#{siteVisitDetail.stars2Admin?'color: orange; font-weight: bold;':''}"
								maximumLength="8" columns="8" />
							<af:selectInputDate rendered="#{siteVisitDetail.stars2Admin}"
								label="AFS Sent Date:" id="afsDate"
								readOnly="#{!siteVisitDetail.editable}"
								inlineStyle="#{siteVisitDetail.stars2Admin?'color: orange; font-weight: bold;':''}"
								value="#{siteVisitDetail.siteVisit.afsDate}">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
							--%>
						</af:panelForm>
					</afh:cellFormat>
					<afh:cellFormat width="4%">
					</afh:cellFormat>
					<afh:cellFormat halign="right">
						<af:panelForm>
							<af:table id="wTable" emptyText=" "
								value="#{stackTests.witnesses}" var="ev" bandingInterval="1"
								banding="row">
								<af:column sortable="true" sortProperty="evaluator"
									formatType="text" headerText="Witness(es)">
									<af:selectOneChoice value="#{ev.evaluator}" unselectedLabel=""
										readOnly="true">
										<f:selectItems
											value="#{infraDefs.basicUsersDef.items[(empty ev.evaluator?0:ev.evaluator)]}" />
									</af:selectOneChoice>
								</af:column>
							</af:table>
						</af:panelForm>
					</afh:cellFormat>
				</afh:rowLayout>
				<af:panelForm rendered="#{siteVisitDetail.siteVisit.memo != null}">
						<af:inputText readOnly="true"
						rendered="#{siteVisitDetail.siteVisit.memo != null}"
						value="#{siteVisitDetail.siteVisit.memo}"
						inlineStyle="color: orange; font-weight: bold;" />
				</af:panelForm>
				<af:showDetailHeader text="Stack Tests" disclosed="true">
					<af:table id="stackTestTable" emptyText=" "
						value="#{stackTests.stackTestList}" var="st" bandingInterval="1"
						banding="row">
						<af:column sortable="true" sortProperty="id" formatType="text"
							headerText="Stack Test ID">
							<af:commandLink text="#{st.id}"
								rendered="#{! siteVisitDetail.editable}"
								action="#{stackTestDetail.submitStackTestFromPopup}">
								<af:setActionListener from="#{st.id}" to="#{stackTestDetail.id}" />
							</af:commandLink>
							<af:inputText readOnly="true" value="#{st.id}"
								rendered="#{siteVisitDetail.editable}" />
						</af:column>
						<jsp:include flush="true" page="../ceta/stackTestList.jsp" />
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</af:showDetailHeader>
				<af:objectSpacer width="100%" height="5" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
					    <%--
						<af:commandButton text="Edit AFS Info"
							action="#{siteVisitDetail.editVisit}"
							disabled="#{!siteVisitDetail.allowEditOperations}"
							rendered="#{! siteVisitDetail.editable && !siteVisitDetail.readOnlyUser && siteVisitDetail.stars2Admin}" />
						--%>
						<af:commandButton text="Save" action="#{siteVisitDetail.save}"
							rendered="#{siteVisitDetail.editable}" />
						<af:commandButton text="Cancel"
							action="#{siteVisitDetail.cancelEdit}" immediate="true"
							rendered="#{siteVisitDetail.editable}" />
						<af:commandButton text="Close"
							rendered="#{!siteVisitDetail.editable}"
							action="#{stackTestDetail.closePopup}" />
						<af:commandButton text="Set/Change Inspection Association"
							rendered="#{!siteVisitDetail.editable}"
							disabled="#{siteVisitDetail.readOnlyUser || !stackTestDetail.internalApp}"
							action="#{stackTests.chgFceAssign}" useWindow="true"
							windowWidth="1200" windowHeight="600" />
						<af:commandButton id="viewHistoryInfo"
							text="View Air Program History Info"
							rendered="#{!siteVisitDetail.editable && siteVisitDetail.siteVisit.facilityHistId != null}"
							disabled="#{!stackTestDetail.internalApp}"
							action="#{fceSiteVisits.readViewHistory}" useWindow="true"
							windowWidth="750" windowHeight="900">
							<t:updateActionListener
								property="#{fceSiteVisits.facilityHistory}"
								value="#{siteVisitDetail.siteVisit.facilityHistory}" />
							<t:updateActionListener property="#{fceSiteVisits.afsId}"
								value="#{siteVisitDetail.siteVisit.afsId}" />
							<t:updateActionListener
								property="#{fceSiteVisits.histFacilityInfo}"
								value="#{siteVisitDetail.siteVisit.facilityId}" />
						</af:commandButton>
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelHeader>
		</af:form>
	</af:document>
</f:view>

