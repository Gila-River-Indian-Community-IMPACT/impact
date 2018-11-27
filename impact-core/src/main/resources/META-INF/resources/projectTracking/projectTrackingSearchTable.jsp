<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1" rendered="#{projectTrackingSearch.hasSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Project List" disclosed="true">

				<af:table value="#{projectTrackingSearch.resultsWrapper}"
					binding="#{projectTrackingSearch.resultsWrapper.table}"
					bandingInterval="1" banding="row" var="project"
					rows="#{projectTrackingSearch.pageLimit}">

					<af:column sortProperty="c01" sortable="true" noWrap="true"
						formatType="text" headerText="Project ID">
						<af:commandLink action="#{projectTrackingDetail.refresh}"
							text="#{project.number}">
							<t:updateActionListener property="#{projectTrackingDetail.projectId}"
								value="#{project.id}" />
							<t:updateActionListener property="#{projectTrackingDetail.projectTypeCd}"
								value="#{project.typeCd}" />	
							<t:updateActionListener
								property="#{menuItem_projectTrackingDetail.disabled}" value="false" />
						</af:commandLink>
					</af:column>

					<af:column sortProperty="c02" sortable="true" formatType="text"
						headerText="Project Name">
						<af:outputText value="#{project.name}" />
					</af:column>

					<af:column sortProperty="c03" sortable="true" formatType="text"
						headerText="Project Type">
						<af:outputText
							value="#{projectTrackingReference.projectTypeDefs.itemDesc[(empty project.typeCd ? '' : project.typeCd)]}" />
					</af:column>

					<af:column sortProperty="c04" sortable="true" formatType="text"
						headerText="Project Status">
						<af:outputText
							value="#{projectTrackingReference.projectStateDefs.itemDesc[(empty project.stateCd ? '' : project.stateCd)]}" />
					</af:column>

					<af:column sortProperty="c06" sortable="true" formatType="text"
						headerText="Project Stage">
						<af:outputText
							value="#{projectTrackingReference.projectStageDefs.itemDesc[(empty project.stageCd ? '' : project.stageCd)]}" />
					</af:column>

					<af:column sortProperty="c07" sortable="true" formatType="text"
						headerText="Outreach Category">
						<af:outputText
							value="#{projectTrackingReference.outreachCategoryDefs.itemDesc[(empty project.outreachCategoryCd ? '' : project.outreachCategoryCd)]}" />
					</af:column>

					<af:column sortProperty="c08" sortable="true" formatType="text"
						headerText="Grant Status">
						<af:outputText
							value="#{projectTrackingReference.grantStatusDefs.itemDesc[(empty project.grantStatusCd ? '' : project.grantStatusCd)]}" />
					</af:column>

					<af:column sortProperty="c09" sortable="true" formatType="text"
						headerText="Letter Type">
						<af:outputText
							value="#{projectTrackingReference.letterTypeDefs.itemDesc[(empty project.letterTypeCd ? '' : project.letterTypeCd)]}" />
					</af:column>
					
					<af:column sortProperty="c10" sortable="true" formatType="text"
						headerText="Contract Type">
						<af:outputText
							value="#{projectTrackingReference.contractTypeDefs.itemDesc[(empty project.contractTypeCd ? '' : project.contractTypeCd)]}" />
					</af:column>

					<af:column sortProperty="c11" sortable="true" formatType="text"
						headerText="Project Description">
						<af:outputText value="#{project.description}" />
					</af:column>
					
					<af:column sortProperty="c12" sortable="true" formatType="text"
						headerText="Project Summary">
						<af:outputText value="#{project.summary}" />
					</af:column>

					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>

			</af:showDetailHeader>
		</af:panelBorder>
	</h:panelGrid>
</afh:rowLayout>
