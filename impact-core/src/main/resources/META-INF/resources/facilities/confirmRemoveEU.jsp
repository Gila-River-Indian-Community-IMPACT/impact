<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Emission Unit Remove Operation">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:messages />
			<af:page>
				<afh:rowLayout halign="center">
					<h:panelGrid>
						<af:panelForm>
							<af:outputFormatted value="#{facilityProfile.removeEUMessage}" />
						</af:panelForm>
					</h:panelGrid>
				</afh:rowLayout>
				<af:objectSpacer width="100%" height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Yes"
							actionListener="#{facilityProfile.removeEU}">
						</af:commandButton>
						<af:commandButton text="No"
							actionListener="#{facilityProfile.cancelRemoveEU}">
						</af:commandButton>
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

