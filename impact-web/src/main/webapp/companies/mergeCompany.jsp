<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
	<af:document id="body" onmousemove="#{infraDefs.iframeResize}"
		onload="#{infraDefs.iframeReload}" title="Company Merge">
		<f:verbatim>
			<script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
		</f:verbatim>
		<af:form usesUpload="true" id="cmpMergeForm">
			<af:messages />
			<f:subview id="cmpMerge">
				<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
					<afh:rowLayout halign="center">
						<af:panelHeader messageType="Information" text="Merge Company" />
					</afh:rowLayout>
				</afh:tableLayout>
				<af:panelGroup rendered="#{companyProfile.mergeEditable}">
					<afh:rowLayout halign="left">
						<af:outputFormatted
							value="Enter the source company to merge its facility ownership records, employed contact records, and note records into the destination company." />
					</afh:rowLayout>
				</af:panelGroup>
				<af:objectSpacer height="20" />
				<af:panelForm
					partialTriggers="sourceCmpId sourceCmpName destinationCmpId destinationCmpName"
					id="mergeCompanies">
					<af:panelForm rows="2" maxColumns="2">
						<af:inputText label="Source Company ID:" columns="12"
							maximumLength="12" showRequired="true"
							value="#{companyProfile.mergeSourceCmpId}"
							onkeyup="refreshCompanies();" onkeydown="disableTimer();"
							id="sourceCmpId" onchange="setCmpInfo();" onfocus="setCmpInfo();"
							autoSubmit="true" readOnly="#{!companyProfile.mergeEditable}" />
						<af:selectOneChoice value="#{companyProfile.mergeSourceCmpId}"
							label="Source Company Name: " showRequired="true"
							id="sourceCmpName" readOnly="true">
							<f:selectItems value="#{companySearch.allCompanies}" />
						</af:selectOneChoice>
						<af:inputText label="Destination Company ID:" columns="12"
							maximumLength="12" showRequired="true"
							value="#{companyProfile.mergeDestinationCmpId}"
							id="destinationCmpId" onkeyup="refreshCompanies();"
							onkeydown="disableTimer();" onchange="setCmpInfo();"
							autoSubmit="true" readOnly="#{!companyProfile.mergeEditable}" />
						<af:selectOneChoice
							value="#{companyProfile.mergeDestinationCmpId}"
							label="Destination Company Name: " showRequired="true"
							id="destinationCmpName" readOnly="true">
							<f:selectItems value="#{companySearch.allCompanies}" />
						</af:selectOneChoice>
					</af:panelForm>


					<af:panelGroup id="companySourceProgress"
						inlineStyle="display:none;margin-left:auto;margin-right:auto;">
						<af:progressIndicator action="guide" />
					</af:panelGroup>

					<af:panelGroup id="sourceCompanyInformation"
						partialTriggers="sourceCmpId"
						onmouseover="triggerCompanyUpdate();">
						<af:panelGroup
							rendered="#{companyProfile.sourceCompany == null && !(empty companyProfile.mergeSourceCmpId)}">
							<af:objectSpacer height="10" />
							<afh:rowLayout halign="center">
								<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
									value="Warning: The selected source company does not exist." />
							</afh:rowLayout>
						</af:panelGroup>
						<af:panelGroup
							rendered="#{companyProfile.destinationCompany == null && !(empty companyProfile.mergeDestinationCmpId)}">
							<af:objectSpacer height="10" />
							<afh:rowLayout halign="center">
								<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
									value="Warning: The selected destination company does not exist." />
							</afh:rowLayout>
						</af:panelGroup>
						<af:panelGroup rendered="#{companyProfile.sourceCompany != null}">
							<af:panelGroup
								rendered="#{!(empty companyProfile.sourceCompany.externalCompanyId)}">
								<af:objectSpacer height="10" />
								<afh:rowLayout halign="center">
									<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
										value="Warning: The selected source company has an assigned CROMERR ID." />
								</afh:rowLayout>
							</af:panelGroup>

							<af:showDetailHeader text="Source Company's Facilities"
								disclosed="true" size="2" id="cmpFacilitiess"
								rendered="#{companyProfile.dapcUser}">
								<jsp:include flush="true" page="sourceCompanyFacilities.jsp" />
							</af:showDetailHeader>

							<af:showDetailHeader text="Source Company's Employed Contacts"
								disclosed="true" size="2" id="cmpContactss"
								rendered="#{companyProfile.dapcUser}">
								<jsp:include flush="true" page="sourceCompanyContacts.jsp" />
							</af:showDetailHeader>
						</af:panelGroup>
					</af:panelGroup>
				</af:panelForm>



				<af:panelGroup rendered="#{!companyProfile.mergeEditable}">
					<af:objectSpacer height="20" />
					<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
						<afh:rowLayout halign="center">
							<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
								value="Caution: The source company will have its facility ownership records, employed contact records, and note records merged into the destination company. " />
						</afh:rowLayout>
					</afh:tableLayout>
					<af:objectSpacer height="10" />
					<afh:tableLayout width="100%" borderWidth="0" cellSpacing="0">
						<afh:rowLayout halign="center">
							<af:outputFormatted inlineStyle="color: orange; font-weight: bold;"
								value="Are you sure you would like to continue?" />
						</afh:rowLayout>
					</afh:tableLayout>
				</af:panelGroup>
				<af:objectSpacer height="20" />
				<af:panelForm id="mergeCompany"
					onmouseover="triggerCompanyUpdate();">
					<afh:rowLayout halign="center">
						<af:panelButtonBar>
							<af:commandButton text="Merge Companies"
								action="#{companyProfile.beginCompanyMerge}"
								rendered="#{companyProfile.mergeEditable}" />
							<af:commandButton text="Yes"
								action="#{companyProfile.mergeCompanies}"
								rendered="#{!companyProfile.mergeEditable}" />
							<af:commandButton text="No"
								action="#{companyProfile.stopCompanyMerge}"
								rendered="#{!companyProfile.mergeEditable}" />
							<af:commandButton text="Cancel"
								action="#{companyProfile.closeDialog}" immediate="true" />
						</af:panelButtonBar>
					</afh:rowLayout>
				</af:panelForm>
			</f:subview>
		</af:form>
		<f:verbatim>
			<script type="text/javascript">
				var timer = 0;
				var sourceCmpIdInitValue = document.getElementById('cmpMerge:sourceCmpId').value;
				var destCmpIdInitValue = document.getElementById('cmpMerge:destinationCmpId').value;
				
				function refreshCompanies() {
		    		  clearTimeout(timer);
		    		  timer=setTimeout(
		    				function triggerPartialUpdate(){
		    					triggerCompanyUpdate();
		    				}
		    					,4000);
		    		  
		    		  input = document.getElementById("cmpMerge:sourceCmpId");
		    		  if(document.activeElement.id=="cmpMerge:sourceCmpId"){
		    			if(sourceCmpIdInitValue != input.value){
		    				document.getElementById('cmpMerge:companySourceProgress').style.display = 'table';
		    				document.getElementById('cmpMerge:sourceCompanyInformation').style.display = 'none';
		    		  	} else {
		    				document.getElementById('cmpMerge:companySourceProgress').style.display = 'none';
		    				document.getElementById('cmpMerge:sourceCompanyInformation').style.display = '';
		    		  		disableTimer();
		    		  	}
		    		  }
				}
				
				function triggerCompanyUpdate(){
					disableTimer();
					if(document.activeElement.id=="cmpMerge:sourceCmpId"){
			    		input = document.getElementById("cmpMerge:sourceCmpId");
			    		input.blur();
					} else if(document.activeElement.id=="cmpMerge:destinationCmpId"){
			    		input = document.getElementById("cmpMerge:destinationCmpId");
			    		input.blur();
					} 
					

				}
				
				function disableTimer() {
					clearTimeout(timer);
					timer=0;
				}
				
				function setCmpInfo() {
					sourceCmpIdInitValue = document.getElementById('cmpMerge:sourceCmpId').value;
					destCmpIdInitValue = document.getElementById('cmpMerge:destinationCmpId').value;
				}
				
			</script>
		</f:verbatim>
	</af:document>
</f:view>