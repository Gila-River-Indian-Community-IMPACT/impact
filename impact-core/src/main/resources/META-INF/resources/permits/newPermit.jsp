<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<f:view>
  <af:document title="Add permit">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form usesUpload="true">
      <af:page>
        <af:messages />
        <af:panelForm partialTriggers="permitTypeChoice">
          <af:outputFormatted value="<b>Add Old Permit Record</b>" />
          <af:outputFormatted
            value="This permit will be marked 'Legacy', Permit Publication Stage as Issued Final, 
            and Final Issuance status as Issued. Public Notice Status will be set to Issued if the
            checkbox labeled 'Public Notice' below is checked." />
          <af:inputText columns="10" label="Legacy Permit Number"
            value="#{permitSearch.legacyPermitNumber}" />
          <af:selectOneChoice id="permitTypeChoice" autoSubmit="true"
          label="Permit type" required="true"
            value="#{permitSearch.newPermitType}">
            <f:selectItem itemLabel="Please select" itemValue="#{null}" />
            <f:selectItems value="#{permitReference.permitObjectTypes}" />
          </af:selectOneChoice>
          <af:inputText id="permitDesc" label="Permit Description:"
          	columns="50" rows="5" maximumLength="2048"
          	showRequired="true"
		    value="#{permitSearch.newPermitDescription}" />
          <af:selectBooleanCheckbox label="Public Notice: " 
          	value="#{permitSearch.newPemitIssuedDraft}"/>
          <af:selectInputDate id="draftIssueDate" label="Public Notice Date"
          	value="#{permitSearch.newPermitDraftIssueDate}"/>
          <af:selectInputDate id="finalIssueDate" label="Final Issue Date"
          	showRequired="true"
          	value="#{permitSearch.newPermitFinalIssueDate}"/>
          <af:selectInputDate id="effectiveDate" label="Effective Date"
          	value="#{permitSearch.newPermitEffectiveDate}"/>
          <af:selectInputDate id="expirationDate" label="Expiration Date"
          	showRequired="#{permitSearch.newPermitType.second}"
          	value="#{permitSearch.newPermitExpirationDate}"/>
	        
	        <af:inputFile id="draftIssuanceFileTV"
          	label="Public Notice Document"
          	rendered="#{permitSearch.draftFileInfo == null && permitSearch.newPermitType != null && permitSearch.newPermitType.second}"
          	value="#{permitSearch.draftFileToUpload}" />
	      <af:inputText id="draftIssuanceFileNameTV"
	        label="Public Notice Document"
	        rendered="#{permitSearch.draftFileInfo != null && permitSearch.newPermitType != null && permitSearch.newPermitType.second}"
	        value="#{permitSearch.draftFileInfo.fileName}"
	        readOnly="true" />
	        
	       <af:inputFile id="draftIssuanceFileNSR"
          	label="NSR Analysis Document"
          	rendered="#{permitSearch.draftFileInfo == null && permitSearch.newPermitType != null && !permitSearch.newPermitType.second}"
          	value="#{permitSearch.draftFileToUpload}" />
	      <af:inputText id="draftIssuanceFileNameNSR"
	        label="NSR Analysis Document"
	        rendered="#{permitSearch.draftFileInfo != null && permitSearch.newPermitType != null && !permitSearch.newPermitType.second}"
	        value="#{permitSearch.draftFileInfo.fileName}"
	        readOnly="true" />
	        
          <af:inputFile id="finalIssuanceFile"
          	label="Final Issuance Document"
          	rendered="#{permitSearch.finalFileInfo == null}"
          	showRequired="#{permitSearch.newPermitType.second}"
          	value="#{permitSearch.finalFileToUpload}" />
	      <af:inputText id="finalIssuanceFileName"
	        label="Final Issuance Document"
	        rendered="#{permitSearch.finalFileInfo != null}"
	        value="#{permitSearch.finalFileInfo.fileName}"
	        readOnly="true" />
	        
	      <af:inputFile id="statementOfBasisFile"
          	label="Final Statement of Basis Document"
          	rendered="#{permitSearch.sobFileInfo == null && permitSearch.newPermitType.second}"
          	showRequired="#{permitSearch.newPermitType.second}"
          	value="#{permitSearch.sobFileToUpload}" />
	      <af:inputText id="statementOfBasisFileName"
	        label="Final Statement of Basis Document"
	        rendered="#{permitSearch.sobFileInfo != null}"
	        value="#{permitSearch.sobFileInfo.fileName}"
	        readOnly="true" />
          
          <f:facet name="footer">
            <af:panelButtonBar>
              <af:commandButton text="Create"
                actionListener="#{permitSearch.createNewPermit}" />
              <af:commandButton text="Cancel" immediate="true"
                actionListener="#{permitSearch.cancelNewPermit}" />
            </af:panelButtonBar>
          </f:facet>
        </af:panelForm>
      </af:page>
    </af:form>
  </af:document>
</f:view>
