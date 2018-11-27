<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Confirm Transfer Emission Units">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page>
				<afh:rowLayout halign="center">
					<h:panelGrid>
						<af:panelForm>
							<af:outputFormatted
								value="Confirmation needed. You have requested to transfer equipment from one facility (source) to another (destination). This operation will result in the selected Emission Units, Emission Processes, Control Equipment, and Release Points being copied from the source facility to the destination facility. Click Yes to confirm the operation, or click No to cancel." />
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>
				<af:objectSpacer width="100%" height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Yes" actionListener="#{trEu.submitTrEu}" />
						<af:commandButton text="No" actionListener="#{trEu.noConfirm}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>