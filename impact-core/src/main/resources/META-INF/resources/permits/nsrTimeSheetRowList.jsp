<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" rendered="true">
	<af:showDetailHeader text="Time Card Information" disclosed="true">

		<%
            /* Content begin */
            %>
        <af:outputFormatted
	        rendered="#{!permitDetail.permit.timeCardInfoRetrieved}"
	        inlineStyle="color: orange; font-weight: bold;"
	        value="<b>** Failed to retrieve time card information from AQDS. Please contact AQDS Administrator ** </b>" />	  
		<h:panelGrid columns="1" border="1"
			width="#{permitDetail.permitWidth}">
			<af:panelGroup>
				<afh:rowLayout halign="center">
					<af:table value="#{permitDetail.NSRTimeSheetRowWrapper}"
						binding="#{permitDetail.NSRTimeSheetRowWrapper.table}"
						bandingInterval="1" banding="row" id="chargePaymentTab"
						width="#{permitDetail.permitTableWidth}" var="nsrTimeSheetRow"
						rows="#{permitDetail.pageLimit}" emptyText=" ">

						<af:column headerText="ID" sortable="true" width="5%"
							rendered="#{!infraDefs.timesheetEntryEnabled}"
							sortProperty="hoursEntryId" formatType="text" id="hoursEntryId">
							<af:panelHorizontal valign="middle" halign="left">
								<af:inputText columns="50" rows="1" maximumLength="50"
									label="ID" readOnly="true" value="#{nsrTimeSheetRow.hoursEntryId}" />
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Date" formatType="text" width="5%"
							sortProperty="date" sortable="true"
							id="date">
							<af:panelHorizontal valign="middle" halign="left">
								<af:selectInputDate label="Date" readOnly="true" valign="middle"
									required="true" value="#{nsrTimeSheetRow.date}">
									<af:validateDateTimeRange minimum="1900-01-01" />
								</af:selectInputDate>
							</af:panelHorizontal>
						</af:column>
						
						<af:column headerText="AppNum" formatType="text"
							sortProperty="appNum" sortable="true" id="appNumber" noWrap="true">
							<af:panelHorizontal valign="middle" halign="left">
								<af:inputText maximumLength="50" label="AppNumber"
									readOnly="true" value="#{nsrTimeSheetRow.appNumber}" />
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Engineer" formatType="text"
							sortProperty="engineer" sortable="true" id="engineer" noWrap="true">
							<af:panelHorizontal valign="middle" halign="left">
								<af:inputText maximumLength="50" label="Engineer"
									readOnly="true" value="#{nsrTimeSheetRow.employeeName}" />
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Hours" sortProperty="hours" width="5%"
							sortable="true" formatType="number" id="hours">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText label="Hours :" readOnly="true"
									value="#{nsrTimeSheetRow.hours}">
									<af:convertNumber type="number" maxFractionDigits="2"
										minFractionDigits="2" />
								</af:inputText>
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Comment" width="50%" formatType="text"
							sortProperty="comments" sortable="true" id="comments">
							<af:panelHorizontal valign="middle" halign="left">
								<af:inputText columns="50" rows="1" maximumLength="50"
									label="Comment" readOnly="true" value="#{nsrTimeSheetRow.comments}" />
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Hourly Rate" sortProperty="hourlyRate" width="8%"
							sortable="true" formatType="number" id="hourlyRate" headerNoWrap="true">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText label="Hourly Rate :" readOnly="true"
									value="#{nsrTimeSheetRow.hourlyRate}">
									<af:convertNumber type='currency' locale="en-US"
										minFractionDigits="2" />
								</af:inputText>
							</af:panelHorizontal>
						</af:column>

						<af:column headerText="Amount" sortProperty="amount" width="8%"
							sortable="true" formatType="number"
							id="amount">
							<af:panelHorizontal valign="middle" halign="right">
								<af:inputText label="Amount :" readOnly="true"
									value="#{nsrTimeSheetRow.amount}">
									<af:convertNumber type='currency' locale="en-US"
										minFractionDigits="2" />
								</af:inputText>
							</af:panelHorizontal>
						</af:column>
						
						<af:column headerText="Invoiced" sortProperty="invoiced" width="8%"
							sortable="true" id="invoiced" rendered="#{infraDefs.timesheetEntryEnabled}">
							<af:panelHorizontal valign="middle" halign="center">
								<af:selectBooleanCheckbox value="#{nsrTimeSheetRow.invoiced}" 
									readOnly="true"/>
							</af:panelHorizontal>
						</af:column>

						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
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


