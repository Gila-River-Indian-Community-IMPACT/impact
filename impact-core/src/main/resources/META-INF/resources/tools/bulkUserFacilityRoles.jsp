<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Facilities - Bulk Operations">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<f:facet name="messages">
				<af:messages />
			</f:facet>

			<afh:rowLayout>
				<af:outputFormatted
					value="<br>**<b> #{bulkOperationsCatalog.bulkOperation.role.facilityRoleDsc} role change will affect multiple districts.</b>"
					rendered="#{bulkOperationsCatalog.bulkOperation.affectMultipleDolaas}"
					inlineStyle="color: orange; font-weight: bold;" />
			</afh:rowLayout>

			<afh:rowLayout halign="center">
				<h:panelGrid border="1" columns="1" width="400">
					<af:panelBorder>
						<af:panelHeader text="Change User Facility Role" size="0" />
						<af:panelForm>

							<af:selectOneChoice label="Originally assigned staff member: "
								value="#{bulkOperationsCatalog.staffId}"
								readOnly="true">
								<f:selectItems
									value="#{infraDefs.basicUsersDef.items[(empty bulkOperationsCatalog.staffId?0:bulkOperationsCatalog.staffId)]}" />
							</af:selectOneChoice>

							<af:selectOneChoice
								value="#{bulkOperationsCatalog.bulkOperation.userIdForRole}"
								readOnly="#{!bulkOperationsCatalog.bulkOperation.facilityRoleEditable}"
								label="New staff member for selected role(s) : ">
								<f:selectItems value="#{infraDefs.basicUsersDef.items[(empty bulkOperationsCatalog.bulkOperation.userIdForRole?0:bulkOperationsCatalog.bulkOperation.userIdForRole)]}" />
							</af:selectOneChoice>

							<af:objectSpacer height="10" />
							<afh:rowLayout halign="center">
								<af:panelButtonBar>

									<af:commandButton text="Apply"
										actionListener="#{bulkOperationsCatalog.applyFinalAction}" />
									<af:commandButton text="Cancel" immediate="true"
										actionListener="#{bulkOperationsCatalog.bulkOperationCancel}" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>
					</af:panelBorder>
				</h:panelGrid>
			</afh:rowLayout>

			<af:objectSpacer width="100%" height="15" />

			<h:panelGrid border="1"
				rendered="#{bulkOperationsCatalog.hasFacilitySearchResults}">
				<af:panelBorder>
					<af:showDetailHeader text="Selected Facility List" disclosed="true">
						<af:table value="#{bulkOperationsCatalog.selectedFacilities}"
							rows="#{bulkOperationsCatalog.pageLimit}" bandingInterval="1"
							banding="row" var="facility">

							<af:column sortProperty="#{facility.facilityId}" sortable="true"
								formatType="text" headerText="Facility ID">
								<af:outputText value="#{facility.facilityId}" />
							</af:column>

							<af:column sortProperty="#{facility.name}" sortable="true"
								formatType="text" headerText="Facility Name">
								<af:outputText value="#{facility.name}" />
							</af:column>

							<af:column sortable="false" formatType="text"
								headerText="Operating Status">
								<af:outputText
									value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facility.operatingStatusCd ? '' : facility.operatingStatusCd)]}" />
							</af:column>
							<af:column sortable="false" formatType="text"
								headerText="Reporting Category">
								<af:outputText
									value="#{facilityReference.emissionReportsDefs.itemDesc[(empty facility.reportingTypeCd ? '' : facility.reportingTypeCd)]}" />
							</af:column>
							<af:column sortable="false" formatType="text"
								headerText="Permitting Classification">
								<af:outputText
									value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
							</af:column>
							<af:column sortProperty="#{facility.permitStatusCd}"
								sortable="true" formatType="text" headerText="TV Permit Status">
								<af:selectOneChoice value="#{facility.permitStatusCd}"
									readOnly="true">
									<mu:selectItems value="#{facilityReference.permitStatusDefs}" />
								</af:selectOneChoice>
							</af:column>
							<af:column sortProperty="role" sortable="true" formatType="text"
								headerText="Facility Role">
								<af:selectOneChoice value="#{facility.roleCd}" readOnly="true">
									<f:selectItems value="#{bulkOperationsCatalog.facilityRoles}" />
								</af:selectOneChoice>
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
					</af:showDetailHeader>
				</af:panelBorder>
			</h:panelGrid>

		</af:form>
	</af:document>
</f:view>



