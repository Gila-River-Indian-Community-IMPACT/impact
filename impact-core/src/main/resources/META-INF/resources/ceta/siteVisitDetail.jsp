<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="siteVisitBody" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Site Visit Detail">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}" title="Site Visit Detail">
			<af:inputHidden value="#{stackTests.popupRedirect}" />
			<%@ include file="../util/branding.jsp"%>
			<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}"
						onclick="if (#{siteVisitDetail.editable}) { alert('Please save or discard your changes'); return false; }" />
			</f:facet>
					
				
				<afh:rowLayout halign="center" width="1000"  rendered="#{!siteVisitDetail.blankOutPage}">
				<h:panelGrid border="1">
				<af:panelBorder>
				<af:panelBox background="light" width="1000">
					<af:panelForm rows="2" maxColumns="3">
						<af:inputText label="Facility ID:" readOnly="True"
							value="#{siteVisitDetail.facility.facilityId}" />
						<af:inputText label="Facility Name:" readOnly="true"
							value="#{siteVisitDetail.facility.name}" />
						<af:selectOneChoice label="Facility Class:"
							value="#{siteVisitDetail.facility.permitClassCd}" readOnly="true">
							<f:selectItems
								value="#{facilityReference.permitClassDefs.items[(empty siteVisitDetail.facility.permitClassCd ? '' : siteVisitDetail.facility.permitClassCd)]}" />
						</af:selectOneChoice>
						<af:inputText label="Facility Type:" readOnly="True"
							value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty siteVisitDetail.facility.facilityTypeCd ? '' : siteVisitDetail.facility.facilityTypeCd)]}" />
						<af:inputText readOnly="true" value=" " />
						<af:inputText label="Site Visit ID:" readOnly="true"
							value="#{siteVisitDetail.siteVisit.siteId}" />
						<af:inputText label="Associated Inspection ID:" readOnly="true"
							rendered="#{siteVisitDetail.siteVisit.inspId != null}"
							value="#{siteVisitDetail.siteVisit.inspId}" />
						<af:inputText readOnly="true"
							rendered="#{siteVisitDetail.fce == null}" value=" " />
					</af:panelForm>
				</af:panelBox>
					<af:objectSpacer height="20" />
							<af:panelForm maxColumns="1"
								rendered="#{!siteVisitDetail.blankOutPage}">
								<afh:rowLayout>
									<afh:cellFormat valign="top" halign="left" width="700">
										<af:panelForm maxColumns="1">
											<af:selectOneChoice label="Visit Type:" unselectedLabel="" rendered="#{!siteVisitDetail.siteVisit.stackTest}"
												value="#{siteVisitDetail.siteVisit.visitType}"
												showRequired="true" readOnly="#{!siteVisitDetail.editable}"
												id="visitType">
												<f:selectItems
													value="#{compEvalDefs.siteVisitNoEmissionTestTypeDef.items[(empty siteVisitDetail.siteVisit.visitType ? '' : siteVisitDetail.siteVisit.visitType)]}" />
											</af:selectOneChoice>
											<af:selectOneChoice label="Visit Type:" unselectedLabel="" rendered="#{siteVisitDetail.siteVisit.stackTest}"
												value="#{siteVisitDetail.siteVisit.visitType}"
												readOnly="true" id="visitTypeStackTest">
												<f:selectItems
													value="#{compEvalDefs.siteVisitTypeDef.items[(empty siteVisitDetail.siteVisit.visitType ? '' : siteVisitDetail.siteVisit.visitType)]}" />
											</af:selectOneChoice>
											<af:selectInputDate id="visitDate" label="Visit Date:"
												readOnly="#{!siteVisitDetail.editable}" showRequired="true"
												value="#{siteVisitDetail.siteVisit.visitDate}">
												<af:validateDateTimeRange minimum="1900-01-01" />
											</af:selectInputDate>
											<af:selectOneChoice label="Compliance Issue:" rendered="#{!siteVisitDetail.siteVisit.stackTest}"
												readOnly="#{!siteVisitDetail.editable}" showRequired="true"
												value="#{siteVisitDetail.siteVisit.complianceIssued}"
												unselectedLabel="">
												<f:selectItem itemLabel="Yes" itemValue="Y" />
												<f:selectItem itemLabel="No" itemValue="N" />
											</af:selectOneChoice>
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
									<%-- Sorting is intentionally disabled, as allowing sorting causes anomalies when
									maintaining the list. 
									 --%>
									<afh:cellFormat halign="left" width="600">
										<af:panelForm rendered = "#{!siteVisitDetail.siteVisit.stackTest}">
											<af:table id="svTable" emptyText=" " width="50%"
												value="#{siteVisitDetail.siteVisit.evaluators}" var="ev"
												bandingInterval="1" banding="row">
												<af:column sortable="false" sortProperty="selected"
													rendered="#{siteVisitDetail.editable}" formatType="icon"
													headerText="Select">
													<af:selectBooleanCheckbox value="#{ev.selected}"
														readOnly="false" />
												</af:column>
												<af:column sortable="false" sortProperty="evaluator"
													formatType="text" headerText="Evaluator(s)">
													<af:selectOneChoice value="#{ev.evaluator}"
														unselectedLabel="" readOnly="#{!siteVisitDetail.editable}">
														<f:selectItems
															value="#{empty ev.evaluator ? siteVisitDetail.svBasicUsersDef.items[0] : infraDefs.basicUsersDef.items[ev.evaluator]}" />
													</af:selectOneChoice>
												</af:column>
												<f:facet name="footer">
													<afh:rowLayout halign="center">
														<af:panelButtonBar>
															<af:commandButton
																action="#{siteVisitDetail.addVisitEvaluator}"
																rendered="#{siteVisitDetail.editable}"
																shortDesc="Click to add another person.  Blank out name to remove it."
																text="Add Person" />
															<af:commandButton
																action="#{siteVisitDetail.deleteEvaluators}"
																rendered="#{siteVisitDetail.editable}"
																shortDesc="Click to remove selected persons."
																text="Delete Selected People" />
														</af:panelButtonBar>
													</afh:rowLayout>
												</f:facet>
											</af:table>
										</af:panelForm>
										<af:panelForm rendered = "#{siteVisitDetail.siteVisit.stackTest}">
											<af:table id="wTable" emptyText=" " width="50%"
												value="#{siteVisitDetail.witnesses}" var="ev" bandingInterval="1"
												banding="row">
												<af:column sortable="true" sortProperty="evaluator"
													formatType="text" headerText="Witness(es)">
													<af:selectOneChoice value="#{ev.evaluator}"
														unselectedLabel="" readOnly="true">
														<f:selectItems
															value="#{infraDefs.basicUsersDef.items[(empty ev.evaluator?0:ev.evaluator)]}" />
													</af:selectOneChoice>
												</af:column>
											</af:table>
										</af:panelForm>
									</afh:cellFormat>
								</afh:rowLayout>
								<afh:rowLayout halign="left">
									<af:panelForm rendered="#{!siteVisitDetail.blankOutPage && !siteVisitDetail.siteVisit.stackTest}">
										<af:inputText id="noteTxt" label="Memo :"
											readOnly="#{!siteVisitDetail.editable}"
											value="#{siteVisitDetail.siteVisit.memo}" columns="145"
											rows="6" maximumLength="4000" onkeydown="charsLeft(4000);"
											onkeyup="charsLeft(4000);" />
									</af:panelForm>
								</afh:rowLayout>

								<af:showDetailHeader text="Stack Tests" disclosed="true"
									rendered="#{siteVisitDetail.siteVisit.stackTest}">
									<af:table id="stackTestTable" emptyText=" "
										value="#{siteVisitDetail.stackTestList}" var="st"
										bandingInterval="1" banding="row">
										<af:column sortable="true" sortProperty="id" formatType="text"
											headerText="Stack Test ID">
											<af:commandLink text="#{st.stckId}"
												rendered="#{! siteVisitDetail.editable}"
												action="#{stackTestDetail.submitStackTest}">
												<af:setActionListener from="#{st.id}"
													to="#{stackTestDetail.id}" />
											</af:commandLink>
											<af:inputText readOnly="true" value="#{st.id}"
												rendered="#{siteVisitDetail.editable}" />
										</af:column>
										<jsp:include flush="true" page="../ceta/stackTestList.jsp" />
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
								<f:subview id="doc_attachments"
									rendered="#{!siteVisitDetail.blankOutPage && siteVisitDetail.siteVisit.id != null && !siteVisitDetail.siteVisit.stackTest}">
									<jsp:include flush="true"
										page="../doc_attachments/doc_attachments.jsp" />
								</f:subview>
								<af:showDetailHeader text="Notes" disclosed="true"  rendered="#{siteVisitDetail.siteVisit.id != null && !siteVisitDetail.siteVisit.stackTest}"
									id="siteVisitNotes">
									<jsp:include flush="true" page="notesSiteVisitsTable.jsp" />
								</af:showDetailHeader>
							</af:panelForm>
				<af:objectSpacer height="20" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							action="#{siteVisitDetail.editVisit}"
							disabled="#{!siteVisitDetail.allowEditOperations}"
							rendered="#{! siteVisitDetail.editable && !siteVisitDetail.blankOutPage && !siteVisitDetail.siteVisit.stackTest}" />
						<af:commandButton text="Delete"
							disabled="#{!siteVisitDetail.allowEditOperations}"
							action="#{siteVisitDetail.deleteVisit}"
							returnListener="#{siteVisitDetail.deleteFinished}"
							useWindow="true" windowWidth="600" windowHeight="300"
							rendered="#{ (!siteVisitDetail.editable && !siteVisitDetail.blankOutPage && !siteVisitDetail.siteVisit.stackTest) || siteVisitDetail.orphanedSiteVisit}" />
						<af:commandButton text="Save" action="#{siteVisitDetail.save}"
							rendered="#{siteVisitDetail.editable && !siteVisitDetail.blankOutPage}" />
						<af:commandButton text="Cancel"
							action="#{siteVisitDetail.cancelEdit}" immediate="true"
							rendered="#{siteVisitDetail.editable && !siteVisitDetail.blankOutPage}" />
						<af:commandButton text="Set/Change Inspection Association"
							action="#{siteVisitDetail.chgFceAssign}" useWindow="true"
							windowWidth="1200" windowHeight="600"
							disabled="#{!siteVisitDetail.cetaUpdate}"
							rendered="#{!siteVisitDetail.editable && !siteVisitDetail.blankOutPage}">
							<t:updateActionListener property="#{fceSiteVisits.fromFacility}"
								value="true" />
						</af:commandButton>
					</af:panelButtonBar>
				</afh:rowLayout>
				<af:objectSpacer height="5" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar rendered="#{!siteVisitDetail.blankOutPage}">
						<af:commandButton id="viewFacility"
							text="Show Current Facility Inventory"
							rendered="#{!siteVisitDetail.editable}"
							action="#{facilityProfile.submitProfileById}">
							<t:updateActionListener property="#{facilityProfile.facilityId}"
								value="#{siteVisitDetail.facilityId}" />
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandButton>
						<af:commandButton id="viewHistoryInfo"
							text="View Air Program History Info"
							rendered="#{!siteVisitDetail.editable && siteVisitDetail.siteVisit.facilityHistId != null}"
							action="#{fceSiteVisits.viewHistory}" useWindow="true"
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
						<af:commandButton id="goToSiteVisits"
							text="Show Facility Site Visits"
							rendered="#{!siteVisitDetail.editable}"
							action="#{siteVisitDetail.goToSummaryPage}">
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandButton>
						</af:panelButtonBar>
				</afh:rowLayout>
				<af:objectSpacer width="100%" height="75" />
				</af:panelBorder>
				</h:panelGrid>
				</afh:rowLayout>
			
			</af:page>
		</af:form>
	</af:document>
</f:view>
