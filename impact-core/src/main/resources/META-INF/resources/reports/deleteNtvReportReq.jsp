<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Delete Period">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page>
				<afh:rowLayout halign="center">
					<h:panelGrid>
						<af:panelForm>
							<afh:rowLayout halign="center">
								<af:outputFormatted
									value="Clicking Delete Report will permanently delete the emissions inventory including contact information, emissions and any attachments" />
							</afh:rowLayout>
							<af:objectSpacer width="10" height="6"/>
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Delete Report"
										action="#{erNTVDetail.deleteNtvReport}" />
									<af:objectSpacer width="10" />
									<af:commandButton text="Cancel"
										action="#{erNTVDetail.closeDialog}" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
