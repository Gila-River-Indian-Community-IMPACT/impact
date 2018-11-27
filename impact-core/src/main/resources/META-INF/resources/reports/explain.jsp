<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}"
		title="Explain Calculation of Emissions">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}">
				<af:panelHeader messageType="Information"
					text="Calculating Emissions" size="0" />
				<af:outputFormatted styleUsage="instruction"
					value="The following describes the process of calculating emissions, recommends calculation methods based on certain criteria, reviews applicable FIRE factor(s), and shows a breakdown of any computed emissions." />
				<af:objectSpacer height="10px" />

				<af:selectOneChoice
					label="Pollutant: "
					value="#{reportProfile.emissionRowMethod.pollutantCd}"
					readOnly="true">
					<f:selectItems
						value="#{facilityReference.nonToxicPollutantDefs.items[(empty reportProfile.emissionRowMethod.pollutantCd ? '' : reportProfile.emissionRowMethod.pollutantCd)]}" />
				</af:selectOneChoice>

				<af:objectSpacer height="10px" />

				<%@ include file="explainRecommendedMethods.jsp"%>
				<af:objectSpacer height="10px" />

				<%@ include file="explainMethods.jsp"%>
				<af:objectSpacer height="10px" />

				<%@ include file="explainFactors.jsp"%>
				<af:objectSpacer height="10px" />

				<%@ include file="explainCalculations.jsp"%>

				<afh:rowLayout halign="center">
					<af:objectSpacer width="10" height="10" />
					<af:panelButtonBar>
						<af:commandButton text="Select FIRE Factor" id="FIREbutton"
							rendered="#{!reportProfile.fireEditable && reportProfile.fireWrapper.table.rowCount > 1 && reportProfile.emissionRowMethod.throughputBasedFactor}"
							action="#{reportProfile.editFire}">
						</af:commandButton>
						<af:commandButton text="Save FIRE Factor Selection"
							rendered="#{reportProfile.fireEditable}"
							action="#{reportProfile.saveFireSelection}" />
						<af:commandButton text="Cancel FIRE Factor Selection"
							rendered="#{reportProfile.fireEditable}"
							action="#{reportProfile.closeCancelFIRE}" immediate="true" />
						<af:commandButton text="Close"
							rendered="#{!reportProfile.fireEditable}"
							action="#{reportProfile.viewCalcOK}" immediate="true" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
