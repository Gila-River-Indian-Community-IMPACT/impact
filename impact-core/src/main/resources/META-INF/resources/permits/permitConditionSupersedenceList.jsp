<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" >
	<af:showDetailHeader text="Superseded By Permit Conditions" disclosed="true">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>

			<%@ include file="../scripts/navigate.js"%>
		</f:verbatim>
		<%
			/* Content begin */
		%>

		<h:panelGrid columns="1" border="1" width="#{permitDetail.permitWidth}">
			<af:panelGroup>
				<afh:rowLayout halign="left" valign="top">
					<af:table value="#{permitConditionDetail.permitConditionStatusWrapper2}"
						binding="#{permitConditionDetail.permitConditionStatusWrapper2.table}"
						banding="row" bandingInterval="1" id="permitConditionStatus"
						width="#{permitDetail.permitTableWidth}"
						var="permitConditionStatus" rows="#{permitDetail.pageLimit}"
						emptyText=" " varStatus="permitConditionStatusTable">
						
						<af:column headerText="Permit Number" formatType="text"
							sortProperty="permitNumber" sortable="true"
							id="permitNumber" noWrap="true" headerNoWrap="true">
							<af:commandLink text="#{permitConditionStatus.supersedingPermitNumber}"
								onclick = "setFocus('loadPermitFromPermitConditionSearch')" >
								<t:updateActionListener property="#{permitDetail.tempPermitId}"
									value="#{permitConditionStatus.supersedingPermitId}"	/>
								<t:updateActionListener property="#{permitDetail.fromTODOList}"
									value="false"/>
							</af:commandLink>
						</af:column>
	
						<af:column headerText="Permit Type" formatType="text"
							sortProperty="permitType" sortable="true"
							id="permitType" noWrap="true" headerNoWrap="true">
							<af:selectOneChoice unselectedLabel=" " readOnly="true" valign="top" 
												value="#{permitConditionStatus.supersedingPermitType}">
								<mu:selectItems value="#{permitReference.permitTypes}" />
							</af:selectOneChoice>
						</af:column>
						
						<af:column headerText="Final Issuance Date" formatType="text"
							sortProperty="finalIssueDate" sortable="true"
							id="finalIssueDate" noWrap="true" headerNoWrap="true">
							<af:selectInputDate readOnly="true" 
												value="#{permitConditionStatus.supersedingPermitIssuanceDate}" />
						</af:column>
						
						<af:column headerText="Condition ID" formatType="text"
							sortProperty="conditionId" sortable="true"
							id="conditionId" noWrap="true" headerNoWrap="true">
							<af:commandLink
								text="#{permitConditionStatus.supersedingpcondId}"
								useWindow="true"
								windowWidth="#{permitConditionDetail.permitConditionWindowWidth}"
								windowHeight="#{permitConditionDetail.permitConditionWindowHeight}"
								action="#{permitConditionDetail.showPermitConditionDetailFromSearch}">
								<t:updateActionListener
									property="#{permitConditionDetail.permitConditionId}"
									value="#{permitConditionStatus.supersedingPermitConditionId}" />
								<t:updateActionListener
									property="#{permitConditionDetail.fromFacilityPermitConditionSearch}"
									value="true" />
							</af:commandLink>
						</af:column>
						
						<af:column headerText="Condition Number" formatType="text"
							sortProperty="conditionNumber" sortable="true"
							id="conditionNumber" noWrap="true" headerNoWrap="true">
							<af:inputText readOnly="true" 
										  value="#{permitConditionStatus.supersedingPermitCondiditionNumber}" />
						</af:column>
						
						<af:column headerText="Superseded Option" formatType="text"
							sortProperty="permitConditionSupersedingOption" sortable="true"
							id="permitConditionSupersedingOption" noWrap="true" headerNoWrap="false">
							<af:inputText readOnly="true" 
										  value="#{permitReference.conditionSupersedenceStatusDefs.itemDesc[
												(empty permitConditionStatus.supersedingOption
												? '' : permitConditionStatus.supersedingOption)]}" />
						</af:column>
						
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelGroup>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</afh:rowLayout>
				
				<af:objectSpacer height="15" />
			</af:panelGroup>
		</h:panelGrid>
		
		<%
			/* Content end */
		%>
	</af:showDetailHeader>		
</af:panelGroup>