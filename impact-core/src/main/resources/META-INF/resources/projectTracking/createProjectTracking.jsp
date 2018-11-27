<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Create Project Tracking">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page id="createProject" var="foo" value="#{menuModel.model}"
				title="Create Project Tracking">
				<%@ include file="../util/header.jsp"%>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000px">
						<af:panelBorder>
							<af:panelGroup layout="vertical">
								<af:panelHeader text="Create Project" size="0" />

								<f:subview id="commonAttributes">
									<af:panelForm maxColumns="1" labelWidth="220px" width="98%">
										<af:inputText id="projectName" 
											label="Project Name:"
											value="#{createProject.projectName}"
											columns="100" maximumLength="250"
											showRequired="true" readOnly="false" />
									
										<af:inputText id="projectDescription" 
											label="Project Description:"
											value="#{createProject.projectDescription}"
											columns="100" rows="5" maximumLength="1000"
											showRequired="true" readOnly="false" />
											
										<af:selectOneChoice id="projectTypeCd"
											label="Project Type:"
											disabled="#{empty projectTrackingDetail.projectTypesByAppUsr}"
											value="#{createProject.projectTypeCd}"
											showRequired="true" readOnly="false">
											<f:selectItems
												value="#{projectTrackingDetail.projectTypesByAppUsr}" />
										</af:selectOneChoice>		
										
										<af:selectOneChoice id="projectStateCd"
											label="Project Status:"
											value="#{createProject.projectStateCd}"
											showRequired="true" readOnly="false">
											<f:selectItems
												value="#{projectTrackingReference.projectStateDefs.items[0]}" />
										</af:selectOneChoice>
									</af:panelForm>
								</f:subview>


								<af:objectSpacer height="25" />

								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton id="createBtn"
											disabled="#{projectTrackingDetail.readOnlyUser || empty projectTrackingDetail.projectTypesByAppUsr}"
											text="Create"
											shortDesc="#{empty projectTrackingDetail.projectTypesByAppUsr 
															? 'Insufficient privileges for creating project'
															: 'Create Project'}"
											action="#{createProject.create}" />
										<af:commandButton id="resetBtn"
											disabled="#{projectTrackingDetail.readOnlyUser}"
											text="Reset"
											action="#{createProject.reset}" />	
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelGroup>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>