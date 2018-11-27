<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<af:outputText id="euTypeLabel" value="Emission Unit Type : Fugitive"
	inlineStyle="font-size: 13px; font-weight: bold;" />
<af:objectSpacer height="15" />
<af:panelForm width="600" labelWidth="150" maxColumns="2"
	partialTriggers="fugitiveEmissionTypeCd">

	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
		<af:selectOneChoice id="fugitiveEmissionTypeCd"
			label="Type of Fugitive Emission :"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.selectedEU.euType.fugitiveEmissionTypeCd}"
			autoSubmit="true" showRequired="true">
			<f:selectItems
				value="#{applicationReference.appEUFUGEmissionTypeDefs.items[(empty applicationDetail.selectedEU.euType.fugitiveEmissionTypeCd ? '' : applicationDetail.selectedEU.euType.fugitiveEmissionTypeCd)]}" />
		</af:selectOneChoice>
	</af:panelForm>
	<af:switcher
		facetName="#{applicationDetail.selectedEU.euType.fugitiveEmissionTypeCd}"
		id="fugitiveEmissionTypeCd">
		<f:facet name="1">
			<af:panelGroup>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="maxDistanceMateriaHauled"
						label="Maximum Distance Material will be Hauled (or until Reaching Pavement) (miles) :"
						columns="12" maximumLength="12"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.maxDistanceMateriaHauled}"
						showRequired="true">
						<af:convertNumber pattern=".00" />
					</af:inputText>
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="truckType1" label="Truck Type 1 :" columns="50"
						maximumLength="50" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.truckType1}"
						showRequired="true" />
					<af:inputText id="type1TrucksCount"
						label="Number of Type 1 Trucks :" columns="12" maximumLength="12"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type1TrucksCount}"
						showRequired="true" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="type1TrucksCapacity"
						label="Capacity of Type 1 Trucks (tons) :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type1TrucksCapacity}"
						showRequired="true" />
					<af:inputText id="type1TrucksEmptyWeight"
						label="Empty Weight of Type 1 Trucks (lbs) :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type1TrucksEmptyWeight}"
						showRequired="true" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="truckType2" label="Truck Type 2 :" columns="50"
						maximumLength="50" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.truckType2}" />
					<af:inputText id="type2TrucksCount"
						label="Number of Type 2 Trucks :" columns="12" maximumLength="12"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type2TrucksCount}" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="type2TrucksCapacity"
						label="Capacity of Type 2 Trucks (tons) :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type2TrucksCapacity}" />
					<af:inputText id="type2TrucksEmptyWeight"
						label="Empty Weight of Type 2 Trucks (lbs) :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type2TrucksEmptyWeight}" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="truckType3" label="Truck Type 3 :" columns="50"
						maximumLength="50" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.truckType3}" />
					<af:inputText id="type3TrucksCount"
						label="Number of Type 3 Trucks :" columns="12" maximumLength="12"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type3TrucksCount}" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="type3TrucksCapacity"
						label="Capacity of Type 3 Trucks (tons) : " columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type3TrucksCapacity}" />
					<af:inputText id="type3TrucksEmptyWeight"
						label="Empty Weight of Type 3 Trucks (lbs) :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type3TrucksEmptyWeight}" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="truckType4" label="Truck Type 4 :" columns="50"
						maximumLength="50" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.truckType4}" />
					<af:inputText id="type4TrucksCount"
						label="Number of Type 4 Trucks :" columns="12" maximumLength="12"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type4TrucksCount}" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="type4TrucksCapacity"
						label="Capacity of Type 4 Trucks (tons) : " columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type4TrucksCapacity}" />
					<af:inputText id="type4TrucksEmptyWeight"
						label="Empty Weight of Type 4 Trucks (lbs) :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.type4TrucksEmptyWeight}" />
				</af:panelForm>
			</af:panelGroup>
		</f:facet>
		<f:facet name="2">
			<af:panelGroup>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="acreageSubjectToWindErosion"
						label="Acreage Subject to Wind Erosion (acres) :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.acreageSubjectToWindErosion}"
						showRequired="true">
						<af:convertNumber pattern=".00" />
					</af:inputText>
				</af:panelForm>
			</af:panelGroup>
		</f:facet>
		<f:facet name="3">
			<af:panelGroup>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:selectOneChoice id="stockPileTypeCd" label="Type of Stockpile :"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.stockPileTypeCd}"
						showRequired="true">
						<f:selectItems
							value="#{applicationReference.appEUStockpileTypeDefs.items[(empty applicationDetail.selectedEU.euType.stockPileTypeCd ? '' : applicationDetail.selectedEU.euType.stockPileTypeCd)]}" />
					</af:selectOneChoice>
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="materialAddedRemovedFromPileDay"
						label="Material Added/Removed from Pile (tons/day) :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.materialAddedRemovedFromPileDay}">
						<af:convertNumber pattern=".00" />
					</af:inputText>
					<af:inputText id="materialAddedRemovedFromPileYr"
						label="Material Added/Removed from Pile (tons/yr) :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.materialAddedRemovedFromPileYr}">
						<af:convertNumber pattern=".00" />
					</af:inputText>
				</af:panelForm>

				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="stockPilesCount" label="Number of Stockpiles :"
						columns="12" maximumLength="12"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.stockPilesCount}"
						showRequired="true" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="stockPileSize" label="Size of Stockpile :"
						columns="12" maximumLength="12"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.stockPileSize}"
						showRequired="true" />
					<af:selectOneChoice id="stockPileUnitCd" label="Units :"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.stockPileUnitCd}"
						showRequired="true">
						<f:selectItems
							value="#{applicationReference.appEUStockpileSizeDefs.items[(empty applicationDetail.selectedEU.euType.stockPileUnitCd ? '' : applicationDetail.selectedEU.euType.stockPileUnitCd)]}" />
					</af:selectOneChoice>
				</af:panelForm>
			</af:panelGroup>
		</f:facet>
		<f:facet name="4">
			<af:panelGroup>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="blastsPerYearNumber"
						label="Number of Blasts per year :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.blastsPerYearNumber}"
						showRequired="true" />
					<af:inputText id="blastingAgentUsedType"
						label="Type of Blasting Agent Used :" columns="50"
						maximumLength="50" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.blastingAgentUsedType}"
						showRequired="true" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="blastingAgentUsedAmount"
						label="Amount of Blasting Agent Used (tons/yr) :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.blastingAgentUsedAmount}"
						showRequired="true">
						<af:convertNumber pattern=".00" />
					</af:inputText>
					<af:inputText id="blastHorizontalArea"
						label="Horizontal Area of Blast (cu. ft) :" columns="12"
						maximumLength="12" readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.blastHorizontalArea}"
						showRequired="true">
						<af:convertNumber pattern=".00" />
					</af:inputText>
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:selectOneChoice id="materialBlastedTypeCd"
						label="Type of material blasted :"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.materialBlastedTypeCd}"
						showRequired="true">
						<f:selectItems
							value="#{applicationReference.appEUMaterialTypeDefs.items[(empty applicationDetail.selectedEU.euType.materialBlastedTypeCd ? '' : applicationDetail.selectedEU.euType.materialBlastedTypeCd)]}" />
					</af:selectOneChoice>
				</af:panelForm>
			</af:panelGroup>
		</f:facet>
		<f:facet name="5">
			<af:panelGroup>
				<%@ include file="emissionsFugLeaksOilGasInfoHistory.jsp"%>
			</af:panelGroup>
		</f:facet>
		<f:facet name="6">
			<af:panelGroup>
				<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">
					<af:inputText id="fugitiveSourceDescription"
						label="Detailed Description of Fugitive Source :" columns="120"
						rows="4" maximumLength="500"
						readOnly="#{! applicationDetail.editMode}"
						value="#{applicationDetail.selectedEU.euType.fugitiveSourceDescription}"
						showRequired="true"
						tip="Provide detailed calculations documenting the potential emissions and emission factors used to calculate emissions from this source." />
				</af:panelForm>
			</af:panelGroup>
		</f:facet>
	</af:switcher>
</af:panelForm>
