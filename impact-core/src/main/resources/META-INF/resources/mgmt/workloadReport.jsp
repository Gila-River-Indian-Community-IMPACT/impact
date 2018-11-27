<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Workload Report">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Workload Report">
        <%@ include file="../util/header.jsp"%>

		<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
		    <afh:rowLayout halign="left">
				<af:panelHorizontal>
                	<af:outputFormatted value="<b>Data source:</b> workflow, permit detail<br><br>" />
                	<af:objectSpacer width="10" height="5" />
				</af:panelHorizontal>
            </afh:rowLayout>
        </afh:tableLayout>
		<af:showDetailHeader text="Explanation" disclosed="true">
        	<af:outputFormatted styleUsage="instruction"
				value="Click on the Submit button to view the number of permits at a specific task in a workflow on a given user’s To Do list. The data is presented by permit type and issuance stage.  Click on each total to see the individual workflow task data as well as charts detailing statistics on permit reasons and issuance type (if applicable to the data set).<br><br>"/>
        </af:showDetailHeader>	
        <%--<afh:rowLayout halign="center">
          <h:panelGrid border="1" width="600">
            <af:panelBorder>
              <af:showDetailHeader text="Workload Report Filter"
                disclosed="true">
                <af:panelForm rows="1">

                  <af:selectManyListbox label="Districts :" size="6"
                    value="#{workloadReport.selectedDoLaas}">
                    <f:selectItems value="#{workloadReport.doLaas}" />
                  </af:selectManyListbox>

                </af:panelForm>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{workloadReport.submit}" />
                    <af:commandButton text="Reset"
                      action="#{workloadReport.reset}" />
                    <af:commandButton text="Go Back"
                      rendered="#{workloadReport.hasSummaryResults}"
                      action="#{workloadReport.refresh}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>

        <af:objectSpacer width="100%" height="15" />--%>

        <afh:rowLayout halign="center">
          <af:panelGroup layout="horizontal"
            rendered="#{workloadReport.hasSummaryResults}">
            <af:panelBorder>
              <afh:rowLayout halign="center">
                <af:panelGroup layout="horizontal">
                  <%--<t:graphicImage
                    rendered="#{workloadReport.hasDolaaResults}"
                    url="/util/imageView.jsf?beanName=dolaaSummary&time=#{dolaaSummary.time}"
                    usemap="#dolaaSummary" border="0" />
                  <mu:areaMap beanName="dolaaSummary" />
                  <t:graphicImage
                    rendered="#{workloadReport.hasGeneralResults}"
                    url="/util/imageView.jsf?beanName=generalSummary&time=#{generalSummary.time}"
                    usemap="#generalSummary" border="0" />
                  <mu:areaMap beanName="generalSummary" />--%>
                  <t:graphicImage
                    rendered="#{workloadReport.hasReasonResults}"
                    url="/util/imageView.jsf?beanName=reasonSummary&time=#{reasonSummary.time}"
                    usemap="#reasonSummary" border="0" />
                  <mu:areaMap beanName="reasonSummary" />
                  <t:graphicImage
                    rendered="#{workloadReport.hasTotalResults}"
                    url="/util/imageView.jsf?beanName=typeSummary&time=#{typeSummary.time}"
                    usemap="#typeSummary" border="0" />
                  <mu:areaMap beanName="typeSummary" />
                </af:panelGroup>
              </afh:rowLayout>
            </af:panelBorder>
          </af:panelGroup>
        </afh:rowLayout>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" 
            rendered="#{workloadReport.hasSearchResults}">
            <af:panelBorder>
              <af:showDetailHeader text="Workload Report"
                disclosed="true">
                <af:outputText
                  value="Number of tasks"
                  inlineStyle="font-weight:bold;" />
                <af:table bandingInterval="1" banding="row" var="data"
                  emptyText='No Permits'
                  rows="#{workloadReport.pageLimit}" width="99%"
                  value="#{workloadReport.details}">

                  <%--<af:column headerText="" bandingShade="light">
                    <af:column sortProperty="doLaaCdDsp" sortable="true"
                      noWrap="true" headerText="District">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{data.doLaaName}" />
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>--%>
                  
                  <af:column headerText="" bandingShade="light">
                    <af:column sortProperty="userName" sortable="true"
                      noWrap="true" headerText="User">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{data.userName}" />
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column headerText="" bandingShade="light">
                    <af:column sortProperty="activityName"
                      sortable="true" noWrap="true" headerText="Task">
                      <af:panelHorizontal valign="middle" halign="left">
                        <af:inputText readOnly="true"
                          value="#{data.activityName}" />
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>
                  
                  <%--<af:column headerText="PTI" bandingShade="light">
                    <af:column sortProperty="ptiWorkingNow" sortable="true" headerText="Working">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.ptiWorkingNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.TVPTI.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{generalSummary.workloadParams}"
                            value="TVPTI.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="TVPTI.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="TVPTI.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Pti.Working.IP,RF.General.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="ptiDraftNow" sortable="true" headerText="Draft">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.ptiDraftNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.TVPTI.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{generalSummary.workloadParams}"
                            value="TVPTI.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="TVPTI.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="TVPTI.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Pti.Draft.IP,RF.General.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>
                  </af:column> --%>

                  <af:column headerText="NSR" bandingShade="light">
                    <af:column sortProperty="ptioWorkingNow"
                      sortable="true" headerText="Working">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.ptioWorkingNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.NSR.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{generalSummary.workloadParams}"
                            value="NSR.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="NSR.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="NSR.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Ptio.Working.IP,RF.General.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="ptioDraftNow"
                      sortable="true" headerText="Draft">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.ptioDraftNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.NSR.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{generalSummary.workloadParams}"
                            value="NSR.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="NSR.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="NSR.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Ptio.Draft.IP,RF.General.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <%--<af:column headerText="TIV" bandingShade="light">
                    <af:column sortProperty="tivWorkingNow"
                      sortable="true" headerText="Working">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.tivWorkingNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.TIVPTO.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="TIVPTO.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="TIVPTO.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Tiv.Working.IP,RF.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="tivDraftNow"
                      sortable="true" headerText="Draft">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.tivDraftNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.TIVPTO.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="TIVPTO.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="TIVPTO.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Tiv.Draft.IP,RF.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="tivPppNow" sortable="true"
                      headerText="PPP">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.tivPppNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.TIVPTO.PPP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="TIVPTO.PPP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="TIVPTO.PPP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Tiv.Ppp.IP,RF.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="tivPpNow" sortable="true"
                      headerText="PP">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.tivPpNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.TIVPTO.PP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="TIVPTO.PP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="TIVPTO.PP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Tiv.Pp.IP,RF.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>
                  </af:column> --%>

                  <af:column headerText="TV" bandingShade="light">
                    <af:column sortProperty="tvWorkingNow"
                      sortable="true" headerText="Working">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.tvWorkingNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.TVPTO.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="TVPTO.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="TVPTO.N#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Tv.Working.IP,RF.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="tvDraftNow"
                      sortable="true" headerText="Draft">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.tvDraftNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.TVPTO.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="TVPTO.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="TVPTO.D#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Tv.Draft.IP,RF.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <%--<af:column sortProperty="tvPppNow" sortable="true"
                      headerText="PPP">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.tvPppNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.TVPTO.PPP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="TVPTO.PPP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="TVPTO.PPP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Tv.Ppp.IP,RF.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>--%>

                    <af:column sortProperty="tvPpNow" sortable="true"
                      headerText="PP">
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink text="#{data.tvPpNow}"
                          action="#{workloadToDoSearch.submit}"
                          inlineStyle="white-space: nowrap;">
                          <t:updateActionListener
                            property="#{dolaaSummary.workloadParams}"
                            value="WORK.TVPTO.PP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{reasonSummary.workloadParams}"
                            value="TVPTO.PP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                          <t:updateActionListener
                            property="#{workloadToDoSearch.workloadParams}"
                            value="TVPTO.PP#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                          <t:updateActionListener
                            property="#{workloadReport.displayView}"
                            value="#{data.doLaaName}.#{data.activityName}.Tv.Pp.IP,RF.Reason" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column sortable="true" sortProperty="totalNow" headerText="Total" bandingShade="light">
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:commandLink text="#{data.totalNow}"
                        action="#{workloadToDoSearch.submit}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener
                          property="#{dolaaSummary.workloadParams}"
                          value="WORK.TOTAL#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                        <t:updateActionListener
                          property="#{typeSummary.workloadParams}"
                          value="TOTAL#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}#{dolaaSummary.separator}IP,RF" />
                        <t:updateActionListener
                          property="#{workloadToDoSearch.workloadParams}"
                          value="TOTAL#{dolaaSummary.separator}#{data.doLaaCd}#{dolaaSummary.separator}IP,RF#{dolaaSummary.separator}#{data.activityName}#{dolaaSummary.separator}#{data.userId}" />
                        <t:updateActionListener
                          property="#{workloadReport.displayView}"
                          value="#{data.doLaaName}.#{data.activityName}.All.Total.IP,RF" />
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



        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="950"
            rendered="#{workloadReport.hasSummaryResults}">
            <af:panelBorder>
              <af:showDetailHeader
                text="To Do List: #{workloadReport.drillDown}"
                disclosed="true">
                <af:table bandingInterval="1" banding="row" var="todos"
                  emptyText='No todos'
                  rows="#{workloadToDoSearch.pageLimit}" width="99%"
                  value="#{workloadToDoSearch.shortActivities}">

                  <af:column sortProperty="facilityId" sortable="true"
                    noWrap="true">
                    <f:facet name="header">
                      <af:outputText value="Facility ID" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:commandLink text="#{todos.facilityId}"
                        action="#{facilityProfile.submitProfile}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener
                          property="#{facilityProfile.fpId}"
                          value="#{todos.fpId}" />
                        <t:updateActionListener
                          property="#{menuItem_facProfile.disabled}"
                          value="false" />
                      </af:commandLink>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="facilityNm" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Facility Name" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:inputText readOnly="true"
                        value="#{todos.facilityNm}" />
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="processTemplateNm"
                    sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Permit/ Report Type" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:inputText readOnly="true"
                        value="#{todos.processTemplateNm}" />
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="externalId" sortable="true">
                    <f:facet name="header">
                      <af:outputText value="Permit/ Report ID" />
                    </f:facet>
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:inputText readOnly="true"
                        value="#{todos.externalId}" />
                    </af:panelHorizontal>
                  </af:column>

                  <af:column headerText="Task" bandingShade="light">

                    <af:column sortProperty="activityTemplateNm"
                      sortable="true">
                      <f:facet name="header">
                        <af:outputText value="Name" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink
                          action="#{activityProfile.submitProfile}">
                          <af:outputText
                            value="#{todos.activityTemplateNm}" />
                          <t:updateActionListener
                            property="#{menuItem_activityProfile.disabled}"
                            value="false" />
                          <t:updateActionListener
                            property="#{activityProfile.processId}"
                            value="#{todos.processId}" />
                          <t:updateActionListener
                            property="#{workFlow2DDraw.processId}"
                            value="#{todos.processId}" />
                          <t:updateActionListener
                            property="#{activityProfile.loopCnt}"
                            value="#{todos.loopCnt}" />
                          <t:updateActionListener
                            property="#{activityProfile.activityTemplateId}"
                            value="#{todos.activityTemplateId}" />
                          <t:updateActionListener
                            property="#{activityProfile.fromExternal}"
                            value="#{workloadToDoSearch.showList}" />
                          <t:updateActionListener
                            property="#{activityProfile.aggregate}"
                            value="#{todos.aggregate}" />
                          <t:updateActionListener
                            property="#{menuItem_workflowProfile.disabled}"
                            value="false" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="userId" sortable="true">
                      <f:facet name="header">
                        <af:outputText value="Staff Assigned" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:selectOneChoice value="#{todos.userId}"
                          readOnly="true">
                          <f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
                        </af:selectOneChoice>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="activityStatusCd"
                      sortable="true">
                      <f:facet name="header">
                        <af:outputText value="State" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:selectOneChoice
                          value="#{todos.activityStatusCd}"
                          readOnly="true">
                          <f:selectItems
                            value="#{workFlowDefs.activityStatus}" />
                        </af:selectOneChoice>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="startDt" sortable="true">
                      <f:facet name="header">
                        <af:outputText value="Start Date" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:selectInputDate value="#{todos.startDt}"
                          readOnly="true" />

                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="duration" sortable="true">
                      <f:facet name="header">
                        <af:outputText value="Day" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:inputText readOnly="true"
                          value="#{todos.duration}" />
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="dueDt" sortable="true">
                      <f:facet name="header">
                        <af:outputText value="Due Date" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:selectInputDate value="#{todos.dueDt}"
                          readOnly="true" />

                      </af:panelHorizontal>
                    </af:column>
                  </af:column>

                  <af:column>
                    <f:facet name="header">
                      <af:outputText value="Workflow" />
                    </f:facet>

                    <af:column sortProperty="processTemplateNm"
                      sortable="true">
                      <f:facet name="header">
                        <af:outputText value="Type" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:commandLink
                          action="#{workFlow2DDraw.submitProfile}">
                          <af:outputText
                            value="#{todos.processTemplateNm}" />
                          <t:updateActionListener
                            property="#{workFlow2DDraw.processId}"
                            value="#{todos.processId}" />
                          <t:updateActionListener
                            property="#{menuItem_workflowProfile.disabled}"
                            value="false" />
                        </af:commandLink>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="status" sortable="true">
                      <f:facet name="header">
                        <af:outputText value="Status" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <h:panelGrid border="0" width="100%"
                          align="center" bgcolor="#{todos.statusColor}">
                          <af:selectOneChoice label="Status"
                            inlineStyle="font-weight:bold"
                            value="#{todos.status}" readOnly="true">
                            <f:selectItems
                              value="#{workFlowDefs.statusDef}" />
                          </af:selectOneChoice>
                        </h:panelGrid>
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="processStartDt"
                      sortable="true">
                      <f:facet name="header">
                        <af:outputText value="Start Date" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:selectInputDate
                          value="#{todos.processStartDt}"
                          readOnly="true" />
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="processDuration"
                      sortable="true">
                      <f:facet name="header">
                        <af:outputText value="Day" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:inputText readOnly="true"
                          value="#{todos.processDuration}" />
                      </af:panelHorizontal>
                    </af:column>

                    <af:column sortProperty="processDueDt"
                      sortable="true">
                      <f:facet name="header">
                        <af:outputText value="Due Date" />
                      </f:facet>
                      <af:panelHorizontal valign="middle"
                        halign="center">
                        <af:selectInputDate
                          value="#{todos.processDueDt}" readOnly="true" />
                      </af:panelHorizontal>
                    </af:column>
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
        
        <af:objectSpacer width="100%" height="15"/>
        
        <afh:rowLayout halign="center">
        	<af:panelButtonBar>
            	<af:commandButton text="Submit"
                	action="#{workloadReport.submit}" />
               	<af:commandButton text="Reset"
               		action="#{workloadReport.reset}" />
               	<af:commandButton text="Go Back"
               		rendered="#{workloadReport.hasSummaryResults}"
               		action="#{workloadReport.refresh}" />
			</af:panelButtonBar>
		</afh:rowLayout>
        
      </af:page>
    </af:form>
  </af:document>
</f:view>
