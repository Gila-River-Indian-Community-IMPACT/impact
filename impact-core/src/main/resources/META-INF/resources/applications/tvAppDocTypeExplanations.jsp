<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document title="Attachment Type Explanations">
		<af:page title="Attachment Type Explanations:">
			<af:table
				value="#{applicationReference.tvApplicationDocumentTypeWrapper}"
				binding="#{applicationReference.tvApplicationDocumentTypeWrapper.table}"
				bandingInterval="1" banding="row" var="appDocType">
				<af:column formatType="text" headerText="Attachment Type"
					inlineStyle="padding:5px;" width="160px">
					<af:inputText value="#{appDocType.description}" readOnly="true"
						inlineStyle="padding:5px;" />
				</af:column>
				<af:column sortable="false" formatType="text"
					inlineStyle="padding:5px;" headerText="Explanation">
					<af:inputText value="#{appDocType.typeExp}" readOnly="true"
						inlineStyle="padding:5px;" />
				</af:column>
			</af:table>
			<af:form>
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Close"
							action="#{applicationDetail.closeDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:form>
		</af:page>
	</af:document>
</f:view>