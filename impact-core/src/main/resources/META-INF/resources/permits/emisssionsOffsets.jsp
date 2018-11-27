<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Offset Tracking">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				title="Offset Tracking">
				<%@ include file="../permits/header.jsp"%>
				<h:panelGrid border="1">
					<af:panelBorder>
						<f:facet name="top">
							<f:subview id="permitDetailTop">
								<jsp:include page="permitDetailTop.jsp" />
							</f:subview>
						</f:facet>
						<afh:rowLayout halign="center">
							<af:outputFormatted
								rendered="#{!permitDetail.offsetTrackingInfoAvailable}"
								inlineStyle="color: orange; font-weight: bold;"
								value="<b>Offset Tracking information is either not applicable to this facility or is not available (legacy permit or pre-8.0)</b>" />
						</afh:rowLayout>

						<f:subview id="emissions_offset">
							<af:forEach items="#{permitDetail.areaEmissionsOffsetList}"
								var="areaEmissionsOffset">
								<%@ include file="emissionOffsetsList.jsp"%>
							</af:forEach>
						</f:subview>

						<af:objectSpacer height="20" />

						<f:subview id="offsetTrackingButtons">
							<afh:rowLayout halign="center"
								rendered="#{!permitDetail.readOnlyUser}">
								<af:switcher defaultFacet="view"
									facetName="#{permitDetail.editMode ? 'edit': 'view'}">
									<f:facet name="view">
										<af:panelButtonBar>
											<af:commandButton text="Edit"
												rendered="#{permitDetail.offsetTrackingInfoAvailable && permitDetail.editAllowed}"
												action="#{permitDetail.enterEditMode}" />
											<af:commandButton text="Workflow Task"
												disabled="#{!permitDetail.fromTODOList}"
												action="#{permitDetail.goToCurrentWorkflow}" />
											<af:commandButton text="Refresh Offset Tracking Information"
												rendered="#{permitDetail.editAllowed}" 
												useWindow="true" windowWidth="600" windowHeight="200"
												action="#{permitDetail.refreshOffsetTrackingInformation}" />
										</af:panelButtonBar>
									</f:facet>
									<f:facet name="edit">
										<af:panelButtonBar>
											<af:commandButton text="Save changes"
												action="#{permitDetail.updatePermitEmissionsOffsets}" />
											<af:commandButton text="Discard changes" immediate="true"
												action="#{permitDetail.undoPermit}" />
										</af:panelButtonBar>
									</f:facet>
								</af:switcher>
							</afh:rowLayout>
						</f:subview>

					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>