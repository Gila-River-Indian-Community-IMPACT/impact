<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:table id="issuances" emptyText=" " var="issuance" width="98%"
  value="#{generalIssuance.issuances}">

  <af:column headerText="ID" sortProperty="issuanceId" sortable="true"
    width="10%" formatType="text">
    <af:commandLink text="#{issuance.issuanceId}" useWindow="true"
      windowWidth="800" windowHeight="400" action="#{generalIssuance.loadIssuance}">
      <af:setActionListener to="#{generalIssuance.issuanceId}"
        from="#{issuance.issuanceId}" />
    </af:commandLink>
  </af:column>

  <af:column headerText="Type" sortProperty="issuanceTypeCd"
    sortable="true" width="15%" formatType="text">
    <af:selectOneChoice id="issuanceType" label="Issuance Type :"
      required="true" readOnly="true" autoSubmit="true"
      value="#{issuance.issuanceTypeCd}">
      <f:selectItems value="#{generalIssuance.issuanceTypesAll}" />
    </af:selectOneChoice>
  </af:column>

  <af:column headerText="Date" sortProperty="issuanceDate"
    sortable="true" width="15%" formatType="text">
    <af:selectInputDate readOnly="true" value="#{issuance.issuanceDate}" />
  </af:column>

  <af:column headerText="Status" sortProperty="issued"
    sortable="true" formatType="text">
    <af:outputText value="Issued" rendered="#{issuance.issued}" />
    <af:outputText value="Pending" rendered="#{!issuance.issued}" />
  </af:column>

  <af:column headerText="Issuance Document" sortProperty="issuanceDocDesc"
    sortable="true" formatType="text">
    <h:outputLink value="#{issuance.issuanceDoc.docURL}">
      <af:inputText value="#{issuance.issuanceDocDesc}"
        readOnly="true" />
    </h:outputLink>
  </af:column>

  <f:facet name="footer">
    <afh:rowLayout halign="left">
      <af:panelButtonBar>
        <af:commandButton id="CreateIssuanceButton"
          text="Create Issuance" useWindow="true" windowWidth="800"
          windowHeight="400" rendered="#{generalIssuance.issuanceAllow}"
          action="#{generalIssuance.createIssuance}" />
      </af:panelButtonBar>
    </afh:rowLayout>
  </f:facet>
</af:table>
