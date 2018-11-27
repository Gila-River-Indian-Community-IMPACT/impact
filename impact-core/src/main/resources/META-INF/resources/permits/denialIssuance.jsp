<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Withdrawal Publication">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				title="Withdrawal Publication">
				<%@ include file="../permits/header.jsp"%>

				<mu:setProperty property="#{permitDetail.currentIssuanceAction}"
					value="#{permitReference.deniedIssuanceType}" />

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="permitDetailTop">
								<jsp:include page="permitDetailTop.jsp" />
							</f:subview>
						</f:facet>

						<%
							/* Content begin */
						%>
						<h:panelGrid columns="1" border="1"
							width="#{permitDetail.permitWidth}">
							<af:panelGroup>

								<%
									/* Draft begin */
								%>
								<af:panelHeader text="Withdrawal">

								</af:panelHeader>
								<%
									/* Draft end */
								%>
								
								<f:subview id="permit_attachments">
										<jsp:include page="permit_attachments.jsp" />
								</f:subview>

								<af:objectSpacer height="10" />

								<afh:rowLayout halign="center" rendered="false">
									<af:panelButtonBar
										rendered="#{permitDetail.editMode && permitDetail.permit.deniedIssuanceStatusCd != permitReference.issuanceStatusIssued}">

									</af:panelButtonBar>
									<af:panelButtonBar
										rendered="#{!permitDetail.editMode && permitDetail.permit.deniedIssuanceStatusCd != permitReference.issuanceStatusIssued}">
										<af:commandButton text="Prepare Issuance"
											rendered="#{permitDetail.permit.deniedIssuanceStatusCd != permitReference.issuanceStatusReady}"
											action="#{permitDetail.prepareIssuance}">
										</af:commandButton>
										<af:commandButton text="Mark Ready Issuance"
											rendered="#{permitDetail.permit.deniedIssuanceStatusCd == permitReference.issuanceStatusNotReady}"
											action="#{permitDetail.markReadyIssuance}">
										</af:commandButton>
										<af:commandButton text="UnMark Ready Issuance"
											rendered="#{permitDetail.permit.deniedIssuanceStatusCd == permitReference.issuanceStatusReady}"
											action="#{permitDetail.unprepareIssuance}">
										</af:commandButton>
										<af:commandButton text="Validate Issuance"
											rendered="#{permitDetail.permit.deniedIssueDate != null}"
											action="#{permitDetail.validateIssuance}">
										</af:commandButton>
										<af:commandButton text="Finalize Issuance"
											rendered="#{permitDetail.permit.deniedIssuanceStatusCd == permitReference.issuanceStatusReady}"
											action="#{permitDetail.finalizeIssuance}">
										</af:commandButton>
									</af:panelButtonBar>
								</afh:rowLayout>

								<af:objectSpacer height="10" rendered="false" />

								<f:subview id="permitIssuanceButtons" rendered="false">
									<jsp:include page="editOnlyButtons.jsp" />
								</f:subview>
								<%
									/* Buttons end */
								%>

							</af:panelGroup>
						</h:panelGrid>
						<%
							/* Content end */
						%>

					</af:panelBorder>
				</h:panelGrid>

			</af:page>
			<af:iterator value="#{permitDetail}" var="validationBean" id="v">
				<%@ include file="../util/validationComponents.jsp"%>
			</af:iterator>
		</af:form>
	</af:document>
</f:view>
