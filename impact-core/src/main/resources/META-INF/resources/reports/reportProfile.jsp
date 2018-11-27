<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<af:panelBorder id="reportProfilePage"
	rendered="#{!reportSearch.ntvReport && !reportProfile.err}">
	<% /* 	partialTriggers="reportProfilePage:report:deletePeriod"  */ %>
	<f:facet name="top">
		<f:subview id="reportHeader">
			<jsp:include flush="true" page="reportHeader.jsp" />
		</f:subview>
	</f:facet>

	<f:facet name="left">
		<t:div style="overflow:auto;width:200px;height:600px"
			rendered="#{!reportProfile.editable && !reportProfile.reportLevelOnly}">
			<t:tree2 id="reportProfileTree" value="#{reportProfile.treeData}"
				var="node" clientSideToggle="false">
				<f:facet name="report">
					<h:panelGroup>
						<af:commandMenuItem action="#{reportProfile.nodeClicked}"
							id="emissionsInventorySummaryNode"
							onclick="if (#{reportProfile.editable || reportProfile.editableM}) { alert('Please save or discard your changes'); return false; }"
							shortDesc="view/edit facility-wide data for emissions inventory">
							<af:switcher rendered="#{!reportProfile.doRptCompare}"
								facetName="#{node.userObject.validated? 'validated': 'nonValidated'}"
								defaultFacet="nonValidated">
								<f:facet name="validated">
									<t:graphicImage border="0" value="/images/Check.gif"
										title="Emissions inventory validated" />
								</f:facet>
								<f:facet name="nonValidated">
									<t:graphicImage border="0" value="/images/Caution.gif"
										title="Emissions inventory not Validated " />
								</f:facet>
							</af:switcher>
							<t:outputText value="#{node.description}"
								style="color:#FFFFFF; background-color:#000000"
								title="Emissions Inventory Summary"
								rendered="#{reportProfile.current == node.identifier}" />
							<t:outputText value="#{node.description}"
								title="Emissions Inventory Summary"
								rendered="#{reportProfile.current != node.identifier}" />
							<t:updateActionListener
								property="#{reportProfile.selectedTreeNode}" value="#{node}" />
							<t:updateActionListener property="#{reportProfile.current}"
								value="#{node.identifier}" />
						</af:commandMenuItem>
					</h:panelGroup>
				</f:facet>
				<f:facet name="moreNodes">
					<h:panelGroup>
						<af:commandMenuItem action="#{reportProfile.nodeClickedCollaspe}"
							onclick="if (#{reportProfile.editable || reportProfile.editableM}) { alert('Please save or discard your changes'); return false; }">
							<t:graphicImage value="/images/vellipsis.gif" border="0"
								title="More Emissions Unit/Group Summary" />
							<t:outputText value="#{node.description}"
								style="color:#FFFFFF; background-color:#000000"
								title="More Emissions Unit/Group Summary"
								rendered="#{reportProfile.current == node.identifier}" />
							<t:outputText value="#{node.description}"
								title="More Emissions Unit/Group Summary"
								rendered="#{reportProfile.current != node.identifier}" />
							<t:updateActionListener
								property="#{reportProfile.selectedTreeNode}" value="#{node}" />
							<t:updateActionListener property="#{reportProfile.current}"
								value="#{node.identifier}" />
						</af:commandMenuItem>
					</h:panelGroup>
				</f:facet>
				<f:facet name="emissionUnitGroups">
					<h:panelGroup>
						<af:commandMenuItem action="#{reportProfile.nodeClickedCollaspe}"
							onclick="if (#{reportProfile.editable || reportProfile.editableM}) { alert('Please save or discard your changes'); return false; }">
							<t:graphicImage value="/images/reportEUGroup.gif" border="0"
								title="Emissions Unit Group Summary" />
							<t:outputText value="#{node.description}"
								style="color:#FFFFFF; background-color:#000000"
								title="Emissions Unit Group Summary"
								rendered="#{reportProfile.current == node.identifier}" />
							<t:outputText value="#{node.description}"
								title="Emissions Unit Group Summary"
								rendered="#{reportProfile.current != node.identifier}" />
							<t:updateActionListener
								property="#{reportProfile.selectedTreeNode}" value="#{node}" />
							<t:updateActionListener property="#{reportProfile.current}"
								value="#{node.identifier}" />
						</af:commandMenuItem>
					</h:panelGroup>
				</f:facet>
				<f:facet name="emissionUnitGroupsCaution">
					<h:panelGroup>
						<af:commandMenuItem action="#{reportProfile.nodeClickedCollaspe}"
							onclick="if (#{reportProfile.editable || reportProfile.editableM}) { alert('Please save or discard your changes'); return false; }">
							<t:graphicImage value="/images/reportEUGroupCaution.gif"
								border="0" title="Emissions Unit Group Summary" />
							<t:outputText value="#{node.description}"
								style="color:#FFFFFF; background-color:#000000"
								title="Emissions Unit Group Summary"
								rendered="#{reportProfile.current == node.identifier}" />
							<t:outputText value="#{node.description}"
								title="Emissions Unit Group Summary"
								rendered="#{reportProfile.current != node.identifier}" />
							<t:updateActionListener
								property="#{reportProfile.selectedTreeNode}" value="#{node}" />
							<t:updateActionListener property="#{reportProfile.current}"
								value="#{node.identifier}" />
						</af:commandMenuItem>
					</h:panelGroup>
				</f:facet>
				<f:facet name="emissionUnits">
					<h:panelGroup>
						<af:commandMenuItem action="#{reportProfile.nodeClickedCollaspe}"
							id="emissionsUnitNode"
							onclick="if (#{reportProfile.editable || reportProfile.editableM}) { alert('Please save or discard your changes'); return false; }">
							<t:graphicImage  value="/images/EU.gif" border="0"
								title="Emissions Unit Summary" />
							<t:outputText value="#{node.description}"
								style="color:#FFFFFF; background-color:#000000"
								title="Emissions Unit Summary"
								rendered="#{reportProfile.current == node.identifier}" />
							<t:outputText value="#{node.description}"
								title="Emissions Unit Summary"
								rendered="#{reportProfile.current != node.identifier}" />
							<t:updateActionListener
								property="#{reportProfile.selectedTreeNode}" value="#{node}" />
							<t:updateActionListener property="#{reportProfile.current}"
								value="#{node.identifier}" />
						</af:commandMenuItem>
					</h:panelGroup>
				</f:facet>
				<f:facet name="emissionUnitsExcluded">
					<h:panelGroup>
						<af:commandMenuItem action="#{reportProfile.nodeClickedCollaspe}"
							onclick="if (#{reportProfile.editable || reportProfile.editableM}) { alert('Please save or discard your changes'); return false; }">
							<t:graphicImage  value="/images/ExcludedEU.gif" border="0"
								title="Emissions Unit Summary" />
							<t:outputText value="#{node.description}"
								style="color:#FFFFFF; background-color:#000000"
								title="Emissions Unit Summary"
								rendered="#{reportProfile.current == node.identifier}" />
							<t:outputText value="#{node.description}"
								title="Emissions Unit Summary"
								rendered="#{reportProfile.current != node.identifier}" />
							<t:updateActionListener
								property="#{reportProfile.selectedTreeNode}" value="#{node}" />
							<t:updateActionListener property="#{reportProfile.current}"
								value="#{node.identifier}" />
						</af:commandMenuItem>
					</h:panelGroup>
				</f:facet>
				<f:facet name="emissionUnitsCaution">
					<h:panelGroup>
						<af:commandMenuItem action="#{reportProfile.nodeClickedCollaspe}"
							onclick="if (#{reportProfile.editable || reportProfile.editableM}) { alert('Please save or discard your changes'); return false; }">
							<t:graphicImage value="/images/EUcaution.gif" border="0"
								title="Emissions Unit NEW" />
							<t:outputText value="#{node.description}"
								style="color:#FFFFFF; background-color:#000000"
								title="Emissions Unit Summary"
								rendered="#{reportProfile.current == node.identifier}" />
							<t:outputText value="#{node.description}"
								title="Emissions Unit Summary"
								rendered="#{reportProfile.current != node.identifier}" />
							<t:updateActionListener
								property="#{reportProfile.selectedTreeNode}" value="#{node}" />
							<t:updateActionListener property="#{reportProfile.current}"
								value="#{node.identifier}" />
						</af:commandMenuItem>
					</h:panelGroup>
				</f:facet>
				<f:facet name="emissionPeriods">
					<h:panelGroup>
						<af:commandMenuItem action="#{reportProfile.nodeClicked}"
							id="emissionsInventoryProcessNode"
							onclick="if (#{reportProfile.editable || reportProfile.editableM}) { alert('Please save or discard your changes'); return false; }">
							<t:graphicImage value="/images/process.gif" border="0"
								title="Emissions Process" />
							<t:outputText value="#{node.description}"
								style="color:#FFFFFF; background-color:#000000"
								title="Process & Emissions Detail"
								rendered="#{reportProfile.current == node.identifier}" />
							<t:outputText value="#{node.description}"
								title="Process & Emissions Detail"
								rendered="#{reportProfile.current != node.identifier}" />
							<t:updateActionListener
								property="#{reportProfile.selectedTreeNode}" value="#{node}" />
							<t:updateActionListener property="#{reportProfile.current}"
								value="#{node.identifier}" />
						</af:commandMenuItem>
					</h:panelGroup>
				</f:facet>
				<f:facet name="emissionPeriodsCaution">
					<h:panelGroup>
						<af:commandMenuItem action="#{reportProfile.nodeClicked}"
							onclick="if (#{reportProfile.editable || reportProfile.editableM}) { alert('Please save or discard your changes'); return false; }">
							<t:graphicImage value="/images/processCaution.gif"
								border="0" title="Emissions Process NEW" />
							<t:outputText value="#{node.description}"
								style="color:#FFFFFF; background-color:#000000"
								title="Process & Emissions Detail"
								rendered="#{reportProfile.current == node.identifier}" />
							<t:outputText value="#{node.description}"
								title="Process & Emissions Detail"
								rendered="#{reportProfile.current != node.identifier}" />
							<t:updateActionListener
								property="#{reportProfile.selectedTreeNode}" value="#{node}" />
							<t:updateActionListener property="#{reportProfile.current}"
								value="#{node.identifier}" />
						</af:commandMenuItem>
					</h:panelGroup>
				</f:facet>
				<f:facet name="emissionGPeriods">
					<h:panelGroup>
						<af:commandMenuItem action="#{reportProfile.nodeClicked}"
							onclick="if (#{reportProfile.editable || reportProfile.editableM}) { alert('Please save or discard your changes'); return false; }">
							<t:graphicImage value="/images/process.gif" border="0"
								title="Emissions Process" />
							<t:outputText value="#{node.description}"
								style="color:#FFFFFF; background-color:#000000"
								title="Process & Emissions Detail"
								rendered="#{reportProfile.current == node.identifier}" />
							<t:outputText value="#{node.description}"
								title="Process & Emissions Detail"
								rendered="#{reportProfile.current != node.identifier}" />
							<t:updateActionListener
								property="#{reportProfile.selectedTreeNode}" value="#{node}" />
							<t:updateActionListener property="#{reportProfile.current}"
								value="#{node.identifier}" />
						</af:commandMenuItem>
					</h:panelGroup>
				</f:facet>
				<f:facet name="emissionGPeriodsCaution">
					<h:panelGroup>
						<af:commandMenuItem action="#{reportProfile.nodeClicked}"
							onclick="if (#{reportProfile.editable || reportProfile.editableM}) { alert('Please save or discard your changes'); return false; }">
							<t:graphicImage value="/images/processCaution.gif"
								border="0" title="Emissions Process NEW" />
							<t:outputText value="#{node.description}"
								style="color:#FFFFFF; background-color:#000000"
								title="Process & Emissions Detail"
								rendered="#{reportProfile.current == node.identifier}" />
							<t:outputText value="#{node.description}"
								title="Process & Emissions Detail"
								rendered="#{reportProfile.current != node.identifier}" />
							<t:updateActionListener
								property="#{reportProfile.selectedTreeNode}" value="#{node}" />
							<t:updateActionListener property="#{reportProfile.current}"
								value="#{node.identifier}" />
						</af:commandMenuItem>
					</h:panelGroup>
				</f:facet>
			</t:tree2>
		</t:div>
	</f:facet>
	<h:panelGrid columns="1" border="1" width="80%">
	
		<f:subview id="report">
			<jsp:include page="facilityRep.jsp" />
		</f:subview>

		<f:subview id="emissionUnitGroups">
			<jsp:include page="emissionUnitGrpRep.jsp" />
		</f:subview>

		<f:subview id="emissionUnits">
			<jsp:include page="emissionUnitRep.jsp" />
		</f:subview>

		<f:subview id="emissionPeriods">
			<jsp:include page="emissionPeriod.jsp" />
		</f:subview>

	</h:panelGrid>
</af:panelBorder>


