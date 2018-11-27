<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document id="body" title="Emissions Inventory Attachment">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form usesUpload="true">
      <af:messages />
      <af:panelForm partialTriggers="docTypeChoice docTypeChoice1 tradeSecretFile">
        <af:inputText id="descriptionText" label="Description :"
          value="#{erNTVDetail.emissionsDoc.description}" 
          maximumLength="500" rows="4" columns="75"
          readOnly="#{!erNTVDetail.okToEdit}"/>
        <af:selectOneChoice label="Attachment Type :" id="docTypeChoice" rendered="#{erNTVDetail.internalApp}"
          unselectedLabel="Please select" autoSubmit="true"
          value="#{erNTVDetail.emissionsDoc.emissionsDocumentTypeCD}"
          readOnly="#{!erNTVDetail.okToEdit}">
          <f:selectItems
            value="#{facilityReference.emissionsAttachmentTypeDefs.items[(empty erNTVDetail.emissionsDoc.emissionsDocumentTypeCD? '': erNTVDetail.emissionsDoc.emissionsDocumentTypeCD)]}" />
        </af:selectOneChoice>
        <af:selectOneChoice label="Attachment Type :" id="docTypeChoice1" rendered="#{!erNTVDetail.internalApp}"
          unselectedLabel="Please select" autoSubmit="true"
          value="#{erNTVDetail.emissionsDoc.emissionsDocumentTypeCD}"
          readOnly="#{!erNTVDetail.okToEdit}">
          <f:selectItems
            value="#{facilityReference.excludeDAPCEmissionsAttachmentTypeDefs.items[(empty erNTVDetail.emissionsDoc.emissionsDocumentTypeCD? '': erNTVDetail.emissionsDoc.emissionsDocumentTypeCD)]}" />
        </af:selectOneChoice>
        <af:inputFile columns="75" label="Public File to Upload : " 
          id="publicFile"
          rendered="#{erNTVDetail.publicFileInfo == null && !erNTVDetail.docUpdate}"
          value="#{erNTVDetail.fileToUpload}" />
        <af:inputText id="publicFileName" label="Public File to Upload : "
          rendered="#{erNTVDetail.publicFileInfo != null}"
          value="#{erNTVDetail.publicFileInfo.fileName}"
          readOnly="true"/>
        <af:inputFile columns="75" id="tradeSecretFile"
          label="Trade Secret File to Upload :"
          rendered="#{erNTVDetail.tsFileInfo == null && erNTVDetail.tradeSecretVisible &&
            (!erNTVDetail.docUpdate || erNTVDetail.emissionsDoc.tradeSecretReason == null)}"
          value="#{erNTVDetail.tsFileToUpload}"/>
        <af:inputText id="tsFileName" label="Trade Secret File to Upload : "
          rendered="#{erNTVDetail.tsFileInfo != null}"
          value="#{erNTVDetail.tsFileInfo.fileName}"
          readOnly="true"/>
        <af:inputText id="tradeSecretReason"
          label="Trade Secret Justification :" rows="6"
          columns="100" maximumLength="500"
          value="#{erNTVDetail.emissionsDoc.tradeSecretReason}" 
          readOnly="#{!erNTVDetail.okToEdit || !erNTVDetail.tradeSecretVisible}"/>
        <af:objectSpacer width="99%" height="10" />
        <f:facet name="footer">
          <af:panelButtonBar>
            <af:commandButton text="Apply"
              actionListener="#{erNTVDetail.applyEditDoc}" 
              rendered="#{erNTVDetail.okToEdit && !erNTVDetail.readOnlyUser}"/>
            <af:commandButton text="Cancel"
              immediate="true"
              actionListener="#{erNTVDetail.cancelEditDoc}" />
            <af:commandButton text="Delete Attachment"
              actionListener="#{erNTVDetail.removeEditDoc}" 
              rendered="#{erNTVDetail.docUpdate && erNTVDetail.okToEdit && !erNTVDetail.readOnlyUser}"/>
          </af:panelButtonBar>
        </f:facet>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>