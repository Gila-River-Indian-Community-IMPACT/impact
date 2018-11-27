package us.oh.state.epa.stars2.database.dbObjects.report;


public class PermitTime implements java.io.Serializable {

    private String _permitNbr;
    private String _permitType;
    private int _agencyTime;
    private int _applicantTime;
    private int _otherTime;
    private int _elapsedTime;

    public String getPermitNbr() {
        return _permitNbr;
    }

    public void setPermitNbr(String permitNbr) {
        _permitNbr = permitNbr;
    }

    public String getPermitType() {
        return _permitType;
    }

    public void setPermitType(String permitType) {
        _permitType = permitType;
    }

    public int getAgencyTime() {
        return _agencyTime;
    }

    public void setAgencyTime(int agencyTime) {
        _agencyTime = agencyTime;
    }

    public int getApplicantTime() {
        return _applicantTime;
    }

    public void setApplicantTime(int applicantTime) {
        _applicantTime = applicantTime;
    }

    public int getOtherTime() {
        return _otherTime;
    }

    public void setOtherTime(int otherTime) {
        _otherTime = otherTime;
    }

    public int getElapsedTime() {
        return _elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        _elapsedTime = elapsedTime;
    }

}
