<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Adjustment to Revenues">
		    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
			<af:page>
				<af:messages />

				<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
					value="<b>Note: To Decrease current amount enter a negative number.</b>" />
				<af:panelGroup layout="vertical">

					<af:objectSpacer height="40" />

					<afh:rowLayout halign="center">
						<af:panelForm rows="4" maxColumns="2" fieldWidth="200">
							<af:selectOneChoice label="Adjustment Type:"
								unselectedLabel="Please select"
								value="#{invoiceDetail.adjustmentType}">
								<mu:selectItems value="#{invoiceDetail.adjustmentTypes}" />
							</af:selectOneChoice>

							<af:inputText label="Amount:" id="adjustmentAmount"
								showRequired="true" value="#{invoiceDetail.adjustmentAmount}"
								columns="20" />

							<af:inputText label="Document Id:" id="documentId"
								showRequired="true" columns="20"
								value="#{invoiceDetail.adjustmentDocID}"/>

							<af:inputText label="Reason:" id="reason" showRequired="true"
								value="#{invoiceDetail.reason}" rows="5" columns="40"
								maximumLength="2000" />

						</af:panelForm>
					</afh:rowLayout>
					<af:objectSpacer height="40" />
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Post Adjustment"
								action="#{invoiceDetail.postAdjustment}" />
							<af:commandButton text="Cancel"
								action="#{invoiceDetail.closeDialog}" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelGroup>
			</af:page>
		</af:form>
	</af:document>
</f:view>
