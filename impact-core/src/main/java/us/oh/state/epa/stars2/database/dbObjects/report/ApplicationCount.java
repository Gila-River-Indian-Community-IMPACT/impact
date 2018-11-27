package us.oh.state.epa.stars2.database.dbObjects.report;


@SuppressWarnings("serial")
public class ApplicationCount implements java.io.Serializable {

    private String _doLAAName;
    private int _pbr;
    private int _ptio;
    private int _tvpto;
    private int _rpc;
    private int _rpe;
    private int _rpr;

    public ApplicationCount(String doLAAName) {

        _doLAAName = doLAAName;

    } // END: public ApplicationCount(String doLAAName)

    public final String getDoLaaName() {
        return _doLAAName;
    }

    public final int getPbr() {
        return _pbr;
    }

    public final void setPbr(int pbr) {
        _pbr = pbr;
    }

    public final int getPtio() {
        return _ptio;
    }

    public final void setPtio(int ptio) {
        _ptio = ptio;
    }

    public final int getTvPto() {
        return _tvpto;
    }

    public final void setTvPto(int tvpto) {
        _tvpto = tvpto;
    }

    public final int getRpc() {
        return _rpc;
    }

    public final void setRpc(int rpc) {
        _rpc = rpc;
    }

    public final int getRpe() {
        return _rpe;
    }

    public final void setRpe(int rpe) {
        _rpe = rpe;
    }

    public final int getRpr() {
        return _rpr;
    }

    public final void setRpr(int rpr) {
        _rpr = rpr;
    }

    public final int getTotalCount() {

        return _pbr + _ptio + _tvpto + _rpc + _rpe + _rpr;

    } // END: public int getTotalCount()

} // END: public class ApplicationCount implements java.io.Serializable
