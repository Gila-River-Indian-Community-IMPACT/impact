<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
  <af:document title="Expiration Report">
  		<f:facet name="metaContainer">
			<f:verbatim>
				<h:outputText value="#{facilityProfile.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:page var="foo" value="#{menuModel.model}"
        title="Expiration Report">
        <%@ include file="../util/header.jsp"%>
        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="600">
            <af:panelBorder>
              <af:showDetailHeader text="Expiration Report Filter"
                disclosed="true">
                <af:panelForm rows="1">
                  <af:selectInputDate label="To Date" showRequired="true"
                    readOnly="#{permitExpirationReport.hasSearchResults}"
                    value="#{permitExpirationReport.toDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                </af:panelForm>
				<af:panelHeader text="Exclude">
	                <af:panelForm>
	                	<af:selectBooleanCheckbox readOnly="#{permitExpirationReport.hasSearchResults}"
	                		value="#{permitExpirationReport.hideShutdownFacility}"
	                		text="Shutdown Facility"/>
	                	<af:selectBooleanCheckbox readOnly="#{permitExpirationReport.hasSearchResults}"
	                		value="#{permitExpirationReport.hideExemptPBR}"
	                		text="Exempt/PBR Facility Permitting Classification"/>
	                	<af:selectBooleanCheckbox readOnly="#{permitExpirationReport.hasSearchResults}"
	                		value="#{permitExpirationReport.hideEUPermitStatusTRS}"
	                		text="Permit EU Status Terminated/Rescinded/Superseded"/>
	                	<af:selectBooleanCheckbox readOnly="#{permitExpirationReport.hasSearchResults}"
	                		value="#{permitExpirationReport.hideEUExemptionDmPe}"
	                		text="Current Facility Inventory EU Exemption Status is 'De Minimis' or 'Permit Exempt'"/>
	                	<af:selectBooleanCheckbox readOnly="#{permitExpirationReport.hasSearchResults}"
	                		value="#{permitExpirationReport.hideEUShutdownInvalid}"
	                		text="Current Facility Inventory EU Operating Status Shutdown/Invalid"/>
	                	<af:selectBooleanCheckbox readOnly="#{permitExpirationReport.hasSearchResults}"
	                		value="#{permitExpirationReport.hidePtoPtioEuActivePBR}"
	                		text="State PTO/PTIO EU is in an 'active' PBR"/>
	                </af:panelForm>
							<afh:rowLayout halign="left">
								<af:panelButtonBar>
									<af:commandButton action="#{permitExpirationReport.selectAll}"
										rendered="#{!permitExpirationReport.hasSearchResults}"
										text="Select All" />
								</af:panelButtonBar>
								<af:objectSpacer height="1" width="6" />
								<af:panelButtonBar>
									<af:commandButton action="#{permitExpirationReport.selectNone}"
										rendered="#{!permitExpirationReport.hasSearchResults}"
										text="Select None" />
								</af:panelButtonBar>
							</afh:rowLayout>
                </af:panelHeader>
                <af:objectSpacer width="100%" height="15" />
                <afh:rowLayout halign="center">
                  <af:panelButtonBar>
                    <af:commandButton text="Submit"
                      action="#{permitExpirationReport.submit}" />
                    <af:commandButton text="Reset" immediate="true"
                      action="#{permitExpirationReport.reset}" />
                    <af:commandButton text="Go Back"
                      rendered="#{permitExpirationReport.hasSummaryResults}"
                      action="#{permitExpirationReport.refresh}" />
                  </af:panelButtonBar>
                </afh:rowLayout>
              </af:showDetailHeader>
            </af:panelBorder>
          </h:panelGrid>
        </afh:rowLayout>
				
				<af:panelGroup layout="vertical"
					rendered="#{permitExpirationReport.showProgressBar}">
					<af:progressIndicator id="progressid"
						value="#{permitExpirationReport}">
						<af:outputFormatted value="Searching for expired permits. " />
					</af:progressIndicator>
				</af:panelGroup>

        <af:objectSpacer width="100%" height="15" />

        <afh:rowLayout halign="center">
          <af:panelGroup layout="horizontal"
            rendered="false">
            <af:panelBorder>
              <afh:rowLayout halign="center">
                <af:panelGroup layout="horizontal">
                  <t:graphicImage
                    url="/util/imageView.jsf?beanName=dolaaSummary&time=#{dolaaSummary.time}"
                    usemap="#dolaaSummary" border="0" />
                  <mu:areaMap beanName="dolaaSummary" />
                  <t:graphicImage
                    url="/util/imageView.jsf?beanName=reasonSummary&time=#{reasonSummary.time}"
                    usemap="#reasonSummary" border="0" />
                  <mu:areaMap beanName="reasonSummary" />
                  <t:graphicImage
                    url="/util/imageView.jsf?beanName=finalSummary&time=#{finalSummary.time}"
                    usemap="#finalSummary" border="0" />
                  <mu:areaMap beanName="finalSummary" />
                </af:panelGroup>
              </afh:rowLayout>
            </af:panelBorder>
          </af:panelGroup>
        </afh:rowLayout>

        <afh:rowLayout halign="center">
          <h:panelGrid border="1" width="600"
            rendered="#{permitExpirationReport.showSummaryTable}">
            <af:panelBorder>
              <af:showDetailHeader text="Expiration Report"
                disclosed="true">
                <af:table bandingInterval="1" banding="row" var="issued"
                  emptyText='No Expiration Permits'
                  rows="#{permitExpirationReport.pageLimit}" width="99%"
                  value="#{permitExpirationReport.details}">

                  <af:column sortProperty="doLaaShortDsc" sortable="true"
                    noWrap="true" headerText="District">
                    <af:panelHorizontal valign="middle" halign="left">
                      <af:inputText readOnly="true"
                        value="#{issued.doLaaShortDsc}" />
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="spto" sortable="true"
                    headerText="State PTO">
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:commandLink text="#{issued.spto}" disabled="#{issued.spto == 0}"
                        action="#{permitExpirationReport.submitDrillDown}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener
                          property="#{permitExpirationReport.doLaaCd}"
                          value="#{issued.doLaaName}" />
                        <t:updateActionListener
                          property="#{permitExpirationReport.type}"
                          value="SPTO.F" />
                      </af:commandLink>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="ptio" sortable="true"
                    headerText="PTIO">
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:commandLink text="#{issued.ptio}" disabled="#{issued.ptio == 0}"
                        action="#{permitExpirationReport.submitDrillDown}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener
                          property="#{permitExpirationReport.doLaaCd}"
                          value="#{issued.doLaaName}" />
                        <t:updateActionListener
                          property="#{permitExpirationReport.type}"
                          value="PTIO.F" />
                      </af:commandLink>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="tv" sortable="true"
                    headerText="TV">
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:commandLink text="#{issued.tv}" disabled="#{issued.tv == 0}"
                        action="#{permitExpirationReport.submitDrillDown}"
                        inlineStyle="white-space: nowrap;">
                        <t:updateActionListener
                          property="#{permitExpirationReport.doLaaCd}"
                          value="#{issued.doLaaName}" />
                        <t:updateActionListener
                          property="#{permitExpirationReport.type}"
                          value="TVPTO.F" />
                      </af:commandLink>
                    </af:panelHorizontal>
                  </af:column>

                  <af:column sortProperty="total" sortable="true"
                    headerText="Total">
                    <af:panelHorizontal valign="middle" halign="center">
                      <af:inputText readOnly="true"
                        value="#{issued.total}" />
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

        <jsp:include flush="true" page="permitExpirationSpto.jsp" />

        <jsp:include flush="true" page="permitExpirationPtio.jsp" />

        <jsp:include flush="true" page="permitExpirationTv.jsp" />

      </af:page>
    </af:form>
  </af:document>
</f:view>
