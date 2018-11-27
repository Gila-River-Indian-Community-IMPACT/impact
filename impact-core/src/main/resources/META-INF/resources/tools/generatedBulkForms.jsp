<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Generated Documents">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">

			<afh:rowLayout halign="center">
				<h:panelGrid border="1" rendered="true">
					<af:panelBorder>
						<af:showDetailHeader text="Generated Documents" disclosed="true">

							<af:messages />

							<af:selectInputDate readOnly="true" label="Correspondence Date: "
							    value="#{bulkOperationsCatalog.bulkOperation.correspondenceDate}"
								rendered="#{bulkOperationsCatalog.correspondenceDateEnabled}" />

							<af:objectSpacer width="100%" height="15"
								rendered="#{bulkOperationsCatalog.correspondenceDateEnabled}" />
							<af:outputText value="This type of correspondence can not be saved."
								rendered="#{!bulkOperationsCatalog.correspondenceSavable}" />
							<af:panelButtonBar 
									rendered="#{bulkOperationsCatalog.correspondenceSavable}">
								<af:commandButton text="Save Correspondence Record(s)"
									immediate="false"
									actionListener="#{bulkOperationsCatalog.correspondence}"
									disabled="#{bulkOperationsCatalog.allowClose}" />
								<af:commandButton text="Do Not Save Correspondence"
									immediate="false"
									action="#{bulkOperationsCatalog.enableClose}"
									disabled="#{bulkOperationsCatalog.allowClose}" />
							</af:panelButtonBar>

							<af:objectSpacer width="100%" height="15" />

							<af:table value="#{bulkOperationsCatalog.bulkOperation.results}"
								bandingInterval="1" banding="row" var="formResult">

								<af:column sortable="false" noWrap="true" formatType="text"
									headerText="#{bulkOperationsCatalog.bulkSearchType}">
									<af:panelHorizontal valign="middle" halign="left">
										<af:outputText value="#{formResult.id}" />
									</af:panelHorizontal>
								</af:column>

								<af:column sortable="false" formatType="text"
									headerText="Link To Document">
									<af:panelHorizontal valign="middle" halign="left">
										<h:outputLink value="#{formResult.formURL}">
											<af:outputText value="#{formResult.fileName}" />
										</h:outputLink>
									</af:panelHorizontal>
								</af:column>

								<af:column headerText="Notes, Errors, and Warnings"
									sortable="false" formatType="text">
									<af:panelHorizontal valign="middle" halign="left">
										<af:outputText value="#{formResult.notes}" />
									</af:panelHorizontal>
								</af:column>

							</af:table>

						</af:showDetailHeader>
					</af:panelBorder>
				</h:panelGrid>
			</afh:rowLayout>

			<af:panelForm rendered="#{bulkOperationsCatalog.allowClose || !bulkOperationsCatalog.correspondenceSavable}">
				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Close" immediate="true"
							actionListener="#{bulkOperationsCatalog.applyFinalAction}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
