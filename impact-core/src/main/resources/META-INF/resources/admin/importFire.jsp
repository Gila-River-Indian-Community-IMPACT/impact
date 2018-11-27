<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Import FIRE Database">
		<f:facet name="metaContainer">
			<f:verbatim>
				<h:outputText value="#{fireUpdater.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				title="Import FIRE Database">
				<%@ include file="../util/header.jsp"%>
				<af:inputHidden value="#{fireUpdater.popupRedirect}" />
				<af:objectSpacer height="10" />
				<af:panelGroup layout="vertical"
					rendered="#{fireUpdater.fileStatus == 0}">
					<af:poll interval="1500" id="fireImportPoll" />
					<af:panelGroup layout="vertical" partialTriggers="fireImportPoll">
						<af:progressIndicator id="progressid" value="#{fireUpdater}">
							<af:outputFormatted value="#{fireUpdater.value} % Complete" />
						</af:progressIndicator>
					</af:panelGroup>
				</af:panelGroup>

				<af:showDetailHeader text="Explanation" disclosed="false">
					<af:outputFormatted styleUsage="instruction"
						value="<ul>
						<li>FIRE Import:</li>
							<ul>
								<li>FIRE Import supports adding, modifying, and deprecating rows in the IMPACT FIRE database table.</li>
								<li>There are three categories of import: </li>
									<ul>
										<li>Full Import</li>
										<li>Partial Import to modify or deprecate existing entries</li>
										<li>Import to add new entries</li>
									</ul>
								</li>
								<li>Full Import:</li>
									<ul>
										<li>Entries can be added, modified, and deprecated.</li>
										<li>Import file must contain all the FIRE entries with values that are to be current after the file is successfully imported.</li>
										<li>Import file should omit any current FIRE entries that are to be deprecated.</li>
										<li>Import file may include new entries.</li>
									</ul>
								</li>
								<li>Partial Import to modify or deprecate existing entries:</li>
									<ul>
										<li>Entries that match the values selected for SCC ID, Material(s), and Pollutant(s) can be modified and deprecated.</li>
										<li>Entries cannot be added.</li>
										<li>Import file must contain all the FIRE entries for the selected SCC ID, Material(s), and Pollutant(s) with values that are to be current after the file is successfully imported.</li>
										<li>If the import file includes any rows for which the SCC ID, Material, and/or Pollutant does not match the selected values on the page, the row will be skipped and a validation error will be written to the error file.</li>
										<li>Import file should omit any current FIRE entries for the selected SCC ID, Material(s), and Pollutant(s) that are to be deprecated.</li>
										<li>Import file may not include new entries...if the user selects an SCC ID, Material(s), or Pollutants(s), the options to 'Analyze adding new entries only' and 'Add new entries only' are disabled.</li>
									</ul>
								</li>
								<li>Import to add new entries:</li>
									<ul>
										<li>Entries can only be added.</li>
										<li>Import file must include only new entries.</li>
										<li>Entries that match all of the following critical values for an existing row in the database will be skipped as duplicates: SCC ID, Material, Pollutant, Measure, and Action.</li> 
									</ul>
								</li>
					  </ul>" />
				</af:showDetailHeader>


				<af:showDetailHeader partialTriggers="whichOper sccId material pollutant" disclosed="true"
					text="Operation to Perform">
					<af:objectSpacer height="10" width="300" />
					<af:panelForm rows="3" maxColumns="1" labelWidth="150px" fieldWidth="250px" id="pfl">
					
						<afh:rowLayout halign="center">
							<af:outputFormatted rendered="#{fireUpdater.sccId == null}"
								value="When SCC ID is empty, a <b>Full Import</b> will be done unless adding new entries only.  If doing a Full Import, Import file must be complete. If adding new entries only, Import file must include only new entries <b>and</b> Operation to Perform: 'Analyze adding new entries only.' or 'Add new entries only.' must be chosen." />
							<af:outputFormatted rendered="#{fireUpdater.sccId != null}"
								value="When SCC ID has a value, a <b>Partial Import</b> will be done to modify existing current entries.  Import file must only include rows that match the selected SCC ID, Material(s), and Pollutant(s). Any current entries that are missing from the import file will be deprecated." />
						</afh:rowLayout>
					
						<af:objectSpacer height="5" width="300" />
						<afh:rowLayout halign="center">
						<af:selectOneChoice id="sccId" label="SCC ID: " unselectedLabel=" "
							valueChangeListener="#{fireUpdater.sccChanged}"
							value="#{fireUpdater.sccId}" autoSubmit="true">
							<f:selectItems
								value="#{facilityReference.sccCodesDefs.items[(empty fireUpdater.sccId ? '' : fireUpdater.sccId)]}" />
						</af:selectOneChoice>
						</afh:rowLayout>
					
						<af:objectSpacer height="5" width="300" />
					
						<afh:rowLayout halign="center">
						<af:selectManyListbox id="material" label="Material(s): " size="5"
							value="#{fireUpdater.materialCds}" autoSubmit="true">
							<f:selectItems
								value="#{fireUpdater.sccMaterials}" />
						</af:selectManyListbox>
						</afh:rowLayout>
					
						<af:objectSpacer height="5" width="10" />
					
						<afh:rowLayout halign="center">
						<af:selectManyListbox id="pollutant" label="Pollutant(s): " size="5"
							value="#{fireUpdater.pollutantCds}" autoSubmit="true">
							<f:selectItems
								value="#{fireUpdater.sccPollutants}" />
						</af:selectManyListbox>
						</afh:rowLayout>
					
						<af:objectSpacer height="5" width="300" />
						<afh:rowLayout halign="center">
							<af:selectOneRadio id="whichOper" label="Operation to Perform: "
								readOnly="#{fireUpdater.fileStatus == 0}"
								value="#{fireUpdater.importChoice}" autoSubmit="true">
								<f:selectItem itemLabel="Analyze adding new entries only."
									itemValue="1" 
									itemDisabled="#{fireUpdater.sccId != null || fireUpdater.materialCdsLength != 0 || fireUpdater.pollutantCdsLength != 0}"/>
								<f:selectItem itemLabel="Analyze doing a FIRE Import."
									itemValue="2" />
								<f:selectItem itemLabel="Add new entries only." 
									itemValue="3" 
									itemDisabled="#{fireUpdater.sccId != null || fireUpdater.materialCdsLength != 0 || fireUpdater.pollutantCdsLength != 0}"/>
								<f:selectItem itemLabel="Perform FIRE Import."
									itemValue="4" />
							</af:selectOneRadio>
						</afh:rowLayout>
						<af:objectSpacer height="10" width="300" />
					</af:panelForm>
					
					<af:panelForm rendered="#{fireUpdater.importChoice != null}">
						<af:objectSpacer height="15" width="300" />
						<afh:rowLayout halign="center">
							<af:inputFile label="FIRE file to Import: " id="fireFile"
								rendered="#{fireUpdater.importChoice != null}"
								disabled="#{fireUpdater.fileStatus == 0}"
								value="#{fireUpdater.fireFileToUpload}" />
						</afh:rowLayout>
						<af:objectSpacer height="15" width="300" />
						<afh:rowLayout halign="center">
							<af:outputFormatted rendered="#{fireUpdater.importChoice == 1}"
								value="Logs the FIRE entries that would be added if the operation <b>Add new entries only</b> is performed" />
							<af:outputFormatted rendered="#{fireUpdater.importChoice == 2}"
								value="Logs the changes that would be made if the operation <b>Perform FIRE Import</b> is performed" />
							<af:outputFormatted rendered="#{fireUpdater.importChoice == 3}"
								value="Entries in the input file that are not already in the database are added.&nbsp;&nbsp;Unless the entry has a create year, #{fireUpdater.defaultCreateDeprecate} is used.&nbsp;&nbsp;No entries already in the database are changed or deleted." />
							<af:outputFormatted rendered="#{fireUpdater.importChoice == 4}"
								value="Any FIRE entries added that don't supply a create year use #{fireUpdater.defaultCreateDeprecate} and any FIRE entries not included use a deprecate year of #{fireUpdater.defaultCreateDeprecate}.&nbsp;&nbsp;Existing entries are updated if they match on all critical fields." />
						</afh:rowLayout>
						<afh:rowLayout halign="center">
							<af:panelButtonBar rendered="#{fireUpdater.fileStatus != 0}">
								<af:commandButton text="Analyze adding new entries only"
									rendered="#{fireUpdater.importChoice == 1}"
									action="#{fireUpdater.startOperation}" />
								<af:commandButton text="Analyze doing a FIRE Import"
									rendered="#{fireUpdater.importChoice == 2}"
									action="#{fireUpdater.startOperation}" />
								<af:commandButton text="Add new entries only"
									rendered="#{fireUpdater.importChoice == 3}"
									action="#{fireUpdater.startOperation}" />
								<af:commandButton text="Perform FIRE Import"
									rendered="#{fireUpdater.importChoice == 4}"
									action="#{fireUpdater.startOperation}" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</af:panelForm>
				</af:showDetailHeader>
					<af:objectSpacer height="15" width="300" />
						<af:showDetailHeader text="#{(fireUpdater.fileStatus == 2)?'Results:':'Previous Results:'}" disclosed="true">
						<afh:rowLayout halign="center"
							rendered="#{fireUpdater.fileStatus != 0}">
							<af:outputFormatted inlineStyle="color: orange; font-weight: bold;" value="#{fireUpdater.displayErr}" />
							<af:outputFormatted value="#{fireUpdater.displayResults}" />
						</afh:rowLayout>
						<af:objectSpacer height="10" />
						<afh:rowLayout halign="center">
							<af:goLink id="logLink" text="log file of what operation did"
								disabled="#{fireUpdater.fileStatus == 0 ||!fireUpdater.logFileExists}"
								destination="#{fireUpdater.logFileDoc.docURL}" targetFrame="_blank" />
						</afh:rowLayout>
						<af:objectSpacer height="5" />
						<afh:rowLayout halign="center">
							<af:goLink id="errLink" text="error file of problems found"
								disabled="#{fireUpdater.fileStatus == 0 || !fireUpdater.errFileExists}"
								rendered="#{!fireUpdater.noErrors}"
								destination="#{fireUpdater.errFileDoc.docURL}" targetFrame="_blank" />
							<h:outputText value="No Errors" 
								rendered="#{fireUpdater.noErrors}"/>
						</afh:rowLayout>
						</af:showDetailHeader>
			</af:page>
		</af:form>
	</af:document>
</f:view>
