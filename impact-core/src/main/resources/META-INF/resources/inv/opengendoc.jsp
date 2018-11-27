<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Invoice Documents">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<af:panelForm>

				<af:objectSpacer width="100%" height="15" />

				<af:outputText inlineStyle="font-weight:bold;"
					value="Emissions Inventory Fee Calculations." />
				<af:outputText
					rendered="#{reportProfile.emissionsInvoice.allowablesNotConfiguredInFacilityEu}"
					value="#{reportProfile.emissionsInvoice.allowablesNotConfiguredMessage}" />
				<af:outputText
					rendered="#{reportProfile.emissionsInvoice.allowablesConfiguredZeroInFacilityEu}"
					value="#{reportProfile.emissionsInvoice.allowablesConfiguredZeroMessage}" />
				<af:objectSpacer width="100%" height="20" />
				<af:showDetailHeader text="Explanation" disclosed="false">
					<af:outputFormatted styleUsage="instruction"
						value="<ul>
						<li>Emissions Inventory – Show Invoice Details:</li>
							<ul>
								<li>A button labeled ‘Show Invoice Details’ will be displayed at the bottom of any Emissions Inventory (EI) that has passed validation.</li>
								<li>Clicking on ‘Show Invoice Details’ button causes a popup titled ‘Emissions Inventory Fee Calculations’ to be displayed.</li>
								<li>The ‘Emissions Inventory Fee Calculations’ popup contains two collapsible tables:</li>
									<ul>
										<li>‘Emissions Inventory Pollutant-Fee Summary’ aka ‘first table’</li>
										<li>‘Emissions Inventory Pollutant Emissions Details per EU and Process’ aka ‘second table’</li>
									</ul>
								</li>
								<li>‘Emissions Inventory Pollutant-Fee Summary’ has the following columns:</li>
									<ul>
										<li>Pollutant Description</li>
										<li>Pollutant Code</li>
										<li>First Half Actual Total Tons</li>
										<li>Second Half Actual Total Tons</li>
										<li>Actual Total Tons</li>
										<li>Chargeable Total Tons</li>
										<li>Total Fee Per Pollutant</li>
									</ul>
								</li>
								<li>There is a row in the ‘Emissions Inventory Pollutant-Fee Summary’ table for each pollutant in the EI.</li>
								<li>Pollutants in the EI may or may not be chargeable.  An IMPACT Administrator can configure pollutants to be non-chargeable in the Service Catalog for a given year.</li>
								<li>An IMPACT Administrator can also configure pollutants to belong to a set of pollutants that are chargeable as a set. Sub-members of a superset are not chargeable.</li>
								<li>For each pollutant that is non-chargeable for either reason, a footnote symbol will be displayed next to the value and an associated footnote will be displayed underneath the first table.</li>
								<li>By default, the rows in the ‘Emissions Inventory Pollutant-Fee Summary’ table are sorted by Pollutant Description. </li>
								<li>The values in the ‘Emissions Inventory Pollutant-Fee Summary’ columns are populated as follows:</li>
									<ul>
										<li>Pollutant Description</li>
										<li>Pollutant Code</li>
										<li>First Half Actual Total Tons – Total of values in ‘First Half Actual Total Tons’ column of each row for this pollutant in the ‘Emissions Inventory Pollutant Emissions Details per EU and Process’ table.</li>
										<li>Second Half Actual Total Tons - Total of values in ‘Second Half Actual Total Tons’ column of each row for this pollutant in the ‘Emissions Inventory Pollutant Emissions Details per EU and Process’ table.</li>
										<li>Actual Total Tons - Total of values in ‘Actual Total Tons’ column of each row for this pollutant in the ‘Emissions Inventory Pollutant Emissions Details per EU and Process’ table.</li>
										<li>Chargeable Total Tons</li>
											<ul>
											<li>Total the values in the (Hidden) ‘Chargeable Total Tons’ column for this pollutant in the second table.</li>
											<li>Important Note: Chargeable total tons is capped at the value for ‘Chargeable Emissions Limit (TPY)’ value in the Service Catalog entry for the EI Reporting Year. Note: Only the IMPACT Administrator can access the Service Catalog.</li>
											<li>If Chargeable Total Tons amount for a given pollutant = Chargeable Emissions Limit (TPY), a footnote symbol will be displayed next to the value and an associated footnote will be displayed underneath the first table.</li>
											<li>For billing purposes, if the long term (Tons/Year) Allowable emissions amounts are configured in the facility inventory for any pollutant with a non-blank value AND the pollutant is configured in the Service Catalog to be billed based on Allowable, IMPACT will calculate the fee for that pollutant based on the Allowable amount instead of the actual emissions amount.  Otherwise, if the long term (Tons/Year) Allowable emissions amount is blank in the facility inventory for a given pollutant, IMPACT will calculate the fee based on the actual emissions amount. Note: if the long term (Tons/Year) Allowable emissions amount is 0 Tons/Year, the fee for that pollutant will be $0.00.</li>
											<li>If Chargeable Total Tons amount for a given pollutant is calculated based on the long term (Tons/Year) Allowable emissions amount in the facility inventory because the pollutant is configured to be billed based on Allowable instead of Actual amount, a footnote symbol will be displayed next to the value and an associated footnote will be displayed underneath the first table. This footnote symbol and text will differ from those that indicate that the amount is based on the Chargeable Emissions Limit (TPY).</li>
											<li>For billing purposes, if the long term (Tons/Year) Allowable emissions amounts are configured in the facility inventory for any PM pollutants, IMPACT will assume that the long term (Tons/Year) allowable amount for the most significant PM pollutant is the allowable amount for the chargeable PM. The chargeable PM is the most significant PM with a long term (Tons/Year) Allowable emissions amount configured in the facility inventory for a given EU (if any).  Else, the chargeable PM is the most significant PM in the EI for a given EU.</li>
											</ul>
										<li>Total Fee Per Pollutant</li>
											<ul>
											<li>For each pollutant, total the (Hidden) First Half Chargeable Total Tons and (Hidden) Second Half Chargeable Total Tons columns in the second table.</li>
											<li>Given that:</li>
												<ul>
												<li>X = total First Half Chargeable Total Tons for a given pollutant</li>
												<li>Y = total Second Half Chargeable Total Tons for a given pollutant</li>
												<li>Max = Chargeable Emissions Limit (TPY) from Service Catalog entry for Reporting Year of the EI, which has a value of 4,000 as of the 2013 reporting year</li>
												</ul>
											<li>If X >= max,</li>
												<ul>
												<li>Fee = 1st Half Per Ton Fee * max</li>
												</ul>
											<li>Else if X + Y >= max</li>
												<ul>
												<li>Fee = (1st Half Per Ton Fee * X) + (2nd Half Per Ton Fee * (max – X))</li>
												</ul>
											<li>Else</li>
												<ul>
												<li>Fee = (1st Half Per Ton Fee * X) + (2nd Half Per Ton Fee * Y)</li>
												</ul>
											</ul>
											<li>Total Fee Due = Total of values in Total Fee Per Pollutant column. If Total of values in Total Fee Per Pollutant column less than $500, Total Fee Due = $500</li>
										</ul>
								<li>‘Emissions Inventory Pollutant Emissions Details per EU and Process’ (second table) has the following columns: note that, while reviewing or validating the table values, the user should mentally keep track of the values for the columns marked as ‘Hidden’ as these hidden columns are used to calculate ‘Chargeable Total Tons’ and ‘Total Fee Per Pollutant’ in the ‘Emissions Inventory Pollutant-Fee Summary’ table (first table).</li>
									<ul>
									<li>Pollutant Description</li>
									<li>Pollutant Code</li>		
									<li>Emission Unit ID</li>	
									<li>Process ID</li>	
									<li>First Half Actual Total Tons</li>
									<li>Second Half Actual Total Tons</li>
									<li>Actual Total Tons - Total of First Half and Second Half Actual Total Tons</li>
									<li>First Half Allowable Total Tons	(Hidden)</li>
										<ul>	
																<li>First Half Allowable Tons for this EU = the long term (Tons/Year) Allowable Tons from facility inventory EU page * Hours of Operation (First Half Year)% from EI Process & Emissions Detail page. </li>
																<li>If there are multiple processes associated with an EU and the Hours of Operation (First Half Year)% are not the same across the processes, the above calculation will use the largest value and the associated calculation for Second Half Allowable Total Tons will use the associated Hours of Operation (Second Half Year)%. </li>
																<li>(Note: if 1st and 2nd half year fee are the same, Hours of Operation (First Half Year)% = 50).</li>
										</ul>
									<li>Second Half Allowable Total Tons (Hidden)</li>
										<ul>
																<li>Second Half Allowable Tons for this EU = the long term (Tons/Year) Allowable Tons from facility inventory EU page * Hours of Operation (Second Half Year)% from EI Process & Emissions Detail page. </li>
																<li>If there are multiple processes associated with an EU and the Hours of Operation (First Half Year)% are not the same across the processes, see the related note in the above row.</li>
																<li>(Note: if 1st and 2nd half year fee are the same, Hours of Operation (Second Half Year)% = 50).</li>
										</ul>
									<li>Allowable Total Tons		Total of First Half and Second Half Allowable Total Tons</li>
									<li>First Half Chargeable Total Tons (Hidden) If pollutant is configured in Service Catalog to be Billed Based on Allowable instead of Actual AND First Half Allowable Total Tons has a value, this column = First Half Allowable Total Tons. Otherwise, it = First Half Actual Total Tons.</li>
									<li>Second Half Chargeable Total Tons (Hidden) If pollutant is configured in Service Catalog to be Billed Based on Allowable instead of Actual AND Second Half Allowable Total Tons has a value, this column = Second Half Allowable Total Tons. Otherwise, it = Second Half Actual Tons.</li>
									<li>Chargeable Total Tons (Hidden) Total of First Half and Second Half Chargeable Total Tons</li>
									</ul>
								</li>
								<li>A button labeled ‘Generate Invoice’ at the bottom of the 'Emissions Inventory Fee Calculations' page will be active if the associated Emissions Inventory has been submitted.</li>								
							</ul>
						
					  </ul>" />
				</af:showDetailHeader>
				<af:showDetailHeader
					text="Emissions Inventory Pollutant-Fee Summary." disclosed="true">
					<af:table
						value="#{reportProfile.emissionsInvoice.emissTableWrapper}"
						bandingInterval="1"
						binding="#{reportProfile.emissionsInvoice.emissTableWrapper.table}"
						id="totalEmissionsTab" banding="row" width="98%" var="calcLine">
						<af:column formatType="text" headerText="Pollutant Description"
							sortable="true" sortProperty="fullPollutantDesc">
							<af:outputText value="#{calcLine.fullPollutantDesc}" />
						</af:column>
						<af:column formatType="text" headerText="Pollutant Code"
							sortable="true" sortProperty="pollutantCd">
							<af:outputText value="#{calcLine.pollutantCd}" />
						</af:column>
						<af:column formatType="number"
							headerText="First Half Actual Total Tons" sortable="true"
							sortProperty="firstHalfActualTotalTons">
							<af:outputText value="#{calcLine.firstHalfActualTotalTonsStr}" />
						</af:column>
						<af:column formatType="number"
							headerText="Second Half Actual Total Tons" sortable="true"
							sortProperty="secondHalfActualTotalTons">
							<af:outputText value="#{calcLine.secondHalfActualTotalTonsStr}" />
						</af:column>
						<af:column formatType="number" headerText="Actual Total Tons"
							sortable="true" sortProperty="actualTotalTons">
							<af:outputText value="#{calcLine.actualTotalTonsStr}" />
						</af:column>
						<af:column formatType="number" headerText="Chargeable Total Tons"
							sortable="true" sortProperty="chargeableTotalTons">
							<af:outputText value="#{calcLine.chargeableTotalTonsStr}" />
						</af:column>
						<af:column formatType="number"
							headerText="Total Fee Per Pollutant" sortable="true"
							sortProperty="totalFeePerPollutant">
							<af:outputText value="#{calcLine.totalFeePerPollutantFormatted}" />
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="Right">
							
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />

								<af:objectSpacer width="400" height="3" />
								<af:outputLabel value="Total Chargeable Tons (TPY):" />
								<af:objectSpacer width="7" height="5" />
								<af:outputText
									value="#{reportProfile.emissionsInvoice.totalChargeableTons}" />
								<af:outputLabel value=", Total Fee Due:" />
								<af:objectSpacer width="7" height="5" />
								<af:outputText
									value="#{reportProfile.emissionsInvoice.totalFormattedFee}" />
									
							</afh:rowLayout>
						</f:facet>

					</af:table>
				</af:showDetailHeader>
				<af:outputText
					value="(1) The Total Fee Per Pollutant is calculated Based on Allowable Emissions (TPY) configured for at least one emissions unit in the Facility Inventory." />
				<af:outputText
					value="(2) Some or all of this pollutant's emissions are configured in Service Catalog to be non-chargeable." />
				<af:outputText
					value="(3) This pollutant is non-chargeable because it is a member of a pollutant set that is chargeable as a set." />
				<af:outputText
					value="(4) The Total Fee Per Pollutant is based on the Chargeable Emissions Limit of #{reportProfile.emissionsInvoice.maxTotalEmissionsPerPollutant} Tons Per Year (TPY) configured in the Service Catalog." />

				<af:outputText
					value="#{reportProfile.emissionsInvoice.minFeeForEmissionInventory}" />

				<af:showDetailHeader
					text="Emissions Inventory Pollutant Emission Details per EU and Process."
					disclosed="true">
					<af:table
						value="#{reportProfile.emissionsInvoice.emissTableDetailWrapper}"
						bandingInterval="1"
						binding="#{reportProfile.emissionsInvoice.emissTableDetailWrapper.table}"
						id="totalEmissionsDetailsTab" banding="row" width="98%"
						var="calcLineDetail">

						<af:column formatType="text" headerText="Pollutant Description"
							sortable="true" sortProperty="pollutantDesc">
							<af:outputText value="#{calcLineDetail.pollutantDesc}" />
						</af:column>
						<af:column formatType="text" headerText="Pollutant Code"
							sortable="true" sortProperty="pollutantCd">
							<af:outputText value="#{calcLineDetail.pollutantCd}" />
						</af:column>
						<af:column formatType="text" headerText="Emission Unit ID"
							sortable="true" sortProperty="epaEmuID">
							<af:outputText value="#{calcLineDetail.epaEmuID}" />
						</af:column>
						<af:column formatType="text" headerText="Process ID"
							sortable="true" sortProperty="processID">
							<af:outputText value="#{calcLineDetail.processID}" />
						</af:column>
						<af:column formatType="number"
							headerText="First Half Actual Total Tons" sortable="true"
							sortProperty="firstHalfActualTotalTons">
							<af:outputText value="#{calcLineDetail.firstHalfActualTotalTons}" />
						</af:column>
						<af:column formatType="number"
							headerText="Second Half Actual Total Tons" sortable="true"
							sortProperty="secondHalfActualTotalTons">
							<af:outputText
								value="#{calcLineDetail.secondHalfActualTotalTons}" />
						</af:column>
						<af:column formatType="number" headerText="Actual Total Tons"
							sortable="true" sortProperty="actualTotalTons">
							<af:outputText value="#{calcLineDetail.actualTotalTons}" />
						</af:column>
						<af:column formatType="number" headerText="Allowable Total Tons"
							sortable="true" sortProperty="allowableTotalTons">
							<af:outputText value="#{calcLineDetail.allowableTotalTons}" />
						</af:column>

						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />

							</afh:rowLayout>
						</f:facet>

					</af:table>
				</af:showDetailHeader>
				<af:outputText
					value="#{reportProfile.emissionsInvoice.emissionsCalulationInfo}" />
				<af:outputText rendered="#{!reportProfile.submitted}"
					value="Emissions Inventory is not submitted so Invoice document can't be generated." />



				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton immediate="true" text="Generate Invoice"
							disabled="#{!reportProfile.submitted}" useWindow="true"
							windowWidth="500" windowHeight="300"
							action="#{reportProfile.generateInvoice}">
						</af:commandButton>
						<af:commandButton text="Cancel" immediate="true">
							<af:returnActionListener />
						</af:commandButton>
					</af:panelButtonBar>
				</f:facet>

			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
