<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Preliminary Completed">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Preliminary Completed">
        <%@ include file="../util/header.jsp"%>

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
                text="Preliminary Completed Filter"
                disclosed="true">
                <af:panelForm rows="1" width="800" maxColumns="4">

                  <af:selectManyListbox label="Districts :" size="6"
                    value="#{prelimCompleted.selectedDoLaas}">
                    <f:selectItems value="#{prelimCompleted.doLaas}" />
                  </af:selectManyListbox>

                  <af:selectManyListbox label="Types :" size="6"
                    value="#{prelimCompleted.selectedPermitTypes}">
                    <f:selectItems
                      value="#{prelimCompleted.permitTypes}" />
                  </af:selectManyListbox>

                  <af:selectManyListbox label="Reasons :" size="6"
                    value="#{prelimCompleted.selectedReasonCds}">
                    <f:selectItems value="#{prelimCompleted.reasonCds}" />
                  </af:selectManyListbox>

                  <af:panelGroup layout="vertical">
                    <af:outputLabel value="Preliminary Review Completion Date"/>
                    <af:selectInputDate label="From :"
                      value="#{prelimCompleted.fromDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                    <af:selectInputDate label="To :"
                      value="#{prelimCompleted.toDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                  </af:panelGroup>

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{prelimCompleted.submit}" />
                    <af:commandButton text="Reset"
                      action="#{prelimCompleted.reset}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">
          <af:panelGroup layout="horizontal"
            rendered="#{prelimCompleted.hasSearchResults}">
            <af:panelBorder>
              <afh:rowLayout halign="center">
                <af:panelGroup layout="horizontal">
                  <t:graphicImage
                    rendered="#{prelimCompleted.showChartResults}"
                    url="/util/imageView.jsf?beanName=sopDolaaSummary&time=#{sopDolaaSummary.time}"
                    usemap="#sopDolaaSummary" border="0" />
                  <mu:areaMap beanName="sopDolaaSummary" />
                </af:panelGroup>
                <af:panelGroup layout="horizontal">
                  <t:graphicImage
                    rendered="#{prelimCompleted.showChartResults}"
                    url="/util/imageView.jsf?beanName=sopPermitTypeSummary&time=#{sopPermitTypeSummary.time}"
                    usemap="#sopPermitTypeSummary" border="0" />
                  <mu:areaMap beanName="sopPermitTypeSummary" />
                </af:panelGroup>
                <af:panelGroup layout="horizontal">
                  <t:graphicImage
                    rendered="#{prelimCompleted.showChartResults}"
                    url="/util/imageView.jsf?beanName=sopPermitStatusSummary&time=#{sopPermitStatusSummary.time}"
                    usemap="#sopPermitStatusSummary" border="0" />
                  <mu:areaMap beanName="sopPermitStatusSummary" />
                </af:panelGroup>
              </afh:rowLayout>
            </af:panelBorder>
          </af:panelGroup>
        </afh:rowLayout>

        <jsp:include flush="true" page="preliminaryCData.jsp" />

      </af:page>
    </af:form>
  </af:document>
</f:view>
