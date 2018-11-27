<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Company - Delete Company">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:messages />
			<f:subview id="Company">
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" columns="1" width="400">
						<af:panelBorder>
							<af:panelHeader text="Delete Company" size="0" />

							<af:panelForm>
								<afh:rowLayout halign="left">
									<af:outputFormatted value="#{companyProfile.message}" />
								</afh:rowLayout>

								<af:panelGroup
									rendered="#{companyProfile.company != null && !(empty companyProfile.company.externalCompanyId)}">
									<af:objectSpacer height="10" />
									<afh:rowLayout halign="center">
										<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
											value="Warning: This company has an assigned CROMERR ID." />
									</afh:rowLayout>
								</af:panelGroup>

								<af:panelGroup
									rendered="#{companyProfile.company != null && companyProfile.companyHasNotes}">
									<af:objectSpacer height="10" />
									<afh:rowLayout halign="center">
										<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
											value="Warning: This company has assigned notes." />
									</afh:rowLayout>
								</af:panelGroup>

								<af:objectSpacer height="10" />

								<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
									<afh:rowLayout halign="center">
										<af:outputFormatted
											value="Are you sure you would like to continue?" />
									</afh:rowLayout>
								</afh:tableLayout>

								<af:objectSpacer height="10" />
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Yes"
											action="#{companyProfile.applyCompanyDelete}" />
										<af:commandButton text="No"
											action="#{companyProfile.cancelCompanyDelete}"
											immediate="true" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</af:panelForm>
						</af:panelBorder>
					</h:panelGrid>
				</afh:rowLayout>

			</f:subview>
		</af:form>
	</af:document>
</f:view>



