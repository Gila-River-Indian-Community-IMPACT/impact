<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Stars2 Another Report">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />
      <af:panelGroup layout="vertical">
        <af:showDetailHeader text="Enter Pollutant Details"
          disclosed="true">
          <af:panelForm rows="6" width="600">
            <af:selectOneChoice label="Pollutant" value="">
              <f:selectItems value="" />
            </af:selectOneChoice>
            <af:selectOneChoice label="Primary Control" value="">
              <f:selectItems value="" />
            </af:selectOneChoice>
            <af:selectOneChoice label="Secondary Control" value="">
              <f:selectItems value="" />
            </af:selectOneChoice>
            <af:selectOneChoice label="Overall Efficiency Method"
              value="">
              <f:selectItems value="" />
            </af:selectOneChoice>
            <af:inputText label="Control Adjustment Factor" value=""
              columns="1" />
            <af:selectOneChoice label="Emissions Calculation Method"
              value="">
              <f:selectItems value="" />
            </af:selectOneChoice>
            <af:inputText label="Emissions Factor" value="" columns="3" />
            <af:inputText label="Emissions Numerator" value=""
              columns="1" />
            <af:inputText label="Emissions Denominator" value=""
              columns="1" />
            <af:inputText label="Emissions " value="" columns="1" />
            <af:inputText label="Emissions Rate Unit" value=""
              columns="1" />
          </af:panelForm>
          <afh:rowLayout halign="center">
            <af:panelButtonBar>
              <af:commandButton text="Save"
                action="#{reportProfile.saveEmissions}" />
              <af:commandButton text="Cancel"
                action="#{reportProfile.cancelAddEmmissions}" />
            </af:panelButtonBar>
          </afh:rowLayout>
        </af:showDetailHeader>
      </af:panelGroup>
    </af:form>
  </af:document>
</f:view>
