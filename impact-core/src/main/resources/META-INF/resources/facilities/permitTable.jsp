<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:showDetailHeader text="NSR Permits/Waivers" disclosed="true" id="permitsNSR">
	<af:table emptyText=" " value="#{permitSearch.permitsNSR}" width="99%"
		var="permit" bandingInterval="1" banding="row"
		rows="#{permitSearch.pageLimitShort}">
		<af:column headerText=" Permit
	Number" sortable="true"
			sortProperty="permitNumber" formatType="text" width="60px">
			<af:panelHorizontal valign="middle" halign="left">
				<af:outputText value="#{permit.permitNumber}" />
			</af:panelHorizontal>
		</af:column>
		<af:column headerText="Legacy Permit Number" sortable="true"
			sortProperty="legacyPermitNumber" formatType="text" width="70px">
			<af:panelHorizontal valign="middle" halign="left">
				<af:outputText value="#{permit.legacyPermitNumber}" />
			</af:panelHorizontal>
		</af:column>

		<af:column id="applicationNumbersNSR" headerText="Application Number(s)"
			formatType="text" noWrap="false" sortable="false">
			<af:panelHorizontal valign="middle" halign="left">
				<af:iterator value="#{permit.applications}" var="permitApplication">
					<af:commandLink text="#{permitApplication.applicationNumber}"
						action="homeApplicationDetail">
						<af:setActionListener from="#{permitApplication.applicationNumber}"
							to="#{applicationDetail.applicationNumber}" />
					</af:commandLink>
					<af:outputText value=" " />
				</af:iterator>
			</af:panelHorizontal>
		</af:column>

		<af:column sortable="true" sortProperty="permitActionType"
			formatType="text" headerText="Action">
			<af:panelHorizontal valign="middle" halign="left">
				<af:outputText
					value="#{permitReference.permitActionTypeDefs.itemDesc[(empty permit.permitActionType ? '' : permit.permitActionType)]}" />
			</af:panelHorizontal>
		</af:column>

		<af:column headerText="Reason(s)" sortable="true"
			sortProperty="primaryReasonCD" width="55px">
			<af:panelHorizontal valign="middle" halign="left">
				<af:selectManyCheckbox label="Reason(s) :" valign="top"
					readOnly="true" value="#{permit.permitReasonCDs}">
					<f:selectItems value="#{permitSearch.allPermitReasons}" />
				</af:selectManyCheckbox>
			</af:panelHorizontal>
		</af:column>

		<af:column headerText="Public Notice" bandingShade="light">
			<af:column headerText="Start Date" sortable="true"
				sortProperty="publicNoticePublishDate" formatType="text">
				<af:panelHorizontal valign="middle" halign="left">
					<af:selectInputDate readOnly="true"
						value="#{permit.publicNoticePublishDate}"
						rendered="#{permit.showPublicNoticeColumns}" />
				</af:panelHorizontal>
			</af:column>

			<af:column headerText="End Date" sortable="true"
				sortProperty="publicCommentEndDate" formatType="text">
				<af:panelHorizontal valign="middle" halign="left">
					<af:selectInputDate readOnly="true"
						value="#{permit.publicCommentEndDate}"
						rendered="#{permit.showPublicNoticeColumns}" />
				</af:panelHorizontal>
			</af:column>

			<af:column headerText="Public Notice Document" sortable="false"
				sortProperty="" formatType="text" width="82px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:goLink targetFrame="_blank"
						destination="#{permit.publicNoticeDoc.docURL}"
						rendered="#{permit.publicNoticeDoc != null && permit.showPublicNoticeColumns}"
						text="#{permit.publicNoticeDoc.description}" />
				</af:panelHorizontal>
			</af:column>

			<af:column headerText="Permit Analysis Document" sortable="false"
				sortProperty="" formatType="text" width="82px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:goLink targetFrame="_blank"
						destination="#{permit.draftIssuanceDoc.docURL}"
						rendered="#{permit.draftIssuanceDoc != null && permit.showPublicNoticeColumns}"
						text="#{permit.draftIssuanceDoc.description}" />
				</af:panelHorizontal>
			</af:column>
			<af:column id="commentsDocuments" headerText="Public Comments"
				formatType="text" noWrap="false" width="82px" sortable="false"
				sortProperty="">
				<af:panelHorizontal valign="middle" halign="left">
					<af:iterator value="#{permit.commentsDocuments}" var="document">
						<af:outputFormatted value="<br><br>" />
						<af:goLink targetFrame="_blank" destination="#{document.docURL}"
							rendered="#{permit.commentsDocuments != null && permit.showFinalColumns}"
							text="#{document.description}" />
					</af:iterator>
				</af:panelHorizontal>
			</af:column>
		</af:column>

		<af:column headerText="Final" bandingShade="light">

			<af:column headerText="Final Permit/Waiver Document" sortable="false"
				sortProperty="" formatType="text" width="82px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:goLink targetFrame="_blank"
						destination="#{permit.finalIssuanceDoc.docURL}"
						rendered="#{permit.finalIssuanceDoc != null && permit.showFinalColumns}"
						text="#{permit.finalIssuanceDoc.description}" />
				</af:panelHorizontal>
			</af:column>
			<af:column headerText="Final Issuance Date" sortable="true"
				sortProperty="finalIssueDate" formatType="text">
				<af:panelHorizontal valign="middle" halign="left">
					<af:selectInputDate readOnly="true"
						value="#{permit.finalIssueDate}"
						rendered="#{permit.showFinalColumns}" />
				</af:panelHorizontal>
			</af:column>

			<af:column headerText="Expiration Date" sortable="true"
				sortProperty="expirationDate" formatType="text" width="60px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:selectInputDate readOnly="true"
						value="#{permit.expirationDate}"
						rendered="#{permit.showFinalColumns}" />
				</af:panelHorizontal>
			</af:column>
		</af:column>

		<af:column headerText="Rescission Date" sortable="true"
			sortProperty="recissionDate" formatType="text" width="65px">
			<af:panelHorizontal valign="middle" halign="left">
				<af:selectInputDate readOnly="true" value="#{permit.recissionDate}" />
			</af:panelHorizontal>
		</af:column>

		<af:column sortable="true" sortProperty="description"
			formatType="text" headerText="Description" noWrap="false">
			<af:panelHorizontal valign="middle" halign="left">
				<af:outputText truncateAt="100" value="#{permit.description}"
					shortDesc="#{permit.description}" />
			</af:panelHorizontal>
		</af:column>

		<af:column sortProperty="" sortable="false">
			<f:facet name="header">
				<af:outputText value="Workflow Diagram Link" />
			</f:facet>
			<af:panelHorizontal valign="middle" halign="left">
				<af:commandLink action="#{permitSearch.loadWorkflowDiagram}">
					<af:outputText value="Workflow Diagram" />
					<af:setActionListener from="#{permit.permitNumber}"
						to="#{permitSearch.selectedPermitNumber}" />
				</af:commandLink>
			</af:panelHorizontal>
		</af:column>

		<f:facet name="footer">
			<afh:rowLayout halign="center">
				<af:panelButtonBar>
					<af:commandButton actionListener="#{tableExporter.printTable}"
						onclick="#{tableExporter.onClickScript}" text="Printable view" />
					<af:commandButton actionListener="#{tableExporter.excelTable}"
						onclick="#{tableExporter.onClickScript}" text="Export to excel" />
				</af:panelButtonBar>
			</afh:rowLayout>
		</f:facet>
	</af:table>
