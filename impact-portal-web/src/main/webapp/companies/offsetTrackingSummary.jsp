<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Offset Tracking Summary">
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				id="companyOffsetTrackingSummary" title="Offset Tracking Summary">
				<h:panelGrid border="1">
					<af:panelBorder>
						<f:subview id="emissions_offset">
							<af:forEach items="#{companyProfile.areaEmissionsOffsetList}"
								var="areaEmissionsOffset">
								<%@ include file="emissionOffsetsList.jsp"%>
							</af:forEach>
						</f:subview>

						<f:subview id="offset_adjustments">
							<%@ include file="offsetAdjustmentList.jsp"%>
						</f:subview>

						<af:objectSpacer height="20" />

						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton text="Close"
									action="#{companyProfile.closeDialog}" />
							</af:panelButtonBar>
						</afh:rowLayout>
						
					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>