<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:verbatim>
	<style>
		div.permitConditionSearchTable>table.x2d>tbody>tr>td {
			vertical-align: top;
		}
	</style>
</f:verbatim> 

<afh:rowLayout halign="center">
	<h:panelGrid border="1" rendered="#{permitConditionSearch.hasSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Permit Condition List" disclosed="true">

				<af:table value="#{permitConditionSearch.resultsWrapper}"
					width="100%"
					binding="#{permitConditionSearch.resultsWrapper.table}"
					bandingInterval="1" banding="row" var="condition"
					rows="#{permitConditionSearch.pageLimit}" styleClass="permitConditionSearchTable">
					
					<af:column headerText="Permit Condition ID"
						sortable="true" sortProperty="c01"  
						formatType="text" noWrap="true" >
						<af:commandLink 
							useWindow="true" 
							windowWidth="#{permitConditionDetail.permitConditionWindowWidth}" 
							windowHeight="#{permitConditionDetail.permitConditionWindowHeight}"
							inlineStyle="padding-left:5px;"
							text="#{condition.pcondId}"
							action="#{permitConditionDetail.showPermitConditionDetailFromSearch}" >
							<t:updateActionListener property="#{permitConditionDetail.permitConditionId}"
								value="#{condition.permitConditionId}" />
							<t:updateActionListener property="#{permitConditionDetail.fromFacilityPermitConditionSearch}"
								value="true" />
						</af:commandLink>
					</af:column>
					
					<af:column headerText="Associated Permit ID"
						sortable="true" sortProperty="c02" 
						formatType="text" noWrap="true" >
						<af:commandLink text="#{condition.permitNbr}"
							action="#{permitDetail.loadPermitFromPermitConditionSearch}">
							<t:updateActionListener property="#{permitDetail.tempPermitId}"
								value="#{condition.permitId}" />
							<t:updateActionListener property="#{permitDetail.fromTODOList}"
								value="false" />
						</af:commandLink>
					</af:column>	 
					
					<af:column headerText="Permit Type" 
						sortable="true" sortProperty="c03" 
						formatType="text" >
						<af:selectOneChoice
							readOnly="true"
							value="#{condition.permitTypeCd}" >
							<f:selectItems value="#{permitSearch.permitTypes}" /> 
						</af:selectOneChoice>
					</af:column>	 
					
					<af:column headerText="Permit Status" 
						sortable="true" sortProperty="c04" 
						formatType="text" >
						<af:outputText
							value="#{permitReference.permitLevelStatusDefs.itemDesc[
								(empty condition.permitLevelStatusCd ? '' : condition.permitLevelStatusCd)]}" />
					</af:column>
						 
					<af:column headerText="Final Issuance Date" 
						sortable="true" sortProperty="finalIssuanceDate" 
						formatType="text" noWrap="true" >
						<af:selectInputDate 
							readOnly="true" 
							value="#{condition.finalIssuanceDate}" />
					</af:column>	 
					
					<af:column headerText="Permit Basis Date" 
						sortable="true" sortProperty="permitBasisDate" 
						formatType="text" noWrap="true" >
						<af:selectInputDate 
							readOnly="true" 
							value="#{condition.permitBasisDate}" />
					</af:column>	 
					
					<af:column headerText="Permit Condition Number" 
						sortable="true" sortProperty="c07" 
						formatType="text" noWrap="true" >
						<af:outputText
							value="#{condition.permitConditionNumber}" />
					</af:column>	 
					
					<af:column headerText="Condition Text" 
						sortable="true" sortProperty="c08" width="15%"
						formatType="text" noWrap="false" >
						<af:outputText
							value="#{condition.conditionTextPlain}" 
							truncateAt="#{permitConditionDetail.permitConditionTextTruncateAt}" />
					</af:column>	 
					
					<af:column headerText="Associated EUs"
						sortable="true" sortProperty="c09"  width="10%"
						formatType="text" noWrap="false" >
						<af:outputText
							value="#{condition.associatedEUsValue}" />
					</af:column>	 
					
					<af:column headerText="Category" 
						sortable="true" sortProperty="c10"  width="50px"
						formatType="text" noWrap="false" headerNoWrap="true">
						<af:outputText value="#{condition.categoryCdsDescription}" />
					</af:column>	 
					
					<af:column headerText="Permit Condition Status" 
						sortable="true" sortProperty="c11"  
						formatType="text" >
						<af:commandLink useWindow="true" 
							windowWidth="600" windowHeight="250"
							text="#{permitReference.permitConditionStatusDefs.itemDesc[
										(empty condition.permitConditionStatusCd ? '' : condition.permitConditionStatusCd)]}"
							action="#{permitConditionDetail.displayPermitConditionStatusFromSearch}" >
							<t:updateActionListener property="#{permitConditionDetail.permitConditionSearchLineItem}"
								value="#{condition}" />	
							<t:updateActionListener property="#{permitConditionDetail.fromFacilityPermitConditionSearch}"
								value="true" />	
							<t:updateActionListener 
								property="#{permitConditionDetail.permitConditionId}"
								value="#{condition.permitConditionId}" />

	
	
						</af:commandLink>
					</af:column>
					
					<af:column headerText="Compliance Status History" 
						sortable="true" sortProperty="c12" 
						formatType="text" >
						<af:commandLink useWindow="true"
							windowWidth="#{permitConditionDetail.permitConditionWindowWidth}"
							windowHeight="#{permitConditionDetail.permitConditionWindowHeight}"
							inlineStyle="padding-left:5px;"
							text="Events(#{condition.associatedComplianceStatusEventsCount})"
							action="#{permitConditionDetail.displayComplianceEventHistoryFromSearch}">
							<t:updateActionListener
								property="#{permitConditionDetail.permitConditionSearchLineItem}"
								value="#{condition}" />
							<t:updateActionListener property="#{permitConditionDetail.facilityId}"
								value="#{facilityProfile.facilityId}" />	
							<t:updateActionListener
								property="#{permitConditionDetail.fromFacilityPermitConditionSearch}"
								value="true" />
						</af:commandLink>
					</af:column>

					<af:column headerText="Superseding/Partially Superseding Section"
						sortable="true" sortProperty="c13" formatType="text"  width="40%">
						<af:table value="#{condition.supersededByThis}"
							bandingInterval="1" banding="row" id="supersededByList"
							rendered="#{!empty condition.supersededByThis}"
							var="supersedes">
							<af:column headerText="Permit Number" formatType="text"
								id="permitnbr" noWrap="true" headerNoWrap="false">
								<af:commandLink text="#{supersedes.supersededPermitNumber}"
									action = "#{permitDetail.loadPermitFromPermitConditionSearch}">
									<t:updateActionListener property="#{permitDetail.tempPermitId}"
										value="#{supersedes.supersededPermitId}"	/>
									<t:updateActionListener property="#{permitDetail.fromTODOList}"
										value="false"/>
								</af:commandLink>
							</af:column>
							<af:column headerText="Condition ID" formatType="text"
								id="scondid" noWrap="true" headerNoWrap="false">
								<af:commandLink text="#{supersedes.supersededpcondId}"
									useWindow="true"
									windowWidth="#{permitConditionDetail.permitConditionWindowWidth}"
									windowHeight="#{permitConditionDetail.permitConditionWindowHeight}"
									action="#{permitConditionDetail.showPermitConditionDetailFromSearch}">
									<t:updateActionListener
										property="#{permitConditionDetail.permitConditionId}"
										value="#{supersedes.supersededPermitConditionId}" />
									<t:updateActionListener
										property="#{permitConditionDetail.fromFacilityPermitConditionSearch}"
										value="true" />
								</af:commandLink>
							</af:column>
							<af:column headerText="Condition Number" formatType="text"  
								id="scondnbr" noWrap="true" headerNoWrap="false">
								<af:outputText
									value="#{supersedes.supersededPermitCondiditionNumber}" />
							</af:column>
							<af:column headerText="Superseding Option" formatType="text" 
								id="supersedingoption" noWrap="true" headerNoWrap="false">
								<af:outputText
									value="#{permitReference.conditionSupersedenceStatusDefs.itemDesc[
												(empty supersedes.supersedingOption
												? '' : supersedes.supersedingOption)]}" />
							</af:column>
							<af:column headerText="Comments" formatType="text" id="comments" width="40%"
								noWrap="false" headerNoWrap="false">
								<af:outputText truncateAt="100" value="#{supersedes.comments}"
									shortDesc="#{supersedes.comments}" />
							</af:column>
						</af:table>
					</af:column>

					<af:column headerText="Last Updated By" 
						sortable="true" sortProperty="c14" 
						formatType="text" >
						<af:outputText
							value="#{infraDefs.basicUsersDef.itemDesc[
								(empty condition.lastUpdatedById ? '' : condition.lastUpdatedById)]}" />
					</af:column>
					
					<af:column headerText="Last Updated Date" 
						sortable="true" sortProperty="lastUpdatedDate" 
						formatType="text" noWrap="true" >
						<af:selectInputDate
							readOnly="true"
							value="#{condition.lastUpdatedDate}" />
					</af:column>	 	 

					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>

			</af:showDetailHeader>
		</af:panelBorder>
	</h:panelGrid>
</afh:rowLayout>
