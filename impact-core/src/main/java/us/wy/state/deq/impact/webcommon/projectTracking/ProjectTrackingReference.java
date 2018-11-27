package us.wy.state.deq.impact.webcommon.projectTracking;

import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.def.AgencyDef;
import us.oh.state.epa.stars2.def.BLMFieldOfficeDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.BudgetFunctionDef;
import us.oh.state.epa.stars2.def.ContractStatusDef;
import us.oh.state.epa.stars2.def.ContractTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.DivisionDef;
import us.oh.state.epa.stars2.def.ModelingProtocolStatusTypeDef;
import us.oh.state.epa.stars2.def.NEPACateogryTypeDef;
import us.oh.state.epa.stars2.def.NEPALevelTypeDef;
import us.oh.state.epa.stars2.def.NationalForestDef;
import us.oh.state.epa.stars2.def.NationalParkDef;
import us.oh.state.epa.stars2.def.ProjectEIStatusTypeDef;
import us.oh.state.epa.stars2.def.ProjectGrantStatusDef;
import us.oh.state.epa.stars2.def.ProjectLetterTypeDef;
import us.oh.state.epa.stars2.def.ProjectOutreachCategoryDef;
import us.oh.state.epa.stars2.def.ProjectStageDef;
import us.oh.state.epa.stars2.def.ProjectStateDef;
import us.oh.state.epa.stars2.def.ProjectTrackingEventTypeDef;
import us.oh.state.epa.stars2.def.ProjectTypeDef;
import us.oh.state.epa.stars2.def.ShapeDef;
import us.oh.state.epa.stars2.def.State;
import us.oh.state.epa.stars2.webcommon.AppBase;

@SuppressWarnings("serial")
public class ProjectTrackingReference extends AppBase {
	
	public final DefSelectItems getProjectTypeDefs() {
		return ProjectTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getProjectStateDefs() {
		return ProjectStateDef.getData().getItems();
	}
	
	public final DefSelectItems getProjectTrackingEventTypeDefs() {
		return ProjectTrackingEventTypeDef.getData().getItems();
	}
	
	public final List<SelectItem> getProjectTrackingEventsForNEPA() {
		return ProjectTrackingEventTypeDef
				.getProjectTrackingEvents(ProjectTrackingEventTypeDef.NEPA);
	}
	
	public final List<SelectItem> getProjectTrackingEventsForGrants() {
		return ProjectTrackingEventTypeDef
				.getProjectTrackingEvents(ProjectTrackingEventTypeDef.GRANTS);
	}
	
	public final List<SelectItem> getProjectTrackingEventsForLetters() {
		return ProjectTrackingEventTypeDef
				.getProjectTrackingEvents(ProjectTrackingEventTypeDef.LETTERS);
	}
	
	public final DefSelectItems getAgencyDefs() {
		return AgencyDef.getData().getItems();
	}
	
	public final DefSelectItems getDivisionDefs() {
		return DivisionDef.getData().getItems();
	}
	
	public final DefSelectItems getNEPACategoryTypeDefs() {
		return NEPACateogryTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getNEPALevelTypeDefs() {
		return NEPALevelTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getBLMFieldOfficeDefs() {
		return BLMFieldOfficeDef.getData().getItems();
	}
	
	public final DefSelectItems getModelingProtocolStatusTypeDefs() {
		return ModelingProtocolStatusTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getNationalForestDefs() {
		return NationalForestDef.getData().getItems();
	}
	
	public final DefSelectItems getNationalParkDefs() {
		return NationalParkDef.getData().getItems();
	}
	
	public final DefSelectItems getProjectEIStatusTypeDefs() {
		return ProjectEIStatusTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getProjectStageDefs() {
		return ProjectStageDef.getData().getItems();
	}
	
	public final DefSelectItems getAdjacentStateDefs() {
		return State.getAdjacentStateData().getItems();
	}
	
	public final DefSelectItems getOutreachCategoryDefs() {
		return ProjectOutreachCategoryDef.getData().getItems();
	}
	
	public final DefSelectItems getGrantStatusDefs() {
		return ProjectGrantStatusDef.getData().getItems();
	}
	
	public final DefSelectItems getLetterTypeDefs() {
		return ProjectLetterTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getShapeDefs() {
		return ShapeDef.getData().getItems();
	}
	
	public final DefSelectItems getContractStatusDefs() {
		return ContractStatusDef.getData().getItems();
	}
	
	public final DefSelectItems getContractTypeDefs() {
		return ContractTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getBudgetFunctionDefs() {
		return BudgetFunctionDef.getData().getItems();
	}
	
}
