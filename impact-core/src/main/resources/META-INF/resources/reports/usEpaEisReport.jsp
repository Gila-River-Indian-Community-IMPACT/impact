<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="US EPA EIS Report">
		<f:facet name="metaContainer">
			<f:verbatim>
				<h:outputText value="#{usEpaEisReport.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}"
				title="US EPA EIS Report">
				<%@ include file="../util/header.jsp"%>
				<h:panelGrid border="1" align="center">
					<af:panelForm maxColumns="1">
						<af:selectOneChoice value="#{usEpaEisReport.year}" id="year"
							label="Reporting Year:" readOnly="#{!usEpaEisReport.allowInput}"
							showRequired="true" autoSubmit="true">
							<f:selectItems value="#{infraDefs.eisYears}" />
						</af:selectOneChoice>
						<af:selectOneChoice value="#{usEpaEisReport.lastEmissionsYear}"
							id="endYear" partialTriggers="year"
							tip="Used when ending processes that no longer exist"
							label="Previous Reporting Year:"
							readOnly="#{!usEpaEisReport.allowInput}" autoSubmit="true">
							<f:selectItems value="#{infraDefs.eis2Years}" />
						</af:selectOneChoice>
						<af:inputText value="#{usEpaEisReport.authorName}" label="Author:"
							readOnly="#{!usEpaEisReport.allowInput}" showRequired="true" />
						<af:inputText value="#{usEpaEisReport.aqdContact}"
							label="AQD Contact:" readOnly="#{!usEpaEisReport.allowInput}" />	
						<af:inputText value="#{usEpaEisReport.eisLogin}"
							label="EIS Login:" readOnly="#{!usEpaEisReport.allowInput}"
							showRequired="true" />
						<%--<af:selectBooleanCheckbox
							label="Include cer:ProcessControlApproach information:"
							value="#{usEpaEisReport.includeProcessControls}"
							readOnly="#{!usEpaEisReport.allowInput}" id="euShutdown" />
						<af:selectBooleanCheckbox label="Include HAP emissions:"
							value="#{usEpaEisReport.includeHAPS}"
							readOnly="#{!usEpaEisReport.allowInput}" id="includeHAPS" />
						<af:selectBooleanCheckbox label="Include Warnings in Log:"
							value="#{usEpaEisReport.includeWarns}"
							readOnly="#{!usEpaEisReport.allowInput}" />
						<af:inputText id="singleF" autoSubmit="true" maximumLength="10"
							label="Single facility or blank for all facilities:"
							readOnly="#{!usEpaEisReport.allowInput}" columns="10"
							value="#{usEpaEisReport.singleFacility}" />--%>
						 <af:selectManyListbox 
							label="Include Facility Class(es): "
							showRequired="true"
							readOnly="#{!usEpaEisReport.allowInput}"
							value="#{usEpaEisReport.facilityClassList}">
							<f:selectItems
								value="#{facilityReference.permitClassDefs.items[(empty usEpaEisReport.facilityClassList ? '' : usEpaEisReport.facilityClassList)]}" />
						</af:selectManyListbox>

						<af:selectManyListbox 
							label="Exclude Facility Type(s): "
							readOnly="#{!usEpaEisReport.allowInput}"
							styleClass="FacilityTypeClass x6"
							value="#{usEpaEisReport.facilityTypeList}">
							<f:selectItems
								value="#{facilityReference.facilityTypeDefs.items[(empty usEpaEisReport.facilityTypeList ? '' : usEpaEisReport.facilityTypeList)]}" />
						</af:selectManyListbox>	
						<af:selectOneChoice id="submissionType" label="SubmissionType:"
							readOnly="#{!usEpaEisReport.allowInput}"
							value="#{usEpaEisReport.submissionType}" showRequired="true">
							<f:selectItems
								value="#{usEpaEisReport.EISSubmissionTypes.items[(empty usEpaEisReport.submissionType ? 0 : usEpaEisReport.submissionType)]}"/>
						</af:selectOneChoice>		
						<af:inputText value="#{usEpaEisReport.submittalComment}"
							label="Submittal Comment:" rows="4" columns="120"
							maximumLength="#{usEpaEisReport.submittalCommentMaxSize}"
							readOnly="#{!usEpaEisReport.allowInput}" />
						<%--<af:inputFile id="usEpaFacilities"
							label="US EPA Facility Info (.csv):"
							shortDesc="US EPA Facility information--in facility Id order"
							columns="75" rendered="#{usEpaEisReport.allowInput}"
							value="#{usEpaEisReport.usEpaFacilities}" />
						<af:inputFile id="usEpaEUs"
							label="US EPA Emission Unit Info (.csv):"
							shortDesc="US EPA Emission Unit information--in facility Id, EU Id  order"
							columns="75" rendered="#{usEpaEisReport.allowInput}"
							value="#{usEpaEisReport.usEpaEUs}" />
						<af:inputFile id="usEpaProcesses"
							label="US EPA Process Info (.csv):"
							shortDesc="US EPA Emission Unit Process information--in facility Id, EU Id  order"
							columns="75" rendered="#{usEpaEisReport.allowInput}"
							value="#{usEpaEisReport.usEpaProcesses}" />
						<af:inputFile id="usEpaEPs"
							label="US EPA Release Point Info (.csv):"
							shortDesc="US EPA Release Point information--in facility Id order"
							columns="75" rendered="#{usEpaEisReport.allowInput}"
							value="#{usEpaEisReport.usEpaEPs}" />
						<af:inputText label="US EPA Facility Info (.csv):"
							shortDesc="US EPA Facility information--in facility Id order"
							columns="75" rendered="#{!usEpaEisReport.allowInput}"
							value="#{usEpaEisReport.usEpaFacilitiesStr}" />
						<af:inputText label="US EPA Emission Unit Info (.csv):"
							shortDesc="US EPA Emission Unit information--in facility Id, EU Id  order"
							columns="75" rendered="#{!usEpaEisReport.allowInput}"
							value="#{usEpaEisReport.usEpaEUsStr}" />
						<af:inputText label="US EPA Process Info (.csv):"
							shortDesc="US EPA Emission Unit Process information--in facility Id, EU Id  order"
							columns="75" rendered="#{!usEpaEisReport.allowInput}"
							value="#{usEpaEisReport.usEpaProcessesStr}" />
						<af:inputText label="US EPA Release Point Info (.csv):"
							shortDesc="US EPA Release Point information--in facility Id order"
							columns="75" rendered="#{!usEpaEisReport.allowInput}"
							value="#{usEpaEisReport.usEpaEPsStr}" />--%>
					</af:panelForm>
				</h:panelGrid>
				<af:objectSpacer height="6" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Create EIS Report" useWindow="true"
							windowWidth="400" windowHeight="200"
							disabled="#{!usEpaEisReport.allowInput}"
							action="#{usEpaEisReport.generateXMLFiles}" />
						<af:commandButton text="Reset" 
							disabled="#{!usEpaEisReport.allowInput}"
							action="#{usEpaEisReport.reset}" />	
					</af:panelButtonBar>
				</afh:rowLayout>
				<af:objectSpacer height="6" /> 
				<%-- <af:panelGroup layout="vertical"
					rendered="#{usEpaEisReport.showProgress}">
					<af:objectSpacer height="6" />
					<af:progressIndicator id="progressid" value="#{usEpaEisReport}">
						<af:outputFormatted value="#{usEpaEisReport.value} % Complete" />
					</af:progressIndicator>
				</af:panelGroup>--%>
				<%--<af:panelGroup layout="vertical"
					rendered="#{usEpaEisReport.errorStr != null}">
					<af:objectSpacer height="6" />
					<afh:rowLayout halign="center">
						<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
							value="#{usEpaEisReport.errorStr}" />
					</afh:rowLayout>
				</af:panelGroup>
				<afh:rowLayout halign="center">
					<af:objectSpacer height="6" width="100%" />
				</afh:rowLayout>
				<afh:rowLayout halign="center"
					rendered="#{usEpaEisReport.cancelButton}">
					<af:objectSpacer height="6" width="100%" />
					<af:panelButtonBar>
						<af:commandButton text="Cancel EIS Report"
							action="#{usEpaEisReport.cancel}" />
					</af:panelButtonBar>
				</afh:rowLayout>
				<afh:rowLayout halign="center" partialTriggers="year singleF">
					<af:goLink id="logLink"
						text="#{usEpaEisReport.facilityMismatchXmlFileStat}"
						disabled="#{usEpaEisReport.showProgress}"
						rendered="#{usEpaEisReport.allowInput}"
						destination="#{usEpaEisReport.facilityMismatchXmlFileDoc.docURL}"
						targetFrame="_blank" />
				</afh:rowLayout>
				<afh:rowLayout halign="center" partialTriggers="year singleF">
					<af:goLink id="logLink2"
						text="#{usEpaEisReport.facilityXmlFileStat}"
						disabled="#{usEpaEisReport.showProgress}"
						rendered="#{usEpaEisReport.allowInput}"
						destination="#{usEpaEisReport.facilityXmlFileDoc.docURL}"
						targetFrame="_blank" />
				</afh:rowLayout>
				<afh:rowLayout halign="center" partialTriggers="year singleF">
					<af:goLink id="logLink3" text="#{usEpaEisReport.pointXmlFileStat}"
						disabled="#{usEpaEisReport.showProgress}"
						rendered="#{usEpaEisReport.allowInput}"
						destination="#{usEpaEisReport.pointXmlFileDoc.docURL}"
						targetFrame="_blank" />
				</afh:rowLayout>
				<af:objectSpacer height="5" />
				<afh:rowLayout halign="center" partialTriggers="year singleF">
					<af:goLink id="errLink" text="#{usEpaEisReport.logFileStat}"
						disabled="#{usEpaEisReport.showProgress}"
						rendered="#{usEpaEisReport.allowInput}"
						destination="#{usEpaEisReport.logFileDoc.docURL}"
						targetFrame="_blank" />
				</afh:rowLayout>
				<af:objectSpacer height="15" />
				<af:showDetailHeader text="What data goes into EIS Report"
					disclosed="false">
					<afh:rowLayout halign="left">
						<af:outputFormatted value="#{usEpaEisReport.whatInReport}" />
					</afh:rowLayout>
				</af:showDetailHeader>
				<af:outputFormatted inlineStyle="font-size:75%;color:#666"
					value="#{usEpaEisReport.xmlInfo}" /> --%>
			</af:page>
		</af:form>
		<f:verbatim>
			<%@ include
				file="../scripts/jquery-1.9.1.min.js"%>
			<%@ include
				file="../scripts/FacilityType-Option.js"%>
			<%@ include
				file="../scripts/wording-filter.js"%>
			<%@ include
				file="../scripts/facility-detail-location.js"%>
		</f:verbatim>
	</af:document>
</f:view>
