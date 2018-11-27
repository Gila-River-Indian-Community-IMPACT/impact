<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Add Non-Chargeable Pollutant"
		inlineStyle="width:1060px;">
		<f:verbatim>
			<script>
				</f:verbatim>
				<h:outputText value="#{infraDefs.js}" />
				<f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:messages />
			<af:page>
				<af:panelForm>
					<af:objectSpacer width="100%" height="15" />
					<af:outputFormatted
						value="Adding a pollutant will indicate that the emissions for that pollutant are excluded from fee
					calculations." />
					<af:objectSpacer width="100%" height="10" />

					<af:panelForm rows="2" maxColumns="1">
						<af:inputText label="Pollutant Code" readOnly="true"
							value="#{serviceCatalog.addedNcPollutant.pollutantCd}"
							partialTriggers="ncPollutantCd" />
						<af:selectOneChoice label="Pollutant" autoSubmit="true"
							value="#{serviceCatalog.addedNcPollutant.pollutantCd}"
							id="ncPollutantCd" readOnly="#{!serviceCatalog.editingReport}"
							required="true">
							<f:selectItems
								value="#{facilityReference.nonToxicPollutantDefs.items[(empty serviceCatalog.addedNcPollutant.pollutantCd ? '' : serviceCatalog.addedNcPollutant.pollutantCd)]}" />
						</af:selectOneChoice>
					</af:panelForm>

					<af:objectSpacer width="100%" height="15" />
					<af:outputFormatted
						value="Adding an optional Source Classification Code (SCC) will indicate that emissions for 
						that pollutant are excluded from fee calculations solely <br>for Emission Processes with a matching 
						SCC. If an SCC is added, all four parts of the code are required." />
					<af:objectSpacer width="100%" height="10" />

					<af:panelForm rows="4" maxColumns="1">

						<af:selectOneChoice label="SCC Level 1 Description:"
							value="#{serviceCatalog.addedNcPollutant.sccDesc1}"
							id="ncSccIdL1" autoSubmit="true"
							readOnly="#{!serviceCatalog.editingReport}">
							<f:selectItems value="#{infraDefs.sccLevel1Codes}" />
						</af:selectOneChoice>
						<af:selectOneChoice label="SCC Level 2 Description:"
							value="#{serviceCatalog.addedNcPollutant.sccDesc2}"
							id="ncSccIdL2" autoSubmit="true" partialTriggers="ncSccIdL1"
							readOnly="#{!serviceCatalog.editingReport}">
							<f:selectItems
								value="#{serviceCatalog.addedNcPollutant.sccLevel2Codes}" />
						</af:selectOneChoice>
						<af:selectOneChoice label="SCC Level 3 Description:"
							value="#{serviceCatalog.addedNcPollutant.sccDesc3}"
							id="ncSccIdL3" autoSubmit="true" partialTriggers="ncSccIdL2"
							readOnly="#{!serviceCatalog.editingReport}">
							<f:selectItems
								value="#{serviceCatalog.addedNcPollutant.sccLevel3Codes}" />
						</af:selectOneChoice>
						<af:selectOneChoice label="SCC Level 4 Description:"
							value="#{serviceCatalog.addedNcPollutant.sccDesc4}"
							id="ncSccIdL4" autoSubmit="true" partialTriggers="ncSccIdL3"
							readOnly="#{!serviceCatalog.editingReport}">
							<f:selectItems
								value="#{serviceCatalog.addedNcPollutant.sccLevel4Codes}" />
						</af:selectOneChoice>
					</af:panelForm>

					<af:objectSpacer width="100%" height="10" />
					<af:outputFormatted
						value="Checking the Fugitive Only box will restrict the fee exclusion for the pollutant to fugitive emissions only. 
							The Fugitive Only exclusion may be <br>added regardless of whether an SCC exclusion
							is present. If an SCC exclusion is present, the Fugitive Only fee exclusion will apply solely <br>to Emission 
							Processes with a matching SCC." />
					<af:objectSpacer width="100%" height="10" />

					<af:panelForm rows="1" maxColumns="1">

						<af:selectBooleanCheckbox label="Fugitive Only" autoSubmit="true"
							readOnly="#{!serviceCatalog.editingReport}"
							value="#{serviceCatalog.addedNcPollutant.fugitiveOnly}" />

					</af:panelForm>

					<af:objectSpacer height="15" />
					<f:facet name="footer">
						<af:panelButtonBar>
							<af:commandButton text="Add"
								rendered="#{serviceCatalog.editingReport}"
								action="#{serviceCatalog.applyAddNonChargeablePollutant}" />
							<af:commandButton text="Cancel"
								rendered="#{serviceCatalog.editingReport}" immediate="true"
								action="#{serviceCatalog.cancelAddNonChargeablePollutant}" />
						</af:panelButtonBar>
					</f:facet>

				</af:panelForm>
			</af:page>
		</af:form>
	</af:document>
</f:view>

