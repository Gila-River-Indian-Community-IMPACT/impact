package us.oh.state.epa.stars2.app.ceta;

@SuppressWarnings("serial")
public class SccElement  {
	private boolean included;
    protected String sccId;
	
    public SccElement() {
        super();
    }
    
    public SccElement(boolean included, String sccId) {
        super();
        this.included = included;
        this.sccId = sccId;
    }

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public String getSccId() {
        return sccId;
    }

    public void setSccId(String sccId) {
        this.sccId = sccId;
    }
}
