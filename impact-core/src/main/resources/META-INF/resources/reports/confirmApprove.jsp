<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Confirm Approval Operation">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page>
				<afh:rowLayout halign="center">
					<h:panelGrid>
						<af:panelForm>
							<af:outputFormatted value="#{erNTVDetail.approveConfirmText}"
								inlineStyle="color: orange; font-weight: bold;" />
							<af:objectSpacer height="20" width="100%"/>
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Yes"
										action="#{erNTVDetail.confirmedApprove}">
									</af:commandButton>
									<af:objectSpacer height="10" width="15"/>
									<af:commandButton text="No"
										action="#{erNTVDetail.approveCancel}">
									</af:commandButton>
								</af:panelButtonBar>
							</afh:rowLayout>
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

