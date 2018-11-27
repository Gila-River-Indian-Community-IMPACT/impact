<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:objectSpacer height="5"/>
<af:panelForm maxColumns="5" rows="1" partialTriggers="nsps part61NESHAP part63NESHAP">
	<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" id="acidRain" label="Title IV Acid Rain:"
		rendered="#{permitDetail.permit.permitType == 'TVPTO' }"
		readOnly="#{!permitDetail.editMode}"
    	value="#{permitDetail.permit.acidRain}" />
  	<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" id="cam" label="CAM:"
	    rendered="#{permitDetail.permit.permitType == 'TVPTO' }"
	    readOnly="#{!permitDetail.editMode}"
    	value="#{permitDetail.permit.cam}" />
    <af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" id="cem" label="CEM:"
		rendered="#{permitDetail.permit.permitType == 'NSR' }"
		readOnly="#{! permitDetail.editMode}"
		value="#{permitDetail.permit.cem}" />
	<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;"	id="nsps" label="NSPS:"
  		autoSubmit="TRUE" readOnly="#{! permitDetail.editMode}"
 		value="#{permitDetail.permit.nsps}" />	 	
  	<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" id="part61NESHAP" label="Part 61 NESHAP:"
    	autoSubmit="TRUE" readOnly="#{! permitDetail.editMode}"
    	value="#{permitDetail.permit.part61NESHAP}" />
  	<af:selectBooleanCheckbox inlineStyle="margin-left: 0px;" id="part63NESHAP" label="Part 63 NESHAP:"
    	autoSubmit="TRUE" readOnly="#{! permitDetail.editMode}"
    	value="#{permitDetail.permit.part63NESHAP}" />
	
	<f:facet name="footer">
		<af:panelHeader text="Federal Rules Applicability" size="4" id="fra"
			rendered="#{permitDetail.fraAllowed}">
			<afh:rowLayout halign="center">
				<t:div style="overflow:auto;width:680px">
					<af:objectSpacer height="5" />
					<af:table value="#{permitDetail.nspsSubparts}" bandingInterval="1"
						banding="row" var="nspsSubpart"
						rendered="#{permitDetail.permit.nsps}"
						binding="#{permitDetail.nspsSubpartsTable}" width="98%">
						<f:facet name="selection">
							<af:tableSelectMany rendered="#{permitDetail.editMode}" />
						</f:facet>
						<af:column sortProperty="value" sortable="true" formatType="text"
							headerText="NSPS Subpart">
							<af:selectOneChoice value="#{nspsSubpart.value}"
								readOnly="#{! permitDetail.editMode}">
								<f:selectItems value="#{permitReference.nspsSubpartDefs}" />
							</af:selectOneChoice>
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="left">
								<af:panelButtonBar>
									<af:commandButton text="Add Subpart"
										rendered="#{permitDetail.editMode}"
										actionListener="#{permitDetail.addSubpart}">
										<t:updateActionListener property="#{permitDetail.subparts}"
											value="#{permitDetail.nspsSubparts}" />
										<t:updateActionListener
											property="#{permitDetail.subpartsTable}"
											value="#{permitDetail.nspsSubpartsTable}" />
									</af:commandButton>
									<af:commandButton text="Delete Selected Subparts"
										rendered="#{permitDetail.editMode}"
										actionListener="#{permitDetail.deleteSubparts}">
										<t:updateActionListener property="#{permitDetail.subparts}"
											value="#{permitDetail.nspsSubparts}" />
										<t:updateActionListener
											property="#{permitDetail.subpartsTable}"
											value="#{permitDetail.nspsSubpartsTable}" />
									</af:commandButton>
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
					
					<af:objectSpacer height="5" />
					<af:table value="#{permitDetail.part61NESHAPSubparts}"
						bandingInterval="1" banding="row" var="part61NESHAPSubpart"
						rendered="#{permitDetail.permit.part61NESHAP}"
						binding="#{permitDetail.part61NESHAPSubpartsTable}" width="98%">
						<f:facet name="selection">
							<af:tableSelectMany rendered="#{permitDetail.editMode}" />
						</f:facet>
						<af:column sortProperty="value" sortable="true" formatType="text"
							headerText="Part 61 NESHAP Subpart">
							<af:selectOneChoice value="#{part61NESHAPSubpart.value}"
								readOnly="#{! permitDetail.editMode}">
								<f:selectItems value="#{permitReference.neshapsSubpartDefs}" />
							</af:selectOneChoice>
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="left">
								<af:panelButtonBar>
									<af:commandButton text="Add Subpart"
										rendered="#{permitDetail.editMode}"
										actionListener="#{permitDetail.addSubpart}">
										<t:updateActionListener property="#{permitDetail.subparts}"
											value="#{permitDetail.part61NESHAPSubparts}" />
										<t:updateActionListener
											property="#{permitDetail.subpartsTable}"
											value="#{permitDetail.part61NESHAPSubpartsTable}" />
									</af:commandButton>
									<af:commandButton text="Delete Selected Subparts"
										rendered="#{permitDetail.editMode}"
										actionListener="#{permitDetail.deleteSubparts}">
										<t:updateActionListener property="#{permitDetail.subparts}"
											value="#{permitDetail.part61NESHAPSubparts}" />
										<t:updateActionListener
											property="#{permitDetail.subpartsTable}"
											value="#{permitDetail.part61NESHAPSubpartsTable}" />
									</af:commandButton>
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
					
					<af:objectSpacer height="5" />
					<af:table value="#{permitDetail.part63NESHAPSubparts}"
						bandingInterval="1" banding="row" var="part63NESHAPSubpart"
						rendered="#{permitDetail.permit.part63NESHAP}"
						binding="#{permitDetail.part63NESHAPSubpartsTable}" width="98%">
						<f:facet name="selection">
							<af:tableSelectMany rendered="#{permitDetail.editMode}" />
						</f:facet>
						<af:column sortProperty="value" sortable="true" formatType="text"
							headerText="Part 63 NESHAP Subpart">
							<af:selectOneChoice value="#{part63NESHAPSubpart.value}"
								readOnly="#{! permitDetail.editMode}">
								<f:selectItems value="#{permitReference.mactSubpartDefs}" />
							</af:selectOneChoice>
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="left">
								<af:panelButtonBar>
									<af:commandButton text="Add Subpart"
										rendered="#{permitDetail.editMode}"
										actionListener="#{permitDetail.addSubpart}">
										<t:updateActionListener property="#{permitDetail.subparts}"
											value="#{permitDetail.part63NESHAPSubparts}" />
										<t:updateActionListener
											property="#{permitDetail.subpartsTable}"
											value="#{permitDetail.part63NESHAPSubpartsTable}" />
									</af:commandButton>
									<af:commandButton text="Delete Selected Subparts"
										rendered="#{permitDetail.editMode}"
										actionListener="#{permitDetail.deleteSubparts}">
										<t:updateActionListener property="#{permitDetail.subparts}"
											value="#{permitDetail.part63NESHAPSubparts}" />
										<t:updateActionListener
											property="#{permitDetail.subpartsTable}"
											value="#{permitDetail.part63NESHAPSubpartsTable}" />
									</af:commandButton>
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</t:div>
			</afh:rowLayout>
			<af:objectSpacer height="10" />
		</af:panelHeader>
	</f:facet>
