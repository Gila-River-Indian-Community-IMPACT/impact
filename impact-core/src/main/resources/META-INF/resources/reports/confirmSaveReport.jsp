<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Confirm marking the facility did not operate for the reporting year">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Confirm marking the facility did not operate for the reporting year">
				<af:panelForm maxColumns="1">
					<afh:rowLayout halign="left">
						<af:outputFormatted
							value="Clicking <b>Save</b> will remove the existing emission information already recorded under these Emission Units." />
					</afh:rowLayout>
					<afh:rowLayout halign="center" width="100%">
						<h:panelGrid border="1" align="center">
							<af:table value="#{reportProfile.haveEmissions}"
								bandingInterval="1" id="procTab"
								banding="row" var="emissionUnit">
								<af:column sortProperty="epaEmuId" sortable="true" width="50"
									formatType="icon" headerText="Emission Unit">
									<af:outputText value="#{emissionUnit.epaEmuId}" rendered="#{!emissionUnit.inGroup}"/>
								</af:column>
								<af:column sortProperty="description" sortable="true"
									formatType="text" headerText="Company Equipment ID">
									<af:outputText value="#{emissionUnit.description}" />
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
						</h:panelGrid>
					</afh:rowLayout>
				</af:panelForm>
				<af:objectSpacer width="100%" height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Save"
							action="#{reportProfile.confirmedSaveReport}"/>
						<af:commandButton
							text="Cancel"
							action="#{reportProfile.cancelEditTS}" immediate="true" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
