<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Create Facility ">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" id="createFacility"
				title="Create Facility">

				<jsp:include page="header.jsp" />

				<afh:rowLayout halign="center">
					<af:panelForm labelWidth="200" width="650">
						<af:panelForm rows="8" maxColumns="2" labelWidth="200" width="650">
							<af:panelHeader text="Facility" size="0" />
							<af:inputText label="Facility Name:"
								value="#{createFacility.facility.name}" id="name" columns="55"
								maximumLength="55" showRequired="true" />
							<af:inputText label="Facility Description:"
								value="#{createFacility.facility.desc}" columns="60"
								rows="3" maximumLength="3000" wrap="soft"  />
							<af:selectOneChoice label="Company Name:"
								value="#{createFacility.facility.companyName}"
								 unselectedLabel="" showRequired="true">
								<f:selectItems value="#{companySearch.allCompanies}" />
							</af:selectOneChoice>				
							<af:selectOneChoice label="Facility Type:"
								value="#{createFacility.facility.facilityTypeCd}"
								id="facilityTypeCd" unselectedLabel=""
								styleClass="FacilityTypeClass x6" showRequired="true">
								<f:selectItems
									value="#{facilityReference.facilityTypeDefs.items[0]}" />
								<%-- 	<af:clientListener method="ready" type="valueChange"/> --%>
							</af:selectOneChoice>
							<af:selectOneChoice label="Facility Class:"
								value="#{createFacility.facility.permitClassCd}"
								showRequired="true">
								<f:selectItems
									value="#{facilityReference.permitClassDefs.items[0]}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="Operating Status:"
								value="#{createFacility.facility.operatingStatusCd}"
								id="facOperatingStatus" showRequired="true">
								<f:selectItems
									value="#{facilityReference.operatingStatusDefs.items[0]}" />
							</af:selectOneChoice>
						</af:panelForm>
						<af:panelHeader text="Facility Location" size="0" />
						<af:panelForm rows="1" maxColumns="5" labelWidth="200" width="650">
							<af:inputText label="Latitude:" styleClass="latitude"
								value="#{createFacility.facility.phyAddr.latitude}" columns="15"
								maximumLength="10" id="latitude" showRequired="true"
								shortDesc="The value must be between #{infraDefs.minLatitude}  ~ #{infraDefs.maxLatitude}."
								onchange="$('.loading').show();" />
							<af:inputText label="Longitude:" styleClass="longitude"
								value="#{createFacility.facility.phyAddr.longitude}"
								columns="15" maximumLength="11" id="longitude"
								showRequired="true" autoSubmit="true"
								shortDesc="The value must be between #{infraDefs.minLongitude} ~ #{infraDefs.maxLongitude}." />
							<af:commandButton text="Generate Location Data" id="genButton1"
								onmouseover="document.getElementById('createFacility:genButton1').focus();"
								action="#{createFacility.facility.phyAddr.calculatePlss}" />
						</af:panelForm>
						<af:panelForm rows="1" maxColumns="11" labelWidth="200"
							width="650">
							<af:selectOneChoice label="Quarter Quarter:"
								value="#{createFacility.facility.phyAddr.quarterQuarter}"
								id="quarterQuarter">
								<f:selectItems value="#{infraDefs.quarterQuarters}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="Quarter:"
								value="#{createFacility.facility.phyAddr.quarter}" id="quarter">
								<f:selectItems value="#{infraDefs.quarters}" />
							</af:selectOneChoice>
							<af:inputText label="Section:" styleClass="section"
								value="#{createFacility.facility.phyAddr.section}" columns="3"
								maximumLength="2" id="section" showRequired="true"
								autoSubmit="true" shortDesc="The value must be between 1 ~ 36." />
							<af:inputText label="Township:" styleClass="township"
								value="#{createFacility.facility.phyAddr.township}" columns="3"
								maximumLength="3" id="township" showRequired="true"
								autoSubmit="true"
								shortDesc="The value must be of the format D followed by N or S, where D is a positive number. E.g., 45N." />
							<af:inputText label="Range:" styleClass="range"
								value="#{createFacility.facility.phyAddr.range}" columns="3"
								maximumLength="4" id="range" showRequired="true"
								autoSubmit="true"
								shortDesc="The value must be of the format D followed by W or E, where D is a positive number. E.g., 6W." />
							<af:commandButton text="Generate Location Data" id="genButton2"
								onmouseover="document.getElementById('createFacility:genButton2').focus();"
								action="#{createFacility.facility.phyAddr.calculateLatLong}" />
						</af:panelForm>

						<af:panelForm rows="1" maxColumns="6" labelWidth="200" width="650">
							<af:selectOneChoice label="County:" autoSubmit="true"
								styleClass="county"
								value="#{createFacility.facility.phyAddr.countyCd}"
								id="countyCd" showRequired="true">
								<f:selectItems value="#{infraDefs.counties}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="State:" autoSubmit="true"
								value="#{createFacility.facility.phyAddr.state}" id="state"
								showRequired="true">
								<f:selectItems value="#{infraDefs.states}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="District:" autoSubmit="true" rendered="#{infraDefs.districtVisible}"
								styleClass="district"
								value="#{createFacility.facility.phyAddr.districtCd}"
								id="districtCd" showRequired="false">
								<f:selectItems value="#{infraDefs.districts}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="Indian Reservation:" autoSubmit="true"
								value="#{createFacility.facility.phyAddr.indianReservationCd}" unselectedLabel=" "
								id="indianReservationCd">
								<f:selectItems value="#{infraDefs.indianReservations.items[(empty createFacility.facility.phyAddr.indianReservationCd ? '' : createFacility.facility.phyAddr.indianReservationCd)]}" />
							</af:selectOneChoice>
						</af:panelForm>

						<af:panelForm rows="1" maxColumns="4" labelWidth="200" width="650"
							partialTriggers="loadAddress">
							<af:inputText label="Physical Address 1:"
								tip="Facility’s physical location address. Do not enter a PO box number."
								value="#{createFacility.facility.phyAddr.addressLine1}" showRequired="#{!infraDefs.plssAutoReplication}"
								styleClass="address1" id="addressLine1" columns="65"
								maximumLength="100" />
							<af:inputText label="Physical Address 2:"
								tip="Facility’s physical location address. Do not enter a PO box number."
								value="#{createFacility.facility.phyAddr.addressLine2}"
								id="addressLine2" columns="65" maximumLength="100" />
						</af:panelForm>
						<af:panelForm rows="1" maxColumns="6" labelWidth="200" width="650"
							partialTriggers="loadAddress">
							<af:inputText label="City:" columns="15" styleClass="city"
								inlineStyle="font-size:10pt;" showRequired="#{!infraDefs.plssAutoReplication}"
								value="#{createFacility.facility.phyAddr.cityName}"
								maximumLength="50" id="cityName" />
							<af:inputText label="Zip:" columns="15" styleClass="zip"
								inlineStyle="font-size:10pt;" showRequired="#{!infraDefs.plssAutoReplication}"
								value="#{createFacility.facility.phyAddr.zipCode}" id="zipCode"
								maximumLength="10" />
							<af:selectInputDate id="effectiveDate" label="Effective Date: "
								value="#{createFacility.facility.phyAddr.beginDate}"
								showRequired="true">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
						</af:panelForm>
					</af:panelForm>
				</afh:rowLayout>
				<af:objectSpacer width="100%" height="15" />

				<af:inputText styleClass="ismatch" inlineStyle="visibility: hidden"
					partialTriggers="latitude longitude section township range countyCd state districtCd"
					value="#{createFacility.facility.phyAddr.isMatch}" readOnly="True" />

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Submit Create Facility" id="saveButton"
							onclick="return showSavingConfirmation();return false;"
							onmouseover="document.getElementById('createFacility:saveButton').focus();"
							disabled="#{facilityProfile.readOnlyUser || createFacility.buttonClicked}"
							action="#{createFacility.submitCreateFacility}" partialSubmit="true"/>
						<af:commandButton text="Reset" id="cancelButton"
							onmouseover="document.getElementById('createFacility:cancelButton').focus();"
							action="#{createFacility.resetCreateFacility}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
		<f:verbatim><%@ include
				file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/FacilityType-Option.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/wording-filter.js"%></f:verbatim>
		<f:verbatim><%@ include
				file="../scripts/facility-detail-location.js"%></f:verbatim>
	</af:document>
	
</f:view>
