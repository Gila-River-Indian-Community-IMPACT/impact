<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document title="Issuance Report">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Issuance Report">
        <%@ include file="../util/header.jsp"%>
        
        <afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
            <afh:rowLayout halign="left">
                  	<af:panelHorizontal>
                	<af:outputFormatted value="<b>Data source:</b> permit detail<<br><br>" />
                	<af:objectSpacer width="10" height="5" />
                	<af:commandButton text="Show Explanation" rendered="#{!complianceSearch.showExplain}"
							action="#{complianceSearch.turnOn}"> </af:commandButton>
						<af:commandButton text="Hide Explanation" rendered="#{complianceSearch.showExplain}"
							action="#{complianceSearch.turnOff}"> </af:commandButton>
                      </af:panelHorizontal>
            </afh:rowLayout>
        	<afh:rowLayout rendered="#{complianceSearch.showExplain}" halign="left">
        		<af:outputFormatted value="Enter a date range to view the number of permits issued during the specified time period.  The data is presented by permit type and issuance stage and broken down by DO/LAA.  A permit will still be counted if it has been terminated since the records represent that a permit was issued.  Click on each total to see the list of specific permits as well as charts detailing statistics on General Permits, permit reasons and issuance stage.<br><br>"/>
        	</afh:rowLayout>
        </afh:tableLayout>
        
        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="600">
            <af:panelBorder>
              <af:showDetailHeader text="Issuance Report Filter"
                disclosed="true">
                <af:panelForm rows="1">
                  <af:selectInputDate label="From Date"
                    showRequired="true"
                    value="#{issuanceReport.fromDate}"
                    readOnly="#{issuanceReport.hasSearchResultsOnly}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                  <af:selectInputDate label="To Date"
                    showRequired="true"
                    value="#{issuanceReport.toDate}"
                    readOnly="#{issuanceReport.hasSearchResultsOnly}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      rendered="#{!issuanceReport.hasSearchResultsOnly}"
                      action="#{issuanceReport.submit}" />
                    <af:commandButton text="Reset"
                      action="#{issuanceReport.reset}" />
                    <af:commandButton text="Go Back"
                      rendered="#{!issuanceReport.hasSearchResults}"
                      action="#{issuanceReport.refresh}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">
          <af:panelGroup layout="horizontal"
            rendered="#{issuanceReport.hasSummaryResults}">
            <af:panelBorder>
              <afh:rowLayout halign="center">
                <af:panelGroup layout="horizontal">
                  <t:graphicImage
                    rendered="#{issuanceReport.hasDolaaResults}"
                    url="/util/imageView.jsf?beanName=dolaaSummary&time=#{dolaaSummary.time}"
                    usemap="#dolaaSummary" border="0" />
                  <mu:areaMap beanName="dolaaSummary" />
                  <t:graphicImage
                    rendered="#{issuanceReport.hasGeneralResults}"
                    url="/util/imageView.jsf?beanName=generalSummary&time=#{generalSummary.time}"
                    usemap="#generalSummary" border="0" />
                  <mu:areaMap beanName="generalSummary" />
                  <t:graphicImage
                    rendered="#{issuanceReport.hasReasonResults}"
                    url="/util/imageView.jsf?beanName=reasonSummary&time=#{reasonSummary.time}"
                    usemap="#reasonSummary" border="0" />
                  <mu:areaMap beanName="reasonSummary" />
                  <t:graphicImage
                    rendered="#{issuanceReport.hasFinalResults}"
                    url="/util/imageView.jsf?beanName=finalSummary&time=#{finalSummary.time}"
                    usemap="#finalSummary" border="0" />
                  <mu:areaMap beanName="finalSummary" />
                  <t:graphicImage
                    rendered="#{issuanceReport.hasTotalResults}"
                    url="/util/imageView.jsf?beanName=typeSummary&time=#{typeSummary.time}"
                    usemap="#typeSummary" border="0" />
                  <mu:areaMap beanName="typeSummary" />
                </af:panelGroup>
              </afh:rowLayout>
            </af:panelBorder>
          </af:panelGroup>
        </afh:rowLayout>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="950"
            rendered="#{issuanceReport.hasSearchResults}">
            <af:panelBorder>
              <af:showDetailHeader text="Issuance Report"
                disclosed="true">
                <af:table bandingInterval="1" banding="row" var="issued"
                  emptyText='No Issued Permits'
                  rows="#{issuanceReport.pageLimit}" width="99%"
                  value="#{issuanceReport.details}">

                  <af:column headerText="" bandingShade="light">
                    <af:column sortProperty="doLaaShortDsc" sortable="true"
                      noWrap="true">
                      <f:facet name="header">
                        <af:outputText value="District" />
                      </f:facet>
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{issued.doLaaShortDsc}" />
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column headerText="PTI" bandingShade="light">
                    <af:column sortProperty="ptiDraft" sortable="false">
                      <f:facet name="header">
                        <af:outputText value="Draft" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.ptiDraft}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.TVPTI.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{generalSummary.params}"
                            value="TVPTI.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{reasonSummary.params}"
                            value="TVPTI.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{finalSummary.params}"
                            value="TVPTI.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="TVPTI.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="TVPTI.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Pti.Dolaa.General.Reason.Draft" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="ptiFinal" sortable="false">
                      <f:facet name="header">
                        <af:outputText value="Final" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.ptiFinal}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.TVPTI.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{generalSummary.params}"
                            value="TVPTI.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{reasonSummary.params}"
                            value="TVPTI.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{finalSummary.params}"
                            value="TVPTI.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="TVPTI.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="TVPTI.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Pti.Dolaa.General.Reason.Final" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column headerText="PTIO" bandingShade="light">
                    <af:column sortProperty="ptioDraft" sortable="false">
                      <f:facet name="header">
                        <af:outputText value="Draft" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.ptioDraft}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.PTIO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{generalSummary.params}"
                            value="PTIO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{reasonSummary.params}"
                            value="PTIO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{finalSummary.params}"
                            value="PTIO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="PTIO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="PTIO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Ptio.Dolaa.General.Reason.Draft" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="ptioFinal" sortable="false">
                      <f:facet name="header">
                        <af:outputText value="Final" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.ptioFinal}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.PTIO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{generalSummary.params}"
                            value="PTIO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{reasonSummary.params}"
                            value="PTIO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{finalSummary.params}"
                            value="PTIO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="PTIO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="PTIO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Ptio.Dolaa.General.Reason.Final" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column headerText="TV" bandingShade="light">
                    <af:column sortProperty="tvDraft" sortable="false">
                      <f:facet name="header">
                        <af:outputText value="Draft" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.tvDraft}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.TVPTO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{generalSummary.params}"
                            value="TVPTO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{reasonSummary.params}"
                            value="TVPTO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{finalSummary.params}"
                            value="TVPTO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="TVPTO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="TVPTO.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Tv.Dolaa.Reason.Draft" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="tvPpp" sortable="false">
                      <f:facet name="header">
                        <af:outputText value="PPP" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.tvPpp}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.TVPTO.PPP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{generalSummary.params}"
                            value="TVPTO.PPP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{reasonSummary.params}"
                            value="TVPTO.PPP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{finalSummary.params}"
                            value="TVPTO.PPP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="TVPTO.PPP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="TVPTO.PPP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Tv.Dolaa.Reason.Ppp" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="tvPp" sortable="false">
                      <f:facet name="header">
                        <af:outputText value="PP" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.tvPp}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.TVPTO.PP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{generalSummary.params}"
                            value="TVPTO.PP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{reasonSummary.params}"
                            value="TVPTO.PP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{finalSummary.params}"
                            value="TVPTO.PP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="TVPTO.PP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="TVPTO.PP/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Tv.Dolaa.Reason.Pp" />
                        </af:commandLink>
                      </af:panelHorizontal>

                    </af:column>

                    <af:column sortProperty="tvFinal" sortable="false">
                      <f:facet name="header">
                        <af:outputText value="Final" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.tvFinal}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.TVPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{generalSummary.params}"
                            value="TVPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{reasonSummary.params}"
                            value="TVPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{finalSummary.params}"
                            value="TVPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="TVPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="TVPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Tv.Dolaa.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column headerText="Total" bandingShade="light">
                    <af:column sortProperty="totalPti" sortable="false">
                      <f:facet name="header">
                        <af:outputText value="PTI" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.totalPti}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.TVPTI/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="TVPTI/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="TVPTI/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Pti.Dolaa.Total" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="totalPtio" sortable="false">
                      <f:facet name="header">
                        <af:outputText value="PTIO" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.totalPtio}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.PTIO/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="PTIO/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="PTIO/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Ptio.Dolaa.Total" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="totalTv" sortable="false">
                      <f:facet name="header">
                        <af:outputText value="TV" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.totalTv}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.TVPTO/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="TVPTO/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="TVPTO/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Tv.Dolaa.Total" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="totalPbr" sortable="false"
                      rendered="false">
                      <f:facet name="header">
                        <af:outputText value="PBR" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{issued.totalPbr}"
                          action="#{dolaaSummary.submitProfile}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.params}"
                            value="ISSUANCE.PBR/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{typeSummary.params}"
                            value="PBR/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceDetailReport.params}"
                            value="PBR/#{issued.doLaaName}/#{issuanceReport.dates}" />
                          <t:updateActionListener
                            property="#{issuanceReport.displayView}"
                            value="#{issued.doLaaName}.Pbr.Dolaa.Total" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column headerText="PBR" bandingShade="light"
                    sortProperty="pbr" sortable="false">
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:commandLink text="#{issued.pbr}"
                        action="#{dolaaSummary.submitProfile}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener
                          property="#{dolaaSummary.params}"
                          value="ISSUANCE.PBR.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{generalSummary.params}"
                          value="PBR.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{reasonSummary.params}"
                          value="PBR.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{finalSummary.params}"
                          value="PBR.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{typeSummary.params}"
                          value="PBR.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{issuanceDetailReport.params}"
                          value="PBR.D/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{issuanceReport.displayView}"
                          value="#{issued.doLaaName}.Pbr.Dolaa.Draft" />
                      </af:commandLink>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column headerText="SPTO" bandingShade="light"
                    sortProperty="spto" sortable="false">
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:commandLink text="#{issued.spto}"
                        disabled="true"
                        action="#{dolaaSummary.submitProfile}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener
                          property="#{dolaaSummary.params}"
                          value="ISSUANCE.SPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{generalSummary.params}"
                          value="SPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{reasonSummary.params}"
                          value="SPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{finalSummary.params}"
                          value="SPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{typeSummary.params}"
                          value="SPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{issuanceDetailReport.params}"
                          value="SPTO.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{issuanceReport.displayView}"
                          value="#{issued.doLaaName}.SPTO.Dolaa.Final" />
                      </af:commandLink>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column headerText="REG" bandingShade="light"
                    sortProperty="reg" sortable="false">
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:commandLink text="#{issued.reg}"
                        disabled="true"
                        action="#{dolaaSummary.submitProfile}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener
                          property="#{dolaaSummary.params}"
                          value="ISSUANCE.REG.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{generalSummary.params}"
                          value="REG.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{reasonSummary.params}"
                          value="REG.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{finalSummary.params}"
                          value="REG.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{typeSummary.params}"
                          value="REG.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{issuanceDetailReport.params}"
                          value="REG.F/#{issued.doLaaName}/#{issuanceReport.dates}" />
                        <t:updateActionListener
                          property="#{issuanceReport.displayView}"
                          value="#{issued.doLaaName}.REG.Dolaa.Fianl" />
                      </af:commandLink>
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

        <jsp:include flush="true" page="issuancePti.jsp" />

        <jsp:include flush="true" page="issuancePtio.jsp" />

        <jsp:include flush="true" page="issuanceTv.jsp" />

        <jsp:include flush="true" page="issuancePbr.jsp" />

      </af:page>
    </af:form>
  </af:document>
</f:view>
