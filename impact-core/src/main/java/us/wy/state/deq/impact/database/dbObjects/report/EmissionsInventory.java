package us.wy.state.deq.impact.database.dbObjects.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class EmissionsInventory extends BaseDB {
	
	private static final long serialVersionUID = 1L;
	
	private List<EmissionInventoryRow> inventoryList;
	
	
	public EmissionsInventory(List<EmissionInventoryRow> inventoryList) {
		super();
		this.inventoryList = inventoryList;
	}

	public final List<EmissionInventoryRow> getInventoryList() {
		
		if (this.inventoryList == null) {
			this.inventoryList = new ArrayList<EmissionInventoryRow>();
		}
		return inventoryList;
	}

	public final void setInventoryList(List<EmissionInventoryRow> inventoryList) {
		this.inventoryList = inventoryList;
		if (this.inventoryList == null) {
			this.inventoryList = new ArrayList<EmissionInventoryRow>();
		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
}
