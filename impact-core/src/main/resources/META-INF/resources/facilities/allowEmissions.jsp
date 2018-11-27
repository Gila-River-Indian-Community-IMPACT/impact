<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Permitted Emissions">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Permitted Emissions">

				<jsp:include page="header.jsp" />

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="facilityHeader">
								<jsp:include page="comFacilityHeader.jsp" />
							</f:subview>
						</f:facet>

						<f:facet name="right">
							<h:panelGrid columns="1" border="1" 
								style="margin-left:auto;margin-right:auto;">
								<af:panelGroup layout="vertical">
									<af:objectSpacer height="10" />
									<af:panelForm>
										<af:table value="#{facilityProfile.facEmissionsWrapper}"
											bandingInterval="1"
											binding="#{facilityProfile.facEmissionsWrapper.table}"
											banding="row" var="emission" id="EuPollutantTab" width="98%">
											<af:column sortProperty="c01" sortable="true"
												formatType="text" headerText="Pollutant" noWrap="true">
												<af:selectOneChoice value="#{emission.pollutantCd}"
													readOnly="true">
													<f:selectItems
														value="#{facilityReference.euPollutantDefs.items[(empty emission.pollutantCd ? '' : emission.pollutantCd)]}" />
												</af:selectOneChoice>
											</af:column>
											<af:column formatType="number"
												headerText="Potential Emissions Rate (Lbs/Hour)" width="135px">
												<af:outputText value="#{emission.potentialEmissionsLbsHour}">
													<af:convertNumber type='number' locale="en-US"
														maxFractionDigits="6" />
												</af:outputText>
											</af:column>
											<af:column formatType="number"
												headerText="Potential Emissions Rate (Tons/Year)" width="130px">
												<af:outputText value="#{emission.potentialEmissionsTonsYear}">
													<af:convertNumber type='number' locale="en-US"
														maxFractionDigits="6" />
												</af:outputText>
											</af:column>
											<af:column formatType="number"
												headerText="Allowable Emissions Rate (Lbs/Hour)" width="137px">
												<af:outputText value="#{emission.allowableEmissionsLbsHour}">
													<af:convertNumber type='number' locale="en-US"
														maxFractionDigits="6" />
												</af:outputText>
											</af:column>
											<af:column formatType="number"
												headerText="Allowable Emissions Rate (Tons/Year)" width="137px">
												<af:outputText value="#{emission.allowableEmissionsTonsYear}">
													<af:convertNumber type='number' locale="en-US"
														maxFractionDigits="6" />
												</af:outputText>
											</af:column>
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
									</af:panelForm>
								</af:panelGroup>
							</h:panelGrid>
						</f:facet>

					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>
