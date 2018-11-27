<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" rendered="true">
	<af:showDetailHeader text="Fixed/Other Charges" disclosed="true">

		<%
            /* Content begin */
            %>
		<h:panelGrid columns="1" border="1"
			width="#{permitDetail.permitWidth}">
			<af:panelGroup>
				<afh:rowLayout halign="center">
					<af:table value="#{permitDetail.NSRFixedChargeWrapper}"
						binding="#{permitDetail.NSRFixedChargeWrapper.table}"
						bandingInterval="1" banding="row" id="fixedChargeTab"
						width="#{permitDetail.permitTableWidth}" var="fixedCharge"
						rows="#{permitDetail.pageLimit}" emptyText=" " varStatus="fixedChargeTableVs">

						<af:column id="edit" formatType="text" headerText="Row Id">
							<af:panelHorizontal valign="middle" halign="left">
								<af:commandLink useWindow="true" windowWidth="900"
									windowHeight="500" inlineStyle="padding-left:5px;"
									returnListener="#{permitDetail.dialogDone}"
									action="#{permitDetail.startToEditNSRFixedCharge}">
									<af:inputText value="#{fixedChargeTableVs.index+1}"
										readOnly="true" valign="middle">
										<af:convertNumber pattern="000" />
									</af:inputText>
								</af:commandLink>
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Date" formatType="text"
							sortProperty="createdDate" sortable="true"
							id="createdDate" noWrap="true" headerNoWrap="true">
							<af:panelHorizontal valign="middle" halign="left">
								<af:selectInputDate label="Date" readOnly="true" valign="middle"
									required="true" value="#{fixedCharge.createdDate}">
									<af:validateDateTimeRange minimum="1900-01-01" />
								</af:selectInputDate>
							</af:panelHorizontal>
						</af:column>
						
						<af:column headerText="Description" formatType="text"
							sortProperty="description" sortable="true"
							id="description" noWrap="true" headerNoWrap="true">
							<af:panelHorizontal valign="middle" halign="left">
								<af:inputText maximumLength="30" label="Description"
									readOnly="true" value="#{fixedCharge.description}"
									columns="30">
								</af:inputText>
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Comment" formatType="text"  width="70%"
							sortProperty="noteTxt" sortable="true" id="noteTxt">
							<af:panelHorizontal valign="middle" halign="left">
								<af:inputText columns="50" rows="1" maximumLength="50"
									label="Comment" readOnly="true"
									value="#{fixedCharge.noteTxt}" />
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Amount" width="10%" sortProperty="amountString"
							sortable="true" formatType="number"
							id="amountString" noWrap="true" headerNoWrap="true">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText label="Amount :" readOnly="true"
									value="#{fixedCharge.amountString}">
								</af:inputText>
							</af:panelHorizontal>
						</af:column>
						
						<af:column headerText="Invoiced" sortProperty="invoicedValue" width="10%"
							sortable="true" id="invoicedValue" rendered="#{infraDefs.timesheetEntryEnabled}">
								<af:panelHorizontal valign="middle" halign="center">
									<af:selectBooleanCheckbox readOnly="true" 
										value="#{fixedCharge.invoicedValue}"/>
								</af:panelHorizontal>
						</af:column>

						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
									<af:commandButton text="Add" id="addFixedCharge"
										action="#{permitDetail.startToAddNSRFixedCharge}"
										useWindow="true" windowWidth="900" windowHeight="450"
										inlineStyle="padding-left:5px;"
										returnListener="#{permitDetail.dialogDone}" 
										disabled="#{!permitDetail.NSRFeeSummaryEditAllowed}"/>
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelGroup>
							</afh:rowLayout>
						</f:facet>
					</af:table>
					<%
                  /* Fixed Charge list end */
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


