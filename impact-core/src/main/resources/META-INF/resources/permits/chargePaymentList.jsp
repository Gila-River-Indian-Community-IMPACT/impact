<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" rendered="true">
	<af:showDetailHeader text="Charge/Payment History" disclosed="true">

		<%
            /* Content begin */
            %>
		<h:panelGrid columns="1" border="1"
			width="#{permitDetail.permitWidth}">
			<af:panelGroup>
				<afh:rowLayout halign="center">
					<af:table value="#{permitDetail.permitChargePaymentWrapper}"
						binding="#{permitDetail.permitChargePaymentWrapper.table}"
						bandingInterval="1" banding="row" id="chargePaymentTab"
						width="#{permitDetail.permitTableWidth}" var="chargePayment"
						rows="#{permitDetail.pageLimit}" emptyText=" " varStatus="chargePaymentTableVs">

						<af:column id="edit" formatType="text" headerText="Row Id" width="5%"
							noWrap="true">
							<af:panelHorizontal valign="middle" halign="left">
								<af:commandLink useWindow="true" windowWidth="900"
									windowHeight="500" inlineStyle="padding-left:5px;"
									returnListener="#{permitDetail.dialogDone}"
									action="#{permitDetail.startToEditPermitChargePayment}"
									id="rowID">
									<af:inputText value="#{chargePaymentTableVs.index+1}"
										readOnly="true" valign="middle">
										<af:convertNumber pattern="000" />
									</af:inputText>
								</af:commandLink>
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Date" formatType="text" width="5%"
							sortProperty="transactionDate" sortable="true"
							id="transactionDate">
							<af:panelHorizontal valign="middle" halign="left">
								<af:selectInputDate label="Date" readOnly="true" valign="middle"
									required="true" value="#{chargePayment.transactionDate}">
									<af:validateDateTimeRange minimum="1900-01-01" />
								</af:selectInputDate>
							</af:panelHorizontal>
						</af:column>
						<af:column formatType="text"
							headerText="Charge/Payment Type" sortProperty="transactionType"
							sortable="true" id="transactionType" width="20%">
							<af:panelHorizontal valign="middle" halign="left">
								<af:selectOneChoice label="Charge/Payment Type" readOnly="true"
									value="#{chargePayment.transactionType}" unselectedLabel="">
									<mu:selectItems id="nsrBillingChargePaymentTypes"
										value="#{permitReference.NSRBillingChargePaymentTypeDefs}" />
								</af:selectOneChoice>
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Comment" width="50%" formatType="text"
							sortProperty="noteTxt" sortable="true" id="noteTxt">
							<af:panelHorizontal valign="middle" halign="left">
								<af:inputText
									label="Comment" readOnly="true"
									value="#{chargePayment.noteTxt}" />
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Transmittal Number" formatType="number"
							sortProperty="transmittalNumber" sortable="true" width="10%"
							id="transmittalNumber">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText maximumLength="50" label="Transmittal Number"
									readOnly="true" value="#{chargePayment.transmittalNumber}"
									columns="15">
									<af:convertNumber type="number" pattern="#######" />
								</af:inputText>
							</af:panelHorizontal>
						</af:column>
						<af:column headerText="Check Number" width="10%"
							sortProperty="checkNumber" sortable="true" formatType="number"
							id="checkNumber">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText label="Check Number" readOnly="true"
									maximumLength="50" value="#{chargePayment.checkNumber}"
									columns="15" />
							</af:panelHorizontal>
						</af:column>
						<af:column headerText="Amount" sortProperty="transactionAmount"
							sortable="true" formatType="number" width="15%"
							id="transactionAmount">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText label="Amount :" readOnly="true"
									value="#{chargePayment.transactionAmountString}"
									id="amount">
								</af:inputText>
							</af:panelHorizontal>
						</af:column>

						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
									<af:objectSpacer width="375" height="3" />
									<af:commandButton text="Add" id="addPermitChargePayment"
										action="#{permitDetail.startToAddPermitChargePayment}"
										useWindow="true" windowWidth="900" windowHeight="450"
										inlineStyle="padding-left:5px;"
										returnListener="#{permitDetail.dialogDone}" 
										disabled="#{!permitDetail.NSRFeeSummaryEditAllowed}"/>
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
									<af:objectSpacer width="170" height="3" />
									<af:panelHorizontal valign="middle" halign="left">
										<af:outputLabel value="Current Balance:" />
									</af:panelHorizontal>
									<af:objectSpacer width="17" height="5" />
									<af:panelHorizontal valign="middle" halign="right">
										<af:outputText
											value="#{permitDetail.totalFormatedCurrentBalance}"
											 id="currentBalanceAmount" />
									</af:panelHorizontal>
								</af:panelGroup>
							</afh:rowLayout>
						</f:facet>
					</af:table>
					<%
                  /* Charge Payment list end */
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