</af:panelForm>
<%--
<afh:tableLayout width="100%">
	<afh:rowLayout>
		<afh:cellFormat width="50%" halign="left" valign="top"
			partialTriggers="psUploadButton psDeleteButton"
			rendered="#{permitDetail.permit.permitType != 'TVPTO' && permitDetail.permit.permitType != 'TIVPTO'}">
			<afh:rowLayout>
				<afh:cellFormat halign="right" valign="top" width="195">
					<af:inputText label="Permit Strategy " readOnly="true" />
					<af:inputText label="Summary Write-up :" readOnly="true" />
				</afh:cellFormat>
				<afh:cellFormat>
					<af:goLink targetFrame="_blank"
						rendered="#{permitDetail.docsMap[permitReference.permitStrategySummaryWriteupCD][permitReference.allFlag] != null}"
						destination="#{permitDetail.docsMap[permitReference.permitStrategySummaryWriteupCD][permitReference.allFlag].docURL}"
						text="#{permitDetail.docsMap[permitReference.permitStrategySummaryWriteupCD][permitReference.allFlag].description}" />
					<af:objectSpacer width="10" />
					<af:commandButton text="Generate" useWindow="true"
						windowWidth="500" windowHeight="300" id="psGenerateButton"
						rendered="#{permitDetail.editMode}"
						returnListener="#{permitDetail.docDialogDone}"
						action="#{permitDetail.generatePermitStrategyDoc}" />
					<af:commandButton text="Upload" useWindow="true" windowWidth="500"
						windowHeight="300" id="psUploadButton"
						rendered="#{permitDetail.editMode}"
						returnListener="#{permitDetail.docDialogDone}"
						action="#{permitDetail.uploadDoc}">
						<t:updateActionListener
							value="#{permitReference.permitStrategySummaryWriteupCD}"
							property="#{permitDetail.docTypeCD}" />
						<t:updateActionListener value="#{permitReference.allFlag}"
							property="#{permitDetail.issuanceStageFlag}" />
						<t:updateActionListener
							value="#{permitDetail.docsMap[permitReference.permitStrategySummaryWriteupCD][permitReference.allFlag]}"
							property="#{permitDetail.singleDoc}" />
					</af:commandButton>
					<af:commandButton text="Delete" useWindow="true"
						disabled="#{permitDetail.docsMap[permitReference.permitStrategySummaryWriteupCD][permitReference.allFlag] == null}"
						id="psDeleteButton" rendered="#{permitDetail.editMode}"
						returnListener="#{permitDetail.deleteDocFromMap}"
						action="#{confirmWindow.confirm}"
						windowWidth="#{confirmWindow.width}"
						windowHeight="#{confirmWindow.height}">
						<t:updateActionListener property="#{confirmWindow.type}"
							value="#{confirmWindow.yesNo}" />
						<t:updateActionListener property="#{confirmWindow.message}"
							value="Click Yes to confirm the deletion of #{permitDetail.docsMap[permitReference.permitStrategySummaryWriteupCD][permitReference.allFlag].description}" />
						<t:updateActionListener
							value="#{permitReference.permitStrategySummaryWriteupCD}"
							property="#{permitDetail.docTypeCD}" />
						<t:updateActionListener value="#{permitReference.allFlag}"
							property="#{permitDetail.issuanceStageFlag}" />
						<t:updateActionListener
							value="#{permitDetail.docsMap[permitReference.permitStrategySummaryWriteupCD][permitReference.allFlag]}"
							property="#{permitDetail.singleDoc}" />
					</af:commandButton>
				</afh:cellFormat>
			</afh:rowLayout>
		</afh:cellFormat>

		<afh:cellFormat width="50%" halign="left" valign="top"
			partialTriggers="sbUploadButton sbDeleteButton"
			rendered="#{permitDetail.permit.permitType == 'TVPTO'}">
			<afh:rowLayout>
				<afh:cellFormat halign="right" valign="top" width="195">
					<af:inputText label="Statement of Basis :" readOnly="true" />
				</afh:cellFormat>
				<afh:cellFormat>
					<af:goLink targetFrame="_blank"
						rendered="#{permitDetail.docsMap[permitReference.statementBasisCD][permitReference.allFlag] != null}"
						destination="#{permitDetail.docsMap[permitReference.statementBasisCD][permitReference.allFlag].docURL}"
						text="#{permitDetail.docsMap[permitReference.statementBasisCD][permitReference.allFlag].description}" />
					<af:objectSpacer width="10" />
					<af:commandButton text="Generate" useWindow="true"
						windowWidth="500" windowHeight="300" id="sbGenerateButton"
						rendered="#{permitDetail.editMode}"
						returnListener="#{permitDetail.docDialogDone}"
						action="#{permitDetail.generateStatementOfBasisDoc}" />
					<af:commandButton text="Upload" useWindow="true" windowWidth="500"
						windowHeight="300" id="sbUploadButton"
						rendered="#{permitDetail.editMode}"
						returnListener="#{permitDetail.docDialogDone}"
						action="#{permitDetail.uploadDoc}">
						<t:updateActionListener
							value="#{permitReference.statementBasisCD}"
							property="#{permitDetail.docTypeCD}" />
						<t:updateActionListener value="#{permitReference.allFlag}"
							property="#{permitDetail.issuanceStageFlag}" />
						<t:updateActionListener
							value="#{permitDetail.docsMap[permitReference.statementBasisCD][permitReference.allFlag]}"
							property="#{permitDetail.singleDoc}" />
					</af:commandButton>
					<af:commandButton text="Delete" useWindow="true"
						disabled="#{permitDetail.docsMap[permitReference.statementBasisCD][permitReference.allFlag] == null}"
						id="sbDeleteButton" rendered="#{permitDetail.editMode}"
						returnListener="#{permitDetail.deleteDocFromMap}"
						action="#{confirmWindow.confirm}"
						windowWidth="#{confirmWindow.width}"
						windowHeight="#{confirmWindow.height}">
						<t:updateActionListener property="#{confirmWindow.type}"
							value="#{confirmWindow.yesNo}" />
						<t:updateActionListener property="#{confirmWindow.message}"
							value="Click Yes to confirm the deletion of #{permitDetail.docsMap[permitReference.statementBasisCD][permitReference.allFlag].description}" />
						<t:updateActionListener
							value="#{permitReference.statementBasisCD}"
							property="#{permitDetail.docTypeCD}" />
						<t:updateActionListener value="#{permitReference.allFlag}"
							property="#{permitDetail.issuanceStageFlag}" />
						<t:updateActionListener
							value="#{permitDetail.docsMap[permitReference.statementBasisCD][permitReference.allFlag]}"
							property="#{permitDetail.singleDoc}" />
					</af:commandButton>
				</afh:cellFormat>
			</afh:rowLayout>
		</afh:cellFormat>

		<afh:cellFormat width="50%" halign="left" valign="top"
			partialTriggers="rcUploadButton rcDeleteButton">
			<afh:rowLayout>
				<afh:cellFormat width="190" halign="right" valign="top">
					<af:inputText label="Response to Comments :" readOnly="true" />
				</afh:cellFormat>
				<afh:cellFormat width="200">
					<af:goLink targetFrame="_blank"
						rendered="#{permitDetail.docsMap[permitReference.responseToCommentsCD][permitReference.allFlag] != null}"
						destination="#{permitDetail.docsMap[permitReference.responseToCommentsCD][permitReference.allFlag].docURL}"
						text="#{permitDetail.docsMap[permitReference.responseToCommentsCD][permitReference.allFlag].description}" />
					<af:objectSpacer width="10" />
					<af:commandButton text="Generate" useWindow="true"
						windowWidth="500" windowHeight="300" id="rcGenerateButton"
						rendered="#{permitDetail.editMode}"
						returnListener="#{permitDetail.docDialogDone}"
						action="#{permitDetail.generateResponseToCommentsDoc}" />
					<af:commandButton text="Upload" useWindow="true" windowWidth="500"
						windowHeight="300" id="rcUploadButton"
						rendered="#{permitDetail.editMode}"
						returnListener="#{permitDetail.docDialogDone}"
						action="#{permitDetail.uploadDoc}">
						<t:updateActionListener
							value="#{permitReference.responseToCommentsCD}"
							property="#{permitDetail.docTypeCD}" />
						<t:updateActionListener value="#{permitReference.allFlag}"
							property="#{permitDetail.issuanceStageFlag}" />
						<t:updateActionListener
							value="#{permitDetail.docsMap[permitReference.responseToCommentsCD][permitReference.allFlag]}"
							property="#{permitDetail.singleDoc}" />
					</af:commandButton>
					<af:commandButton text="Delete" id="rcDeleteButton"
						disabled="#{permitDetail.docsMap[permitReference.responseToCommentsCD][permitReference.allFlag] == null}"
						rendered="#{permitDetail.editMode}"
						returnListener="#{permitDetail.deleteDocFromMap}"
						action="#{confirmWindow.confirm}" useWindow="true"
						windowWidth="#{confirmWindow.width}"
						windowHeight="#{confirmWindow.height}">
						<t:updateActionListener property="#{confirmWindow.type}"
							value="#{confirmWindow.yesNo}" />
						<t:updateActionListener property="#{confirmWindow.message}"
							value="Click Yes to confirm the deletion of #{permitDetail.docsMap[permitReference.responseToCommentsCD][permitReference.allFlag].description}" />
						<t:updateActionListener
							value="#{permitReference.responseToCommentsCD}"
							property="#{permitDetail.docTypeCD}" />
						<t:updateActionListener value="#{permitReference.allFlag}"
							property="#{permitDetail.issuanceStageFlag}" />
						<t:updateActionListener
							value="#{permitDetail.docsMap[permitReference.responseToCommentsCD][permitReference.allFlag]}"
							property="#{permitDetail.singleDoc}" />
					</af:commandButton>
				</afh:cellFormat>
			</afh:rowLayout>
		</afh:cellFormat>
	</afh:rowLayout>

