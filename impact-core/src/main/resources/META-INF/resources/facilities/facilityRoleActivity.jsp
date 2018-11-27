<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Defined Workflow Tasks">
		<af:form>
			<af:page title="Defined Workflow Tasks">

				<af:objectSpacer height="10" />
				<af:outputFormatted rendered="#{!facilityProfile.isActivityNull}"
					value="The IMPACT system has found the following Workflow Activities associated with the selected Facility Role." />
				
				<af:outputFormatted rendered="#{facilityProfile.isActivityNull}"
					inlineStyle="color: orange; font-weight: bold;"
					value="The IMPACT system has no Workflow Activities associated with the selected Facility Role." />
				<af:objectSpacer height="20" />

				<af:table value="#{facilityProfile.facUserRoleAactivityWrapper}"
					bandingInterval="1" width="98%" var="roleActivity"
					binding="#{facilityProfile.facUserRoleAactivityWrapper.table}" banding="row">
					
					<af:column headerText="Workflow Type" sortProperty="c01"
						sortable="true" headerNoWrap="true" noWrap="true"
						formatType="text" width="30%">
						<af:outputText value="#{roleActivity.processDsc}" />
					</af:column>

					<af:column headerText="Workflow Name" sortProperty="c02"
						sortable="true" headerNoWrap="true" noWrap="true"
						formatType="text" width="35%">
						<af:outputText value="#{roleActivity.processTemplateDsc}" />
					</af:column>

					<af:column headerText="Activity" sortProperty="c03"
						sortable="true" headerNoWrap="true" noWrap="true"
						formatType="text" width="35%">
						<af:outputText value="#{roleActivity.activityTemplateNm}" />
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

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Close" immediate="true"
							action="#{facilityProfile.closeDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>