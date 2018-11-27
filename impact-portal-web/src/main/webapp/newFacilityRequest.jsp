<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body"
		title="Request creation of a new facility">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page id="newFacilityRequest"
				title="Request creation of a new facility">
				<f:facet name="messages">
					<af:messages />
				</f:facet>

				<afh:rowLayout halign="center">
					<af:panelForm labelWidth="200" width="900">
						<af:panelForm rows="8" maxColumns="2" labelWidth="200" width="900">
							<af:panelHeader text="Facility" size="0" />
							<af:inputText label="Company Name:" readOnly="true"
								value="#{companyProfile.company.name}"
								rendered="#{companyProfile.company != null}" />
							<af:inputText label="Facility Name:"
								value="#{newFacilityRequest.facilityRequest.name}" id="name"
								columns="55" maximumLength="55" showRequired="true" />
							<af:inputText label="Facility Description:"
								value="#{newFacilityRequest.facilityRequest.desc}" columns="60"
								rows="3" maximumLength="3000" wrap="soft"  />
							<af:selectOneChoice label="Facility Type:"
								value="#{newFacilityRequest.facilityRequest.facilityTypeCd}"
								id="facilityTypeCd" unselectedLabel=""
								styleClass="FacilityTypeClass x6" showRequired="true">
								<f:selectItems
									value="#{facilityReference.facilityTypeDefs.items[0]}" />
								<%-- 	<af:clientListener method="ready" type="valueChange"/> --%>
							</af:selectOneChoice>
							<af:selectOneChoice label="Operating Status:"
								value="#{newFacilityRequest.facilityRequest.operatingStatusCd}"
								id="operatingStatusCd" showRequired="true">
								<f:selectItems
									value="#{facilityReference.operatingStatusDefs.items[0]}" />
							</af:selectOneChoice>
						</af:panelForm>
						<af:panelHeader text="Facility Location" size="0" />
						<af:panelForm rows="1" maxColumns="5" labelWidth="200" width="900">
							<af:inputText label="Latitude:" styleClass="latitude"
								value="#{newFacilityRequest.facilityRequest.phyAddr.latitude}"
								columns="15" maximumLength="10" id="latitude"
								showRequired="true"
								shortDesc="The value must be between #{infraDefs.minLatitude}  ~ #{infraDefs.maxLatitude}."
								onchange="$('.loading').show();" />
							<af:inputText label="Longitude:" styleClass="longitude"
								value="#{newFacilityRequest.facilityRequest.phyAddr.longitude}"
								columns="15" maximumLength="10" id="longitude"
								showRequired="true" autoSubmit="true"
								shortDesc="The value must be between #{infraDefs.minLongitude}  ~ #{infraDefs.maxLongitude}." />
							<af:commandButton text="Generate Location Data" id="genButton1"
								onmouseover="document.getElementById('newFacilityRequest:genButton1').focus();"
								action="#{newFacilityRequest.facilityRequest.phyAddr.calculatePlss}" />
						</af:panelForm>
						<af:panelForm rows="1" maxColumns="11" labelWidth="200"
							width="700">
							<af:selectOneChoice label="Quarter Quarter:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.quarterQuarter}"
								id="quarterQuarter">
								<f:selectItems value="#{infraDefs.quarterQuarters}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="Quarter:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.quarter}"
								id="quarter">
								<f:selectItems value="#{infraDefs.quarters}" />
							</af:selectOneChoice>
							<af:inputText label="Section:" styleClass="section"
								value="#{newFacilityRequest.facilityRequest.phyAddr.section}"
								columns="3" maximumLength="2" id="section" showRequired="true"
								autoSubmit="true" shortDesc="The value must be between 1 ~ 36." />
							<af:inputText label="Township:" styleClass="township"
								value="#{newFacilityRequest.facilityRequest.phyAddr.township}"
								columns="3" maximumLength="3" id="township" showRequired="true"
								autoSubmit="true"
								shortDesc="The value must be of the format D followed by N or S, where D is a positive number. E.g., 45N." />
							<af:inputText label="Range:" styleClass="range"
								value="#{newFacilityRequest.facilityRequest.phyAddr.range}"
								columns="3" maximumLength="4" id="range" showRequired="true"
								autoSubmit="true"
								shortDesc="The value must be of the format D followed by W or E, where D is a positive number. E.g., 6W." />
							<af:commandButton text="Generate Location Data" id="genButton2"
								onmouseover="document.getElementById('newFacilityRequest:genButton2').focus();"
								action="#{newFacilityRequest.facilityRequest.phyAddr.calculateLatLong}" />
						</af:panelForm>

						<af:panelForm rows="1" maxColumns="6" labelWidth="200" width="900">
							<af:selectOneChoice label="County:" autoSubmit="true"
								styleClass="county"
								value="#{newFacilityRequest.facilityRequest.phyAddr.countyCd}"
								id="countyCd" showRequired="true">
								<f:selectItems value="#{infraDefs.counties}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="State:" autoSubmit="true"
								value="#{newFacilityRequest.facilityRequest.phyAddr.state}"
								id="state" showRequired="true">
								<f:selectItems value="#{infraDefs.states}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="District:" autoSubmit="true" rendered="#{infraDefs.districtVisible}"
								readOnly="true"
								value="#{newFacilityRequest.facilityRequest.phyAddr.districtCd}"
								id="districtCd">
								<f:selectItems value="#{infraDefs.districts}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="Indian Reservation:" autoSubmit="true"
								value="#{newFacilityRequest.facilityRequest.phyAddr.indianReservationCd}" unselectedLabel=" "
								id="indianReservationCd">
								<f:selectItems value="#{infraDefs.indianReservations.items[(empty newFacilityRequest.facilityRequest.phyAddr.indianReservationCd ? '' : newFacilityRequest.facilityRequest.phyAddr.indianReservationCd)]}" />
							</af:selectOneChoice>
						</af:panelForm>

						<af:panelForm rows="1" maxColumns="4" labelWidth="200" width="900"
							partialTriggers="loadAddress">
							<af:inputText label="Physical Address 1:"
								tip="Facilitys physical location address. Do not enter a PO box number."
								value="#{newFacilityRequest.facilityRequest.phyAddr.addressLine1}" showRequired="#{!infraDefs.plssAutoReplication}"
								styleClass="address1" id="addressLine1" columns="65"
								maximumLength="100" />
							<af:inputText label="Physical Address 2:"
								tip="Facilitys physical location address. Do not enter a PO box number."
								value="#{newFacilityRequest.facilityRequest.phyAddr.addressLine2}"
								id="addressLine2" columns="65" maximumLength="100" />
						</af:panelForm>
						<af:panelForm rows="1" maxColumns="6" labelWidth="200" width="900"
							partialTriggers="loadAddress">
							<af:inputText label="City:" columns="15" styleClass="city"
								inlineStyle="font-size:10pt;" showRequired="#{!infraDefs.plssAutoReplication}"
								value="#{newFacilityRequest.facilityRequest.phyAddr.cityName}"
								maximumLength="50" id="cityName" />
							<af:inputText label="Zip:" columns="15" styleClass="zip"
								inlineStyle="font-size:10pt;" showRequired="#{!infraDefs.plssAutoReplication}"
								value="#{newFacilityRequest.facilityRequest.phyAddr.zipCode}"
								id="zipCode" maximumLength="10" />
						</af:panelForm>
						<af:panelHeader text="Memo" size="0" /> 
						<af:panelForm rows="1" maxColumns="1" labelWidth="200" width="900">
							<af:inputText id="noteTxt" label="Additional Information :"
								tip="In the text box above, please provide details about the new facility. If it is similar to another facility that already exists in IMPACT, please provide as many details as possible using the Facility ID and Name and the associated AQD IDs for the Emission Units, Processes, Control Equipment, and Release Points that are similar."
								value="#{newFacilityRequest.facilityRequest.memo}" columns="145"
								rows="6" maximumLength="4000" onkeydown="charsLeft(4000);"
								onkeyup="charsLeft(4000);" />
						</af:panelForm>
					</af:panelForm>

				</afh:rowLayout>
				<af:objectSpacer width="100%" height="15" />

				<af:inputText styleClass="ismatch" inlineStyle="visibility: hidden"
					partialTriggers="latitude longitude section township range countyCd state districtCd"
					value="#{newFacilityRequest.facilityRequest.phyAddr.isMatch}"
					readOnly="True" />

				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Submit New Facility Request"
							id="saveButton"
							onmouseover="document.getElementById('newFacilityRequest:saveButton').focus();"
							actionListener="#{newFacilityRequest.submitNewFacilityRequest}" />
						<af:commandButton text="Reset" id="resetButton"
							onmouseover="document.getElementById('newFacilityRequest:resetButton').focus();"
							actionListener="#{newFacilityRequest.resetNewFacilityRequest}" />
						<af:commandButton text="Cancel" id="cancelButton" immediate="true"
							onmouseover="document.getElementById('newFacilityRequest:cancelButton').focus();"
							actionListener="#{newFacilityRequest.cancelNewFacilityRequest}" />
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
