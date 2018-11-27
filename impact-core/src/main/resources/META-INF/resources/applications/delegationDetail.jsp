<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>


                   
	<af:panelForm rows="3" maxColumns="2">    
       <af:selectInputDate label="Date Request Received: "  required="true" id="mailingDate"
          value="#{delegation.delegationRequest.receivedDate}" 
          rendered="#{delegation.delegationRequest.applicationTypeCD!=''}"
          readOnly="#{!delegation.editable}">
        </af:selectInputDate>
        
         <af:selectOneChoice label="Disposition: " id="disposition" 
          readOnly="#{!delegation.editable}" 
          rendered="true"
          value="#{delegation.delegationRequest.requestDispositionCd}">
          <f:selectItems
            value="#{delegation.dispositionDef.items[(empty '')]}" />
        </af:selectOneChoice>
        
        <%/*Changes for #2175*/%>
        <%-- af:selectInputDate label="Effective Date: "  id="effectiveDate"
          value="#{delegation.delegationRequest.effectiveDate}" 
          rendered="#{delegation.delegationRequest.applicationTypeCD!=''}"
          readOnly="#{! delegation.editable || delegation.delegationRequest.submittedDate == null}" --%> 
                        
         <af:selectInputDate label="Date Submitted: " id="submittedDate"
          value="#{delegation.delegationRequest.submittedDate}" 
          rendered="#{delegation.delegationRequest.applicationTypeCD!=''}"
          readOnly="true">
        </af:selectInputDate>
        
          <af:selectBooleanCheckbox
            label="Corporate Qualified :"
            id="corpQualified" 
            readOnly="#{!delegation.editable}" 
            value="#{delegation.delegationRequest.facCorpQualified}" 
		 />
		 
		 <af:selectBooleanCheckbox
            label="Successor Qualified :"
            id="successorQualified" 
            readOnly="#{!delegation.editable}" 
            value="#{delegation.delegationRequest.facSuccessorQualified}" 
		 />
		 
		 <af:selectBooleanCheckbox
            label="$25M and/or 250+ Employees :"
            id="fac25MilOr250Emp" 
            readOnly="#{!delegation.editable}" 
            value="#{delegation.delegationRequest.fac25MilOr250EmpQualified}" 
		 />
		 
		</af:panelForm>

<h:panelGrid columns="2" border="0" width="100%" id="ownShut"> 
	<af:panelForm rows="8" maxColumns="1"> 
		<af:panelHeader text="Person Requesting Delegation" size="0" />                     
   
        <af:inputText id="origFirstName" 
          label="First Name:"
          value="#{delegation.delegationRequest.origFirstName}"
          rendered="true"
          columns="25" maximumLength="50"  
          readOnly="#{!delegation.editable}" />        
        
		 <af:inputText id="origLastName" 
          label="Last Name:"
          value="#{delegation.delegationRequest.origLastName}"
          rendered="true"
          columns="25" maximumLength="50"  
          readOnly="#{!delegation.editable}" />        
        
		<af:inputText id="origTitle" 
          label="Title:"
          value="#{delegation.delegationRequest.origTitle}"
          rendered="true"
          columns="40" maximumLength="100"  
          readOnly="#{!delegation.editable}" />
        
        
       <af:inputText id="origAddress1" 
          label="Address:"
          value="#{delegation.delegationRequest.origAddress1}"
          rendered="true"
          columns="40" maximumLength="100"  
          readOnly="#{!delegation.editable}" />
                
        <af:inputText id="origAddress2" 
          label="" 
          value="#{delegation.delegationRequest.origAddress2}"
          rendered="true"
          columns="40" maximumLength="100"  
          readOnly="#{!delegation.editable}" />
                
        <af:inputText id="origCity" 
          label="City:"
          value="#{delegation.delegationRequest.origCity}"
          rendered="true"
          columns="40" maximumLength="50"  
          readOnly="#{!delegation.editable}" />
        
        <af:selectOneChoice label="State:" id="origState" 
          readOnly="#{!delegation.editable}" 
          rendered="true"
          value="#{delegation.delegationRequest.origStateCd}">
          <f:selectItems
            value="#{infraDefs.states}" />
        </af:selectOneChoice>
        
        <af:inputText id="origZip" 
          label="Zip Code:"
          value="#{delegation.delegationRequest.origZip}"
          rendered="true"
          columns="9" maximumLength="5"  
          readOnly="#{!delegation.editable}" />
        
    </af:panelForm>
    
	<af:panelForm rows="8" maxColumns="1">  
		<af:panelHeader text="Person Being Delegated To" size="0" /> 
		 
		<af:inputText id="assigFirstName" 
          label="First Name:"
          value="#{delegation.delegationRequest.assigFirstName}"
          rendered="true"
          columns="25" maximumLength="50"  
          readOnly="#{!delegation.editable}" />
                
		 <af:inputText id="assigLastName" 
          label="Last Name:"
          value="#{delegation.delegationRequest.assigLastName}"
          rendered="true"
          columns="25" maximumLength="50"  
          readOnly="#{!delegation.editable}" />
                
		<af:inputText id="assigTitle" 
          label="Title:"
          value="#{delegation.delegationRequest.assigTitle}"
          rendered="true"
          columns="40" maximumLength="100"  
          readOnly="#{!delegation.editable}" />
                
		<af:inputText id="assigAddress1" 
          label="Address:"
          value="#{delegation.delegationRequest.assigAddress1}"
          rendered="true"
          columns="40" maximumLength="100"  
          readOnly="#{!delegation.editable}" />
                
        <af:inputText id="assigAddress2" 
          label=""
          value="#{delegation.delegationRequest.assigAddress2}"
          rendered="true"
          columns="40" maximumLength="100"  
          readOnly="#{!delegation.editable}" />
                
        <af:inputText id="assigCity" 
          label="City:"
          value="#{delegation.delegationRequest.assigCity}"
          rendered="true"
          columns="40" maximumLength="50"  
          readOnly="#{!delegation.editable}" />
        
        <af:selectOneChoice label="State:" id="assigState" 
          readOnly="#{!delegation.editable}" 
          rendered="true"
          value="#{delegation.delegationRequest.assigStateCd}">
          <f:selectItems
            value="#{infraDefs.states}" />
        </af:selectOneChoice>
        
        <af:inputText id="assigZip" 
          label="Zip Code:"
          value="#{delegation.delegationRequest.assigZip}"
          rendered="true"
          columns="9" maximumLength="5"  
          readOnly="#{!delegation.editable}" />
        
      </af:panelForm>
      
	</h:panelGrid>
	
	<af:showDetailHeader text="Issuances" size="2" disclosed="true"
  		  rendered="#{delegation.delegationRequest.submittedDate != null}">
   			 <jsp:include page="../util/issuancesTable.jsp" flush="true" />
  	</af:showDetailHeader>