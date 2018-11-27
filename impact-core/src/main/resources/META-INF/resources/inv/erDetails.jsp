<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:switcher facetName="#{invoiceDetail.reportType}">
	<f:facet name="tv">
		<f:subview id="tv">			
				<af:table
					value="#{invoiceDetail.invoice.compInvoice.invoiceDetails}"
					banding="row" rendered="#{invoiceDetail.invoice.adjustedInvoice}"
					var="emissions" width="980">
					<jsp:include page="invTVDetail.jsp" />
				</af:table>
				<af:table value="#{invoiceDetail.invoice.invoiceDetails}"
					banding="row" rendered="#{!invoiceDetail.invoice.adjustedInvoice}"
					var="emissions" width="980">
					<jsp:include page="invTVDetail.jsp" />
				</af:table>			
		</f:subview>
	</f:facet>
	<f:facet name="smtv">
		<f:subview id="smtv">
			<af:table value="#{invoiceDetail.emissionRanges}" banding="row" var="emissions" width="980">
				<af:column>
					<af:column headerText="Emissions"	formatType="text">
						<af:outputText value="#{emissions.name}" />
					</af:column>				
					<af:column headerText="Amount" formatType="number">
						<af:inputText value="#{emissions.value}" readOnly="true">
							<af:convertNumber type='currency' locale="en-US"
		             				minFractionDigits="2" />
						</af:inputText>
					</af:column>
				</af:column>		
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>	
				</f:facet>		
			</af:table>	
			
		</f:subview>
	</f:facet>
	<f:facet name="ntv">
		<f:subview id="ntv">	
			<af:table value="#{invoiceDetail.emissionRanges}" banding="row" var="emissions" width="980">
				<af:column>
					<af:column headerText="Year" formatType="text">
						<af:outputText value="#{emissions.name}"/>
					</af:column>
					<af:column headerText="Amount" formatType="number">
						<af:inputText value="#{emissions.value}" readOnly="true">			
							<af:convertNumber type='currency' locale="en-US"
		             				minFractionDigits="2" />
						</af:inputText>
					</af:column>
				</af:column>
				<f:facet name="footer">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
							<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
						</af:panelButtonBar>
					</afh:rowLayout>	
				</f:facet>		
			</af:table>
					
		</f:subview>
	</f:facet>
</af:switcher>
