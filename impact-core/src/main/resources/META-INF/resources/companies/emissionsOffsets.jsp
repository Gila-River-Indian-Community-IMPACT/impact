<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document title="Offset Tracking">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:page var="foo" value="#{menuModel.model}" id="companyOffsetTracking" title="Offset Tracking">
				<af:inputHidden value="#{companyProfile.popupRedirect}"
						rendered="#{companyProfile.dapcUser}" />
                <jsp:include page="header.jsp" />
				<h:panelGrid border="1">
					<af:panelBorder>
						<f:facet name="top">
							<f:subview id="companyDetailTop">
								<jsp:include page="companyHeader.jsp" />
							</f:subview>
						</f:facet>
						<f:subview id="emissions_offset">
							<af:forEach 
								items="#{companyProfile.areaEmissionsOffsetList}" var="areaEmissionsOffset">
								<%@ include file="emissionOffsetsList.jsp"%>
							</af:forEach>
						</f:subview>
						
						<f:subview id="offset_adjustments">
							<%@ include file="offsetAdjustmentList.jsp"%>
						</f:subview>
					</af:panelBorder>
				</h:panelGrid>
			</af:page>
		</af:form>
	</af:document>
</f:view>