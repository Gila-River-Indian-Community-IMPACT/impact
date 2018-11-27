<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<f:view>
	<af:document title="Notes">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Notes">

				<jsp:include page="header.jsp" />

				<jsp:useBean id="facilityProfile" scope="session"
					class="us.oh.state.epa.stars2.app.facility.FacilityProfile" />
				<jsp:setProperty name="facilityProfile" property="noteFromFacility"
					value="false" />

				<h:panelGrid border="1">
					<af:panelBorder>

						<f:facet name="top">
							<f:subview id="facilityHeader">
								<jsp:include page="comFacilityHeader3.jsp" />
							</f:subview>
						</f:facet>

						<f:facet name="right">
							<h:panelGrid columns="1" border="1" width="950">
								<af:panelGroup layout="vertical">
									<af:objectSpacer height="10" />
									<af:table value="#{facilityProfile.notesWrapper}"
										bandingInterval="1"
										binding="#{facilityProfile.notesWrapper.table}" id="NotesTab"
										partialTriggers="NotesTab:AddNoteButton" banding="row"
										width="98%" var="note" rows="#{facilityProfile.pageLimit}">
										<af:column sortProperty="c01" sortable="true"
											formatType="text" headerText="Note ID">
											<af:commandLink text="#{note.noteId}" id="viewNote"
												useWindow="true" windowWidth="650" windowHeight="300"
												returnListener="#{facilityProfile.dialogDone}"
												action="#{facilityProfile.startViewNote}">
												<t:updateActionListener
													property="#{facilityProfile.facilityNote}" value="#{note}" />
											</af:commandLink>
										</af:column>
										<af:column sortProperty="c02" sortable="true"
											formatType="text" headerText="Note">
											<af:outputText 
												truncateAt="90"	
												value="#{note.noteTxt}" 
												shortDesc="#{note.noteTxt}"/>
										</af:column>
										<af:column sortProperty="c03" sortable="true"
											formatType="text" headerText="User Name">
											<af:selectOneChoice value="#{note.userId}" readOnly="true">
												<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
											</af:selectOneChoice>
										</af:column>
										<af:column sortProperty="c04" sortable="true"
											formatType="text" headerText="Date">
											<af:selectInputDate value="#{note.dateEntered}"
												readOnly="true" />
										</af:column>
										<f:facet name="footer">
											<afh:rowLayout halign="center">
												<af:panelButtonBar>
													<af:commandButton text="Add Note" id="AddNoteButton"
														useWindow="true" windowWidth="650" windowHeight="300"
														disabled="#{facilityProfile.readOnlyUser}"
														returnListener="#{facilityProfile.dialogDone}"
														rendered="#{!facilityProfile.editable}"
														action="#{facilityProfile.startAddNote}">
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
								</af:panelGroup>
							</h:panelGrid>
						</f:facet>

					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>

