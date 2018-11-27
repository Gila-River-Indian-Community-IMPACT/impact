<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>

<f:view>
	<af:document id="body" title="Stars2 NTV Report Add/Edit Contact">
		    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
			<af:messages />

			<af:panelForm>

				<f:subview id="contact">
					<af:iterator value="#{erNTVDetail}" var="facilityProfile" id="v">
						<%@ include file="../facilities/comContact1.jsp"%>
					</af:iterator>
				</f:subview>

				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Save"
							action="#{erNTVDetail.saveEditContact}" />
						<af:commandButton text="Cancel" immediate="true"
							action="#{erNTVDetail.cancelAddContact}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>
