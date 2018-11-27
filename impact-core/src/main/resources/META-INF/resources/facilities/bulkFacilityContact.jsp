<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Stars2 Facility Contact Bulk Update">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />

      <af:panelForm>
        <af:showDetailHeader text="Contact Name" disclosed="true">
          <af:panelForm rows="3" maxColumns="2" labelWidth="150"
            width="600">
            <af:selectOneChoice label="Title:"
              value="#{facilityBulk.contact.titleCd}" disabled="false">
              <f:selectItems value="" />
            </af:selectOneChoice>
            <af:inputText label="First Name:"
              value="#{facilityBulk.contact.firstNm}" columns="40"
              maximumLength="40" disabled="false" />
            <af:inputText label="Middle Name:"
              value="#{facilityBulk.contact.middleNm}" columns="40"
              maximumLength="40" disabled="false" />
            <af:inputText label="Last Name:"
              value="#{facilityBulk.contact.lastNm}" columns="40"
              maximumLength="40" disabled="false" />
            <af:selectOneChoice label="Suffix:"
              value="#{facilityBulk.contact.suffixCd}" disabled="false">
              <f:selectItems value="" />
            </af:selectOneChoice>
          </af:panelForm>
        </af:showDetailHeader>
        <af:showDetailHeader text="Contact Address" disclosed="true">
          <af:panelForm rows="2" maxColumns="1" labelWidth="150"
            width="600">
            <af:inputText label="Address 1:"
              value="#{facilityBulk.contact.address.addressLine1}"
              columns="60" maximumLength="100" disabled="false" />
            <af:inputText label="Address 2:"
              value="#{facilityBulk.contact.address.addressLine2}"
              columns="60" maximumLength="100" disabled="false" />
          </af:panelForm>
          <af:panelForm rows="2" maxColumns="2" labelWidth="150"
            width="600">
            <af:inputText label="City"
              value="#{facilityBulk.contact.address.cityName}"
              columns="30" maximumLength="50" disabled="false" />
            <af:selectOneChoice label="State:"
              value="#{facilityBulk.contact.address.state}"
              disabled="false">
              <f:selectItems value="#{infraDefs.states}" />
            </af:selectOneChoice>
            <af:inputText label="Zip Code:"
              value="#{facilityBulk.contact.address.zipCode5}"
              disabled="false" />
          </af:panelForm>
        </af:showDetailHeader>
        <af:showDetailHeader text="Contact Phone Numbers"
          disclosed="true">
          <af:panelForm rows="1" maxColumns="2" labelWidth="150"
            width="600">
            <af:inputText label="Primary Phone No.:"
              value="#{facilityBulk.contact.phoneNo}" columns="20"
              maximumLength="20" disabled="false" />
            <af:inputText label="Primary Ext. No.:"
              value="#{facilityBulk.contact.phoneExtensionVal}"
              columns="8" maximumLength="8" disabled="false" />
          </af:panelForm>
          <af:panelForm rows="1" maxColumns="2" labelWidth="150"
            width="600">
            <af:inputText label="Secondary Phone No.:"
              value="#{facilityBulk.contact.secondaryPhoneNo}"
              columns="20" maximumLength="20" disabled="false" />
            <af:inputText label="Secondary Ext. No.:"
              value="#{facilityBulk.contact.secondaryExtensionVal}"
              columns="8" maximumLength="8" disabled="false" />
          </af:panelForm>
          <af:panelForm rows="2" maxColumns="2" labelWidth="150"
            width="600">
            <af:inputText label="Mobile Phone No.:"
              value="#{facilityBulk.contact.mobilePhoneNo}" columns="20"
              maximumLength="20" disabled="false" />
            <af:inputText label="Fax No.:"
              value="#{facilityBulk.contact.faxNo}" columns="20"
              maximumLength="20" disabled="false" />
            <af:inputText label="Pager No.:"
              value="#{facilityBulk.contact.pagerNo}" columns="20"
              maximumLength="20" disabled="false" />
          </af:panelForm>
        </af:showDetailHeader>
        <af:showDetailHeader text="Contact Email" disclosed="true">
          <af:panelForm rows="2" maxColumns="1" labelWidth="150"
            width="600">
            <af:inputText label="Email:"
              value="#{facilityBulk.contact.emailAddressTxt}"
              columns="60" maximumLength="80" disabled="false" />
          </af:panelForm>
        </af:showDetailHeader>
        <af:showDetailHeader text="Contact Dates" disclosed="true">
          <af:panelForm rows="1" maxColumns="2" labelWidth="150"
            width="600">
            <af:selectInputDate label="Start Date:" id="mdf3"
              value="#{facilityBulk.contact.startDate}" disabled="false" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
            <af:selectInputDate label="End Date:" id="mdf4"
              value="#{facilityBulk.contact.endDate}" disabled="false" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
          </af:panelForm>
        </af:showDetailHeader>
      </af:panelForm>
      <af:panelForm>
        <af:objectSpacer width="100%" height="15" />
        <afh:rowLayout halign="center">
          <af:panelButtonBar>
            <af:commandButton text="Apply"
              actionListener="#{facilityBulk.applyUpdateFacilityContact}" />
            <af:commandButton text="Cancel" immediate="true"
              actionListener="#{facilityBulk.cancelUpdateFacilityContact}" />
          </af:panelButtonBar>
        </afh:rowLayout>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>
