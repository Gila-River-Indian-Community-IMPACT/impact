<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Invoice Detail">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Invoice Detail">
				<jsp:include flush="true" page="header.jsp" />
				<f:subview id="invoice2" rendered="#{!invoiceDetail.err}">
					<%@ page session="true" contentType="text/html;charset=utf-8"%>


					<af:panelBox background="light" width="980">
						<af:panelForm rows="2" maxColumns="4">
							<af:inputText label="Facility ID:" readOnly="True"
								value="#{invoiceDetail.invoice.facilityId}" />
							<af:inputText label="Facility Name:" readOnly="True"
								value="#{invoiceDetail.invoice.facilityName}" />
							<af:inputText label="Invoice ID:" readOnly="True"
								value="#{invoiceDetail.invoice.invoiceId}" />
							<af:inputText label="Invoice State:" readOnly="True"
								value="#{invoiceDetail.invoice.invoiceStateDsc}" />
							<af:inputText label="Revenue ID:" readOnly="True"
								value="#{invoiceDetail.invoice.revenueId}" />
							<af:inputText label="Revenue Type:" readOnly="True"
								value="#{invoiceDetail.invoice.revenueTypeCd}" />
							<af:inputText label="Revenue State:" readOnly="True"
								value="#{invoiceDetail.invoice.posted?invoiceDetail.revenueState:''}"/>
							<af:inputText label="Intra State Voucher:" readOnly="True"
								value="#{invoiceDetail.intraStateVoucherFlag ?'Yes':'No'}" />
						</af:panelForm>
					</af:panelBox>

					<af:showDetailHeader text="Billing Details" disclosed="true">
						<jsp:include page="billingInfo.jsp" />
					</af:showDetailHeader>

					<af:objectSpacer height="15" />

					<afh:rowLayout halign="center"
						rendered="#{! empty invoiceDetail.invoice.prevInvoiceFailureMsg}">
						<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
							value="<b>#{invoiceDetail.invoice.prevInvoiceFailureMsg} </b>" />
					</afh:rowLayout>

					<af:showDetailHeader text="Adjusted Invoice" disclosed="true"
						rendered="#{! empty invoiceDetail.invoice.emissionsRptId && invoiceDetail.invoice.adjustedInvoice}">
						<jsp:include page="adjustedInvoice.jsp" />
					</af:showDetailHeader>
					<af:outputText rendered="#{invoiceDetail.autoGenRpt}"
						value="This invoice is based upon an Auto-Generated report."
						inlineStyle="color: orange; font-weight: bold;" />
					<af:showDetailHeader text="Emission Report Details"
						disclosed="true"
						rendered="#{invoiceDetail.invoice.revenueTypeCd != 'PTIDA'}">
						<jsp:include page="erDetails.jsp" />
					</af:showDetailHeader>

					<af:showDetailHeader text="Adjustments" disclosed="true">
						<jsp:include page="revenueAdjustments.jsp" />
					</af:showDetailHeader>

					<af:showDetailHeader text="Other Invoices"
						rendered="#{invoiceDetail.invoice.multiInvoice}" disclosed="true">
						<jsp:include page="otherInvoicesTable.jsp" />
					</af:showDetailHeader>

					<af:objectSpacer height="15" />

					<af:showDetailHeader text="Notes" disclosed="true">
						<jsp:include page="notesTable.jsp" />
					</af:showDetailHeader>

					<af:objectSpacer height="15" />

					<afh:rowLayout halign="center">
						<af:commandButton immediate="true"
							text="Show Current Facility Inventory"
							rendered="#{! invoiceDetail.editable}"
							action="#{facilityProfile.submitProfileById}">
							<t:updateActionListener property="#{facilityProfile.facilityId}"
								value="#{invoiceDetail.invoice.facilityId}" />
							<t:updateActionListener
								property="#{menuItem_facProfile.disabled}" value="false" />
						</af:commandButton>
						<af:objectSpacer width="5" />
						<af:panelButtonBar
							rendered="#{invoiceDetail.postToRevenuesVisible}">
							<af:commandButton text="Edit"
								disabled="#{! invoiceDetail.stars2Admin 
												&& invoiceDetail.invoice.invoiceStateCd !='9inv'}"
								rendered="#{! invoiceDetail.editable}"
								action="#{invoiceDetail.editContact}" />
							<af:commandButton text="Save"
								rendered="#{invoiceDetail.editable}"
								action="#{invoiceDetail.saveEditData}" />
							<af:commandButton text="Cancel" immediate="true"
								rendered="#{invoiceDetail.editable}"
								action="#{invoiceDetail.cancelEdit}" />
							<af:commandButton
								disabled="#{invoiceDetail.invoice.invoiceStateCd != '9inv'}"
								rendered="#{! invoiceDetail.editable}" text="#{invoiceDetail.invoice.adjustedInvoice?'Post Adjustment to Revenues':'Post to Revenues'}"
								action="dialog:confirmInvoiceOperation" useWindow="true"
								windowWidth="600" windowHeight="180">
								<t:updateActionListener
									property="#{invoiceDetail.confirmOperation}"
									value="Post to Revenues" />
							</af:commandButton>
							<af:commandButton text="New Adjustment"
								rendered="#{! empty invoiceDetail.invoice.revenueId && ! invoiceDetail.editable}"
								disabled="#{invoiceDetail.invoice.invoiceStateCd == '9inv'}"
								action="#{invoiceDetail.adjustmentDialog}" useWindow="true"
								windowWidth="600" windowHeight="500" />
							<af:commandButton text="Workflow Task"
								rendered="#{! empty invoiceDetail.invoice.emissionsRptId && reportDetail.fromTODOList}"
								action="#{reportDetail.goToCurrentWorkflow}" />
							<af:commandButton text="Workflow Task"
								rendered="#{! empty invoiceDetail.invoice.permitId && permitDetail.fromTODOList}"
								action="#{permitDetail.goToCurrentWorkflow}" />
							<af:commandButton text="Cancel Invoice"
								disabled="#{invoiceDetail.invoice.invoiceStateCd != '9inv'}"
								rendered="#{! invoiceDetail.editable}"
								action="dialog:confirmInvoiceOperation" useWindow="true"
								windowWidth="600" windowHeight="180">
								<t:updateActionListener
									property="#{invoiceDetail.confirmOperation}" value="Cancel" />
							</af:commandButton>
						</af:panelButtonBar>
					</afh:rowLayout>
					<af:objectSpacer height="10" />
					<afh:rowLayout halign="center">
						<af:commandButton text="#{invoiceDetail.unPostLabel}"
							rendered="#{invoiceDetail.allowUnPost}" useWindow="true"
							windowWidth="600" windowHeight="200"
							action="dialog:confirmUnPost">
							<t:updateActionListener
								property="#{invoiceDetail.confirmOperation}" value="UnPost" />
						</af:commandButton>
						<af:panelButtonBar
							rendered="#{invoiceDetail.postToRevenuesVisible}">
							<af:commandButton text="Activate Revenue"
								disabled="#{invoiceDetail.invoice.revenue.active}"
								rendered="#{! empty invoiceDetail.invoice.revenueId 
												&& ! invoiceDetail.editable}"
								useWindow="true" windowWidth="600" windowHeight="180"
								action="dialog:confirmIndicatorAdjustment">
								<t:updateActionListener
									property="#{invoiceDetail.adjustmentType}" value="activate" />
							</af:commandButton>
							<af:commandButton text="Prepare for New Revenue ID"
								rendered="#{invoiceDetail.newRevenueButton}" useWindow="true"
								windowWidth="600" windowHeight="180"
								action="dialog:createNewRevenueId">
							</af:commandButton>
							<af:commandButton text="Deactivate Revenue"
								disabled="#{! invoiceDetail.invoice.revenue.active}"
								rendered="#{! empty invoiceDetail.invoice.revenueId 
												&& ! invoiceDetail.editable}"
								useWindow="true" windowWidth="600" windowHeight="180"
								action="dialog:confirmIndicatorAdjustment">
								<t:updateActionListener
									property="#{invoiceDetail.adjustmentType}" value="deActivate" />
							</af:commandButton>
						</af:panelButtonBar>
						<af:objectSpacer width="5" />
						<af:commandButton
							disabled="#{invoiceDetail.invoice.invoiceStateCd =='9inv' || invoiceDetail.readOnlyUser ||
							! invoiceDetail.invoice.haveBillingAddress ||invoiceDetail.invoice.invoiceStateCd == '12cn'}"
							rendered="#{! invoiceDetail.editable && invoiceDetail.postToRevenuesVisible}"
							text="Generate Document" action="dialog:confirmInvoiceOperation"
							useWindow="true" windowWidth="600" windowHeight="180">
							<t:updateActionListener
								property="#{invoiceDetail.confirmOperation}"
								value="Print Invoice" />
						</af:commandButton>
						<af:objectSpacer width="5" />
						<af:commandButton text="Edit Billing Address"
							disabled="#{invoiceDetail.readOnlyUser}"
							rendered="#{! invoiceDetail.editable && invoiceDetail.invoice.contact != null}"
							action="dialog:viewContact" useWindow="true" windowWidth="950"
							windowHeight="700" />

					</afh:rowLayout>

				</f:subview>
			</af:page>
		</af:form>
	</af:document>
</f:view>
