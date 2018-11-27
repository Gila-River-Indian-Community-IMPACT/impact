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
				<h:panelGrid>
					<af:panelForm>
						<afh:rowLayout halign="center">
							<af:outputFormatted
								value="Clicking delete will remove this period and all of its emissions information from the report" />
						</afh:rowLayout>
						<af:objectSpacer height="10" />
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton text="Delete Period"
									action="#{reportProfile.deletePeriod}" />
								<af:objectSpacer width="10" />
								<af:commandButton text="Cancel"
									action="#{reportProfile.closeDialog}" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</af:panelForm>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>
