<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Facility Exclusion List">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true">
			<af:messages />
			<f:subview id="fal">
				<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
					<afh:rowLayout halign="center">
						<af:panelHeader messageType="Information"
							text="Facility Exclusion List" />
					</afh:rowLayout>
				</afh:tableLayout>
				<af:panelForm>
					<af:table
						value="#{contactDetail.companyFacilityExclusionListWrapper}"
						binding="#{contactDetail.companyFacilityExclusionListWrapper.table}"
						rows="#{contactDetail.pageLimit}" bandingInterval="1"
						banding="row" var="facility" partialTriggers="authCompany"
						width="98%">

						<f:facet name="header">
							<afh:rowLayout halign="left">
								<af:panelButtonBar>
									<af:commandButton
										disabled="#{!contactDetail.facilityExclusionListEditable}"
										action="#{contactDetail.selectAllFacilities}"
										text="Exclude All" />
								</af:panelButtonBar>
								<af:objectSpacer height="1" width="6" />
								<af:panelButtonBar>
									<af:commandButton
										disabled="#{!contactDetail.facilityExclusionListEditable}"
										action="#{contactDetail.selectNoneFacilities}"
										text="Exclude None" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>

						<af:column sortable="true" sortProperty="selected"
							formatType="icon" headerText="Exclude">
							<af:selectBooleanCheckbox label="Exclude"
								value="#{facility.selected}" rendered="true"
								disabled="#{!contactDetail.facilityExclusionListEditable}">
								<f:selectItems value="#{facility.selected}" />
							</af:selectBooleanCheckbox>
						</af:column>

						<af:column sortProperty="facilityId" sortable="true" noWrap="true"
							formatType="text" headerText="Facility ID">
							<af:commandLink action="#{facilityProfile.submitProfile}"
								text="#{facility.facilityId}">
								<t:updateActionListener property="#{facilityProfile.fpId}"
									value="#{facility.fpId}" />
								<t:updateActionListener
									property="#{menuItem_facProfile.disabled}" value="false" />
							</af:commandLink>
						</af:column>

						<af:column sortProperty="name" sortable="true" formatType="text"
							headerText="Facility Name">
							<af:outputText value="#{facility.name}" />
						</af:column>

						<af:column sortProperty="companyName" sortable="true"
							formatType="text" headerText="Company Name">
							<af:outputText value="#{facility.companyName}" />
						</af:column>

						<af:column sortProperty="operatingStatusCd" sortable="true"
							formatType="text" headerText="Operating">
							<af:outputText
								value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facility.operatingStatusCd ? '' : facility.operatingStatusCd)]}" />
						</af:column>

						<af:column sortProperty="permitClassCd" sortable="true"
							formatType="text" headerText="Facility Class">
							<af:outputText
								value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
						</af:column>

						<af:column sortProperty="facilityTypeCd" sortable="true"
							formatType="text" headerText="Facility Type">
							<af:outputText
								value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty facility.facilityTypeCd ? '' : facility.facilityTypeCd)]}" />
						</af:column>

						<af:column sortProperty="countyCd" sortable="true"
							formatType="text" headerText="County">
							<af:selectOneChoice value="#{facility.countyCd}" readOnly="true">
								<f:selectItems value="#{infraDefs.counties}" />
							</af:selectOneChoice>
						</af:column>

						<af:column sortProperty="c08" sortable="true" formatType="text"
							headerText="Lat/Long">
							<af:goLink text="#{facility.phyAddr.latlong}" targetFrame="_new"
								rendered="#{not empty facility.googleMapsURL}"
								destination="#{facility.googleMapsURL}"
								shortDesc="Clicking this will open Google Maps in a separate tab or window." />
						</af:column>
						<f:facet name="footer">
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton actionListener="#{tableExporter.printTable}"
										onclick="#{tableExporter.onClickScript}" text="Printable view" />
									<af:commandButton actionListener="#{tableExporter.excelTable}"
										onclick="#{tableExporter.onClickScript}"
										text="Export to excel" />
								</af:panelButtonBar>
							</afh:rowLayout>
						</f:facet>
					</af:table>
				</af:panelForm>
				<af:objectSpacer height="20" />
				<af:panelForm>
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Edit"
								disabled="#{contactDetail.portalDetailEditDisabled}"
								rendered="#{!contactDetail.facilityExclusionListEditable}"
								action="#{contactDetail.editFacilityExclusionList}" />
							<af:commandButton text="Save"
								disabled="#{contactDetail.portalDetailEditDisabled}"
								rendered="#{contactDetail.facilityExclusionListEditable}"
								action="#{contactDetail.saveFacilityExclusionList}" />
							<af:commandButton text="Cancel"
								rendered="#{contactDetail.facilityExclusionListEditable}"
								action="#{contactDetail.cancelEditFacilityExclusionList}" />
							<af:commandButton text="Return"
								action="#{contactDetail.closeDialog}" immediate="true" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</f:subview>
		</af:form>
	</af:document>
</f:view>