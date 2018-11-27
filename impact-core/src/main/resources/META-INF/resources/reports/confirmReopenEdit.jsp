<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Confirm Emissions Inventory Reopen Edit">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Confirm Emissions Inventory Reopen Edit">
				<af:panelForm maxColumns="1">
						<af:outputFormatted
							value="
							<p>
								You have clicked 'Reopen Edit' for a submitted Emissions Inventory. If you continue, the Emissions Inventory will be 're-opened', you may make changes,
								and you must click 'Recompute Totals/Save' to finish. Any Service Catalog and Facility Inventory Changes that have been made since the Emissions Inventory 
								was submitted will be applied.
								This capability can be used if:
							</p>
							<ul>
								<li>there has been a change in the Service Catalog for the reporting year (e.g. the pollutant or fee configuration has changed) OR</li>
								<li>the Emissions Inventory needs to be associated with a different facility inventory version OR</li>
								<li>the associated Facility Inventory has been modified (e.g. the Allowable Emissions amount for a pollutant or capture and control 
								percentages from CEs have changed)</li>
								<li>there have been changes to definition lists (e.g. pollutants)</li>
								<li>other corrections are required.</li>
							</ul>
							<p>
								If you continue by clicking 'Confirm Reopen Edit', you will be able to associate the Emissions Inventory with a different Facility Inventory
								 by clicking 'Associate with Different Facility Inventory'. You will need to navigate to the Process & Emissions Detail page and click 'Edit Emissions' 
								 to add emissions values for any pollutants that are added as a result of Service Catalog or Facility Inventory changes.
							</p>
							<p>
								After saving changes on the detail pages, you must also navigate back to the facility-level Emissions Inventory Summary page and finalize the changes
								by clicking 'Recompute Totals/Save'.  The Emissions Inventory will be updated to incorporate any Service Catalog, Facility Inventory, 
								and/or emissions changes you have made. Use the 'Validate' button to identify potential errors and review the final result carefully to ensure that your 
								intended changes were applied.
							</p>
							<p>
								Note: By using this feature, you may be modifying emissions values previously provided by the industry user. 
								If any changes are applied as a result of updates to the Service Catalog or the Facility Inventory, previous values will NOT be preserved.
							</p> 
								Only continue if you are sure that you want to change this previously-submitted Emissions Inventory. If you would like for the submitted Emissions 
								Inventory to be preserved, use 'Create Revised Emissions Inventory' instead of 'Reopen Edit'.
							</p>
							<p>
								Note: Do not use 'Reopen Edit' if you simply need to update the invoice details. Instead, click 'Cancel' and then click 'Edit Invoice Tracking Info'.
							</p>" />
				</af:panelForm>
				<af:objectSpacer width="100%" height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Confirm Reopen Edit"
							action="#{reportProfile.toReopenEdit}"/>
						<af:commandButton
							text="Cancel"
							action="#{reportProfile.cancelReopenEdit}" immediate="true" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
