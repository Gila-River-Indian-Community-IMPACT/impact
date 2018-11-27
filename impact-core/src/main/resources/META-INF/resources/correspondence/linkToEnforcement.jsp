<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document id="body" title="Link To Discovery Source">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<af:panelForm>
				<afh:rowLayout halign="center">
					<h:panelGrid border="1" width="875">
						<af:showDetailHeader text="Enforcements" disclosed="true" >
							<af:table var="enf" bandingInterval="1" banding="row" width="100%"
								value="#{correspondenceDetail.linkedToObjs}"
								rows="#{enforcementActionDetail.pageLimit}" >
								<af:column rendered="true" formatType="text" headerText="Enforcement ID"
  									sortable="true" sortProperty="enforcementActionId" >
    								<af:commandLink text="#{enf.enforcementActionId}"
											actionListener="#{correspondenceDetail.updateLinkToObj}" >
											<t:updateActionListener
												property="#{correspondenceDetail.enforcementActionId}" 
												value="#{enf.enforcementActionId}" />
    								</af:commandLink>
    								</af:column>
  									
  									<af:column sortable="true" sortProperty="createdDate"
    									formatType="text" headerText="Date Enforcement Action Created">
      									<af:selectInputDate readOnly="true" value="#{enf.createdDate}" />      
  									</af:column>
								<f:facet name="footer">
									<afh:rowLayout halign="center" >
										<af:panelButtonBar>
											<af:commandButton
												actionListener="#{tableExporter.printTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Printable view" />
											<af:commandButton
												actionListener="#{tableExporter.excelTable}"
												onclick="#{tableExporter.onClickScript}"
												text="Export to excel" />
										</af:panelButtonBar>
									</afh:rowLayout>
								</f:facet>
							</af:table>
						</af:showDetailHeader>
					</h:panelGrid>
				</afh:rowLayout>

				<f:facet name="footer">
					<af:panelButtonBar>
						<af:commandButton text="Cancel" immediate="true"
							actionListener="#{correspondenceDetail.cancelLinkToObj}" />
					</af:panelButtonBar>
				</f:facet>
			</af:panelForm>
		</af:form>
	</af:document>
</f:view>