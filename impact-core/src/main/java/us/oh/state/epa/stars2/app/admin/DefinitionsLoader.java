package us.oh.state.epa.stars2.app.admin;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.adhoc.DataGridCellDef;
import us.oh.state.epa.stars2.database.adhoc.DataGridRow;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

@SuppressWarnings("serial")
public class DefinitionsLoader extends AppBase {
    private Hashtable<String, DataGridRow> datasetDefinitions = 
        new Hashtable<String, DataGridRow>(0);
    private DefinitionCategory rootCategory = new DefinitionCategory("root");
    private int group;
    private String defConfigNode;

    public String getDefConfigNode() {
		return defConfigNode;
	}

	public void setDefConfigNode(String defConfigNode) {
		this.defConfigNode = defConfigNode;
	}

	public static void main(String[] args) {
        // set the system variable for the location of the xml files since we're
        // not in the production context.

        DefinitionsLoader dl = new DefinitionsLoader();
        DefinitionCategory dc = dl.loadFromConfigMgr();
        if (dc == null) {
            System.out.println("null root node");
        }
    }
    
    public final DataGridRow getDataSetDefinition(DefinitionSet ds) throws DAOException {
        if (!datasetDefinitions.containsKey(ds.getPath())) {
            createDataSetDef(ds);
        }
        return datasetDefinitions.get(ds.getPath());
    }

    private void createDataSetDef(DefinitionSet ds) throws DAOException {
        // add the definition to the hashtable
        DataGridRow dgr = setup(ds);
        datasetDefinitions.put(ds.getPath(), dgr);
    }

    public final DataGridRow setup(DefinitionSet ds) throws DAOException {
        Hashtable<String, DataGridCellDef> hMetaData = null;

        try {
            InfrastructureService service = ServiceFactory.getInstance().getInfrastructureService();
            hMetaData = service.getMetaData(ds.getTable());
        } catch (ServiceFactoryException sfe) {
            logger.error(sfe.getMessage(), sfe);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
        }

        return setupDataSetDef(ds, hMetaData);
    }

    private DataGridRow setupDataSetDef(DefinitionSet ds,
            Hashtable<String, DataGridCellDef> tableDefinition)throws DAOException {
        // prune the list of columns in the table and update
        // various parameters based on the XML.
        DataGridRow datasetDefinition = new DataGridRow();
        ArrayList<DefinitionField> fields = ds.getFields();
        for (int i = 0; i < fields.size(); i++) {
            DefinitionField df = fields.get(i);
            DataGridCellDef dgcd = tableDefinition.get(df
                    .getDbColumn());
            if(dgcd == null) {
                String s = "The database attribute " + df.getDbColumn() + " does not appear to exist (check capitalization).";
                logger.error(s);
                throw new DAOException(s);
            }
            dgcd.setHeaderText(df.getLabel());
            dgcd.setPickListTable(df.getPickListTable());
            dgcd.setPickListColumn(df.getPickListColumn());
            dgcd.setPickListDisplay(df.getPickListDisplay());
            dgcd.setOrderBy(df.getOrderBy());
            dgcd.setReadOnly(false);
            if ((dgcd.isPrimaryKey()) || (!df.isUpdatable())) {
                dgcd.setReadOnly(true);
            }
            if (dgcd.isPrimaryKey()) {
                dgcd.setRequired(true);
            } else {
                dgcd.setRequired(false);
            }
            datasetDefinition.addCell(dgcd);
            tableDefinition.remove(df.getDbColumn());
        }

        // now go through any remaining columns and if they are PK's,
        // add them to the datasetDefinition.
        Enumeration<String> e = tableDefinition.keys();
        while (e.hasMoreElements()) {
            String field = e.nextElement();
            DataGridCellDef dgcd = tableDefinition.get(field);
            if (dgcd.isPrimaryKey()) {
                datasetDefinition.addCell(dgcd);
            }
        }
        return datasetDefinition;
    }

    private void loadFields(Node id, DefinitionSet ds) {
        Node[] fields = Config.findNodes(id);
        if (fields.length > 0) {
            ds.setCustom(true);
            for (int i = 0; i < fields.length; i++) {
                DefinitionField df = new DefinitionField();
                df.setDbColumn(fields[i].getAsString("column"));
                df.setLabel(fields[i].getAsString("label"));
                df.setMaxLength(0);
                if (fields[i].attributeExists("pickListTable")) {
                    df.setPickListTable(fields[i].getAsString("pickListTable"));
                    df.setPickListColumn(fields[i]
                            .getAsString("pickListColumn"));
                    df.setPickListDisplay(fields[i]
                            .getAsString("pickListDisplay"));
                    df.setInputType("pickList");
                    df.setOrderBy(fields[i].getAsString("orderBy"));
                } else {
                    df.setPickListTable("");
                    df.setInputType("text");
                }
                if (fields[i].getAsString("update").equals("T")) {
                    df.setUpdatable(true);
                } else {
                    df.setUpdatable(false);
                }
                ds.addDefinitionField(df);
            }
        } else {
            ds.setCustom(false);
        }
    }

