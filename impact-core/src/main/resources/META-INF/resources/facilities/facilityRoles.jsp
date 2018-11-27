<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="User Roles">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="User Roles">

				<jsp:include page="header.jsp" />

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="facilityHeader">
								<jsp:include page="comFacilityHeader3.jsp" />
							</f:subview>
						</f:facet>
						<f:facet name="right">
							<h:panelGrid columns="1" border="1"
								style="margin-left:auto;margin-right:auto;">
								<af:panelGroup layout="vertical">
									<af:objectSpacer height="10" />
									<af:panelForm>
										<af:table value="#{facilityProfile.facRolesWrapper}"
											bandingInterval="1" width="98%"
											binding="#{facilityProfile.facRolesWrapper.table}"
											banding="row" var="facRole">
											
											<af:column sortProperty="facilityRoleDsc" sortable="true"
												formatType="text" headerText="Facility Roles" width="55%" noWrap="true">
												<af:outputText value="#{facRole.facilityRoleDsc}" />
											</af:column>
											<af:column sortProperty="c02" sortable="true"
												formatType="text" headerText="Staff" width="25%" noWrap="true">
												<af:selectOneChoice value="#{facRole.userId}"
													readOnly="#{!facilityProfile.editable}">
													<f:selectItems
														value="#{infraDefs.basicUsersDef.items[(empty facRole.userId?0:facRole.userId)]}" />
												</af:selectOneChoice>
											</af:column>
											<af:column sortProperty="c03" sortable="true" noWrap="true"
												formatType="text" headerText="Defined Workflow Tasks" width="20%">
												<af:commandLink useWindow="true" windowWidth="700"
													windowHeight="500" inlineStyle="padding-left:5px;"
													action="#{facilityProfile.displayRoleActivities}"
													text="Count(#{facRole.activityCount})" >
													<t:updateActionListener value="#{facRole.facilityRoleCd}" 
														property="#{facilityProfile.facilityRoleCdForActivity}" />
												</af:commandLink>
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
									<afh:rowLayout halign="center">
										<af:panelButtonBar>
											<af:commandButton text="Edit Roles"
												action="#{facilityProfile.editFacility}"
												disabled="#{!facilityProfile.facilityRolesUpdatable or facilityProfile.readOnlyUser}"
												rendered="#{!facilityProfile.editable}" />
											<af:commandButton text="Set Default Role Assignments"
												action="#{facilityProfile.confirmResetFacRoles}" useWindow="true"
												disabled="#{!facilityProfile.facilityRolesUpdatable or facilityProfile.readOnlyUser}"
												rendered="#{!facilityProfile.editable}" 
												windowWidth="600"
            									windowHeight="200"/>
											<af:commandButton text="Save"
												action="#{facilityProfile.saveNewFacilityRoles}"
												rendered="#{facilityProfile.editable}" />
											<af:commandButton text="Cancel"
												action="#{facilityProfile.cancelEdit}"
												rendered="#{facilityProfile.editable}" immediate="true" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</af:panelGroup>
							</h:panelGrid>
						</f:facet>
					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
		<f:verbatim><%@ include
				file="../scripts/facility-detail.js"%></f:verbatim>
	</af:document>
</f:view>
