<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<h:panelGrid border="1">
	<af:panelBorder>

		<f:facet name="top">
			<f:subview id="facilityHeader">
				<jsp:include page="comFacilityHeader2.jsp" />
			</f:subview>
		</f:facet>

		<f:facet name="right">
			<h:panelGrid columns="1" border="1" width="800px"
				style="margin-left:auto;margin-right:auto;">
				<af:panelGroup layout="vertical">
					<af:objectSpacer height="10" />

					<af:table value="#{facilityProfile.eventLog}" bandingInterval="1"
						banding="row" var="els" width="98%">
						<af:column sortProperty="date" noWrap="true" sortable="true"
							formatType="text" headerText="Date" width="40px">
							<af:selectInputDate value="#{els.date}" readOnly="true">
								<f:convertDateTime dateStyle="full"
									pattern="MM/dd/yyyy HH:mm:ss" />
							</af:selectInputDate>
						</af:column>
						<af:column sortProperty="userId" sortable="true" formatType="text"
							headerText="Staff" width="40px" noWrap="true">
							<af:selectOneChoice readOnly="true" value="#{els.userId}">
								<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
							</af:selectOneChoice>
						</af:column>
						<af:column sortProperty="eventTypeDefCd" sortable="true"
							formatType="text" headerText="Event Type" width="100px">
							<af:selectOneChoice label="Event Type" readOnly="true"
								value="#{els.eventTypeDefCd}">
								<f:selectItems value="#{eventLog.eventType}" />
							</af:selectOneChoice>
						</af:column>
						<af:column sortProperty="note" sortable="true" formatType="text"
							headerText="Note">
							<af:inputText readOnly="true" columns="40" value="#{els.note}" />
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="New Event" useWindow="true"
										windowWidth="500" windowHeight="500"
										disabled="#{facilityProfile.readOnlyUser}"
										returnListener="#{facilityProfile.dialogDone}"
										rendered="#{facilityProfile.dapcUser}"
										action="#{facilityProfile.createNewEventLog}" />
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScriptTS}"
										text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScriptTS}"
										text="Export to excel" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</af:panelGroup>
			</h:panelGrid>
		</f:facet>

	</af:panelBorder>
</h:panelGrid>