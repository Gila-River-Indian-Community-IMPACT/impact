<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Selection of FIRE Entry">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<af:panelGroup layout="vertical">
				<af:showDetailHeader text="Choice of FIRE Factors" disclosed="true">
					<af:table value="#{reportProfile.fireWrapper}" bandingInterval="1"
						emptyText="No applicable FIRE Factors.  Contact your EPA Agent."
						binding="#{reportProfile.fireWrapper.table}" id="fireTab"
						banding="row" width="98%" var="fireLine">
						
						
						<af:column sortable="true" sortProperty="selected" formatType="icon" headerText="Select">
							<af:selectBooleanRadio  group="RadioButtons"
								rendered="true" value="#{fireLine.selected}">
							</af:selectBooleanRadio >
						</af:column>
						
						<af:column formatType="text" sortProperty="c00" sortable="true"
							headerText="Fire ID">
							<af:outputText value="#{fireLine.factorId}"
							/>
							<af:outputText value="expired FIRE factor"
								rendered="#{fireLine.notActive}"
								inlineStyle="color: orange; font-weight: bold;"
								shortDesc="This Fire factor is no longer active.  Pick a different Fire factor or different calculation method">
							</af:outputText>
						</af:column>
						<af:column headerText="Pollutant">
							<af:column sortProperty="c01" sortable="true" formatType="text"
								headerText="Name">
								<af:selectOneChoice value="#{fireLine.pollutantCd}"
									readOnly="true">
									<f:selectItems
										value="#{facilityReference.pollutantDefs.items[(empty fireLine.pollutantCd ? '' : fireLine.pollutantCd)]}" />
								</af:selectOneChoice>
							</af:column>
							<af:column sortProperty="c02" sortable="true" formatType="text"
								headerText="Code">
								<af:outputText value="#{fireLine.pollutantCd}" />
							</af:column>
						</af:column>
						<af:column formatType="text" sortProperty="c03" sortable="true"
							headerText="Material">
							<af:selectOneChoice value="#{fireLine.material}" readOnly="true">
								<f:selectItems
									value="#{facilityReference.materialDefs.items[(empty fireLine.material ? '' : fireLine.material)]}" />
							</af:selectOneChoice>
						</af:column>
						<af:column formatType="text" sortProperty="c04" sortable="true"
							headerText="Action">
							<af:outputText value="#{fireLine.action}" />
						</af:column>
						<af:column formatType="text" sortProperty="c05" sortable="true"
							headerText="Units">
							<af:selectOneChoice value="#{fireLine.measure}" readOnly="true">
								<f:selectItems
									value="#{facilityReference.unitDefs.items[(empty fireLine.measure ? '' : fireLine.measure)]}" />
							</af:selectOneChoice>
						</af:column>
						<af:column headerText="Uncontrolled Emissions Factor">
							<af:column formatType="text" sortProperty="c06" sortable="true"
								headerText="Factor">
								<af:outputText value="#{fireLine.factor}"
									 />
							</af:column>
							<af:column formatType="text" sortProperty="c07" sortable="true"
								headerText="Formula">
								<af:outputText value="#{fireLine.formula}"
									/>
							</af:column>
							<af:column formatType="text" sortProperty="c08" sortable="true"
								headerText="Numerator">
								<af:selectOneChoice value="#{fireLine.unit}" readOnly="true">
									<f:selectItems
										value="#{facilityReference.unitDefs.items[(empty fireLine.unit ? '' : fireLine.unit)]}" />
								</af:selectOneChoice>
							</af:column>
							<af:column formatType="text" sortProperty="c09" sortable="true"
								headerText="Denominator">
								<af:selectOneChoice value="#{fireLine.measure}" readOnly="true">
									<f:selectItems
										value="#{facilityReference.unitDefs.items[(empty fireLine.measure ? '' : fireLine.measure)]}" />
								</af:selectOneChoice>
							</af:column>
						</af:column>
						<af:column sortProperty="c10" sortable="true" headerText="Notes">
							<af:outputText value="#{fireLine.notes}" />
						</af:column>
						<af:column formatType="text" sortProperty="c11" sortable="true"
							headerText="Quality">
							<af:outputText value="#{fireLine.quality}" />
						</af:column>
						<af:column formatType="text" sortProperty="c12" sortable="true"
							headerText="Origin">
							<af:outputText value="#{fireLine.origin}" />
						</af:column>
						<af:column formatType="icon" sortProperty="c13" sortable="true"
							headerText="First Active">
							<af:outputText value="#{fireLine.created}" />
						</af:column>
						<af:column formatType="icon" sortProperty="c14" sortable="true"
							headerText="First Inactive">
							<af:outputText value="#{fireLine.deprecated}" />
						</af:column>
						<af:column formatType="text" sortProperty="c15" sortable="true"
							headerText="AP42 Section">
							<af:outputText value="#{fireLine.ap42Section}" />
						</af:column>
						<af:column formatType="text" sortProperty="c16" sortable="true"
							headerText="Reference Description">
							<af:outputText value="#{fireLine.refDesc}" />
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
					<af:objectSpacer width="10" height="10" />
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Select FIRE Factor" id="FIREbutton"
								rendered="#{!reportProfile.fireEditable && reportProfile.fireWrapper.table.rowCount > 1}"
								action="#{reportProfile.editFire}">
							</af:commandButton>
							<af:commandButton text="Close"
								rendered="#{!reportProfile.fireEditable}"
								action="#{reportProfile.closeCancelFIRE}" immediate="true" />
							<af:commandButton text="Save"
								rendered="#{reportProfile.fireEditable}"
								action="#{reportProfile.saveFireSelection}" />
							<af:commandButton text="Cancel"
								rendered="#{reportProfile.fireEditable}"
								action="#{reportProfile.closeCancelFIRE}" immediate="true" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:showDetailHeader>
			</af:panelGroup>
		</af:form>
	</af:document>
</f:view>
