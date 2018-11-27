package us.oh.state.epa.stars2.webcommon;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.oh.state.epa.stars2.framework.userAuth.UserAttributes;

public class UseCasePhaseListener implements PhaseListener {
	
	private static final long serialVersionUID = 9002749815607506964L;

	public void afterPhase(PhaseEvent arg0) {
    }

    public void beforePhase(PhaseEvent arg0) {
        if (arg0.getPhaseId() == PhaseId.RENDER_RESPONSE) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) facesContext
                    .getExternalContext().getRequest();
            HttpServletResponse response = (HttpServletResponse) facesContext
                    .getExternalContext().getResponse();
            UserAttributes userAttrs = (UserAttributes) facesContext
                    .getExternalContext().getSessionMap().get("userAttrs");
            String viewId = facesContext.getViewRoot().getViewId();

            if ((userAttrs != null) 
                    && (!userAttrs.isCurrentViewIdValid(viewId))) {
                DisplayUtil
                        .displayError("Not allowed to navigate to that location.");
                String referrer = request.getHeader("referer");

                try {
                    response.sendRedirect(referrer);
                } catch (IOException ioe) {
                    //logger.error(ioe.getMessage(), ioe);
                }

                facesContext.responseComplete();
            }
            return;
        }
    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
}
