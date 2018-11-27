<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Issued Metrics">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Issued Metrics">
        <%@ include file="../util/header.jsp"%>
        <afh:rowLayout halign="center">
          <h:panelGrid border="1">
            <af:panelBorder>
              <af:showDetailHeader
                text="Search Criteria"
                disclosed="true">
                <af:panelForm rows="1" width="1000" maxColumns="5" partialTriggers="pt">

                  <af:selectManyListbox label="Districts :" size="6"
                    value="#{issuedMetrics.selectedDoLaas}">
                    <f:selectItems value="#{issuedMetrics.doLaas}" />
                  </af:selectManyListbox>
                  
                  <af:selectOneChoice label="Type" id="pt" tip=""
						autoSubmit="TRUE" unselectedLabel=" "
						value="#{issuedMetrics.selectedPermitType}">
						<mu:selectItems value="#{issuedMetrics.permitTypes}" />
				  </af:selectOneChoice>
                  
                  <af:selectManyListbox label="Counties :" size="6"
                    value="#{issuedMetrics.selectedCountyCds}">
                    <f:selectItems value="#{infraDefs.counties}" />
                  </af:selectManyListbox>

                  <af:selectManyListbox label="    Actions :" size="2"
                    value="#{issuedMetrics.selectedPermitActionTypes}">
                    <f:selectItems
                      value="#{issuedMetrics.permitActionTypes}" />
                  </af:selectManyListbox>

                  <af:selectManyListbox label="Reasons :" size="7"
                    value="#{issuedMetrics.selectedReasonCds}">
                    <f:selectItems value="#{issuedMetrics.reasonCds}" />
                  </af:selectManyListbox>
                  
                  <af:selectBooleanCheckbox 
                      shortDesc="Enable show notes may slow down the search with large quantities result."
                      value="#{issuedMetrics.showNotes}"
                      label="Show Notes:"/>

					<af:panelGroup layout="vertical">
					<af:selectOneChoice label="Date Field" id="df"
						autoSubmit="true" unselectedLabel=" "
						value="#{issuedMetrics.dateBy}" readOnly="false">
						<mu:selectItems value="#{issuedMetrics.permitDateBy}" />
					</af:selectOneChoice>
					<af:selectInputDate label="From"
						readOnly="#{issuedMetrics.dateBy == null}"
						value="#{issuedMetrics.fromDate}" partialTriggers="df"
						tip="Search will return results inclusive of this date.">
						<af:validateDateTimeRange minimum="1900-01-01" />
					</af:selectInputDate>
					<af:selectInputDate label="To"
						readOnly="#{issuedMetrics.dateBy == null}"
						value="#{issuedMetrics.toDate}" partialTriggers="df"
						tip="Search will return results inclusive of this date.">
						<af:validateDateTimeRange minimum="1900-01-01" />
					</af:selectInputDate>
					</af:panelGroup>

					<af:panelGroup layout="vertical">
					<af:selectBooleanCheckbox label="Exclude Dead-Ended"
                    shortDesc="Exclude Dead-Ended permits from Not Issued Report Metrics."
                    value="#{issuedMetrics.excludeDeadEnded}"  selected="true"/>
                  
                  <af:selectBooleanCheckbox label="Exclude Issued Withdrawal"
                    shortDesc="Exclude Issued Withdrawal permits from Not Issued Report Metrics."
                    value="#{issuedMetrics.excludeIssuedWithdrawal}" selected="true"/>
                  </af:panelGroup>

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{issuedMetrics.submit}" />
                    <af:commandButton text="Reset"
                      action="#{issuedMetrics.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>
        
        <af:objectSpacer width="100%" height="15" />

        <jsp:include flush="true" page="issuedMetricsExplanation.jsp" />

        <af:objectSpacer width="100%" height="15" />

        <jsp:include flush="true" page="issuedMetricsData.jsp" />
        
        <af:objectSpacer width="100%" height="15" />
        
        <jsp:include flush="true" page="issuedMetricsAnalysis.jsp" />
        
        <af:objectSpacer width="100%" height="15" />

        <jsp:include flush="true" page="notIssuedMetricsData.jsp" />
        
        <af:objectSpacer width="100%" height="15" />

        <jsp:include flush="true" page="notIssuedMetricsAnalysis.jsp" />

      </af:page>
    </af:form>
  </af:document>
</f:view>
