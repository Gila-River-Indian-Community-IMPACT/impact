package us.oh.state.epa.stars2.database.adhoc;

import org.apache.commons.collections.map.ListOrderedMap;

public class DataGridRow  implements java.io.Serializable {
    private ListOrderedMap cells = new ListOrderedMap();
    private int currentColumn;

    public DataGridCell[] getCells() {
        DataGridCell[] dgc = new DataGridCell[cells.size()];
        // populate the array
        for (int i = 0; i < cells.size(); i++) {
            DataGridCell dgcTemp = (DataGridCell) cells.getValue(i);
            dgc[i] = dgcTemp;
        }
        return dgc;
    }

    public final DataGridCellDef getCellDef(String field) {
        return (DataGridCellDef) cells.get(field);
    }

    public final DataGridCell getCell(String field) {
        return (DataGridCell) cells.get(field);
    }

    public final DataGridCell getCell(int cell) {
        return (DataGridCell) cells.get(cell);
    }

    public final DataGridCellDef[] getDefCells() {
        DataGridCellDef[] dgc = new DataGridCellDef[cells.size()];
        // populate the array
        for (int i = 0; i < cells.size(); i++) {
            DataGridCellDef dgcTemp = (DataGridCellDef) cells.getValue(i);
            dgc[i] = dgcTemp;
        }
        return dgc;
    }

    public final int getSize() {
        return cells.size();
    }

    public final int getColumnCount() {
        return cells.size();
    }

    public final void addCell(DataGridCell dgc) {
        // check to make sure the cell doesn't already exist
        cells.put(dgc.getField(), dgc);
    }

    public final int getCurrentColumn() {
        return currentColumn;
    }

    public final String[] getValues() {
        // go through all the cells and put the headers into a string array.
        String[] values = new String[cells.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = ((DataGridCell) cells.getValue(i)).getValue();
        }
        return values;
    }

    public final String getColumnValue() {
        if (currentColumn > cells.size() - 1) {
            currentColumn = 0;
        }
        return ((DataGridCell) cells.getValue(currentColumn++)).getValue();
    }
    
    public final String getDisplayValue() {
        if (currentColumn > cells.size() - 1) {
            currentColumn = 0;
        }
        return ((DataGridCell) cells.getValue(currentColumn++)).getDisplayValue();
    }
    
}
