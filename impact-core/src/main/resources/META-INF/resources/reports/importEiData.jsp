<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Import Emissions Inventory Data">
		<f:facet name="metaContainer">
			<f:verbatim>
				<h:outputText value="#{eiDataImport.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>

		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				title="Import Emissions Inventory Data">
				<%@ include file="../util/header.jsp"%>

				<af:objectSpacer height="10" />
				<af:panelGroup layout="vertical"
					rendered="#{eiDataImport.fileStatus == 0}">
					<af:poll interval="1511" id="eiDataImportPoll" />
					<af:panelGroup layout="vertical" partialTriggers="eiDataImportPoll">
						<af:progressIndicator id="progressid" value="#{eiDataImport}">
							<af:outputFormatted value="#{eiDataImport.value} % Complete" />
						</af:progressIndicator>
					</af:panelGroup>
				</af:panelGroup>

				<h:panelGrid border="1" align="center">
					<af:panelForm maxColumns="1">

						<af:objectSpacer height="10" width="300" />

						<af:selectOneChoice id="eiImportYear" label="Inventory Year:"
							value="#{eiDataImport.importCriteria.eiImportYear}"
							showRequired="true">
							<f:selectItems value="#{eiDataImport.eiImportYears}" />
						</af:selectOneChoice>

						<af:objectSpacer height="5" width="300" />

						<af:selectOneChoice id="ContentTypeCd" label="Content Type:"
							value="#{eiDataImport.importCriteria.eiImportContentTypeCd}"
							unselectedLabel=" " showRequired="true">
							<f:selectItems value="#{eiDataImport.eiImportContentTypes}" />
						</af:selectOneChoice>

						<af:objectSpacer height="5" width="300" />

						<af:selectOneChoice id="regulatoryRequirementCd"
							label="Regulatory Requirement Defining Pollutants to be Imported: "
							value="#{eiDataImport.importCriteria.eiImportRegulatoryRequirement}"
							unselectedLabel=" " showRequired="true">
							<f:selectItems
								value="#{eiDataImport.eiImportRegulatoryRequirementTypes}" />
						</af:selectOneChoice>

						<af:objectSpacer height="5" width="300" />

						<af:inputFile label="Emissions Inventory Data File to Import: "
							id="eiDataFile" showRequired="true"
							value="#{eiDataImport.importCriteria.eiDataFileToUpload}" />

						<af:objectSpacer height="10" width="300" />

						<%--Operation to perform --%>
						<afh:rowLayout halign="center">
							<af:selectOneRadio id="whichOper" label="Operation to Perform: "
								value="#{eiDataImport.importCriteria.importChoice}"
								showRequired="true" autoSubmit="true">
								<f:selectItem itemLabel="Analyze import file" itemValue="1" />
								<f:selectItem itemLabel="Perform Data Import" itemValue="2" />
							</af:selectOneRadio>
						</afh:rowLayout>

						<%--Attachment Files Section --%>

						<af:objectSpacer height="20" width="300" />

						<f:subview id="doc_attachments">
							<jsp:include flush="true"
								page="../doc_attachments/doc_attachments.jsp" />
						</f:subview>

						<af:objectSpacer height="10" width="300" />

						<af:panelForm maxColumns="1" partialTriggers="whichOper">
							<afh:rowLayout halign="center">
								<af:panelButtonBar rendered="#{eiDataImport.fileStatus != 0}">
									<af:commandButton text="Analyze import file"
										rendered="#{eiDataImport.importCriteria.importChoice == 1}"
										action="#{eiDataImport.startOperation}" />
									<af:commandButton text="Perform Data Import"
										rendered="#{eiDataImport.importCriteria.importChoice == 2}"
										action="#{eiDataImport.startOperation}" />
									<af:commandButton text="Reset"
										action="#{eiDataImport.resetReportsEiDataImport}" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>

						<af:objectSpacer height="15" width="300" />
						<af:showDetailHeader rendered="#{eiDataImport.fileStatus == 2}"
							text="Results:"
							disclosed="true">
							<afh:rowLayout halign="center"
								rendered="#{eiDataImport.fileStatus != 0}">
								<af:outputFormatted
									inlineStyle="color: orange; font-weight: bold;"
									value="#{eiDataImport.displayErr}" />
								<af:outputFormatted value="#{eiDataImport.displayResults}" />
							</afh:rowLayout>
							<af:objectSpacer height="10" />
							<afh:rowLayout halign="center">
								<af:goLink id="logLink" text="log file of what operation did"
									disabled="#{eiDataImport.fileStatus == 0}"
									destination="#{eiDataImport.importCriteria.logFileDoc.docURL}"
									targetFrame="_blank" />
							</afh:rowLayout>
							<af:objectSpacer height="5" />
							<afh:rowLayout halign="center">
								<af:goLink id="errLink" text="error file of problems found"
									disabled="#{eiDataImport.fileStatus == 0}"
									rendered="#{!eiDataImport.noErrors || !eiDataImport.noWarnings}"
									destination="#{eiDataImport.importCriteria.errFileDoc.docURL}"
									targetFrame="_blank" />
								<h:outputText value="No Errors or Warnings"
									rendered="#{eiDataImport.noErrors && eiDataImport.noWarnings}" />
							</afh:rowLayout>
						</af:showDetailHeader>

					</af:panelForm>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>
