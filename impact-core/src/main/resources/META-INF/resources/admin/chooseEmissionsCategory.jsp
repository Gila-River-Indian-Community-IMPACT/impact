<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Choose Emissions Category" inlineStyle="width:1000px;">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:messages />

      <af:panelForm>
        <af:objectSpacer width="100%" height="15" />
        <af:selectOneChoice label="Pollutant Category :" id="pollutantCategory"
          readOnly="#{!serviceCatalog.editingReport || nonchargeableNcPollutant.deprecated}"
          unselectedLabel="Please select"
          value="#{serviceCatalog.pollutantCategory}">
          <f:selectItems
		  		value="#{facilityReference.pollutantCategoryDefs.items[(empty serviceCatalog.pollutantCategory ? '' : serviceCatalog.pollutantCategory)]}" />
        </af:selectOneChoice>
        <af:objectSpacer height="15" />
       <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="OK"
              rendered="#{serviceCatalog.editingReport}"
              actionListener="#{serviceCatalog.applyAddPollutantByCategory}" />
            <af:commandButton text="Cancel"
              rendered="#{serviceCatalog.editingReport}" immediate="true"
              action="#{serviceCatalog.cancelAddPollutantByCategory}" />
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

