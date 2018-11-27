<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
                                                                                                              
	<af:panelHeader text="Contact Name" size="0" />                     
	<af:panelForm rows="5" maxColumns="2" labelWidth="150" width="600" partialTriggers="companyName" >                      
		<af:selectOneChoice label="Prefix:" value="#{facilityProfile.contact.titleCd}" 
				readOnly="#{! facilityProfile.editable1}">
			<f:selectItems value="#{infraDefs.contactTitles}" />
		</af:selectOneChoice>                      
		<af:inputText label="First Name:" value="#{facilityProfile.contact.firstNm}" 
			columns="40" maximumLength="40" readOnly="#{! facilityProfile.editable1}" 
			id="firstNm" 
			showRequired="#{facilityProfile.contact.companyName == null || facilityProfile.contact.companyName == ''}"/>
		<af:inputText label="Middle Name:" value="#{facilityProfile.contact.middleNm}" 
			columns="40" maximumLength="40" readOnly="#{! facilityProfile.editable1}" id="middleNm" />
		<af:inputText label="Last Name:" value="#{facilityProfile.contact.lastNm}" columns="40" 
			maximumLength="40" readOnly="#{! facilityProfile.editable1}" 
			id="lastNm" 
			showRequired="#{facilityProfile.contact.companyName == null || facilityProfile.contact.companyName == ''}"/>
		<af:inputText label="Suffix:" value="#{facilityProfile.contact.suffixCd}" columns="6" 
			maximumLength="6" readOnly="#{! facilityProfile.editable1}"/>
		<af:inputText label="Company Title:" value="#{facilityProfile.contact.companyTitle}" 
			columns="40" maximumLength="40" readOnly="#{! facilityProfile.editable1}"
			id="companyTitle" showRequired="#{facilityProfile.contact.responsibleOfficial}" />
		<af:inputText label="Contact's Company Name:" value="#{facilityProfile.contact.companyName}" 
			columns="40" maximumLength="40" readOnly="#{! facilityProfile.editable1}" 
			autoSubmit="true" id="companyName"/>			
	</af:panelForm>
	
	<af:panelHeader text="Contact Address" size="0" />		   
	<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600" partialTriggers="loadAddress" >
		<af:inputText label="Address 1:" value="#{facilityProfile.contact.address.addressLine1}" 
			columns="60" maximumLength="100" readOnly="#{! facilityProfile.editable1}"
			id="addressLine1" showRequired="#{!facilityProfile.contact.onSiteContact}"/>
		<af:inputText label="Address 2:" value="#{facilityProfile.contact.address.addressLine2}" 
			columns="60" maximumLength="100" readOnly="#{! facilityProfile.editable1}"/>
	</af:panelForm>
	<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="600" partialTriggers="loadAddress">    
		<af:inputText label="City" value="#{facilityProfile.contact.address.cityName}" columns="30" 
			maximumLength="50" readOnly="#{! facilityProfile.editable1}"
			id="cityName" showRequired="#{!facilityProfile.contact.onSiteContact}" />
		<af:selectOneChoice label="State:" value="#{facilityProfile.contact.address.state}" 
				readOnly="#{! facilityProfile.editable1}" 
				id="state" showRequired="#{!facilityProfile.contact.onSiteContact}" >
			<f:selectItems value="#{infraDefs.states}" />
		</af:selectOneChoice>
		<af:inputText label="Zip Code:" value="#{facilityProfile.contact.address.zipCode}" 
			id="zipCode" readOnly="#{! facilityProfile.editable1}"
			showRequired="#{!facilityProfile.contact.onSiteContact}" />
	</af:panelForm>
    <af:objectSpacer width="100%" height="15"/>
 	<afh:rowLayout halign="center">	
              <af:commandButton id="loadAddress" text="Load Address From Facility"
                                immediate="false" action="#{facilityProfile.loadContactAddress}"
                                rendered="#{facilityProfile.editable1}" />	
	</afh:rowLayout>
	<af:panelHeader text="Contact Phone Numbers" size="0" />  
	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">       
		<af:inputText label="Primary Phone No.:" value="#{facilityProfile.contact.phoneNo}" 
			columns="14" maximumLength="14" readOnly="#{! facilityProfile.editable1}"
			id="phoneNo" showRequired="true"  converter="#{infraDefs.phoneNumberConverter}" />
		<af:inputText label="Primary Ext. No.:" value="#{facilityProfile.contact.phoneExtensionVal}" 
			columns="8" maximumLength="8" readOnly="#{! facilityProfile.editable1}"/>
	</af:panelForm>
	<af:panelForm rows="1" maxColumns="2" labelWidth="150" width="600">       
		<af:inputText label="Secondary Phone No.:" value="#{facilityProfile.contact.secondaryPhoneNo}" 
			id="secondaryPhoneNo" columns="14" maximumLength="14" 
			readOnly="#{! facilityProfile.editable1}"  converter="#{infraDefs.phoneNumberConverter}" />
		<af:inputText label="Secondary Ext. No.:" value="#{facilityProfile.contact.secondaryExtensionVal}" 
			columns="8" maximumLength="8" readOnly="#{! facilityProfile.editable1}"/>
	</af:panelForm>
	<af:panelForm rows="2" maxColumns="2" labelWidth="150" width="600">           
		<af:inputText label="Mobile Phone No.:" value="#{facilityProfile.contact.mobilePhoneNo}" 
			id="mobilePhoneNo" columns="14" maximumLength="14" 
			readOnly="#{! facilityProfile.editable1}"  converter="#{infraDefs.phoneNumberConverter}" />
		<af:inputText label="Fax No.:" value="#{facilityProfile.contact.faxNo}" 
			id="faxNo" columns="14" maximumLength="14" 
			readOnly="#{! facilityProfile.editable1}" converter="#{infraDefs.phoneNumberConverter}" />
		<af:inputText label="Pager No.:" value="#{facilityProfile.contact.pagerNo}"  
			id="pagerNo" columns="14" maximumLength="14" 
			readOnly="#{! facilityProfile.editable1}"  converter="#{infraDefs.phoneNumberConverter}"/>
		<af:inputText label="Pager PIN No.:" value="#{facilityProfile.contact.pagerPinNo}" columns="10" 
			maximumLength="10" readOnly="#{! facilityProfile.editable1}"/>			    
	</af:panelForm>
	
	<af:panelHeader text="Contact Email" size="0" />
	<af:panelForm rows="2" maxColumns="1" labelWidth="150" width="600">
		<af:inputText label="Email:" value="#{facilityProfile.contact.emailAddressTxt}" 
			columns="80" maximumLength="80" readOnly="#{! facilityProfile.editable1}"
			id="emailAddressTxt" />          
	</af:panelForm>