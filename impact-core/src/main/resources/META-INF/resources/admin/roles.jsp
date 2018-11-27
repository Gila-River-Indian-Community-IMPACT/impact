<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Role Catalog">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Role Catalog">
				<%@ include file="../util/header.jsp"%>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1">
						<af:panelBorder>
							<f:facet name="left">
								<h:panelGrid columns="1" width="250">
									<t:tree2 id="roles" value="#{roleTree.treeData}" var="node"
										varNodeToggler="t" clientSideToggle="false">
										<f:facet name="root">
											<h:panelGroup>
												<af:commandMenuItem>
													<t:graphicImage value="/images/catalog.gif" border="0" />
													<t:outputText value="#{node.description}"
														style="color:#FFFFFF; background-color:#000000"
														rendered="#{roleTree.current == node.identifier}" />
													<t:outputText value="#{node.description}"
														rendered="#{roleTree.current != node.identifier}" />
													<t:updateActionListener
														property="#{roleTree.selectedTreeNode}" value="#{node}" />
													<t:updateActionListener property="#{roleTree.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="role">
											<h:panelGroup>
												<af:commandMenuItem>
													<t:graphicImage value="/images/user.gif" border="0" />
													<t:outputText value="#{node.description}"
														style="color:#FFFFFF; background-color:#000000"
														rendered="#{roleTree.current == node.identifier}" />
													<t:outputText value="#{node.description}"
														rendered="#{roleTree.current != node.identifier}" />
													<t:updateActionListener
														property="#{roleTree.selectedTreeNode}" value="#{node}" />
													<t:updateActionListener property="#{roleTree.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
										<f:facet name="useCase">
											<h:panelGroup>
												<af:commandMenuItem>
													<t:graphicImage value="/images/process.gif" border="0" />
													<t:outputText value="#{node.description}"
														style="color:#FFFFFF; background-color:#000000"
														rendered="#{roleTree.current == node.identifier}" />
													<t:outputText value="#{node.description}"
														rendered="#{roleTree.current != node.identifier}" />
													<t:updateActionListener
														property="#{roleTree.selectedTreeNode}" value="#{node}" />
													<t:updateActionListener property="#{roleTree.current}"
														value="#{node.identifier}" />
												</af:commandMenuItem>
											</h:panelGroup>
										</f:facet>
									</t:tree2>
								</h:panelGrid>
							</f:facet>

							<f:facet name="innerLeft">
								<h:panelGrid columns="1" border="1" width="400">
									<af:panelGroup layout="vertical"
										rendered="#{roleTree.selectedTreeNode.type == 'root' }">
										<af:panelHeader text="Roles" />
										<af:panelForm>
											<af:inputText label="Role Name:"
												value="#{roleTree.role.securityGroupName}"
												rendered="#{roleTree.newRole}" />
											<afh:rowLayout halign="center">
												<af:panelButtonBar>
													<af:commandButton text="Add Role"
														action="#{roleTree.addNewRole}"
														rendered="#{!roleTree.newRole}" />
													<af:commandButton text="Save Role"
														action="#{roleTree.saveRole}"
														rendered="#{roleTree.newRole}" />
													<af:commandButton text="Cancel" action="#{roleTree.reset}"
														rendered="#{roleTree.newRole}" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</af:panelForm>
									</af:panelGroup>

									<af:panelGroup layout="vertical"
										rendered="#{roleTree.selectedTreeNode.type == 'role' }">
										<af:panelHeader text="Role Information" />
										<af:panelForm>
											<af:inputText label="Role Name:"
												value="#{roleTree.role.securityGroupName}"
												disabled="#{!roleTree.newRole}" />
											<af:selectManyShuttle leadingHeader="All UseCases"
												trailingHeader="Role's Current UseCases" size="15"
												value="#{roleTree.currentRoleUseCases}"
												disabled="#{!roleTree.roleEditable}">
												<f:selectItems value="#{roleTree.allUseCases}" />
											</af:selectManyShuttle>
											<afh:rowLayout halign="center">
												<af:panelButtonBar>
													<af:commandButton text="Delete Role"
														action="#{roleTree.confirm}" partialSubmit="true"
														useWindow="true" windowWidth="400" windowHeight="100"
														returnListener="#{roleTree.confirmReturned}"
														rendered="#{!roleTree.roleEditable}" />
													<af:commandButton text="Clone Role"
														action="#{roleTree.cloneRole}"
														rendered="#{!roleTree.roleEditable}" />
													<af:commandButton text="Edit" action="#{roleTree.editRole}"
														rendered="#{!roleTree.roleEditable}" />
													<af:commandButton text="Save" action="#{roleTree.saveRole}"
														rendered="#{roleTree.roleEditable}" />
													<af:commandButton text="Cancel" action="#{roleTree.reset}"
														rendered="#{roleTree.roleEditable}" />
												</af:panelButtonBar>
											</afh:rowLayout>
										</af:panelForm>
									</af:panelGroup>

									<af:panelGroup layout="vertical"
										rendered="#{roleTree.selectedTreeNode.type == 'useCase' }">
										<af:panelHeader text="UseCase Information" />
										<af:panelForm>
											<af:outputFormatted
												value="<b>UseCase:</b> #{roleTree.useCase.useCase}" />
											<af:outputFormatted
												value="<b>UseCase Name:</b> #{roleTree.useCase.useCaseName}" />
										</af:panelForm>
									</af:panelGroup>
								</h:panelGrid>
							</f:facet>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
