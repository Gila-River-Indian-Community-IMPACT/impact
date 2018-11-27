<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Stack Tests">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:inputHidden value="#{stackTestSearch.popupRedirect}" />
			<af:page var="foo" value="#{menuModel.model}" title="Stack Tests">

				<jsp:include page="header.jsp" />

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="facilityHeader">
								<jsp:include page="comFacilityHeader2.jsp" /> 
							</f:subview>
						</f:facet>

						<h:panelGrid border="1" width="950"
							style="margin-left:auto;margin-right:auto;">
							<af:panelGroup layout="vertical">
								<af:objectSpacer height="10" />
								<af:panelForm>
										<af:table id="stackTestTable" emptyText=" "
											value="#{stackTests.stackTestList}" var="st"
											bandingInterval="1" banding="row" width="98%">
											<jsp:include flush="true"
												page="../ceta/firstStackTestColumns.jsp" />
											<jsp:include flush="true" page="../ceta/stackTestList.jsp" />
											<f:facet name="footer">
												<afh:rowLayout halign="center">
													<af:panelButtonBar>
														<af:commandButton text="New Stack Test"
															useWindow="true" windowWidth="600" windowHeight="500"
															disabled="#{!stackTests.cetaUpdate || !stackTests.internalApp}"
															rendered="#{!stackTests.publicApp}"
															action="#{stackTests.newStackTest}">
														</af:commandButton>
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
									</af:panelForm>
							</af:panelGroup>
						</h:panelGrid>
					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>
