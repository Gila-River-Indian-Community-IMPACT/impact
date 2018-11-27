package us.oh.state.epa.stars2.webcommon;

import javax.faces.FactoryFinder;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

/**
 * @author Pyeh
 *
 */
public class DisplayUtilPhaseListener implements PhaseListener {

    /**
     * 
     */
    private static final long serialVersionUID = -1985573842518377521L;

    public void beforePhase(PhaseEvent event) {

        DisplayUtil util 
            = DisplayUtil.getSessionInstance(event.getFacesContext(), false);
                                                          
        if (util != null) {
            util.renderQueuedMessages(event.getFacesContext());
            
            LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
                    .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            Lifecycle lifecycle = lifecycleFactory
                    .getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
            lifecycle.removePhaseListener(this);
        }
    }

    public void afterPhase(PhaseEvent event) {
    }

    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
