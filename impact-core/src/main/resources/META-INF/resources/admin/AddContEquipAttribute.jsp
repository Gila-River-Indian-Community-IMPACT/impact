<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Add Control Equipment Type">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />

      <af:panelForm partialTriggers="datatype">
        <af:inputText label="Attribute Name:"
          value="#{contEquipCatalog.data.dataDetailLbl}" columns="40"
          maximumLength="50" required="true" />
        <af:inputText label="Attribute Description:"
          value="#{contEquipCatalog.data.dataDetailDsc}" columns="40"
          maximumLength="50" required="true" />
        <af:selectOneChoice label="Attribute Data Type" id="datatype"
          autoSubmit="true" value="#{contEquipCatalog.data.dataTypeId}" required="true" >
          <f:selectItems value="#{contEquipCatalog.dataTypes}" />
        </af:selectOneChoice>
        <af:selectOneChoice label="Enumeration Type"
          value="#{contEquipCatalog.data.enumCd}"
          rendered="#{contEquipCatalog.gotEnum}" required="true" >
          <f:selectItems value="#{contEquipCatalog.enumTypes}" />
        </af:selectOneChoice>
        <af:objectSpacer height="20" />
        <afh:rowLayout halign="center">
          <af:panelButtonBar>
            <af:commandButton text="Save"
              action="#{contEquipCatalog.applyAddAttribute}" />
            <af:commandButton text="Cancel" immediate="true"
              action="#{contEquipCatalog.cancelAddContAttribute}" />
          </af:panelButtonBar>
        </afh:rowLayout>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>
