<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="EditFacilityProfile">
		    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
			<af:page var="foo" value="#{menuModel.model}">
				<afh:rowLayout halign="center" rendered="#{reportProfile.facility.versionId == -1}">
					<af:outputFormatted
						value="The emissions inventory currently is associated with the Current facility inventory which is preserved.  Clicking Create Inventory will create an editable version of Current facility inventory and associate the emissions inventory with that." />
				</afh:rowLayout>
				<afh:rowLayout halign="center" rendered="#{reportProfile.facility.versionId != -1}">
					<af:outputFormatted
						value="The emissions inventory currently is associated with a preserved historic facility inventory.  Clicking Create Inventory will create a version of the Facility Inventory just for this emissions inventory that you can modify." />
				</afh:rowLayout>
				<afh:rowLayout halign="center" rendered="#{reportProfile.facility.versionId != -1}">
					<af:outputFormatted  inlineStyle="color: orange; font-weight: bold;"
						value="<br><br><b>If there are changes needed for the emissions inventory and in the current Facility Inventory, the changes have to made separately in both facility inventories.</b>" />
				</afh:rowLayout>
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Create Inventory"
							action="#{reportProfile.editFacilityProfile}" />
						<af:objectSpacer width="10" />
						<af:commandButton text="Cancel" action="#{reportProfile.closeDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
