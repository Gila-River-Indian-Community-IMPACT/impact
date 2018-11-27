<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<h:panelGrid border="1">
	<af:panelBorder>

		<f:facet name="top">
			<f:subview id="facilityHeader">
				<jsp:include page="comFacilityHeader2.jsp" />
			</f:subview>
		</f:facet>

		<f:facet name="right">
			<h:panelGrid columns="1" border="1" width="950"
				style="margin-left:auto;margin-right:auto;">
				<af:panelGroup layout="vertical">
					<af:objectSpacer height="10" />
					<af:panelForm>
						<af:table value="#{facilityProfile.facilityOwnersWrapper}"
							bandingInterval="1" banding="row" var="owner" width="98%"
							id="OwnersTab"
							binding="#{facilityProfile.facilityOwnersWrapper.table}">
							<af:column sortProperty="c02" sortable="true" formatType="text"
								headerText="Company ID">
								<af:commandLink text="#{owner.company.cmpId}"
									rendered="#{facilityProfile.internalApp}"
									id="viewCompany"
									returnListener="#{facilityProfile.dialogDone}"
									action="#{companyProfile.submitProfile}">
									<t:updateActionListener property="#{companyProfile.cmpId}"
										value="#{owner.company.cmpId}" />
									<t:updateActionListener
										property="#{menuItem_companyProfile.disabled}" value="false" />
								</af:commandLink>

								<af:commandLink text="#{owner.company.cmpId}" useWindow="true"
									inlineStyle="padding:5px;"
									rendered="#{facilityProfile.portalApp}"
									disabled="#{facilityProfile.readOnlyUser || myTasks.fromHomeContact}"
									returnListener="#{facilityProfile.dialogDone}"
									action="#{companyProfile.goToProfile}">
									<t:updateActionListener property="#{companyProfile.cmpId}"
										value="#{owner.company.cmpId}" />
								</af:commandLink>
								<af:outputText value="#{owner.company.cmpId}" rendered="#{facilityProfile.publicApp}"/>
							</af:column>
							<af:column sortProperty="c02" sortable="true" formatType="text"
								headerText="Company Name">
								<af:outputText value="#{owner.company.name}" />
							</af:column>

							<af:column sortProperty="c06" sortable="true" formatType="text"
								headerText="Start Date">
								<af:selectInputDate value="#{owner.startDate}" readOnly="true" />
							</af:column>
							<af:column sortProperty="c07" sortable="true" formatType="text"
								headerText="End Date">
								<af:selectInputDate value="#{owner.endDate}" readOnly="true" />
							</af:column>
							<f:facet name="footer">
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton actionListener="#{tableExporter.printTable}"
											onclick="#{tableExporter.onClickScript}"
											text="Printable view" />
										<af:commandButton actionListener="#{tableExporter.excelTable}"
											onclick="#{tableExporter.onClickScript}"
											text="Export to excel" />

									</af:panelButtonBar>
								</afh:rowLayout>
							</f:facet>
						</af:table>
					</af:panelForm>
					<afh:rowLayout halign="center"
						rendered="#{!facilityProfile.readOnlyUser && (contactDetail.dapcUser || facilityProfile.editStaging)}">
						<af:panelButtonBar>
							<af:commandButton text="Change Facility Ownership"
								disabled="#{facilityProfile.readOnlyUser}"
								action="#{bulkOperationsCatalog.setBulkOpToBOPS}">
								<t:updateActionListener
									property="#{bulkOperationsCatalog.facOwnerChange}" value="true" />
								<t:updateActionListener
									property="#{bulkOperationsCatalog.facilityId}"
									value="#{facilityProfile.facility.facilityId}" />

							</af:commandButton>
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelGroup>
			</h:panelGrid>
		</f:facet>

	</af:panelBorder>
</h:panelGrid>