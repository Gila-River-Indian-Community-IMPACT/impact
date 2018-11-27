package us.oh.state.epa.stars2.app.admin;

import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.ShapeDef;
import us.oh.state.epa.stars2.webcommon.AppBase;

@SuppressWarnings("serial")
public class ServiceCatalogReference extends AppBase {
	
	public final DefSelectItems getShapeDefs() {
		return ShapeDef.getData().getItems();
	}
	
}