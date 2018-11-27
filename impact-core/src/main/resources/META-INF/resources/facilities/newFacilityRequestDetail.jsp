<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Facility Creation Request Detail">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				id="newFacilityRequestDetail" title="Facility Creation Request Detail">
				<af:inputHidden value="#{facilityRequestSearch.popupRedirect}"
					rendered="#{newFacilityRequest.internalApp}"/>
				
				<%@ include file="../util/branding.jsp"%>
				<f:facet name="nodeStamp">
					<af:commandMenuItem text="#{foo.label}" action="#{foo.getOutcome}"
						type="#{foo.type}" disabled="#{foo.disabled}"
						rendered="#{foo.rendered}" icon="#{foo.icon}"
						onclick="if (#{newFacilityRequest.editMode}) { alert('Please save or discard your changes'); return false; }" />
				</f:facet>

				<afh:rowLayout halign="center" rendered="#{!newFacilityRequest.error}">
					<af:panelForm labelWidth="200" width="650">
						<af:panelHeader text="Facility" size="0" />
						<af:panelForm rows="1" maxColumns="2" labelWidth="200" width="650">
						<af:panelForm>
							<af:inputText label="Request ID:" value="#{newFacilityRequest.facilityRequest.reqId}"
								readOnly="true"
								columns="40" id="reqId" />
							<af:inputText label="Company Name:" readOnly="true"
								value="#{newFacilityRequest.facilityRequest.companyName}" />
							<af:inputText label="Facility Name:"
								value="#{newFacilityRequest.facilityRequest.name}" id="name"
								readOnly="true" columns="55" maximumLength="55"
								showRequired="true" />
							<af:inputText label="Facility Description:"
								value="#{newFacilityRequest.facilityRequest.desc}" columns="60"
								rows="3" maximumLength="3000" wrap="soft" readOnly="true"/>
							<af:selectOneChoice label="Facility Type:"
								value="#{newFacilityRequest.facilityRequest.facilityTypeCd}"
								readOnly="true"
								id="facilityTypeCd" unselectedLabel=""
								styleClass="FacilityTypeClass x6" showRequired="true">
								<f:selectItems
									value="#{facilityReference.facilityTypeDefs.items[0]}" />
								<%-- 	<af:clientListener method="ready" type="valueChange"/> --%>
							</af:selectOneChoice>
							<af:selectOneChoice label="Operating Status:"
								value="#{newFacilityRequest.facilityRequest.operatingStatusCd}"
								readOnly="true"
								id="operatingStatusCd" showRequired="true">
								<f:selectItems
									value="#{facilityReference.operatingStatusDefs.items[0]}" />
							</af:selectOneChoice>
						</af:panelForm>
						<af:panelForm>
							<af:selectOneChoice label="Request State:"
								value="#{newFacilityRequest.facilityRequest.requestStatusCd}"
								readOnly="#{!newFacilityRequest.editMode}"
								id="requestStatusCd" showRequired="true">
								<f:selectItems
									value="#{facilityReference.facilityRequestStatusDefs.items[0]}" />
							</af:selectOneChoice>
							<af:selectInputDate id="submitDate" label="Date Submitted: "
								value="#{newFacilityRequest.facilityRequest.submitDate}"
								readOnly="true"
								showRequired="true">
								<af:validateDateTimeRange minimum="1900-01-01" />
							</af:selectInputDate>
							<af:inputText label="Contact ID:" value="#{newFacilityRequest.facilityRequest.cntId}"
								readOnly="true"
								columns="40" id="cntId" />
							<af:inputText label="First Name:"
								value="#{newFacilityRequest.facilityRequest.firstNm}" columns="40"
								readOnly="true"
								maximumLength="40"
								id="firstNm" showRequired="true" />
							<af:inputText label="Last Name:"
								value="#{newFacilityRequest.facilityRequest.lastNm}" columns="40"
								readOnly="true"
								maximumLength="40"
								id="lastNm" showRequired="true" />
						</af:panelForm>
						</af:panelForm>
						<af:panelHeader text="Facility Location" size="0" />
						<af:panelForm rows="1" maxColumns="5" labelWidth="200" width="650">
							<af:inputText label="Latitude:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.latitude}"
								readOnly="true"
								columns="15" maximumLength="10" id="latitude"
								showRequired="true"
								shortDesc="The value must be between 41 ~ 45." />
							<af:inputText label="Longitude:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.longitude}"
								readOnly="true"
								columns="15" maximumLength="10" id="longitude"
								showRequired="true" 
								shortDesc="The value must be between -111.06 ~ -104.05." />
						</af:panelForm>
						<af:panelForm rows="1" maxColumns="11" labelWidth="200"
							width="650">
							<af:selectOneChoice label="Quarter Quarter:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.quarterQuarter}"
								readOnly="true"
								id="quarterQuarter">
								<f:selectItems value="#{infraDefs.quarterQuarters}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="Quarter:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.quarter}"
								readOnly="true"
								id="quarter">
								<f:selectItems value="#{infraDefs.quarters}" />
							</af:selectOneChoice>
							<af:inputText label="Section:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.section}"
								readOnly="true"
								columns="3" maximumLength="2" id="section" showRequired="true"
								shortDesc="The value must be between 1 ~ 36." />
							<af:inputText label="Township:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.township}"
								readOnly="true"
								columns="3" maximumLength="3" id="township" showRequired="true"	
								shortDesc="The value must be of the format D followed by N or S, where D is a positive number. E.g., 45N." />
							<af:inputText label="Range:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.range}"
								readOnly="true"
								columns="3" maximumLength="4" id="range" showRequired="true"
								shortDesc="The value must be of the format D followed by W or E, where D is a positive number. E.g., 6W." />
						</af:panelForm>

						<af:panelForm rows="1" maxColumns="6" labelWidth="200" width="650">
							<af:selectOneChoice label="County:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.countyCd}"
								readOnly="true"
								id="countyCd" showRequired="true">
								<f:selectItems value="#{infraDefs.counties}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="State:" 
								value="#{newFacilityRequest.facilityRequest.phyAddr.state}"
								readOnly="true"
								id="state" showRequired="true">
								<f:selectItems value="#{infraDefs.states}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="District:" rendered="#{infraDefs.districtVisible}"
								value="#{newFacilityRequest.facilityRequest.phyAddr.districtCd}"
								readOnly="true"
								id="districtCd" showRequired="true">
								<f:selectItems value="#{infraDefs.districts}" />
							</af:selectOneChoice>
							<af:selectOneChoice label="Indian Reservation:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.indianReservationCd}" unselectedLabel=" "
								readOnly="true" id="indianReservationCd">
								<f:selectItems value="#{infraDefs.indianReservations.items[(empty newFacilityRequest.facilityRequest.phyAddr.indianReservationCd ? '' : newFacilityRequest.facilityRequest.phyAddr.indianReservationCd)]}" />
							</af:selectOneChoice>
						</af:panelForm>

						<af:panelForm rows="1" maxColumns="4" labelWidth="200" width="650">
							<af:inputText label="Physical Address 1:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.addressLine1}"
								readOnly="true"
								id="addressLine1" columns="65"
								maximumLength="100" />
							<af:inputText label="Physical Address 2:"
								value="#{newFacilityRequest.facilityRequest.phyAddr.addressLine2}"
								readOnly="true"
								id="addressLine2" columns="65" maximumLength="100" />
						</af:panelForm>
						<af:panelForm rows="1" maxColumns="6" labelWidth="200" width="650">
							<af:inputText label="City:" columns="15"
								inlineStyle="font-size:10pt;"
								value="#{newFacilityRequest.facilityRequest.phyAddr.cityName}"
								readOnly="true"
								maximumLength="50" id="cityName" />
							<af:inputText label="Zip:" columns="15"
								inlineStyle="font-size:10pt;"
								value="#{newFacilityRequest.facilityRequest.phyAddr.zipCode}"
								readOnly="true"
								id="zipCode" maximumLength="10" />
						</af:panelForm>
						<af:panelHeader text="Memo" size="0" />
						<af:panelForm rows="1" maxColumns="1" labelWidth="200" width="650">
							<af:inputText id="noteTxt" label="Additional Information :"
								value="#{newFacilityRequest.facilityRequest.memo}" 
								readOnly="#{!newFacilityRequest.editMode}" columns="145"
								rows="6" maximumLength="4000" onkeydown="charsLeft(4000);"
								onkeyup="charsLeft(4000);" />
						</af:panelForm>
					</af:panelForm>

				</afh:rowLayout>
				<af:objectSpacer width="100%" height="15" />

				<afh:rowLayout halign="center" rendered="#{!newFacilityRequest.error}">
					<af:panelButtonBar>
						<af:commandButton text="Edit" id="editButton" 
							action="#{newFacilityRequest.edit}" 
							rendered="#{!newFacilityRequest.editMode && !newFacilityRequest.requestDeleted && newFacilityRequest.admin}"/>
						<af:commandButton text="Save" id="saveButton"
							action="#{newFacilityRequest.saveFacilityRequest}" 
							rendered="#{newFacilityRequest.editMode}"/>
						<af:commandButton text="Cancel" id="cancelButton"
							action="#{newFacilityRequest.submitFacilityRequest}" 
							rendered="#{newFacilityRequest.editMode}" />
						<af:commandButton text="Delete" id="deleteButton"
							disabled="#{!newFacilityRequest.admin}"
							action="#{newFacilityRequest.requestDelete}" useWindow="true"
							windowWidth="500" windowHeight="300"
							rendered="#{!newFacilityRequest.editMode && !newFacilityRequest.requestDeleted && newFacilityRequest.admin}" />
						<af:commandButton text="Create Facility" id="createButton"
							disabled="#{!newFacilityRequest.admin}"
							action="#{newFacilityRequest.createFacility}"
							windowWidth="500" windowHeight="300"
							rendered="#{!newFacilityRequest.editMode && !newFacilityRequest.requestDeleted && newFacilityRequest.admin}" />
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>
