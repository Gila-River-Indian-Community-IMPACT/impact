<%@ page session="true" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:panelGroup layout="vertical" rendered="true">
  <af:showDetailHeader text="Attachments" disclosed="true">
    <af:outputText inlineStyle="font-size:75%;color:#666"
      value="#{attachments.subtitle}"></af:outputText>
    <af:table id="DocTab" value="#{attachments.attachmentList}" bandingInterval="1"
      width="98%" banding="row" var="report">

      <af:column sortable="true" sortProperty="documentID" noWrap="true"
         formatType="text" headerText="Attachment ID">
        <af:commandLink 
          action="#{attachments.startEditAttachment}" useWindow="true"
          windowWidth="800" windowHeight="400"
          text="#{report.documentID}"
          rendered="#{(complianceReport.internalApp && !complianceReport.readOnlyUser) || (complianceReport.portalApp && complianceReport.editable)}"
          shortDesc="Click to Delete the attachment or to Edit the attachment description">
          <t:updateActionListener property="#{attachments.document}"
            value="#{report}" />
          <t:updateActionListener property="#{attachments.deletePermitted}" value="true" />
          <t:updateActionListener property="#{attachments.locked}"
          	value="#{complianceReport.complianceReport.submittedDateLong < attachments.tempDoc.lastModifiedTSLong ? false : complianceReport.locked}" />
        </af:commandLink>
        <af:outputText value="#{report.documentID}" 
          rendered="#{(complianceReport.internalApp && complianceReport.readOnlyUser) || (!complianceReport.internalApp && !complianceReport.editable)}"/>
      </af:column>

      <af:column sortable="true" sortProperty="attachmentType"
        rendered="#{attachments.hasDocType}" formatType="text"
        headerText="Attachment Type">
        <af:goLink inlineStyle="clear:none" targetFrame="_blank"
          destination="#{report.docURL}"
          text="#{empty attachments.attachmentTypesDef.itemDesc[report.docTypeCd]?'Unknown':attachments.attachmentTypesDef.itemDesc[report.docTypeCd]}"
          shortDesc="Download document (.#{report.extension})" 
          disabled="#{complianceReport.editMode}" />
      </af:column>
      
      <af:column sortable="true"
        rendered="#{attachments.hasDocType}" sortProperty="description"
        formatType="text" headerText="Description">
        <af:outputText inlineStyle="clear:none" value="#{report.description}"/>
      </af:column>

      <af:column sortable="true"
        rendered="#{!attachments.hasDocType}" sortProperty="description"
        formatType="text" headerText="Description">
        <af:goLink inlineStyle="clear:none" targetFrame="_blank"
          destination="#{report.docURL}" text="#{report.description}"
          disabled="#{complianceReport.editMode}"
          shortDesc="Download document (#{report.extension})" />
      </af:column>
      
	  <% 
	  /*-- We don't have 'Application Trade Secret' user role for Impact system, so we don't need to check the additional appCompViewTS flag as Stars2 system */
      %>
      <af:column headerText="Trade Secret Document"
		rendered="#{attachments.tradeSecretSupported &&  !attachments.publicReadOnlyUser}" formatType="text">
		<af:switcher defaultFacet="noTS"
			facetName="#{(empty report.tradeSecretDocId || !report.tradeSecretAllowed || attachments.publicReadOnlyUser)? 'noTS': 'TS'}">
			<f:facet name="noTS">
				<af:outputText value="None Provided" />
			</f:facet>
					<f:facet name="TS">
						<af:switcher defaultFacet="downloadTSDoc"
							facetName="#{(complianceReport.internalApp)? 'downloadTSDoc' : 'noDownloadTSDoc'}">
							<f:facet name="noDownloadTSDoc">
								<af:goLink id="tradeSecretDocDownloadLink" text="Download"
									disabled="#{complianceReport.editMode}" targetFrame="_blank"
									destination="#{report.tradeSecretDocURL}"
									shortDesc="Download trade secret document (#{(empty report) ? '' : report.extension})" />
							</f:facet>
							<f:facet name="downloadTSDoc">
								<af:commandLink text="Download"
									action="#{attachments.startDownloadTSDoc}"
									disabled="#{complianceReport.editMode}" useWindow="true"
									windowWidth="800" windowHeight="400"
									shortDesc="Download trade secret document (#{(empty report) ? '' : report.extension})">
								</af:commandLink>
							</f:facet>
						</af:switcher>
					</f:facet>
				</af:switcher>
	  </af:column>
      
	  <af:column headerText="Trade Secret Justification"
        rendered="#{attachments.tradeSecretSupported &&  !attachments.publicReadOnlyUser}" formatType="text">
		<af:switcher defaultFacet="noJustification"
			facetName="#{(empty report.tradeSecretJustification || !report.tradeSecretAllowed || attachments.publicReadOnlyUser)? 'noJustification': 'Justification'}">
			<f:facet name="noJustification">
				<af:switcher defaultFacet="noTSDoc"
					facetName="#{(empty report.tradeSecretDocId || !report.tradeSecretAllowed || attachments.publicReadOnlyUser)? 'noTSDoc': 'TSDoc'}">
					<f:facet name="TSDoc">
						<af:commandLink text="Add Justification"
							action="#{attachments.startEditAttachment}" useWindow="true"
							windowWidth="800" windowHeight="400"
							shortDesc="Click to Edit Trade Secret Justification">
							<t:updateActionListener property="#{attachments.document}" value="#{report}" />
							<t:updateActionListener	property="#{attachments.deletePermitted}" value="false" />
							<t:updateActionListener property="#{attachments.locked}" value="#{complianceReport.locked}" />
						</af:commandLink>
					</f:facet>
					<f:facet name="noTSDoc">
						<af:outputText value="N/A" />
					</f:facet>
				</af:switcher>
			</f:facet>
			<f:facet name="Justification">
				<af:commandLink text="View Justification"
					action="#{attachments.startEditAttachment}"
					useWindow="true" windowWidth="800" windowHeight="400"
					shortDesc="Click to Edit Trade Secret Justification">
					<t:updateActionListener property="#{attachments.document}" value="#{report}" />
					<t:updateActionListener	property="#{attachments.deletePermitted}" value="false" />
					<t:updateActionListener property="#{attachments.locked}" value="#{complianceReport.locked}" />
				</af:commandLink>
			</f:facet>
		</af:switcher>
	  </af:column>     
					
      <af:column sortable="true" sortProperty="lastModifiedBy"
        formatType="text" headerText="Uploaded By"
        rendered="#{!complianceReport.publicApp}">
        <af:selectOneChoice readOnly="true"
          value="#{report.lastModifiedBy}">
          <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
        </af:selectOneChoice>
      </af:column>

      <af:column sortable="true" sortProperty="uploadDate"
        formatType="text" headerText="Upload Date">
        <af:selectInputDate readOnly="true" value="#{report.uploadDate}" />
      </af:column>

      <f:facet name="footer">
        <afh:rowLayout halign="center">
          <af:panelButtonBar id="footerButtons">
            <af:commandButton text="Add" useWindow="true" id="addBtn"
              windowWidth="800" windowHeight="400"              
              rendered="#{(complianceReport.internalApp && !complianceReport.readOnlyUser)
              				|| (complianceReport.portalApp && complianceReport.editable)}"
              action="#{attachments.startNewAttachment}" />
            <af:commandButton
              actionListener="#{tableExporter.printTable}"
              onclick="#{tableExporter.onClickScript}"
              text="Printable view" />
            <af:commandButton
              actionListener="#{tableExporter.excelTable}"
              onclick="#{tableExporter.onClickScript}"
              text="Export to excel" />
          </af:panelButtonBar>
        </afh:rowLayout>
      </f:facet>
    </af:table>
    <af:outputText
      inlineStyle="font-size:75%;color:#666"
      rendered="#{attachments.updatePermitted}"
      value="To Delete the attachment, or to Edit attachment description, click in the Attachment ID column." />

  </af:showDetailHeader>

</af:panelGroup>