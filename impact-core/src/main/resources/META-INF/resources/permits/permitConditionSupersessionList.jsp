<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" >
	<af:showDetailHeader text="Superseding/Partially Superseding"   disclosed="true">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>

			<%@ include file="../scripts/navigate.js"%>
		</f:verbatim>
		<%
			/* Content begin */
		%>
		<h:panelGrid columns="1" border="1"
			width="#{permitDetail.permitWidth}">
			<af:panelGroup>
				<afh:rowLayout halign="center" valign="top">
					<af:table
						value="#{permitConditionDetail.superSessionWrapper}"
						binding="#{permitConditionDetail.superSessionWrapper.table}"
						bandingInterval="1" banding="row" id="supersededByList"
						var="supersedes" varStatus="supersedesRow"
						width="#{permitDetail.permitTableWidth}">

						<af:column id="rowId" formatType="text" headerText="Row Id"
							width="5%">
							<af:commandLink useWindow="true"
								action="#{permitConditionDetail.displayPermitConditionSupersession}">
								<af:outputText value="#{supersedesRow.index+1}">
									<af:convertNumber pattern="000" />
								</af:outputText>
								<t:updateActionListener property="#{permitConditionDetail.conditionSupersession}"
									value="#{supersedes}" />
							</af:commandLink>
						</af:column>
						<af:column headerText="Permit Number" formatType="text"
							id="permitnbr" noWrap="true" headerNoWrap="false" width="175px"
							sortable="true" sortProperty="supersededPermitNumber">
							<af:commandLink text="#{supersedes.supersededPermitNumber}"
							    onclick = "setFocus('loadSupersededPermit')"    > 
							    <t:updateActionListener property="#{permitDetail.tempPermitId}"
									value="#{supersedes.supersededPermitId}"	/>
								<t:updateActionListener property="#{permitDetail.fromTODOList}"
									value="false"/>
							</af:commandLink>
							
	
						</af:column>
							
						<af:column headerText="Condition ID" formatType="text"
							id="scondid" noWrap="true" headerNoWrap="false" sortable="true"
							sortProperty="supersededpcondId">
							<af:commandLink text="#{supersedes.supersededpcondId}" 
								action="#{permitConditionDetail.refreshPermitConditionDetailWithThis}" >
								<t:updateActionListener property="#{permitConditionDetail.permitConditionId}"
									value="#{supersedes.supersededPermitConditionId}" />
							</af:commandLink>
						</af:column>
						<af:column headerText="Condition Number" formatType="text"
							id="scondnbr" noWrap="true" headerNoWrap="false" sortable="true"
							sortProperty="supersededPermitCondiditionNumber">
							<af:outputText
								value="#{supersedes.supersededPermitCondiditionNumber}" />
						</af:column>
						<af:column headerText="Superseding Option" formatType="text"
							id="supersedingoption" noWrap="true" headerNoWrap="false"
							sortable="true" sortProperty="supersedingOption">
							<af:outputText
								value="#{permitReference.conditionSupersedenceStatusDefs.itemDesc[
												(empty supersedes.supersedingOption
												? '' : supersedes.supersedingOption)]}" />
						</af:column>
						<af:column headerText="Comments" formatType="text" id="comments"
							noWrap="false" headerNoWrap="false">
							<af:outputText value="#{supersedes.comments}" />
						</af:column>

						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
									<af:commandButton text="Add" id="addSupersessionEvent"
										action="#{permitConditionDetail.startToAddPermitConditionSupersession}"
										rendered="#{permitConditionDetail.permitConditionEditAllowed}"
										useWindow="true" windowWidth="700" windowHeight="350" />
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
			</af:panelGroup>
		</h:panelGrid>

	</af:showDetailHeader>
</af:panelGroup>