<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Create Revised Emissions Inventory">
		    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}">
				<afh:rowLayout halign="center" rendered="#{reportProfile.rptInfo != null}">
					<af:outputFormatted
						value="<br><br>Clicking <b>Create Revised Emissions Inventory</b> will create a copy of this emissions inventory which you can then revise.  To minimize errors, this revised emissions inventory will be associated with the current facility inventory, not the facility detail used at the time of last submission.&nbsp;&nbsp;Associate it with a different facility inventory by clicking <b>Associate with Different Facility Inventory</b> after creating the emissions inventory.<br><br> 
							<b>Note:</b> If this emissions inventory was generated from an import file, then the emissions calculation method will be changed from <b>AQD Generated</b> to <b>Emissions</b> in the revised emissions inventory."/>
					<af:outputFormatted
						rendered="false"
						value="<br><br>Clicking <b>Create Revised Emissions Inventory</b> will create a NTV emissions inventory to replace this inventory"/>
				</afh:rowLayout>
				<afh:rowLayout halign="center" rendered="#{reportProfile.rptInfo == null}">
					<af:outputFormatted
						value="Failed to read information from reporting category table for requested year (or no reporting required), click cancel to return." />
				</afh:rowLayout>
				<afh:rowLayout halign="center">
					<af:objectSpacer width="100" height="20" />
				</afh:rowLayout>
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Create Revised Emissions Inventory"
							rendered="#{reportProfile.rptInfo != null}"
							action="#{reportProfile.startCreateReviseReportDone}" />
						<af:objectSpacer width="10" />
						<af:commandButton text="Cancel"
							action="#{reportProfile.closeDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
