<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document title="Documents">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="Documents">
				<f:facet name="messages">
					<af:messages />
				</f:facet>
				<f:facet name="branding">
					<af:objectImage source="/images/stars2.png" />
				</f:facet>
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" />
				</f:facet>
				<af:panelGroup layout="vertical">
					<af:panelForm>
						<af:inputText columns="50" label="Text to find"
							value="#{documentSearch.fullTextQuery}" />
						<af:panelLabelAndMessage rendered="true">
							<af:commandButton text="Submit" action="#{documentSearch.search}" />
							<af:commandButton text="Reset" action="#{documentSearch.reset}" />
						</af:panelLabelAndMessage>
						<f:facet name="footer">
						</f:facet>
					</af:panelForm>
				</af:panelGroup>

				<af:objectSpacer width="100%" height="15" />

				<afh:rowLayout halign="center"
					rendered="#{documentSearch.hasSearchResults}">
					<af:table emptyText=" " value="#{documentSearch.documents}"
						var="doc">
						<f:facet name="header">
							<af:outputText value="Documents" />
						</f:facet>

						<af:column sortable="false" headerText="">
							<f:facet name="header">
								<af:outputText value="Document ID" />
							</f:facet>
							<af:goLink destination="#{doc.docURL}" text="#{doc.description}" />
						</af:column>

						<af:column sortable="false" headerText="">
							<f:facet name="header">
								<af:outputText value="Path" />
							</f:facet>
							<af:outputText value="#{doc.path}" />
						</af:column>
						<af:column sortable="false" headerText="">
							<f:facet name="header">
								<af:outputText value="Description" />
							</f:facet>
							<af:outputText value="#{doc.description}" />
						</af:column>
					</af:table>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
