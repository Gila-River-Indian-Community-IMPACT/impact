package us.oh.state.epa.stars2.database.adhoc;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

public class DataGridCell implements java.io.Serializable {
    private String value = "";
    private String headerText = "";
    private String field = "";
    private boolean changed;
    private boolean isReadOnly;
    private boolean PickList;
    private DataSet dataset;
    private int maximumLength;
    private String dataType;
    private transient Logger logger;
    private String displayValue;
    private boolean required=true;

    public DataGridCell() {
        logger = Logger.getLogger(this.getClass()); 
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public boolean isPickList() {
        return PickList;
    }

    public final void setPickList(boolean pickList) {
        PickList = pickList;
    }

    public final boolean isReadOnly() {
        return isReadOnly;
    }

    public final void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public final String getField() {
        return field;
    }

    public final void setField(String field) {
        this.field = field;
    }

    public final String getHeaderText() {
        return headerText;
    }

    public final String getInitialValue() {
        return value;
    }

    public final void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public final String getValue() {
        return value;
    }

    public final void setInitialValue(String value) {
        this.value = value;
    }

    public final boolean isChanged() {
        return changed;
    }

    public final void setValue(String value) {
        if ((this.value == null && value != null)
                || (!this.value.equals("value")) && (!this.isReadOnly())) {
            changed = true;
        }
        this.value = value;
    }

    public final List<SelectItem> getPickListItems() {
        List<SelectItem> ret = new ArrayList<SelectItem>();
        /*
         * If this is a read-only column than return a pick-list of one (1) item
         * otherwise, return the full picklist
         */
        
	        if (isReadOnly) {
	        	if(this.getValue() != null && this.getDisplayValue() != null){
	        		ret.add(new SelectItem(this.getValue(),this.getDisplayValue()));
	            }
	        } else {
	            try {
	                ret = this.getDataSet().retrievePickList(this.getField());
	            } catch (Exception e) {
	                logger.error(e.getMessage() + e);
	            }
	        }

        return ret;
    }

    public final DataSet getDataSet() {
        return dataset;
    }

    public final void setDataSet(DataSet dataset) {
        this.dataset = dataset;
    }

    public int getMaximumLength() {
        return maximumLength;
    }

    public void setMaximumLength(int maximumLength) {
        this.maximumLength = maximumLength;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
    
    public final String getDocURL() {
    	String wrongSep="";
    	if (headerText.equals("Template Path")) {
    		wrongSep =  DocumentUtil.getFileStoreBaseURL() + File.separator + getValue();
    	}
    		return wrongSep.replace('\\', '/');

    }
    
    public boolean isDocumentTemplateExists() {
    	if (DocumentUtil.isFileExists(File.separator + getValue())) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
}
