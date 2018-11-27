<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:table id="DocTab" emptyText=" " var="doc" width="98%"
	value="#{reportProfile.attachments}"
	partialTriggers="DocTab:AddDocButton">
	<af:column headerText="Attachment ID">
		<af:commandLink id="attachmentIdLink" text="#{doc.emissionsDocId}"
			useWindow="true" windowWidth="900" windowHeight="600"
			returnListener="#{reportProfile.docDialogDone}"
			action="#{reportProfile.startEditDoc}"
			shortDesc="Click to Delete the attachment or to Edit the attachment description">
			<t:updateActionListener property="#{reportProfile.deleteDocAllowed}"
				value="true" />
		</af:commandLink>
	</af:column>
	<af:column headerText="Attachment Type">
		<af:switcher defaultFacet="hasPublicDoc"
			facetName="#{doc.tradeSecretOnly ? 'tsOnly': 'hasPublicDoc'}">
			<f:facet name="hasPublicDoc">
				<af:goLink id="attachmentTypeLink" disabled="#{reportProfile.editable}"
					text="#{facilityReference.emissionsAttachmentTypeDefs.itemDesc[doc.emissionsDocumentTypeCD]}"
					targetFrame="_blank" destination="#{doc.publicDoc.docURL}"
					shortDesc="Download public document (#{(empty doc.publicDoc) ? '' : doc.publicDoc.extension})" />
			</f:facet>
			<f:facet name="tsOnly">
				<af:outputText
					value="#{facilityReference.emissionsAttachmentTypeDefs.itemDesc[doc.emissionsDocumentTypeCD]}" />
			</f:facet>
		</af:switcher>
	</af:column>
	<af:column headerText="Description">
		<af:outputText id="docDescriptionText" value="#{doc.description}" />
	</af:column>
	<af:column headerText="Trade Secret Document"
		rendered="#{reportProfile.tradeSecretVisible &&  !reportProfile.publicReadOnlyUser}" formatType="text">
		<af:switcher defaultFacet="noTS"
			facetName="#{(doc.tradeSecret && doc.tradeSecretAllowed && !reportProfile.publicReadOnlyUser)? 'TS': 'noTS'}">
			<f:facet name="noTS">
				<af:outputText value="None Provided" />
			</f:facet>
			<f:facet name="TS">
			  <af:switcher defaultFacet="downloadTSDoc" facetName="#{(reportProfile.internalApp)? 'downloadTSDoc' : 'noDownloadTSDoc'}">
			    <f:facet name="noDownloadTSDoc">
				  <af:goLink
					id="tradeSecretDocDownloadLink" text="Download" disabled="#{reportProfile.editable}" 
					targetFrame="_blank" destination="#{doc.tradeSecretDoc.docURL}"
					shortDesc="Download trade secret document (#{(empty doc.tradeSecretDoc) ? '' : doc.tradeSecretDoc.extension})" />
			    </f:facet>
				<f:facet name="downloadTSDoc">
			        <af:commandLink
				      text="Download" action="#{reportProfile.startDownloadTSDoc}"
				      disabled="#{reportProfile.editable}" useWindow="true" windowWidth="800" windowHeight="400"
				      shortDesc="Download trade secret document (#{(empty doc.tradeSecretDoc) ? '' : doc.tradeSecretDoc.extension})">
			        </af:commandLink>
				</f:facet>
			  </af:switcher>
			</f:facet>
		</af:switcher>
	</af:column>
	<af:column headerText="Trade Secret Justification" rendered="#{reportProfile.tradeSecretVisible &&  !reportProfile.publicReadOnlyUser}" formatType="text">
		<af:switcher defaultFacet="noJustification"
			facetName="#{(empty doc.tradeSecretReason || !doc.tradeSecretAllowed || reportProfile.publicReadOnlyUser)? 'noJustification': 'Justification'}">
			<f:facet name="noJustification">
				<af:switcher defaultFacet="noTSDoc"
					facetName="#{(doc.tradeSecret && doc.tradeSecretAllowed  && !reportProfile.publicReadOnlyUser)? 'TSDoc' : 'noTSDoc'}">
					<f:facet name="TSDoc">
						<af:commandLink text="Add Justification"
							action="#{reportProfile.startEditDoc}" 
							returnListener="#{reportProfile.docDialogDone}" useWindow="true"
							windowWidth="900" windowHeight="600"
							shortDesc="Click to Add Trade Secret Justification">
							<t:updateActionListener	property="#{reportProfile.deleteDocAllowed}" value="false" />
						</af:commandLink>
					</f:facet>
					<f:facet name="noTSDoc">
						<af:outputText value="N/A" />
					</f:facet>
				</af:switcher>
			</f:facet>
			<f:facet name="Justification">
				<af:switcher defaultFacet="viewJustification"
					facetName="#{reportProfile.tradeSecretVisible && reportProfile.okToEditAttachment ? 'editJustification' : 'viewJustification'}">
					<f:facet name="viewJustification">
						<af:commandLink text="View Justification"
							action="#{reportProfile.startEditDoc}"
							returnListener="#{reportProfile.docDialogDone}" useWindow="true"
							windowWidth="900" windowHeight="600"
							shortDesc="Click to View Trade Secret Justification">
							<t:updateActionListener	property="#{reportProfile.deleteDocAllowed}" value="false" />
						</af:commandLink>
					</f:facet>
					<f:facet name="editJustification">
						<af:commandLink text="Edit/View Justification"
							action="#{reportProfile.startEditDoc}"
							returnListener="#{reportProfile.docDialogDone}" useWindow="true"
							windowWidth="900" windowHeight="600"
							shortDesc="Click to Edit/View Trade Secret Justification">
							<t:updateActionListener	property="#{reportProfile.deleteDocAllowed}" value="false" />
						</af:commandLink>
					</f:facet>
				</af:switcher>
			</f:facet>
		</af:switcher>
	</af:column>
	<af:column headerText="Uploaded By">
		<af:selectOneChoice readOnly="true" value="#{doc.lastModifiedBy}">
			<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
		</af:selectOneChoice>
	</af:column>
	<af:column headerText="Upload Date">
		<af:selectInputDate readOnly="true" id="uploadDateText" value="#{doc.uploadDate}" />
	</af:column>
	<f:facet name="footer">
		<afh:rowLayout halign="center">
			<af:panelButtonBar>
				<af:commandButton text="Add" id="AddDocButton"
					rendered="#{!reportProfile.readOnlyUser && reportProfile.internalEditable}" useWindow="true"
					windowWidth="900" windowHeight="600"
					returnListener="#{reportProfile.docDialogDone}"
					action="#{reportProfile.startAddDoc}" />
				<af:commandButton actionListener="#{tableExporter.printTable}"
					onclick="#{tableExporter.onClickScript}" text="Printable view" />
				<af:commandButton actionListener="#{tableExporter.excelTable}"
					onclick="#{tableExporter.onClickScript}" text="Export to excel" />
			</af:panelButtonBar>
		</afh:rowLayout>
	</f:facet>
</af:table>
<af:outputText
	inlineStyle="font-size:75%;color:#666"
    rendered="#{reportProfile.deleteDocs}"
	value="To Delete the attachment, or to Edit attachment description, click in the Attachment ID column." />

