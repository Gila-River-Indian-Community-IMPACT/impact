<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<f:view>
  <af:document title="Permit Conditions">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form usesUpload="true">
      <af:page id="ThePage" var="foo" value="#{menuModel.model}"
        title="Permit Conditions">
        <%@ include file="../permits/header.jsp"%>

        <h:panelGrid border="1" width="100%">
          <af:panelBorder>

            <f:facet name="top">
              <f:subview id="permitDetailTop">
                <jsp:include page="permitDetailTop.jsp" />
              </f:subview>
            </f:facet>
            <%
            /* Top end */
            %>

            <%
            /* Content begin - placeholder */
            %>
            
            <h:panelGrid columns="1" border="1"
              width="100%">
              
              <afh:rowLayout halign="left" valign="top">
	              <af:panelGroup>
	              		<af:objectSpacer width="10" height="1"/>
	              		  <af:panelForm labelWidth="5%" rows="2" maxColumns="2"
											fieldWidth="95%">
	          				<af:selectOneChoice
			          			id="permitLevelStatusCd"
			          			label="Permit Status: "
			          			readOnly="#{!(permitDetail.editMode || permitDetail.semiEditMode)}"
			          			value="#{permitDetail.permit.permitLevelStatusCd}" >
								<f:selectItems
									value="#{permitReference.permitLevelStatusDefs.items[
										(empty permitDetail.permit.permitLevelStatusCd
										? '' : permitDetail.permit.permitLevelStatusCd)]}" />
							</af:selectOneChoice>
			          	  </af:panelForm>
			          	<af:objectSpacer height="10" />
			          		
		                <f:subview id="permit_condition_list">
								<jsp:include page="permitConditionList.jsp" />
						</f:subview>
						
					<af:objectSpacer height="10" />
					
					<afh:rowLayout halign="center" rendered="#{!permitDetail.readOnlyUser}">
					  <af:switcher defaultFacet="view"
					    facetName="#{(permitDetail.editMode || permitDetail.semiEditMode) ? 'edit': 'view'}">
					    <f:facet name="view">
					      <af:panelButtonBar>
					        <af:commandButton text="Edit"
					           rendered="#{permitConditionDetail.permitConditionEditAllowed}"
					           action="#{permitDetail.enterEditMode}" />
				            <af:commandButton text="Workflow Task"
					          disabled="#{!permitDetail.fromTODOList}"
					          action="#{permitDetail.goToCurrentWorkflow}" />   
					      </af:panelButtonBar>
					    </f:facet>
					    <f:facet name="edit">
					      <af:panelButtonBar>
					        <af:commandButton text="Save changes"
					          action="#{permitDetail.updatePermit}" />
					        <af:commandButton text="Discard changes" immediate="true"
					          action="#{permitDetail.undoPermit}" />
					      </af:panelButtonBar>
					    </f:facet>
					  </af:switcher>
					</afh:rowLayout>
					<af:objectSpacer height="10" />
	              </af:panelGroup>
              </afh:rowLayout>
            </h:panelGrid>
            <%
            /* Content end */
            %>

          </af:panelBorder>
        </h:panelGrid>
      </af:page>
      <af:iterator value="#{permitDetail}" var="validationBean" id="v">
				<%@ include file="../util/validationComponents.jsp"%>
	  </af:iterator>
		<%/*  hidden controls for navigation to compliance report from popup */%>
		<%@ include file="../util/hiddenControls.jsp"%>
		</af:form>
  </af:document>
</f:view>
