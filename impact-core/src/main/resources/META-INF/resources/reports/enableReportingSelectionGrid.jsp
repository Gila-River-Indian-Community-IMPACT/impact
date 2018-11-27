<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid columns="1" border="1" width="900px">
		<afh:rowLayout halign="left">
			<af:panelButtonBar>
				<af:commandLink id="selectAllLnk" text="Select All"
					action="#{reportProfile.selectAllSCEmissionsReports}" />
				<af:outputText value="|" />
				<af:commandLink id="selectNoneLnk" text="Select None"
					action="#{reportProfile.unSelectAllSCEmissionsReports}" />
			</af:panelButtonBar>
		</afh:rowLayout>
		<af:panelGroup layout="vertical" rendered="true">
			<afh:rowLayout halign="center">

				<af:table id="scEmissionsReports" width="900px"
					value="#{reportProfile.scEmissionsReports}" bandingInterval="1"
					banding="row" var="scEmissionsReport"
					rows="#{reportProfile.pageLimit}" emptyText=" ">

					<af:column id="selected" headerText="Enable Reporting" width="5%"
						sortable="true" sortProperty="selected" formatType="icon">
						<af:selectBooleanCheckbox id="enableReporting"
							label="Enable Reporting" autoSubmit="true"
							value="#{scEmissionsReport.selected}" />
					</af:column>

					<af:column id="reportingYear" headerText="Reporting Year"
						width="5%" sortable="true" sortProperty="reportingYear"
						formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:inputText readOnly="true"
								value="#{scEmissionsReport.reportingYear}" />
						</af:panelHorizontal>
					</af:column>

					<af:column id="contentTypeCd" headerText="Content Type" width="14%"
						sortable="true" sortProperty="contentTypeCd" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:selectOneChoice label="Content Type"
								value="#{scEmissionsReport.contentTypeCd}" readOnly="true">
								<f:selectItems value="#{serviceCatalog.contentTypes}" />
							</af:selectOneChoice>
						</af:panelHorizontal>
					</af:column>

					<af:column id="regulatoryRequirementCd"
						headerText="Regulatory Requirement" width="25%" sortable="true"
						sortProperty="regulatoryRequirementCd" formatType="text">
						<af:panelHorizontal valign="middle" halign="left">
							<af:selectOneChoice label="Regulatory Requirement"
								value="#{scEmissionsReport.regulatoryRequirementCd}"
								readOnly="true">
								<f:selectItems
									value="#{serviceCatalog.regulatoryRequirementTypes}" />
							</af:selectOneChoice>
						</af:panelHorizontal>
					</af:column>

					<af:column formatType="text"
						headerText="Facility Selection Criteria">
						<af:column id="facilityLocation" headerText="Facility Location"
							width="17%" sortable="true" sortProperty="shapeLabel"
							formatType="text">
							<af:panelLabelAndMessage for="shapeId" label="Facility Location">
								<af:commandLink text="#{scEmissionsReport.shapeLabel}"
									action="#{scEmissionsReport.displayFacilityLocationOnMap}">
								</af:commandLink>
							</af:panelLabelAndMessage>
						</af:column>

						<af:column id="permitClassCds" headerText="Facility Class"
							width="12%" formatType="text">
							<af:selectManyListbox id="permitClassCds" label="Facility Class"
								value="#{scEmissionsReport.permitClassCds}" readOnly="true">
								<f:selectItems
									value="#{facilityReference.permitClassDefs.items[(empty scEmissionsReport.permitClassCds ? '' : scEmissionsReport.permitClassCds)]}" />
							</af:selectManyListbox>
						</af:column>

						<af:column id="treatPartialAsFullPeriodFlag"
							headerText="Require Emissions Reporting if Facility Class Met During Period?"
							width="5%" sortable="true"
							sortProperty="treatPartialAsFullPeriodFlag" formatType="text">
							<af:selectOneChoice id="treatPartialAsFullPeriodFlag"
								unselectedLabel=" " readOnly="true"
								label="Require Emissions Reporting if Facility Class Met During Period?"
								value="#{scEmissionsReport.treatPartialAsFullPeriodFlag}">
								<f:selectItem itemLabel="Yes" itemValue="Y" />
								<f:selectItem itemLabel="No" itemValue="N" />
							</af:selectOneChoice>
						</af:column>

						<af:column id="facilityTypeCds" headerText="Facility Type"
							width="17%" formatType="text">

							<af:selectManyListbox id="facilityTypeCds" label="Facility Type"
								readOnly="true" value="#{scEmissionsReport.facilityTypeCds}">
								<f:selectItems
									value="#{facilityReference.facilityTypeTextDefs.items[(empty scEmissionsReport.facilityTypeCds ? '' : scEmissionsReport.facilityTypeCds)]}" />
							</af:selectManyListbox>
						</af:column>
					</af:column>
				</af:table>

			</afh:rowLayout>
		</af:panelGroup>
	</h:panelGrid>
</afh:rowLayout>