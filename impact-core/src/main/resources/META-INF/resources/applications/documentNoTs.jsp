<%@ page session="false" contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<f:view>
  <af:document id="body" title="Printable Application Documents">
    <f:verbatim><script></f:verbatim><h:outputText value="#{infraDefs.js}" /><f:verbatim></script></f:verbatim>
    <af:form usesUpload="true">
      <af:messages />
      <af:panelForm>
      <af:panelButtonBar>
            <af:commandButton text="Close" immediate="true">
              <af:returnActionListener />
            </af:commandButton>
          </af:panelButtonBar>
      </af:panelForm>
    </af:form>
  </af:document>
</f:view>

