package us.oh.state.epa.stars2.webcommon;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/*
 * In JSF 1.1, it is not possible to directly pass an argument to a JSF
 * backing bean property from an EL expression - the JSF EL does not support
 * method arguments. However, if a backing bean property implements
 * java.util.Map, an argument can be passed to its get() method as a key.
 * Example:
 * 
 * #{bean.property[argument]}
 * 
 * We can leverage this by having a backing bean declare an inner class
 * that implements java.util.Map, and whose get() method is passed
 * an argument. Only get() is of interest - all other methods in java.util.Map
 * are not needed. The ELMap abstract class provides dummy implementations for 
 * all methods in java.util.Map except get().
 */
@SuppressWarnings("unchecked")
public abstract class ELMap implements Map {
    public final int size() {
        return 0;
    }

    public final boolean isEmpty() {
        return false;
    }

    public final boolean containsKey(Object object) {
        return false;
    }

    public final boolean containsValue(Object object) {
        return false;
    }

    public final Object put(Object k, Object v) {
        return null;
    }

    public final Object remove(Object object) {
        return null;
    }

    public final void putAll(Map map) {
    }

    public final void clear() {
    }

    public final Set<?> keySet() {
        return null;
    }

    public final Collection<?> values() {
        return null;
    }

    public final Set<Map.Entry> entrySet() {
        return null;
    }
}
