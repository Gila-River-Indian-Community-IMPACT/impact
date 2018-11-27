<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<afh:rowLayout halign="center">
  <h:panelGrid border="1" width="1100"
    rendered="#{permitExpirationReport.hasTvResults}">
    <af:panelBorder>
      <af:showDetailHeader text="TV Report: #{permitExpirationReport.drillDown}" disclosed="true">
        <af:table bandingInterval="1" banding="row" var="tvDetails"
          emptyText='No Expired Permits' rows="#{permitExpirationDetailReport.pageLimit}" width="99%"
          value="#{permitExpirationDetailReport.tvDetails}">

  		  <af:column sortProperty="doLaaName" sortable="true"
          	headerText="District">
          	<af:outputText value="#{tvDetails.doLaaName}"/>
          </af:column>

          <af:column sortProperty="facilityId" sortable="true">
            <f:facet name="header">
              <af:outputText value="Facility ID" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{tvDetails.facilityId}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="facilityName" sortable="true">
            <f:facet name="header">
              <af:outputText value="Facility Name" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{tvDetails.facilityName}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="permitClassCd" sortable="true"
            headerText="Permitting Classification">
	          <af:selectOneChoice
				value="#{tvDetails.permitClassCd}"
				id="facPermitClassCd" readOnly="true">
				<f:selectItems
					value="#{facilityReference.permitClassDefs.items[(empty facilityProfile.facility.permitClassCd ? '' : facilityProfile.facility.permitClassCd)]}" />
				</af:selectOneChoice>
		  </af:column>
          
          <af:column sortProperty="permitNumber" sortable="true"
            noWrap="true">
            <f:facet name="header">
              <af:outputText value="Permit Number" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:commandLink text="#{tvDetails.permitNumber}"
                 action="#{permitDetail.loadPermit}" >
                <af:setActionListener to="#{permitDetail.permitID}"
                   from="#{tvDetails.permitId}" />
                <t:updateActionListener property="#{permitDetail.fromTODOList}"
                   value="false" />
              </af:commandLink>
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="reasonDsc" sortable="true">
            <f:facet name="header">
              <af:outputText value="Permit Reason" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="left">
              <af:inputText readOnly="true"
                value="#{tvDetails.reasonDsc}" />
            </af:panelHorizontal>
          </af:column>
          
          <af:column sortProperty="euCount" sortable="true"
          	headerText="EU Count">
          	<af:outputText value="#{tvDetails.euCount}"/>
          </af:column>

          <af:column sortProperty="issuanceDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Issuance Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:inputText readOnly="true"
                value="#{tvDetails.issuanceDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="effectiveDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Effective Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{tvDetails.effectiveDt}" />
            </af:panelHorizontal>
          </af:column>

          <af:column sortProperty="expirationDt" sortable="true">
            <f:facet name="header">
              <af:outputText value="Expiration Date" />
            </f:facet>
            <af:panelHorizontal valign="middle" halign="center">
              <af:selectInputDate readOnly="true" value="#{tvDetails.expirationDt}" />
            </af:panelHorizontal>
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

