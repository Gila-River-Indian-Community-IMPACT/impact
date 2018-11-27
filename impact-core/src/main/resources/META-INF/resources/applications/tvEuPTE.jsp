<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup>
	<%
		/**************** CAP Table *************************************/
	%>
	<af:showDetailHeader disclosed="true"
		text="Emission Unit Potential to Emit (PTE)">
		<af:outputText inlineStyle="font-size:75%;color:#666"
			value="\"Potential to emit\" means the maximum capacity of a stationary source to emit any air pollutant 
      under its physical and operational design. Any physical or operational limitation on the capacity of a 
      source to emit an air pollutant, including air pollution control equipment and restrictions on hours of 
      operation or on the type or amount of material combusted, stored or processed, shall be treated as part 
      of its design if the limitation is enforceable by the EPA and the Division.." />
		<af:objectSpacer height="1" />
		<af:outputText value="Criteria Pollutants : "
			inlineStyle="font-size: 13px;font-weight:bold" />
		<af:table emptyText=" " var="tvCapEmissions" bandingInterval="1"
			banding="row"
			rendered="#{not empty applicationDetail.selectedScenario}"
			value="#{applicationDetail.selectedScenario.capEmissions}" width="98%" id="capTable">
			<af:column id="capPollutantCol" formatType="text"
				headerText="Pollutant">
				<af:selectOneChoice id="capPollutantChoice" readOnly="true"
					value="#{tvCapEmissions.pollutantCd}">
					<f:selectItems value="#{applicationDetail.tvCapPollutantDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="capPTECol"
				headerText="Potential to Emit (PTE) (tons/year)" formatType="number">
				<af:switcher defaultFacet="capPollutant"
					facetName="#{tvCapEmissions.pollutantCd == '7439921' ? 
              'hapPollutant' : 'capPollutant'}">
					<f:facet name="capPollutant">
						<af:inputText readOnly="#{! applicationDetail.editMode}"
							value="#{tvCapEmissions.pteTonsYr}" columns="30"
							id="potentialToEmitTonYr"
							label="Potential to Emit (PTE) (tons/year)"
							valueChangeListener="#{applicationDetail.refreshAttachments}"
							autoSubmit="true">
							<mu:convertSigDigNumber
								pattern="#{applicationDetail.capEmissionsValueFormat}" />
						</af:inputText>
					</f:facet>
					<f:facet name="hapPollutant">
						<af:inputText readOnly="#{! applicationDetail.editMode}"
							value="#{tvCapEmissions.pteTonsYr}" columns="30"
							id="potentialToEmitTonYr"
							label="Potential to Emit (PTE) (tons/year)"
							valueChangeListener="#{applicationDetail.refreshAttachments}"
							autoSubmit="true">
							<mu:convertSigDigNumber
								pattern="#{applicationDetail.hapEmissionsValueFormat}" />
						</af:inputText>
					</f:facet>
				</af:switcher>
			</af:column>
			<af:column headerText="Basis for Determination"
				id="capPTEDetBasisCol">
				<af:selectOneChoice id="capPTEDetBasisDropdown"
					readOnly="#{! applicationDetail.editMode}"
					label="Basis for Determination" unselectedLabel=" "
					value="#{tvCapEmissions.pteDeterminationBasis}"
					valueChangeListener="#{applicationDetail.refreshAttachments}"
					autoSubmit="true">
					<f:selectItems
						value="#{applicationDetail.patveuPteDeterminationBasisDefs}" />
				</af:selectOneChoice>
			</af:column>
		</af:table>
		<af:objectSpacer height="5" />
		<%
			/**************** HAPS Table *************************************/
		%>
		<af:outputText value="Hazardous Air Pollutants (HAPs) : "
			rendered="#{not empty applicationDetail.selectedScenario}"
			inlineStyle="font-size: 13px;font-weight:bold" />
		<af:table emptyText=" " var="tvHapEmissions" bandingInterval="1"
			banding="row"
			value="#{applicationDetail.selectedScenario.hapEmissions}"
			width="98%" id="tvHapsTable">
			<f:facet name="selection">
				<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
			</f:facet>
			<af:column id="hapPollutantCol" formatType="text"
				headerText="Pollutant">
				<af:selectOneChoice id="hapPollutantChoice" readOnly="true"
					value="#{tvHapEmissions.pollutantCd}"
					inlineStyle="#{tvHapEmissions.hapStat ? 'font-style: italic' : ''}">
					<f:selectItems value="#{applicationDetail.tvHapPollutantDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="hapPTECol"
				headerText="Potential to Emit (PTE) (tons/year)" formatType="number">
				<af:inputText id="hapPTEText"
					readOnly="#{! applicationDetail.editMode}"
					value="#{tvHapEmissions.pteTonsYr}" columns="10" maximumLength="40"
					label="Potential to Emit (PTE) (tons/year)*"
					inlineStyle="#{tvHapEmissions.hapStat ? 'font-style: italic' : ''}">
					<mu:convertSigDigNumber
						pattern="#{applicationDetail.hapEmissionsValueFormat}" />
				</af:inputText>

			</af:column>
			<af:column id="hapPTEDetBasisCol"
				headerText="Basis for Determination" formatType="text"
				inlineStyle="#{tvHapEmissions.hapStat ? 'font-style: italic' : ''}">
				<af:selectOneChoice id="AppTvBasisForDeterminationDropdown"
					readOnly="true" label="Basis for Determination*"
					unselectedLabel=" " inlineStyle="color: #000000;"
					value="#{tvHapEmissions.pteDeterminationBasis}"
					valueChangeListener="#{applicationDetail.refreshAttachments}"
					autoSubmit="true">
					<f:selectItems
						value="#{applicationDetail.patveuPteDeterminationBasisDefs}" />
				</af:selectOneChoice>
			</af:column>
			<f:facet name="footer">
				<af:panelButtonBar>
					<af:commandButton text="Add" id="addHapPollutant" useWindow="true"
						windowWidth="700" windowHeight="300"
						rendered="#{applicationDetail.editMode}"
						returnListener="#{applicationDetail.refreshAttachments}"
						action="#{applicationDetail.startAddTVEmissionsInfo}">
						<t:updateActionListener
							property="#{applicationDetail.pollutantType}"
							value="#{applicationDetail.HAPTAC}" />
					</af:commandButton>

					<af:commandButton text="Delete Selected HAPs"
						rendered="#{applicationDetail.editMode}"
						actionListener="#{applicationDetail.initActionTable}"
						action="#{applicationDetail.deleteEmissions}">
					</af:commandButton>

					<af:inputText id="hapPTETotal"
						rendered="#{!applicationDetail.editMode}" readOnly="true"
						columns="50"
						value="#{applicationDetail.selectedScenario.hapsTotal} (tons/year)"
						label="Total HAPs*"
						inlineStyle="#{tvHapEmissions.hapStat ? 'font-style: italic' : ''}">
					</af:inputText>

				</af:panelButtonBar>
			</f:facet>
		</af:table>
		<af:objectSpacer height="2" />
		<af:outputText id="hapsFootnoteText"
			inlineStyle="font-size:75%;color:#666"
			value="* Based on the sum of all pollutants provided in this table." />
		<af:objectSpacer height="5" />
		<%
			/**************** GHG Table *************************************/
		%>
		<af:outputText value="Greenhouse Gases (GHGs) : "
			inlineStyle="font-size: 13px;font-weight:bold" />
		<af:table emptyText=" " var="tvGhgEmissions" bandingInterval="1"
			banding="row"
			value="#{applicationDetail.selectedScenario.ghgEmissions}"
			width="98%" id="tvGHGTable">
			<f:facet name="selection">
				<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
			</f:facet>
			<af:column id="ghgPollutantCol" formatType="text"
				headerText="Pollutant">
				<af:selectOneChoice id="ghgPollutantChoice" readOnly="true"
					value="#{tvGhgEmissions.pollutantCd}"
					inlineStyle="#{tvGhgEmissions.hapStat ? 'font-style: italic' : ''}">
					<f:selectItems value="#{applicationDetail.ghgPollutantDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="ghgPTECol"
				headerText="Potential to Emit (PTE) (tons/year)" formatType="number">
				<af:inputText id="ghgPTEText"
					readOnly="#{! applicationDetail.editMode}"
					value="#{tvGhgEmissions.pteTonsYr}" columns="10" maximumLength="40"
					label="PTE (ton/year)"
					inlineStyle="#{tvGhgEmissions.hapStat ? 'font-style: italic' : ''}">
					<mu:convertSigDigNumber
						pattern="#{applicationDetail.ghgEmissionsValueFormat}" />
				</af:inputText>
			</af:column>
			<af:column id="ghgPTEDetBasisCol"
				headerText="Basis for Determination" formatType="text"
				inlineStyle="#{tvGhgEmissions.hapStat ? 'font-style: italic' : ''}">
				<af:selectOneChoice id="ghgAppTvBasisForDeterminationDropdown"
					readOnly="true" label="Basis for Determination*"
					unselectedLabel=" " inlineStyle="color: #000000;"
					value="#{tvGhgEmissions.pteDeterminationBasis}"
					valueChangeListener="#{applicationDetail.refreshAttachments}"
					autoSubmit="true">
					<f:selectItems
						value="#{applicationDetail.patveuPteDeterminationBasisDefs}" />
				</af:selectOneChoice>
			</af:column>
			<f:facet name="footer">
				<af:panelButtonBar>
					<af:commandButton text="Add" id="addGHGPollutant" useWindow="true"
						windowWidth="700" windowHeight="300"
						rendered="#{applicationDetail.editMode}"
						returnListener="#{applicationDetail.refreshAttachments}"
						action="#{applicationDetail.startAddTVEmissionsInfo}">
						<t:updateActionListener
							property="#{applicationDetail.pollutantType}"
							value="#{applicationDetail.GHG}" />
					</af:commandButton>
					<af:commandButton text="Delete Selected GHGs"
						rendered="#{applicationDetail.editMode}"
						actionListener="#{applicationDetail.initActionTable}"
						action="#{applicationDetail.deleteEmissions}">
					</af:commandButton>
				</af:panelButtonBar>
			</f:facet>
		</af:table>
		<af:objectSpacer height="5" />
		<%
			/**************** OTHER Table *************************************/
		%>
		<af:outputText value="Other Regulated Pollutants : "
			inlineStyle="font-size: 13px;font-weight:bold" />
		<af:table emptyText=" " var="tvOthEmissions" bandingInterval="1"
			value="#{applicationDetail.selectedScenario.othEmissions}"
			width="98%" id="tvOTHTable">
			<f:facet name="selection">
				<af:tableSelectMany rendered="#{applicationDetail.editMode}" />
			</f:facet>
			<af:column id="othPollutantCol" formatType="text"
				headerText="Pollutant">
				<af:selectOneChoice id="othPollutantChoice" readOnly="true"
					value="#{tvOthEmissions.pollutantCd}"
					inlineStyle="#{tvOthEmissions.hapStat ? 'font-style: italic' : ''}">
					<f:selectItems value="#{applicationDetail.othPollutantDefs}" />
				</af:selectOneChoice>
			</af:column>
			<af:column id="othPTECol"
				headerText="Potential to Emit (PTE) (tons/year)" formatType="number">
				<af:inputText id="othPTEText"
					readOnly="#{! applicationDetail.editMode}"
					value="#{tvOthEmissions.pteTonsYr}" columns="10" maximumLength="40"
					label="PTE (ton/year)"
					inlineStyle="#{tvOthEmissions.hapStat ? 'font-style: italic' : ''}">
					<mu:convertSigDigNumber
						pattern="#{applicationDetail.othEmissionsValueFormat}" />
				</af:inputText>
			</af:column>
			<af:column id="othPTEDetBasisCol"
				headerText="Basis for Determination*" formatType="text"
				inlineStyle="#{tvOthEmissions.hapStat ? 'font-style: italic' : ''}">
				<af:selectOneChoice id="othAppTvBasisForDeterminationDropdown"
					readOnly="true" label="Basis for Determination*"
					unselectedLabel=" " inlineStyle="color: #000000;"
					value="#{tvOthEmissions.pteDeterminationBasis}"
					valueChangeListener="#{applicationDetail.refreshAttachments}"
					autoSubmit="true">
					<f:selectItems
						value="#{applicationDetail.patveuPteDeterminationBasisDefs}" />
				</af:selectOneChoice>
			</af:column>
			<f:facet name="footer">
				<af:panelButtonBar>
					<af:commandButton text="Add" id="addOTHPollutant" useWindow="true"
						windowWidth="700" windowHeight="300"
						rendered="#{applicationDetail.editMode}"
						returnListener="#{applicationDetail.refreshAttachments}"
						action="#{applicationDetail.startAddTVEmissionsInfo}">
						<t:updateActionListener
							property="#{applicationDetail.pollutantType}"
							value="#{applicationDetail.OTH}" />
					</af:commandButton>
					<af:commandButton text="Delete Selected Other Regulated Pollutants"
						rendered="#{applicationDetail.editMode}"
						actionListener="#{applicationDetail.initActionTable}"
						action="#{applicationDetail.deleteEmissions}">
					</af:commandButton>
				</af:panelButtonBar>
			</f:facet>
		</af:table>
		<af:outputText inlineStyle="font-size: 12px;  font-style: italic;"
			rendered="#{not empty applicationDetail.selectedScenario}"
			value="*Attach a description and calculations, as appropriate, documenting
       the basis for the PTE for each pollutant.  If permit limits are used, 
       no additional information is needed.  For other basis methods:" />
		<af:objectSpacer height="1" />
		<af:panelList maxColumns="1" id="pteAttachmentDescription"
			shortDesc="Attachment description">
			<af:outputText inlineStyle="font-size: 12px;  font-style: italic;"
				rendered="#{not empty applicationDetail.selectedScenario}"
				value="Manufacturer’s Data – Attach a copy of the information from the manufacturer" />
			<af:outputText inlineStyle="font-size: 12px;  font-style: italic;"
				rendered="#{not empty applicationDetail.selectedScenario}"
				value="Test Results – Indicate test data and results" />
			<af:outputText inlineStyle="font-size: 12px;  font-style: italic;"
				rendered="#{not empty applicationDetail.selectedScenario}"
				value="Similar Source Test Results – Attach a test result summary with a description of how this is a similar source" />
			<af:outputText inlineStyle="font-size: 12px;  font-style: italic;"
				rendered="#{not empty applicationDetail.selectedScenario}"
				value="GRICalc – Attach a printout of results" />
			<af:outputText inlineStyle="font-size: 12px;  font-style: italic;"
				rendered="#{not empty applicationDetail.selectedScenario}"
				value="Tanks Program – Attach a printout of results" />
			<af:outputText inlineStyle="font-size: 12px;  font-style: italic;"
				rendered="#{not empty applicationDetail.selectedScenario}"
				value="AP-42 – Indicate AP-42 factor and publication date" />
			<af:outputText inlineStyle="font-size: 12px;  font-style: italic;"
				rendered="#{not empty applicationDetail.selectedScenario}"
				value="Other – Attach a description of the method used" />

		</af:panelList>
		<af:panelForm partialTriggers="engineOnlyReq"
			rendered="#{applicationDetail.selectedEU.fpEU.emissionUnitTypeCd == 'ENG'}">
			<af:outputText value="Engine Only Requirements"
				inlineStyle="font-size: 15px;font-weight:bold" />
			<af:selectInputDate id="engineOrderDt" label="Engine Order Date :"
				readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.selectedScenario.engOrderDate}">
				<af:validateDateTimeRange minimum="1900-01-01" />
			</af:selectInputDate>
			<af:selectInputDate id="engineManufactureDt"
				label="Engine Manufactured Date :"
				readOnly="#{! applicationDetail.editMode}"
				value="#{applicationDetail.selectedScenario.engManufactureDate}">
				<af:validateDateTimeRange minimum="1900-01-01" />
			</af:selectInputDate>
		</af:panelForm>
	</af:showDetailHeader>

</af:panelGroup>
