<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document title="Referral End Date">
    <f:verbatim>
      <script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script>
    </f:verbatim>
    <af:form>
      <af:messages />

      <af:panelForm>
        <af:selectInputDate label="End Date :" required="true" showRequired="true"
          value="#{activityProfile.changeEndDt}" > <af:validateDateTimeRange minimum="1970-01-01"/></af:selectInputDate>

      </af:panelForm>

      <af:objectSpacer width="100%" height="15" />
      <afh:rowLayout halign="center">
        <af:panelButtonBar>
          <af:commandButton text="Submit"
            rendered="#{!activityProfile.readOnlyUser}"
            actionListener="#{activityProfile.endReferral}" />
        </af:panelButtonBar>
      </afh:rowLayout>
    </af:form>
  </af:document>
</f:view>
