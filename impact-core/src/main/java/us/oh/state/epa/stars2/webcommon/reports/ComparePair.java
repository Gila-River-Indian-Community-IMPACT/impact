package us.oh.state.epa.stars2.webcommon.reports;

import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeSet;

/*
 * Class ComparePair is used to compare two hashmaps and generate a TreeSet of the results
 * The results is a TreeSet of ComparePair objects that is compared based upon the name.
 * Each ComparePair object contains the name and a reference to both the corresponding object
 * in the original set and the corresponding object in the compare set--if that name appears
 * in each.  Otherwise one or the other references are null to indicate that set does not
 * contain the value.
 * 
 * TreeSet is used so the names can be retrieved in sorted order.
 */

@SuppressWarnings("serial")
public class ComparePair implements Comparable<ComparePair>, Serializable {
    private String name;
    private Object orig;
    private Object comp;

    ComparePair(String name, Object orig, Object comp) {
        this.name = name;
        this.orig = orig;
        this.comp = comp;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public final int compareTo(ComparePair b) {
        return name.compareTo(b.name);
    }

    /**
     * @param orig
     * @param comp
     * @return
     */
    public static TreeSet<ComparePair> compareObjects(
            HashMap<String, Object> orig, HashMap<String, Object> comp) {
        TreeSet<ComparePair> relations = new TreeSet<ComparePair>();
        for (String nameO : orig.keySet()) {
            Object o = comp.remove(nameO);
            if (null != o) {
                relations.add(new ComparePair(nameO, orig.get(nameO), o));
            } else {
                relations.add(new ComparePair(nameO, orig.get(nameO), null));
            }
        }
        // Go through remaining items in compare map
        for (String nameC : comp.keySet()) {
            relations.add(new ComparePair(nameC, null, comp.get(nameC)));
        }
        return relations;
    }

    /**
     * @return
     */
    public final Object getComp() {
        return comp;
    }

    /**
     * @return
     */
    public final String getName() {
        return name;
    }

    /**
     * @return
     */
    public final Object getOrig() {
        return orig;
    }
}
