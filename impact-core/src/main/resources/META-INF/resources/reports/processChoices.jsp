<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Select Process to Intially Create The Emissions Group">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Select Process to Intially Create The Emissions Group">
				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<af:outputLabel
						value="Click on the Process ID you wish to be the first process of a new Emissions Group:" />
				</afh:rowLayout>
				<af:objectSpacer width="100%" height="5" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1">
						<af:table value="#{reportProfile.processGroupTable}"
							bandingInterval="1" id="procTab" banding="row" var="process">
							<af:column sortProperty="processId" sortable="true"
								formatType="text" noWrap="true" headerText="Process ID">
								<af:commandLink action="#{reportProfile.createGroupFromPop}">
									<af:outputText value="#{process.processId}"/>
									<af:setActionListener to="#{reportProfile.firstGrpProcess}"
										from="#{process}" />
								</af:commandLink>
							</af:column>
							<af:column sortProperty="scc" sortable="true" noWrap="true"
								formatType="text" headerText="SCC Code">
								<af:outputText value="#{process.scc}" />
							</af:column>
							<af:column sortProperty="epaEmuId" sortable="true" noWrap="true"
								formatType="icon" headerText="Emission Unit">
								<af:outputText value="#{process.epaEmuId}" />
							</af:column>
							<af:column sortProperty="pDesc" sortable="true" noWrap="true"
								formatType="text" headerText="Process Description">
								<af:outputText value="#{process.description}" />
							</af:column>
							<f:facet name="footer">
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton actionListener="#{tableExporter.printTable}"
											onclick="#{tableExporter.onClickScript}"
											text="Printable view" />
										<af:commandButton actionListener="#{tableExporter.excelTable}"
											onclick="#{tableExporter.onClickScript}"
											text="Export to excel" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</f:facet>
						</af:table>
					</h:panelGrid>
				</afh:rowLayout>
				<af:objectSpacer width="100%" height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Cancel"
							action="#{reportProfile.cancelEditTS}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
