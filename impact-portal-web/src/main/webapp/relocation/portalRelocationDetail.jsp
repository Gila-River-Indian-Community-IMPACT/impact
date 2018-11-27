<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:panelForm partialTriggers="futureAddressChoice">

  <af:selectOneChoice label="Future Address: " id="futureAddressChoice"
    readOnly="#{relocation.readOnly}"
    rendered="#{relocation.relocateToPreApproved}" autoSubmit="true"
    value="#{relocation.preApprovedAddressCd}">
    <f:selectItems value="#{relocation.preApprovedAddressList}" />
  </af:selectOneChoice>

  <af:inputText id="futureAddress"
    label="#{relocation.relocateToPreApproved ? '' : 'Future Address: '}"
    rows="2"
    tip="#{relocation.relocateToPreApproved ? 
              'Please choose a future address from the drop down list above. Only use the text box if the pre-approved address is not included in the drop down list.' : ''}"
    value="#{relocation.portalRelReq.futureAddress}"
    rendered="#{relocation.portalRelReq.applicationTypeCD!=''}"
    columns="50" maximumLength="150" readOnly="#{! relocation.editable}">
  </af:inputText>

  <af:selectOneChoice label="Future County: " id="targetCounty"
    readOnly="#{! relocation.editable}"
    rendered="#{relocation.portalRelReq.applicationTypeCD!='' && relocation.portalRelReq.applicationTypeCD!='RPS'}"
    value="#{relocation.portalRelReq.targetCountyCd}">
    <f:selectItems value="#{infraDefs.counties}" />
  </af:selectOneChoice>

</af:panelForm>
