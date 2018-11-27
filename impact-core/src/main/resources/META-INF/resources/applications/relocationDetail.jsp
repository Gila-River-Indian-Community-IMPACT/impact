<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:panelForm
  partialTriggers="requestType futureAddressChoice targetCounty disposition jfoRecommendation">

  <af:selectInputDate label="Date Request Received: " required="true"
    id="mailingDate" value="#{relocation.relocateRequest.receivedDate}"
    rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
    readOnly="#{relocation.readOnly}">
  </af:selectInputDate>

  <af:selectInputDate label="Date Request Submitted: "
    id="submittedDate"
    value="#{relocation.relocateRequest.submittedDate}"
    rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
    readOnly="true">
  </af:selectInputDate>

  <af:selectBooleanCheckbox label="ITR Form Complete :" id="itrForm"
    rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
    readOnly="#{! relocation.editable}"
    value="#{relocation.relocateRequest.formComplete}" />

  <af:selectBooleanCheckbox label="Facility compliant :"
    id="facCompliant"
    rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
    readOnly="#{! relocation.editable}"
    value="#{relocation.relocateRequest.facilityCompliant}" />

  <af:selectBooleanCheckbox label="Site pre-approved :"
    id="sitePreapproved" readOnly="#{relocation.readOnly}"
    rendered="#{relocation.relocateRequest.applicationTypeCD=='' && (relocation.relocateRequest.applicationTypeCD=='RPS' || relocation.relocateRequest.applicationTypeCD=='ITR')}"
    value="#{relocation.relocateRequest.sitePreApproved}" />

  <af:selectOneChoice label="JFO Recommendation: "
    id="jfoRecommendation" readOnly="#{! relocation.editable}"
    autoSubmit="true"
    value="#{relocation.relocateRequest.jfoRecommendation}"
    rendered="#{relocation.relocateRequest.applicationTypeCD!='' && relocation.relocateRequest.applicationTypeCD != 'RPS' }">
    <f:selectItems
      value="#{relocation.relocateRequest.JFODef.items[(empty relocation.relocateRequest.jfoRecommendation ? '' : relocation.relocateRequest.jfoRecommendation)]}" />
  </af:selectOneChoice>

  <af:selectOneChoice label="Request Disposition: " id="disposition"
    readOnly="#{! relocation.editable}"
    rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
    value="#{relocation.relocateRequest.requestDisposition}">
    <f:selectItems
      value="#{relocation.relocateRequest.dispositionDef.items[(empty relocation.relocateRequest.requestDisposition ? '' : relocation.relocateRequest.requestDisposition)]}" />
  </af:selectOneChoice>

  <af:selectOneChoice label="Future Address: " id="futureAddressChoice"
    readOnly="#{! relocation.editable}"
    rendered="#{relocation.relocateToPreApproved}" autoSubmit="true"
    value="#{relocation.preApprovedAddressCd}">
    <f:selectItems value="#{relocation.preApprovedAddressList}" />
  </af:selectOneChoice>

  <af:inputText id="futureAddress"
    label="#{relocation.relocateToPreApproved ? '' : 'Future Address: '}"
    rows="2"
    tip="#{relocation.relocateToPreApproved ? 
              'Please choose a future address from the drop down list above. Only use the text box if the pre-approved address is not included in the drop down list.' : ''}"
    value="#{relocation.relocateRequest.futureAddress}"
    rendered="#{relocation.relocateRequest.applicationTypeCD!=''}"
    columns="50" maximumLength="150" readOnly="#{! relocation.editable}">
  </af:inputText>

  <af:selectOneChoice label="Target County: " id="targetCounty"
    readOnly="#{relocation.readOnly}"
    rendered="#{relocation.relocateRequest.applicationTypeCD!='' && relocation.relocateRequest.applicationTypeCD!='RPS'}"
    value="#{relocation.relocateRequest.targetCountyCd}">
    <f:selectItems value="#{infraDefs.counties}" />
  </af:selectOneChoice>

  <af:inputText id="additionalCond" label="Additional Conditions"
    rows="6" value="#{relocation.relocateRequest.specialText}"
    columns="50" maximumLength="4000" required="false"
    rendered="#{relocation.relocateRequest.applicationTypeCD!='' && relocation.relocateRequest.applicationTypeCD!='RPS' && relocation.relocateRequest.jfoRecommendation=='awc'}"
    readOnly="#{!relocation.editable}">
  </af:inputText>

  <af:inputText id="reasonForDenial" label="Reason for Denial:" rows="6"
    value="#{relocation.relocateRequest.specialText}" columns="50"
    maximumLength="4000" required="false"
    rendered="#{relocation.relocateRequest.applicationTypeCD!='' && relocation.relocateRequest.applicationTypeCD!='RPS' && relocation.relocateRequest.jfoRecommendation=='deny'}"
    readOnly="#{!relocation.editable}">
  </af:inputText>


  <af:goLink
    text="Refer to Engineering Guidelines #44 for denial reasons"
    targetFrame="_blank"
    rendered="#{relocation.relocateRequest.applicationTypeCD!='' && relocation.relocateRequest.applicationTypeCD!='RPS' && relocation.relocateRequest.jfoRecommendation=='deny'}"
    destination="../util/externalReferences.jsf" />

  <af:showDetailHeader text="Issuances" size="2" disclosed="true"
    rendered="#{relocation.relocateRequest.includeIssuance}">
    <jsp:include page="../util/issuancesTable.jsp" flush="true" />
  </af:showDetailHeader>

</af:panelForm>


