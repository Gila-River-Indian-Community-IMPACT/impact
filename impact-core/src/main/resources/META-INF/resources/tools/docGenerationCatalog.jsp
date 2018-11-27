<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


<f:view>
  <af:document title="Document Generation">
    <f:facet name="metaContainer">
      <f:verbatim>
        <h:outputText value="#{facilityProfile.refreshStr}" escape="false" />
      </f:verbatim>
    </f:facet>
    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" />
    <f:verbatim></script></f:verbatim><af:form>

    <af:page var="foo" value="#{menuModel.model}"
      title="Document Generation">
      <%@ include file="../util/header.jsp"%>
      
      <af:poll interval="3000" id="progressPoll"
					rendered="#{bulkOperationsCatalog.asyncRunning}" />
	  <af:panelGroup layout="vertical"
					partialTriggers="progressPoll">
      	<af:progressIndicator id="progressid" 
        	value="#{bulkOperationsCatalog.bulkOperation}" 
         	rendered="#{bulkOperationsCatalog.showProgressBar}"
         	partialTriggers="progressPoll">
        	<af:outputFormatted 
           		value="Processing search request..." />
      	</af:progressIndicator>
      </af:panelGroup>

      <afh:rowLayout halign="center">
        <h:panelGrid border="1">
          <af:panelBorder>
            <f:facet name="top">
              <t:outputText value="Each folder contains documents arranged by a specific topic.  Open the appropriate folder and select the document you want by clicking on the title.  You will be prompted with search criteria that will allow you to define the population for which the document(s) will be generated." />
            </f:facet>

            <f:facet name="left">
              <h:panelGrid columns="1" width="500">
                <t:tree2 id="bulkOperation" showRootNode="false"
                  value="#{bulkOperationsCatalog.treeData}" var="node"
                  varNodeToggler="t" clientSideToggle="false">
                  <f:facet name="root">
                    <h:panelGroup>
                      <af:commandMenuItem immediate="true">
                        <t:graphicImage
                          value="/images/folder_open.gif" border="0"
                          rendered="#{t.nodeExpanded}" />
                        <t:graphicImage
                          value="/images/folder_closed.gif" border="0"
                          rendered="#{!t.nodeExpanded}" />
                        <t:outputText value="#{node.description}"
                          style="color:#FFFFFF; background-color:#000000"
                          rendered="#{bulkOperationsCatalog.current == node.identifier}" />
                        <t:outputText value="#{node.description}"
                          rendered="#{bulkOperationsCatalog.current != node.identifier}" />
                        <t:updateActionListener
                          property="#{bulkOperationsCatalog.selectedTreeNode}"
                          value="#{node}" />
                        <t:updateActionListener
                          property="#{bulkOperationsCatalog.current}"
                          value="#{node.identifier}" />
                      </af:commandMenuItem>
                    </h:panelGroup>
                  </f:facet>
                  <f:facet name="group">
                    <h:panelGroup>
                      <af:commandMenuItem immediate="true">
                        <t:graphicImage
                          value="/images/folder_open.gif" border="0"
                          rendered="#{t.nodeExpanded}" />
                        <t:graphicImage
                          value="/images/folder_closed.gif" border="0"
                          rendered="#{!t.nodeExpanded}" />
                        <t:outputText value="#{node.description}"
                          style="color:#FFFFFF; background-color:#000000"
                          rendered="#{bulkOperationsCatalog.current == node.identifier}" />
                        <t:outputText value="#{node.description}"
                          rendered="#{bulkOperationsCatalog.current != node.identifier}" />
                        <t:updateActionListener
                          property="#{bulkOperationsCatalog.selectedTreeNode}"
                          value="#{node}" />
                        <t:updateActionListener
                          property="#{bulkOperationsCatalog.current}"
                          value="#{node.identifier}" />
                      </af:commandMenuItem>
                    </h:panelGroup>
                  </f:facet>
                  <f:facet name="bulkOperation">
                    <h:panelGroup>
                      <af:commandMenuItem immediate="true">
                        <t:graphicImage
                          value="/images/definitions.gif" border="0" />
                        <t:outputText value="#{node.description}"
                          style="color:#FFFFFF; background-color:#000000"
                          rendered="#{bulkOperationsCatalog.current == node.identifier}" />
                        <t:outputText value="#{node.description}"
                          rendered="#{bulkOperationsCatalog.current != node.identifier}" />
                        <t:updateActionListener
                          property="#{bulkOperationsCatalog.selectedTreeNode}"
                          value="#{node}" />
                        <t:updateActionListener
                          property="#{bulkOperationsCatalog.current}"
                          value="#{node.identifier}" />
                      </af:commandMenuItem>
                    </h:panelGroup>
                  </f:facet>
                </t:tree2>
              </h:panelGrid>
            </f:facet>

              <f:facet name="innerLeft">
                <h:panelGrid columns="1" border="1" width="400">
                  <af:panelGroup layout="vertical"
                    rendered="#{bulkOperationsCatalog.selectedTreeNode.type == 'root' }">
                    <af:panelHeader text="Document Generation Search Criteria" />
                  </af:panelGroup>

                  <af:panelGroup layout="vertical"
                    rendered="#{bulkOperationsCatalog.selectedTreeNode.type == 'group' }">
                    <af:panelHeader text="Document Generation Search Criteria" />
                  </af:panelGroup>

                  <af:panelGroup layout="vertical"
                    rendered="#{bulkOperationsCatalog.selectedTreeNode.type == 'bulkOperation' }">

                    <af:panelForm>

                      <af:objectSpacer width="100%" height="15" />

                      <af:selectInputDate label="Set Correspondence Sent Date To"
                        value="#{bulkOperationsCatalog.correspondenceDate}"
                        rendered="#{bulkOperationsCatalog.correspondenceDateEnabled}" 
                        required="true" />

                      <af:panelHeader text="#{bulkOperationsCatalog.bulkSearchType}Search Criteria" />
                      
                      <af:outputFormatted styleUsage="instruction" rendered="#{bulkOperationsCatalog.NSRInvoiceExplanationEnabled}"
							value="<ul>
							Search results will include permits for which all of the following are true:
									<ul>
										<li>The “Billable?” box on the Fee Summary page is checked.</li>
										<li>The permit has been Issued Final or Issued Withdrawal.</li>
										<li>There is no entry in the Fee Summary Charge/Payment History datagrid for Final Invoice.</li>
									</ul>
					  			</ul>" />

                      <af:inputText label="Emissions Inventory Number"
                        value="#{bulkOperationsCatalog.emissionsReportNumber}"
                        rendered="#{bulkOperationsCatalog.emissionsReportNumberEnabled}" />

                      <af:inputText label="Application Number"
                        value="#{bulkOperationsCatalog.applicationNumber}"
                        rendered="#{bulkOperationsCatalog.applicationNumberEnabled}" />

                      <af:inputText label="Permit Number"
                        value="#{bulkOperationsCatalog.permitNumber}"
                        rendered="#{bulkOperationsCatalog.permitNumberEnabled}" />

                      <af:inputText label="Permit Type"
                        value="#{bulkOperationsCatalog.permitType}"
                        rendered="#{bulkOperationsCatalog.permitTypeEnabled}" />

                      <af:inputText label="Permit Reason"
                        value="#{bulkOperationsCatalog.permitReason}"
                        rendered="#{bulkOperationsCatalog.permitReasonEnabled}" />

                      <af:inputText label="Permit Status Code"
                        value="#{bulkOperationsCatalog.permitStatusCd}"
                        rendered="#{bulkOperationsCatalog.permitStatusCdEnabled}" />

                      <af:inputText label="Facility ID"
                        value="#{bulkOperationsCatalog.facilityId}"
                        rendered="#{bulkOperationsCatalog.facilityIdEnabled}" />

                      <af:inputText label="Facility Name"
                        value="#{bulkOperationsCatalog.facilityNm}"
                        rendered="#{bulkOperationsCatalog.facilityNmEnabled}" />

					  <af:selectOneChoice label="Company"
						value="#{bulkOperationsCatalog.companyName}"
						rendered="#{bulkOperationsCatalog.companyNameEnabled}"
						unselectedLabel="">
						<f:selectItems value="#{companySearch.allCompanies}" />
					  </af:selectOneChoice>

					  <af:selectOneChoice label="Facility Type"
						value="#{bulkOperationsCatalog.facilityTypeCd}" unselectedLabel=""
						rendered="#{bulkOperationsCatalog.facilityTypeEnabled}"
						styleClass="FacilityTypeClass x6">
						<f:selectItems value="#{facilityReference.facilityTypeDefs.items[0]}" />
 					  </af:selectOneChoice>  

                      <af:inputText label="Facility Description"
                        value="#{bulkOperationsCatalog.facilityDesc}"
                        rendered="#{bulkOperationsCatalog.facilityDescEnabled}" />

                      <af:selectOneChoice label="Facility Roles"
                        value="#{bulkOperationsCatalog.facilityRole}"
                        rendered="#{bulkOperationsCatalog.facilityRoleEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.facilityRoles}" />
                      </af:selectOneChoice>
                      
                      <af:selectOneChoice label="Company"
						rendered="#{bulkOperationsCatalog.cmpIdEnabled}"
						value="#{bulkOperationsCatalog.cmpId}">
							<f:selectItems value="#{companySearch.allCompanies}"/>
 					  </af:selectOneChoice>

                      <af:selectOneChoice label="District"
                        value="#{bulkOperationsCatalog.doLaa}"
                        rendered="#{bulkOperationsCatalog.doLaaEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.doLaas}" />
                      </af:selectOneChoice>

                      <af:inputText label="City"
                        value="#{bulkOperationsCatalog.city}"
                        rendered="#{bulkOperationsCatalog.cityEnabled}" />

                      <af:selectOneChoice label="County"
                        value="#{bulkOperationsCatalog.county}"
                        rendered="#{bulkOperationsCatalog.countyEnabled}">
                        <f:selectItems value="#{infraDefs.counties}" />
                      </af:selectOneChoice>

                      <af:inputText label="Zip Code"
                        value="#{bulkOperationsCatalog.zipCode}"
                        rendered="#{bulkOperationsCatalog.zipCodeEnabled}" />

                      <af:selectOneChoice
                        label="Permitting Classification"
                        value="#{bulkOperationsCatalog.permittingClassCd}"
                        rendered="#{bulkOperationsCatalog.permittingClassCdEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.permittingClassCds}" />
                      </af:selectOneChoice>

                      <af:selectOneChoice
                        label="Permitting Issuance Stage"
                        value="#{bulkOperationsCatalog.issuanceStageCd}"
                        rendered="#{bulkOperationsCatalog.issuanceStageCdEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.issuanceStageCds}" />
                      </af:selectOneChoice>

                      <af:selectOneChoice
                        label="Permitting Issuance Status"
                        value="#{bulkOperationsCatalog.globalStatusCd}"
                        rendered="#{bulkOperationsCatalog.globalStatusCdEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.globalStatusCds}" />
                      </af:selectOneChoice>

                      <af:selectOneChoice label="Reporting Category"
                        value="#{bulkOperationsCatalog.reportCategoryCd}"
                        rendered="#{bulkOperationsCatalog.reportCategoryEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.reportCategoryCds}" />
                      </af:selectOneChoice>

                      <af:selectOneChoice label="TV Permitting Status"
                        value="#{bulkOperationsCatalog.tvPermitStatus}"
                        rendered="#{bulkOperationsCatalog.tvPermitStatusEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.reportCategoryCds}" />
                      </af:selectOneChoice>

                      <af:selectOneChoice label="Operating Status"
                        value="#{bulkOperationsCatalog.operatingStatus}"
                        rendered="#{bulkOperationsCatalog.operatingStatusEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.operatingStatuses}" />
                      </af:selectOneChoice>

                      <af:selectInputDate label="Start Date"
                        value="#{bulkOperationsCatalog.startDate}"
                        rendered="#{bulkOperationsCatalog.startDateEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                      <af:selectInputDate label="End Date"
                        value="#{bulkOperationsCatalog.endDate}"
                        rendered="#{bulkOperationsCatalog.endDateEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                      <af:selectInputDate label="Install Start Date"
                        value="#{bulkOperationsCatalog.startDate}"
                        rendered="#{bulkOperationsCatalog.installStartDateEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                      <af:selectInputDate label="Install End Date"
                        value="#{bulkOperationsCatalog.endDate}"
                        rendered="#{bulkOperationsCatalog.installEndDateEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                      <af:selectInputDate label="Issuance Start Date"
                        value="#{bulkOperationsCatalog.startDate}"
                        rendered="#{bulkOperationsCatalog.issuanceStartDateEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                      <af:selectInputDate label="Issuance End Date"
                        value="#{bulkOperationsCatalog.endDate}"
                        rendered="#{bulkOperationsCatalog.issuanceEndDateEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                      <af:selectInputDate label="Effective Start Date"
                        value="#{bulkOperationsCatalog.startDate}"
                        rendered="#{bulkOperationsCatalog.effectiveStartDateEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                      <af:selectInputDate label="Effective End Date"
                        value="#{bulkOperationsCatalog.endDate}"
                        rendered="#{bulkOperationsCatalog.effectiveEndDateEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                      <af:selectInputDate label="Balance Start Date"
                        value="#{bulkOperationsCatalog.startDate}"
                        rendered="#{bulkOperationsCatalog.balanceStartDateEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                      <af:selectInputDate label="Balance End Date"
                        value="#{bulkOperationsCatalog.endDate}"
                        rendered="#{bulkOperationsCatalog.balanceEndDateEnabled}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>

                      <af:inputText label="Year"
                        value="#{bulkOperationsCatalog.year}"
                        rendered="#{bulkOperationsCatalog.yearEnabled}" />

                      <af:selectOneChoice label="SIC Codes"
                        value="#{bulkOperationsCatalog.sicCd}"
                        rendered="#{bulkOperationsCatalog.sicCdEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.sicCds}" />
                      </af:selectOneChoice>

                      <af:selectOneChoice label="NAICS Codes"
                        value="#{bulkOperationsCatalog.naicsCd}"
                        rendered="#{bulkOperationsCatalog.naicsCdEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.naicsCds}" />
                      </af:selectOneChoice>

                      <af:selectBooleanCheckbox label="In Attainment"
                        value="#{bulkOperationsCatalog.inAttainment}"
                        rendered="#{bulkOperationsCatalog.inAttainmentEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.inAttainment}" />
                      </af:selectBooleanCheckbox>

                      <af:selectBooleanCheckbox label="Portable"
                        value="#{bulkOperationsCatalog.portable}"
                        rendered="#{bulkOperationsCatalog.portableEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.portable}" />
                      </af:selectBooleanCheckbox>

                      <af:selectBooleanCheckbox label="NESHAP Indicator"
                        value="#{bulkOperationsCatalog.neshapsInd}"
                        rendered="#{bulkOperationsCatalog.neshapsIndEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.neshapsInd}" />
                      </af:selectBooleanCheckbox>

                      <af:selectOneChoice label="NESAPS Subparts"
                        value="#{bulkOperationsCatalog.neshapsSubpart}"
                        rendered="#{bulkOperationsCatalog.neshapsSubpartEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.neshapsSubparts}" />
                      </af:selectOneChoice>

                      <af:selectBooleanCheckbox label="NSPS Indicator"
                        value="#{bulkOperationsCatalog.nspsInd}"
                        rendered="#{bulkOperationsCatalog.nspsIndEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.nspsInd}" />
                      </af:selectBooleanCheckbox>

                      <af:selectOneChoice label="NSPS Subparts"
                        value="#{bulkOperationsCatalog.nspsSubpart}"
                        rendered="#{bulkOperationsCatalog.nspsSubpartEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.nspsSubparts}" />
                      </af:selectOneChoice>

                      <af:selectOneChoice label="Pollutants"
                        value="#{bulkOperationsCatalog.pollutant}"
                        rendered="#{bulkOperationsCatalog.pollutantEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.pollutants}" />
                      </af:selectOneChoice>

                      <af:selectBooleanCheckbox label="MACT Indicator"
                        value="#{bulkOperationsCatalog.mactInd}"
                        rendered="#{bulkOperationsCatalog.mactIndEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.mactInd}" />
                      </af:selectBooleanCheckbox>

                      <af:selectOneChoice label="MACT Codes"
                        value="#{bulkOperationsCatalog.mactCd}"
                        rendered="#{bulkOperationsCatalog.mactCdEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.mactCds}" />
                      </af:selectOneChoice>

                      <af:selectOneChoice label="MACT Subparts"
                        value="#{bulkOperationsCatalog.mactSubpart}"
                        rendered="#{bulkOperationsCatalog.mactSubpartEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.mactSubparts}" />
                      </af:selectOneChoice>

                      <af:selectOneChoice label="Transit Statuses"
                        value="#{bulkOperationsCatalog.transitStatus}"
                        rendered="#{bulkOperationsCatalog.transitStatusEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.transitStatuses}" />
                      </af:selectOneChoice>

                      <af:selectOneChoice label="Portable Group Types"
                        value="#{bulkOperationsCatalog.portableGroupType}"
                        rendered="#{bulkOperationsCatalog.portableGroupTypeEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.portableGroupTypes}" />
                      </af:selectOneChoice>

                      <af:selectOneChoice label="Portable Group Names"
                        value="#{bulkOperationsCatalog.portableGroupName}"
                        rendered="#{bulkOperationsCatalog.portableGroupNameEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.portableGroupNames}" />
                      </af:selectOneChoice>

                      <af:inputText label="EU Description"
                        value="#{bulkOperationsCatalog.euDesc}"
                        rendered="#{bulkOperationsCatalog.euDescEnabled}" />

                      <af:selectOneChoice
                        label="Release Point Type Codes"
                        value="#{bulkOperationsCatalog.egressPointTypeCd}"
                        rendered="#{bulkOperationsCatalog.egressPointTypeEnabled}">
                        <f:selectItems
                          value="#{bulkOperationsCatalog.egressPointTypes}" />
                      </af:selectOneChoice>
                      <af:inputText label="Inspection ID" rendered="#{bulkOperationsCatalog.inspIdEnabled}"
										value="#{bulkOperationsCatalog.inspId}" />

                      <af:objectSpacer height="1" width="400" />
                      <afh:rowLayout halign="center" partialTriggers="progressPoll">
												<af:panelButtonBar
													rendered="#{!bulkOperationsCatalog.readOnlyUser}">
													<af:commandButton text="Submit Search"
														action="#{bulkOperationsCatalog.submitFacilitySearch}"
														rendered="#{bulkOperationsCatalog.bulkFacilitySearch && !bulkOperationsCatalog.notReset}" />
													<af:commandButton text="Submit Search"
														action="#{bulkOperationsCatalog.submitPermitSearch}"
														rendered="#{bulkOperationsCatalog.bulkPermitSearch && !bulkOperationsCatalog.notReset}" />
													<af:commandButton text="Submit Search"
														action="#{bulkOperationsCatalog.submitApplicationSearch}"
														rendered="#{bulkOperationsCatalog.bulkApplicationSearch && !bulkOperationsCatalog.notReset}" />
													<af:commandButton text="Submit Search"
														action="#{bulkOperationsCatalog.submitEmissionsReportSearch}"
														rendered="#{bulkOperationsCatalog.bulkEmissionsReportSearch && !bulkOperationsCatalog.notReset}" />
													<af:commandButton text="Submit Search"
														action="#{bulkOperationsCatalog.submitComplianceReportSearch}"
														rendered="#{bulkOperationsCatalog.bulkComplianceReportSearch && !bulkOperationsCatalog.notReset}" />
														<af:commandButton text="Submit Search"
														action="#{bulkOperationsCatalog.submitInspectionSearch}"
														rendered="#{bulkOperationsCatalog.bulkInspectionSearch && !bulkOperationsCatalog.notReset}" />
													<af:commandButton text="Stop Search"
														action="#{bulkOperationsCatalog.refineSearch}"
														rendered="#{bulkOperationsCatalog.notReset}" />
													<af:commandButton text="Reset"
														action="#{bulkOperationsCatalog.reset}" />
												</af:panelButtonBar>
											</afh:rowLayout>

                    </af:panelForm>

                  </af:panelGroup>
                </h:panelGrid>
              </f:facet>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>
        <af:objectSpacer width="100%" height="15" />
		<af:panelGroup layout="vertical" partialTriggers="progressPoll">
        	<jsp:include flush="true" page="bulkFacilitySearchTable.jsp" />
        	<jsp:include flush="true" page="bulkFacilitySyncSearchTable.jsp" />
        	<jsp:include flush="true" page="bulkPermitSearchTable.jsp" />
        	<jsp:include flush="true" page="bulkApplicationSearchTable.jsp" />
        	<jsp:include flush="true" page="bulkCorrespondenceSearchTable.jsp" />
        	<jsp:include flush="true" page="bulkERSearchTable.jsp" />
        	<jsp:include flush="true" page="bulkComplianceReportSearchTable.jsp" />
        	<jsp:include flush="true" page="bulkInspectionSearchTable.jsp" />
        </af:panelGroup>
        

      </af:page>
    </af:form>
	<f:verbatim><%@ include
		file="../scripts/jquery-1.9.1.min.js"%></f:verbatim>
	<f:verbatim><%@ include
		file="../scripts/FacilityType-Option.js"%></f:verbatim>
  </af:document>
</f:view>

