<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Add Definition">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />

      <af:panelForm>
        <af:inputText label="Code" value="#{defCatalog.data.code}"
          rendered="#{defCatalog.newRecord}" readOnly="true" columns="4"
          maximumLength="4" />
      </af:panelForm>
      <af:panelForm>
        <af:inputText label="Description" required="true"
          value="#{defCatalog.data.description}"
          rendered="#{defCatalog.newRecord}" columns="40"
          maximumLength="40" />
      </af:panelForm>
      <af:panelForm>
        <af:selectBooleanCheckbox rendered="#{defCatalog.newRecord}"
          value="#{defCatalog.data.deprecated}" label="Inactive" />
      </af:panelForm>
      <af:panelForm>
        <af:objectSpacer height="20" />
        <afh:rowLayout halign="center">
          <af:panelButtonBar>
            <af:commandButton text="Save"
              actionListener="#{defCatalog.save}" />
            <af:commandButton text="Cancel" immediate="true"
              actionListener="#{defCatalog.cancel}" />
          </af:panelButtonBar>
        </afh:rowLayout>
      </af:panelForm>

    </af:form>
  </af:document>
</f:view>
