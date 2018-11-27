<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Auto-Generate Missing NTV Reports">
		<f:facet name="metaContainer">
			<f:verbatim>
				<h:outputText value="#{autoGenNTV.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Auto-Generate Missing NTV Reports">
				<%@ include file="../util/header.jsp"%>
				<afh:rowLayout halign="center">
					<h:panelGrid border="1">
						<af:panelForm rows="1" maxColumns="2">
							<h:panelGrid>
								<af:outputText
									value="Specify the NTV reporting cycle to generate candidate facilities which are missing NTV reports.">
								</af:outputText>
							</h:panelGrid>
							<h:panelGrid>
								<af:inputText label="Even Year:" maximumLength="4"
									readOnly="#{!autoGenNTV.allowInput}" columns="4"
									value="#{autoGenNTV.evenYear}" />
								<af:inputText label=" Odd Year:" maximumLength="4"
									readOnly="#{!autoGenNTV.allowInput}" columns="4"
									value="#{autoGenNTV.oddYear}" />
							</h:panelGrid>
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>
				<af:objectSpacer height="6" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Generate Candidate List"
							rendered="#{autoGenNTV.genListButton}"
							action="#{autoGenNTV.startOperation}" />
					</af:panelButtonBar>
				</afh:rowLayout>
				<af:objectSpacer height="6" />
				<af:panelGroup layout="vertical"
					rendered="#{autoGenNTV.showProgress}">
					<af:objectSpacer height="6" />
					<af:progressIndicator id="progressid" value="#{autoGenNTV}">
						<af:outputFormatted value="#{autoGenNTV.value} % Complete" />
					</af:progressIndicator>
				</af:panelGroup>
				<af:panelGroup layout="vertical">
					<af:objectSpacer height="6" />
					<afh:rowLayout halign="center">
						<af:outputFormatted value="#{autoGenNTV.candidateIncludedStr}" />
					</afh:rowLayout>
				</af:panelGroup>
				<af:panelGroup layout="vertical"
					rendered="#{autoGenNTV.errorStr != null}">
					<af:objectSpacer height="6" width="100%"/>
					<afh:rowLayout halign="center">
						<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
							value="#{autoGenNTV.errorStr}" />
					</afh:rowLayout>
					<af:objectSpacer height="6" width="100%"/>
				</af:panelGroup>
				<afh:rowLayout halign="center" rendered="#{autoGenNTV.cancelButton}">
					<af:objectSpacer height="6" width="100%" />
					<af:panelButtonBar>
						<af:commandButton text="Cancel Generate NTV Reports"
							disabled="#{!autoGenNTV.execute}" action="#{autoGenNTV.cancel}" />
					</af:panelButtonBar>
				</afh:rowLayout>
				<afh:rowLayout halign="center" rendered="#{autoGenNTV.showTable}">
					<af:table value="#{autoGenNTV.genList}" var="row"
						bandingInterval="1" id="datagridTab" banding="row" rows="600">
						<f:facet name="header">
							<afh:rowLayout halign="left">
								<af:panelButtonBar>
									<af:commandButton action="#{autoGenNTV.selectAll}"
										disabled="#{!autoGenNTV.execute}" text="Select All" />
								</af:panelButtonBar>
								<af:objectSpacer height="1" width="6" />
								<af:panelButtonBar>
									<af:commandButton action="#{autoGenNTV.selectNone}"
										disabled="#{!autoGenNTV.execute}" text="Select None" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
						<af:column headerText="Select" sortable="true">
							<af:panelHorizontal valign="middle" halign="center">
								<af:selectBooleanCheckbox value="#{row.canGenerate}"
									rendered="#{!row.hasBeenGenerated && !row.duplicate}"
									readOnly="#{!autoGenNTV.execute}" />
								<af:outputText value="Generated"
									rendered="#{row.hasBeenGenerated}" inlineStyle="padding:5px" />
								<af:outputText value="Not Generated"
									rendered="#{row.duplicate}" inlineStyle="padding:5px;color: orange; font-weight: bold;" />
							</af:panelHorizontal>
						</af:column>
						<af:column headerText="Facility Information">
							<af:column headerText="Facility ID" sortable="true">
								<af:panelHorizontal valign="middle" halign="center">
									<af:outputText value="#{row.facilityId}"
										inlineStyle="padding:5px" />
								</af:panelHorizontal>
							</af:column>
							<af:column headerText="Facility Name" sortable="true">
								<af:panelHorizontal valign="middle" halign="left">
									<af:outputText value="#{row.facilityName}"
										inlineStyle="padding:5px" />
								</af:panelHorizontal>
							</af:column>
							<af:column headerText="Status" sortable="true">
								<af:panelHorizontal valign="middle" halign="center">
									<af:outputText
										value="#{facilityReference.operatingStatusDefs.itemDesc[(empty row.facilityOpState ? '' : row.facilityOpState)]}" />
								</af:panelHorizontal>

							</af:column>
						</af:column>
						<af:column headerText="Previous Approved Report">
							<af:column headerText="Id" sortable="true">
								<af:panelHorizontal valign="middle" halign="right">
									<af:outputText value="#{row.prevRptId}"
										inlineStyle="padding:5px" />
								</af:panelHorizontal>
							</af:column>
							<af:column headerText="Category" sortable="true">
								<af:panelHorizontal valign="middle" halign="center">
									<af:outputText value="#{row.prevRptCat}"
										inlineStyle="padding:5px" />
								</af:panelHorizontal>
							</af:column>
							<af:column headerText="Emissions" sortable="true">
								<af:panelHorizontal valign="middle" halign="center">
									<af:outputText value="#{row.prevRptTons}"
										inlineStyle="padding:5px" />
								</af:panelHorizontal>
							</af:column>
						</af:column>
						<af:column headerText="Auto-Generate Info">
							<af:column headerText="Year(s)" sortable="true">
								<af:panelHorizontal valign="middle" halign="center">
									<af:outputText value="#{row.genYears}"
										inlineStyle="padding:5px" />
								</af:panelHorizontal>
							</af:column>
							<af:column headerText="Emissions" sortable="true">
								<af:panelHorizontal valign="middle" halign="center">
									<af:outputText value="#{row.genRanges}"
										inlineStyle="padding:5px" />
								</af:panelHorizontal>
							</af:column>
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</afh:rowLayout>
				<af:objectSpacer height="6" />
				<afh:rowLayout halign="center"
					rendered="#{autoGenNTV.genRptsButton}">
					<af:panelButtonBar>
						<af:commandButton text="Generate NTV Reports"
							disabled="#{!autoGenNTV.execute}"
							action="#{autoGenNTV.doGenerate}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
