<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
	<h:panelGrid border="1"
		rendered="#{facilityRequestSearch.hasSearchResults}">
		<af:panelBorder>
			<af:showDetailHeader text="Facility Request List" disclosed="true">

				<af:table value="#{facilityRequestSearch.resultsWrapper}"
					binding="#{facilityRequestSearch.resultsWrapper.table}"
					bandingInterval="1" banding="row" var="request"
					rows="#{facilityRequestSearch.pageLimit}">

					<af:column sortProperty="reqId" sortable="true" noWrap="true"
						formatType="text" headerText="Request ID">
						<af:commandLink
							action="#{newFacilityRequest.submitFacilityRequest}"
							text="#{request.reqId}">
							<t:updateActionListener
								property="#{newFacilityRequest.requestId}"
								value="#{request.requestId}" />
							<t:updateActionListener
								property="#{menuItem_facilityCreationRequestDetail.disabled}" value="false" />
						</af:commandLink>
					</af:column>
            
					<af:column sortProperty="name" sortable="true" formatType="text"
						headerText="Facility Name">
						<af:outputText value="#{request.name}" />
					</af:column>

					<af:column sortProperty="cmpId" sortable="true" noWrap="true"
						formatType="text" headerText="Company ID">
						<af:commandLink action="#{companyProfile.submitProfile}"
							text="#{request.cmpId}">
							<t:updateActionListener property="#{companyProfile.cmpId}"
								value="#{request.cmpId}" />
							<t:updateActionListener
								property="#{menuItem_companyProfile.disabled}" value="false" />
						</af:commandLink>
					</af:column>

					<af:column sortProperty="companyName" sortable="true" noWrap="true"
						formatType="text" headerText="Company Name">
						<af:outputText value="#{request.companyName}" />
					</af:column>

					<af:column sortProperty="truncatedMemo" sortable="true"
						formatType="text" headerText="Memo">
						<af:outputText
							value="#{request.truncatedMemo}" />
					</af:column>
					
					<af:column sortProperty="cntId" sortable="true" noWrap="true"
						formatType="text" headerText="Contact ID">
						<af:commandLink action="#{contactDetail.submitDetail}"
							text="#{request.cntId}">
							<t:updateActionListener property="#{contactDetail.contactId}"
								value="#{request.contactId}" />
							<t:updateActionListener
								property="#{menuItem_contactDetail.disabled}" value="false" />
						</af:commandLink>
					</af:column>
					
					<af:column sortProperty="lastNm" sortable="true" formatType="text"
						headerText="Last Name">
						<af:outputText value="#{request.lastNm}" />
					</af:column>

					<af:column sortProperty="firstNm" sortable="true" formatType="text"
						headerText="First Name">
						<af:outputText value="#{request.firstNm}" />
					</af:column>
					
					<af:column sortProperty="externalUsername" sortable="true" formatType="text"
						headerText="CROMERR Username">
						<af:outputText value="#{request.externalUsername}" />
					</af:column>

					<af:column sortProperty="operatingStatusCd" sortable="true" formatType="text"
						headerText="Operating">
						<af:outputText
							value="#{facilityReference.operatingStatusDefs.itemDesc[(empty request.operatingStatusCd ? '' : request.operatingStatusCd)]}" />
					</af:column>

					<af:column sortProperty="facilityTypeDsc" sortable="true" formatType="text"
						headerText="Facility Type">
						<af:outputText
							value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty request.facilityTypeCd ? '' : request.facilityTypeCd)]}" />
					</af:column>
					
					<af:column sortProperty="countyNm" sortable="true" formatType="text"
						headerText="County">
						<af:selectOneChoice value="#{request.countyCd}" readOnly="true">
							<f:selectItems value="#{infraDefs.counties}" />
						</af:selectOneChoice>
					</af:column>

					<af:column sortable="true" sortProperty="submitDate"
						formatType="icon" headerText="Date Submitted">
						<af:selectInputDate readOnly="true" value="#{request.submitDate}" />
					</af:column>
					
					<af:column sortProperty="requestStatusCd" sortable="true" formatType="text"
						headerText="Request State">
						<af:outputText
							value="#{facilityReference.facilityRequestStatusDefs.itemDesc[(empty request.requestStatusCd ? '' : request.requestStatusCd)]}" />
					</af:column>

					<f:facet name="footer">
						<afh:rowLayout halign="center">
							<af:panelButtonBar>
								<af:commandButton actionListener="#{tableExporter.printTable}"
									onclick="#{tableExporter.onClickScript}" text="Printable view" />
								<af:commandButton actionListener="#{tableExporter.excelTable}"
									onclick="#{tableExporter.onClickScript}" text="Export to excel" />
							</af:panelButtonBar>
						</afh:rowLayout>
					</f:facet>
				</af:table>

			</af:showDetailHeader>
		</af:panelBorder>
	</h:panelGrid>
</afh:rowLayout>
