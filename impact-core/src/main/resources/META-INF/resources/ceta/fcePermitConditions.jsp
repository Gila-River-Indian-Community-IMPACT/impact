<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Permit Conditions">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
			<style>
					div.permitConditionSearchTable>table.x2d>tbody>tr>td {
						vertical-align: top;
					}
			</style>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				title="Permit Conditions" id="permitConditions">
				<%@ include file="../util/branding.jsp"%>
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}"
						onclick="if (#{fceDetail.permitSelectionEditable || fceDetail.permitConditionsSelectionEditable}) { alert('Please save or discard your changes'); return false; }" />
				</f:facet>
				<afh:rowLayout halign="center" rendered="#{!fceDetail.blankOutPage}">
					<h:panelGrid border="1">
						<af:panelBorder>
							<f:facet name="top">
								<f:subview id="fceHeader">
									<jsp:include page="fceHeader.jsp" />
								</f:subview>
							</f:facet>

							<af:objectSpacer height="10" />
								
							<afh:rowLayout halign="center">
								<af:outputFormatted rendered="#{fceDetail.inspectionReadOnly}" inlineStyle="color: orange; font-weight: bold;" value="#{fceDetail.inspectionReadOnlyStatement}"/>		
							</afh:rowLayout>
							
							<af:objectSpacer height="10" />	

							<h:panelGrid border="1" align="center" width="100%">
								<af:panelBorder>
									<af:panelHeader text="Included/Excluded Permits:" size="0" />
									<af:objectSpacer height="5" />
									<afh:rowLayout halign="center"
										rendered="#{!fceDetail.blankOutPage}">
										<af:selectManyShuttle size="15" id="associatedPermits"
											rendered="true" leadingHeader="Excluded Permits"
											trailingHeader="Included Permits"
											disabled="#{!fceDetail.permitSelectionEditable}"
											value="#{fceDetail.associatedPermits}">
											<f:selectItems value="#{fceDetail.availablePermits}" />
										</af:selectManyShuttle>
									</afh:rowLayout>
									<af:objectSpacer height="5" />
									<afh:rowLayout halign="center"
										rendered="#{!fceDetail.blankOutPage}">
										<af:selectBooleanCheckbox
											label="Automatically add associated permit conditions to Included List:"
											id="addPermitConditionsAutomatically"
											rendered="#{fceDetail.permitSelectionEditable}"
											value="#{fceDetail.addPermitConditionsAutomatically}" />
									</afh:rowLayout>

									<af:objectSpacer height="10" />

									<afh:rowLayout halign="center"
										rendered="#{!fceDetail.blankOutPage}">
										<af:panelButtonBar>
											<af:commandButton text="Edit"
												action="#{fceDetail.editFcePermitSelection}"
												disabled="#{fceDetail.inspectionReadOnly || fceDetail.permitConditionsSelectionEditable}"
												rendered="#{!fceDetail.permitSelectionEditable}" />
											<af:commandButton id="saveBtn" text="Save" useWindow="true"
												windowWidth="600"
												windowHeight="200"
												rendered="#{fceDetail.permitSelectionEditable}"
												action="#{fceDetail.confirmPermitSelection}">
											</af:commandButton>
											<af:commandButton text="Cancel"
												action="#{fceDetail.cancelPermitSelectionEditable}"
												immediate="true"
												rendered="#{fceDetail.permitSelectionEditable}" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</af:panelBorder>
							</h:panelGrid>

							<af:objectSpacer height="10" />

							<h:panelGrid border="1" align="center" width="100%">
								<af:panelBorder>
									<af:panelHeader text="Included/Excluded Permit Conditions:"
										size="0" />
									<af:objectSpacer height="5" />

									<f:subview id="includedPermitConditions">
										<af:showDetailHeader text="Included Permit Conditions"
											disclosed="true" />
										<afh:rowLayout valign="top">
											<af:table value="#{fceDetail.includedPermitConditions}"
												binding="#{fceDetail.includedPermitConditions.table}"
												bandingInterval="1" banding="row"
												id="includedPermitConditions" width="100%"
												var="permitCondition" emptyText=" "
												styleClass="permitConditionTableStyle">
												<af:column headerText="Exclude" sortable="false"
													sortProperty="c01" width="60px" formatType="text"
													rendered="#{fceDetail.permitConditionsSelectionEditable}">
													<af:selectBooleanCheckbox
														value="#{permitCondition.selected}" />
												</af:column>
												<jsp:include flush="true"
													page="fcePermitConditionsTable.jsp" />

												<af:column headerText="Compliance Status" sortable="true"
													sortProperty="c14" formatType="text">
													<af:selectOneChoice label=""
														readOnly="#{!fceDetail.permitConditionsSelectionEditable}"
														value="#{permitCondition.complianceStatusCd}"
														unselectedLabel=" ">
														<f:selectItems
															value="#{compEvalDefs.permitConditionComplianceStatusDef.items[(empty permitCondition.complianceStatusCd ? '' : permitCondition.complianceStatusCd)]}" />
													</af:selectOneChoice>
												</af:column>

												<af:column headerText="Comments" sortable="true"
													sortProperty="c15" formatType="text" width="300px">
													<af:outputText value="#{permitCondition.comments}"
														rendered="#{!fceDetail.permitConditionsSelectionEditable}" />
													<af:inputText label="" maximumLength="200" rows="6"
														rendered="#{fceDetail.permitConditionsSelectionEditable}"
														value="#{permitCondition.comments}" inlineStyle="vertical-align: top;"/>
												</af:column>
											</af:table>
										</afh:rowLayout>
									</f:subview>

									<af:objectSpacer height="15" />

									<f:subview id="excludedPermitConditions">
										<af:showDetailHeader text="Excluded Permit Conditions"
											disclosed="true" />
										<afh:rowLayout valign="top">
											<af:table value="#{fceDetail.excludedPermitConditions}"
												binding="#{fceDetail.excludedPermitConditions.table}"
												bandingInterval="1" banding="row"
												id="excludedPermitConditions" width="100%"
												var="permitCondition" emptyText=" "
												styleClass="permitConditionTableStyle">
												<af:column headerText="Include" sortable="false"
													sortProperty="c01" width="60px" formatType="text"
													rendered="#{fceDetail.permitConditionsSelectionEditable}">
													<af:selectBooleanCheckbox
														value="#{permitCondition.selected}" />
												</af:column>
												<jsp:include flush="true"
													page="fcePermitConditionsTable.jsp" />
											</af:table>
										</afh:rowLayout>
									</f:subview>

									<afh:rowLayout halign="center"
										rendered="#{!fceDetail.blankOutPage}">
										<af:panelButtonBar>
											<af:commandButton text="Edit"
												action="#{fceDetail.editFcePermitConditionSelection}"
												disabled="#{fceDetail.inspectionReadOnly || fceDetail.permitSelectionEditable}"
												rendered="#{!fceDetail.permitConditionsSelectionEditable}" />
											<af:commandButton id="savePermitConditions" text="Save"
												rendered="#{fceDetail.permitConditionsSelectionEditable}"
												action="#{fceDetail.saveFcePermitConditionSelection}" />
											<af:commandButton text="Cancel"
												action="#{fceDetail.cancelFcePermitConditionSelection}"
												immediate="true"
												rendered="#{fceDetail.permitConditionsSelectionEditable}" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</af:panelBorder>
							</h:panelGrid>

						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
			<%-- hidden controls for navigation to compliance report from popup --%>
			<%@ include file="../util/hiddenControls.jsp"%>
		</af:form>

	</af:document>
</f:view>
