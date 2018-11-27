<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>

<af:objectSpacer height="10" />

<af:panelForm rows="2" maxColumns="1"
	labelWidth="250px" width="98%" >
	<af:selectOneChoice id="letterTypeCd"
		label="Letter Type: " 
		readOnly="#{!projectTrackingDetail.editMode}"
		value="#{projectTrackingDetail.project.letterTypeCd}" >
		<f:selectItems
			value="#{projectTrackingReference.letterTypeDefs.items[
						(empty projectTrackingDetail.project.letterTypeCd
							? '' : projectTrackingDetail.project.letterTypeCd)]}" />
	</af:selectOneChoice>		
</af:panelForm>