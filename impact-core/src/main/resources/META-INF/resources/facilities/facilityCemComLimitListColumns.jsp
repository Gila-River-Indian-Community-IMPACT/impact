<%@ page session="true" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:column formatType="text" headerText="Limit Description"
	sortProperty="limitDesc" sortable="true" id="limitDesc">
	<af:inputText value="#{facilityCemComLimit.limitDesc}" readOnly="true"
		valign="middle">
	</af:inputText>
</af:column>

<af:column formatType="text" headerText="Source of Limit"
	sortProperty="limitSource" sortable="true" id="limitSource"
	width="45px" noWrap="true">
	<af:inputText value="#{facilityCemComLimit.limitSource}"
		readOnly="true" valign="middle">
	</af:inputText>
</af:column>

<af:column headerText="Start Monitoring Limit" formatType="text"
	width="65px" sortProperty="startDate" sortable="true" id="startDate">
	<af:selectInputDate label="Start Monitoring Limit" readOnly="true"
		valign="middle" required="true"
		value="#{facilityCemComLimit.startDate}">
		<af:validateDateTimeRange minimum="1900-01-01" />
	</af:selectInputDate>
</af:column>

<af:column headerText="End Monitoring Limit" formatType="text"
	width="65px" sortProperty="endDate" sortable="true" id="endDate">
	<af:selectInputDate label="End Monitoring Limit" readOnly="true"
		valign="middle" required="true" value="#{facilityCemComLimit.endDate}">
		<af:validateDateTimeRange minimum="1900-01-01" />
	</af:selectInputDate>
</af:column>

<af:column formatType="text" headerText="Additional Information"
	sortProperty="addlInfo" sortable="true" id="addlInfo">
	<af:inputText value="#{facilityCemComLimit.addlInfo}" readOnly="true"
		valign="middle">
	</af:inputText>
</af:column>

<af:column sortable="true" sortProperty="addedBy" formatType="text"
	headerText="Added By" noWrap="true" rendered="#{!facilityProfile.publicApp}" 
	width="55px">
	<af:selectOneChoice readOnly="true"
		value="#{facilityCemComLimit.addedBy}">
		<f:selectItems value="#{infraDefs.basicUsersDef.allItems}" />
	</af:selectOneChoice>
</af:column>

<af:column sortable="false" formatType="icon"
	headerText="Periodic Limit Trend Report"
	rendered="#{facilityProfile.internalApp}" width="80px">
	<af:commandButton text="Show Report"
		action="#{facilityProfile.showPeriodicLimitTrendReport}">
		<t:updateActionListener property="#{facilityProfile.trendRptLimId}"
			value="#{facilityCemComLimit.limId}" />
		<t:updateActionListener
			property="#{facilityProfile.trendRptLimitDesc}"
			value="#{facilityCemComLimit.limitDesc}" />
		<t:updateActionListener property="#{facilityProfile.trendRptMonId}"
			value="#{facilityCemComLimit.monId}" />
		<t:updateActionListener
			property="#{facilityProfile.trendRptMonitorId}"
			value="#{facilityCemComLimit.monitorId}" />
		<t:updateActionListener
			property="#{facilityProfile.trendRptCorrLimitId}"
			value="#{facilityCemComLimit.corrLimitId}" />
	</af:commandButton>
</af:column>

<af:column sortable="false" formatType="icon"
	headerText="Audit Limit Trend Report"
	rendered="#{facilityProfile.internalApp}" width="72px">
	<af:commandButton text="Show Report"
		action="#{facilityProfile.showAuditLimitTrendReport}">
		<t:updateActionListener property="#{facilityProfile.trendRptLimId}"
			value="#{facilityCemComLimit.limId}" />
		<t:updateActionListener
			property="#{facilityProfile.trendRptLimitDesc}"
			value="#{facilityCemComLimit.limitDesc}" />
		<t:updateActionListener property="#{facilityProfile.trendRptMonId}"
			value="#{facilityCemComLimit.monId}" />
		<t:updateActionListener
			property="#{facilityProfile.trendRptMonitorId}"
			value="#{facilityCemComLimit.monitorId}" />
		<t:updateActionListener
			property="#{facilityProfile.trendRptCorrLimitId}"
			value="#{facilityCemComLimit.corrLimitId}" />
	</af:commandButton>
</af:column>
