<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="ISTV Facilities">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page>
				<%@ include file="../util/branding.jsp"%>
				<af:objectSpacer height="6" />
				
				<afh:rowLayout halign="center">
					<af:outputFormatted value="<b> ISTV Facilities </b>" />
				</afh:rowLayout>
				<af:objectSpacer height="5" />
				<afh:rowLayout halign="center">
					<af:panelGroup layout="vertical">
						<af:table value="#{istvInvoiceTables}" banding="row" var="invoice" rows="15"
								width="760">
								<af:column headerText="Facility ID" sortable="true"
									sortProperty="facilityId" formatType="text">
									<af:panelHorizontal valign="middle" halign="left">
										<af:inputText readOnly="true" value="#{invoice.facilityId}"
											inlineStyle="white-space: nowrap;" />
									</af:panelHorizontal>
								</af:column>
								<af:column headerText="Facility Name" sortable="true"
									sortProperty="facilityName">
									<af:panelHorizontal valign="middle" halign="left">
										<af:inputText readOnly="true" value="#{invoice.facilityName}" />
									</af:panelHorizontal>
								</af:column>
								<af:column headerText="Invoice ID" sortable="true"
									sortProperty="invoiceId" formatType="number">
									<af:panelHorizontal valign="middle" halign="left">
										<af:inputText readOnly="true" value="#{invoice.invoiceId}" />
									</af:panelHorizontal>
								</af:column>
								<af:column headerText="Invoice Amount" sortable="true"
									sortProperty="origAmount" formatType="number">
									<af:panelHorizontal valign="middle" halign="right">
										<af:inputText readOnly="true" value="#{invoice.origAmount}">
											<af:convertNumber type='currency' locale="en-US"
												minFractionDigits="2" />
										</af:inputText>
									</af:panelHorizontal>
								</af:column>
								<af:column headerText="Revenue ID" sortable="true"
									sortProperty="revenueId" formatType="number">
									<af:panelHorizontal valign="middle" halign="left">
										<af:inputText readOnly="true" value="#{invoice.revenueId}" />
									</af:panelHorizontal>
								</af:column>

								<f:facet name="footer">
									<afh:rowLayout halign="center">
										<af:panelButtonBar>
											<af:commandButton
												actionListener="#{tableExporter.printTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Printable view" />
											<af:commandButton
												actionListener="#{tableExporter.excelTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Export to excel" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</f:facet>

							</af:table>
							<af:objectSpacer height="15" />
						
						<af:commandButton text="Close" onclick="window.close()" />
					</af:panelGroup>
				</afh:rowLayout>

			</af:page>
		</af:form>
	</af:document>
</f:view>
