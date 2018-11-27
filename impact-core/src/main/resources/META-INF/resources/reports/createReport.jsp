<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Create Another Emissions Inventory">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}">
				<af:panelForm partialTriggers="reportCreate existing reportCreateContentTypeCd">
					<af:selectOneChoice label="For reporting year:" autoSubmit="true"
						id="reportCreate" value="#{reportProfile.create_year}" rendered="#{reportProfile.numberEnabledYears > 0}">
						<f:selectItems value="#{reportProfile.reportYears}" />
					</af:selectOneChoice>
					<af:selectOneChoice label="For content type:" autoSubmit="true"
						id="reportCreateContentTypeCd" value="#{reportProfile.createContentTypeCd}" rendered="#{reportProfile.create_year > 0}"
						valueChangeListener="#{reportProfile.resetCopyFlag}">
						<f:selectItems value="#{reportProfile.reportYearContentTypes}" />
					</af:selectOneChoice>
					<afh:rowLayout rendered="#{reportProfile.numberEnabledYears == 0}"
						halign="center">
						<af:outputFormatted
							value="Emissions Inventory reporting is not yet enabled for this facility for any year. Please contact AQD and inquire as to when you will be able to enter Emissions Inventory information." />
					</afh:rowLayout>
					<afh:rowLayout halign="center" rendered="#{not empty reportProfile.create_year && not empty reportProfile.createContentTypeCd}">
						<af:outputFormatted
							value="#{reportProfile.createReviseMsg}#{(reportProfile.createRptUsingProfile && !reportProfile.creatingRevisedRpt)?'&nbsp;&nbsp;This emissions inventory will be associated with the current facility inventory.':''}" />
					</afh:rowLayout>
					<afh:rowLayout rendered="#{empty reportProfile.selectRptList}"
						halign="center">
						<af:outputFormatted
							rendered="#{reportProfile.createRptUsingProfile}"
							value="<b>Important: To create an accurate emissions inventory with minimal errors, you must first update and reconcile all information within your facility inventory. All emissions inventories directly refer to information in your facility inventory.  If you have not yet updated your facility inventory and ensured that the information contained within it is correct, do not proceed with creating an emissions inventory. Instead, review and correct your facility inventory information first.</b>" />
					</afh:rowLayout>
					<af:selectBooleanCheckbox label="Copy data from existing emissions inventory :"
						id="existing" rendered="#{reportProfile.createContentTypeCd != null && reportProfile.enableRptCopy}"
						value="#{reportProfile.copyFromExistingRpt}" autoSubmit="true" />
					<afh:rowLayout rendered="#{reportProfile.copyFromExistingRpt}"
						halign="center">
						<af:outputFormatted
							value="#{(!reportProfile.creatingRevisedRpt)? 'To minimize errors, this emissions inventory will be associated with the current facility inventory, not the facility inventory used at the time of last submission.&nbsp;&nbsp;If necessary, you may associate the emissions inventory with a different facility inventory by clicking <b>Associate with Different Facility Inventory</b> after creating the emissions inventory.<br><br>':''}Select the emissions inventory you wish to copy data from for the new emissions inventory you are creating by clicking on the emissions inventory number. 
								<br><br><b>Note:</b> If the emissions inventory that you select was generated from an import file, then the emissions calculation method will be changed from <b>AQD Generated</b> to <b>Emissions</b> in the emissions inventory that will be created." />
					</afh:rowLayout>
					<af:objectSpacer height="10" />
					<afh:rowLayout halign="center"
						rendered="#{reportProfile.copyFromExistingRpt}">
						<h:panelGrid border="1">
							<jsp:include flush="true" page="erCopyTable.jsp" />
						</h:panelGrid>
					</afh:rowLayout>
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Create"
								action="#{reportProfile.startCreateReportDone}"
								disabled="#{reportProfile.invalidYear}"
								rendered="#{reportProfile.create_year != null && !reportProfile.copyFromExistingRpt && reportProfile.createContentTypeCd != null}">
							</af:commandButton>
							<af:commandButton text="Cancel" immediate="true"
								action="#{reportProfile.closeDialog}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
</f:view>
