package us.oh.state.epa.stars2.webcommon.tools;

import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.config.TreeNode;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.bean.NameValue;

public class ReferenceSection extends AppBase {
	
	private static final long serialVersionUID = 1L;

	private String name;
    private List<NameValue> references = new ArrayList<NameValue>();

    public ReferenceSection(String sectionName) {
        this.name = sectionName;
    }

    public final List<NameValue> getReferences() {
        return references;
    }

    public final void setReferences(List<NameValue> references) {
        this.references = references;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    protected final void loadReferences(TreeNode sectionRoot){
    	if ((name != null) && (sectionRoot != null)) {
            references = new ArrayList<NameValue>();
            	   for (Node link : sectionRoot.getAllChildren()) {
                		references.add(new NameValue(link.getAsString("title"), link
                        .getAsString("url")));
            }
        }
    }

}
