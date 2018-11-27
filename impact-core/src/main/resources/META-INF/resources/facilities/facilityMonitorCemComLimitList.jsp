<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup layout="vertical" rendered="true">
	<af:showDetailHeader text="Limits Measured by the Monitor"
		disclosed="true">

		<%
            /* Content begin */
            %>
		<h:panelGrid columns="1" width="885px">
			<af:panelGroup>

				<afh:rowLayout halign="center">
					<af:table value="#{facilityProfile.facilityCemComLimitsWrapper}"
						binding="#{facilityProfile.facilityCemComLimitsWrapper.table}"
						bandingInterval="1" banding="row" id="facilityCemComLimitTab"
						var="facilityCemComLimit"
						rows="#{facilityProfile.pageLimit}" emptyText=" ">
						
						<af:column id="edit" formatType="text" headerText="Limit ID"
							noWrap="true" sortProperty="limId" sortable="true" width="50px">
							<af:commandLink useWindow="true" windowWidth="700"
								windowHeight="600" rendered="#{!facilityProfile.publicApp}"
								returnListener="#{facilityProfile.dialogDone}"
								action="#{facilityProfile.startToEditFacilityCemComLimit}">
								<af:inputText value="#{facilityCemComLimit.limId}"
									readOnly="true" valign="middle">
								</af:inputText>
							</af:commandLink>
							<af:outputText value="#{facilityCemComLimit.limId}" 
								rendered="#{facilityProfile.publicApp}" >
							</af:outputText>
						</af:column>

						<jsp:include flush="true" page="facilityCemComLimitListColumns.jsp" />

						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelGroup layout="horizontal">
									<af:commandButton text="Add Limit" id="addFacilityCemComLimit"
										action="#{facilityProfile.startToAddFacilityCemComLimit}"
										useWindow="true" windowWidth="700" windowHeight="600"
										inlineStyle="padding-left:5px;"
										returnListener="#{facilityProfile.dialogDone}"
										rendered="#{facilityProfile.dapcUser}"
										disabled="#{!facilityProfile.facilityCemComLimitEditAllowed || facilityProfile.disabledUpdateButton}" />
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
								/* Facility CEM COM Limit list end */
							%>
				</afh:rowLayout>



				<af:objectSpacer height="10" />

			</af:panelGroup>
		</h:panelGrid>
		<%
            /* Content end */
        %>

	</af:showDetailHeader>
	
	<%-- hidden controls for navigation to compliance report from popup --%>
	<%@ include file="../util/hiddenControls.jsp"%>

</af:panelGroup>


