<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="NTV Input Invoice Date">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page>
				<af:messages />

				<af:outputText value="#{reportDetail.invoiceDtMsg}" />
				<af:objectSpacer height="15" />
				<af:panelForm rows="1" maxColumns="2" labelWidth="30%" width="400">
					<af:selectInputDate label="Invoice Date:"
						value="#{reportDetail.invoiceDt}" showRequired="true" >
						<af:validateDateTimeRange minimum="#{reportDetail.minInvoiceDate}"
							maximum="#{reportDetail.maxInvoiceDate}" />
					</af:selectInputDate>
					<af:panelButtonBar>
						<af:commandButton text="Post/ Adjust"
							action="#{reportDetail.doAggregatePost}"
							rendered="#{reportDetail.postState}" />
						<!-- af:commandButton text="Print"
							action="#{reportDetail.doAggregatePrint}"
							rendered="#{!reportDetail.postState}" -->
					</af:panelButtonBar>
				</af:panelForm>


			</af:page>
		</af:form>
	</af:document>
</f:view>
