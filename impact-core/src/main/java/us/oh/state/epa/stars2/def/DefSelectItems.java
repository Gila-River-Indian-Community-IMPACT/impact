package us.oh.state.epa.stars2.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.webcommon.ELMap;

/*
 * Instances of DefSelectItems can be used to back a JSF select-type
 * component when the component's value comes from a database DEF table.
 * DefSelectItems handles DEF item deprecation as follows:
 * - If the current value does not reference a deprecated DEF item,
 *   DefSelectItems returns only the current - i.e., non-deprecated -
 *   items
 * - If the object references a deprecated DEF item, DefSelectItems
 *   returns all items - both current and deprecated.
 * DefSelectItems handles both multi-choice (e.g., af:selectManyCheckbox)
 * and single-choice (e.g., af:selectOneChoice) JSF components.
 * DefSelectItems can also be used to retrieve the description for a certain
 * DEF code from an EL expression. Example:
 *
 * Examples:
 * <af:selectOneChoice value="#{bean.mainPollutantCD}">
 *   <f:selectItems value="#{reference.pollutantItems.items[bean.mainPollutantCD]}"/>
 * </af:selectOneChoice>
 *
 * <af:selectManyCheckbox value="#{bean.pollutantCDs}">
 *   <f:selectItems value="#{reference.pollutantItems.items[bean.pollutantCDs]}"/>
 * </af:selectOneChoice>
 * 
 * <af:outputText value="#{reference.pollutantItems.itemDesc[bean.mainPollutantCD]}"/>
 */
@SuppressWarnings("serial")
public class DefSelectItems implements java.io.Serializable {
    private List<SelectItem> currentItems = new ArrayList<SelectItem>(1);
    private List<SelectItem> allItems = new ArrayList<SelectItem>(1);
    private List<SelectItem> allItemsEnabled = new ArrayList<SelectItem>(1);
    private List<SelectItem> allSearchItems = new ArrayList<SelectItem>(1);
    private Set<Object> deprecatedCodes = new HashSet<Object>(1);
    private List<Object> excludedKeys = new ArrayList<Object>(1);
    private HashMap<String, BaseDef> completeItems = new HashMap<String, BaseDef>(1);
    public static String INACTIVE = "(inactive)";

    public void add(BaseDef[] defs) {
        for (BaseDef def : defs) {
            if(def.isDeprecated()) def.setDescription(def.getDescription() + INACTIVE);
            if (def instanceof SimpleIdDef){
                SimpleIdDef td = (SimpleIdDef)def;
                add(td.getId(), td.getDescription(), td.isDeprecated());
            }else{
                add(def.getCode(), def.getDescription(), def.isDeprecated());
            }
            completeItems.put(def.getCode(), def);
        }
    }

    public void add(SimpleDef[] defs) {
        for (SimpleDef def : defs) {
            if(def.isDeprecated()) def.setDescription(def.getDescription() + INACTIVE);
            add(def.getCode(), def.getDescription(), def.isDeprecated());
        }
    }

    public void add(SimpleIdDef[] defs) {
        for (SimpleIdDef def : defs) {
            if(def.isDeprecated()) def.setDescription(def.getDescription() + INACTIVE);
            add(def.getId(), def.getDescription(), def.isDeprecated());
        }
    }

    public void add(Object code, String desc, boolean isDeprecated) {
        if (!excludedKeys.contains(code)) {
            SelectItem item = new SelectItem(code, desc);

            allItemsEnabled.add(item);

            if (isDeprecated) {
                deprecatedCodes.add(code);
                item.setDisabled(true);
            } else {
                currentItems.add(item);
            }
            allItems.add(item);
            SelectItem i;
            if(item.isDisabled()) {
                i = new SelectItem(code, desc, desc, true);
            } else {
                i = item;
            }
            allSearchItems.add(i);
        }
    }

    public void addExcludedKeys(List<Object> excludedKeys) {
        this.excludedKeys = excludedKeys;
    }

    public List<SelectItem> getCurrentItems() {
        return currentItems;
    }

    public List<SelectItem> getAllItems() {
        return allItems;
    }
    
    public String getDescFromAllItem(String code) {
        String ret = null;
        for (SelectItem si : allItems)
            if (si.getValue().equals(code))
                ret = si.getLabel();
        return ret;
    }
    
    public String getDescFromAllItem(Integer code) {
        String ret = null;
        for (SelectItem si : allItems)
            if (si.getValue().equals(code))
                ret = si.getLabel();
        return ret;
    }

    public String getItemDesc(Object itemCode) {
        String ret = null;
        
        if (itemCode != null) {
            for (SelectItem item : allItems) {
                if (itemCode.equals(item.getValue())) {
                    ret = item.getLabel();
                }
            }
        }
        return ret;
    }
    
    public String getItemShortDesc(Object itemCode) {
        String ret = null;
        
        if (itemCode != null) {
            for (SelectItem item : allItems) {
                if (itemCode.equals(item.getValue())) {
                    ret = item.getValue().toString();
                    String desc = item.getLabel();
                    int separator = desc.indexOf(" - ", 0);
                    if(separator > 0 && separator < 10) {
                        ret = desc.substring(0, separator);
                    }
                }
            }
        }
        return ret;
    }
    
