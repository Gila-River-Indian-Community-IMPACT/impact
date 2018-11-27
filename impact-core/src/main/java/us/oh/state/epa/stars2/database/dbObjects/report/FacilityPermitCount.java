package us.oh.state.epa.stars2.database.dbObjects.report;


public class FacilityPermitCount implements java.io.Serializable {

    private String _permitTypeDsc;
    private int _facilitiesWithIssued;
    private int _facilitiesWithActive;
    private int _facilitiesWithExtended;
    private int _facilitiesWithExpired;
    private int _facilitiesWithSuperseded;

    public FacilityPermitCount(String permitTypeDsc) {
        _permitTypeDsc = permitTypeDsc;
    }

    public String getPermitType() {
        return _permitTypeDsc;
    }

    public void getPermitType(String permitTypeDsc) {
        _permitTypeDsc = permitTypeDsc;
    }

    public int getIssued() {
        return _facilitiesWithIssued;
    }

    public void setIssued(int issued) {
        _facilitiesWithIssued = issued;
    }

    public int getActive() {
        return _facilitiesWithActive;
    }

    public void setActive(int active) {
        _facilitiesWithActive = active;
    }

    public String getExtended() {
        if (_permitTypeDsc.equals("PTI")) {
            return "-";
        }
        return Integer.toString(_facilitiesWithExtended);
    }

    public void setExtended(int extended) {
        _facilitiesWithExtended = extended;
    }

    public int getExpired() {
        return _facilitiesWithExpired;
    }

    public void setExpired(int expired) {
        _facilitiesWithExpired = expired;
    }

    public int getSuperseded() {
        return _facilitiesWithSuperseded;
    }

    public void setSuperseded(int superseded) {
        _facilitiesWithSuperseded = superseded;
    }

}
