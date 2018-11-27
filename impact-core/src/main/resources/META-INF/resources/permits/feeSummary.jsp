<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <af:document title="Fee Summary">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form usesUpload="true">
      <af:page id="ThePage" var="foo" value="#{menuModel.model}"
        title="Fee Summary">
        <%@ include file="../permits/header.jsp"%>

        <h:panelGrid border="1">
          <af:panelBorder>

            <f:facet name="top">
              <f:subview id="permitDetailTop">
                <jsp:include page="permitDetailTop.jsp" />
              </f:subview>
            </f:facet>
            <%
            /* Top end */
            %>

            <%
            /* Content begin */
            %>
            
            <h:panelGrid columns="1" border="1"
              width="#{permitDetail.permitWidth}">
              <af:panelGroup>
              	<af:objectSpacer height="10" />
              	<af:panelForm rows="1" labelWidth="48%" fieldWidth="52%">
					<af:selectBooleanCheckbox id="billable"
						label="Billable? :" value="#{permitDetail.permit.billable}"
						readOnly="#{!permitDetail.editMode}" />
					<af:selectInputDate id="lastInvoiceRefDate"
						label="Last Invoice Reference Date :" value="#{permitDetail.permit.lastInvoiceRefDate}"
						readOnly="#{!(permitDetail.editMode && (permitDetail.stars2Admin || permitDetail.NSRAdmin))}">
						<af:validateDateTimeRange maximum="#{permitDetail.todaysDate}"/>
					</af:selectInputDate>
				</af:panelForm>
				<af:panelForm rows="1" labelWidth="48%" fieldWidth="52%">
					<af:inputText id="paykey"
						label="Pay Key :" value="#{permitDetail.permit.companyPayKey}"
						readOnly="true" />
					<af:inputText id="vendornumber"
						label="Vendor Number :" value="#{permitDetail.permit.companyVendorNumber}"
						readOnly="true">
						<af:convertNumber pattern="0000000000"/>
					</af:inputText>	
				</af:panelForm>
                <af:panelHeader text="Fee" rendered="#{!permitDetail.permit.feeNullOrEmpty}">
                    <af:panelForm maxColumns="2" rows="1"
                      labelWidth="44%" fieldWidth="56%">
					  <af:inputText label="Initial Invoice Amount :"
                        columns="10" maximumLength="50"
                        value="#{(permitDetail.permit.initialInvoice != 0.0)?permitDetail.permit.initialInvoice:''}"
                        readOnly="#{!permitDetail.editMode}"
                        rendered="#{!permitDetail.editMode}">
                        <af:convertNumber type='currency' locale="en-US"
                          minFractionDigits="2" />
                      </af:inputText>
                      <af:inputText label="Initial Invoice Amount :"
                        columns="10"
                        value="#{permitDetail.permit.initialInvoice}"
                        readOnly="#{!permitDetail.editMode}"
                        rendered="#{permitDetail.editMode}">
                      </af:inputText>
                      
                      <af:inputText label="Final Invoice Amount :"
                        columns="10" maximumLength="50"
                        value="#{(permitDetail.permit.finalInvoice != 0.0)?permitDetail.permit.finalInvoice:''}"
                        readOnly="#{!permitDetail.editMode}"
                        rendered="#{!permitDetail.editMode}">
                        <af:convertNumber type='currency' locale="en-US"
                          minFractionDigits="2" />
                      </af:inputText>
                      <af:inputText label="Final Invoice Amount :"
                        columns="10"
                        value="#{permitDetail.permit.finalInvoice}"
                        readOnly="#{!permitDetail.editMode}"
                        rendered="#{permitDetail.editMode}">
                      </af:inputText>
                      
                      <af:inputText label="Total Amount :" columns="10"
                        value="#{(permitDetail.permit.initialInvoice != 0.0 && permitDetail.permit.finalInvoice != 0.0)?permitDetail.permit.totalAmount:''}"
                        readOnly="true">
                        <af:convertNumber type='currency' locale="en-US"
                          minFractionDigits="2" />
                      </af:inputText>
                    </af:panelForm>
                </af:panelHeader>
                <%
                /* Fees end */
                %>
                
                <f:subview id="permit_fixed_charge">
						<jsp:include flush="true" page="nsrFixedChargeList.jsp" />
				</f:subview>

				<af:objectSpacer height="10" />
                
                <f:subview id="time_sheet_info">
						<jsp:include flush="true" page="nsrTimeSheetRowList.jsp" />
				</f:subview>

				<af:objectSpacer height="10" />
                
                <f:subview id="permit_charge_payment">
						<jsp:include flush="true" page="chargePaymentList.jsp" />
				</f:subview>

				<af:objectSpacer height="10" />

				<f:subview id="permit_attachments">
						<jsp:include page="feeSummaryAttachments.jsp" />
				</f:subview>

				<af:objectSpacer height="10" />

				<%
                /* Buttons begin */
                %>
               <f:subview id="permitDetailButtons">
	                <afh:rowLayout halign="center" rendered="#{!permitDetail.readOnlyUser}">
					  <af:switcher defaultFacet="view"
					    facetName="#{permitDetail.editMode ? 'edit': 'view'}">
					    <f:facet name="view">
					      <af:panelButtonBar>
					        <af:commandButton text="Edit"
					          rendered="#{permitDetail.permit.permitType == 'NSR' && permitDetail.NSRFeeSummaryEditAllowed}"
					          action="#{permitDetail.enterFeeSummaryEditMode}"
					          id="feeSummaryEditBtn" />
					        <af:commandButton text="Workflow Task"
					          disabled="#{!permitDetail.fromTODOList}"
					          action="#{permitDetail.goToCurrentWorkflow}"
					          id="feeSummaryWorkflowTaskBtn" />
					        <af:commandButton text="Generate Invoice"
							  rendered="#{permitDetail.permit.permitType == 'NSR' && permitDetail.NSRFeeSummaryEditAllowed}" 	
					          disabled="#{!(permitDetail.permit.billable && permitDetail.permit.timeCardInfoRetrieved)}" 
					          useWindow="true" windowHeight="250" windowWidth="450"
					          action="#{permitDetail.startGenerateInvoice}"
					          id="feeSummaryGenerateInvoiceBtn"/>
					      </af:panelButtonBar>
					    </f:facet>
					    <f:facet name="edit">
					      <af:panelButtonBar>
					        <af:commandButton text="Save changes"
					          action="#{permitDetail.updatePermit}"
					          id="feeSummarySaveBtn" />
					        <af:commandButton text="Discard changes" immediate="true"
					          action="#{permitDetail.undoPermit}"
					          id="feeSummaryDiscardBtn" />
					      </af:panelButtonBar>
					    </f:facet>
					  </af:switcher>
					</afh:rowLayout>
                </f:subview>
                <%
                /* Buttons end */
                %>
              </af:panelGroup>
            </h:panelGrid>
            <%
            /* Content end */
            %>

          </af:panelBorder>
        </h:panelGrid>
      </af:page>
      <af:iterator value="#{permitDetail}" var="validationBean" id="v">
				<%@ include file="../util/validationComponents.jsp"%>
	  </af:iterator>
    </af:form>
  </af:document>
</f:view>