    public boolean isItemDepricated(Object itemCode) {
        boolean ret = false;
        
        if (itemCode != null) {
            for (SelectItem item : allItems) {
                if (itemCode.equals(item.getValue())) {
                    ret = item.isDisabled();
                }
            }
        }
        return ret;
    }

    public BaseDef getItem(String code) {
        return completeItems.get(code);
    }

//    public List<SelectItem> getItems(List<Object> codes) {
//        for (Object code : codes) {
//            if (deprecatedCodes.contains(code)) {
//                return allItems;
//            }
//        }
//        return currentItems;
//    }
    
    //  for any items in the list that are deprecated, change so they do not look deprecated  in the allItems list returned.
    public List<SelectItem> getItems(List<Object> codes) {
        boolean returnAllItems = false;
        for (Object code : codes) {
            if (deprecatedCodes.contains(code)) {
                ListIterator<SelectItem> i = allItems.listIterator();
                if(code instanceof String) {
                    while(i.hasNext()) {
                        SelectItem si = i.next();
                        if(((String)si.getValue()).equals((String)code)) {
                            si.setDisabled(false);
                            returnAllItems = true;
                        }
                    }
                } else {
                    while(i.hasNext()) {
                        SelectItem si = i.next();
                        if(((Integer)si.getValue()).equals((Integer)code)) {
                            si.setDisabled(false);
                            returnAllItems = true;
                        }
                    }
                }
            }
        }
        if(returnAllItems) return allItems;
        return currentItems;
    }

//    public List<SelectItem> getItems(Object code) {
//        if (deprecatedCodes.contains(code)) {
//            return allItems;
//        }
//        return currentItems;
//    }
    
    //  If the item is deprecated, change it so it does not look deprecated in the allItems list returned.
    public List<SelectItem> getItems(Object code) {
        if (deprecatedCodes.contains(code)) {
            ListIterator<SelectItem> i = allItems.listIterator();
            if(code instanceof String) {
                while(i.hasNext()) {
                    SelectItem si = i.next();
                    if(((String)si.getValue()).equals((String)code)) {
                        si.setDisabled(false);
                        return allItems;
                    }
                }
            } else {
                while(i.hasNext()) {
                    SelectItem si = i.next();
                    if(((Integer)si.getValue()).equals((Integer)code)) {
                        si.setDisabled(false);
                        return allItems;
                    }
                }
            }
        }
        return currentItems;
    }

    public List<SelectItem> getItems(Object code, List<Object> excludedCodes) {
        List<SelectItem> temp = null;

        if (deprecatedCodes.contains(code)) {
            temp = allItems;
        }
        temp = currentItems;

        List<SelectItem> ret = temp;

        if ((excludedCodes != null) && (excludedCodes.size() > 0)) {
            ret = new ArrayList<SelectItem>(0);

            for (SelectItem item : temp) {
                if (!excludedCodes.contains(item.getValue())) {
                    ret.add(item);
                }
            }
        }
        return ret;
    }

    public List<SelectItem> getItems(List<Object> codes, boolean allEnabled) {
        List<SelectItem> ret = currentItems;

        for (Object code : codes) {
            if (deprecatedCodes.contains(code) && !allEnabled) {
                ret = allItems;
            } else if (allEnabled) {
                ret = allItemsEnabled;
            }
        }

        return ret;
    }

    public List<SelectItem> getItems(Object code, boolean allEnabled) {
        List<SelectItem> ret = currentItems;

        if (deprecatedCodes.contains(code) && !allEnabled) {
            ret = allItems;
        } else if (allEnabled) {
            ret = allItemsEnabled;
        }

        return ret;
    }

    /*
     * public List<SelectItem> getItems() { return currentItems; }
     */

    @SuppressWarnings("unchecked")
    public Map getItemDesc() {
        return new ELMap() {
            public Object get(Object object) {
                return getItemDesc(object);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public Map getItems() {
        return new ELMap() {
            public Object get(Object object) {
                if (object instanceof List) {
                    return getItems((List<Object>) object);
                } else if (object instanceof String) {
                    return getItems(object);
                } else if (object instanceof Integer) {
                    return getItems(object);
                }
                return getCurrentItems();
            }
        };
    }
    
    public List<SelectItem> getAllSearchItems() {
        return allSearchItems;
    }

    public HashMap<String, BaseDef> getCompleteItems() {
        return completeItems;
    }
    
    //  See if item is actually deprecated
    public boolean isItemCurrent(Object code) {
    	if (deprecatedCodes.contains(code)) return false;
    	else return true;

    }
    
    public Object getItemCode(String itemDesc) {
        Object ret = null;
        
        if (itemDesc != null) {
            for (SelectItem item : allItems) {
                if (itemDesc.equalsIgnoreCase(item.getLabel())) {
                    ret = item.getValue();
                }
            }
        }
        return ret;
    }
}
