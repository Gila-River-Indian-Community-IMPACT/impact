<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Create Company">
		<f:verbatim>
			<script>
				</f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim>
			</script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" id="createCompany"
				title="Create Company">
				<jsp:include page="../util/header.jsp" />

				<afh:rowLayout halign="center">

					<af:panelForm labelWidth="200" width="650">
						<af:panelHeader text="Company" size="0" />
						<af:inputText label="Name:" value="#{createCompany.company.name}"
							id="name" columns="55" maximumLength="80" showRequired="true" />
						<af:inputText label="Alias:"
							value="#{createCompany.company.alias}" id="alias" columns="55"
							maximumLength="80" />
						<af:inputText label="CROMERR ID:"
							value="#{createCompany.company.externalCompanyId}" id="externalId"
							columns="10" maximumLength="11" tip="When entering a CROMERR ID, CROMERR ID must be an integer." />
					</af:panelForm>
				</afh:rowLayout>


				<afh:rowLayout halign="center">
					<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="650">
						<af:panelHeader text="Company Address" size="0" />
						<af:inputText label="Address 1:"
							value="#{createCompany.company.address.addressLine1}"
							columns="60" maximumLength="100" id="addressLine1"
							tip="When entering an address, City, State, Zip, and Country fields are required." />
						<af:inputText label="Address 2:"
							value="#{createCompany.company.address.addressLine2}"
							columns="60" maximumLength="100" />
					</af:panelForm>
				</afh:rowLayout>

				<afh:rowLayout halign="center">
					<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="650">
						<af:inputText label="City"
							value="#{createCompany.company.address.cityName}" columns="30"
							maximumLength="50" id="cityName" />
						<af:selectOneChoice label="State:" unselectedLabel=""
							value="#{createCompany.company.address.state}" id="state">
							<f:selectItems value="#{infraDefs.states}" />
						</af:selectOneChoice>
						<af:inputText label="Zip Code:"
							value="#{createCompany.company.address.zipCode}" id="zipCode" />
						<af:selectOneChoice label="Country:" unselectedLabel=""
							value="#{createCompany.company.address.countryCd}" id="country">
							<f:selectItems value="#{infraDefs.countries}" />
						</af:selectOneChoice>
					</af:panelForm>
				</afh:rowLayout>

				<afh:rowLayout halign="center">
					<af:panelForm rows="2" maxColumns="1" labelWidth="200" width="650">
						<af:panelHeader text="Company Phone Numbers" size="0" />
						<af:inputText label="Phone No.:"
							value="#{createCompany.company.phone}" columns="14"
							maximumLength="14" id="phone"
							converter="#{infraDefs.phoneNumberConverter}" />
						<af:inputText label="Fax No.:"
							value="#{createCompany.company.fax}" id="fax" columns="14"
							maximumLength="14" converter="#{infraDefs.phoneNumberConverter}" />
					</af:panelForm>
				</afh:rowLayout>
				
				<afh:rowLayout halign="center">
					<af:panelForm rows="2" maxColumns="1" labelWidth="200" width="650">
						<af:panelHeader text="Pay Key and Vendor Number" size="0" />
						<af:inputText label="Pay Key:"
							value="#{createCompany.company.payKey}" columns="20"
							maximumLength="20" id="paykey" />
						<af:inputText label="Vendor Number:"
							value="#{createCompany.company.vendorNumber}" id="vendornumber" columns="10"
							maximumLength="10">
							<af:convertNumber pattern="0000000000"/>
						</af:inputText>	
					</af:panelForm>
				</afh:rowLayout>

				<af:objectSpacer height="10" />
				<afh:rowLayout halign="center">
					<af:panelButtonBar>
						<af:commandButton text="Submit Create Company"
							action="#{createCompany.submitCreateCompany}" />
						<af:commandButton text="Reset" immediate="true"
							actionListener="#{createCompany.resetCreateCompany}">
							<af:resetActionListener/>
						</af:commandButton>
					</af:panelButtonBar>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>
</f:view>

