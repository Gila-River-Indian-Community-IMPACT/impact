<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document title="NEI Export">
	<f:verbatim>
      <h:outputText value="#{facilityProfile.refreshStr}" escape="false" />
    </f:verbatim>
    
		<f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim>
		<af:form>
			<af:page var="foo" value="#{menuModel.model}" title="NEI Export">
				<%@ include file="../util/header.jsp"%>
					<af:outputText value="PB :#{neiExport.showProgressBar}"></af:outputText>
					<af:progressIndicator id="progressid"> <!-- rendered="neiExport.showProgressBar" -->
          				<af:outputFormatted 
				             value="Generating NEI Export #{neiExport.value} out of 
                			  #{neiExport.maximum} Reports returned by 
			                     the initial search." 
            				 rendered="#{neiExport.showProgressBar}" />
			        </af:progressIndicator>
        
					<h:panelGrid border="1" align="center">
						<af:panelBorder>
							<af:panelForm rows="1" width="1000" maxColumns="1">
								<af:outputText
									value="Select the year and file format to generate NEI Export files." />
							</af:panelForm>
							<af:objectSpacer height="15" />
							
							<afh:tableLayout width="80%" halign="center" borderWidth="0" cellSpacing="3">
								<afh:rowLayout>
									<afh:cellFormat halign="right">
										<af:inputText readOnly="true" label="Year :" required="true" />
									</afh:cellFormat>
									<afh:cellFormat>
										<af:selectOneChoice value="#{neiExport.year}"
											unselectedLabel="Select Year">
											<f:selectItems value="#{infraDefs.years}" />
										</af:selectOneChoice>

									</afh:cellFormat>
									<afh:cellFormat halign="right">
										<af:inputText label="Format :" required="true" readOnly="true" />
									</afh:cellFormat>
									<afh:cellFormat>
										<af:panelGroup layout="horizontal">
											<af:selectBooleanRadio group="group1" text="NIF"
												value="#{neiExport.nif}"></af:selectBooleanRadio>
											<af:selectBooleanRadio group="group1" text="XML"
												value="#{neiExport.xml}"></af:selectBooleanRadio>
										</af:panelGroup>
									</afh:cellFormat>
								</afh:rowLayout>

								<afh:rowLayout>
									<afh:cellFormat halign="right">
										<af:inputText label="Transaction Submittal Code :"
											required="true" readOnly="true" />
									</afh:cellFormat>
									<afh:cellFormat>
										<af:panelGroup layout="horizontal">
											<af:selectOneChoice value="#{neiExport.transCd}"
												unselectedLabel="Select Code">
												<f:selectItems value="#{neiExport.transactionCds}" />
											</af:selectOneChoice>
										</af:panelGroup>
									</afh:cellFormat>
									<afh:cellFormat halign="right">
										<af:inputText label="Transaction Type :" required="true"
											readOnly="true"></af:inputText>
									</afh:cellFormat>
									<afh:cellFormat>
										<af:panelGroup layout="horizontal">
											<af:selectOneChoice value="#{neiExport.transType}"
												unselectedLabel="Select Type">
												<f:selectItems value="#{neiExport.transactionTypes}" />
											</af:selectOneChoice>
										</af:panelGroup>
									</afh:cellFormat>
								</afh:rowLayout>

								<afh:rowLayout>
									<afh:cellFormat halign="right">
										<af:inputText label="Transaction Comment :" readOnly="true" />
									</afh:cellFormat>
									<af:inputText rows="4" columns="40" maximumLength="80"
										value="#{neiExport.transComment}" />
									<afh:cellFormat>
									</afh:cellFormat>
								</afh:rowLayout>
							</afh:tableLayout>
							<af:objectSpacer height="15" />
							<afh:rowLayout halign="center">
								<af:panelButtonBar>
									<af:commandButton text="Generate File(s)"
										action="#{neiExport.generateNEIFiles}" useWindow="true"
										windowWidth="950" windowHeight="500">
									</af:commandButton>

									<af:commandButton text="Reset" action="#{neiExport.reset}" />
								</af:panelButtonBar>
								</afh:rowLayout>
						</af:panelBorder>
					</h:panelGrid>
				
				<af:objectSpacer width="100%" height="15" />
				<afh:rowLayout halign="center">
					<h:panelGrid border="1"
						rendered="#{neiExport.hasSearchResults}">
						<af:showDetailHeader text="NEI File(s) List" disclosed="true">
							<jsp:include flush="true" page="neiExportTable.jsp" />
						</af:showDetailHeader>
					</h:panelGrid>
				</afh:rowLayout>
				
			</af:page>
		</af:form>
	</af:document>
</f:view>
