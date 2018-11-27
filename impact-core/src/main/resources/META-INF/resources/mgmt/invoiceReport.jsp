<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<f:view>
	<af:document title="Invoice Report">
		<f:facet name="metaContainer">
			<f:verbatim>
				<h:outputText value="#{facilityProfile.refreshStr}" escape="false" />
			</f:verbatim>
		</f:facet>
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form>
		  <af:page var="foo" value="#{menuModel.model}" title="Invoice Report">
		    <%@ include file="../util/header.jsp"%>

				<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
					<afh:rowLayout halign="left">
						<af:panelHorizontal>
							<af:outputFormatted
								value="<b>Data source:</b> invoice detail<br><br>" />
							<af:objectSpacer width="10" height="5" />
							<af:commandButton text="Show Explanation"
								rendered="#{!complianceSearch.showExplain}"
								action="#{complianceSearch.turnOn}">
							</af:commandButton>
							<af:commandButton text="Hide Explanation"
								rendered="#{complianceSearch.showExplain}"
								action="#{complianceSearch.turnOff}">
							</af:commandButton>
						</af:panelHorizontal>
					</afh:rowLayout>
					<afh:rowLayout rendered="#{complianceSearch.showExplain}"
						halign="left">
						<af:outputFormatted
							value="Enter an invoice date and select a type of fee to generate a list of facilities that were billed on that date.  The results are separated by DO/LAA.<br><br>" />
					</afh:rowLayout>
				</afh:tableLayout>

				<af:panelGroup layout="vertical"
					rendered="#{invoiceReport.showProgressBar}">
					<af:progressIndicator id="progressid"
						value="#{invoiceReport}"/>
				</af:panelGroup>
				
			 <afh:rowLayout halign="center">
			   <h:panelGrid border="1" width="600">
			     <af:panelBorder>
				   <af:showDetailHeader text="Invoice Report Filter" disclosed="true">
				     <af:panelForm rows="1" maxColumns="2">
					   <af:selectInputDate label="Invoice Date" value="#{invoiceReport.invoiceDate}" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
                       
                       <af:selectOneChoice label="Revenue Type"
                           value="#{invoiceReport.revenueType}">
                         <f:selectItems value="#{invoiceReport.revenueTypes}" />
                       </af:selectOneChoice>
					 </af:panelForm>
					 <af:objectSpacer width="100%" height="15" />
					 <afh:rowLayout halign="center">
					   <af:panelButtonBar>
					     <af:commandButton text="Submit" action="#{invoiceReport.submit}" />
						   <af:commandButton text="Reset" action="#{invoiceReport.reset}" />
					   </af:panelButtonBar>
					 </afh:rowLayout>
				   </af:showDetailHeader>
				  </af:panelBorder>
				</h:panelGrid>
			  </afh:rowLayout>

				<af:objectSpacer width="100%" height="15" />

			<afh:rowLayout halign="center">
			  <h:panelGrid border="1" width="950" rendered="#{invoiceReport.hasSearchResults}">
			    <af:panelBorder>
			      <af:showDetailHeader text="Invoice Report" disclosed="true">
			      
			        <af:table value="#{invoiceReport.entrySet}" var="dolaa" width="900">
			          <af:column noWrap="true">
					    <af:panelHorizontal valign="middle" halign="center">
						  <af:inputText readOnly="true" value="#{dolaa.key}"
									inlineStyle="font-weight:bold; font-size:larger;">
						  </af:inputText>
						</af:panelHorizontal>

						<af:panelHorizontal valign="middle" halign="left">
						  <af:table bandingInterval="2" banding="row" var="dolaaC"
									emptyText='' width="900" value="#{dolaa.value}"
									rendered="#{! empty dolaa.value}">

						    <af:column noWrap="true" headerText="Facility ID">
							  <af:panelHorizontal valign="middle" halign="left">
							    <af:commandLink action="#{facilityProfile.submitProfile}">
								  <af:outputText value="#{dolaaC.facilityId}" />
									<t:updateActionListener property="#{facilityProfile.fpId}"
																value="#{dolaaC.fpId}" />
								</af:commandLink>
							  </af:panelHorizontal>
							</af:column>

							<af:column noWrap="true" headerText="Facility Name">
							  <af:panelHorizontal valign="middle" halign="left">
								<af:outputText rendered="#{dolaaC.facilityName !='dummy'}"
														    value="#{dolaaC.facilityName}" />
							  </af:panelHorizontal>
							</af:column>					

							<af:column noWrap="true" headerText="ISTV">
							  <af:panelHorizontal valign="middle" halign="left">
								 <af:outputText rendered="#{dolaaC.facilityName !='dummy'}"
												value="#{dolaaC.intraStateVoucherFlag ? 'Y':'N'}" />
							  </af:panelHorizontal>
							</af:column>

							<af:column noWrap="true" headerText="Revenue ID">
							  <af:panelHorizontal valign="middle" halign="left">
								<af:outputText value="#{dolaaC.revenueId}" />
							  </af:panelHorizontal>
							</af:column>

							<af:column noWrap="true" headerText="Revenue Type">
							  <af:panelHorizontal valign="middle" halign="left">
								<af:outputText value="#{dolaaC.revenueTypeCd}" />
								<!-- // stars2 merge //
														    inLineStyle="#{dolaaC.facilityName =='dummy'?'font-weight:bold;':''}"/>
								 -->
							  </af:panelHorizontal>
							</af:column>

							<af:column noWrap="true" headerText="Invoice Date">
							  <af:panelHorizontal valign="middle" halign="left">
								 <af:outputText rendered="#{dolaaC.facilityName !='dummy'}"
								    	    value="#{dolaaC.creationDate}" />
								    	   <!-- creationDate actually holds the Invoice Date(Effective Date) -->
							  </af:panelHorizontal>
							</af:column>

							<af:column noWrap="true" headerText="Original Amount" formatType="number">
							  <af:panelHorizontal valign="middle" halign="left">
								<af:inputText readOnly="true" value="#{dolaaC.origAmount}">
								<!-- // stars2 merge //
										    inLineStyle="#{dolaaC.facilityName =='dummy'?'font-weight:bold;':''}">
								 -->
									<af:convertNumber type="currency" currencySymbol="$" />
								</af:inputText>
							  </af:panelHorizontal>
							</af:column>
										          
							<f:facet name="footer">
							  <h:panelGrid width="100%">
								<afh:rowLayout halign="center">
								  <af:panelButtonBar>
									<af:commandButton actionListener="#{tableExporter.printTable}"
											onclick="#{tableExporter.onClickScript}" text="Printable view" />

									<af:commandButton
											actionListener="#{tableExporter.excelTable}"
											onclick="#{tableExporter.onClickScript}" text="Export to excel" />
								  </af:panelButtonBar>															
								</afh:rowLayout>
							  </h:panelGrid>
							</f:facet>
						</af:table>										          
					   </af:panelHorizontal>
										
					  </af:column>

					  <f:facet name="footer">
						<h:panelGrid width="100%">
						  <afh:rowLayout halign="right">
							<af:outputLabel value="Total Invoiced -" />
							 <af:objectSpacer width="7" height="5" />
							 <af:outputText value="#{invoiceReport.grandTotal}">
							   <af:convertNumber type="currency" currencySymbol="$" />
							 </af:outputText>
     					  </afh:rowLayout>
						</h:panelGrid>
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
