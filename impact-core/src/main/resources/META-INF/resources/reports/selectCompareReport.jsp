<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" title="Select Emissions Inventory to Compare">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:messages />
			<af:page var="foo" value="#{menuModel.model}"
				title="Select Emissions Inventory to Compare">

				<af:objectSpacer width="100%" height="15" />
				<af:panelBorder>
					<f:facet name="left">
						<afh:rowLayout halign="left">
							<af:outputFormatted
								value="What is compared in each process:<ul>  <li>Hours of Operation per Year</li>  <li>Material Throughput</li>  <li>Variable Value</li>  <li>Emission Row (details to right)</li> </ul> " />
						</afh:rowLayout>
					</f:facet>
					<f:facet name="right">
						<afh:rowLayout halign="left">
							<af:outputFormatted
								value="What is compared in each emissions row:<ul>  <li>Method Used</li>  <li>Throughput-based factor value</li> <li>Time-based factor value</li> <li>Fugitive Amount</li> <li>Stack Amount</li>  <li>Total</li> </ul>" />
						</afh:rowLayout>
					</f:facet>
					<f:facet name="bottom">
						<afh:rowLayout halign="left">
							<af:outputFormatted
								value="Numeric differences are highlighted if the inventory value (first value/row) differs from the comparison inventory (second value/row) by more than +/- the comparison percentage from the comparison value. <br><br> Emissions differences (in Fugitive, Stack or Total) also highlights the pollutant name." />
						</afh:rowLayout>
					</f:facet>
				</af:panelBorder>
				<af:objectSpacer width="100%" height="3" />
				<af:panelBorder>
					<f:facet name="top">
						<af:panelForm>
							<afh:rowLayout halign="center">
								<af:inputText
									label="Highlight emissions differences of more than"
									value="#{reportProfile.percentDiff}" columns="2" />
								<af:outputText value="%" />
							</afh:rowLayout>
						</af:panelForm>
					</f:facet>
					<f:facet name="bottom">
						<h:panelGrid border="1">
							<jsp:include flush="true" page="erSelectTable.jsp" />
						</h:panelGrid>
					</f:facet>
				</af:panelBorder>
				<af:objectSpacer width="100%" height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Compare"
							action="#{reportProfile.compareReportSelection}">
							<t:updateActionListener
								property="#{reportProfile.selectedReportId}"
								value="#{reportSearch.selectedReportId}" />
						</af:commandButton>
						<af:commandButton text="Cancel"
							action="#{reportProfile.cancelCompareReportDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

