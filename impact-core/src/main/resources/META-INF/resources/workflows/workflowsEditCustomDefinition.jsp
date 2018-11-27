<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
  
<f:view>
  <af:document title="Edit Definition">
        <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim>
  <af:form usesUpload="true">
      <af:messages/>
     <f:subview id="defList">             
      <af:panelForm>
		<af:outputText value="#{workflowDefCatalog.label}" />
	  </af:panelForm> 
      
      <af:forEach var="record" varStatus="vsi" items="#{workflowDefCatalog.customRecord.cells}">           
	      <af:panelForm>
	      	
	      	<af:inputText 
				label="#{record.headerText}" 
			    value="#{record.value}" 
			    rendered="#{!record.pickList && (record.dataType == 'NUMBER' || record.dataType == 'numeric')}" 
			    maximumLength="#{record.maximumLength}"
			    readOnly="#{record.readOnly && !workflowDefCatalog.newCustomRecord}" 
			    tip="Number"  
			    showRequired="#{record.required}"
			    required="#{record.required}">
			    <af:validateRegExp pattern="[0-9]*(\.[0-9]+)?" noMatchMessageDetail="{0} must be numeric."/>
			 </af:inputText>
			
			<af:inputText 
				label="#{record.headerText}" 
			    value="#{record.value}" 
			    rendered="#{record.headerText == 'Template Path'}" 
			    maximumLength="#{record.maximumLength}" 
			    readOnly="#{record.readOnly && !workflowDefCatalog.newCustomRecord}" 
			    id="TemplatePath" 
			    showRequired="#{record.required}"
			    required="#{record.required}"  
			    />
			<af:panelLabelAndMessage for="docUrl" rendered="#{record.headerText == 'Template Path' && (!empty record.value)}" >

				<af:goLink targetFrame="_blank" destination="#{record.docURL}"
					text="download template" 
					id="docUrl"
					/>
			</af:panelLabelAndMessage>
			    
			<af:inputText 
				label="#{record.headerText}" 
			    value="#{record.value}" 
			    rendered="#{!record.pickList && record.dataType != 'TIMESTAMP(6)' && record.dataType != 'NUMBER' && record.dataType != 'numeric'  && record.headerText != 'Template Path'}" 
			    maximumLength="#{record.maximumLength}" 
			    readOnly="#{record.readOnly && !workflowDefCatalog.newCustomRecord}" 
			    tip="Text"  
			    showRequired="#{record.required}"
			    required="#{record.required}"  
			    />
			<af:selectInputDate 
				label="#{record.headerText}" 
			    value="#{record.value}" 
			    rendered="#{!record.pickList && record.dataType == 'TIMESTAMP(6)'}" 
			    readOnly="#{record.readOnly && !workflowDefCatalog.newCustomRecord}" 
			    tip="Date"  
			    showRequired="#{record.required}"
			    required="#{record.required}"  
			/>
			<af:selectOneChoice label="#{record.headerText}"
            	rendered="#{record.pickList}" 
            	readOnly="#{record.readOnly && !workflowDefCatalog.newCustomRecord}" 
                showRequired="#{record.required}"
			    required="#{record.required}"  
                value="#{record.value}">
                <af:forEach var="plist" items="#{record.pickListItems}">
               		<af:selectItem label="#{plist.label}" value="#{plist.value}"/>
  			   </af:forEach>
            </af:selectOneChoice>
           
            <afh:rowLayout halign="Center"> 
            <af:inputFile onchange="setTemplatePath();" onselect="setTemplatePath();"
             		value="#{permitDetail.templateFileToUpload}"
             		id="UploadDDocButton"
					rendered="#{record.headerText == 'Template Path'}" 
                    valueChangeListener="#{permitDetail.uploadTemplate}"
			/>			
            </afh:rowLayout>
		  </af:panelForm> 
	  </af:forEach>
      <af:panelForm>         
        <af:objectSpacer height="20" /> 
          <afh:rowLayout halign="center">           
           <af:panelButtonBar>
            <af:commandButton text="Save"
                actionListener="#{workflowDefCatalog.saveCustom}"/>
            <af:commandButton text="Cancel"
                immediate="true"
                actionListener="#{workflowDefCatalog.cancel}"/>
           </af:panelButtonBar>
          </afh:rowLayout>
        </af:panelForm>
     </f:subview>
    </af:form>
    
    <f:verbatim>
			<script type="text/javascript">
				
				function setTemplatePath() {
					var path = document.getElementById('defList:UploadDDocButton_3').value ;
					var startIndex = path.lastIndexOf(".");
					var lengthOfPath = path.length;					
					var extension = path.substring(startIndex,lengthOfPath);	
					if(extension != ".docx" && extension != ".DOCX"){
						alert("Please select a microsoft docx file");
						return;
					}else{	
						var filePath = document.getElementById('defList:UploadDDocButton_3').value;
						var startIndex = filePath.lastIndexOf("\\");
						var lengthOfFilePath = filePath.length;	
						var fileName = path.substring(startIndex,lengthOfFilePath);
						var modifiedFileName = ("\\Templates").concat(fileName);
						document.getElementById('defList:TemplatePath_3').value = modifiedFileName;
					}
				}
				
			</script>
	</f:verbatim>
  </af:document>
</f:view>