<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Attachments">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page id="ThePage" var="foo" value="#{menuModel.model}"
				title="Attachments">
				<f:facet name="messages">
					<af:messages />
				</f:facet>
				<f:facet name="branding">
					<af:objectImage source="/images/stars2.png" />
				</f:facet>
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						disabled="#{foo.disabled}" type="#{foo.type}"
						rendered="#{foo.rendered}"
						onclick="if (#{permitDetail.editMode}) { alert('Please save or discard your changes'); return false; }" />
				</f:facet>

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
						<h:panelGrid columns="1" border="1" width="980">
							<afh:rowLayout halign="center">
								<af:table value="#{permitDetail.attachments}" id="AttachTab"
									width="980" var="doc" emptyText=" "
									partialTriggers="AttachTab:UploadDocButton AttachTab:EditDocButton">

									<af:column headerText="Description">
										<af:goLink destination="#{doc.uRL}" text="#{doc.description}" />
									</af:column>

									<af:column headerText="Last modified by">
										<af:selectOneChoice readOnly="true"
											value="#{doc.lastModifiedBy}">
											<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
										</af:selectOneChoice>
									</af:column>

									<af:column headerText="Last modified date">
										<af:selectInputDate readOnly="true"
											value="#{doc.lastModifiedTS}" />
									</af:column>

									<af:column headerText="Operation">
										<af:switcher
											facetName="#{permitDetail.editMode? 'edit': 'view'}">
											<f:facet name="edit">
												<af:commandButton text="Edit" id="EditDocButton"
													useWindow="true" windowWidth="500" windowHeight="300"
													returnListener="#{permitDetail.docDialogDone}"
													action="#{permitDetail.startEditDoc}" />
											</f:facet>
											<f:facet name="view">
												<af:commandButton text="Detail" useWindow="true"
													windowWidth="500" windowHeight="300"
													returnListener="#{permitDetail.docDialogDone}"
													action="#{permitDetail.startViewDoc}" />
											</f:facet>
										</af:switcher>
									</af:column>

									<f:facet name="footer">
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton text="Upload" id="UploadDocButton"
													useWindow="true" windowWidth="500" windowHeight="300"
													returnListener="#{permitDetail.docDialogDone}"
													action="#{permitDetail.startUploadAttachDoc}" />
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
							</afh:rowLayout>
						</h:panelGrid>
						<%
							/* Attachments end */
						%>
						<%
							/* Content end */
						%>

					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>