    private DefinitionCategory loadNode(Node id, DefinitionCategory container) {
        String groupText = "";
        for (int k = 0; k < group; k++) {
            groupText = groupText + "\t";
        }

        Node[] subCategories = Config.findNodes(id);
        for (int i = 0; i < subCategories.length; i++) {
            String name = subCategories[i].getName();
            if (name.equals("category")) {
                group++;
                // subCategories[i].getAsString("label"));
                DefinitionCategory dc = new DefinitionCategory(subCategories[i]
                        .getAsString("label"));
                if (container.getPath().equals("")) {
                    dc.setPath(subCategories[i].getAsString("label"));
                } else {
                    dc.setPath(container.getPath() + ","
                            + subCategories[i].getAsString("label"));
                }
                container.addCategory(dc);
                dc.setLabel(subCategories[i].getAsString("label"));
                if (subCategories[i].attributeExists("description")) {
                    dc.setDescription(subCategories[i]
                            .getAsString("description"));
                } else {
                    dc.setDescription("");
                }
                loadNode(subCategories[i], dc);
            } else if (name.equals("definition-set")) {
                // subCategories[i].getAsString("label"));
                DefinitionSet ds = new DefinitionSet();
                ds.setPath(container.getPath() + ","
                        + subCategories[i].getAsString("label"));
                ds
                        .setColumnPrefix(subCategories[i]
                                .getAsString("columnPrefix"));
                ds.setContentType(subCategories[i].getAsString("contentType"));
                if (subCategories[i].getAsString("create").equals("T")) {
                    ds.setCreate(true);
                } else {
                    ds.setCreate(false);
                }
                if (subCategories[i].attributeExists("deprecateModel")) {
                    String depModel = subCategories[i]
                            .getAsString("deprecateModel");
                    ds.setDeprecateModel(depModel);
                    if (depModel.equals("none")) {
                        ds.setDeprecate(false);
                    } else {
                        ds.setDeprecate(true);
                    }
                } else {
                    ds.setDeprecate(false);
                    ds.setDeprecateModel("none");
                }

                if (subCategories[i].attributeExists("orderBy")) {
                    ds.setOrderBy(subCategories[i].getAsString("orderBy"));
                }

                if (subCategories[i].attributeExists("description")) {
                    ds.setDescription(subCategories[i]
                            .getAsString("description"));
                } else {
                    ds.setDescription("");
                }

                if (subCategories[i].attributeExists("importClass")) {
                    ds.setImportClass(subCategories[i]
                            .getAsString("importClass"));
                } else {
                    ds.setImportClass("");
                }

                if (subCategories[i].attributeExists("contentType")) {
                    if (subCategories[i].getAsString("contentType").equals(
                            "custom")) {
                        ds.setCustom(true);
                    } else {
                        ds.setCustom(false);
                    }
                } else {
                    ds.setCustom(false);
                }

                ds.setTable(subCategories[i].getAsString("table"));
               
                if (subCategories[i].getAsString("update").equals("T")) {
                    ds.setUpdate(true);
                } else {
                    ds.setUpdate(false);
                }
                ds.setLabel(subCategories[i].getAsString("label"));
                loadFields(subCategories[i], ds);
                container.addDefinition(ds);
            }
            group = 0;
        }
        return container;
    }

    public final DefinitionCategory loadFromConfigMgr() {
        us.oh.state.epa.stars2.framework.config.Node rootNode = Config
                .findNode(defConfigNode);
        rootCategory.setPath("");

        return loadNode(rootNode, rootCategory);
    }

    private DefinitionSet getDefinition(DefinitionCategory dc, String path) {
        DefinitionSet ret = null;
        /*
         * This method is a private supporter function to the public getDefSet
         * It is expected to call itself recursively as it walks through the
         * provided path and Category elements.
         */
        String[] pathElements = path.split(",");
//        logger.debug("Definition Path is " + path + " PathElements length is " + pathElements.length);
        if (pathElements.length > 1) {
            String newPath = path.substring(path.indexOf(",") + 1);
            DefinitionCategory dx = dc.getCategory(pathElements[0]);
            ret = getDefinition(dx, newPath);
        } else {
            /*
             * we're at the last element so we can return the DefinitionSet
             */
            ret = dc.getDefinition(path); 
        }
        return ret;
    }

    public DefinitionSet getDefSet(String path) {
        /*
         * This method provides a way to get an arbitrary DefinitionSet using
         * the key(s) to that set as a path, which must be separated with ','.
         * The last element in the path is expected to be the name of the
         * DefinitionSet. All preceding elements are expected to be 1..n
         * categories.
         */
        return getDefinition(rootCategory, path);
    }
    
    public DefinitionCategory getRootCategory() {
    	return rootCategory;
    }
}
