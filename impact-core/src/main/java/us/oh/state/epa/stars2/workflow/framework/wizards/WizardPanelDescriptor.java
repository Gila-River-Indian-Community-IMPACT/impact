package us.oh.state.epa.stars2.workflow.framework.wizards;

/**
 * <p>
 * Title: WizardPanelDescriptor
 * </p>
 * 
 * <p>
 * Description: This is a utility class that allows an application to add a
 * <code>WizardPanel</code> to a <code>Wizard</code>. Allows to identify
 * the panel, whether or not this panel is the initial panel (every wizard must
 * have one and only one initial panel), and whether or not this panel is a
 * final panel (every wizard must have one or more final panels).
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 */

public class WizardPanelDescriptor {
    private boolean initPanel;
    private boolean finalPanel;
    private WizardPanel panel;

    /**
     * Constructor.
     * 
     * @param panel
     *            WizardPanel The visual wizard panel component.
     * @param initPanel
     *            boolean Set to "true" if this is the initial panel.
     * @param finalPanel
     *            boolean Set to "true " if this is a final panel.
     */
    public WizardPanelDescriptor(WizardPanel panel, boolean initPanel,
            boolean finalPanel) {
        this.panel = panel;
        this.initPanel = initPanel;
        this.finalPanel = finalPanel;
    }

    /**
     * Returns the wizard panel component.
     * 
     * @return WizardPanel the visual component.
     */
    public final WizardPanel getPanelComponent() {
        return panel;
    }

    /**
     * Returns "true" if this is the initial panel of the wizard.
     * 
     * @return boolean "true" if this is the initial panel.
     */
    public final boolean isInitPanel() {
        return initPanel;
    }

    /**
     * Returns "true" if this is a final panel of the wizard.
     * 
     * @return boolean "true" if this is the final panel.
     */
    public final boolean isFinalPanel() {
        return finalPanel;
    }
}
