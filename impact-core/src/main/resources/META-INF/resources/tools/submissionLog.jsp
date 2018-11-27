<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Gateway Submission Log">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Gateway Submission Log">
        <%@ include file="../util/header.jsp"%>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1">

            <af:panelBorder>
              <af:showDetailHeader text="Search Criteria"
                disclosed="true">
                <af:panelForm rows="2" width="1000" maxColumns="3" >

                  <af:inputText tip="0000000000, 0%, %0%, *0*, 0*"
                    columns="12" label="Facility ID"
                    value="#{submissionLogSearch.searchSubmissionLog.facilityId}"
                    valign="top" />

                  <af:selectOneChoice label="Submission Type"
                    value="#{submissionLogSearch.searchSubmissionLog.submissionType}">
                    <f:selectItems value="#{submissionLogSearch.submissionTypes}"/>
                  </af:selectOneChoice>				  
					
                  <af:inputText tip="acme%, %acme% *acme*, acme*"
                    columns="40" label="Gateway Submission ID"
                    value="#{submissionLogSearch.searchSubmissionLog.gatewaySubmissionId}"
                    valign="top" />
                   
                  <af:inputText tip="acme%, %acme% *acme*, acme*"
                    columns="40" label="Gateway User Name"
                    value="#{submissionLogSearch.searchSubmissionLog.gatewayUserName}"
                    valign="top" /> 
                      
                  <af:selectInputDate label="Begin Date"
					value="#{submissionLogSearch.beginDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
					  
                  <af:selectInputDate label="End Date"
					value="#{submissionLogSearch.endDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate> 
                    
                      
					  
                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:objectSpacer width="100%" height="20" />
                    <af:commandButton text="Submit"
                      action="#{submissionLogSearch.submitSearch}">
                    </af:commandButton>

                    <af:commandButton text="Reset" immediate="true"
                      action="#{submissionLogSearch.reset}" />

                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1"
            rendered="#{submissionLogSearch.hasSearchResults}">
            <af:panelBorder>
              <af:showDetailHeader text="Gateway Submission Log List"
                disclosed="true">

                <af:table value="#{submissionLogSearch.submissionLogs}"
                  bandingInterval="1" banding="row" var="submissionLog"
                  rows="#{submissionLogSearch.pageLimit}">
                  <af:column sortProperty="facilityId" sortable="true"
                    noWrap="true" formatType="text"
                    headerText="Facility Id">
                    <af:commandLink
                      action="#{submissionLogSearch.submitProfile}"
                      text="#{submissionLog.facilityId}">
                      <t:updateActionListener
                        property="#{submissionLogSearch.selFacilityId}"
                        value="#{submissionLog.facilityId}" />
                      <t:updateActionListener
                		property="#{menuItem_facProfile.disabled}" value="false" />
                    </af:commandLink>
                  </af:column>
                  <af:column sortProperty="facilityName" sortable="true"
                    formatType="text" headerText="Facility Name">
                    <af:outputText value="#{submissionLog.facilityName}" />
                  </af:column>
                  <af:column sortProperty="gatewaySubmissionId" sortable="true"
                    formatType="text" headerText="Submission ID">
                    <af:outputText value="#{submissionLog.gatewaySubmissionId}" />
                  </af:column>
                  <af:column sortProperty="submissionType" sortable="true"
                    formatType="text" headerText="Submission Type">
                    <af:outputText value="#{submissionLog.submissionType}" />
                  </af:column>
                  <af:column sortProperty="gatewayUserName" sortable="true"
                    formatType="text" headerText="User Name">
                    <af:outputText value="#{submissionLog.gatewayUserName}" />
                  </af:column>
                  <af:column sortProperty="submissionDt" sortable="true" noWrap="true"
                    formatType="text" headerText="Submission Date">
                    <af:selectInputDate  value="#{submissionLog.submissionDt}" readOnly="true" >
                          <f:convertDateTime dateStyle="full" pattern="MM/dd/yyyy HH:mm:ss"/>
                        </af:selectInputDate>
                  </af:column>
                  
		          <af:column headerText="Attestation Document">
		          	<af:goLink inlineStyle="clear:none" targetFrame="_blank"
		          		destination="#{submissionLog.attestationDoc.docURL}" text="Download"
				        rendered="#{submissionLog.nonROSubmission}"
				        shortDesc="Download document" />
				    <af:outputText value="None Available" rendered="#{!submissionLog.nonROSubmission}"/>
		          </af:column>
		          
                  <f:facet name="footer">
                    <afh:rowLayout halign="center">
                      <af:panelButtonBar>
                        <af:commandButton
                          actionListener="#{tableExporter.printTable}"
                          onclick="#{tableExporter.onClickScriptTS}"
                          text="Printable view" />
                        <af:commandButton
                          actionListener="#{tableExporter.excelTable}"
                          onclick="#{tableExporter.onClickScriptTS}"
                          text="Export to excel" />
                      </af:panelButtonBar>
                    </afh:rowLayout>
                  </f:facet>
                </af:table>

              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>
