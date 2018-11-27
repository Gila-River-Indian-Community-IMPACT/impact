<%@ page contentType="text/html;charset=windows-1252"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces" prefix="af"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/html" prefix="afh"%>
<%@ taglib uri="http://us.oh.state.epa.stars2/util" prefix="mu"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<af:panelGroup>
  <af:outputText inlineStyle="font-size:75%;color:#666"
    value="An air quality modeling analysis is required for NSRs for
      new installations or modifications, 
      where either the increase of toxic air contaminants from any air 
      contaminant source or the increase of any other pollutant for all air 
      contaminant sources combined exceed a threshold listed below. 
      Permit requests that would have unacceptable impacts cannot be approved as proposed.  
      See the line-by-line NSR instructions for additional information." />
  <af:objectSpacer height="1" />
  <af:outputText inlineStyle="font-size:75%;color:#666"
    value="The specific information listed below must be completed in the 
      Facility Inventory for at least one stack emissions release point or fugitive 
      emissions release point if the requested allowable annual emission rate 
      for this NSR exceeds the following: " />
  <af:panelList>
    <af:outputText inlineStyle="font-size:75%;color:#666"
      value="Particulate Emissions (PE/PM10):  10 tons per year" />
    <af:outputText inlineStyle="font-size:75%;color:#666"
      value="Sulfur Dioxide (SO2): 25 tons per year" />
    <af:outputText inlineStyle="font-size:75%;color:#666"
      value="Nitrogen Oxides (NOx): 25 tons per year"  />
    <af:outputText inlineStyle="font-size:75%;color:#666"
      value="Carbon Monoxide (CO):  100 tons per year" />
    <af:outputText inlineStyle="font-size:75%;color:#666"
      value="Lead (Pb):  0.6 ton per year" />
    <af:outputText inlineStyle="font-size:75%;color:#666"
      value="Toxic Air Contaminants:  1 ton per year." />
  </af:panelList>
  <af:objectSpacer height="1" />
  <af:outputText inlineStyle="font-size:75%;color:#666"
    value="If allowable emissions exceed the above thresholds, please ensure
        that the following stack or fugitive release point information has been
        included in the Facility Inventory." />

  <af:showDetailHeader disclosed="false"
    text="Stack Release Point Modeling Information">
    <af:outputText inlineStyle="font-size:75%;color:#666"
      value="For each stack emissions release point:   An release point is a 
        point at which emissions from an air contaminant source are released 
        into the ambient (outside) air.  Use the dimensions of the tallest nearby 
        (or attached) building, building segment or structure. " />
    <af:panelList>
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Company ID for the Release Point" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Company Description for the Release Point" />
      <af:panelGroup layout="vertical">
        <af:outputText
          inlineStyle="font-size:75%;color:#666"
          value="Type" />
        <af:panelList>
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="vertical stack (unobstructed):  There are no obstructions 
              to upward flow in or on the stack such as a rain cap" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="vertical stack (obstructed): There are obstructions to the 
              upward flow, such as a rain cap, which prevents or inhibits the 
              air flow in a vertical direction" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="non vertical stack: The stack directs the air flow in a 
              direction which is not directly upward" />
        </af:panelList>
      </af:panelGroup>
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Shape: round, square, rectangular" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Dimensions or Diameter" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Cross Sectional Area" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Base Elevation (ft)" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Height from the Ground (ft)" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Temp. at Max. Operation (F)" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Flow Rate at Max. Operation (ACFM)" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Minimum Distance to Fence Line (ft)" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Building Height (ft)" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Building Width (ft)" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Building Length (ft)" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Release Point Latitude:               deg             min           sec" />
      <af:outputText inlineStyle="font-size:75%;color:#666"
        value="Release Point Longitude:            deg             min           sec" />
    </af:panelList>
  </af:showDetailHeader>

  <af:showDetailHeader disclosed="false"
    text="Fugitive Release Point Modeling Information">
    <af:outputText inlineStyle="font-size:75%;color:#666"
      value="For each fugitive emissions release point. For an air contaminant 
    source with multiple fugitive emissions release points, include only the 
    primary release points.  There are two types of fugitive release points.  
    Required fields are indicated below each description." />
    <af:panelList>
      <af:panelGroup layout="vertical">
        <af:outputText
          inlineStyle="font-size:75%;color:#666"
          value="Area:   an open fugitive source characterized as a horizontal 
        area (L x W) with a release height.  For irregular surfaces such as 
        storage piles, enter dimensions of an average cross section; release 
        height is entered as half of the maximum pile height.  For process 
        sources such as crushers, use the process opening (e.g., area of crusher 
        hopper opening) and ignore material handling and storage emissions points." />
        <af:panelList>
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Company ID or Name for the Release Point" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Company Description for the Release Point" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Area Source Dimensions (Length x Width, in feet)" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Release Height (ft)" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Exit Gas Temp. (only if in excess of 100&deg; F) (&deg;F)" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Minimum Distance to the Fence Line (ft)" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Release Point Latitude:               deg             min           sec" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Release Point Longitude:            deg             min           sec" />
        </af:panelList>
      </af:panelGroup>
      <af:panelGroup layout="vertical">
        <af:outputText
          inlineStyle="font-size:75%;color:#666"
          value="Volume:   an unpowered vertical opening, such as a window or roof 
      monitor, characterized as a vertical area (W x H) with a release height, 
      measured at the midpoint of the opening.  Multiple openings in a building 
      may be averaged, if necessary. " />
        <af:panelList>
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Company ID or Name for the Release Point" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Company Description for the Release Point" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Volume Source Dimensions (Height x Width, in feet)" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Release Height (ft)" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Exit Gas Temp. (only if in excess of 100&deg; F) (&deg;F)" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Minimum Distance to the Fence Line (ft)" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Release Point Latitude:               deg             min           sec" />
          <af:outputText
            inlineStyle="font-size:75%;color:#666"
            value="Release Point Longitude:            deg             min           sec" />
        </af:panelList>
      </af:panelGroup>
    </af:panelList>
  </af:showDetailHeader>
</af:panelGroup>
