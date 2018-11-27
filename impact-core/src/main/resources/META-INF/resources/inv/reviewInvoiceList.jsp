<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="Review Invoice">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
			<af:page>
				<%@ include file="../util/branding.jsp"%>
				<af:objectSpacer height="10" />

				<afh:rowLayout halign="center">
					<af:panelGroup layout="vertical">

						<af:table value="#{reviewInvoiceList}" var="reviewList"
							bandingInterval="1" banding="row" emptyText=" " rows="#{reportDetail.pageLimit}"
							width="900" binding="#{reportDetail.reviewTable}" id="raggTable">
							<f:facet name="selection">
								<af:tableSelectMany />
							</f:facet>
							<af:column headerText="Facility ID" sortable="true"
								sortProperty="facilityId" formatType="text">
								<af:panelHorizontal valign="middle" halign="left">
									<af:inputText readOnly="true" value="#{reviewList.facilityId}"
										inlineStyle="white-space: nowrap;" />
								</af:panelHorizontal>
							</af:column>

							<af:column headerText="Facility Name" sortable="true"
								sortProperty="facilityName" formatType="text">
								<af:panelHorizontal valign="middle" halign="left">
									<af:inputText readOnly="true"
										value="#{reviewList.facilityName}"
										inlineStyle="white-space: nowrap;" />
								</af:panelHorizontal>
							</af:column>

							<af:column headerText="Report ID" sortable="true"
								sortProperty="emissionsRptId" formatType="text">
								<af:panelHorizontal valign="middle" halign="left">
									<af:inputText readOnly="true"
										value="#{reviewList.emissionsRptId}"
										inlineStyle="white-space: nowrap;" />
								</af:panelHorizontal>
							</af:column>

							<af:column headerText="Report Year" sortable="true"
								sortProperty="reportYear" formatType="text">
								<af:panelHorizontal valign="middle" halign="left">
									<af:inputText readOnly="true" value="#{reviewList.reportYear}"
										inlineStyle="white-space: nowrap;" />
								</af:panelHorizontal>
							</af:column>

							<af:column headerText="Previous report" sortable="true"
								sortProperty="prevReport" formatType="text">
								<af:panelHorizontal valign="middle" halign="left">
									<af:inputText readOnly="true" value="#{reviewList.prevReport}"
										inlineStyle="white-space: nowrap;" />
								</af:panelHorizontal>
							</af:column>

							<af:column headerText="Total Tons" sortable="true"
								sortProperty="totalTons" formatType="text">
								<af:panelHorizontal valign="middle" halign="left">
									<af:inputText readOnly="true" value="#{reviewList.totalTons}"
										inlineStyle="white-space: nowrap;">
										<af:convertNumber pattern="###,##0.##" minFractionDigits="2" />
									</af:inputText>	
								</af:panelHorizontal>
							</af:column>

							<af:column headerText="Invoice ID" sortable="true"
								sortProperty="invoiceId" formatType="text">
								<af:panelHorizontal valign="middle" halign="left">
									<af:inputText readOnly="true" value="#{reviewList.invoiceId}"
										inlineStyle="white-space: nowrap;" />
								</af:panelHorizontal>
							</af:column>

							<af:column headerText="Revenue ID" sortable="true"
								sortProperty="revenueId" formatType="number">
								<af:panelHorizontal valign="middle" halign="left">
									<af:inputText readOnly="true"
										value="#{reviewList.revenueId}"
										inlineStyle="white-space: nowrap;" />
								</af:panelHorizontal>
							</af:column>
							
							<af:column headerText="Revenue Type" sortable="true"
								sortProperty="revenueStateCd" formatType="text">
								<af:panelHorizontal valign="middle" halign="left">
									<af:inputText readOnly="true"
										value="#{reviewList.revenueTypeCd}"
										inlineStyle="white-space: nowrap;" />
								</af:panelHorizontal>
							</af:column>

							<af:column headerText="Original Amount" sortable="true"
								sortProperty="invoiceId" formatType="number">
								<af:panelHorizontal valign="middle" halign="left">
									<af:inputText readOnly="true" value="#{reviewList.origAmount}"
										inlineStyle="white-space: nowrap;">
										<af:convertNumber type='currency' locale="en-US"
											minFractionDigits="2" />
									</af:inputText>	
								</af:panelHorizontal>
							</af:column>

							<af:column headerText="Invoice Difference" sortable="true"
								sortProperty="invoiceId" formatType="number">
								<af:panelHorizontal valign="middle" halign="left">
									<af:inputText readOnly="true"
										value="#{reviewList.invoiceDifference}"
										inlineStyle="white-space: nowrap;">
										<af:convertNumber type='currency' locale="en-US"
											minFractionDigits="2" />
									</af:inputText>	
								</af:panelHorizontal>
							</af:column>

							<f:facet name="footer">
								<afh:rowLayout halign="center">
									<af:panelButtonBar>
										<af:commandButton text="Post/ Adjust Selected"
										    useWindow="true" windowWidth="600" windowHeight="400"
											disabled="#{! reportDetail.enablePostAdjustButton || !(activityProfile.activity.userId == activityProfile.userId)}"
											action="#{reportDetail.doSelectedReview}" />
										<af:commandButton text="Complete Selected"
										    disabled="#{! reportDetail.enableCompletionButton || ! activityProfile.needCheckIn ||!(activityProfile.activity.userId == activityProfile.userId)}"
                                            action="#{reportDetail.checkInSelected}"/>
										<af:commandButton actionListener="#{tableExporter.printTable}"
											onclick="#{tableExporter.onClickScript}"
											text="Printable view" />
										<af:commandButton actionListener="#{tableExporter.excelTable}"
											onclick="#{tableExporter.onClickScript}"
											text="Export to excel" />
									</af:panelButtonBar>
								</afh:rowLayout>
							</f:facet>
						</af:table>
						<af:objectSpacer height="5" />
						<afh:rowLayout halign="center">
	                        <af:commandButton text="Close" onClick="window.close()"/>
                        </afh:rowLayout>

						<af:objectSpacer height="15" />
						
						<af:outputLabel 
		                   value="** Invoice will always post the Original Amount. In case of adjusted invoice if the previous invoice is already posted then Invoice  difference will be posted as an adjustment to the same Revenue Id." />
                        <af:outputLabel 
		                   value="** Complete the Tasks before closing this window, otherwise you need to select again." />  
					</af:panelGroup>
				</afh:rowLayout>
			</af:page>
		</af:form>
	</af:document>

</f:view>
