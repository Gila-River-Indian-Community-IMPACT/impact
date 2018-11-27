<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="nsrPermitGenerateInvoiceBody"
		title="NSR Permit Generate Invoice">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<af:form>
			<af:panelForm rows="9" maxColumns="1" labelWidth="140"
				partialTriggers="invoiceType">
				<afh:rowLayout halign="left">
					<af:selectOneChoice id="invoiceType"
						label="Invoice Type :"
						autoSubmit="true"
						showRequired="true"
						value="#{permitDetail.invoiceType}"
						unselectedLabel=""
						valueChangeListener="#{permitDetail.computeInvoiceReferenceDate}">
						<f:selectItems id="nsrBillingInvoiceTypes"
							value="#{permitDetail.NSRBillingFeeTypeDefs}" />
					</af:selectOneChoice>
				</afh:rowLayout>
				<afh:rowLayout halign="left">
					<af:selectInputDate label="Invoice Reference Date" id="invoiceRefDate"
						rendered="#{permitDetail.invoiceType != null}"
						valign="middle"	showRequired="true"
						value="#{permitDetail.invoiceRefDate}">
						<af:validateDateTimeRange
							minimum="#{permitDetail.minimumInvoiceRefDate}"
							maximum="#{permitDetail.todaysDate}"/>
					</af:selectInputDate>
				</afh:rowLayout>

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Generate"
							action="#{permitDetail.generateNSRPermitInvoice}" />
						<af:commandButton text="Cancel"
							immediate="true"
							action="#{permitDetail.cancelGenerateInvoice}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>