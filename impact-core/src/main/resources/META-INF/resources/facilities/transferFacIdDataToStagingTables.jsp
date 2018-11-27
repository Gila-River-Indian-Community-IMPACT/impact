<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Transfer FACID Data to EPA Staging Tables">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}"
				title="Transfer FACID Data to EPA Staging Tables">
				
					<%@ include file="../util/header.jsp"%>
	
					<af:panelGroup layout="vertical"
						rendered="#{transferFacIdData.transferInProgress}">
						<af:poll interval="3500" id="transferFacIdDataPoll" />
						<af:panelGroup layout="vertical" partialTriggers="transferFacIdDataPoll">
							<af:progressIndicator id="progressid" value="#{transferFacIdData}" 
								action="#{transferFacIdData.complete}">
								<af:outputFormatted value="#{transferFacIdData.value} % Complete" />
							</af:progressIndicator>
						</af:panelGroup>
					</af:panelGroup>
	
				<afh:rowLayout halign="center">
					<h:panelGrid border="1">
						<af:panelBorder>
							<af:showDetailHeader text="Transfer FACID Data" disclosed="true">
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Transfer FACID Data"
											disabled="#{transferFacIdData.transferInProgress}"
											actionListener="#{transferFacIdData.refreshPage}"
											action="#{transferFacIdData.transferData}" />
										<af:commandButton text="Cancel Pending FACID Data Transfer"
											actionListener="#{transferFacIdData.refreshPage}"
											action="#{transferFacIdData.cancelPendingDataTransfer}" />
									</af:panelButtonBar>
								</afh:rowLayout>
						</af:showDetailHeader>
					</af:panelBorder>
				</h:panelGrid>
			</afh:rowLayout>
					



        <af:objectSpacer width="100%" height="15" />




        <afh:rowLayout halign="center" >
          <h:panelGrid border="1" width="1100">
            <af:panelBorder>
              <af:showDetailHeader text="EPA Staging Table Transfer Log" disclosed="true">

				<af:table value="#{transferFacIdData.transferLogEntriesWrapper}"
					binding="#{transferFacIdData.transferLogEntriesWrapper.table}"
					bandingInterval="1" banding="row" var="transfer" partialTriggers="transferFacIdDataPoll"
					rows="#{transferFacIdData.pageLimit}" width="99%">

                  <af:column sortProperty="id" sortable="true"
                    headerText="Transfer ID" formatType="text">
                        <af:outputText value="#{transfer.id}" />
                  </af:column>

                  <af:column sortProperty="id" sortable="true"
                    headerText="Transfer Type" formatType="text">
                        <af:outputText value="#{transfer.type}" />
                  </af:column>

                  <af:column sortProperty="reportingYear" sortable="true"
                    headerText="Reporting Year" formatType="text">
                        <af:outputText value="#{transfer.reportingYear}" />
                  </af:column>

                  <af:column sortProperty="facilityTypes" sortable="true"
                    headerText="Facility Type(s)" formatType="text">
                        <af:outputText value="#{transfer.facilityTypes}" />
                  </af:column>

                    <af:column sortProperty="userName"
                      headerText="User" sortable="true"  formatType="text">
                        <af:inputText readOnly="true"
                          value="#{transfer.username}" />
                    </af:column>

                    <af:column sortProperty="startDate"
                      sortable="true" headerText="Start Time" formatType="number">
                        <af:selectInputDate
                          value="#{transfer.startDate}"
                          readOnly="true">
                          <f:convertDateTime dateStyle="full" pattern="MM/dd/yyyy HH:mm:ss"/>
                        </af:selectInputDate>
                    </af:column>

                    <af:column sortProperty="endDate"
                      sortable="true" headerText="End Time" formatType="number">
                        <af:selectInputDate
                          value="#{transfer.endDate}" readOnly="true">
                          <f:convertDateTime dateStyle="full" pattern="MM/dd/yyyy HH:mm:ss"/>
                        </af:selectInputDate>
                    </af:column>

                    <af:column sortProperty="duration"
                      sortable="true" headerText="Duration" formatType="number">
                        <af:inputText readOnly="true"
                          value="#{transfer.durationFormatted}" />
                    </af:column>

                    <af:column sortProperty="progress"
                      sortable="true" headerText="Progress %" formatType="number">
                        <af:inputText readOnly="true"
                          value="#{transfer.progressPercent}" />
                    </af:column>

                    <af:column sortProperty="message"
                      sortable="true" headerText="Message" formatType="text">
                        <af:inputText readOnly="true"
                          value="#{transfer.message}" />
                    </af:column>

                    <af:column sortProperty="status"
                      sortable="true" headerText="Status" formatType="text">
                        <af:inputText readOnly="true"
                          value="#{transfer.status}"/>
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
