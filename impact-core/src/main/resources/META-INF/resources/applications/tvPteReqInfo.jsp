<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Title V PTE Applicable Requirements">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />

      <af:panelForm>
        <af:objectSpacer width="100%" height="15" />
        
        <af:selectOneChoice  id="pollutantChoice" label="Pollutant :"
          unselectedLabel="Please select"
          value="#{applicationDetail.pteRequirement.pollutantCd}">
          <f:selectItems value="#{applicationDetail.altScenarioPtePollutantDefs}" />
        </af:selectOneChoice>

        <af:inputText id="allowableText" label="Allowable :"
          value="#{applicationDetail.pteRequirement.allowable}"
          maximumLength="20">
            <af:convertNumber pattern="###,##0.####" />
          </af:inputText>

        <af:selectOneChoice id="allowableUnitsChoice" label="Units for the Allowable :"
          unselectedLabel="Please select"
          value="#{applicationDetail.pteRequirement.emissionUnitsCd}">
          <f:selectItems value="#{applicationReference.emissionUnitsDefs}" />
        </af:selectOneChoice>

        <af:inputText id="applicableReqText" label="Applicable Requirement :"
          value="#{applicationDetail.pteRequirement.applicableReq}"
          columns="60" rows="4"
          maximumLength="1024" />
            
        <af:objectSpacer height="10"/>

        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Save"
              rendered="#{applicationDetail.editMode}"
              actionListener="#{applicationDetail.applyEditAltSenarioReq}" />
            <af:commandButton text="Cancel" immediate="true"
              action="#{applicationDetail.cancelEditAltSenarioReq}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

