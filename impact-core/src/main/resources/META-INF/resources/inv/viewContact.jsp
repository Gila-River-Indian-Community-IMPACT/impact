<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Stars2 Invoice Billing Detail">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />

      <af:panelForm>

        <f:subview id="contact">
          <jsp:include flush="true" page="comContact.jsp" />
        </f:subview>

        <af:objectSpacer height="20" />

        <afh:rowLayout halign="center">
          <af:panelButtonBar>
            <af:commandButton text="Edit"
              action="#{invoiceDetail.editContact}"
              rendered="#{! invoiceDetail.editable}" />
            <af:commandButton text="Save"
              action="#{invoiceDetail.saveEditContact}"
              rendered="#{invoiceDetail.editable}" />
            <af:commandButton text="Return"
              action="#{invoiceDetail.closeDialog}"
              rendered="#{! invoiceDetail.editable}" immediate="true" />
            <af:commandButton text="Cancel"
              action="#{invoiceDetail.cancelEditContact}"
              rendered="#{invoiceDetail.editable}" immediate="true" />
          </af:panelButtonBar>
        </afh:rowLayout>

      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