</afh:tableLayout>--%>

<af:showDetailHeader size="2" disclosed="true"
	text="Applications / Reopenings / Other Permit Action Requests">
	<af:table value="#{permitDetail.permit.applications}" var="permitApplication"
		emptyText=" " width="98%" varStatus="applicationTableVs">
		<f:facet name="footer">
			<af:panelGroup layout="horizontal">
				<af:commandButton useWindow="true" windowWidth="760"
					windowHeight="300"
					action="#{permitDetail.startToaddReferencedApp}"
					text="Add" shortDesc="#{permitDetail.addButtonText}"
					rendered="#{!permitDetail.readOnlyUser && permitDetail.editAllowed}"
					disabled = "#{permitDetail.applicationNumbersToAssocaiteCount == '0'}" />
			</af:panelGroup>
		</f:facet>
		<af:column id="edit" headerText="Row Id" formatType="text">
				<af:commandLink
					action="#{permitDetail.startToEditReferencedApp}"
					useWindow="true" windowWidth="800" windowHeight="600">
					<af:inputText value="#{applicationTableVs.index+1}"
						readOnly="true">
						<af:convertNumber pattern="000" />
					</af:inputText>
					<t:updateActionListener
						property="#{permitDetail.newReferencedAppNumber}"
						value="#{permitApplication.applicationNumber}" />
				</af:commandLink>
			</af:column>
		<af:column headerText="Number" sortProperty="applicationNumber"
			sortable="true">
			<af:commandLink text="#{permitApplication.applicationNumber}"
				action="applicationDetail">
				<af:setActionListener from="#{permitApplication.applicationID}"
					to="#{applicationDetail.applicationID}" />
			</af:commandLink>
		</af:column>
		<af:column headerText="Type" sortProperty="applicationTypeCD"
			sortable="true">
			<af:selectOneChoice value="#{permitApplication.applicationTypeCD}" readOnly="true">
				<mu:selectItems value="#{applicationReference.applicationTypeDefs}" />
			</af:selectOneChoice>
		</af:column>
		<af:column headerText="Reasons" sortProperty="" sortable="false">
			<af:outputText value="#{permitApplication.applicationPurposeDesc}" truncateAt="120" 
				shortDesc="#{permitApplication.applicationPurposeDesc}"/>
		</af:column>
		<af:column headerText="Purpose" sortProperty="" sortable="false">
			<af:outputText value="#{permitApplication.applicationDesc}" truncateAt="120" 
				shortDesc="#{permitApplication.applicationDesc}"/>
		</af:column>
	</af:table>
