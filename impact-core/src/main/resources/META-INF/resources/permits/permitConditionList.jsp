<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:verbatim>
	<style>
		div.permitConditionTable>table.x2d>tbody>tr>td {
			vertical-align: top;
        }
	</style>
</f:verbatim> 

<af:panelGroup layout="vertical" rendered="true">
	<af:showDetailHeader text="Permit Conditions" disclosed="true">

		<%
			/* Content begin */
		%>
		<h:panelGrid columns="1" border="1" width="100%">
			<af:panelGroup>
				<afh:rowLayout halign="center" valign="top">
					<af:table value="#{permitDetail.permitConditionsWrapper}"
						binding="#{permitDetail.permitConditionsWrapper.table}"
						bandingInterval="1" banding="row" id="permitConditionTab"
						width="100%" var="permitCondition"
						rows="#{permitDetail.pageLimit}" emptyText=" "
						styleClass="permitConditionTable" 
						varStatus="permitConditionTableVs">

						<af:column headerText="Condition ID" formatType="text"
							sortProperty="pcondId" sortable="true" id="edit" noWrap="true"
							headerNoWrap="false" width="4%">
							<af:commandLink text="#{permitCondition.pcondId}" 
								action="#{permitConditionDetail.startToEditPermitCondition}" >
								<t:updateActionListener property="#{permitConditionDetail.modifyPermitCondition}"
									value="#{permitCondition}" />
								<t:updateActionListener property="#{permitConditionDetail.permit}"
									value="#{permitDetail.permit}" />
							</af:commandLink>
						</af:column>

						<af:column headerText="Condition Number" formatType="text"
							sortProperty="permitConditionNumber" sortable="true" width="4%"
							id="permitConditionNumber" noWrap="true" headerNoWrap="false">
							<af:inputText maximumLength="20" label="Permit Condition Number"
								readOnly="true" value="#{permitCondition.permitConditionNumber}"
								columns="20">
							</af:inputText>
						</af:column>

						<af:column headerText="Condition Status" formatType="text"
							sortProperty="permitConditionStatusCd" sortable="true"  width="4%"
							id="permitConditionStatusCd" noWrap="true" headerNoWrap="false">
							<af:commandLink useWindow="true" windowWidth="600"
								windowHeight="250"
								text="#{permitReference.permitConditionStatusDefs.itemDesc[
										(empty permitCondition.permitConditionStatusCd ? '' : permitCondition.permitConditionStatusCd)]}" 
								action="#{permitConditionDetail.displayPermitConditionStatus}" >		
								<t:updateActionListener property="#{permitConditionDetail.modifyPermitCondition}"
									value="#{permitCondition}" />
								<t:updateActionListener property="#{permitConditionDetail.permit}"
									value="#{permitDetail.permit}" />
								<t:updateActionListener 
									property="#{permitConditionDetail.permitConditionId}"
									value="#{permitCondition.permitConditionId}" />
							</af:commandLink>
						</af:column>

						<af:column headerText="Condition Text" formatType="text"
							sortProperty="conditionTextPlain" sortable="true" width="15%"
							id="conditionTextPlain" noWrap="false" headerNoWrap="false">
							<af:outputText truncateAt="200"
								value="#{permitCondition.conditionTextPlain}" />
						</af:column>

						<af:column headerText="Category" formatType="text"
							sortProperty="categoryCdsDescription" sortable="true" width="4%"
							id="permitConditionCategoryCds" noWrap="false"
							headerNoWrap="true">
							<af:outputText value="#{permitCondition.categoryCdsDescription}" />
						</af:column>

						<af:column headerText="Associated EU(s)" formatType="text"
							sortProperty="associatedObjectsDisplay" sortable="true"
							width="10%"
							id="associatedObjectsDisplay" noWrap="false" headerNoWrap="false">
							<af:outputText truncateAt="100"
								value="#{permitCondition.associatedObjectsDisplay}" />
						</af:column>

						<af:column id="complianceStatusHistoryDisplay" formatType="text"
							headerText="Compliance Status History"
							sortProperty="complianceStatusEventsCount" sortable="true"
							width="4%"
							noWrap="false" headerNoWrap="false">
							<af:commandLink useWindow="true" windowWidth="900"
								windowHeight="500" inlineStyle="padding-left:5px;"
								returnListener="#{permitDetail.dialogDone}"
								action="#{permitConditionDetail.displayPermitConditionComplianceHistory}"
								text="Events(#{permitCondition.complianceStatusEventsCount})" >
								<t:updateActionListener property="#{permitConditionDetail.modifyPermitCondition}"
									value="#{permitCondition}" />
								<t:updateActionListener property="#{permitConditionDetail.permit}"
									value="#{permitDetail.permit}" />
								<t:updateActionListener property="#{permitConditionDetail.fromFacilityPermitConditionSearch}"
									value="false" />	
							</af:commandLink>	
						</af:column>

						<af:column headerText="Last Updated By" formatType="text"
							sortProperty="lastUpdatedByName" sortable="true"  width="4%"
							id="lastUpdatedById" noWrap="false" headerNoWrap="false">
							<af:selectOneChoice readOnly="true"
								value="#{permitCondition.lastUpdatedById}">
								<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
							</af:selectOneChoice>
						</af:column>

						<af:column headerText="Last Updated Date" formatType="text"
							sortProperty="lastUpdatedDate" sortable="true"  width="4%"
							id="lastUpdatedDate" noWrap="true" headerNoWrap="false">
							<af:selectInputDate label="Last Updated Date" readOnly="true"
								valign="top" required="true"
								value="#{permitCondition.lastUpdatedDate}">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
						</af:column>

						<af:column headerText="Superseding/Partially Superseding"
							formatType="text" id="superseding" noWrap="false"
							headerNoWrap="false" width="47%">
							<af:table value="#{permitCondition.supersededByThis}"
								bandingInterval="1" banding="row" id="supersededByList"
								rendered="#{!empty permitCondition.supersededByThis}" 
								var="supersedes">
								<af:column headerText="Permit Number" formatType="text"
									id="permitnbr" noWrap="true" headerNoWrap="false">
<%-- 								<af:outputText value="#{supersedes.supersededPermitNumber}" /> --%>
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
								<af:column headerText="Comments" formatType="text" id="comments"
									width="45%"
									noWrap="false" headerNoWrap="false">
									<af:outputText truncateAt="170" value="#{supersedes.comments}"
										shortDesc="#{supersedes.comments}" />
								</af:column>
							</af:table>
						</af:column>

						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
									<af:commandButton text="Add" id="addPermitCondition"
										rendered="#{permitConditionDetail.permitConditionEditAllowed}"
										action="#{permitConditionDetail.startToAddPermitCondition}" >
										<t:updateActionListener	property="#{permitConditionDetail.permit}"
											value="#{permitDetail.permit}" />
									</af:commandButton>	
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelGroup>
							</afh:rowLayout>
						</f:facet>
					</af:table>
					<%
						/* Fixed Charge list end */
					%>
				</afh:rowLayout>


				<af:objectSpacer height="10" />

			</af:panelGroup>
		</h:panelGrid>
		<%
			/* Content end */
		%>

	</af:showDetailHeader>

</af:panelGroup>


