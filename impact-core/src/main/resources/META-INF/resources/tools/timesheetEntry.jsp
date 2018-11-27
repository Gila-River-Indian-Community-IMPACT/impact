<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>


<f:view>
	<af:document title="Timesheet Entry">
		<f:facet name="metaContainer">
			<f:verbatim>
				<af:outputText value="#{facilityProfile.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>

		<f:verbatim>
			<script>
				</f:verbatim><af:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Timesheet Entry">
				<%@ include file="../util/header.jsp"%>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1100">
						<af:panelBorder>
							<af:showDetailHeader text="Timesheet Entry" disclosed="true">

								<af:objectSpacer height="8" />

								<af:panelHorizontal>
									<af:selectOneChoice label="Staff Member:" id="aqdStaff"
										valueChangeListener="#{timesheetEntry.aqdStaffSelected}"
										autoSubmit="true" required="true"
										readOnly="#{!timesheetEntry.admin}" unselectedLabel=" "
										value="#{timesheetEntry.aqdStaff}">
										<f:selectItems value="#{infraDefs.allActiveUsersDef.allItems}" />
									</af:selectOneChoice>
								</af:panelHorizontal>

								<af:objectSpacer height="8" />

								<afh:tableLayout cellPadding="4" width="100%"
									partialTriggers="function section nsrId nsrId2 tvId tvId2"
									styleClass="af_table_content">
									<afh:rowLayout halign="left">
										<afh:cellFormat valign="bottom" header="true"
											wrappingDisabled="true"
											styleClass="OraSortableHeaderBorder af_column_sortable-header-text">
											<af:outputText value="Date" />
										</afh:cellFormat>
										<afh:cellFormat
											rendered="#{!timesheetEntry.functionsDefEmpty}"
											valign="bottom" header="true"
											styleClass="OraSortableHeaderBorder af_column_sortable-header-text">
											<af:outputText value="Function" />
										</afh:cellFormat>
										<afh:cellFormat
											rendered="#{not empty timesheetEntry.sectionsByFunction}"
											valign="bottom" header="true"
											styleClass="OraSortableHeaderBorder af_column_sortable-header-text">
											<af:outputText value="Section" />
										</afh:cellFormat>
										<afh:cellFormat rendered="#{timesheetEntry.nsrRequired}"
											valign="bottom" header="true"
											styleClass="OraSortableHeaderBorder af_column_sortable-header-text">
											<af:outputText value="NSR" />
										</afh:cellFormat>
										<afh:cellFormat rendered="#{timesheetEntry.tvRequired}"
											valign="bottom" header="true"
											styleClass="OraSortableHeaderBorder af_column_sortable-header-text">
											<af:outputText value="Title V" />
										</afh:cellFormat>
										<afh:cellFormat valign="bottom" header="true"
											styleClass="OraSortableHeaderBorder af_column_sortable-header-text">
											<af:outputText value="Hours" />
										</afh:cellFormat>
										<afh:cellFormat valign="bottom" header="true"
											styleClass="OraSortableHeaderBorder af_column_sortable-header-text">
											<af:outputText value="OT" />
										</afh:cellFormat>
									</afh:rowLayout>


									<afh:rowLayout halign="left" partialTriggers="aqdStaff">
										<afh:cellFormat width="1%">
											<af:selectInputDate value="#{timesheetEntry.row.date}"
												id="date">
												<af:validateDateTimeRange
													minimum="#{timesheetEntry.minDate}"
													maximum="#{timesheetEntry.maxDate}" />
											</af:selectInputDate>
										</afh:cellFormat>
										<afh:cellFormat
											rendered="#{!timesheetEntry.functionsDefEmpty}">
											<af:selectOneChoice id="function" autoSubmit="true"
												valueChangeListener="#{timesheetEntry.functionSelected}"
												value="#{timesheetEntry.row.function}" unselectedLabel="">
												<mu:selectItems value="#{timesheetEntry.functions}" />
											</af:selectOneChoice>
										</afh:cellFormat>
										<afh:cellFormat
											rendered="#{not empty timesheetEntry.sectionsByFunction}">
											<af:selectOneChoice id="section" autoSubmit="true"
												valueChangeListener="#{timesheetEntry.sectionSelected}"
												value="#{timesheetEntry.row.section}">
												<f:selectItems value="#{timesheetEntry.sectionsByFunction}" />
											</af:selectOneChoice>
										</afh:cellFormat>

										<afh:cellFormat width="1%"
											rendered="#{timesheetEntry.nsrRequired}">
											<af:panelGroup layout="horizontal">
												<af:inputText columns="8" rows="1"
													value="#{timesheetEntry.row.nsrId}" id="nsrId2"
													readOnly="#{!timesheetEntry.nsrAppPermitSearchResultApplied}"
													autoSubmit="true"
													rendered="#{timesheetEntry.nsrAppPermitSearchResultApplied}"
													immediate="true" maximumLength="8">
												</af:inputText>
												<af:inputText columns="8" rows="1"
													value="#{timesheetEntry.row.nsrId}" id="nsrId"
													autoSubmit="true" maximumLength="8" immediate="true"
													rendered="#{!timesheetEntry.nsrAppPermitSearchResultApplied}">
												</af:inputText>
												<af:commandButton text="Search"
													action="#{timesheetEntry.startNsrAppPermitSearch}"
													useWindow="true" windowWidth="800" immediate="true"
													id="nsrAppPermitSearch" partialSubmit="true"
													windowHeight="1000" inlineStyle="padding-left:5px;"
													returnListener="#{timesheetEntry.nsrAppPermitSearchDialogDone}">
												</af:commandButton>
											</af:panelGroup>
										</afh:cellFormat>

										<afh:cellFormat width="1%"
											rendered="#{timesheetEntry.tvRequired}">
											<af:panelGroup layout="horizontal">
												<af:inputText columns="8" rows="1"
													value="#{timesheetEntry.row.tvId}" id="tvId2"
													readOnly="#{!timesheetEntry.tvAppPermitSearchResultApplied}"
													autoSubmit="true"
													rendered="#{timesheetEntry.tvAppPermitSearchResultApplied}"
													immediate="true" maximumLength="8">
												</af:inputText>
												<af:inputText columns="8" rows="1"
													value="#{timesheetEntry.row.tvId}" id="tvId"
													autoSubmit="true" maximumLength="8" immediate="true"
													rendered="#{!timesheetEntry.tvAppPermitSearchResultApplied}">
												</af:inputText>
												<af:commandButton text="Search"
													action="#{timesheetEntry.startTvAppPermitSearch}"
													useWindow="true" windowWidth="800" immediate="true"
													id="tvAppPermitSearch" partialSubmit="true"
													windowHeight="1000" inlineStyle="padding-left:5px;"
													returnListener="#{timesheetEntry.tvAppPermitSearchDialogDone}">
												</af:commandButton>
											</af:panelGroup>
										</afh:cellFormat>


										<afh:cellFormat>

											<af:inputText columns="4" rows="1" id="hours"
												value="#{timesheetEntry.row.hours}" maximumLength="4">
												<af:convertNumber type="number" pattern="##.#" />
											</af:inputText>
										</afh:cellFormat>
										<afh:cellFormat>

											<af:selectBooleanCheckbox id="overtime"
												value="#{timesheetEntry.row.overtime}" />
										</afh:cellFormat>
									</afh:rowLayout>
								</afh:tableLayout>

							</af:showDetailHeader>
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton disabled="#{timesheetEntry.readOnlyUser}"
										text="Submit" action="#{timesheetEntry.submit}" />
									<af:commandButton disabled="#{timesheetEntry.readOnlyUser}"
										text="Reset" action="#{timesheetEntry.reset}" immediate="true">
										<af:resetActionListener />
									</af:commandButton>
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>


				<af:objectSpacer width="100%" height="15" />

				<afh:rowLayout halign="center" partialTriggers="aqdStaff">
					<h:panelGrid border="1" rendered="#{timesheetEntry.hasRows}">
						<af:panelBorder>
							<af:showDetailHeader text="Timesheet Entries" disclosed="true">
								<af:table bandingInterval="1" banding="row" var="row"
									binding="#{timesheetEntry.rows.table}"
									rows="#{timesheetEntry.pageLimit}" width="98%"
									value="#{timesheetEntry.rows}"
									varStatus="timesheetEntriesTableVs">

									<af:column id="edit" formatType="text" width="50px"
										noWrap="true">
										<f:facet name="header">
											<af:outputText value="Row Id" />
										</f:facet>
										<af:panelHorizontal valign="middle">
											<af:commandLink useWindow="true" windowWidth="480"
												disabled="#{timesheetEntry.readOnlyUser}" windowHeight="500"
												returnListener="#{timesheetEntry.dialogDone}"
												action="#{timesheetEntry.startToEditTimesheetEntry}">
												<af:inputText value="#{timesheetEntriesTableVs.index+1}"
													readOnly="true" valign="middle">
													<af:convertNumber pattern="00000" />
												</af:inputText>
											</af:commandLink>
										</af:panelHorizontal>
									</af:column>

									<af:column sortProperty="date" sortable="true" width="30px"
										formatType="text" noWrap="true">
										<f:facet name="header">
											<af:outputText value="Date" />
										</f:facet>
										<af:panelHorizontal valign="middle">
											<af:inputText readOnly="true" value="#{row.date}">
												<f:convertDateTime type="date" dateStyle="full"
													pattern="MM/dd/yyyy" />
											</af:inputText>
										</af:panelHorizontal>
									</af:column>

									<af:column sortProperty="function" sortable="true"
										formatType="text" width="60px" noWrap="true">
										<f:facet name="header">
											<af:outputText value="Function" />
										</f:facet>
										<af:panelHorizontal valign="middle">
											<af:selectOneChoice value="#{row.function}" readOnly="true"
												unselectedLabel="">
												<mu:selectItems value="#{timesheetEntry.functions}" />
											</af:selectOneChoice>
										</af:panelHorizontal>
									</af:column>

									<af:column sortProperty="section" sortable="true"
										formatType="text" width="50px" noWrap="true">
										<f:facet name="header">
											<af:outputText value="Section" />
										</f:facet>
										<af:panelHorizontal valign="middle">
											<af:selectOneChoice value="#{row.section}" readOnly="true">
												<mu:selectItems value="#{timesheetEntry.sections}" />
											</af:selectOneChoice>
										</af:panelHorizontal>
									</af:column>

									<af:column sortProperty="nsrId" sortable="true" width="30px"
										formatType="text" noWrap="true">
										<f:facet name="header">
											<af:outputText value="NSR" />
										</f:facet>
										<af:panelHorizontal valign="middle">
											<af:inputText readOnly="true" value="#{row.nsrId}" />
										</af:panelHorizontal>
									</af:column>

									<af:column sortProperty="tvId" sortable="true" width="50px"
										formatType="text" noWrap="true">
										<f:facet name="header">
											<af:outputText value="Title V" />
										</f:facet>
										<af:panelHorizontal valign="middle">
											<af:inputText readOnly="true" value="#{row.tvId}" />
										</af:panelHorizontal>
									</af:column>

									<af:column sortProperty="hours" sortable="true"
										formatType="number" width="45px">
										<f:facet name="header">
											<af:outputText value="Hours" />
										</f:facet>
										<af:panelHorizontal valign="middle" inlineStyle="float:right;">
											<af:inputText readOnly="true" value="#{row.hours}" />
										</af:panelHorizontal>
									</af:column>

									<af:column sortProperty="overtimeYesNo" sortable="true"
										formatType="text">
										<f:facet name="header">
											<af:outputText value="OT" />
										</f:facet>
										<af:panelHorizontal valign="middle">
											<af:inputText readOnly="true" value="#{row.overtimeYesNo}" />
										</af:panelHorizontal>
									</af:column>

									<af:column sortProperty="invoicedYesNo" sortable="true"
										formatType="text">
										<f:facet name="header">
											<af:outputText value="Invoiced" />
										</f:facet>
										<af:panelHorizontal valign="middle">
											<af:inputText readOnly="true" value="#{row.invoicedYesNo}" />
										</af:panelHorizontal>
									</af:column>

									<f:facet name="footer">
										<afh:rowLayout halign="center">
											<af:panelButtonBar>
												<af:commandButton text="Generate Monthly Report"
													useWindow="true" windowHeight="300" windowWidth="400"
													action="#{timesheetEntry.generateMonthlyTimesheetReport}" />
												<af:commandButton
													actionListener="#{tableExporter.printTable}"
													onclick="#{tableExporter.onClickScript}"
													text="Printable view" />
												<af:commandButton
													actionListener="#{tableExporter.excelTable}"
													onclick="#{tableExporter.onClickScript}"
													text="Export to excel" />
											</af:panelButtonBar>
										</afh:rowLayout>
									</f:facet>
								</af:table>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

			</af:page>
		</af:form>
	</af:document>
</f:view>

