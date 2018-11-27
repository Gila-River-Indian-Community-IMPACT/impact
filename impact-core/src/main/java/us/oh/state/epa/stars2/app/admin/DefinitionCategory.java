package us.oh.state.epa.stars2.app.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import us.oh.state.epa.stars2.webcommon.AppBase;

public class DefinitionCategory extends AppBase {
    private String label = "Categories";
    private String description = "This is the root node";
    private transient TreeMap<String, DefinitionCategory> subCategories = 
        new TreeMap<String, DefinitionCategory>();
    private transient TreeMap<String, DefinitionSet> definitions = 
        new TreeMap<String, DefinitionSet>();
    private String path;

    //the are used for serialization.
    private ArrayList<DefinitionCategory> alSubCategories = new ArrayList<DefinitionCategory>(0);
    private ArrayList<DefinitionSet> alDefinitionSet = new ArrayList<DefinitionSet>(0);
    
    public DefinitionCategory(String label) {
        super();
        this.label = label;
    }

    public final void setPath(String path) {
        this.path = path;
    }

    public final boolean addCategory(DefinitionCategory dc) {
        subCategories.put(dc.getLabel(), dc);
        alSubCategories.add(dc);    //for serialization
        return true;
    }

    public final boolean addDefinition(DefinitionSet ds) {
        definitions.put(ds.getLabel(), ds);
        alDefinitionSet.add(ds);    //for serialization
        return true;
    }

    public final String getLabel() {
        return label;
    }

    public final void setLabel(String label) {
        this.label = label;
    }

    public final int getCategoryCount() {
        return subCategories.size();
    }

    public final int getCategoryCountRecursive() {
        // iterate through ALL children and return the total number of category
        // elements
        int categoryCt = 0;
        Iterator<String> i = subCategories.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            DefinitionCategory dc = subCategories.get(key);
            categoryCt = categoryCt + dc.getCategoryCountRecursive();
        }

        return categoryCt + subCategories.size();
    }

    public final int getDefinitionCount() {
        return definitions.size();
    }

    public final int getDefinitionCountRecursive() {
        // iterate through ALL children and returnthe total number of
        // definitions
        int defCt = 0;

        Iterator<String> ii = subCategories.keySet().iterator();
        while (ii.hasNext()) {
            String key = ii.next();
            DefinitionCategory dc = subCategories.get(key);
            defCt = defCt + dc.getDefinitionCountRecursive();
        }
        return defCt + definitions.size();

    }

    public final TreeMap<String, DefinitionSet> getDefinitions() {
        return definitions;
    }

    public final TreeMap<String, DefinitionCategory> getSubCategories() {
        return subCategories;
    }

    public final void print() {
        Iterator<String> i = subCategories.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            DefinitionCategory dc = subCategories.get(key);
            dc.print();
        }
    }

    public final DefinitionSet getDefinition(String llabel) {
        return definitions.get(llabel);
    }

    public final DefinitionCategory getCategory(String llabel) {
        return subCategories.get(llabel);
    }

    public final String getDescription() {
        return description;
    }

    public final String getPath() {
        return path;
    }

    public final void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * re-instantiates the transient objects after object has been serialzied
     * 
     * @param stream
     *            Output stream.
     * 
     * @throws IOException
     *             Data conversion/formating error.
     * @throws ClassNotFoundException
     *             Could not find class definition.
     */
    private void readObject(java.io.ObjectInputStream stream)
            throws IOException,ClassNotFoundException {
        // Read this stuff in the same order it was written. Note that I
        // use "s" so you can easily see the intermediate value on the
        // debugger.
        stream.defaultReadObject();
        
        //marshall the children back into the TreeMaps
        subCategories = new TreeMap<String, DefinitionCategory>();
        for (DefinitionCategory dc : alSubCategories) {
            subCategories.put(dc.getLabel(), dc);
        }
        
        definitions = new TreeMap<String, DefinitionSet>();
        for (DefinitionSet ds : alDefinitionSet) {
            definitions.put(ds.getLabel(), ds);
        }
        
    }

    public ArrayList<DefinitionCategory> getAlSubCategories() {
        return alSubCategories;
    }

    public void setAlSubCategories(ArrayList<DefinitionCategory> alSubCategories) {
        this.alSubCategories = alSubCategories;
    }

    public ArrayList<DefinitionSet> getAlDefinitionSets() {
        return alDefinitionSet;
    }

    public void setAlDefinitions(ArrayList<DefinitionSet> alDefinitionSet) {
        this.alDefinitionSet = alDefinitionSet;
    }   
}
