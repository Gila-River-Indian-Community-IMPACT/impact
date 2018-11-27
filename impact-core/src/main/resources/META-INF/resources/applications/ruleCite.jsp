<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<af:panelForm partialTriggers="RuleCiteTypeChoice">
  <af:selectOneChoice id="RuleCiteTypeChoice" label="Citation Type :"
    readOnly="#{! applicationDetail.editMode}"
    unselectedLabel="Please select"
    value="#{applicationDetail.tvRuleCiteTypeCd}" autoSubmit="true">
    <f:selectItems value="#{applicationReference.ruleCiteTypeDefs}" />
  </af:selectOneChoice>
  <af:selectOneChoice id="RuleCiteChoice" label="Rule Citation :"
    readOnly="#{! applicationDetail.editMode}"
    unselectedLabel="Please select"
    rendered="#{applicationDetail.ruleCitation}"
    value="#{applicationDetail.tvRuleCiteCd}">
    <f:selectItems value="#{applicationReference.ruleCitationDefs}" />
  </af:selectOneChoice>
  <af:selectOneChoice id="MACTChoice"
    label="Maximum Achievable Control Technology (MACT) :"
    readOnly="#{! applicationDetail.editMode}"
    unselectedLabel="Please select"
    rendered="#{applicationDetail.mactCitation}"
    value="#{applicationDetail.tvRuleCiteCd}">
    <f:selectItems value="#{applicationReference.mactSubpartDefs}" />
  </af:selectOneChoice>
  <af:selectOneChoice id="NESHAPChoice"
    label="National Emission Standards for Hazardous Air Pollutants (NESHAP) :"
    readOnly="#{! applicationDetail.editMode}"
    unselectedLabel="Please select"
    rendered="#{applicationDetail.neshapCitation}"
    value="#{applicationDetail.tvRuleCiteCd}">
    <f:selectItems value="#{applicationReference.neshapSubpartDefs}" />
  </af:selectOneChoice>
  <af:selectOneChoice id="NSPSChoice"
    label="New Source Performance Standards (NSPS) :"
    readOnly="#{! applicationDetail.editMode}"
    unselectedLabel="Please select"
    rendered="#{applicationDetail.nspsCitation}"
    value="#{applicationDetail.tvRuleCiteCd}">
    <f:selectItems value="#{applicationReference.nspsSubpartDefs}" />
  </af:selectOneChoice>
</af:panelForm>

