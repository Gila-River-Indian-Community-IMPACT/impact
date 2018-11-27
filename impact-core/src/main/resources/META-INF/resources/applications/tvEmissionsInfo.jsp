<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Title V Potential to Emit">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true" partialTriggers="pteDetBasisTSCheckbox">
      <af:messages />

      <af:panelForm>
        <af:objectSpacer width="100%" height="15" />

        <%
                    /* This popup is intended to be used to add HAP emissions information */
                    /* or to edit HAP or CAP Trade Secret information. Therefore, the     */
                    /* emissionsModify flag indicates that trade secret information is    */
                    /* being edited and information not related to trade secret is made   */
                    /* read only.                                                         */
        %>
        <af:selectOneChoice label="Pollutant :" id="pollutantChoice"
          unselectedLabel="Please select"
          value="#{applicationDetail.tvEmissions.pollutantCd}"
          readOnly="#{applicationDetail.emissionsModify}">
          <f:selectItems value="#{applicationDetail.tvPollutantDefs}" />
        </af:selectOneChoice>

        <af:inputText id="pteText" label="Potential to Emit (PTE) (ton/year) :"
          value="#{applicationDetail.tvEmissions.pteTonsYr}"
          readOnly="#{applicationDetail.emissionsModify}"
          maximumLength="20">
              <mu:convertSigDigNumber pattern="#{applicationDetail.emissionsValueFormat}" />
          </af:inputText>
 		<af:selectOneChoice id="AppBasisForDeterminationDropdown" readOnly="#{applicationDetail.emissionsModify}"
			label="Basis for Determination*" unselectedLabel=" "
			inlineStyle="color: #000000;"
			value="#{applicationDetail.tvEmissions.pteDeterminationBasis}">
			<f:selectItems
				value="#{applicationDetail.patveuPteDeterminationBasisDefs}" />
		</af:selectOneChoice>
            
        <af:objectSpacer height="10"/>

        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Save"
              rendered="#{applicationDetail.editMode}"
              actionListener="#{applicationDetail.applyEditTVEmissions}" />
            <af:commandButton text="Cancel" immediate="true"
              action="#{applicationDetail.cancelEditTVEmissions}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

