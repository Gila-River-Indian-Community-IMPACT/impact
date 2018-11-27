<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Confirm Contact Merge">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<f:subview id="merge">
				<afh:rowLayout halign="center">
					<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
						<afh:rowLayout halign="center">
							<af:panelHeader messageType="Information"
								text="Confirm Contact Merge" />
						</afh:rowLayout>
					</afh:tableLayout>
				</afh:rowLayout>
				<af:objectSpacer height="10" />
				<afh:rowLayout halign="center">
					<af:panelForm>
						<af:table value="#{mergeContact.selectedTargetContactsWrapper}"
							binding="#{mergeContact.selectedTargetContactsWrapper.table}"
							bandingInterval="1" banding="row" var="contact"
							rows="#{mergeContact.pageLimit}">

							<af:column sortProperty="c01" sortable="true" noWrap="true"
								formatType="text" headerText="Contact ID">
								<af:outputText value="#{contact.cntId}" />
							</af:column>

							<af:column sortProperty="c02" sortable="true" formatType="text"
								headerText="Last Name">
								<af:outputText value="#{contact.lastNm}" />
							</af:column>

							<af:column sortProperty="c03" sortable="true" formatType="text"
								headerText="First Name">
								<af:outputText value="#{contact.firstNm}" />
							</af:column>

							<af:column sortProperty="c04" sortable="true" formatType="text"
								headerText="Preferred Name">
								<af:outputText value="#{contact.preferredName}" />
							</af:column>

							<af:column sortProperty="c05" sortable="true" formatType="text"
								headerText="Phone">
								<af:outputText value="#{contact.phoneNo}"
									converter="#{infraDefs.phoneNumberConverter}" />
							</af:column>

							<af:column sortProperty="c06" sortable="true" formatType="text"
								headerText="Email">
								<af:goLink text="#{contact.emailAddressTxt}"
									destination="mailto:#{contact.emailAddressTxt}" />
							</af:column>
							<af:column sortProperty="c07" sortable="true" formatType="text"
								headerText="Company ID">
								<af:outputText value="#{contact.cmpId}" />
							</af:column>
							
							<af:column sortProperty="c08" sortable="true" formatType="text"
								headerText="Company Name" width="400">
								<af:outputText value="#{contact.companyName}" />
							</af:column>

							<af:column sortProperty="c09" sortable="true" formatType="text"
								headerText="CROMERR Username">
								<af:outputText value="#{contact.externalUser.userName}" />
							</af:column>

							<af:column sortProperty="c10" sortable="true" formatType="icon"
								headerText="Active">
								<af:selectBooleanCheckbox value="#{contact.active}"
									readOnly="true" />
							</af:column>

							<f:facet name="footer">
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton actionListener="#{tableExporter.printTable}"
											onclick="#{tableExporter.onClickScript}"
											text="Printable view" />
										<af:commandButton actionListener="#{tableExporter.excelTable}"
											onclick="#{tableExporter.onClickScript}"
											text="Export to excel" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</f:facet>
						</af:table>
					</af:panelForm>
				</afh:rowLayout>
				<af:objectSpacer height="20" />

				<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
					<afh:rowLayout halign="center">
						<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
							value="Caution: the following contacts will have their contact types, permit application associations, and notes merged with the previously chosen base contact. Any duplicate contact types will be discarded. These contacts will also be marked as inactive." />
					</afh:rowLayout>
				</afh:tableLayout>
				<af:objectSpacer height="10" />
				<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
					<afh:rowLayout halign="center">
						<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
							value="Are you sure you would like to continue?" />
					</afh:rowLayout>
				</afh:tableLayout>
				<af:objectSpacer height="10" />
				<af:panelForm>
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Yes"
								action="#{mergeContact.mergeContacts}" />
							<af:commandButton text="No" action="#{mergeContact.cancelMerge}"
								immediate="true" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>

			</f:subview>
		</af:form>
	</af:document>
</f:view>