<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Workload Trend">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Workload Trend">
        <%@ include file="../util/header.jsp"%>

		<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
		    <afh:rowLayout halign="left">
                  	<af:panelHorizontal>
                	<af:outputFormatted value="<b>Data source:</b> workflow, permit detail, application detail<br><br>" />
                	<af:objectSpacer width="10" height="5" />
                	<af:commandButton text="Show Explanation" rendered="#{!complianceSearch.showExplain}"
							action="#{complianceSearch.turnOn}"> </af:commandButton>
						<af:commandButton text="Hide Explanation" rendered="#{complianceSearch.showExplain}"
							action="#{complianceSearch.turnOff}"> </af:commandButton>
                      </af:panelHorizontal>
            </afh:rowLayout>
        	<afh:rowLayout rendered="#{complianceSearch.showExplain}" halign="left">
        		<af:outputFormatted  value="Select one of the permit type and reason combinations labeled as <b>Types</b> from the search criteria.  The result will be metrics by month/year with numbers of applications received, permits issued and workflows outstanding.<br><br>"/>
        	</afh:rowLayout>
        </afh:tableLayout>

        <afh:rowLayout halign="center">
	      <af:outputFormatted id="issuedMetricsWarningText"
	      	inlineStyle="color: orange; font-weight: bold;"
	      	value="WARNING: This report does not include any permits issued final prior to June 30, 2008."
	      />
	    </afh:rowLayout>
        
        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="1100">
            <af:panelBorder>
              <af:showDetailHeader
                text="Workload Trend Filter"
                disclosed="true">
                <af:panelForm rows="1" width="800" maxColumns="4">

                  <af:selectManyListbox label="Types :" size="6"
                    value="#{workloadTrendReport.selectedTypes}">
                    <f:selectItems
                      value="#{workloadTrendReport.types}" />
                  </af:selectManyListbox>

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{workloadTrendReport.submit}" />
                    <af:commandButton text="Reset"
                      action="#{workloadTrendReport.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <jsp:include flush="true" page="workloadTrendData.jsp" />

      </af:page>
    </af:form>
  </af:document>
</f:view>
