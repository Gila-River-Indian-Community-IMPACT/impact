<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
	<af:document id="locationBody" inlineStyle="width:1000px;">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:messages />
		<%@ include file="../util/validate.js"%>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" id="createLocation"
				title="Location Detail (Use WGS 84 datum)">
				<af:panelForm rows="1" maxColumns="5" labelWidth="140" width="900">
					<af:inputText label="Latitude:" styleClass="latitude"
						inlineStyle="font-size:10pt;"
						value="#{facilityProfile.address.latitude}" columns="8"
						maximumLength="10" readOnly="#{!facilityProfile.editable1}"
						id="latitude" showRequired="true" autoSubmit="true"
						shortDesc="The value must be between #{infraDefs.minLatitude}  ~ #{infraDefs.maxLatitude}." />
					<af:inputText label="Longitude:" styleClass="longitude"
						inlineStyle="font-size:10pt;"
						value="#{facilityProfile.address.longitude}" columns="8"
						maximumLength="10" readOnly="#{!facilityProfile.editable1}"
						id="longitude" showRequired="true" autoSubmit="true"
						shortDesc="The value must be between #{infraDefs.minLongitude}  ~ #{infraDefs.maxLongitude}." />
					<af:commandButton text="Generate Location Data" id="genButton1"
						rendered="#{facilityProfile.editable1}"
						onmouseover="document.getElementById('createLocation:genButton1').focus();"
						action="#{facilityProfile.address.calculatePlss}" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="5" labelWidth="140" width="900">
					<af:panelForm rows="1" maxColumns="2" labelWidth="120" width="100">
						<af:selectOneChoice label="Quarter Quarter:" unselectedLabel=" "
							value="#{facilityProfile.address.quarterQuarter}"
							readOnly="#{!facilityProfile.editable1}" id="quarterQuarter">
							<f:selectItems value="#{infraDefs.quarterQuarters}" />
						</af:selectOneChoice>
						<af:selectOneChoice label="Quarter:"
							value="#{facilityProfile.address.quarter}" unselectedLabel=" "
							readOnly="#{!facilityProfile.editable1}" id="quarter">
							<f:selectItems value="#{infraDefs.quarters}" />
						</af:selectOneChoice>
					</af:panelForm>
					<af:inputText label="Section:" styleClass="section"
						value="#{facilityProfile.address.section}" columns="3"
						maximumLength="2" readOnly="#{!facilityProfile.editable1}"
						id="section" showRequired="true" autoSubmit="true"
						inlineStyle="font-size:10pt;"
						shortDesc="The value must be between 1 ~ 36." />
					<af:inputText label="Township:" styleClass="township"
						value="#{facilityProfile.address.township}" columns="3"
						maximumLength="3" readOnly="#{!facilityProfile.editable1}"
						id="township" showRequired="true" autoSubmit="true"
						inlineStyle="font-size:10pt;"
						shortDesc="The value must be of the format D followed by N or S, where D is a positive number. E.g., 45N." />
					<af:inputText label="Range:" styleClass="range"
						value="#{facilityProfile.address.range}" columns="6"
						maximumLength="6" readOnly="#{!facilityProfile.editable1}"
						id="range" showRequired="true" autoSubmit="true"
						inlineStyle="font-size:10pt;"
						shortDesc="The value must be of the format D followed by W or E, where D is a positive number. E.g., 6W or 92.5E." />
					<af:commandButton text="Generate Location Data" id="genButton2"
						rendered="#{facilityProfile.editable1}"
						onmouseover="document.getElementById('createLocation:genButton2').focus();"
						action="#{facilityProfile.address.calculateLatLong}" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="5" labelWidth="140" width="900">
					<af:selectOneChoice label="County:" autoSubmit="true"
						value="#{facilityProfile.address.countyCd}"
						readOnly="#{!facilityProfile.editable1}" id="countyCd"
						showRequired="true">
						<f:selectItems value="#{infraDefs.counties}" />
					</af:selectOneChoice>
					<af:selectOneChoice label="State:" autoSubmit="true"
						value="#{facilityProfile.address.state}"
						readOnly="#{!facilityProfile.editable1}" id="state"
						showRequired="true">
						<f:selectItems value="#{infraDefs.states}" />
					</af:selectOneChoice>
					<af:selectOneChoice label="District:" autoSubmit="true" rendered="#{infraDefs.districtVisible}"
						value="#{facilityProfile.address.districtCd}"
						readOnly="#{!facilityProfile.editable1}" id="districtCd"
						showRequired="true">
						<f:selectItems value="#{infraDefs.districts}" />
					</af:selectOneChoice>
					<af:selectOneChoice label="Indian Reservation:" autoSubmit="true"
						value="#{facilityProfile.address.indianReservationCd}" unselectedLabel=" "
						readOnly="#{!facilityProfile.editable1}" id="indianReservationCd">
						<f:selectItems value="#{infraDefs.indianReservations.items[(empty facilityProfile.address.indianReservationCd ? '' : facilityProfile.address.indianReservationCd)]}" />
					</af:selectOneChoice>
					
				</af:panelForm>
				<af:panelHeader text="" size="1" />
				<af:panelForm rows="1" maxColumns="5" labelWidth="140" width="900"
					partialTriggers="loadAddress">
					<af:inputText label="Physical Address 1:" columns="50"
						inlineStyle="font-size:10pt;"
						tip="#{facilityProfile.editable1 ? 'Facility\\'s physical location address. Do not enter a PO box number.' : ''}"
						value="#{facilityProfile.address.addressLine1}" showRequired="#{!infraDefs.plssAutoReplication}"
						styleClass="address1" maximumLength="100"
						readOnly="#{!facilityProfile.editable1}" id="addressLine1" />
					<af:inputText label="Physical Address 2:" columns="50"
						inlineStyle="font-size:10pt;"
						tip="#{facilityProfile.editable1 ? 'Facility\\'s physical location address. Do not enter a PO box number.' : ''}"
						value="#{facilityProfile.address.addressLine2}"
						maximumLength="100" readOnly="#{!facilityProfile.editable1}" />
				</af:panelForm>
				<af:panelForm rows="1" maxColumns="5" labelWidth="140" width="900"
					partialTriggers="loadAddress">
					<af:inputText label="City:" columns="15" styleClass="city"
						inlineStyle="font-size:10pt;" showRequired="#{!infraDefs.plssAutoReplication}"
						value="#{facilityProfile.address.cityName}" maximumLength="50"
						readOnly="#{!facilityProfile.editable1}" id="cityName" />
					<af:inputText label="Zip:" columns="15" showRequired="#{!infraDefs.plssAutoReplication}"
						value="#{facilityProfile.address.zipCode}" id="zipCode"
						styleClass="zip" inlineStyle="font-size:10pt;"
						readOnly="#{!facilityProfile.editable1}" maximumLength="10" />
					<af:selectInputDate id="effectiveDate" label="Effective Date: "
						readOnly="#{!facilityProfile.editable1}"
						value="#{facilityProfile.address.beginDate}" showRequired="true">
						<af:validateDateTimeRange minimum="1900-01-01" />
					</af:selectInputDate>
				</af:panelForm>
				<af:objectSpacer width="100%" height="15" />

				<af:inputText styleClass="ismatch" inlineStyle="visibility: hidden"
					partialTriggers="latitude longitude section township range countyCd state districtCd"
					value="#{facilityProfile.address.isMatch}" readOnly="True" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Edit"
							disabled="#{facilityProfile.disabledUpdateButton}"
							rendered="#{!facilityProfile.editable1}"
							action="#{facilityProfile.editAddress}" />
						<af:commandButton text="Save" id="saveButton"
							onclick="return showSavingConfirmation();return false;"
							onmouseover="document.getElementById('createLocation:saveButton').focus();"
							rendered="#{facilityProfile.editable1}"
							action="#{facilityProfile.saveAddresses}" />
						<af:commandButton text="Cancel" id="cancelButton"
							rendered="#{facilityProfile.editable1}"
							onmouseover="document.getElementById('createLocation:cancelButton').focus();"
							action="#{facilityProfile.cancelEditAddress}" />
						<af:commandButton text="Close"
							rendered="#{!facilityProfile.editable1}"
							action="#{facilityProfile.closeAddrDialog}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
	<f:verbatim><%@ include
			file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
	<f:verbatim><%@ include
			file="../scripts/wording-filter.js"%></f:verbatim>
	<f:verbatim><%@ include
			file="../scripts/facility-detail-location.js"%></f:verbatim>
</f:view>