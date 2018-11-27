<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="WISE View Migration">
		<f:facet name="metaContainer">
			<f:verbatim>
				<h:outputText value="#{migrateWiseView.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>

		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				title="WISE View Migration">
				<%@ include file="../util/header.jsp"%>

				<af:objectSpacer height="10" />
				<af:panelGroup layout="vertical"
					rendered="#{migrateWiseView.migrateStatus == 0}">
					<af:poll interval="1511" id="wvDataImportPoll" />
					<af:panelGroup layout="vertical" partialTriggers="wvDataImportPoll">
						<af:progressIndicator id="progressid">
							<af:outputFormatted value="In progress..." />
						</af:progressIndicator>
					</af:panelGroup>
				</af:panelGroup>

				<af:objectSpacer height="20" />

				<h:panelGrid border="1" align="center">
					<af:panelForm maxColumns="1">

						<af:showDetailHeader text="Wise View Migration" disclosed="true">
							<af:inputFile id="filePath" columns="65"
								label="WISE View migration zip file to Upload:"
								value="#{migrateWiseView.fileToUpload}" />

							<af:objectSpacer height="10" />

							<af:selectBooleanCheckbox label="Remove existing WISE View data:"
								value="#{migrateWiseView.deleteExistingData}" />

							<af:objectSpacer height="10" />

							<af:panelForm maxColumns="1">
								<afh:rowLayout halign="center">
									<af:panelButtonBar
										rendered="#{migrateWiseView.migrateStatus != 0}">
										<af:commandButton text="Start to Migrate"
											action="#{migrateWiseView.startMigrateOperation}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelForm>
						</af:showDetailHeader>

						<af:objectSpacer height="15" width="300" />

						<af:showDetailHeader text="Facility Inventory Deletion"
							disclosed="true">
							<af:inputFile id="facilityFilePath" columns="1"
								label="Facility inventory Deletion:"
								value="#{migrateWiseView.fileToDeleteFacilityInventory}" />

							<af:objectSpacer height="10" />

							<af:panelForm maxColumns="1">
								<afh:rowLayout halign="center">
									<af:panelButtonBar
										rendered="#{migrateWiseView.migrateStatus != 0}">
										<af:commandButton text="Start to Delete"
											action="#{migrateWiseView.startDeleteOperation}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelForm>
						</af:showDetailHeader>

						<af:objectSpacer height="15" width="300" />
						<af:showDetailHeader
							rendered="#{migrateWiseView.migrateStatus == 2}" text="Results:"
							disclosed="true">
							<afh:rowLayout halign="center"
								rendered="#{migrateWiseView.migrateStatus != 0}">
								<af:outputFormatted value="#{migrateWiseView.displayResults}" />
							</afh:rowLayout>
							<af:objectSpacer height="10" />

							<afh:rowLayout halign="center">
								<af:goLink id="logLink" text="Log file"
									disabled="#{migrateWiseView.migrateStatus == 0}"
									destination="#{migrateWiseView.logFileURI }" targetFrame="_new" />
							</afh:rowLayout>
						</af:showDetailHeader>

					</af:panelForm>
				</h:panelGrid>

			</af:page>
		</af:form>
	</af:document>
</f:view>