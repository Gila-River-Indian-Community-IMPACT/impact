package us.oh.state.epa.stars2.database.adhoc;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.app.admin.DefinitionCategory;
import us.oh.state.epa.stars2.app.admin.DefinitionsLoader;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

@SuppressWarnings("serial")
public class DataSet extends AppBase {
    private List<DataGridRow> rows = new ArrayList<DataGridRow>();
    private String[] columnHeaders;
    private String orderBy = "";

    /*
     * Variable to hold definitions of all columns in dataset.
     * 
     * Steps: 
     * 1. get columns defined in the XML 
     * 2. get primary keys and meta data 
     * 3. update meta data for existing columns 
     * 4. add any missing columns that are primary keys and set edit=false 
     *    of existing columns that are primary keys 
     * 5. provide convenience methods for getting select, update, and insert 
     *    methods based on the fields and values for a provided row.
     */

    private String table;
    private DataGridRow datasetDefinition = new DataGridRow();
    private DataGridRow newRow;
	private InfrastructureService infrastructureService;

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

    private void reset() {
        //clear all existing data
        rows = new ArrayList<DataGridRow>();
        columnHeaders = new String[0];
    }
    
    public DataSet() {
        super();
    }

    public void setRows(List<DataGridRow> rows) {
        this.rows = rows;
    }
    public static void main(String[] args) {
        DefinitionsLoader dl = new DefinitionsLoader();
        DefinitionCategory dc = dl.loadFromConfigMgr();
        if (dc != null) {
            DataSet ds = new DataSet();
            // ds.setup(dl.getDefSet("SCC (Custom Demo)|Custom Test"));
            try {
                ds.retrieveResultSet();
                ds.printAll();
            } catch (Exception e) {
                System.out.println("error retrieving result set: " + e);
            }
        } else {
            System.out.println("Unable to load definitions");
        }
    }

    public final String[] getColumnHeaders() {
        return columnHeaders;
    }

    public final void setup(String table, DataGridRow dgr, String orderBy) {
        this.setTable(table);
        this.orderBy = orderBy;
        datasetDefinition = dgr;
    }

    public final String getColumnHeader(int i) {
        String ret = "Invalid header";

        if (i < columnHeaders.length) {
            ret = columnHeaders[i];
        }

        return ret;
    }

    public final void setColumnHeaders(String[] columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    public final DataGridRow getRow(int key) {
        return rows.get(key);
    }

    public final DataGridRow getNewRow() {
        if (newRow == null) {
            createNewRow();
        }
        return newRow;
    }

    public final List<DataGridRow> getRows() {
        return rows;
    }

    public final int getColumnCount() {
        return datasetDefinition.getSize();
    }

    public final DataGridRow[] getRowsAsArray() {
        DataGridRow[] x = rows.toArray(new DataGridRow[0]);
        return x;
    }

    public final void addRow(DataGridRow dgr) {
        rows.add(dgr);
    }

    public final void print(int key) {
        DataGridRow dgr = rows.get(key);
        print(dgr);
    }

    public final void print(DataGridRow dgr) {
        DataGridCell[] cells = dgr.getCells();
        for (int k = 0; k < cells.length; k++) {
            logger.debug("\t" + cells[k].getHeaderText());
            if (k == cells.length - 1) {
                System.out.println();
            }
        }
        for (int k = 0; k < cells.length; k++) {
            logger.debug("\t\t" + cells[k].getValue());
            if (k == cells.length - 1) {
                System.out.println();
            }
        }
    }

    public final DataGridRow createNewRow() {
        newRow = new DataGridRow();
        // go through the columns in the Def and add them to this row.
        DataGridCellDef[] dgcd = getDataSetDefinition().getDefCells();
        for (int i = 0; i < dgcd.length; i++) {
            DataGridCell dgc = new DataGridCell();
            dgc.setField(dgcd[i].getField());
            dgc.setInitialValue("");
            dgc.setHeaderText(dgcd[i].getHeaderText());
            dgc.setMaximumLength(dgcd[i].getMaximumLength());
            dgc.setDataType(dgcd[i].getDataType());
            dgc.setDataSet(this);
            dgc.setPickList(dgcd[i].isPickList());
            dgc.setRequired(!dgcd[i].isAllowNull());
            newRow.addCell(dgc);
        }
        return newRow;
    }

    public final int getSize() {
        return rows.size();
    }

    public final void printAll() {
        int row = 1;
        Iterator<?> i = rows.iterator();
        while (i.hasNext()) {
            DataGridRow dgr = (DataGridRow) i.next();
            logger.debug("Row: " + row++);
            print(dgr);
        }
    }

    public final String getTable() {
        return table;
    }

    public final void setTable(String table) {
        this.table = table;
    }

    public final DataGridRow getDataSetDefinition() {
        return datasetDefinition;
    }

    public final boolean insert(DataGridRow dgr) {
        boolean ret = false;
        
        try {
            ret = getInfrastructureService().insertAdHoc(dgr, getTable(), datasetDefinition);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Error inserting data");
            logger.error(re.getMessage(), re);
        }
        
        return ret;
    }
    
    public final boolean update(DataGridRow dgr) {
        boolean ret = false;
        
        try {
            ret = getInfrastructureService().updateAdHoc(dgr, getTable(), datasetDefinition);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Error updating data");
            logger.error(re.getMessage(), re);
        }

        return ret;
    }

    public final List<SelectItem> retrievePickList(String field) {
        List<SelectItem> ret = null;
        
        try {
            ret = getInfrastructureService().retrievePickList(datasetDefinition, field);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Error retrieving picklist data");
            logger.error(re.getMessage(), re);
        }
        return ret;
    }
    
    public final void retrieveResultSet() {
        reset();
        DataSet dsTemp = null;
        
        try { 
            dsTemp = getInfrastructureService().retrieveDataSet(this);
        } catch (RemoteException re) {
            DisplayUtil.displayError("Error retrieving data");
            logger.error(re.getMessage(), re);
        }
        
        this.setRows(dsTemp.getRows());
        this.setColumnHeaders(dsTemp.getColumnHeaders());
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