</af:showDetailHeader>
<%
	/* List of applications end */
%>
<%-- temporary hide the public notice section until further update fromm WY
<af:showDetailHeader text="Public Notice content" size="2"
	disclosed="false">
	<af:panelForm maxColumns="1" rows="1" labelWidth="18%" fieldWidth="72%"
		partialTriggers="pnt">
		<af:selectOneRadio label="Public Notice Type:" autoSubmit="true"
			immediate="true" readOnly="#{! permitDetail.editMode}" id="pnt"
			value="#{permitDetail.publicNoticeType}" layout="horizontal">
			<f:selectItems value="#{permitDetail.publicNoticeTypes}" />
		</af:selectOneRadio>
		<af:inputText label="Public Notice Text:" rows="5" columns="95"
			readOnly="#{(!permitDetail.editMode) || (permitDetail.publicNoticeType == 'SW') || (permitDetail.publicNoticeType == 'PSW')}"
			rendered="#{permitDetail.showNoticeText}" maximumLength="4000"
			value="#{permitDetail.permit.publicNoticeText}" />
		<af:panelGroup layout="horizontal" id="nd"
			rendered="#{!permitDetail.showNoticeText}"
			partialTriggers="ndUploadButton">
			<h:panelGrid width="143">
				<afh:rowLayout halign="right">
					<af:outputLabel value="Notice Document:" for="" />
				</afh:rowLayout>
			</h:panelGrid>
			<af:objectSpacer width="10" />
			<af:goLink targetFrame="_blank"
				rendered="#{permitDetail.docsMap[permitReference.publicNoticeCD][permitReference.draftFlag] != null}"
				destination="#{permitDetail.docsMap[permitReference.publicNoticeCD][permitReference.draftFlag].docURL}"
				text="#{permitDetail.docsMap[permitReference.publicNoticeCD][permitReference.draftFlag].description}" />
			<af:objectSpacer width="10" />
			<af:commandButton text="Upload" useWindow="true" windowWidth="500"
				windowHeight="300" id="ndUploadButton"
				rendered="#{permitDetail.editMode}"
				returnListener="#{permitDetail.docDialogDone}"
				action="#{permitDetail.uploadDoc}">
				<t:updateActionListener value="#{permitReference.publicNoticeCD}"
					property="#{permitDetail.docTypeCD}" />
				<t:updateActionListener value="#{permitReference.draftFlag}"
					property="#{permitDetail.issuanceStageFlag}" />
				<t:updateActionListener
					value="#{permitDetail.docsMap[permitReference.publicNoticeCD][permitReference.draftFlag]}"
					property="#{permitDetail.singleDoc}" />
			</af:commandButton>
		</af:panelGroup>
	</af:panelForm>
</af:showDetailHeader> --%>
