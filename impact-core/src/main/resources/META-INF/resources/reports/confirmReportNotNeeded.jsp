<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Confirm Emissions Inventory is Prior/Invalid">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Confirm Emissions Inventory is Prior/Invalid">
				<af:panelForm maxColumns="1">
						<af:outputFormatted
							value="
							<p>
								If you have two inventories for the same year, the most accurate inventory should be marked 'Approved', with the invalid inventory marked as 'Inventory Prior/Invalid' (if still in the Submitted State). 
								Do not mark an inventory as 'Inventory Prior/Invalid' until a valid, accurate inventory is submitted.
							</p>" />
				</af:panelForm>
				<af:objectSpacer width="100%" height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Mark Emissions Inventory Prior/Invalid"
							action="#{reportProfile.toStateNN}"/>
						<af:commandButton
							text="Cancel"
							action="#{reportProfile.cancelEditTS}" immediate="true" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
