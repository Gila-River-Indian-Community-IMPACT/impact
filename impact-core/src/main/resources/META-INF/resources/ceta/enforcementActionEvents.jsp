<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" rendered="true">
	<af:showDetailHeader text="Tracking Dates" disclosed="true"
		rendered="#{enforcementActionDetail.enforcementAction.enforcementActionId != null}">
		<%
            /* Content begin */
            %>
		<h:panelGrid columns="1"
			width="98%">
			<af:panelGroup>
				<afh:rowLayout halign="center">
					<af:table value="#{enforcementActionDetail.enforcementActionEventWrapper}"
						binding="#{enforcementActionDetail.enforcementActionEventWrapper.table}"
						bandingInterval="1" banding="row" id="enforcementActionEventTab"
						width="#{enforcementActionDetail.EATableWidth}" var="enforcementActionEvent"
						rows="#{enforcementActionDetail.pageLimit}" emptyText=" " varStatus="enforcementActionEventTableVs">

						<af:column id="edit" formatType="text" headerText="Row Id" width="5%"
							noWrap="true">
								<af:commandLink useWindow="true" windowWidth="600"
									windowHeight="250" inlineStyle="padding-left:5px;"
									returnListener="#{enforcementActionDetail.dialogDone}"
									action="#{enforcementActionDetail.startToEditEnforcementActionEvent}">
									<af:inputText value="#{enforcementActionEventTableVs.index+1}"
										readOnly="true" valign="middle">
										<af:convertNumber pattern="000" />
									</af:inputText>
								</af:commandLink>
						</af:column>
						
						<af:column formatType="text"
							headerText="Event" sortProperty="eventDesc"
							sortable="true" id="eventCd" width="60%">
								<af:selectOneChoice label="Enforcement Action Event" readOnly="true"
									value="#{enforcementActionEvent.eventCd}" unselectedLabel="">
									<mu:selectItems id="enforcementActionEvents"
										value="#{enforcementActionDetail.enforcementActionEventDefs}" />
								</af:selectOneChoice>
						</af:column>

						<af:column headerText="Date" formatType="text" width="5%"
							sortProperty="eventDate" sortable="true"
							id="eventDate">
								<af:selectInputDate label="Date" readOnly="true" valign="middle"
									required="true" value="#{enforcementActionEvent.eventDate}">
									<af:validateDateTimeRange minimum="1900-01-01" />
								</af:selectInputDate>
						</af:column>
						
						<af:column sortable="true" sortProperty="addedBy"
        					formatType="text" width="30%" headerText="Added By">
        					<af:selectOneChoice readOnly="true"
          					value="#{enforcementActionEvent.addedBy}">
          						<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
        					</af:selectOneChoice>
      					</af:column>
						

						

						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
									<af:commandButton text="Add Event" id="addEnforcementActionEvent"
										action="#{enforcementActionDetail.startToAddEnforcementActionEvent}"
										useWindow="true" windowWidth="600" windowHeight="250"
										inlineStyle="padding-left:5px;"
										returnListener="#{enforcementActionDetail.dialogDone}" 
										disabled="#{!enforcementActionDetail.enforcementActionEditAllowed}"/>
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
                  /* Enforcement Action Event list end */
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


