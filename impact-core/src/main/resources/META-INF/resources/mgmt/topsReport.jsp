<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="TOPS Report">
    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" />
    <f:verbatim></script></f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="TOPS Report">
        <%@ include file="../util/header.jsp"%>
        
        <afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
            <afh:rowLayout halign="left">
                  	<af:panelHorizontal>
                	<af:outputFormatted value="<b>Data source:</b> current facility inventory, permit detail, application detail<br><br>" />
                	<af:objectSpacer width="10" height="5" />
                    </af:panelHorizontal>
            </afh:rowLayout>
        </afh:tableLayout>
        <af:showDetailHeader text="Explanation" disclosed="true">
        	<af:outputFormatted styleUsage="instruction"
            	value="The TOPS report includes specific data required by U.S. EPA to be submitted on a semi-annual basis.  Most of the results are a snapshot of the current status of permits however in items number 4 and 7 the timeliness is calculated based on the specific time frame specified."/>
		</af:showDetailHeader>
        <af:objectSpacer height="10"/>
        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="600">
            <af:panelBorder>
              <af:showDetailHeader text="TOPS Report Filter"
                disclosed="true">
                <af:panelForm rows="1">
                  <af:selectInputDate label="Report Date"
                    value="#{topsReport.reportDt}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{topsReport.submit}" />
                    <af:commandButton text="Reset"
                      action="#{topsReport.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">

          <h:panelGrid border="1" width="1100"
             rendered="#{topsReport.hasTopsResults}">
            <af:panelBorder>

              <af:showDetailHeader text="Region 8 Semiannual Title V Permit Data Report" disclosed="true">
                <af:objectSpacer width="100%" height="15" />

                <af:outputFormatted value="Reporting Period: <b>#{topsReport.startDt}</b> to <b>#{topsReport.endDt}</b>" />
                <af:objectSpacer width="100%" height="15" />

                <af:outputFormatted value="1 - Outstanding Permit Issuance (commitment population)" />
                <af:objectSpacer width="100%" height = "15" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="a - Number of final actions: <b>Not Available</b>" />
                <af:objectSpacer width="100%" height="0" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="b - Total commitment universe: <b>Not Available</b>" />
                <af:objectSpacer width="100%" height="0" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="c - Date commitment completed: " />
                <af:objectSpacer width="100%" height="15" />

                <af:outputFormatted value="2 - Total Current Part 70 Source Universe and Permit Universe" />
                <af:objectSpacer width="100%" height="15" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="a - Number of active part 70 sources that have obtained part 70 permits plus the number of active part 70 sources that have not yet obtained part 70 permits: <b>#{topsReport.topsData.tvSCSCFacilityCount}</b>" />
                <af:objectSpacer width="100%" height="0" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="b - Number of part 70 sources that have applied to obtain a synthetic minor restriction in lieu of a part 70 permit: <b>#{topsReport.topsData.smtvSCSCFacilityCount}</b>" />
                <af:objectSpacer width="100%" height="0" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="c - Total number of current part 70 sources (a + b): <b>#{topsReport.topsData.tvSCSCFacilityCount + topsReport.topsData.smtvSCSCFacilityCount}</b>" />
                <af:objectSpacer width="100%" height="0" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="d - Total number of active part 70 permits issued plus part 70 permits applied for: <b>#{topsReport.topsData.tvFacilityCount}</b>" />
                <af:objectSpacer width="100%" height="15" />

                <af:outputFormatted value="3 - Total Active Part 70 Permits" />
                <af:objectSpacer width="100%" height="15" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="Total number of active part 70 permits: <b>#{topsReport.topsData.tvActivePTOCount}</b>" />
                <af:objectSpacer width="100%" height="15" />

                <af:outputFormatted value="4 - Timeliness of Initial Permits (PART Element)" />
                <af:objectSpacer width="100%" height="15" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="a - Total number of initial part 70 permits issued during the 6 month reporting period: <b>Zero</b>" 
                	rendered="#{topsReport.topsData.tvInitialIssuedCount == 0}"/>
                <af:outputFormatted value="a - Total number of initial part 70 permits issued during the 6 month reporting period: <b>#{topsReport.topsData.tvInitialIssuedCount}</b>" 
                	rendered="#{topsReport.topsData.tvInitialIssuedCount != 0}"/>
                <af:objectSpacer width="100%" height="0" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="b - Number of initial part 70 permits finalized during the 6 month reporting period that were issued within 18 months: <b>Not Applicable</b>"
                	rendered="#{topsReport.topsData.tvInitialIssuedCount == 0}"/>
                <af:outputFormatted value="b - Number of initial part 70 permits finalized during the 6 month reporting period that were issued within 18 months: <b>#{topsReport.topsData.tvOnTimeCount}</b>"
                	rendered="#{topsReport.topsData.tvInitialIssuedCount != 0}"/>
                <af:objectSpacer width="100%" height="15" />

                <af:outputFormatted value="5 - Total Outstanding Initial Part 70 Applications" />
                <af:objectSpacer width="100%" height="15" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="Total number of active initial part 70 applications older than 18 months: <b>#{topsReport.topsData.tvOverTimeCount}</b>" />
                <af:objectSpacer width="100%" height="15" />

                <af:outputFormatted value="6 - Outstanding Renewal Permit Actions" />
                <af:objectSpacer width="100%" height="15" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="a - Total number of expired permits for active part 70 sources: <b>#{topsReport.topsData.tvExpiredCount}</b>" />
                <af:objectSpacer width="100%" height="0" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="b - Total number of active permits with terms extended past 5 years: <b>#{topsReport.topsData.tvExtendedCount}</b>" />
                <af:objectSpacer width="100%" height="15" />

                <af:outputFormatted value="7 - Timeliness of Significant Modifications (PART Element - a and b Only)" />
                <af:objectSpacer width="100%" height="15" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="a - Total number of significant modifications issued during the 6 month reporting period: <b>Zero</b>" 
                	rendered="#{topsReport.topsData.tvSigModIssued == 0}"/>
                <af:outputFormatted value="a - Total number of significant modifications issued during the 6 month reporting period: <b>#{topsReport.topsData.tvSigModIssued}</b>" 
                	rendered="#{topsReport.topsData.tvSigModIssued != 0}"/>
                <af:objectSpacer width="100%" height="0" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="b - Number of significant modifications finalized during the 6 month reporting period that were issued within 18 months: <b>Not Applicable</b>" 
                	rendered="#{topsReport.topsData.tvSigModIssued == 0}"/>
                <af:outputFormatted value="b - Number of significant modifications finalized during the 6 month reporting period that were issued within 18 months: <b>#{topsReport.topsData.tvSigModIssuedIn18Mo}</b>" 
                	rendered="#{topsReport.topsData.tvSigModIssued != 0}"/>
                <af:objectSpacer width="100%" height="0" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="c - Number of significant modifications finalized during the 6 month reporting period that were issued within 9 months: <b>Not Applicable</b>" 
                	rendered="#{topsReport.topsData.tvSigModIssued == 0}"/>
                <af:outputFormatted value="c - Number of significant modifications finalized during the 6 month reporting period that were issued within 9 months: <b>#{topsReport.topsData.tvSigModIssuedIn9Mo}</b>" 
                	rendered="#{topsReport.topsData.tvSigModIssued != 0}"/>
                <af:objectSpacer width="100%" height="15" />

                <af:outputFormatted value="8 - Outstanding Significant Permit Modifications" />
                <af:objectSpacer width="100%" height="15" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="Total number of active significant modification applications older than 18 months: <b>#{topsReport.topsData.tvSigModOutstanding18Mo}</b>" />
                <af:objectSpacer width="100%" height="15" />

                <%-- Not needed by WY
                <af:outputFormatted value="[9 - Reserved for Sending Comments to Region 5]" />
                <af:objectSpacer width="100%" height="15" />

                <af:outputFormatted value="10 - Outstanding Minor Permit Modifications (Region 5 Data Element)" />
                <af:objectSpacer width="100%" height="15" /><af:objectSpacer width="1%" height="0" />
                <af:outputFormatted value="Total number of active minor modification applications older than 90 days: <b>#{topsReport.topsData.tvMinorModOutstanding3Mo}</b>" />
                <af:objectSpacer width="100%" height="15" />--%>

              </af:showDetailHeader>

            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

      </af:page>
    </af:form>
  </af:document>
</f:view>
