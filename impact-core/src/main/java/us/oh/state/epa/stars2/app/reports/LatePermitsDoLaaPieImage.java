package us.oh.state.epa.stars2.app.reports;

@SuppressWarnings("serial")
public class LatePermitsDoLaaPieImage extends SOPDoLaaPieImage {

    @Override
    protected String getMyManagedBean() {
        return "latePermits";
    }

}
