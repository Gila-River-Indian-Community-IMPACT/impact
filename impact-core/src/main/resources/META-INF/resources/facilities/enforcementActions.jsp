<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document id="body" title="Enforcement">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Enforcement Actions">
				<af:inputHidden value="#{enforcementSearch.popupRedirect}" />
<%-- 				<af:messages /> --%>
				
				<jsp:include page="header.jsp" />

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="facilityHeader">
								<jsp:include page="comFacilityCetaHeader.jsp" />
							</f:subview>
						</f:facet>

						<f:facet name="right">
							<h:panelGrid columns="1" border="1" width="950"
								style="margin-left:auto;margin-right:auto;">
								<af:panelGroup layout="vertical">
									<af:objectSpacer height="10" />
									<afh:rowLayout halign="center">
										<h:panelGrid border="1" width="950">
											<jsp:include flush="true"
												page="../ceta/enforcementSearchTable.jsp" />
										</h:panelGrid>
									</afh:rowLayout>

								</af:panelGroup>
							</h:panelGrid>
						</f:facet>
					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
		<af:iterator value="#{enforcementActionDetail}" var="validationBean" id="v">
			<%@ include file="../util/validationComponents.jsp"%>
		</af:iterator>
	</af:document>
</f:view>

