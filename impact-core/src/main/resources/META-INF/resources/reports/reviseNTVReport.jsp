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
				<afh:rowLayout id="r1" halign="center">
					<af:outputFormatted
						value="<br><br>Clicking <b>Create Revised Emissions Inventory</b> will create a copy of this inventory which you can then revise." />
				</afh:rowLayout>
				<afh:rowLayout id="br" halign="center"
					rendered="#{erNTVDetail.decideModRpt}">
					<af:selectOneRadio id="whichRpt" required = "true"
					    label = "select the report year:"
						value="#{erNTVDetail.selectModRpt}">
						<f:selectItem id="b1" itemDisabled="#{erNTVDetail.evenYearModInfo == null}"
							itemLabel="#{empty erNTVDetail.evenYearModInfo? 'choice not available' : facilityReference.emissionReportsDefs.itemDesc[erNTVDetail.evenYearModInfo.category]} #{empty erNTVDetail.oddYearModInfo? '' : ' report for '} #{empty erNTVDetail.evenYearModInfo? '' : erNTVDetail.evenYearModInfo.year}"
							itemValue="1" />
						<f:selectItem id="b2" itemDisabled="#{erNTVDetail.oddYearModInfo == null}"
							itemLabel="#{empty erNTVDetail.oddYearModInfo? 'choice not available' : facilityReference.emissionReportsDefs.itemDesc[erNTVDetail.oddYearModInfo.category]} #{empty erNTVDetail.oddYearModInfo? '' : ' report for '} #{empty erNTVDetail.oddYearModInfo? '' : erNTVDetail.oddYearModInfo.year}"
							itemValue="2" />
					</af:selectOneRadio>
				</afh:rowLayout>
				<afh:rowLayout id="r2" halign="center">
					<af:objectSpacer width="100" height="20" />
				</afh:rowLayout>
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Create Revised Emissions Inventory"
							action="#{erNTVDetail.startCreateReviseReportDone}" />
						<af:objectSpacer width="10" />
						<af:commandButton text="Cancel"  immediate="true"
							action="#{erNTVDetail.closeDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
