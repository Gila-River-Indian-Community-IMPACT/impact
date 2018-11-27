package us.oh.state.epa.stars2.webcommon.menu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.adf.view.faces.component.core.nav.CoreCommandMenuItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.userAuth.UserAttributes;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

import com.Ostermiller.util.StringTokenizer;

public class SimpleMenuItem implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -476963065894195220L;
	public static final String APP_RESERVED_BEAN_NAME = "menuItem_app_task";
	public static final String REPORT_RESERVED_BEAN_NAME = "menuItem_app_task";
	public static final String FACILITY_RESERVED_BEAN_NAME = "menuItem_facility_task";
	public static final String COMPLIANCE_RESERVED_BEAN_NAME = "menuItem_compliance_task";
	public static final String STACKTEST_RESERVED_BEAN_NAME = "menuItem_stacktest_task";
	public static final String MONITOR_REPORT_RESERVED_BEAN_NAME = "menuItem_monitor_report_task";
    private transient Logger logger;
    private String label;
    private String name;
    private String icon;
    private String type = CoreCommandMenuItem.TYPE_DEFAULT;
    private String outcome;
    private String urlLink;
    private boolean disabled;
    private boolean rendered = true;
    private List<SimpleMenuItem> children;
    private List<String> viewIDs;
    private String beanName;
    
    private String taskId;
    private boolean selected;

    public SimpleMenuItem() {
        logger = Logger.getLogger(SimpleMenuItem.class);
        
        //logger.debug(this.toString() + " S", new Throwable());
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        logger = null;

        if (children != null) {
            Iterator<SimpleMenuItem> it = children.iterator();
            while(it.hasNext()) {
                it.next();
                it.remove();
            }
//            for (SimpleMenuItem item : children) {
//                children.remove(item);
//            }
            children = null;
        }

        if (viewIDs != null) {
            Iterator<String> it = viewIDs.iterator();
            while(it.hasNext()) {
                it.next();
                it.remove();
            }
//            for (String tempStr : viewIDs) {
//                viewIDs.remove(tempStr);
//            }
            viewIDs = null;
        }
    }

    /**
     * @return Label for this menu item
     */
    public final String getLabel() {
        return label;
    }

    /**
     * @param label
     */
    public final void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return Name for this menu item
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * @return List of children for this menu item
     */
    public final List<SimpleMenuItem> getChildren() {
        return children;
    }

    /**
     * @param children
     */
    public final void setChildren(List<SimpleMenuItem> children) {
        if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
//        if (CommonConst.INTERNAL_APP.equals(CommonConst.INTERNAL_APP)) {
            // Get the user's attributes from session and manipulate the
            // menu structure to fit the user's roles.
            UserAttributes userAttrs = (UserAttributes) FacesUtil.getManagedBean("userAttrs");

            if (this.children == null) {
                this.children = new ArrayList<SimpleMenuItem>();
            }

            for (SimpleMenuItem tempItem : children) {
                // Every user gets all "global" menus and the "home" tab,
                // regardless of what their usecases say.
                if ((tempItem.getType().compareTo("global") == 0) || (userAttrs.isCurrentUseCaseValid(tempItem.getName())) || (tempItem.getName().compareTo("home") == 0) || (userAttrs.getUserName().compareTo("Admin") == 0)) {
                    this.children.add(tempItem);

                    if (tempItem.getViewIDs() != null) {
                        for (String viewId : tempItem.getViewIDs()) {
                            userAttrs.addViewIdToUseCase(viewId, tempItem.getName());
                        }
                    }
                    StringTokenizer st = new StringTokenizer(tempItem.getName(), ".");
                    String useCase = st.nextToken();

                    while (st.hasNext())
                        useCase = useCase + "_" + st.nextToken();

                    logger.debug(useCase);
                }
            }
        } else {
            this.children = children;

            UserAttributes userAttrs = (UserAttributes) FacesUtil.getManagedBean("userAttrs");

            for (SimpleMenuItem tempItem : children) {
                if (tempItem.getViewIDs() != null) {
                    for (String viewId : tempItem.getViewIDs()) {
                        userAttrs.addViewIdToUseCase(viewId, tempItem.getName());
                    }
                    StringTokenizer st = new StringTokenizer(tempItem.getName(), ".");
                    String useCase = st.nextToken();

                    while (st.hasNext())
                        useCase = useCase + "_" + st.nextToken();

                    logger.debug(useCase);
                }
            }
        }
    }

    /**
     * @return List of viewIds for this menu item.
     */
    public final List<String> getViewIDs() {
        return viewIDs;
    }

    /**
     * @param viewIDs
     */
    public final void setViewIDs(List<String> viewIDs) {
        this.viewIDs = viewIDs;

    }

    /**
     * @return The outcome for this menu item.
     */
    public final String getOutcome() {
        String ret = outcome;
        StringTokenizer st = new StringTokenizer(outcome, ".");
        
        if (st.nextToken().equalsIgnoreCase("method")) {
            if (st.countTokens() != 2) {
                logger.error("MenuItem function outcome is wrong : " + st);
            }
            String baseName = st.nextToken();
            Object base = FacesUtil.getManagedBean(baseName);
            if (base != null) {
                Object property = st.nextToken();
                Method m;
                String r = "";
                try {
                    m = base.getClass().getMethod(property.toString(),
                            new Class[0]);
                    r = (String) m.invoke(base, new Object[0]);
                } catch (Exception e) {
                    String error = base.toString() + " " + property.toString()
                        + " (not accessible!)" + e.getMessage();
                    logger.error(error, e);
                }
                ret = r;
            } else {
                logger.error("Object is not in session map! : " + baseName);
            }
        }

        return ret;
    }

    /**
     * @param outcome
     */
    public final void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    /**
     * @return The icon for this menu item.
     */
    public final String getIcon() {
        return icon;
    }

    /**
     * @param icon
     */
    public final void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return The type of this menu item.
     */
    public final String getType() {
        return type;
    }

    /**
     * @param type
     */
    public final void setType(String type) {
        this.type = type;
    }

    /**
     * @return URL link for this menu item.
     */
    public final String getUrlLink() {
        return urlLink;
    }

    /**
     * @param urlLink
     */
    public final void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }

    /**
     * @return the beanName
     */
    public final String getBeanName() {
        return beanName;
    }

    /**
     * @param beanName the beanName to set
     */
    public final void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
	 * @return
	 */
	public final boolean getDisabled() {
		if (beanName != null && !beanName.startsWith(APP_RESERVED_BEAN_NAME)
				&& !beanName.startsWith(REPORT_RESERVED_BEAN_NAME)
				&& !beanName.startsWith(FACILITY_RESERVED_BEAN_NAME)
				&& !beanName.startsWith(COMPLIANCE_RESERVED_BEAN_NAME)
				&& !beanName.startsWith(STACKTEST_RESERVED_BEAN_NAME)
				&& !beanName.startsWith(MONITOR_REPORT_RESERVED_BEAN_NAME)
				) {
			SimpleMenuItem map = (SimpleMenuItem) FacesUtil
					.getManagedBean(beanName);
			disabled = map.getDisabledWithName();
		}
		return disabled;
	}

    private boolean getDisabledWithName() {
        return disabled;
    }

    /**
     * @param disabled
     */
    public final void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    /**
     * @param menuItemName
     * @param disabled
     */
    public static final void setDisabled(String menuItemName, boolean disabled) {
        SimpleMenuItem map = (SimpleMenuItem) FacesUtil
                .getManagedBean(menuItemName);

        map.setDisabled(disabled);
    }
    
    /**
     * @return Is this menu item is rendered.
     */
	public final boolean getRendered() {
		if (beanName != null && !beanName.startsWith(APP_RESERVED_BEAN_NAME)
				&& !beanName.startsWith(REPORT_RESERVED_BEAN_NAME)
				&& !beanName.startsWith(FACILITY_RESERVED_BEAN_NAME)
				&& !beanName.startsWith(COMPLIANCE_RESERVED_BEAN_NAME)
				&& !beanName.startsWith(STACKTEST_RESERVED_BEAN_NAME)
				&& !beanName.startsWith(MONITOR_REPORT_RESERVED_BEAN_NAME)
				) {
			SimpleMenuItem map = (SimpleMenuItem) FacesUtil
					.getManagedBean(beanName);
			rendered = map.getRenderedWithName();
		}
		return rendered;
	}

    private boolean getRenderedWithName() {
        return rendered;
    }

    /**
     * @param rendered
     */
    public final void setRendered(boolean rendered) {
        this.rendered = rendered;
    }
    
    /**
     * @param menuItemName
     * @param rendered
     */
    public static final void setRendered(String menuItemName, boolean rendered) {
        SimpleMenuItem map = (SimpleMenuItem) FacesUtil
                .getManagedBean(menuItemName);

        map.setRendered(rendered);
    }
    
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
