<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

<h:panelGrid border="1">
	<af:panelBorder>

		<f:facet name="top">
			<f:subview id="facilityHeader">
				<jsp:include page="comFacilityHeader.jsp" />
			</f:subview>
		</f:facet>

		<f:facet name="right">
			<h:panelGrid columns="1" border="1"
				style="margin-left:auto;margin-right:auto;">
				<af:panelGroup layout="vertical">
					<af:objectSpacer height="10" />
					<afh:rowLayout halign="center">
						<h:panelGrid border="1">
							<jsp:include flush="true" page="facilityCemComLimitList.jsp" />
						</h:panelGrid>
					</afh:rowLayout>

				</af:panelGroup>
			</h:panelGrid>
		</f:facet>

	</af:panelBorder>
</h:panelGrid>