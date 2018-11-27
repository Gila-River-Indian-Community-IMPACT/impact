<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" rendered="true">
	<af:showDetailHeader text="Monitor Tracking Details" disclosed="true">

		<%
            /* Content begin */
            %>
		<h:panelGrid columns="1" width="98%">
			<af:panelGroup>
				<afh:rowLayout halign="center">
					<af:table
						value="#{continuousMonitorDetail.continuousMonitorEqtWrapper}"
						binding="#{continuousMonitorDetail.continuousMonitorEqtWrapper.table}"
						bandingInterval="1" banding="row" id="continuousMonitorEqtTab"
						width="#{continuousMonitorDetail.EATableWidth}"
						var="continuousMonitorEqt"
						rows="#{continuousMonitorDetail.pageLimit}" emptyText=" "
						varStatus="continuousMonitorEqtTableVs">

						<af:column id="edit" formatType="text" headerText="Row Id"
							width="5%" noWrap="true">
							<af:commandLink useWindow="true" windowWidth="600"
								windowHeight="600" inlineStyle="padding-left:5px;"
								returnListener="#{continuousMonitorDetail.dialogDone}"
								rendered="#{!facilityProfile.publicApp}"
								action="#{continuousMonitorDetail.startToEditContinuousMonitorEqt}">
								<af:inputText value="#{continuousMonitorEqtTableVs.index+1}"
									readOnly="true" valign="middle">
									<af:convertNumber pattern="000" />
								</af:inputText>
							</af:commandLink>
							<af:outputText value="#{continuousMonitorEqtTableVs.index+1}" 
								rendered="#{facilityProfile.publicApp}" >
								<af:convertNumber pattern="000" />
							</af:outputText>
						</af:column>

						<af:column formatType="text" headerText="Manufacturer"
							sortProperty="manufacturerName" sortable="true"
							id="manufacturerName" width="10%">
							<af:inputText value="#{continuousMonitorEqt.manufacturerName}"
								readOnly="true" valign="middle">
							</af:inputText>
						</af:column>

						<af:column formatType="text" headerText="Model Number"
							sortProperty="modelNumber" sortable="true" id="modelNumber"
							width="10%">
							<af:inputText value="#{continuousMonitorEqt.modelNumber}"
								readOnly="true" valign="middle">
							</af:inputText>
						</af:column>

						<af:column formatType="text" headerText="Serial Number"
							sortProperty="serialNumber" sortable="true" id="serialNumber"
							width="10%">
							<af:inputText value="#{continuousMonitorEqt.serialNumber}"
								readOnly="true" valign="middle">
							</af:inputText>
						</af:column>

						<af:column headerText="QA/QC Submitted Date" formatType="text"
							width="5%" sortProperty="QAQCSubmittedDate" sortable="true"
							id="QAQCSubmittedDate">
							<af:selectInputDate label="QA/QC Submitted Date" readOnly="true"
								valign="middle" required="true"
								value="#{continuousMonitorEqt.QAQCSubmittedDate}">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
						</af:column>

						<af:column headerText="QA/QC Accepted Date" formatType="text"
							width="5%" sortProperty="QAQCAcceptedDate" sortable="true"
							id="QAQCAcceptedDate">
							<af:selectInputDate label="QA/QC Accepted Date" readOnly="true"
								valign="middle" required="true"
								value="#{continuousMonitorEqt.QAQCAcceptedDate}">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
						</af:column>

						<af:column headerText="Install Date" formatType="text" width="5%"
							sortProperty="installDate" sortable="true" id="installDate">
							<af:selectInputDate label="Install Date" readOnly="true"
								valign="middle" required="true"
								value="#{continuousMonitorEqt.installDate}">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
						</af:column>

						<af:column headerText="Removal Date" formatType="text" width="5%"
							sortProperty="removalDate" sortable="true" id="removalDate">
							<af:selectInputDate label="Removal Date" readOnly="true"
								valign="middle" required="true"
								value="#{continuousMonitorEqt.removalDate}">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
						</af:column>

						<af:column sortable="true" sortProperty="addedBy"
							formatType="text" width="10%" headerText="Added By"
							rendered="#{!facilityProfile.publicApp}">
							<af:selectOneChoice readOnly="true"
								value="#{continuousMonitorEqt.addedBy}">
								<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
							</af:selectOneChoice>
						</af:column>

						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
									<af:commandButton text="Add Monitor"
										id="addContinuousMonitorEqt"
										action="#{continuousMonitorDetail.startToAddContinuousMonitorEqt}"
										useWindow="true" windowWidth="600" windowHeight="600"
										inlineStyle="padding-left:5px;"
										returnListener="#{continuousMonitorDetail.dialogDone}"
										disabled="#{!continuousMonitorDetail.continuousMonitorEditAllowed || facilityProfile.disabledUpdateButton}" 
										rendered="#{continuousMonitorDetail.internalApp}"/>
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelGroup>
							</afh:rowLayout>
						</f:facet>
					</af:table>
					<%
                  /* Physical Monitor list end */
                  %>
				</afh:rowLayout>


				<af:objectSpacer height="10" />

			</af:panelGroup>
		</h:panelGrid>
		<%
            /* Content end */
        %>

	</af:showDetailHeader>

</af:panelGroup>