</af:showDetailHeader>

<af:showDetailHeader text="Title V Permits" disclosed="true"
	id="permitsTV">
	<af:table emptyText=" " value="#{permitSearch.permitsTV}" width="99%"
		var="permit" bandingInterval="1" banding="row"
		rows="#{permitSearch.pageLimitShort}">
		<af:column headerText=" Permit
	Number" sortable="true"
			sortProperty="permitNumber" formatType="text" width="60px">
			<af:panelHorizontal valign="middle" halign="left">
				<af:outputText value="#{permit.permitNumber}" />
			</af:panelHorizontal>
		</af:column>
		<af:column headerText="Legacy Permit Number" sortable="true"
			sortProperty="legacyPermitNumber" formatType="text" width="70px">
			<af:panelHorizontal valign="middle" halign="left">
				<af:outputText value="#{permit.legacyPermitNumber}" />
			</af:panelHorizontal>
		</af:column>

		<af:column id="applicationNumbersTV" headerText="Application Number(s)"
			formatType="text" noWrap="false" sortable="false">
			<af:panelHorizontal valign="middle" halign="left">
				<af:iterator value="#{permit.applications}" var="permitApplication">
					<af:commandLink text="#{permitApplication.applicationNumber}"
						action="homeApplicationDetail">
						<af:setActionListener from="#{permitApplication.applicationNumber}"
							to="#{applicationDetail.applicationNumber}" />
					</af:commandLink>
					<af:outputText value=" " />
				</af:iterator>
			</af:panelHorizontal>
		</af:column>

		<af:column headerText="Reason(s)" sortable="true"
			sortProperty="primaryReasonCD" width="55px">
			<af:panelHorizontal valign="middle" halign="left">
				<af:selectManyCheckbox label="Reason(s) :" valign="top"
					readOnly="true" value="#{permit.permitReasonCDs}">
					<f:selectItems value="#{permitSearch.allPermitReasons}" />
				</af:selectManyCheckbox>
			</af:panelHorizontal>
		</af:column>

		<af:column headerText="Public Notice" bandingShade="light">
			<af:column headerText="Start Date" sortable="true"
				sortProperty="publicNoticePublishDate" formatType="text">
				<af:panelHorizontal valign="middle" halign="left">
					<af:selectInputDate readOnly="true"
						value="#{permit.publicNoticePublishDate}"
						rendered="#{permit.showPublicNoticeColumns}" />
				</af:panelHorizontal>
			</af:column>

			<af:column headerText="End Date" sortable="true"
				sortProperty="publicCommentEndDate" formatType="text">
				<af:panelHorizontal valign="middle" halign="left">
					<af:selectInputDate readOnly="true"
						value="#{permit.publicCommentEndDate}"
						rendered="#{permit.showPublicNoticeColumns}" />
				</af:panelHorizontal>
			</af:column>

			<af:column headerText="Public Notice Document" sortable="false"
				sortProperty="" formatType="text" width="82px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:goLink targetFrame="_blank"
						destination="#{permit.publicNoticeDoc.docURL}"
						rendered="#{permit.publicNoticeDoc != null && permit.showPublicNoticeColumns}"
						text="#{permit.publicNoticeDoc.description}" />
				</af:panelHorizontal>
			</af:column>


			<af:column headerText="Draft Statement of Basis Document"
				sortable="false" sortProperty="" formatType="text" width="82px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:goLink targetFrame="_blank"
						destination="#{permit.draftStatementOfBasisDoc.docURL}"
						rendered="#{permit.draftStatementOfBasisDoc != null && permit.showPublicNoticeColumns}"
						text="#{permit.draftStatementOfBasisDoc.description}" />
				</af:panelHorizontal>
			</af:column>
			<af:column headerText="Draft Title V Permit Document" sortable="false"
				sortProperty="" formatType="text" width="82px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:goLink targetFrame="_blank"
						destination="#{permit.draftTitleVPermitDoc.docURL}"
						rendered="#{permit.draftTitleVPermitDoc != null && permit.showPublicNoticeColumns}"
						text="#{permit.draftTitleVPermitDoc.description}" />
				</af:panelHorizontal>
			</af:column>
			<af:column id="commentsDocuments2" headerText="Public Comments"
				formatType="text" noWrap="false" width="82px" sortable="false"
				sortProperty="">
				<af:panelHorizontal valign="middle" halign="left">
					<af:iterator value="#{permit.commentsDocuments}" var="document">
						<af:outputFormatted value="<br><br>" />
						<af:goLink targetFrame="_blank" destination="#{document.docURL}"
							rendered="#{permit.commentsDocuments != null && permit.showProposedColumns}"
							text="#{document.description}" />
					</af:iterator>
				</af:panelHorizontal>
			</af:column>
		</af:column>

		<af:column headerText="Proposed" bandingShade="light">

			<af:column headerText="Date Permit Sent to EPA" sortable="true"
				sortProperty="usepaPermitSentDate" formatType="text">
				<af:panelHorizontal valign="middle" halign="left">
					<af:selectInputDate readOnly="true"
						value="#{permit.usepaPermitSentDate}"
						rendered="#{permit.showProposedColumns}" />
				</af:panelHorizontal>
			</af:column>

			<af:column headerText="Proposed Statement of Basis Document"
				sortable="false" sortProperty="" formatType="text" width="82px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:goLink targetFrame="_blank"
						destination="#{permit.proposedStatementOfBasisDoc.docURL}"
						rendered="#{permit.proposedStatementOfBasisDoc != null && permit.showProposedColumns}"
						text="#{permit.proposedStatementOfBasisDoc.description}" />
				</af:panelHorizontal>
			</af:column>
			<af:column headerText="Proposed Title V Permit Document"
				sortable="false" sortProperty="" formatType="text" width="82px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:goLink targetFrame="_blank"
						destination="#{permit.ppIssuanceDoc.docURL}"
						rendered="#{permit.ppIssuanceDoc != null && permit.showProposedColumns}"
						text="#{permit.ppIssuanceDoc.description}" />
				</af:panelHorizontal>
			</af:column>
		</af:column>

		<af:column headerText="Final" bandingShade="light">
			<af:column headerText="Final Statement of Basis Document"
				sortable="false" sortProperty="" formatType="text" width="82px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:goLink targetFrame="_blank"
						destination="#{permit.finalStatementOfBasisDoc.docURL}"
						rendered="#{permit.finalStatementOfBasisDoc != null && permit.showFinalColumns}"
						text="#{permit.finalStatementOfBasisDoc.description}" />
				</af:panelHorizontal>
			</af:column>

			<af:column headerText="Final Title V Permit Document" sortable="false"
				sortProperty="" formatType="text" width="82px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:goLink targetFrame="_blank"
						destination="#{permit.finalIssuanceDoc.docURL}"
						rendered="#{permit.finalIssuanceDoc != null && permit.showFinalColumns}"
						text="#{permit.finalIssuanceDoc.description}" />
				</af:panelHorizontal>
			</af:column>
			<af:column headerText="Final Issuance Date" sortable="true"
				sortProperty="finalIssueDate" formatType="text">
				<af:panelHorizontal valign="middle" halign="left">
					<af:selectInputDate readOnly="true"
						value="#{permit.finalIssueDate}"
						rendered="#{permit.showFinalColumns}" />
				</af:panelHorizontal>
			</af:column>

			<af:column headerText="Permit Basis Date" sortable="true"
				sortProperty="permitBasisDate" formatType="text" width="55px">
				<af:panelHorizontal valign="middle" halign="left">
					<af:selectInputDate readOnly="true"
						value="#{permit.permitBasisDate}"
						rendered="#{permit.showFinalColumns}" />
				</af:panelHorizontal>
			</af:column>

			<af:column headerText="Expiration Date" sortable="true"
				sortProperty="expirationDate" formatType="text">
				<af:panelHorizontal valign="middle" halign="left">
					<af:selectInputDate readOnly="true"
						value="#{permit.expirationDate}"
						rendered="#{permit.showFinalColumns}" />
				</af:panelHorizontal>
			</af:column>
		</af:column>

		<af:column headerText="Rescission Date" sortable="true"
			sortProperty="recissionDate" formatType="text">
			<af:panelHorizontal valign="middle" halign="left">
				<af:selectInputDate readOnly="true" 
					value="#{permit.recissionDate}" />
			</af:panelHorizontal>
		</af:column>

		<af:column sortable="true" sortProperty="description"
			formatType="text" headerText="Description" noWrap="false">
			<af:panelHorizontal valign="middle" halign="left">
				<af:outputText truncateAt="100" value="#{permit.description}"
					shortDesc="#{permit.description}" />
			</af:panelHorizontal>
		</af:column>

		<af:column sortProperty="" sortable="false">
			<f:facet name="header">
				<af:outputText value="Workflow Diagram Link" />
			</f:facet>
			<af:panelHorizontal valign="middle" halign="left">
				<af:commandLink action="#{permitSearch.loadWorkflowDiagram}">
					<af:outputText value="Workflow Diagram" />
					<af:setActionListener from="#{permit.permitNumber}"
						to="#{permitSearch.selectedPermitNumber}" />
				</af:commandLink>
			</af:panelHorizontal>
		</af:column>

		<f:facet name="footer">
			<afh:rowLayout halign="center">
				<af:panelButtonBar>
					<af:commandButton actionListener="#{tableExporter.printTable}"
						onclick="#{tableExporter.onClickScript}" text="Printable view" />
					<af:commandButton actionListener="#{tableExporter.excelTable}"
						onclick="#{tableExporter.onClickScript}" text="Export to excel" />
				</af:panelButtonBar>
			</afh:rowLayout>
		</f:facet>
	</af:table>
</af:showDetailHeader>