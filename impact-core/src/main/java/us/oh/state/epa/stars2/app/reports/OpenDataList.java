package us.oh.state.epa.stars2.app.reports;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;

public class OpenDataList implements java.io.Serializable {
    private DataModel data;
    private DataModel columnHeaders;

    // private EmissionUnit emissionUnit;
    // private EmissionUnit[] emissionUnits;
    // private static final int SORT_ASCENDING = 1;
    // private static final int SORT_DESCENDING = -1;

    public OpenDataList(EmissionUnit[] emissionUnits)
    // public OpenDataList()
    {
        List<String> emissions = new ArrayList<String>();
        emissions.add("Emissions Unit");
        emissions.add("SCC");
        emissions.add("SO2");
        emissions.add("NOx");
        emissions.add("CO");
        emissions.add("PM");
        emissions.add("PB");
        emissions.add("VOC");
        emissions.add("Total");

        // Variables to keep track of sum of each of the pollutants
        int SO2Sum = 0;
        int NOxSum = 0;
        int COSum = 0;
        int PMSum = 0;
        int PBSum = 0;
        int VOCSum = 0;

        // create header info
        List<Object> headerList = new ArrayList<Object>();
        for (int i = 0; i < emissions.size(); i++) {
            headerList.add(new ColumnHeader(emissions.get(i).toString(), "400",
                    false));
        }

        columnHeaders = new ListDataModel(headerList);

        List<Object> rowList = new ArrayList<Object>();
        if (true) {
            return; // TODO
        }
        for (int i = 0; i < emissionUnits.length; i++) {
            EmissionUnit tempEU = emissionUnits[i];

            EmissionProcess[] emissionProcesses = tempEU.getEmissionProcesses()
                    .toArray(new EmissionProcess[0]);

            for (int j = 0; j < emissionProcesses.length; j++) {

                List<Object> colList = new ArrayList<Object>();
                EmissionProcess tempEP = emissionProcesses[j];
                colList.add(tempEU.getEpaEmuId());
                colList.add(tempEP.getSccId());
                colList.add(j + 1);
                SO2Sum = SO2Sum + j + 1;
                colList.add(j);
                NOxSum = NOxSum + j;
                colList.add(j + 2);
                COSum = COSum + j + 2;
                colList.add(j);
                PMSum = PMSum + j;
                colList.add(j + 3);
                PBSum = PBSum + j + 3;
                colList.add(j + 2);
                VOCSum = VOCSum + j + 2;
                colList.add(6 * j + 8);

                rowList.add(colList);
                data = new ListDataModel(rowList);
            }
        }
        // Add the last row which is sum of pollutants
        List<Object> colList = new ArrayList<Object>();
        colList.add("Total");
        colList.add("");
        colList.add(SO2Sum);
        colList.add(NOxSum);
        colList.add(COSum);
        colList.add(PMSum);
        colList.add(PBSum);
        colList.add(VOCSum);
        colList.add(SO2Sum + NOxSum + COSum + PMSum + PBSum + VOCSum);
        rowList.add(colList);
        data = new ListDataModel(rowList);

    }

    // ==========================================================================
    // Getters
    // ==========================================================================

    public final DataModel getData() {
        // sort(getSort(), isAscending());
        return data;
    }

    final void setData(DataModel datamodel) {
        // just here to see if the datamodel is updated if
        // preservedatamodel=true
    }

    public final DataModel getColumnHeaders() {
        return columnHeaders;
    }

    // ==========================================================================
    // Public Methods
    // ==========================================================================

    public final Object getColumnValue() {
        Object columnValue = null;
        if (data.isRowAvailable() && columnHeaders.isRowAvailable()) {
            columnValue = ((List<?>) data.getRowData()).get(columnHeaders
                    .getRowIndex());
        }
        return columnValue;
    }

    @SuppressWarnings("unchecked")
    public final void setColumnValue(Object value) {
        if (data.isRowAvailable() && columnHeaders.isRowAvailable()) {
            ((List) data.getRowData()).set(columnHeaders.getRowIndex(), value);
        }
    }

    public final String getColumnWidth() {
        String columnWidth = null;
        if (data.isRowAvailable() && columnHeaders.isRowAvailable()) {
            columnWidth = ((ColumnHeader) columnHeaders.getRowData())
                    .getWidth();
        }
        return columnWidth;
    }

    public final boolean isValueModifiable() {
        boolean valueModifiable = false;
        if (data.isRowAvailable() && columnHeaders.isRowAvailable()) {
            valueModifiable = ((ColumnHeader) columnHeaders.getRowData())
                    .isEditable();
        }
        return valueModifiable;
    }

    // ==========================================================================
    // Protected Methods
    // ==========================================================================

    protected final boolean isDefaultAscending(String sortColumn) {
        return true;
    }
}
