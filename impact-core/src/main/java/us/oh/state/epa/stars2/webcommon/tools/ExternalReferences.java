package us.oh.state.epa.stars2.webcommon.tools;

import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.def.ExternalLinksDef;
import us.oh.state.epa.stars2.def.ExternalLinksSectionTypDef;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.config.TreeNode;
import us.oh.state.epa.stars2.webcommon.AppBase;

public class ExternalReferences extends AppBase {
	
	private static final long serialVersionUID = 1L;

	private List<ReferenceSection> sections = new ArrayList<ReferenceSection>();

	private long sectionsDefLastRefreshTime = ExternalLinksSectionTypDef.getData().getLastRefreshTime();
	
	private long linksDefLastRefreshTime = ExternalLinksSectionTypDef.getData().getLastRefreshTime();
	
    public final ReferenceSection[] getSections() {
    
    	if (sections.size() == 0 
    			|| sectionsDefLastRefreshTime < ExternalLinksSectionTypDef.getData().getLastRefreshTime()
    			|| linksDefLastRefreshTime < ExternalLinksDef.getData().getLastRefreshTime()) {
    		
    		sections.clear();
    		
    		List<TreeNode> sectionNodes = getSectionNodes();
    		for (Node node : sectionNodes) {
    			ReferenceSection newSection = new ReferenceSection(node.getAsString("name"));
    			newSection.loadReferences((TreeNode) node);
    			sections.add(newSection);
    		}

    		sectionsDefLastRefreshTime = ExternalLinksSectionTypDef.getData().getLastRefreshTime();
    		linksDefLastRefreshTime = ExternalLinksDef.getData().getLastRefreshTime();
    	}
    	
    	return sections.toArray(new ReferenceSection[0]);
    }    
	
	public List<TreeNode> getSectionNodes() {

		// Get all Sections as List of TreeNode
		List<TreeNode> sectionNodes = new ArrayList<TreeNode>();
		List<SelectItem> allSections = ExternalLinksSectionTypDef.getData().getItems().getCurrentItems();

		// Get all External Links Data as list of SelectItem
		List<SelectItem> sectionLinkNodes = new ArrayList<SelectItem>();
		List<SelectItem> extlinkNodes = ExternalLinksDef.getData().getItems().getCurrentItems();
		for (SelectItem itm : extlinkNodes) {
			String sectionName = ExternalLinksDef.getExtLinkSectionName(itm.getValue().toString());
			if (sectionName != null) {
				sectionLinkNodes.add(new SelectItem(sectionName, (String) itm.getValue(), itm.getLabel()));
			}
		}

		// Populate each SectionNode with appropriate links.
		int totalSections = allSections.size();
		for (int i = 0; i < totalSections; i++) {
			TreeNode sectionNode = new TreeNode(allSections.get(i).getLabel(), null);
			String sectionName = allSections.get(i).getValue().toString();
			for (SelectItem itrSelectItm : sectionLinkNodes) {
				SelectItem si = itrSelectItm;
				if (si.getValue().toString().equalsIgnoreCase(sectionName)) {
					TreeNode sectionchildNode = new TreeNode("Link", null);
					sectionchildNode.setAttribute("title", si.getLabel());
					sectionchildNode.setAttribute("url", si.getDescription());
					sectionNode.addChild(sectionchildNode);
					sectionNode.setAttribute("name", sectionNode.getName());
				}

			}
			sectionNodes.add(sectionNode);
		}
		return sectionNodes;
	}

}
