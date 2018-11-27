<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Create Correspondence">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form usesUpload="true">
      <af:page>
        <af:messages />
        <af:panelGroup layout="vertical"
          partialTriggers="correspondenceType attachment">
          <af:panelForm>
            <af:selectInputDate label="Date Sent"
              value="#{correspondenceDetail.correspondence.dateGenerated }" > <af:validateDateTimeRange minimum="1900-01-01"/></af:selectInputDate>
            <af:selectOneChoice label="Correspondence Type"
              id="correspondenceType"
              value="#{correspondenceDetail.correspondence.correspondenceTypeCode}"
              autoSubmit="true">
              <f:selectItems
                value="#{correspondenceSearch.correspondenceDef}" />
            </af:selectOneChoice>
            <af:selectOneChoice label="Correspondence Category"
              id="correspondenceCategory"
              value="#{correspondenceDetail.correspondence.correspondenceCategoryCd}">
              <f:selectItems
                value="#{correspondenceSearch.correspondenceCategoryDef}" />
            </af:selectOneChoice>
            <af:selectOneChoice label="Correspondence Direction"
              id="correspondenceDirection"
              value="#{correspondenceDetail.correspondence.directionCd}">
              <f:selectItems
                value="#{correspondenceSearch.correspondenceDirectionDef}" />
            </af:selectOneChoice>
            <af:panelLabelAndMessage label="Enforcement Linked To"
	    		rendered="#{correspondenceDetail.showLinkedToObj}" >
	    		<af:panelHorizontal>
			    	<af:inputText id="enfLinkToIdText" readOnly="true" 
			    		value="#{correspondenceDetail.linkedToObj}"/>
			    	<af:objectSpacer width="10" height="1"/>
			    	<af:commandButton text="Link"
				   		useWindow="true" windowWidth="975" windowHeight="600"
				    	rendered="#{correspondenceDetail.showLinkedToObj}" 
				    	action="#{correspondenceDetail.linkToEnforcement}">				    	
					</af:commandButton>			    
				</af:panelHorizontal>
			</af:panelLabelAndMessage>
            <af:inputText columns="85" rows="10" maximumLength="1000" label="Additional Info"
              value="#{correspondenceDetail.correspondence.additionalInfo}" />

            <af:objectSpacer height="10" />

            <f:facet name="footer">
              <af:panelButtonBar>
                <af:commandButton text="Create"
                  actionListener="#{correspondenceDetail.createCorrespondence}" />
                <af:commandButton text="Cancel" immediate="true">
                  <af:returnActionListener />
                </af:commandButton>
              </af:panelButtonBar>
            </f:facet>
          </af:panelForm>
        </af:panelGroup>
      </af:page>
    </af:form>
  </af:document>
</f:view>
