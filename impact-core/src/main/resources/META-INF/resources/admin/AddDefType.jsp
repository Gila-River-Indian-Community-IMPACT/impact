<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Add Definition Type">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />

      <af:panelForm>
        <af:inputText label="Control Equipment Code:"
          value="#{contEquipCatalog.data.code}"
          rendered="#{contEquipCatalog.newCeType}" columns="4"
          maximumLength="4" />
      </af:panelForm>
      <af:panelForm>
        <af:inputText label="Control Equipment Type:"
          value="#{contEquipCatalog.data.description}"
          rendered="#{contEquipCatalog.newCeType}" columns="40"
          maximumLength="40" />
      </af:panelForm>
      <af:panelForm>
        <af:objectSpacer height="20" />
        <afh:rowLayout halign="center">
          <af:panelButtonBar>
            <af:commandButton text="Add Control Equipment Type"
              action="#{roleTree.addNewRole}"
              rendered="#{!roleTree.newRole}" />
            <af:commandButton text="Save"
              actionListener="#{contEquipCatalog.saveContEquipType}" />
            <af:commandButton text="Cancel" immediate="true"
              actionListener="#{contEquipCatalog.cancelAddContEquipType}" />
          </af:panelButtonBar>
        </afh:rowLayout>
      </af:panelForm>

    </af:form>
  </af:document>
</f:view>
