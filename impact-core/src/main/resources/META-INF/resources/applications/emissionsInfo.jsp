<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Emissions">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form usesUpload="true">
      <af:messages />

      <af:panelForm>
        <af:objectSpacer width="100%" height="15" />
        <af:selectOneChoice label="Pollutant :" id="pollutant"
          unselectedLabel="Please select"
          value="#{applicationDetail.emissions.pollutantCd}"
          showRequired="true">
          <f:selectItems
            value="#{applicationDetail.ptioPollutantDefs}" />
        </af:selectOneChoice>
        <af:inputText label="Pre-Controlled Potential Emissions (tons/yr) :"  id="preCtlPotentialEmissions"
          value="#{applicationDetail.emissions.preCtlPotentialEmissions}"
          columns="10"
          showRequired="true">
          <mu:convertSigDigNumber
            pattern="#{applicationDetail.hapEmissionsValueFormat}" />
        </af:inputText>
        <af:inputText label="Efficiency Standards Potential to Emit (PTE) :"  id="potentialToEmit"
          value="#{applicationDetail.emissions.potentialToEmit}"
          columns="10"
          showRequired="true">
          <mu:convertSigDigNumber
            pattern="#{applicationDetail.hapEmissionsValueFormat}" />
        </af:inputText>
        <af:selectOneChoice id="unitCd" label="Efficiency Standards Units:"
			readOnly="#{! applicationDetail.editMode}"
			value="#{applicationDetail.emissions.unitCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationDetail.paeuPteUnitsDefs.items[(empty applicationDetail.emissions.unitCd ? '' : applicationDetail.emissions.unitCd)]}" />
		</af:selectOneChoice>
        <af:inputText label="Potential to Emit (PTE) (lbs/hr)* :"   id="potentialToEmitLbHr"
          value="#{applicationDetail.emissions.potentialToEmitLbHr}"
          columns="10"
          showRequired="true">
          <mu:convertSigDigNumber
            pattern="#{applicationDetail.hapEmissionsValueFormat}"/>
        </af:inputText>
        <af:inputText label="Potential to Emit (PTE) (tons/yr)* :"   id="potentialToEmitTonYr"
          value="#{applicationDetail.emissions.potentialToEmitTonYr}"
          columns="10"
          showRequired="true">
          <mu:convertSigDigNumber
            pattern="#{applicationDetail.hapEmissionsValueFormat}" />
        </af:inputText>
       <af:selectOneChoice id="AppBasisForDeterminationDropdown" readOnly="#{! applicationDetail.editMode}"
			label="Basis for Determination*" unselectedLabel=" "
			value="#{applicationDetail.emissions.pteDeterminationBasisCd}"
			showRequired="true">
			<f:selectItems
				value="#{applicationDetail.paeuPteDeterminationBasisDefs}" />
		</af:selectOneChoice>
		
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Save"
              rendered="#{applicationDetail.editMode}"
              actionListener="#{applicationDetail.applyEditEmissions}" />
            <af:commandButton text="Cancel"
              rendered="#{applicationDetail.editMode}" immediate="true"
              action="#{applicationDetail.cancelEditEmissions}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

