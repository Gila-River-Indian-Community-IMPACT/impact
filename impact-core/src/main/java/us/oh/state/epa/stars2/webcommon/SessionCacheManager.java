package us.oh.state.epa.stars2.webcommon;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class SessionCacheManager implements PhaseListener {
    public SessionCacheManager() {
    }
    
    public void afterPhase(PhaseEvent phaseEvent) {
    }

    @SuppressWarnings("unchecked")
    public void beforePhase(PhaseEvent phaseEvent) {
        String viewID = phaseEvent.getFacesContext().getViewRoot().getViewId();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap();
        for (Object obj : sessionMap.values()) {
            if ((obj instanceof SessionCacheObject)
                    && (((SessionCacheObject) obj).getCacheViewIds().length > 0)) {
                SessionCacheObject el = (SessionCacheObject) obj;
                boolean match = false;

                for (String viewIDPattern : el.getCacheViewIds()) {
                    if (match(viewIDPattern, viewID)) {
                        match = true;
                        break;
                    }
                }

                if (match) {
                    el.restoreCache();
                } else {
                    el.clearCache();
                }
            }
        }
    }

    private boolean match(String viewIDPattern, String viewID) {
        int pos;
        boolean ret = false;

        if ((pos = viewIDPattern.indexOf("*")) >= 0) {
            ret = viewID.length() >= pos
                    && viewID.startsWith(viewIDPattern.substring(0, pos));
        } else {
            ret = viewIDPattern.equals(viewID);
        }

        return ret;
    }

    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
