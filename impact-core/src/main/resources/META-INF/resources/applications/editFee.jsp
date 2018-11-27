<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Edit Fee">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page>
        <af:messages />
        <afh:rowLayout halign="center">
          <af:panelGroup>

            <af:panelForm maxColumns="2" rows="1" labelWidth="44%"
              fieldWidth="56%" partialTriggers="adj">
              <af:inputText label="Facility ID:" readOnly="true"
                value="#{applicationDetail.application.facility.facilityId}" />
              <af:inputText label="Real Amount :" columns="10"
                value="#{applicationDetail.realAmount}" readOnly="true">
                <af:convertNumber type='currency' locale="en-US"
                  minFractionDigits="2" />
              </af:inputText>
              <af:inputText label="Other Adjustment :" columns="10"
                id="adj" readOnly="false" autoSubmit="true"
                value="#{applicationDetail.application.otherAdjustment}">
              </af:inputText>

              <af:inputText label="Facility Name:" readOnly="true"
                value="#{applicationDetail.application.facility.name}" />
              <af:inputText label="Total Amount :" columns="10"
                value="#{applicationDetail.totalAmount}" readOnly="true">
                <af:convertNumber type='currency' locale="en-US"
                  minFractionDigits="2" />
              </af:inputText>

            </af:panelForm>

            <af:objectSpacer height="10" />

            <afh:rowLayout halign="center">
              <af:panelButtonBar>
                <af:commandButton text="Save"
                  action="#{applicationDetail.saveFeeChange}" />
                <af:commandButton text="Cancel" immediate="true"
                  action="#{applicationDetail.cancelFeeChange}">
                </af:commandButton>
              </af:panelButtonBar>
            </afh:rowLayout>
          </af:panelGroup>

        </afh:rowLayout>
      </af:page>
    </af:form>
  </af:document>
</f:view>

