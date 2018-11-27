<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup partialTriggers="issue delete">
  <af:panelForm>
    <af:inputText id="issuanceId" label="Issuance Id :" readOnly="true"
      value="#{generalIssuance.issuance.issuanceId}" />
    <af:selectOneChoice id="issuanceType" label="Issuance Type :"
      required="true"
      readOnly="#{! generalIssuance.editMode || generalIssuance.oneType}"
      autoSubmit="true" value="#{generalIssuance.issuanceTypeCd}">
      <f:selectItems value="#{generalIssuance.issuanceTypes}" />
    </af:selectOneChoice>
    <af:selectInputDate id="issuanceDate" label="Issuance Date :"
      readOnly="#{! generalIssuance.editMode}"
      value="#{generalIssuance.issuance.issuanceDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
    <af:inputText label="Total Amount :" columns="10"
      value="#{generalIssuance.issuance.feeAmount}"
      rendered="#{generalIssuance.issuance.feeAmount != null}"
      readOnly="true">
      <af:convertNumber type='currency' locale="en-US"
        minFractionDigits="2" />
    </af:inputText>
    <af:inputText label="Public Notice Text :" columns="80" rows="6"
      readOnly="#{! generalIssuance.editMode}"
      partialTriggers="issuanceType"
      inlineStyle="#{!generalIssuance.editMode ? 'border-width: 0px; background-color: #ffffff;' : ''}"
      value="#{generalIssuance.issuance.publicNoticeText}" />

  </af:panelForm>

  <afh:tableLayout>
    <afh:rowLayout>
      <afh:cellFormat partialTriggers="idUploadButton" halign="left"
        valign="top">
        <afh:rowLayout>
          <afh:cellFormat width="150" halign="right" valign="top">
            <af:inputText label="Inactive/Withdrawn Document :"
              id="issuanceDocument" readOnly="true" />
          </afh:cellFormat>
          <afh:cellFormat>
            <h:outputLink
              rendered="#{generalIssuance.issuance.issuanceDoc != null}"
              value="#{generalIssuance.issuance.issuanceDoc.docURL}">
              <af:outputText
                value="#{generalIssuance.issuance.issuanceDoc.description}" />
            </h:outputLink>
            <af:objectSpacer width="10" />
            <af:commandButton text="Upload" useWindow="true"
              windowWidth="500" windowHeight="200" id="idUploadButton"
              rendered="#{generalIssuance.editMode}"
              returnListener="#{generalIssuance.docDialogDone}"
              action="#{generalIssuance.uploadDoc}">
              <t:updateActionListener value="id"
                property="#{generalIssuance.uploadType}" />
            </af:commandButton>
          </afh:cellFormat>
        </afh:rowLayout>
      </afh:cellFormat>
    </afh:rowLayout>
    <afh:rowLayout>
      <afh:cellFormat partialTriggers="alUploadButton" halign="left"
        valign="top">
        <afh:rowLayout>
          <afh:cellFormat width="150" halign="right" valign="top">
            <af:inputText label="Address Label :" id="addressLabel"
              readOnly="true" />
          </afh:cellFormat>
          <afh:cellFormat>
            <h:outputLink
              rendered="#{generalIssuance.issuance.addrLabelDoc != null}"
              value="#{generalIssuance.issuance.addrLabelDoc.docURL}">
              <af:outputText
                value="#{generalIssuance.issuance.addrLabelDoc.description}" />
            </h:outputLink>
            <af:objectSpacer width="10" />
            <af:commandButton text="Upload" useWindow="true"
              windowWidth="500" windowHeight="200" id="alUploadButton"
              rendered="#{generalIssuance.editMode}"
              returnListener="#{generalIssuance.docDialogDone}"
              action="#{generalIssuance.uploadDoc}">
              <t:updateActionListener value="al"
                property="#{generalIssuance.uploadType}" />
            </af:commandButton>
          </afh:cellFormat>
        </afh:rowLayout>
      </afh:cellFormat>
    </afh:rowLayout>

    <afh:rowLayout
      rendered="#{generalIssuance.invoiceDoc != null}">
      <afh:cellFormat halign="left" valign="top">
        <afh:rowLayout>
          <afh:cellFormat width="150" halign="right" valign="top">
            <af:inputText label="Invoice Document :"
              id="invoiceDocument" readOnly="true" />
          </afh:cellFormat>
          <afh:cellFormat>
            <h:outputLink
              value="#{generalIssuance.invoiceDoc.docURL}">
              <af:outputText
                value="#{generalIssuance.invoiceDoc.description}" />
            </h:outputLink>
          </afh:cellFormat>
        </afh:rowLayout>
      </afh:cellFormat>
    </afh:rowLayout>
  </afh:tableLayout>

  <af:objectSpacer height="10" />

  <afh:rowLayout halign="center">
    <af:panelButtonBar>
      <af:commandButton text="Save"
        rendered="#{generalIssuance.editMode}"
        action="#{generalIssuance.save}" />
      <af:commandButton text="Cancel" immediate="true"
        rendered="#{generalIssuance.editMode}"
        actionListener="#{generalIssuance.cancel}">
        <t:updateActionListener value="false"
          property="#{generalIssuance.editMode}" />
      </af:commandButton>

      <af:commandButton text="Issue" id="issue"
        returnListener="#{generalIssuance.issue}"
        rendered="#{generalIssuance.issuanceAllow}"
        action="#{confirmWindow.confirm}" useWindow="true"
        windowWidth="#{confirmWindow.width}"
        windowHeight="#{confirmWindow.height}">
        <t:updateActionListener property="#{confirmWindow.type}"
          value="#{confirmWindow.yesNo}" />
        <t:updateActionListener property="#{confirmWindow.message}"
          value="Please click 'Yes' to complete this action or 'No' to cancel. By clicking 'Yes', you will complete the issuance process. THIS STEP CANNOT BE REVERSED." />
      </af:commandButton>
      <af:commandButton text="Generate Documents"
        rendered="#{generalIssuance.issuanceAllow}"
        action="#{generalIssuance.generateDocs}" />
      <af:commandButton text="Edit"
        rendered="#{generalIssuance.editAllow}"
        action="#{generalIssuance.beginEdit}" />
      <af:commandButton text="Delete" id="delete"
        returnListener="#{generalIssuance.delete}"
        rendered="#{!generalIssuance.issuance.issued && generalIssuance.editAllow}"
        action="#{confirmWindow.confirm}" useWindow="true"
        windowWidth="#{confirmWindow.width}"
        windowHeight="#{confirmWindow.height}">
        <t:updateActionListener property="#{confirmWindow.type}"
          value="#{confirmWindow.yesNo}" />
      </af:commandButton>
      <af:commandButton text="Close"
        rendered="#{!generalIssuance.editMode}"
        actionListener="#{generalIssuance.cancel}">
        <t:updateActionListener value="false"
          property="#{generalIssuance.editMode}" />
      </af:commandButton>
    </af:panelButtonBar>
  </afh:rowLayout>
</af:panelGroup>
