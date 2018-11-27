<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="IMPACT Home">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:inputHidden value="#{myTasks.pageRedirect}" />
			<af:inputHidden value="#{submitTask.logUserOff}"
				rendered="#{!facilityProfile.internalApp}" />
			<af:page var="foo" value="#{menuModel.model}" title="IMPACT Home">
				<jsp:include flush="true" page="util/header.jsp" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="950" rendered="#{myTasks.homePage}">
						<af:panelBorder>
							<af:panelHeader text="Facility Information" size="0" />
							<af:panelForm rows="4" maxColumns="3" width="950"
								rendered="#{myTasks.facility != null}">
								<af:inputText label="Facility ID:" readOnly="True"
									value="#{myTasks.facility.facilityId}" />
								<af:inputText label="Facility Type:" readOnly="True"
									value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty myTasks.facility.facilityTypeCd ? '' : myTasks.facility.facilityTypeCd)]}" />
								<af:inputText label="Physical Address:" readOnly="true"
									value="#{myTasks.facility.phyAddr.addressLine1}" />
								<af:panelLabelAndMessage for="googleLocation" label="Lat/Long:">
									<af:goLink text="#{myTasks.facility.phyAddr.latlong}"
										targetFrame="_new" id="googleLocation"
										rendered="#{not empty myTasks.facility.googleMapsURL}"
										destination="#{myTasks.facility.googleMapsURL}"
										shortDesc="Clicking this will open Google Maps in a separate tab or window." />
								</af:panelLabelAndMessage>
								<af:inputText label="Facility Name:" readOnly="True"
									value="#{myTasks.facility.name}" />
								<af:inputText label="Company Name:" readOnly="True"
									value="#{myTasks.facility.owner.company.name}" />
								<af:inputText label="City:" readOnly="true"
									value="#{myTasks.facility.phyAddr.cityName}">
								</af:inputText>
								<af:inputText label="PLSS:" readOnly="true"
									value="#{myTasks.facility.phyAddr.plss}" />

								<af:selectOneChoice label="District:" readOnly="true" rendered="#{infraDefs.districtVisible}"
									value="#{myTasks.facility.phyAddr.districtCd}">
									<f:selectItems value="#{infraDefs.districts}" />
								</af:selectOneChoice>
								<%-- This is to make the page Layout unchanged with District not rendered --%>
								<af:inputHidden id="districtHidden" rendered="#{!infraDefs.districtVisible}"/>
								
								<af:selectOneChoice label="County:" readOnly="true"
									value="#{myTasks.facility.phyAddr.countyCd}">
									<f:selectItems value="#{infraDefs.counties}" />
								</af:selectOneChoice>
							</af:panelForm>

							<af:objectSpacer width="100%" height="5" />
							<af:showDetailHeader text="In Progress Tasks" disclosed="true">
								<af:panelForm rows="2">
									<af:table value="#{myTasks.tasks}" bandingInterval="2"
										binding="#{myTasks.tasksTable}" banding="row" var="task"
										width="98%">
										<f:facet name="selection">
											<af:tableSelectOne />
										</f:facet>
										<af:column sortProperty="taskTypeDescription" sortable="true"
											headerText="Task Type">
											<af:outputText value="#{task.taskTypeDescription}" />
										</af:column>
										<af:column sortProperty="taskDescription" sortable="true"
											headerText="Task Description">
											<af:commandLink text="#{task.taskDescription}"
												action="#{myTasks.submitTask}"
												inlineStyle="white-space: nowrap;">
												<t:updateActionListener value="#{task.taskId}"
													property="#{myTasks.taskId}" />
											</af:commandLink>
										</af:column>
										<af:column sortProperty="dependentTaskDescription" sortable="true"
											headerText="Dependent on Task" width="10%">
											<af:commandLink text="#{task.dependentTaskDescription}"
												rendered="#{task.dependent}" action="#{myTasks.submitTask}"
												inlineStyle="white-space: nowrap;">
												<t:updateActionListener value="#{task.dependentTaskId}"
													property="#{myTasks.taskId}" />
											</af:commandLink>
											<af:outputText value="N/A" rendered="#{!task.dependent}" />
										</af:column>
										<af:column sortProperty="createDate" sortable="true"
											headerText="Created Date">
											<af:selectInputDate readOnly="true"
												value="#{task.createDate}" />
										</af:column>
										<af:column sortProperty="userName" sortable="true"
											headerText="User Name">
											<af:outputText value="#{task.userName}" />
										</af:column>
										<f:facet name="footer">
											<afh:rowLayout halign="center">
												<af:panelButtonBar>
													<af:commandButton text="Delete selected task(s)"
														action="#{myTasks.deletePopup}" partialSubmit="true"
														useWindow="true" windowWidth="700" windowHeight="400"
														returnListener="#{myTasks.confirmReturned}" />
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
								<af:objectSpacer width="100%" height="5" />
							</af:showDetailHeader>
							<af:panelGroup layout="vertical"
								rendered="#{myTasks.facility != null}">
								<af:panelHeader text="New Tasks" />
								<afh:tableLayout width="350" borderWidth="0" cellSpacing="0">
									<afh:rowLayout halign="center">
										<af:outputFormatted
											value="Select from the lists below to create a new task" />
									</afh:rowLayout>
								</afh:tableLayout>
								<af:panelForm rows="2" maxColumns="2" labelWidth="30%" width="800px">
									<af:panelHeader text="Facility Management" size="2">
										<af:panelForm>
											<af:iterator value="#{myTasks.facilityMenuItems}" var="menuItem">
												<af:panelButtonBar inlineStyle="margin-bottom:5px;">
													<af:commandButton text="#{menuItem.title}"
														disabled="#{menuItem.disabled}"
														rendered="#{!menuItem.popup}" action="#{menuItem.outcome}"
														shortDesc="#{menuItem.explainText}"
														inlineStyle="white-space: nowrap;">
													</af:commandButton>
													<af:commandButton text="#{menuItem.title}"
														disabled="#{menuItem.disabled}"
														rendered="#{menuItem.popup}" action="#{menuItem.outcome}"
														shortDesc="#{menuItem.explainText}" useWindow="true"
														windowWidth="#{menuItem.width}"
														windowHeight="#{menuItem.height}"
														inlineStyle="white-space: nowrap;">
													</af:commandButton>
												</af:panelButtonBar>
											</af:iterator>
										</af:panelForm>
									</af:panelHeader>
									<af:panelHeader text="Emissions Reporting" size="2">
										<af:panelForm>
											<af:iterator value="#{myTasks.emissionsMenuItems}" var="menuItem">
												<af:panelButtonBar inlineStyle="margin-bottom:5px;">
													<af:commandButton text="#{menuItem.title}"
														disabled="#{menuItem.disabled || !myTasks.impactFullEnabled}"
														rendered="#{!menuItem.popup}" action="#{menuItem.outcome}"
														shortDesc="#{menuItem.explainText}"
														inlineStyle="white-space: nowrap;">
													</af:commandButton>
													<af:commandButton text="#{menuItem.title}"
														disabled="#{menuItem.disabled || !myTasks.impactFullEnabled}"
														rendered="#{menuItem.popup}" action="#{menuItem.outcome}"
														shortDesc="#{menuItem.explainText}" useWindow="true"
														windowWidth="#{menuItem.width}"
														windowHeight="#{menuItem.height}"
														inlineStyle="white-space: nowrap;">
													</af:commandButton>
												</af:panelButtonBar>
											</af:iterator>
										</af:panelForm>
									</af:panelHeader>
									<af:panelHeader text="Permitting" size="2" rendered="false">
										<af:panelForm>
											<af:iterator value="#{myTasks.permitMenuItems}" var="menuItem">
												<af:panelButtonBar inlineStyle="margin-bottom:5px;">
													<af:commandButton text="#{menuItem.title}"
														disabled="#{menuItem.disabled}"
														rendered="#{!menuItem.popup}" action="#{menuItem.outcome}"
														shortDesc="#{menuItem.explainText}"
														inlineStyle="white-space: nowrap;">
													</af:commandButton>
													<af:commandButton text="#{menuItem.title}"
														disabled="#{menuItem.disabled}"
														rendered="#{menuItem.popup}" action="#{menuItem.outcome}"
														shortDesc="#{menuItem.explainText}" useWindow="true"
														windowWidth="#{menuItem.width}"
														windowHeight="#{menuItem.height}"
														inlineStyle="white-space: nowrap;">
													</af:commandButton>
												</af:panelButtonBar>
											</af:iterator>
										</af:panelForm>
									</af:panelHeader>
									<af:panelHeader text="Compliance Reporting" size="2" rendered="false">
										<af:panelForm>
											<af:iterator value="#{myTasks.complianceMenuItems}" var="menuItem">
												<af:panelButtonBar inlineStyle="margin-bottom:5px;">
													<af:commandButton text="#{menuItem.title}"
														disabled="#{menuItem.disabled || !myTasks.impactFullEnabled}"
														rendered="#{!menuItem.popup}" action="#{menuItem.outcome}"
														shortDesc="#{menuItem.explainText}"
														inlineStyle="white-space: nowrap;">
													</af:commandButton>
													<af:commandButton text="#{menuItem.title}"
														disabled="#{menuItem.disabled || !myTasks.impactFullEnabled}"
														rendered="#{menuItem.popup}" action="#{menuItem.outcome}"
														shortDesc="#{menuItem.explainText}" useWindow="true"
														windowWidth="#{menuItem.width}"
														windowHeight="#{menuItem.height}"
														inlineStyle="white-space: nowrap;">
													</af:commandButton>
												</af:panelButtonBar>
											</af:iterator>
										</af:panelForm>
									</af:panelHeader>
								</af:panelForm>
								<af:panelForm rows="2" maxColumns="2" labelWidth="30%" width="800px" rendered="false">
									<af:panelHeader text="Ambient Monitoring" size="2" rendered="#{myTasks.monitorGroupId != null}">
										<af:panelForm>
											<af:iterator value="#{myTasks.monitoringMenuItems}" var="menuItem">
												<af:panelButtonBar inlineStyle="margin-bottom:5px;">
													<af:commandButton text="#{menuItem.title}"
														disabled="#{menuItem.disabled || !myTasks.impactFullEnabled}"
														rendered="#{!menuItem.popup}" action="#{menuItem.outcome}"
														shortDesc="#{menuItem.explainText}"
														inlineStyle="white-space: nowrap;">
													</af:commandButton>
													<af:commandButton text="#{menuItem.title}"
														disabled="#{menuItem.disabled || !myTasks.impactFullEnabled}"
														rendered="#{menuItem.popup}" action="#{menuItem.outcome}"
														shortDesc="#{menuItem.explainText}" useWindow="true"
														windowWidth="#{menuItem.width}"
														windowHeight="#{menuItem.height}"
														inlineStyle="white-space: nowrap;">
													</af:commandButton>
												</af:panelButtonBar>
											</af:iterator>
										</af:panelForm>
									</af:panelHeader>
								</af:panelForm>
							</af:panelGroup>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
