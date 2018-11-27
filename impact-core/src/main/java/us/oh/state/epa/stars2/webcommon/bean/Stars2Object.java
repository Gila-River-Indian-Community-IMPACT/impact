package us.oh.state.epa.stars2.webcommon.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.adf.view.faces.component.UIXTable;

// This object is needed for front end works with tables that works with basic objects like String and Integer.

@SuppressWarnings("serial")
public class Stars2Object implements java.io.Serializable {
    private Object value;

    public Stars2Object() {
    }

    /**
     * @param value
     */
    public Stars2Object(Object value) {
        this.value = value;
    }

    public final Object getValue() {
        return value;
    }

    /**
     * @param value
     */
    public final void setValue(Object value) {
        this.value = value;
    }

    /**
     * @param inString
     * @return
     */
    public static List<Stars2Object> toStar2Object(List<String> inString) {

        List<Stars2Object> stars2Objs = new ArrayList<Stars2Object>();
        for (String inS : inString) {
            stars2Objs.add(new Stars2Object(inS));
        }
        return stars2Objs;
    }

    /**
     * @param inObjs
     * @return
     */
    public static List<String> fromStar2Object(List<Stars2Object> inObjs) {
        List<String> outS = new ArrayList<String>();
        
        if (inObjs != null) {
            for (Stars2Object inObj : inObjs) {
                if (inObj.getValue() != null) {
                    outS.add(new String((String) inObj.getValue()));
                } else {
                    outS.add(new String());                   
                }
            }
        }
        return outS;
    }

    /**
     * @param inInteger
     * @return
     */
    public static List<Stars2Object> toStar2IntObject(List<Integer> inInteger) {

        List<Stars2Object> stars2Objs = new ArrayList<Stars2Object>();
        for (Integer inS : inInteger) {
            stars2Objs.add(new Stars2Object(inS));
        }
        return stars2Objs;
    }

    /**
     * @param inObjs
     * @return
     */
    public static List<Integer> fromStar2IntObject(List<Stars2Object> inObjs) {
        List<Integer> outS = new ArrayList<Integer>();
        
        if (inObjs != null) {
            for (Stars2Object inObj : inObjs) {
                if (inObj.getValue() != null) {
                    outS.add(new Integer((Integer) inObj.getValue()));
                } else {
                    outS.add(new Integer(0));                   
                }
            }
        }
        return outS;
    }

    /**
     * @param inObjs
     * @param delObjs
     * @return
     */
    public static List<Stars2Object> deleteItems(List<Stars2Object> inObjs,
            List<Stars2Object> delObjs) {
        List<Stars2Object> retObjs = inObjs;
        for (Stars2Object delObject : delObjs) {
            retObjs.remove(delObject);
        }
        return retObjs;
    }

    /**
     * @param table
     * @param list
     * @return
     * 
     */
    public static List<Stars2Object> deleteStars2Objects(UIXTable table,
            List<Stars2Object> list) {
        List<Stars2Object> retObjs;
        List<Stars2Object> delObjects = new ArrayList<Stars2Object>();

        Iterator<?> it = table.getSelectionState().getKeySet().iterator();
        Object oldKey = table.getRowKey();
        while (it.hasNext()) {
            Object obj = it.next();
            table.setRowKey(obj);
            delObjects.add((Stars2Object) table.getRowData());
        }

        retObjs = Stars2Object.deleteItems(list, delObjects);

        table.setRowKey(oldKey);
        table.getSelectionState().clear();

        return retObjs;
    }
}
