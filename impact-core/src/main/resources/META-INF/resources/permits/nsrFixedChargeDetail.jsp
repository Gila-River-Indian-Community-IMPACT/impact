<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="nsrFixedChargeBody"
		title="NSR Permit Fixed Charge Detail">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:panelHeader text="NSR Permit Fixed Charge Detail" size="0" />
			<af:panelForm rows="9" maxColumns="1" labelWidth="140">
				
				<afh:rowLayout halign="left">
					<af:selectInputDate label="Date" id="createdDate"
						readOnly="#{!permitDetail.editMode1}" valign="middle"
						showRequired="true"
						value="#{permitDetail.modifyNSRFixedCharge.createdDate}">
						<af:validateDateTimeRange 
							minimum="1970-01-01"
							maximum="#{permitDetail.todaysDate}" />
					</af:selectInputDate>
				</afh:rowLayout>
				<afh:rowLayout halign="left">
					<af:inputText maximumLength="30" label="Description" readOnly="#{!permitDetail.editMode1}"
						showRequired="true"
						value="#{permitDetail.modifyNSRFixedCharge.description}" columns="30">
					</af:inputText>
				</afh:rowLayout>

				<afh:rowLayout halign="left">
					<af:inputText
						value="#{permitDetail.modifyNSRFixedCharge.noteTxt}"
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
					<af:inputText label="Amount :"
						readOnly="#{!permitDetail.editMode1}"
						showRequired="true"
						value="#{permitDetail.modifyNSRFixedCharge.amount}"
						rendered="#{!permitDetail.editMode1}">
						<af:convertNumber type='currency' locale="en-US"
							minFractionDigits="2" />
					</af:inputText>

					<af:inputText label="Amount :"
						maximumLength="16"
						readOnly="#{!permitDetail.editMode1}"
						showRequired="true"
						value="#{permitDetail.modifyNSRFixedCharge.amount}"
						valueChangeListener="#{permitDetail.modifyNSRFixedCharge.amountValueChanged}"
					    rendered="#{permitDetail.editMode1}"
					    columns="16">
						<af:convertNumber type="number" maxFractionDigits="2"
							minFractionDigits="2" />
					</af:inputText>
				</afh:rowLayout>
				
				<afh:rowLayout>
					<af:selectBooleanCheckbox id="invoiced"
						rendered="#{infraDefs.timesheetEntryEnabled && infraDefs.stars2Admin}"
						readOnly="#{!permitDetail.editMode1}"
						label="Invoiced :"
						value="#{permitDetail.modifyNSRFixedCharge.invoiced}"
						valueChangeListener="#{permitDetail.modifyNSRFixedCharge.invoicedValueChanged}" />
				</afh:rowLayout>

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							rendered="#{!permitDetail.editMode1}"
							disabled="#{!permitDetail.NSRFixedChargeEditOrDeleteAllowed}"
							action="#{permitDetail.editNSRFixedCharge}" />
						<af:commandButton text="Save" rendered="#{permitDetail.editMode1}"
							action="#{permitDetail.saveNSRFixedCharge}" />
						<af:commandButton text="Cancel"
							immediate="true"
							rendered="#{permitDetail.editMode1}"
							action="#{permitDetail.cancelNSRFixedCharge}" />
						<af:commandButton text="Delete"
							rendered="#{!permitDetail.editMode1}"
							disabled="#{!permitDetail.NSRFixedChargeEditOrDeleteAllowed}"
							action="#{permitDetail.deleteNSRFixedCharge}" />
						<af:commandButton text="Close"
							immediate="true"
							rendered="#{!permitDetail.editMode1}"
							action="#{permitDetail.closeNSRFixedChargeDialog}" />
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