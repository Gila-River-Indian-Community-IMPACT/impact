<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Facility History search">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Facility History Search">
        <%@ include file="../util/header.jsp"%>
        <afh:rowLayout halign="center">
          <h:panelGrid border="1">

            <af:panelBorder>
              <afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
				<afh:rowLayout halign="left">
					<af:outputFormatted
						value="<b>Locate the facility detail (current and historic) which matches the search criteria.</b>" />
				</afh:rowLayout>
			  </afh:tableLayout>
              <af:showDetailHeader text="Search Criteria"
                disclosed="true">
                <af:panelForm rows="1" width="1000" maxColumns="2">

                  <af:inputText tip="acme%, %acme% *acme*, acme*"
                    columns="40" label="Facility Name"
                    value="#{facilityHistorySearch.searchFacility.name}"
                    valign="top" />

                  <af:inputText tip="acme%, %acme% *acme*, acme*"
                    columns="40" label="Facility Description"
                    value="#{facilityHistorySearch.searchFacility.desc}"
                    valign="top" />

                  <af:inputText tip="street%, %street%, *street*, street*"
                  	columns="40" label="Physical Address 1"
                    value="#{facilityHistorySearch.searchFacility.addressLine1}"
                    valign="top" />

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:objectSpacer width="100%" height="20" />
                    <af:commandButton text="Submit"
                      action="#{facilityHistorySearch.submitSearch}">
                    </af:commandButton>

                    <af:commandButton text="Reset"
                      action="#{facilityHistorySearch.reset}" />

                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">
          <h:panelGrid border="1"
            rendered="#{facilityHistorySearch.hasSearchResults}">
            <af:panelBorder>
              <af:showDetailHeader text="Facility Historical List"
                disclosed="true">

                <af:table value="#{facilityHistorySearch.facilities}"
                  bandingInterval="1" banding="row" var="facility"
                  rows="#{facilityHistorySearch.pageLimit}">
                  <af:column sortProperty="facilityId" formatType="text"
                    sortable="true" noWrap="true"
                    headerText="Facility ID">
                    <af:commandLink
                      action="#{facilityHistorySearch.submitCurrentProfile}"
                      text="#{facility.facilityId}">
                      <t:updateActionListener
                        property="#{facilityHistorySearch.selFacilityId}"
                        value="#{facility.facilityId}" />
                      <t:updateActionListener
                		property="#{menuItem_facProfile.disabled}" value="false" />
                    </af:commandLink>
                  </af:column>
                  <af:column sortProperty="versionId" sortable="true"
                    noWrap="true" formatType="text"
                    headerText="History ID">
                    <af:commandLink
                      rendered="#{facility.versionId == -1}"
                      action="#{facilityProfile.submitProfile}"
                      text="CURRENT">
                      <t:updateActionListener
                        property="#{facilityProfile.fpId}"
                        value="#{facility.fpId}" />
                      <t:updateActionListener
                		property="#{menuItem_facProfile.disabled}" value="false" />
                    </af:commandLink>
                    <af:commandLink
                      rendered="#{facility.versionId != -1}"
                      action="#{facilityProfile.submitProfile}"
                      text="#{facility.fpId}">
                      <t:updateActionListener
                        property="#{facilityProfile.fpId}"
                        value="#{facility.fpId}" />
                      <t:updateActionListener
                		property="#{menuItem_facProfile.disabled}" value="false" />
                    </af:commandLink>
                  </af:column>
                  <af:column sortProperty="name" sortable="true"
                    formatType="text" headerText="Facility Name">
                    <af:outputText value="#{facility.name}" />
                  </af:column>
       			   
		           <af:column sortProperty="cmpId" sortable="true" noWrap="true"
		            formatType="text" headerText="Company ID">
		            <af:commandLink action="#{companyProfile.submitProfile}"
		              text="#{facility.cmpId}">
		              <t:updateActionListener property="#{companyProfile.cmpId}"
		                value="#{facility.cmpId}" />
		              <t:updateActionListener
		                property="#{menuItem_companyProfile.disabled}" value="false" />
		            </af:commandLink>         
		          </af:column>
          
		          <af:column sortProperty="companyName" sortable="true" noWrap="true" formatType="text" headerText="Company Name">
		 				<af:outputText value="#{facility.companyName}" />
		          </af:column>
				          
		          
		          <af:column sortProperty="operatingStatusCd" sortable="true"
		            formatType="text" headerText="Operating">
		            <af:outputText
		              value="#{facilityReference.operatingStatusDefs.itemDesc[(empty facility.operatingStatusCd ? '' : facility.operatingStatusCd)]}" />
		          </af:column>
		          
		          <af:column sortProperty="permitClassCd" sortable="true"
		            formatType="text" headerText="Facility Class">
		            <af:outputText
		              value="#{facilityReference.permitClassDefs.itemDesc[(empty facility.permitClassCd ? '' : facility.permitClassCd)]}" />
		          </af:column>
		          
		          <af:column sortProperty="facilityTypeCd" sortable="true"
		            formatType="text" headerText="Facility Type">
		            <af:outputText
		              value="#{facilityReference.facilityTypeTextDefs.itemDesc[(empty facility.facilityTypeCd ? '' : facility.facilityTypeCd)]}" />
		          </af:column>
		          
		          <af:column sortProperty="countyCd" sortable="true"
		            formatType="text" headerText="County">
		            <af:selectOneChoice value="#{facility.countyCd}"
		              readOnly="true">
		              <f:selectItems value="#{infraDefs.counties}" />
		            </af:selectOneChoice>
		          </af:column>
		                         
		   		  <af:column sortProperty="googleMapsURL" sortable="true"
		            formatType="text" headerText="Lat/Long">            
		            <af:goLink text="#{facility.phyAddr.latlong}"
					targetFrame="_new" rendered="#{not empty facility.googleMapsURL}"
					destination="#{facility.googleMapsURL}" 
					shortDesc="Clicking this will open Google Maps in a separate tab or window."/>
		          </af:column>
                
                  <af:column sortProperty="startDate" formatType="text"
                    sortable="true" headerText="Start Date">
                    <af:selectInputDate value="#{facility.startDate}"
                      readOnly="true" />
                  </af:column>
                  <af:column sortProperty="endDate" formatType="text"
                    sortable="true" headerText="End Date">
                    <af:selectInputDate value="#{facility.endDate}"
                      readOnly="true" />
                  </af:column>

                  <f:facet name="footer">
                    <afh:rowLayout halign="center">
                      <af:panelButtonBar>
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

              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>
