package us.oh.state.epa.stars2.app.reports;

@SuppressWarnings("serial")
public class LatePermitsPermitTypePieImage extends SOPPermitTypePieImage {

    @Override
    protected String getMyManagedBean() {
        return "latePermits";
    }

}
