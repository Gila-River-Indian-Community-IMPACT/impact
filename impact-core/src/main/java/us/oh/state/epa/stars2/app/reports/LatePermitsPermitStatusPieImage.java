package us.oh.state.epa.stars2.app.reports;

@SuppressWarnings("serial")
public class LatePermitsPermitStatusPieImage extends SOPPermitStatusPieImage {

    @Override
    protected String getMyManagedBean() {
        return "latePermits";
    }

}
