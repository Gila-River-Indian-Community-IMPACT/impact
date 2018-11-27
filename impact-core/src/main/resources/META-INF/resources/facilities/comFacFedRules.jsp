<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelForm
	partialTriggers="MactRuleCheckbox NeshapsRuleCheckbox NspsRuleCheckbox PsdRuleCheckbox NsrRuleCheckbox">
	<af:panelForm rows="2" maxColumns="3">
		<af:selectBooleanCheckbox label="Subject to Part 60 NSPS:"
			value="#{facilityProfile.facility.nsps}"
			readOnly="#{facilityProfile.demReadOnlyAttr}" id="NspsRuleCheckbox"
			autoSubmit="true" />
		<af:selectBooleanCheckbox label="Subject to Part 61 NESHAP:"
			value="#{facilityProfile.facility.neshaps}"
			readOnly="#{facilityProfile.demReadOnlyAttr}"
			id="NeshapsRuleCheckbox" autoSubmit="true" />
		<af:selectBooleanCheckbox label="Subject to Part 63 NESHAP:"
			value="#{facilityProfile.facility.mact}"
			readOnly="#{facilityProfile.demReadOnlyAttr}" id="MactRuleCheckbox"
			autoSubmit="true" />
		<af:selectBooleanCheckbox label="Subject to PSD:"
			value="#{facilityProfile.facility.psd}"
			readOnly="#{facilityProfile.demReadOnlyAttr}" id="PsdRuleCheckbox"
			autoSubmit="true" />
		<af:selectBooleanCheckbox label="Subject to non-attainment NSR:"
			value="#{facilityProfile.facility.nsrNonattainment}"
			readOnly="#{facilityProfile.demReadOnlyAttr}" id="NsrRuleCheckbox"
			autoSubmit="true" />
		<af:selectBooleanCheckbox
			label="Subject to 112(r) Accidental Release Prevention:"
			value="#{facilityProfile.facility.sec112}"
			readOnly="#{! facilityProfile.editable}" />
		<af:selectBooleanCheckbox label="Subject to Title IV Acid Rain:"
			value="#{facilityProfile.facility.tivInd}"
			readOnly="#{! facilityProfile.editable}" />
	</af:panelForm>

	<af:panelHeader
		text="Rules & Regs Applicability - Part 60 NSPS Subparts"
		size="4"
		rendered="#{facilityProfile.dapcUser && facilityProfile.facility.nsps}" />
	<af:panelHeader
		text="Rules & Regs Applicability - Part 60 NSPS Subparts" size="4"
		rendered="#{!facilityProfile.dapcUser && facilityProfile.facility.nsps}" />
	<afh:rowLayout halign="center">
		<af:panelForm rows="1" maxColumns="1" width="650">
			<af:table value="#{facilityProfile.nspsSubpartsWrapper}"
				bandingInterval="1" banding="row" var="nspsSubpart"
				rows="#{facilityProfile.pageLimit}"
				rendered="#{facilityProfile.facility.nsps}"
				binding="#{facilityProfile.nspsSubpartsWrapper.table}" width="98%">
				<f:facet name="selection">
					<af:tableSelectMany
						rendered="#{facilityProfile.dapcUpdateRenderComp}" />
				</f:facet>
				<af:column sortProperty="c01" sortable="true" formatType="text"
					headerText="Part 60 NSPS Subpart">
					<af:selectOneChoice value="#{nspsSubpart.value}"
						readOnly="#{facilityProfile.demReadOnlyAttr}">
						<f:selectItems
							value="#{infraDefs.nspsSubparts.items[(empty nspsSubpart.value ? '' : nspsSubpart.value)]}" />
					</af:selectOneChoice>
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Add Subpart"
								rendered="#{facilityProfile.dapcUpdateRenderComp}"
								action="#{facilityProfile.addNspsSubpart}">
							</af:commandButton>
							<af:commandButton text="Delete Selected Subparts"
								rendered="#{facilityProfile.dapcUpdateRenderComp}"
								action="#{facilityProfile.deleteNspsSubparts}">
							</af:commandButton>
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</af:panelForm>
	</afh:rowLayout>
	
	<af:panelHeader
		text="Rules & Regs Applicability - Part 61 NESHAP Subparts" size="4"
		rendered="#{facilityProfile.facility.neshaps}" />
	<afh:rowLayout halign="center">
		<af:panelForm rows="1" maxColumns="1" width="600"
			rendered="#{facilityProfile.facility.neshaps}">
			<af:table value="#{facilityProfile.neshapsSubpartsWrapper}"
				bandingInterval="1" banding="row" var="neshapsSubpart"
				rows="#{facilityProfile.pageLimit}"
				rendered="#{facilityProfile.facility.neshaps}"
				binding="#{facilityProfile.neshapsSubpartsWrapper.table}"
				width="98%">
				<f:facet name="selection">
					<af:tableSelectMany
						rendered="#{facilityProfile.dapcUpdateRenderComp}" />
				</f:facet>
				<af:column sortProperty="c01" sortable="true" formatType="text"
					headerText="Part 61 NESHAP Subpart">
					<af:selectOneChoice value="#{neshapsSubpart.pollutantCd}"
						readOnly="#{facilityProfile.demReadOnlyAttr}">
						<f:selectItems
							value="#{infraDefs.neshapsSubparts.items[(empty neshapsSubpart.pollutantCd ? '' : neshapsSubpart.pollutantCd)]}" />
					</af:selectOneChoice>
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Add Subpart"
								rendered="#{facilityProfile.dapcUpdateRenderComp}"
								action="#{facilityProfile.addNeshapsSubpart}">
							</af:commandButton>
							<af:commandButton text="Delete Selected Subparts"
								rendered="#{facilityProfile.dapcUpdateRenderComp}"
								action="#{facilityProfile.deleteNeshapsSubparts}">
							</af:commandButton>
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</af:panelForm>
	</afh:rowLayout>


	<af:panelHeader
		text="Rules & Regs Applicability - Part 63 NESHAP Subparts" size="4"
		rendered="#{facilityProfile.facility.mact}" />
	<afh:rowLayout halign="center">
		<af:panelForm rows="1" maxColumns="1" width="600"
			rendered="#{facilityProfile.facility.mact}">
			<af:table value="#{facilityProfile.mactSubpartsWrapper}"
				bandingInterval="1" banding="row" var="mactSubpart"
				rows="#{facilityProfile.pageLimit}"
				rendered="#{facilityProfile.facility.mact}"
				binding="#{facilityProfile.mactSubpartsWrapper.table}" width="98%">
				<f:facet name="selection">
					<af:tableSelectMany
						rendered="#{facilityProfile.dapcUpdateRenderComp}" />
				</f:facet>
				<af:column sortProperty="c01" sortable="true" formatType="text"
					headerText="Part 63 NESHAP Subpart">
					<af:selectOneChoice value="#{mactSubpart.value}"
						readOnly="#{facilityProfile.demReadOnlyAttr}">
						<f:selectItems
							value="#{infraDefs.mactSubparts.items[(empty mactSubpart.value ? '' : mactSubpart.value)]}" />
					</af:selectOneChoice>
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Add Subpart"
								rendered="#{facilityProfile.dapcUpdateRenderComp}"
								action="#{facilityProfile.addMactSubpart}">
							</af:commandButton>
							<af:commandButton text="Delete Selected Subparts"
								rendered="#{facilityProfile.dapcUpdateRenderComp}"
								action="#{facilityProfile.deleteMactSubparts}">
							</af:commandButton>
							<af:commandButton actionListener="#{tableExporter.printTable}"
								onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
								onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</f:facet>
			</af:table>
		</af:panelForm>
	</afh:rowLayout>
</af:panelForm>