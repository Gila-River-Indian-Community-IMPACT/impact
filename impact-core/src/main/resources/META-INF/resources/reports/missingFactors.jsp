<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Missing FIRE Factors">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="missingFactorsForm">
			<f:subview id="missingFactors">
				<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
					<afh:rowLayout halign="center">
						<af:panelHeader messageType="Information"
							text="Missing FIRE Factors" />
					</afh:rowLayout>
				</afh:tableLayout>
				<af:panelGroup>
					<afh:rowLayout halign="left">
						<af:outputFormatted
							value="The following lists actively used SCCs that do not have a FIRE entry. Actively used SCCs only consists of emission processes with an emissions unit that is operating and was installed before or during #{reportProfile.missingFactorsYear}." />
					</afh:rowLayout>
				</af:panelGroup>
				<af:objectSpacer height="20" width="1" />
				<af:table value="#{reportProfile.missingFactorsWrapper}"
					binding="#{reportProfile.missingFactorsWrapper.table}"
					bandingInterval="1" banding="row" var="factor" width="98%">
					<af:column sortProperty="facilityId" sortable="true" noWrap="true"
						formatType="text" headerText="Facility ID">
						<af:outputText value="#{factor.facility.facilityId}" />
<%-- 						<af:commandLink action="#{facilityProfile.submitProfile}" --%>
<%-- 							text="#{factor.facility.facilityId}"> --%>
<%-- 							<t:updateActionListener property="#{facilityProfile.fpId}" --%>
<%-- 								value="#{factor.facility.fpId}" /> --%>
<%-- 							<t:updateActionListener --%>
<%-- 								property="#{menuItem_facProfile.disabled}" value="false" /> --%>
<%-- 						</af:commandLink> --%>
					</af:column>

					<af:column sortProperty="name" sortable="true" formatType="text"
						headerText="Facility Name">
						<af:outputText value="#{factor.facility.name}" />
					</af:column>

					<af:column sortProperty="epaEmuId" sortable="true"
						formatType="text" headerText="Emissions Unit">
						<af:outputText value="#{factor.eu.epaEmuId}" />
					</af:column>

					<af:column sortProperty="sccId" sortable="true" formatType="text"
						headerText="SCC">
						<af:outputText value="#{factor.ep.sccCode.sccId}" />
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
				<af:objectSpacer height="20" />
				<af:panelForm id="closeMissingFactors">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Close"
								action="#{reportProfile.closeDialog}" immediate="true" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</f:subview>
		</af:form>
	</af:document>

</f:view>