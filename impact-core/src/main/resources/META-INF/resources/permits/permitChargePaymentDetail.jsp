<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="permitChargePaymentBody"
		title="Permit Charge Payment Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:panelHeader text="Permit Charge Payment Detail" size="0" />
			<af:panelForm rows="9" maxColumns="1" labelWidth="140" partialTriggers="transactionType">
				
				<afh:rowLayout halign="left">
					<af:selectInputDate label="Date" id="transactionDate"
						readOnly="#{!permitDetail.editMode1}" valign="middle"
						showRequired="true"
						value="#{permitDetail.modifyPermitChargePayment.transactionDate}">
						<af:validateDateTimeRange
							minimum="1970-01-01"
							maximum="#{permitDetail.todaysDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
				<afh:rowLayout halign="left">
					<af:selectOneChoice label="Charge/Payment Type"
						readOnly="#{!permitDetail.editMode1}" id="transactionType"
						autoSubmit="true"
						showRequired="true"
						value="#{permitDetail.modifyPermitChargePayment.transactionType}"
						unselectedLabel="">
						<mu:selectItems id="nsrBillingChargePaymentTypes"
							value="#{permitDetail.NSRBillingChargePaymentTypeDefs}" />
					</af:selectOneChoice>
				</afh:rowLayout>
				<afh:rowLayout halign="left">
					<af:inputText
						value="#{permitDetail.modifyPermitChargePayment.noteTxt}"
						label="Comment: " readOnly="#{!permitDetail.editMode1}"
						columns="80" rows="7" id="noteTxt" maximumLength="500"
						onkeydown="charsLeft(500);" onkeyup="charsLeft(500);" />
				</afh:rowLayout>

				<afh:rowLayout>
					<afh:cellFormat halign="right">
						<h:outputText style="font-size:12px" value="Characters Left :" />
					</afh:cellFormat>
					<afh:cellFormat halign="left">
						<h:inputText readonly="true" size="3" value="500" id="messageText" />
					</afh:cellFormat>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:inputText maximumLength="7" label="Transmittal Number"
						rendered="#{permitDetail.modifyPermitChargePayment.payment
									|| permitDetail.modifyPermitChargePayment.otherCredit}"
						readOnly="#{!permitDetail.editMode1}"
						value="#{permitDetail.modifyPermitChargePayment.transmittalNumber}"
						columns="7">
						<af:convertNumber type="number" pattern="#######" />
					</af:inputText>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:inputText label="Check Number" maximumLength="12"
						rendered="#{permitDetail.modifyPermitChargePayment.payment
									|| permitDetail.modifyPermitChargePayment.otherCredit}"
						readOnly="#{!permitDetail.editMode1}"
						showRequired="#{permitDetail.modifyPermitChargePayment.payment}"
						value="#{permitDetail.modifyPermitChargePayment.checkNumber}"
						columns="12"> 
						<af:validateRegExp pattern="[a-zA-Z0-9]+" noMatchMessageDetail="Check Number must be alphanumeric."/>
					</af:inputText>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:inputText label="Amount :"
						readOnly="#{!permitDetail.editMode1}"
						showRequired="true"
						value="#{permitDetail.modifyPermitChargePayment.transactionAmount}"
						rendered="#{!permitDetail.editMode1}">
						<af:convertNumber type='currency' locale="en-US"
							minFractionDigits="2" />
					</af:inputText>

					<af:inputText label="Amount :"
						maximumLength="10"
						showRequired="true"
						value="#{permitDetail.modifyPermitChargePayment.transactionAmount}"
					    rendered="#{permitDetail.editMode1}"
					    columns="10">
					    <af:convertNumber type='number' locale="en-US"
					    maxFractionDigits="6"/>
					</af:inputText>
				</afh:rowLayout>

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							rendered="#{!permitDetail.editMode1}"
							disabled="#{!permitDetail.NSRFeeSummaryEditAllowed}"
							action="#{permitDetail.editPermitChargePayment}" />
						<af:commandButton text="Save" rendered="#{permitDetail.editMode1}"
							action="#{permitDetail.savePermitChargePayment}" />
						<af:commandButton text="Cancel"
							immediate="true"
							rendered="#{permitDetail.editMode1}"
							action="#{permitDetail.cancelPermitChargePayment}" />
						<af:commandButton text="Delete"
							rendered="#{!permitDetail.editMode1}"
							disabled="#{!permitDetail.NSRFeeSummaryEditAllowed}"
							action="#{permitDetail.deletePermitChargePayment}" />
						<af:commandButton text="Close"
							immediate="true"
							rendered="#{!permitDetail.editMode1}"
							action="#{permitDetail.closePermitChargePaymentDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:panelForm>
		</af:form>
		<f:verbatim>
			<script type="text/javascript">
				charsLeft(500);
			</script>
			<%@ include file="../scripts/jquery-1.9.1.min.js"%>
		</f:verbatim>
	</af:document>
</f:view>
