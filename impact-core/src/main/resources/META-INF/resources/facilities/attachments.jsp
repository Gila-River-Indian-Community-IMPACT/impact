<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>

    <f:view>
      <af:document title="Attachments">
            <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim><af:form>
          <af:page var="foo" value="#{menuModel.model}" title="Attachments">
          
			<jsp:include page="header.jsp" />
			
            <h:panelGrid border="1">
              <af:panelBorder>
              
                <f:facet name="top">
				  <f:subview id="facilityHeader" rendered="#{facilityProfile.facility != null}">
                    <jsp:include page="comFacilityHeader3.jsp"/>
                  </f:subview>
                </f:facet>

                <f:facet name="right">
                	<f:subview id="attachments">
				  		<jsp:include flush="true" page="../doc_attachments/doc_attachments.jsp"/>
				  	</f:subview>
                </f:facet>  
                
              </af:panelBorder>
            </h:panelGrid>
          </af:page>
        </af:form>
      </af:document>
    </f:view>
