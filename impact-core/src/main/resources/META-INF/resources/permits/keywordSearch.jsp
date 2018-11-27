<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Keywords search">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Permit search">
				<%@ include file="../permits/header.jsp"%>

				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000">
						<af:panelBorder>
							<af:showDetailHeader text="Search Criteria" disclosed="true">
								<af:panelForm rows="1" maxColumns="3">

									<af:inputText label="Search String" columns="80"
										value="#{keywordSearch.searchString}" />
									<af:objectSpacer width="20" height="5" />
									<af:inputText id="hitLimit"
										label="Maximum Number of Documents Returned"
										rendered="#{keywordSearch.stars2Admin}" columns="5"
										maximumLength="5" value="#{keywordSearch.hitLimit}" />
								</af:panelForm>
								<af:objectSpacer width="100%" height="15" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Submit"
											action="#{keywordSearch.search}" />
										<af:commandButton text="Reset" action="#{keywordSearch.reset}" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:showDetailHeader>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="1000"
						rendered="#{keywordSearch.hasSearchResults}">
						<af:showDetailHeader text="Document List" disclosed="true">

							<af:table emptyText=" " value="#{keywordSearch.docs}" width="99%"
								var="doc" rows="#{keywordSearch.pageLimit}">

								<af:column headerText="Description" sortProperty="description"
									sortable="true" formatType="text">
									<af:goLink targetFrame="_blank" destination="#{doc.docURL}"
										text="#{doc.description}" />
								</af:column>
								<af:column headerText="Facility Id" sortProperty="facilityID"
									sortable="true" formatType="text">
									<af:outputText value="#{doc.facilityID}" />
								</af:column>
								<af:column headerText="Facility Name" formatType="text">
									<h:outputText binding="#{facilityName.outputText}"
										value="#{facilityName.facilityName}">
										<f:attribute name="facilityId" value="#{doc.facilityID}" />
									</h:outputText>
								</af:column>
								<af:column headerText="Last modified by" rendered="false"
									sortProperty="lastModifiedBy" sortable="true" formatType="text">
									<af:selectOneChoice readOnly="true"
										value="#{doc.lastModifiedBy}">
										<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
									</af:selectOneChoice>
								</af:column>
								<af:column headerText="Date Issued Final"
									sortProperty="lastModifiedTS" sortable="true" formatType="text">
									<af:selectInputDate readOnly="true"
										value="#{doc.lastModifiedTS}" />
								</af:column>

								<f:facet name="footer">
									<afh:rowLayout halign="center">
										<af:panelButtonBar>
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
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
