package us.oh.state.epa.stars2.webcommon.reports;

import java.util.ArrayList;
import java.util.Comparator;

public class RowContainer implements Comparable {
    ArrayList<String> row;
    private static int facCol = 2;
    private int euCol = -1;
    private static int yearCol = 0;
    private int procCol = -1;
    
    RowContainer () {
        
    }

    public RowContainer(int euCol, int procCol, ArrayList<String> row) {
        this.euCol = euCol;
        this.procCol = procCol;
        this.row = row;
    }
    
    public ArrayList<String> getRow() {
        return row;
    }

    public final int compareTo(Object b) {
        RowContainer rowB = (RowContainer)b;
        if(!row.get(facCol).equals(rowB.getRow().get(facCol))) {
            return row.get(facCol).compareTo(rowB.getRow().get(facCol));
        }
        if(euCol >= 0) {
            if(row.get(euCol).equals("facil")  && !rowB.getRow().get(euCol).equals("facil")) return -1;
            if(rowB.getRow().get(euCol).equals("facil") && !row.get(euCol).equals("facil")) return 1;
            if(!row.get(euCol).equals(rowB.getRow().get(euCol))) {
                return row.get(euCol).compareTo(rowB.getRow().get(euCol));
            }
        }
        if(row.get(yearCol).equals("curr") && !rowB.getRow().get(yearCol).equals("curr")) return -1;
        if(rowB.getRow().get(yearCol).equals("curr") && !row.get(yearCol).equals("curr")) return 1;
        if(!row.get(yearCol).equals(rowB.getRow().get(yearCol))) {
            return rowB.getRow().get(yearCol).compareTo(row.get(yearCol));
        }
        if(procCol >= 0) {
            if(row.get(procCol).equals(" - ") && !rowB.getRow().get(procCol).equals(" - ")) return -1;
            if(rowB.getRow().get(procCol).equals(" - ") && !row.get(procCol).equals(" - ")) return 1;
            if(!row.get(procCol).equals(rowB.getRow().get(procCol))) {
                return row.get(procCol).compareTo(rowB.getRow().get(procCol));
            }
        }
        return 0;
    }

}
