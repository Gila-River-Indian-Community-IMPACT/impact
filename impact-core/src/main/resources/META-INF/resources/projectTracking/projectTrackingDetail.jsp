<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Project Tracking Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page id="projectTrackingDetail" var="foo"
				value="#{menuModel.model}" title="Project Tracking Detail">
				<%@ include file="header.jsp"%>

				<af:inputHidden value="#{projectTrackingDetail.popupRedirect}" />

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<af:panelBorder>
							<f:subview id="projectTrackingTop">
								<jsp:include page="projectTrackingTop.jsp" />
							</f:subview>

							<f:subview id="commonAttributes">
								<jsp:include page="projectTrackingDetailCommon.jsp" />
							</f:subview>

							<af:showDetailHeader text="Project Type Specific Information"
								disclosed="true"
								rendered="#{projectTrackingDetail.projectType != 'basic'}">
								<af:switcher defaultFacet="basic"
									facetName="#{projectTrackingDetail.projectType}">
									<f:facet name="basic" />
									<f:facet name="nepa">
										<f:subview id="nepaAttributes">
											<jsp:include flush="true"
												page="projectTrackingDetailNEPA.jsp" />
										</f:subview>
									</f:facet>
									<f:facet name="grants">
										<f:subview id="grantAttributes">
											<jsp:include flush="true"
												page="projectTrackingDetailGrant.jsp" />
										</f:subview>
									</f:facet>
									<f:facet name="letters">
										<f:subview id="letterAttributes">
											<jsp:include flush="true"
												page="projectTrackingDetailLetter.jsp" />
										</f:subview>
									</f:facet>
									<f:facet name="contract">
										<f:subview id="contractAttributes">
											<jsp:include flush="true"
												page="projectTrackingDetailContract.jsp" />
										</f:subview>
									</f:facet>
								</af:switcher>
							</af:showDetailHeader>

							<f:subview id="projectTrackingEventsList"
								rendered="#{projectTrackingDetail.displayProjectTrackingEvents}">
								<jsp:include flush="true" page="projectTrackingEventsList.jsp" />
							</f:subview>
							
							<f:subview id="projectAttachments" >
								<jsp:include flush="true" page="projectAttachmentList.jsp" />
							</f:subview>

							 <f:subview id="projectTrackingNotes" >
								<jsp:include flush="true" page="projectTrackingNotesTable.jsp" />
							</f:subview>
							
							<af:objectSeparator />

							<afh:rowLayout halign="center">
								<af:switcher defaultFacet="edit"
									facetName="#{projectTrackingDetail.editMode ? 'edit' : 'readOnly'}">
									<f:facet name="edit">
										<af:panelButtonBar>
											<af:commandButton id="saveBtn" text="Save"
												action="#{projectTrackingDetail.save}" />
											<af:commandButton id="cancelBtn" text="Cancel"
												action="#{projectTrackingDetail.cancel}" />
										</af:panelButtonBar>
									</f:facet>
									<f:facet name="readOnly">
										<af:panelButtonBar>
											<af:commandButton id="editBtn" text="Edit"
												disabled="#{projectTrackingDetail.readOnlyUser}"
												action="#{projectTrackingDetail.enterEditMode}" />
											<af:commandButton id="deleteBtn" text="Delete"
												rendered="#{infraDefs.stars2Admin}"
												useWindow="true" windowHeight="#{confirmWindow.height}"
												windowWidth="#{confirmWindow.width}"
												action="#{confirmWindow.confirm}">
												<t:updateActionListener property="#{confirmWindow.type}"
													value="#{confirmWindow.yesNo}" />
												<t:updateActionListener property="#{confirmWindow.method}"
													value="projectTrackingDetail.delete" />
												<t:updateActionListener property="#{confirmWindow.message}"
													value="All data associated with project '#{projectTrackingDetail.project.projectNumber}' will be deleted. Would you like to continue?" />
											</af:commandButton>
										
										
										
											<af:commandButton text="Download/Print" id="ProjectTrackingDetail_PrintBtn"
												rendered="#{! projectTrackingDetail.editMode}"
												action="#{projectTrackingDetail.printProjectTrackingDetail}" useWindow="true"
												windowWidth="500" windowHeight="300">
											</af:commandButton>



										</af:panelButtonBar>
									</f:facet>
								</af:switcher>
							</afh:rowLayout>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>